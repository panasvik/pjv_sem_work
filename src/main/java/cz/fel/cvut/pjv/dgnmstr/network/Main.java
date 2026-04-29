package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.global.Inputs;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int port = 8888;

        System.out.println("=== 1. ЗАПУСК СЕРВЕРА ===");
        ServerNetworkManager server = new ServerNetworkManager(port);
        server.start();

        // Ждем немного, чтобы сервер успел поднять сокет
        Thread.sleep(500);

        System.out.println("\n=== 2. СОЗДАНИЕ КЛИЕНТА И ПРОФИЛЯ ===");
        // Создаем профиль для клиента
        PlayerProfile myProfile = null;
        try {
            myProfile = PlayerProfile.loadFromJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        myProfile.setGold(150); // Добавим немного золота для теста

        ClientNetworkManager clientTmp;
        try {
            clientTmp = new ClientNetworkManager("localhost", port, myProfile);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        final ClientNetworkManager client = clientTmp;

        System.out.println("\n=== 3. ПОДКЛЮЧЕНИЕ КЛИЕНТА (HANDSHAKE) ===");
        // После handshake сервер запускает level-loop и начинает слать world updates клиентам.
        client.connect();

        // Даем серверу время зарегистрировать клиента и запустить level.
        Thread.sleep(1000);

        System.out.println("\n=== 4. LOOP: КЛИЕНТ ШЛЕТ INPUTS, СЕРВЕР ШЛЕТ UPDATES ===");
        System.out.println("Press Ctrl+C to stop.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n=== SHUTDOWN ===");
            client.disconnect();
            server.stop();
        }));

        int tick = 0;
        while (true) {
            boolean right = (tick / 30) % 2 == 0;
            boolean left = !right;
            boolean attack = tick % 20 == 0;
            boolean interact = tick % 180 == 0;

            Inputs inputs = new Inputs(
                    false,            // ult
                    interact,         // interact
                    attack,           // attack
                    false,            // up
                    false,            // down
                    left,             // left
                    right,            // right
                    right ? 30 : -30, // mouseX
                    0                 // mouseY
            );

            client.sendInputs(inputs);
            Thread.sleep(33); // ~30 updates/sec
            tick++;
        }
    }
}
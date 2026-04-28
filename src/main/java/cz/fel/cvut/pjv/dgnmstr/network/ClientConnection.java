package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientConnection {
    private static int idCounter = 1;
    private final int id;
    private final Socket tcpSocket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final InetAddress address;
    private int udpPort = -1;
    private static final Logger log = Logger.getLogger(ServerNetworkManager.class.getName());


    public ClientConnection(Socket tcpSocket) throws IOException {
        this.id = idCounter++;
        this.tcpSocket = tcpSocket;
        this.address = tcpSocket.getInetAddress();

        this.oos = new ObjectOutputStream(tcpSocket.getOutputStream());
        this.ois = new ObjectInputStream(tcpSocket.getInputStream());
    }

    public void sendTCP(DGNPacket packet) {
        try {
            synchronized (oos) {
                oos.writeObject(packet);
                oos.flush();
                oos.reset(); // Сбрасываем кэш сериализации
            }
        } catch (IOException e) {
            log.warning("Failed to send TCP to client " + id + ": " + e.getMessage());
        }
    }

    public int getId() { return id; }
    public ObjectInputStream getOis() { return ois; }
    public InetAddress getAddress() { return address; }
    public int getUdpPort() { return udpPort; }
    public void setUdpPort(int udpPort) { this.udpPort = udpPort; }
    public boolean isConnected() { return !tcpSocket.isClosed(); }

    public void close() {
        try {
            oos.close();
            ois.close();
            tcpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

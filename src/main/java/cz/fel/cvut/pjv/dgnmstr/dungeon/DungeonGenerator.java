package cz.fel.cvut.pjv.dgnmstr.dungeon;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.io.IOException;


public class DungeonGenerator {

    private static final int ARENA_SIZE = 64;
    private static final int CORRIDOR_LENGTH = 20;

    private final Random random = new Random();

    public Level generate(DifficultyStats difficulty) {
        Level level = new Level(difficulty);

        int totalWidth = (ARENA_SIZE * 3) + (CORRIDOR_LENGTH * 2);
        int totalHeight = ARENA_SIZE;

        TileType[][] map = new TileType[totalWidth][totalHeight];
        level.setMap(map, totalWidth, totalHeight);

        for (int x = 0; x < totalWidth; x++) {
            for (int y = 0; y < totalHeight; y++) {
                map[x][y] = TileType.WALL;
            }
        }

        int currentX = 0;

        for (int i = 0; i < 3; i++) {

            ArenaType type = getRandomArenaType();

            pasteArena(map, currentX, 0, type);

            Arena arena = new Arena(currentX, 0, level, type);
            level.addArena(arena);

            currentX += ARENA_SIZE;

            if (i < 2) {
                pasteCorridor(map, currentX, 0);
                currentX += CORRIDOR_LENGTH;
            }
        }

        int exitY = ARENA_SIZE / 2;
        map[totalWidth - 1][exitY] = TileType.EXIT;

        return level;
    }

    private void pasteArena(TileType[][] map, int startX, int startY, ArenaType type) {

        try {
            loadArenaFromFile(map, startX, startY, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int doorStartY = startY + (ARENA_SIZE / 2) - 3;
        int doorEndY = startY + (ARENA_SIZE / 2) + 3;

        if (startX > 0) {
            for (int y = doorStartY; y <= doorEndY; y++) {
                map[startX][y] = TileType.FLOOR;
            }
        }

        if (startX + ARENA_SIZE < map.length) {
            for (int y = doorStartY; y <= doorEndY; y++) {
                map[startX + ARENA_SIZE - 1][y] = TileType.FLOOR;
            }
        }
    }

    private void pasteCorridor(TileType[][] map, int startX, int startY) {
        int corridorStartY = startY + (ARENA_SIZE / 2) - 3;
        int corridorEndY = startY + (ARENA_SIZE / 2) + 3;

        for (int x = 0; x < CORRIDOR_LENGTH; x++) {
            for (int y = corridorStartY; y <= corridorEndY; y++) {
                map[startX + x][y] = TileType.FLOOR;
            }
        }
    }

    private void loadArenaFromFile(TileType[][] map, int startX, int startY, ArenaType type) throws IOException {
        String fileName = "/data/arenas/" + type.name() + ".txt";

        InputStream is = getClass().getResourceAsStream(fileName);

        if (is == null) {
            System.err.println("File not found: " + fileName);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int y = 0;

            while ((line = reader.readLine()) != null && y < ARENA_SIZE) {
                for (int x = 0; x < line.length() && x < ARENA_SIZE; x++) {
                    char c = line.charAt(x);
                    int globalX = startX + x;
                    int globalY = startY + y;

                    switch (c) {
                        case 'X': // Wall
                            map[globalX][globalY] = TileType.WALL;
                            break;
                        case '.': // floor
                            map[globalX][globalY] = TileType.FLOOR;
                            break;
                        case '*': // spawn zone
                            map[globalX][globalY] = TileType.SPAWN_FLOOR;
                            break;
                        case 'C': // chest
                            map[globalX][globalY] = TileType.CHEST_FLOOR;
                            break;
                        default: // unknown symbol
                            map[globalX][globalY] = TileType.FLOOR;
                            break;
                    }
                }
                y++;
            }
        } catch (IOException e) {
            System.err.println("Error while reading file " + fileName);
            throw e;
        }
    }

    private ArenaType getRandomArenaType() {
        ArenaType[] types = ArenaType.values();
        return types[random.nextInt(types.length)];
    }
}
# Dungeon Master Architecture

Dungeon Master is a JavaFX dungeon crawler with a client/server-style runtime. Even when the player hosts locally, the game is split into two main parts:

- **Server side**: owns the real game state, level simulation, spawning, collisions, arena progression, and victory/game-over decisions.
- **Client side**: owns JavaFX screens, input capture, rendering, inventory UI, and a local synchronized copy of the level.
- **Network layer**: sends player input to the server and sends authoritative updates back to clients.

```text
Start Menu
   |
   +--> ClientManager --> ClientNetworkManager
   |
   +--> ServerManager --> ServerNetworkManager

Client input -> server simulation -> network updates -> client rendering
```

## Main Packages

### `cz.fel.cvut.pjv.dgnmstr`

Application startup.

- `DungeonMasterLauncher` starts the JavaFX application.
- `GameApp` loads fonts, configures logging, initializes scenes, and cleans up server/client threads on shutdown.

### `global`

Runtime coordination.

- `GameManager` stores references to active managers, controllers, renderer, and input handler.
- `ServerManager` creates the authoritative level, starts the server update loop, applies inputs, and triggers network updates.
- `ClientManager` maintains the client-side level copy, handles incoming packets, sends inputs, and sends profile changes.
- `InputHandler` converts JavaFX keyboard/mouse events into `Inputs`.

### `view`

JavaFX UI and rendering.

- `SceneManager` loads and switches FXML views.
- `StartMenuController` handles loading profiles, hosting, connecting, saving, and starting the game.
- `GameController` starts rendering/input once the client has a level and local hero.
- `GameRenderer` draws the client-side `Level` on a JavaFX `Canvas`.

### `dungeon`

World structure.

- `Level` stores the tile map, arenas, entities, players, projectiles, chests, current state, and lookup maps.
- `Arena` represents one room with bounds, type, grates, active mobs, and arena state.
- `DungeonGenerator` builds a deterministic level from a seed and text arena templates.
- `EntitySpawner` abstracts entity creation.

### `dungeon.controllers`

Game state machines.

- `LevelController` updates entities/projectiles, checks player death, activates arenas, spawns the final boss, and detects victory.
- `ArenaController` starts arena fights, closes/open grates, spawns waves, spawns miniboss enemies, and marks arenas cleared.

### `entity`

Runtime game objects.

- `Entity` is the base class for HP, position, faction, collision-aware movement, damage handling, and rendering ID.
- Subpackages contain heroes, mobs, bosses, projectiles, map objects, and behavior interfaces.

### `item` and `player`

Player state and equipment.

- `PlayerProfile` stores role, armor, weapon, inventory, and gold.
- `PlayerStats` is derived from the profile and synchronized into the hero.
- `Inventory`, weapons, armor, and item classes define loot and equipment behavior.

### `network`

UDP packet synchronization.

- `DGNPacket` wraps a `PacketType` and payload.
- `ServerNetworkManager` receives client inputs/profile updates and broadcasts authoritative state.
- `ClientNetworkManager` sends handshake/input/profile packets and dispatches incoming packets to `ClientManager`.
- `ServerSpawner` creates server-side entities and broadcasts matching spawn packets.
- `payloads` contains packet records for seeds, IDs, spawns, updates, removals, states, and profile/stat sync.

## Startup Flow

1. `DungeonMasterLauncher` launches `GameApp`.
2. `GameApp` initializes JavaFX and opens the start menu.
3. The player loads a `PlayerProfile`.
4. Hosting creates a `ServerManager` and a connected local `ClientManager`.
5. Connecting creates only a client and connects it to an existing server.
6. The client sends a `PLAYER_PROFILE` handshake.
7. The host starts the game, generates a level, sends the seed, assigns hero IDs, and starts the server loop.
8. `GameController` waits for the client level and local hero, then starts input polling and rendering.

## Level Generation

`DungeonGenerator` creates the same base map on the server and every client by using a shared seed.

The generated level contains:

- Three `64 x 64` arenas.
- Two corridors between arenas.
- Arena templates loaded from `src/main/resources/cz/fel/cvut/pjv/dgnmstr/dungeon`.
- Tile markers for walls, floors, spawn points, chests, fences, and exit tiles.

Only the base map is reproduced from the seed. Entities are synchronized separately through network spawn packets.

## Server Simulation Loop

`ServerManager` runs the authoritative update loop every 16 ms.

Each tick:

1. Updates the `LevelController`.
2. Updates entities and projectiles.
3. Advances arena/level state machines.
4. Broadcasts changed arena and level states.
5. Broadcasts hero, mob, and projectile updates.
6. Broadcasts removals for entities/projectiles that disappeared.

Client movement is input-driven:

```text
InputHandler -> ClientManager -> ClientNetworkManager
    -> ServerNetworkManager -> ServerManager.applyInputs
    -> server-side Hero update -> broadcast updated Hero state
```

## State Machines

### Level

```text
NOT_STARTED -> PLAYING -> VICTORY -> GAME_OVER
```

- `PLAYING` begins when the level starts.
- `GAME_OVER` happens when all players are dead.
- `VICTORY` happens after the final boss is dead and a living player reaches the exit.

### Arena

```text
NOT_STARTED -> WAVE_FIGHT -> MINI_BOSS_FIGHT -> CLEARED
```

- A player entering the arena starts the fight.
- Grates close the arena entrances.
- Regular mobs spawn first.
- A stronger miniboss enemy spawns after the wave is cleared.
- Grates open when the arena is cleared.

## Entity and Spawning Model

`Level` stores entities and projectiles in both lists and ID maps:

- Lists are used for updating and rendering.
- ID maps are used for network packet lookup.

The server owns all spawning. `Level` requests spawns through `EntitySpawner`.

For each spawn, `ServerSpawner`:

1. Allocates an ID.
2. Creates the object in the server level.
3. Broadcasts a `SPAWN_*` packet.
4. Clients recreate the object with the same ID.

This keeps server and client level copies aligned.

## Networking

Networking uses UDP and serialized `DGNPacket` objects.

Client to server:

- `PLAYER_PROFILE` registers a client before the game starts.
- `UPDATE_HERO` sends the latest input state.
- `PROFILE_UPDATE` sends inventory/equipment/gold changes.

Server to client:

- `SEED` sends the level seed.
- `CLIENT_ID` tells each client which hero is local.
- `SPAWN_*` creates synchronized objects.
- `UPDATE_*` synchronizes hero, mob, and projectile state.
- `ENTITY_REMOVE` and `REMOVE_PROJECTILE` remove deleted objects.
- `LEVEL_STATE`, `ARENA_STATE`, and `SYNC_STATS` synchronize game state and derived stats.

## Rendering and UI

The client renders only its synchronized copy of the level.

`GameRenderer`:

- runs as a JavaFX `AnimationTimer`;
- centers the camera on the local hero;
- draws visible tiles, entities, and projectiles;
- updates health and gold UI through `GameController`.

`GameController` handles inventory/chest UI and forwards item/profile changes through `ClientManager`.

## Loading and Saving data

`PlayerProfile` is saved and loaded with Jackson JSON.

It stores:

- role;
- equipped armor;
- equipped weapon;
- inventory;
- gold.

The default profile is loaded from:

```text
src/main/resources/cz/fel/cvut/pjv/dgnmstr/player/StartPlayer.json
```

## Threads

The application uses several execution threads:

- JavaFX application thread for UI and rendering.
- Server UDP listener thread.
- Client UDP listener thread.
- Server scheduled update loop.
- Client scheduled input polling loop.

Some maps use `ConcurrentHashMap`, but entity/projectile lists are normal lists. Rendering therefore takes snapshots before drawing.

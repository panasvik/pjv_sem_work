#Dungeon Master (PJV Semestral Project)

    A 2D Dungeon Crawler game developed in Java using JavaFX. In this game, players navigate through procedurally generated arenas, fight various monsters, collect loot, and face challenging bosses.

#Work division

Viktor Panasiuk:

    Core Game Loop: Developed the Level and Arena state machines, including wave progression, room locking, and entity updates.

    Graphical Interface: Set up the JavaFX environment, FXML layouts, and the main GameApp application structure.

    Player Mechanics: Implemented the Hero and Warrior classes, including the dexterity/parry mathematics and abilities.

    Inventory Logic: Created the PlayerProfile and the Double-Dispatch item management system for weapons and armor.

    World Generation & Data: Programmed the procedural DungeonGenerator and handled saving/loading the player state via JSON (Jackson).

Filip Mařík:
 
    Enemy AI & Boss Encounters: Designed the entity hierarchy (Mob, Zombie, Skeleton, etc.) and implemented multi-phase boss fights (DwightTheLeviathan).

    Network Architecture: Created the foundational network packages and records (Spawn, Sync, State packets) for future multiplayer support.

    System Infrastructure: Implemented the centralized java.util.logging system,  and loot drop mechanics (DroppedGold).

    Level Design & Documentation: Designed the text-based arena layouts, authored the project README, and generated the core Javadoc technical documentation.

#Technologies Used

    Programming Language: Java 24

    GUI Framework: JavaFX

    Data Serialization: Jackson (JSON processing)

    Build Tool: Maven

    AI: Gemini to consult with and help with troubleful code
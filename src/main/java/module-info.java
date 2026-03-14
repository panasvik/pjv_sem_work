module cz.fel.cvut.pjv.dgnmstr.dungeonmaster {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens cz.fel.cvut.pjv.dgnmstr.dungeonmaster to javafx.fxml;
    exports cz.fel.cvut.pjv.dgnmstr.dungeonmaster;
}
module cz.fel.cvut.pjv.dgnmstr {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens cz.fel.cvut.pjv.dgnmstr to javafx.fxml;
    exports cz.fel.cvut.pjv.dgnmstr;
}
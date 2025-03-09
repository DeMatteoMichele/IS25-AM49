module it.polimi.ingsw.is25am49 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens it.polimi.ingsw.is25am49 to javafx.fxml;
    exports it.polimi.ingsw.is25am49;
}
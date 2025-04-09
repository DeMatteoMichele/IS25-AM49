module it.polimi.ingsw.is25am49 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;

    opens it.polimi.ingsw.is25am49 to javafx.fxml;
    exports it.polimi.ingsw.is25am49.client;
}
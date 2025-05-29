module it.polimi.ingsw.is25am49 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires org.jline;

    opens it.polimi.ingsw.is25am49.client to javafx.fxml;
    opens it.polimi.ingsw.is25am49.server to javafx.fxml;
    exports it.polimi.ingsw.is25am49.client;
    exports it.polimi.ingsw.is25am49.server;
    exports it.polimi.ingsw.is25am49.shared.enums to com.fasterxml.jackson.databind;

    exports it.polimi.ingsw.is25am49.shared;
    opens it.polimi.ingsw.is25am49.shared to javafx.fxml;
    exports it.polimi.ingsw.is25am49.client.GUI to javafx.graphics;
    opens it.polimi.ingsw.is25am49.client.GUI to javafx.fxml;

}
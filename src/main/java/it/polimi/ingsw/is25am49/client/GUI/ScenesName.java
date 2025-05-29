package it.polimi.ingsw.is25am49.client.GUI;



public enum ScenesName {
    HOME("/it/polimi/ingsw/is25am49/FXML_files/home.fxml"),
    SETTINGS("/it/polimi/ingsw/is25am49/FXML_files/settings.fxml"),
    INIT("/it/polimi/ingsw/is25am49/FXML_files/init.fxml"),
    BUILD("/it/polimi/ingsw/is25am49/FXML_files/build.fxml");

    private final String path;

    /**
     * Constructor for ScenesName enum.
     *
     * @param path The file path of the corresponding FXML file.
     */
    ScenesName(String path) {
        this.path = path;
    }

    /**
     * Getter for the file path of the FXML file associated with the scene.
     *
     * @return The file path of the FXML file.
     */
    public String getPath() {
        return path;
    }
    public String getName() {
        return this.name();
}}


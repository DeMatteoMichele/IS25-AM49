package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.TileCardDeck;
import it.polimi.ingsw.is25am49.client.model.shipboard.Tile;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class buildController extends GenericSceneController {
    @FXML
    private GridPane cardContainer;
    @FXML
    private FlowPane buildContainer;
    //associo a ogni id della tile l'immagine scoperta
    private final Map<Integer, ImageView> tilesMap = new HashMap<>();
    //salvo la posizione in cui salvo la tessera e il nodo corrispondente
    private final Map<Integer, Node> container = new HashMap<>();
    private final Map<Integer,Integer> tileMap = new HashMap<>();

    public void loadTilesMap() {
        // Carica le immagini delle tile e le associa agli ID
        for (int j = 1; j < 157; j++) {

            String path = "/it/polimi/ingsw/is25am49/tiles/GT-new_tiles_16_for web" + j + ".jpg";
            ImageView imageView = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
            imageView.setFitWidth(40);
            imageView.setFitHeight(60);
            tilesMap.put(j, imageView);
        }
    }


    int columns =13;

   public void loadCards() {
       Image image = new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tiles/GT-new_tiles_16_for web157.jpg").toExternalForm());
       TileCardDeck deck = getGameSession().getGame().get().getGameboard().getTilesDeck();
       System.out.println("Numero totale di tile nel deck: " + deck.stream().toList().size());

       int columns = 13;
       int rows = 12;
       int maxTiles = columns * rows;
// Pulisci eventuali elementi esistenti nella griglia
        cardContainer.getChildren().clear();

        // Aggiungi direttamente le tile nella griglia
        int count = 0;
        for (Map.Entry<Integer, Tile> entry : deck.stream().toList()) {
            if (count >= maxTiles) break;  // Limita al massimo di celle disponibili

            int index = entry.getKey();
            Tile tile = entry.getValue();
            int tileId = tile.getIdTile();

            // Calcola posizione nella griglia 12x13
            int col = count % columns;
            int row = count / columns;

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            imageView.setUserData(tileId);
            imageView.setOnMouseClicked(event -> {
                int clickedTileId = (int) imageView.getUserData();
                try {
                    getGui().getController().userDrawTile(getGui().getMyId(), clickedTileId);
                } catch (Exception e) {
                    getGui().printUpdateMessage(e.getMessage());
                }
            });

            // Aggiungi direttamente l'immagine nella posizione corretta
            cardContainer.add(imageView, col, row);

            container.put(index, imageView);
            tileMap.put(tileId, index);
            count++;
        }
    }


    public void clearHandTiles() {
        buildContainer.getChildren().clear();
    }

    public void disableTile(int idTile) {
        int index = tileMap.get(idTile);
        Node node = container.get(index);
        if(node instanceof ImageView){
            ImageView imageView = (ImageView) node;
            imageView.setOpacity(0.5);
            imageView.setDisable(true);

//            System.out.println("disable tile with ID: " + idTile);
        }
    }
    public void enableTile(int idTile){

        // Per accedere al nodo

        int index = tileMap.get(idTile);
        Node node = container.get(index);
        if(node instanceof ImageView){
            ImageView imageView = (ImageView) node;
            imageView.setOpacity(1.0);
            imageView.setDisable(false);
            ImageView tileView = tilesMap.get(idTile);

            imageView.setImage(tileView.getImage());

//            System.out.println("Enabling tile with ID: " + idTile);
        }

    }
    public void setHandTile(int tileId){
        ImageView imageView = tilesMap.get(tileId);
        imageView.setFitHeight(181);
        imageView.setFitWidth(181);
        buildContainer.getChildren().add(imageView);
    }
}

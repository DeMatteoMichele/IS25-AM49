package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.TileCardDeck;
import it.polimi.ingsw.is25am49.client.model.shipboard.Tile;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class buildController extends GenericSceneController {
    @FXML
    private FlowPane cardContainer;
    @FXML
    private FlowPane buildContainer;
    //associo a ogni id della tile il percorso per la sua immagine scoperta
    private final Map<Integer, ImageView> tilesMap = new HashMap<>();

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
    public FlowPane getCardContainer() {
        return cardContainer;
    }

    public void loadCards() {
        Image image = new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tiles/GT-new_tiles_16_for web157.jpg").toExternalForm());
        TileCardDeck deck = getGameSession().getGame().get().getGameboard().getTilesDeck();
        deck.stream().forEach(entry -> {
            int index = entry.getKey();
            Tile tile = entry.getValue();
            int tileId = tile.getIdTile();

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(40);
            imageView.setFitHeight(60);
            imageView.setUserData(tileId); // Memorizza l'ID della tessera

            //Aggiungi eventi e informazioni per debug
//            System.out.println("Aggiungendo la tessera con id: " + tileId + " all'indice: " + index);
            imageView.setOnMouseClicked(event -> {
                int clickedTileId = (int) imageView.getUserData();
                System.out.println("Tile cliccata con ID: " + clickedTileId);
                try {
                    getGui().getController().userDrawTile(getGui().getMyId(), clickedTileId);
                    System.out.println("Richiesta di pesca inviata per la tile con ID: " + clickedTileId);

                } catch (Exception e) {
                    getGui().showError(getStage(), e.getMessage());
                }
//                imageView.setDisable(true);
//                imageView.setOpacity(0.5);

            });
            cardContainer.getChildren().add(imageView);
        });
//        Image backImage = new Image(getClass().getResource("/sfondo3.jpg").toExternalForm());
//
//        // Ottieni il riferimento al mazzo di tile
//        var tilesDeck = getGameSession().getGame().get().getGameboard().getTilesDeck();
//
//        // Inizializza un array con gli ID delle tile nel mazzo (nell'ordine attuale)
//        int[] tileIds = new int[tilesDeck.getNumberOfTile()];
//        // Qui dovresti popolare l'array con gli ID effettivi delle tile
//        // Per esempio: tileIds = tilesDeck.getTileIds(); oppure iterando sul mazzo
//
//        for (int i = 0; i < tilesDeck.getNumberOfTile(); i++) {
//            ImageView imageView = new ImageView(backImage);
//            imageView.setFitWidth(40);
//            imageView.setFitHeight(60);
//
//            // Salva l'ID della tile associandolo all'ImageView
//            int tileId = tileIds[i];
//            imageView.setUserData(tileId);
//
//            imageView.setOnMouseClicked(event -> {
//                // Recupera l'ID della tile cliccata
//                int clickedTileId = (int) imageView.getUserData();
//                System.out.println("Tile cliccata con ID: " + clickedTileId);
//
//                // Invia la richiesta di pesca al server
//                try{
//                    getGui().getController().userDrawTile(getGui().getMyId(),clickedTileId);
//                    System.out.println("Richiesta di pesca inviata per la tile con ID: " + clickedTileId);
////                    buildContainer.getChildren().add(tilesMap.get(clickedTileId));
////                    ImageView tile = new ImageView(getClass().getResource("/it/polimi/ingsw/is25am49/GT-new_tiles_16_for web1.jpg").toExternalForm());
////                    buildContainer.getChildren().add(tile);
//                }catch (Exception e){
//                    getGui().showError(getStage(),e.getMessage());
//                }
//                // Disabilita l'imageView dopo il click per evitare doppie pescate
//                imageView.setDisable(true);
//                imageView.setOpacity(0.5);
//            });
//
//            cardContainer.getChildren().add(imageView);
//        }
//    }
    }
}

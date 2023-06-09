package com.automataproj.automataproject;

import com.automataproj.automataproject.Metier.AFD;
import com.automataproj.automataproject.Metier.AFND;
import com.automataproj.automataproject.Metier.AutomateFini;
import com.automataproj.automataproject.Popups.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import presentation.ShowResultAutomaton;
import presentation.showAutomataProduct;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AutomataCreation implements Initializable {
    @FXML
    private Pane mainPane;

    public AutomateFini af;

    private ImageView imgview;
    @FXML
    public Button minimiseId;
    @FXML
    public Button determinerId;
    @FXML
    public Button wordsId;
    @FXML
    public TextField readField;
    @FXML
    public Label labelRead;
    @FXML
    public Button btnRead;
    private List<AutomateFini> listAutomates;

    @FXML
    public void onClickState(ActionEvent event) throws IOException {
        PopupStateReturn stateReturn = PopupStateCreation.display();
        if (stateReturn != null && stateReturn.isOK && !stateReturn.stateName.isEmpty()) {
            String errorMsg = af.ajouterEtat(stateReturn.stateName, stateReturn.type);
            if (errorMsg != null)
                new Alert(Alert.AlertType.ERROR, errorMsg).show();
            else
                repaint();
        } else
            System.out.println("it is null !");
    }

    @FXML
    public void onClickTransition(ActionEvent event) throws IOException {
        PopupTransitionReturn stateReturn = PopupTransitionCreation.display(af);
        if (stateReturn != null && stateReturn.isOK) {
            String errorMsg = af.ajouterTransition(stateReturn.startState, stateReturn.symbol, stateReturn.destState);
            if (errorMsg != null)
                new Alert(Alert.AlertType.ERROR, errorMsg).show();
            else
                repaint();
        } else
            System.out.println("it is null !");
    }
    @FXML
    public void onUnionAutomata(ActionEvent actionEvent) {
    	 if (af.getEtats().size() == 0)
         {
             new Alert(Alert.AlertType.ERROR, "Automate vide !").show();
             return;
         }
        Stage stageProduct = new Stage();
        AFD afdConverted = (af instanceof AFD) ? (AFD) af : ((AFND) af).determiniser_2();
        new showAutomataProduct(afdConverted, listAutomates, stageProduct, "union");
    }
    
    @FXML
    public void onIntersectAutomata(ActionEvent actionEvent) {
    	 if (af.getEtats().size() == 0)
         {
             new Alert(Alert.AlertType.ERROR, "Automate vide !").show();
             return;
         }
    	 Stage stageProduct = new Stage();
    	 AFD afdConverted = (af instanceof AFD) ? (AFD) af : ((AFND) af).determiniser_2();
       new showAutomataProduct(afdConverted, listAutomates, stageProduct, "intersect");
    }

    @FXML
    public void onClickComplement(ActionEvent event) throws IOException {
        if (af.getEtats().size() == 0)
        {
            new Alert(Alert.AlertType.ERROR, "Automate vide !").show();
            return;
        }
        Stage stageComplement = new Stage();
        AFD afdConverted = (af instanceof AFD) ? (AFD) af : ((AFND) af).determiniser_2();
        new ShowResultAutomaton(afdConverted.ComplementAFD(), stageComplement, "Complémentaire");
    }


    @FXML
    public void onClickMirror(ActionEvent event) throws IOException {
        if (af.getEtats().size() == 0)
        {
            new Alert(Alert.AlertType.ERROR, "Automate vide !").show();
            return;
        }
        Stage stageMirror = new Stage();
        AFD afdConverted = (af instanceof AFD) ? (AFD)af : ((AFND) af).determiniser_2();
        AFD imgDeterminisation = afdConverted.imageMirror().determiniser_2();
        new ShowResultAutomaton(imgDeterminisation, stageMirror, "Image miroir");
    }

    @FXML
    public void onClickSave(ActionEvent event) throws IOException {
        if (af == null || af.getEtats().size() == 0)
        {
            new Alert(Alert.AlertType.ERROR, "Automate Vide !").show();
            return;
        }

        if (!listAutomates.contains(af))
            listAutomates.add(af);

        FXMLLoader loader = new FXMLLoader(Home.class.getResource("homeView.fxml"));
        ((Stage) mainPane.getScene().getWindow()).setTitle("Théorie de la calculabilité");
        mainPane.getScene().setRoot(loader.load());
        loader.<HomeController>getController().listAutomates = listAutomates;
    }

    private void repaint() {
        imgview.setImage(SwingFXUtils.toFXImage(af.getAutomateImage().toImage(), null));
    }

    public void setAf(AutomateFini af) {
        this.af = af;
        if (af instanceof AFD)
            determinerId.setDisable(true);
        else
        {
            wordsId.setDisable(true);
            labelRead.setDisable(true);
        }
        repaint();
    }

    public void setAfOnCreation(PopupAutomataReturn returnObj) {
        af = returnObj.isAFND ? new AFND() : new AFD();
        af.setAlphabet(returnObj.alphabets);
        af.setIdAutomate(returnObj.automataName);
        if (!returnObj.isAFND)
            determinerId.setDisable(true);
        else
        {
            wordsId.setDisable(true);
            labelRead.setDisable(true);
        }

        Platform.runLater(() -> ((Stage) mainPane.getScene().getWindow()).setTitle("Creating Automate : " + returnObj.automataName + " " + (returnObj.isAFND ? "(AFND)" : "(AFD)")));
        // C'est un Exemple Manuelle
//        if (returnObj.isAFND)
//        {
//            af.setAlphabet(List.of('a','b'));
//            af.ajouterEtat("0", TypeEtat.INIT);
//            af.ajouterEtat("1", TypeEtat.MID);
//            af.ajouterEtat("2", TypeEtat.FINAL);
//            af.ajouterTransition("0",'b',"0");
//            af.ajouterTransition("0",'a',"1");
//            af.ajouterTransition("0",'a',"0");
//            af.ajouterTransition("1",'b',"2");
//            af.ajouterTransition("2",'a',"2");
//            af.ajouterTransition("2",'b',"2");
//        }
        repaint();
    }

    public void setListAutomates(List<AutomateFini> listAutomates) {
        this.listAutomates = listAutomates;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        imgview = new ImageView();
        imgview.setPreserveRatio(true);
        imgview.fitWidthProperty().bind(mainPane.widthProperty());
        imgview.fitHeightProperty().bind(mainPane.heightProperty());
        mainPane.getChildren().add(imgview);
    }

    public void onDeterminerClick(ActionEvent actionEvent) {
        AFD afd = ((AFND) af).determiniser_2();
        new ShowResultAutomaton(afd, new Stage(), afd.getIdAutomate());
    }

    public void onMinimiserClick(ActionEvent actionEvent) {
        AFD minAFD = ((AFD) af).minimiser();
        new ShowResultAutomaton(minAFD, new Stage(), "Minimisation");
    }

    public void onClickWords(ActionEvent actionEvent) {
        TextInputDialog td = new TextInputDialog("La taille des mots ?");
        td.showAndWait();
        String input = td.getEditor().getText();
        System.out.println(input);
        try {
            int taille = Integer.parseInt(input);
            List<String> words = ((AFD) af).generateAcceptedWords(taille);
            Alert a = new Alert(Alert.AlertType.INFORMATION, words.toString());
            a.setHeaderText("Les Mots Acceptés : ");
            a.setTitle("Mots Acceptés");
            a.show();
        }
        catch (NumberFormatException e)
        {
            new Alert(Alert.AlertType.ERROR, "Taille doivent être un entier !").show();
        }
    }

    public void readingWordId(ActionEvent actionEvent) {
        String word = readField.getText().strip();

        boolean isReconnue = ((AFD)af).reconnaissanceMot(word);
        labelRead.setText(isReconnue ? "True" : "False");
    }
}

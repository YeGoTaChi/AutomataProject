//package presentation;
//
//import java.awt.image.BufferedImage;
//
//import com.automataproj.automataproject.Metier.AFD;
//
//import javafx.beans.binding.Bindings;
//import javafx.embed.swing.SwingFXUtils;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.control.Alert.AlertType;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//public class AutomataProduct {
//	
//	public AutomataProduct(AFD M, AFD primeM, Stage stageProduct) {
//		
//   	 GridPane vpane = new GridPane();
//   	 vpane.setStyle("-fx-background-color: white;");
//
//         Scene scene2 = new Scene(vpane, 1280, 720);
//		
//         stageProduct.setTitle("Automate Produit");
//         stageProduct.setScene(scene2);
//         stageProduct.show();
//           
//         VBox p1 = new VBox();
//         Label automate1 = new Label("Automate 1 actuel");
//        ImageView imgview1 = automateImage(M);
//         p1.setMinHeight(100);
//         p1.setMinWidth(100);
//         imgview1.fitWidthProperty().bind(p1.widthProperty());
//         imgview1.fitHeightProperty().bind(p1.heightProperty());
//         p1.getChildren().addAll(automate1,imgview1);
//         p1.setMaxHeight(250);
//         
//         VBox p2 = new VBox();
//         Label automate2 = new Label("Automate 2");
//         // To Print Automate on Screen !
//         ImageView imgview2 = automateImage(primeM);
//         p2.setMinHeight(100);
//         p2.setMinWidth(100);
//         imgview2.fitWidthProperty().bind(p2.widthProperty());
//         imgview2.fitHeightProperty().bind(p2.heightProperty());
//         p2.getChildren().addAll(automate2,imgview2);
//         p2.setMaxHeight(250);
//         
//         Pane p3 = new Pane();
//         p3.setMinHeight(100);
//         p3.setMaxHeight(100);
//         Button constructionBtn = new Button("Construction");
//         p3.getChildren().add(constructionBtn);
//         constructionBtn.translateXProperty().bind(Bindings.divide(p3.widthProperty(), 2).subtract(constructionBtn.widthProperty().divide(2)));
//         constructionBtn.translateYProperty().bind(Bindings.divide(p3.heightProperty(), 2).subtract(constructionBtn.heightProperty().divide(2)));
//
//         
//         vpane.setAlignment(Pos.BASELINE_CENTER);
//         vpane.setPadding(new Insets(50,0,0,0));
//         
//         vpane.add(p1,0,0);
//         vpane.add(p2,0,1);
//         vpane.add(p3,0,2);
//         
//         
//         constructionBtn.setOnAction(e -> {
//       	  Stage stageProductConstruction = new Stage();
//       	  automateProductBtn(M, primeM, stageProductConstruction);  	  
//         });	          
//        
//	}
//	
//	private void automateProductBtn(AFD M, AFD primeM, Stage stage) {
//		if (M.getAlphabet().equals(primeM.getAlphabet()))
//		{
//			VBox root = new VBox();
//	    	root.setStyle("-fx-background-color: white;");
//	    	Scene scene = new Scene(root, 1280, 720);
//	    	
//	    	Pane p = new Pane();
//	    	p.setStyle("-fx-background-color: white;");
//	         ImageView imgview = automateImage(M.automataProduct(primeM));
//	         imgview.fitWidthProperty().bind(p.widthProperty());
//	         imgview.fitHeightProperty().bind(p.heightProperty());
//	         p.getChildren().add(imgview);
//	         root.getChildren().add(p);
//	         
//	         VBox nbunion = new VBox();
//	         nbunion.setStyle("-fx-background-color: white;");
//	         Label nb1 = new Label("N.B : Les deux cercles signifient les états finaux de la réunion !");
//	         
//	         nbunion.getChildren().add(nb1);
//	         nbunion.setPadding(new Insets(50,0,0,50));
//	         
//	         HBox reconnaissance1 = new HBox();
//	         reconnaissance1.setStyle("-fx-background-color: white;");
//	         TextField mot = new TextField();
//	         Button reconnaissanceBtn = new Button("Reconnaissance d'un mot par réunion");
//	         Label output = new Label();
//	         reconnaissanceBtn.setOnAction(event -> {
//	        	 if(M.automataProduct(primeM).reconnaissanceMot(mot.getText()))
//	        		 output.setText("True");
//	        	 else
//	        		 output.setText("False");
//	         });
//	         reconnaissance1.getChildren().addAll(mot,reconnaissanceBtn, output);
//	         reconnaissance1.setSpacing(20);
//	         reconnaissance1.setPadding(new Insets(0,0,0,50));;
//	
//	         VBox nbintersect = new VBox();
//	         nbintersect.setStyle("-fx-background-color: white;");
//	         Label nb2 = new Label("N.B : Les deux cercles en rouge signifient les états finaux de l'intersection !");
//	         
//	         nbintersect.getChildren().add(nb2);
//	         nbintersect.setPadding(new Insets(50,0,0,50));
//	         
//	         HBox reconnaissance2 = new HBox();
//	         reconnaissance2.setStyle("-fx-background-color: white;");
//	         TextField mot2 = new TextField();
//	         Button reconnaissanceBtn2 = new Button("Reconnaissance d'un mot par intersection");
//	         Label output2 = new Label();
//	         reconnaissanceBtn2.setOnAction(event -> {
//	        	 if(M.automataProduct(primeM).reconnaissanceMot(mot2.getText()))
//	        		 output2.setText("True");
//	        	 else
//	        		 output2.setText("False");
//	         });
//	         reconnaissance2.getChildren().addAll(mot2,reconnaissanceBtn2, output2);
//	         reconnaissance2.setSpacing(20);
//	         reconnaissance2.setPadding(new Insets(0,0,50,50));;
//	         
//	         root.getChildren().addAll(nbunion,reconnaissance1,nbintersect,reconnaissance2);
//	         
//	         stage.setTitle("Automate Produit");
//	         stage.setScene(scene);
//	         stage.show();
//		}
//         else
//     	{
//     		 Alert a = new Alert(AlertType.ERROR,"Les deux automates n'ont pas le même alphabet !");
//     		 a.show();
//     	}
//    }
//	
//	private ImageView automateImage(AFD M) {
//		BufferedImage img = M.getAutomateImage().toImage();
//        ImageView imgview = new ImageView(SwingFXUtils.toFXImage(img, null));
//        imgview.setPreserveRatio(true);  
//        return imgview;
//	}
//}

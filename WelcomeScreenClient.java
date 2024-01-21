package Game;

import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
/**
 * The splash screen for the client player. This slightly differs from the server player - there is an additional field for opponent name/address
 * This screen gets first choice of color so they are able to choose from all 4 of the available colors
 * This splash screen appears first followed by the server splash screen 
 * This information (name and color) is used to set the client player and opponent address will be used to set HOST.
 * @param parent
 * @param obMainStage
 */
public class WelcomeScreenClient extends Pane
{
	private ClientPlayer obParent;
	private String sColor;
	public Stage obLeaderStage = new Stage();
	public Stage obReplayStage = new Stage();



public WelcomeScreenClient(ClientPlayer parent, Stage obMainStage) 
{
		super();
		
		obParent = parent;
		
		//group the radio buttons
		ToggleGroup colorGroup = new ToggleGroup();
		GridPane gpane = CommonSplash.setSplash(colorGroup);
	
		//set up radio buttons for the 4 chosen colors - client player gets first choice of color
		setUpColorRadios(gpane, colorGroup);
		
		//Set up the name textfield
		TextField nameBox = new TextField();
		GridPane.setConstraints(nameBox, 1, 5);
		
		//opponent info
		Label oppLbl=new Label("Opponent IP:");
		TextField oppBox=new TextField();
		
		//set the default text to localhost this can simply be changed still but provides default value
		oppBox.setText("localhost");
		
		//set up opponent info on the screen
		GridPane.setConstraints(oppLbl, 0, 8); 
		GridPane.setConstraints(oppBox, 1, 8); 
		
		//Button for replay on splash screen and associated on action event
		Button replayButton = new Button("Replay Last Game");
		CommonSplash.replayAction(replayButton, obReplayStage);
		
		//Button for leaderboard on splash screen and associated on action event
		Button leaderBoard = new Button("LeaderBoard");
		CommonSplash.leaderBoardAction(leaderBoard, obLeaderStage);
		
		//Button to start the game - all fields must be full
		Button startButton = new Button("Start Game");  
		setStart(startButton, nameBox, colorGroup, oppBox, obMainStage, parent);
		
		//set up where each button appears within the grid pane on the splash screen
		GridPane.setConstraints(replayButton, 0, 0);
		GridPane.setConstraints(leaderBoard, 0, 1);
		GridPane.setConstraints(startButton, 1, 13);
		
		gpane.getChildren().addAll(startButton, leaderBoard, replayButton, nameBox, oppLbl, oppBox);
		
		this.setStyle("-fx-background-color: honeydew");
		this.getChildren().add(gpane);
	}
	
	/**
	 * Setting up the radio buttons for each color choice. Adding them to the togglegroup
	 * The client player will get the choice of all the colors: first pick of color.
	 * @param gpane
	 * @param colorGroup
	 */
	private void setUpColorRadios(GridPane gpane, ToggleGroup colorGroup) 
	{
		//colours
		RadioButton btnBlue = new RadioButton("BLUE");
	    CommonSplash.createRadio(Color.CORNFLOWERBLUE, 1, 10, btnBlue,colorGroup);
	    
		RadioButton btnRed = new RadioButton("RED");
	    CommonSplash.createRadio(Color.FIREBRICK, 1, 12, btnRed,colorGroup); 
		
	    RadioButton btnGreen = new RadioButton("GREEN");
	    CommonSplash.createRadio(Color.GREENYELLOW, 2, 10, btnGreen,colorGroup); 
	    
	    RadioButton btnPink = new RadioButton("PINK");
	    CommonSplash.createRadio(Color.LIGHTPINK, 2, 12, btnPink,colorGroup); 
		
		gpane.getChildren().addAll(btnBlue,btnRed,btnGreen,btnPink);
		sColor= "";
		btnPink.setOnAction(e -> sColor = "LIGHTPINK");
		btnGreen.setOnAction(e -> sColor = "GREENYELLOW");
		btnRed.setOnAction(e -> sColor = "FIREBRICK");
		btnBlue.setOnAction(e -> sColor = "CORNFLOWERBLUE");
	}
	
	/**
	 * Setting the start button when the client player has entered information they can press start to start the game.
	 * If all fields are not filled/selected an alert is show.
	 * The client player requires an extra field for the server name/ip address - which will be used to set up the socket
	 * When start button is pressed the game will start - actually will go to server splash screen and have them fill in the information and then
	 * the game can proceed
	 * @param startButton
	 * @param nameBox
	 * @param colorGroup
	 * @param oppBox
	 * @param obMainStage
	 * @param parent
	 */
	private void setStart(Button startButton, TextField nameBox, ToggleGroup colorGroup, TextField oppBox, Stage obMainStage, ClientPlayer parent) 
	{
		startButton.setOnAction(e -> {
	        if (nameBox.getText().isEmpty() || colorGroup.getSelectedToggle() == null || oppBox.getText().isEmpty()) {
	           
	        	// If any of the required fields is empty, show an error message
	            Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setHeaderText("Please fill in all fields to play.");
	            alert.showAndWait();
	        }
	        else 
	        {
	           obParent.setClientPlayer(sColor, nameBox.getText());
	           obParent.setHostName(oppBox.getText());
	           obMainStage.setScene(new Scene(obParent.obBorPaneClient, 600, 600));
	           obMainStage.show();
	           parent.startGame();
	        }
	    });
	}
}
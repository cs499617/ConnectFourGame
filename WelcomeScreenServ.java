

package Game;

import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
/**
 * The splash screen for the server player. This will have fields for names and the remaining colors (the client chooses a color and
 * that color will be removed from the options available for this player). This information (name and color) is used to set the server player.
 * @param parent
 * @param obMainStage
 * @param sClientColor
 */
public class WelcomeScreenServ extends Pane 
{
	private ServerPlayer obParent;
	private String sColor;
	public Stage obLeaderStage = new Stage();
	public Stage obReplayStage = new Stage();

	
	public WelcomeScreenServ(ServerPlayer parent, Stage obMainStage, String sClientColor) 
	{
		super();
		
		obParent = parent;
			
		//group the radio buttons
		ToggleGroup colorGroup = new ToggleGroup();
		GridPane gpane = CommonSplash.setSplash(colorGroup);
	
		//set up radio buttons for color choice
		setUpColorRadios(sClientColor, colorGroup, gpane);
		
		TextField nameBox = new TextField();
		GridPane.setConstraints(nameBox, 1, 5);
		
		//Button for replay on splash screen and associated on action event
		Button replayButton = new Button("Replay Last Game");
		CommonSplash.replayAction(replayButton, obReplayStage);
		
		//Button for leaderboard on splash screen and associated on action event
		Button leaderBoard = new Button("LeaderBoard");
		CommonSplash.leaderBoardAction(leaderBoard, obLeaderStage);
		
		Button startButton = new Button("Start Game");  
		setStart(startButton, colorGroup, nameBox, parent); 
		
		//set up where each button appears within the grid pane on the splash screen
		GridPane.setConstraints(replayButton, 0, 0);
		GridPane.setConstraints(leaderBoard, 0, 1);
		GridPane.setConstraints(startButton, 1, 13);
		
		gpane.getChildren().addAll(startButton, leaderBoard, replayButton, nameBox);
		
		this.setStyle("-fx-background-color: honeydew");
		this.getChildren().add(gpane);
	}
	
	/**
	 * This method takes the color selected by the client player and adds radio buttons to the server splash screen
	 * for only the colors that were not selected by the client. This prevents both players using the same color of discs. 
	 * Only add the colors to the screen that were not already selected by the client
	 * @param sClientColor
	 * @param colorGroup
	 * @param gPane
	 */
	private void setUpColorRadios( String sClientColor, ToggleGroup colorGroup, GridPane gPane) 
	{
		//colours
				System.out.println(sClientColor+" <----- client colour we are looking at"); 
				
				if(!sClientColor.contains("CORNFLOWERBLUE")) 
				{
					RadioButton btnBlue = new RadioButton("BLUE");
					CommonSplash.createRadio(Color.CORNFLOWERBLUE, 1, 10, btnBlue,colorGroup); 
					gPane.getChildren().add(btnBlue);
					btnBlue.setOnAction(e -> sColor = "CORNFLOWERBLUE");
				}
		        
				if(!sClientColor.contains("FIREBRICK")) 
				{
					RadioButton btnRed = new RadioButton("RED");
					CommonSplash.createRadio(Color.FIREBRICK, 1, 12, btnRed,colorGroup); 
					gPane.getChildren().add(btnRed);
					btnRed.setOnAction(e -> sColor = "FIREBRICK");
				}
				
				if(!sClientColor.contains("GREENYELLOW")) 
				{
			        RadioButton btnGreen = new RadioButton("GREEN");
			        CommonSplash.createRadio(Color.GREENYELLOW, 2, 10, btnGreen,colorGroup); 
			        gPane.getChildren().add(btnGreen);
			        btnGreen.setOnAction(e -> sColor = "GREENYELLOW");
				}
		        
				if(!sClientColor.contains("LIGHTPINK")) 
				{
			        RadioButton btnPink = new RadioButton("PINK");
			        CommonSplash.createRadio(Color.LIGHTPINK, 2, 12, btnPink,colorGroup); 
			        gPane.getChildren().add(btnPink);
			        btnPink.setOnAction(e -> sColor = "LIGHTPINK");
				}     
	}
	
	/**
	 * Set up the start button for the server player. This screen just checks that the fields for name and color is filled.
	 * An alert is shown if a field is left empty
	 * on click will start the game
	 * @param startButton
	 * @param colorGroup
	 * @param nameBox
	 * @param parent
	 */
	private void setStart(Button startButton, ToggleGroup colorGroup, TextField nameBox, ServerPlayer parent) 
	{
		startButton.setOnAction(e -> {
		    if (nameBox.getText().isEmpty() || colorGroup.getSelectedToggle() == null) {
		      
		    	// If any of the required fields is empty, show an error message
		        Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Error");
		        alert.setHeaderText("Please fill in all fields to play.");
		        alert.showAndWait();
		    } else {
		    	System.out.println(sColor);
		    	obParent.setServerPlayer(sColor, nameBox.getText());
		    	parent.startGame();
		    }
		});
		
	}
	
}

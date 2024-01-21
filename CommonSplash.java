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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


/**
 * This class contains the methods that are needed for both the server and client splash screen
 * the purpose of this class is to reduce duplicate code between server and client splash screens.
 *
 */
public class CommonSplash 
{
	
	/**
	 * Set up and return the GridPane and the common features between the server and client splash screen
	 * This gridpane will serve as the display of what will be seen when the splash screen shows. 
	 * @param colorGroup
	 * @return
	 */
	public static GridPane setSplash(ToggleGroup colorGroup) 
	{
		GridPane gpane=new GridPane();
		gpane.setPadding(new Insets(10,10,10,10));
		gpane.setVgap(8);
        gpane.setHgap(10);
  
		//add title
        Label titleLbl=new Label("WELCOME TO CONNECT 4!");
        titleLbl.setFont(Font.font("",FontWeight.BOLD,12));
        GridPane.setConstraints(titleLbl, 0, 2); 
		
		//player info
		Label nameLbl=new Label("Your screen name:");
		
		//set them up
		GridPane.setConstraints(nameLbl, 0, 5); 
		
		//Select your colour label
		Label selLbl=new Label("Pick your disc colour:");
		GridPane.setConstraints(selLbl, 0, 9); 
		
		gpane.getChildren().addAll(nameLbl, selLbl, titleLbl);
		
		return gpane;
	}
	
	/**
	 * The action that occurs when the leaderboard button is pressed on either splash screen
	 * basically just pops up a BorderPane that displays current leader information. 
	 * @param leaderBoard
	 * @param obLeaderStage
	 */
	public static void leaderBoardAction(Button leaderBoard, Stage obLeaderStage) 
	{
		
		  leaderBoard.setOnAction(e -> {
				Platform.runLater(()->{
					BorderPane leaderPane = new BorderPane();
					leaderPane = LeaderBoard.getLeaderBoard();
					obLeaderStage.setScene(new Scene(leaderPane, 400, 500));
					obLeaderStage.show();
				});
		   });
		 
	}
	
	/**
	 * Action that occurs when the replay button is pressed on either splash screen
	 * Will take the Moves stored in the database and show them with a sleep in between each move
	 * essentially showing what happened in the last game that was played. 
	 * @param replayButton
	 * @param obReplayStage
	 */
	public static void replayAction(Button replayButton, Stage obReplayStage) 
	{
		 replayButton.setOnAction(e -> {
			 
			 
		    	Platform.runLater(()->{
		    		
		    		BorderPane replayPane = new BorderPane();
					replayPane = Replay.getReplay();
					obReplayStage.setScene(new Scene(replayPane, 600, 600));
					obReplayStage.show();
					
					MyCon myCon = new MyCon();
					myCon.connectDB();
					List<Move> obList = MyCon.getMoves(MyCon.getLastGameID());
					new Thread(()->{
						for(Move obMove : obList) {
							
							if(obList.indexOf(obMove) % 2 == 0) {
							Replay.showTurn(new Player("LIGHTPINK", "REPLAY"), obMove);
							
							}
							else 
							{
								Replay.showTurn(new Player("FIREBRICK", "REPLAY"), obMove);
							}
							try 
							{
								Thread.sleep(1000);
							} 
							catch (InterruptedException e1) 
							{
								
								e1.printStackTrace();
							}
						}
					}).start();
					Replay.resetTracker();
				});
		    }); 
	}
	
	/**
	 * This method was created to reduce redundant code when creating the seven radiobuttons used between the client and player splash screen
	 * We tabke in the button we want to set up along with the color it corresponds to and the positions in the gridpane where we want it to show up
	 * @param obColor
	 * @param nPos1
	 * @param nPos2
	 * @param btnRad
	 * @param colorGroup
	 */
	public static void createRadio(Color obColor, int nPos1, int nPos2, RadioButton btnRad, ToggleGroup colorGroup) 
	{
		
		btnRad.setGraphic(CommonSplash.createCircle(10, obColor));
		btnRad.setGraphicTextGap(5);
		btnRad.setToggleGroup(colorGroup);
		GridPane.setConstraints(btnRad, nPos1, nPos2); 
		
	}
	
	/**
	 * Creates the circles that will be filled with the associated color for each radio button
	 * @param radius
	 * @param color
	 * @return
	 */
	public static Circle createCircle(double radius, Color color) {
        Circle circle = new Circle(radius);
        circle.setFill(color);
        return circle;
    }
}

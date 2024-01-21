package Game;

import java.util.List;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class provides a leader board that will appear if the leader board button is pressed on the splash screen \
 * Leader board consists of the top 10 players from the database in terms of overall connect-four wins
 *
 */
public class LeaderBoard extends Application 
{
	private static Text obText;
	private static MyCon dbCon = new MyCon();
	
	@Override
	public void start(Stage obStage) throws Exception
	{
		BorderPane obBorPane = new BorderPane();
		obBorPane.setTop(getTop());
		obBorPane.setCenter(getCenter());
		
		obStage.setScene(new Scene(obBorPane,400,500));
		obStage.show();
	}

	/**
	 * Set up the borderpane for that will display when the corresponding button is pressed
	 * @return
	 */
	public static BorderPane getLeaderBoard() 
	{
		BorderPane obBorPane = new BorderPane();
		obBorPane.setTop(getTop());
		obBorPane.setCenter(getCenter());
		return obBorPane;
	}
	
	/**
	 * Here we go through the List of Players from the database that correspond to the top ten players by win
	 * and display the player name and number of wins in the Pane which will be centered in the Leaderboard BorderPane
	 * @return
	 */
	public static Pane getCenter() 
	{
		Pane obPane = new Pane();
		
		int nFontSize = 30;
		int nRow = 1;
		List<String[]> obList = MyCon.getLeaderBoardInfo();
		for(String[] sVal : obList) 
		{
			Text leadersTxtName = new Text(sVal[0]);
			Text leadersTxtWins = new Text(sVal[1]);
			
			leadersTxtName.setFont(Font.font("arial", FontWeight.BOLD, FontPosture.REGULAR, nFontSize));
			leadersTxtWins.setFont(Font.font("arial", FontWeight.BOLD, FontPosture.REGULAR, nFontSize));
			leadersTxtName.setTranslateY(nRow * nFontSize + 10);
			leadersTxtName.setTranslateX(130);
			leadersTxtWins.setTranslateY(nRow * nFontSize + 10);
			leadersTxtWins.setTranslateX(100);
			obPane.getChildren().addAll(leadersTxtName,leadersTxtWins);
			
			nRow++;
		}
		
		return obPane;
	}
	
	/**
	 * Setting up the styles for the top of the LeaderBoard borderpane. 
	 * @return
	 */
	public static HBox getTop() 
	{
		HBox obBox = new HBox();
		obText = new Text("LeaderBoard");
		obBox.setStyle("-fx-background-color: honeydew");
		obBox.setPrefHeight(100);
		obText.setFont(Font.font("arial", FontWeight.BOLD, FontPosture.REGULAR, 45));
		obText.setFill(Color.DIMGREY);
		obBox.setAlignment(Pos.CENTER);
		obBox.getChildren().add(obText);
		
		return obBox;
	}
	
	public static void main(String[] args) 
	{
		Application.launch(args);

	}

}

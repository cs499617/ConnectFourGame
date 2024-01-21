package Game;

import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;



/**
 * This class provides a replay board that will appear if the replay last game button is pressed on the splash screen 
 * The replay pulls the last game's moves from the database and plays them to show the last game. 
 * Unfortunately this class was implemented using alot of duplicated code in order to separate the replay functionality from the
 * functionality of the main game with limited time. 
 *
 */
public class Replay extends Application 
{
	private static final int numRows = 6;
	private static final int numCols = 7;
	private static int[] nRowTracker = {5,5,5,5,5,5,5};
	private static Circle[][] aCircles = new Circle[numRows][numCols];
	
	@Override
	public void start(Stage obStage) throws Exception
	{
		BorderPane obBorPane = new BorderPane();
		obBorPane.setCenter(getCenter());
		
		obStage.setScene(new Scene(obBorPane,400,500));
		obStage.show();
	}

	public static BorderPane getReplay() 
	{
		BorderPane obBorPane = new BorderPane();
		obBorPane.setCenter(getCenter());
		return obBorPane;
	}
	
	public static BorderPane getCenter() 
	{
		GridPane obPane = new GridPane();
		obPane.setHgap(20);
		obPane.setVgap(20);
		obPane.setAlignment(Pos.CENTER);
		obPane.setStyle("-fx-background-color: honeydew");
		
		BorderPane obBorPane = new BorderPane();
		popBoard(obPane);
		obBorPane.setCenter(obPane);
	
		obBorPane.setBottom(getBottom("replay"));
		
		return obBorPane;	
	}
	
	public static void popBoard(GridPane obPane) 
	{
		for(int i =0; i < aCircles.length; i++) 
		{
			for(int j =0; j < aCircles[i].length; j++) 
			{
				aCircles[i][j] = new Circle(25);
				aCircles[i][j].setFill(Color.WHITE);
				aCircles[i][j].setStroke(Color.DIMGREY);
				aCircles[i][j].setStrokeWidth(2);
				
				obPane.add(aCircles[i][j], j, i);
			}
		}
	}
	
		public static HBox getBottom(String sText) 
		{
			HBox obBox = new HBox();
			
			Text obText = new Text(sText);
			obBox.setStyle("-fx-background-color: lightyellow");
			obBox.setPrefHeight(100);
			obText.setFont(Font.font("arial", FontWeight.BOLD, FontPosture.ITALIC, 45));
			obText.setFill(Color.DIMGREY);
			obBox.setAlignment(Pos.CENTER);
			obBox.getChildren().add(obText);
			
			return obBox;
		}
		
		public static void showTurn(Player obCurrent, Move obMove) 
		{
			Color sColor = CommonFX.convertToColor(obCurrent.getColor());
			
			int sleepTime = 150;
			
			new Thread(()->{
				
				for(int i = 0; i <= nRowTracker[obMove.getCol()]; i++) 
				{
					
					try
					{
						final int nRow = i;
						
						Platform.runLater(()->
						{
							aCircles[nRow][obMove.getCol()].setFill(sColor);
						});
						
						if(nRow > 0) 
						{
							Platform.runLater(()->{
								aCircles[nRow - 1][obMove.getCol()].setFill(Color.WHITE);
							});	
						}
						
						Thread.sleep(sleepTime);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				nRowTracker[obMove.getCol()]--;
			}).start();
			
		}
	
	/**
	 * Reset the row tracker so if the replay is viewed more than once it will reset so the new replay game starts from row zero
	 */
	public static void resetTracker() 
	{
		for(int i = 0; i < nRowTracker.length; i++) 
		{
			nRowTracker[i] = 5;
		}
	}
	
	public static void main(String[] args) 
	{
		Application.launch(args);

	}

}

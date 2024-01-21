package Game;

import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
  

/**
 * This class contains the methods that are shared between ServerPlayer.java and ClientPlayer.java.
 * This helps us to reduce our amount of code as we can call from this common location rather than
 * incorporating the identical methods into each. 
 *
 */
public class CommonFX 
{
	private static final int numRows = 6;
	private static final int numCols = 7;

	private static int[] nRowTracker = {5,5,5,5,5,5,5};
	private static Text obText;
	private static Circle[][] aCircles = new Circle[numRows][numCols];

	public static Stage obReplayStage = new Stage();
	
	/**
	 * This method sets up the initial connect-four board and populates it with the circles.
	 * We also set an Hbox at the bottom to track player turns. 
	 * 
	 * @param startText
	 * @return
	 */
	public static BorderPane setBoard(String startText) 
	{
		GridPane obPane = new GridPane();
		obPane.setHgap(20);
		obPane.setVgap(20);
		obPane.setAlignment(Pos.CENTER);
		obPane.setStyle("-fx-background-color: honeydew");
		
		BorderPane obBorPane = new BorderPane();
		popBoard(obPane);
		obBorPane.setCenter(obPane);
	
		obBorPane.setBottom(getBottom(startText));
		
		return obBorPane;
	}

	/**
	 * This method populates the gridpane with circles
	 * Here we set the initial size, color and outline and position of Circles within the grid. 
	 * @param obPane
	 */
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
	
	/**
	 * This method returns a box that will be at the bottom of the game displaying text.
	 * 
	 * @param sText
	 * @return
	 */
	public static HBox getBottom(String sText) 
	{
		HBox obBox = new HBox();
		
		obText = new Text(sText);
		obBox.setStyle("-fx-background-color: lightyellow");
		obBox.setPrefHeight(100);
		obText.setFont(Font.font("arial", FontWeight.BOLD, FontPosture.ITALIC, 45));
		obText.setFill(Color.DIMGREY);
		obBox.setAlignment(Pos.CENTER);
		obBox.getChildren().add(obText);
		
		return obBox;
	}

	
	/**
	 * This is the fx styles for the BorderPane which represents the Waiting screen while the
	 * server waits for the client to connect. 
	 * @param startText
	 * @return
	 */
	public static BorderPane setWait(String startText) 
	{
		GridPane obPane = new GridPane();
		obPane.setHgap(20);
		obPane.setVgap(20);
		obPane.setAlignment(Pos.CENTER);
		
		BorderPane obBorPane = new BorderPane();
		obBorPane.setCenter(obPane);

		obBorPane.setCenter(getBottom(startText));
		
		
		return obBorPane;
	}
	
	/**
	 * This method is used in both the client and server player programs.
	 * We use this to set the text to show whose turn it is. 
	 * @param sText
	 */
	public static void setBottomText(String sText) 
	{
		new Thread(()->{

			Platform.runLater(()->
			{
				obText.setText(sText);
			});
					
		}).start();
	}
	
	/**
	 * This method will go through each circle in the top row of the multidimensional array of circles that effectively make up the "board"
	 * It will disable the circles; we will use this to disable all circles once a player clicks one- so they can only make one play per turn.
	 * Here we also clear the dashed lines surrounding the circles which are used to show which circles are available to be clicked on when 
	 * it is your turn. 
	 * @param obCirc
	 */
	public static void disable() 
	{
		for(int i=0; i< numCols; i++) 
		{
			aCircles[0][i].getStrokeDashArray().clear();
			aCircles[0][i].setDisable(true);
		}
	}
	
	/**
	 * This method does essentially the same thing as the method above. However this is used to disable a single column which would be
	 * used in the case that the column is full. 
	 * @param nCol
	 */
	public static void disable(int nCol) 
	{
		aCircles[0][nCol].getStrokeDashArray().clear();
		aCircles[0][nCol].setDisable(true);
	}
	
	/** 
	 * Effectively the opposite of the disable method above - goes through and enables all the circles in the top "row" of the "board"
	 * @param obCirc
	 */
	public static void enable() 
	{
		for(int i=0; i< numCols; i++) 
		{
			aCircles[0][i].setDisable(false);
			aCircles[0][i].getStrokeDashArray().addAll(5d, 5d);
		}
	}

	/**
	 * This is a getter method for the aCircles multidimensional array of circles
	 * Called in the ServerPlayer.java and ClientPlayer.java so that the mouse click event can set which circles can be clicked. 
	 * @return
	 */
	public static Circle[][] getCircles()
	{
		return aCircles;
	}
	
	/**
	 * This is the getter method for the Row tracker.
	 * This will be used to track when to disable circles once the column has no more space in the ServerPlayer.java and ClientPlayer.java.
	 * @return
	 */
	public static int[] getTracker()
	{
		return nRowTracker;
	}

	/**
	 * This method will show moves. By changing the color of the circles in the "board" it will appear as though a puck is dropping
	 * and setting at the bottom available space.
	 * To achieve this we use nRowTracker to determine what the last available space is in the column and that is where the circle will "fall". 
	 * @param obCurrent
	 * @param obMove
	 */
	public static int[] showTurn(Player obCurrent, Move obMove) 
	{
		System.out.println(obCurrent.getColor());
		
		//convert the string color of the current player to a Color
		Color sColor = convertToColor(obCurrent.getColor());
		
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
		return nRowTracker;
	}

	
	/**
	 * This method is used to make the winning connected - four circles appear as though they are pulsing.
	 * We simply change the color of the stroke of those four circles from one color to another so they appear as though they are pulsing.
	 * @param winnerCoords
	 */
	public static void highlightWinnerCircles(int[][] winnerCoords) 
	{
		for(int i = 0; i < winnerCoords.length; i++) 
		{
			final int nIndex = i;
			
			new Thread(()->{
				
				while (true) {
					try {
						Platform.runLater(()->
						{
							if (aCircles[winnerCoords[nIndex][0]][winnerCoords[nIndex][1]].getStroke() == Color.DIMGREY) 
							{
								aCircles[winnerCoords[nIndex][0]][winnerCoords[nIndex][1]].setStroke(Color.WHITE);
								aCircles[winnerCoords[nIndex][0]][winnerCoords[nIndex][1]].setFill(Color.BLACK);
							}
							else 
							{
								aCircles[winnerCoords[nIndex][0]][winnerCoords[nIndex][1]].setStroke(Color.DIMGREY);
							}
						});
						Thread.sleep(500);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				
			}).start();
		}
	}
	
	/**
	 * This method is used to take in a color as a string and return that color as a Color object. 
	 * This is useful for when we have the color as a string of text and need it to be passed as a Color.
	 * @param sCol
	 * @return
	 */
	public static Color convertToColor(String sCol)
	{
		switch(sCol) 
		{

		case "CORNFLOWERBLUE":
			return Color.CORNFLOWERBLUE;
		
		case "GREENYELLOW":
			return Color.GREENYELLOW;
			
		case "FIREBRICK":
			return Color.FIREBRICK;
			
		default:
		case "LIGHTPINK":
			return Color.LIGHTPINK;			
		}
	} 
	
	/**
	 * This method allows us to make text flash when there is a winner.
	 * The text goes from visible to non visible to achieve this flashing. 
	 */
	public static  void flashText() 
	{
		new Thread(() -> { 
				try {
			
					while (true) {
						Platform.runLater(()-> 
						{
							if (obText.isVisible()) 
							{
								obText.setVisible(false);
							}
							else 
							{
								obText.setVisible(true); 
							}
						});
						Thread.sleep(500);
					}
				}
				catch (InterruptedException ex) {
				}
		}).start(); 
	}
	
}

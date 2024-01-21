package Game;

import java.io.Serializable;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


/**
 * The game class contains methods and information needed to create and keep track of a connect-four game
 * Here we determine who goes first - randomly
 * Here we have getters/setters for the players in the game
 * Here we swap turns between players
 * Here we keep track of each move (using player ID's to mark which spots are filled)
 * We have a method to check for winner using the previously stated method - are four positions in the array filled with the same non - zero number. 
 * If we find a winner we also track where the winning coordinates in the array are, so we can make them appear how we want when a player wins. 
 *
 */
public class Game implements Serializable 
{
	
	private static final long serialVersionUID = 1L;
	private int currentPlayerID;
	private Player playerServer;
	private Player playerClient;
	private final static int numRows = 6;
	private final static int numCols = 7;
	private static int[][] aCurrentBoardState = new int[numRows][numCols];
	private int[][] winnerCoords = new int[4][2];
	private int gameID = 0;
	

	public Game(Player server, Player client)
	{
		currentPlayerID = getFirstPlayer();
		this.playerServer = server;
		this.playerClient = client;
		
	}
	
	public void setClientPlayer(Player nPlayer)
	{
		playerClient = nPlayer;
	}

	public Player getClientPlayer()
	{
		return playerClient;
	}
	
	public void setServerPlayer(Player nPlayer)
	{
		playerServer = nPlayer;
	}
	
	public Player getServerPlayer()
	{
		return playerServer;
	}
	
	/**
	 * This will choose randomly which of the 2 players goes first
	 * @return
	 */
	private int getFirstPlayer() 
	{
		return (int)(Math.random() *2);
	}
	
	/**
	 * This will return the currentPlayer whose turn it currently is
	 * @return
	 */
	public  Player getCurrentPlayer()
	{
		if(currentPlayerID == 0) 
		{
			return playerServer;
		}
		else 
		{
			return playerClient;
		}
	}
	
	/**
	 * Returns the currentPlayer ID
	 * 0 means its the servers turn
	 * 1 means its the clients turn
	 * @return
	 */
	public int getCurrentPlayerID()
	{
		return currentPlayerID;
	}
	
	/**
	 * Swap which player's turn it is
	 */
	public void swapPlayer()
	{
		currentPlayerID = (currentPlayerID + 1) % 2;
	}
	
	public int getID() 
	{
		return gameID;
	}
	
	public void setID( int nVal) 
	{
		gameID += nVal;
	}
	
	
	/**
	 *This stores each move made as the player turn (+1) in order to differentiate between the initialized
	 *array of zeros. 
	 * @param row
	 * @param col
	 */
	public  void storeMove(Move obMove) 
	{
		aCurrentBoardState[obMove.getRow()][obMove.getCol()] = currentPlayerID + 1;		
	}
	
	/**
	 * This method is used to check if there is a winner of the game.
	 * using the current state of the board we are checking for four 1s or 2s in a row within the array
	 * Zeros indicate that no disc is in that spot. 
	 * @return
	 */
	public boolean isWinner() 
	{
		consoleGameArray();
		int[] nVals = {1,2};
	    for (int nV : nVals) 
	    {
	        for (int i = 0; i < aCurrentBoardState.length; i++) 
	        {
	            for (int j = 0; j < aCurrentBoardState[0].length; j++) 
	            {
	                if (j <= aCurrentBoardState[0].length - 4 &&
		                	aCurrentBoardState[i][j] == aCurrentBoardState[i][j+1] &&
		                	aCurrentBoardState[i][j] == aCurrentBoardState[i][j+2] &&
		                	aCurrentBoardState[i][j] == aCurrentBoardState[i][j+3] &&
		                	aCurrentBoardState[i][j] != 0) 
	                {
	                	winnerCoords[0] = new int[]{i, j};
	                	winnerCoords[1] = new int[]{i, j + 1};
	                	winnerCoords[2] = new int[]{i, j + 2};
	                	winnerCoords[3] = new int[]{i, j + 3};
	                    return true;
	                }
	                // Check vertical line
	                if (i <= aCurrentBoardState.length - 4 &&
	                		aCurrentBoardState[i][j] == nV &&
	                		aCurrentBoardState[i+1][j] == nV &&
	                		aCurrentBoardState[i+2][j] == nV &&
	                		aCurrentBoardState[i+3][j] == nV) 
	                {
	                	winnerCoords[0] = new int[]{i, j};
	                	winnerCoords[1] = new int[]{i + 1, j};
	                	winnerCoords[2] = new int[]{i + 2, j};
	                	winnerCoords[3] = new int[]{i + 3, j};
	                    return true;
	                }
	                // Check diagonal line (top left to bottom right)
	                if (i <= aCurrentBoardState.length - 4 && j <= aCurrentBoardState[0].length - 4 &&
	                		aCurrentBoardState[i][j] == nV &&
	                		aCurrentBoardState[i+1][j+1] == nV &&
	                		aCurrentBoardState[i+2][j+2] == nV &&
	                		aCurrentBoardState[i+3][j+3] == nV) 
	                {
	                	winnerCoords[0] = new int[]{i, j};
	                	winnerCoords[1] = new int[]{i + 1, j + 1};
	                	winnerCoords[2] = new int[]{i + 2, j + 2};
	                	winnerCoords[3] = new int[]{i + 3, j + 3};
	                    return true;
	                }
	                // Check diagonal line (bottom left to top right)
	                if (i >= 3 && j <= aCurrentBoardState[0].length - 4 &&
	                		aCurrentBoardState[i][j] == nV &&
	                		aCurrentBoardState[i-1][j+1] == nV &&
	                		aCurrentBoardState[i-2][j+2] == nV &&
	                		aCurrentBoardState[i-3][j+3] == nV) 
	                {
	                	winnerCoords[0] = new int[]{i, j};
	                	winnerCoords[1] = new int[]{i - 1, j + 1};
	                	winnerCoords[2] = new int[]{i - 2, j + 2};
	                	winnerCoords[3] = new int[]{i - 3, j + 3};
	                    return true;
	                }
	            }
	        }
	    }
	    return false;   
	}
	
	/**
	 * This method simply checks the rowTracker. If all the positions in this array are zero then all columns are full of discs and
	 * the game is a draw/tie
	 * @return
	 */
	public boolean isTie() 
	{		
		int[] naVals = CommonFX.getTracker();
		
		for(int i = 0; i < naVals.length; i++) 
		{
			if(naVals[i] != 0) 
			{
				return false;
			}
		}	
	    return true;
	}

	/**
	 * Returns the winnerCoords array which is an array of where the Winning/connected - 4 discs are
	 * @return
	 */
	public int[][] getWinnerCoords()
	{
		return winnerCoords;
	}
	
	public int getCols() 
	{
		return numCols;
	}
	
	public int getRows() 
	{
		return numRows;
	}
	
	public static void consoleGameArray() 
	{
		System.out.println("Game Board State:");
		System.out.println("-----------------------------------------");
		for (int i = 0; i < aCurrentBoardState.length; i++) 
        {
            for (int j = 0; j < aCurrentBoardState[0].length; j++) 
            {
            	System.out.print(aCurrentBoardState[i][j]+" ");
            }
            System.out.print(" \n");
        }
	}
	
	
}

package Game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/**
 * This is the ClientPlayer class which represents one of the connect-four players
 * Here we set up a socket to initialize a connection with the other player (serverplayer)
 * We start with a splash screen that will ask for name, opponent name, and color of disc
 * These values are used to establish the connection (opponent name/ip)
 * and create the client Player (color and name). 
 * Each move will be shown on the players display and sent to the other player so it is played on their display.
 * Then we will receive a move from the other player and show that move on our display and repeat until the game
 *  is won (4 connected discs of the same color)or drawn (board is completely filled with no winner)
 * 
 *
 */
public class ClientPlayer extends Application 
{
	private static final int CLIENT_PORT = 19122;   
	private static String HOST;
	private static Socket obSocket;

	private static ObjectOutputStream obOut;
	private static ObjectInputStream obIn;
	
	private static Game connectFour;
	private static Player clientPlayer = new Player();
	
	public BorderPane obBorPaneClient;
	
	@Override
	public void start(Stage obStage) throws Exception 
	{	
		obStage.setScene(new Scene(new WelcomeScreenClient(this,obStage), 500, 350));
		obStage.setTitle("Connect-Four: ");
		obBorPaneClient = CommonFX.setBoard("");
		obStage.show();
	}
	
	/**
	 * This represents the basic structure of the game. Start the connection, send the client player to the server so it can make a new game,
	 * then receive the new game (which includes the 2 Player objects and by random draw who will go first)
	 * From here we will either take our turn or receive a move.
	 */
	public void startGame()
	{
		new Thread(()->{
			//initialize the connection with the server
			initConnection();
			
			//send client Player Object
			sendPlayer(clientPlayer);
			
			//Receive game from server
			connectFour = receiveGame();
	
			//Take turn and show and send it to the server or receive a move from the server and show it
			doTurn();
		}).start();	
	}
	
	/**
	 * Take a turn 
	 * check for winner, check for tie and do corresponding effects
	 * Based on the current player we either take a turn and send show and send our move or
	 * we receive the server players Move and show it.
	 * Swap players at the end and call for another turn 
	 * Whichever player's turn it is will have the top layer of circles dashed to indicate it is their turn and which circles are available to drop a disc
	 */
	public static void doTurn()
	{
		if(connectFour.isWinner()) 
		{
			//do winner stuff
			connectFour.swapPlayer();
			CommonFX.setBottomText(connectFour.getCurrentPlayer().getName()+" wins!!!");
			CommonFX.flashText();
			Platform.runLater(() -> CommonFX.highlightWinnerCircles(connectFour.getWinnerCoords()));
			
			System.out.println("Client: Winner");
		}
		else
		{
			System.out.println("Client: Checking if tie");
			if(connectFour.isTie())
			{
				//do tie stuff
				CommonFX.setBottomText("It's a draw");
				System.out.println("Client: Tie");
			}
			else
			{
				//no win or tie, do the turn
				CommonFX.setBottomText(connectFour.getCurrentPlayer().getName()+" turn");
				
				//current player 0 means its the servers turn
				if(connectFour.getCurrentPlayerID() == 1) 
				{
						System.out.println("Client: Doing turn. Player is 1, add mouse event.");
						CommonFX.enable();
						addMouseEvent();
				}
				//current player 1 means its the clients turn
				else
				{
					System.out.println("Client: Doing turn. Player is 0, waiting for server.");
				
					//receive a move
					Move obRec = receiveMove();
				
					//show the move
					CommonFX.showTurn(connectFour.getCurrentPlayer(), obRec);
					
					//store results here - used to check for winner
					connectFour.storeMove(obRec);
					
					//swap the current player/ whose turn it is
					connectFour.swapPlayer();
					//Platform.runLater(() -> obPane.setBottom(CommonFX.getBottom(connectFour.getCurrentPlayer().getName()  + "'s turn")));
					
					doTurn();
					
				}
			}
		}
	}
	
	/**
	 * If it is the client players turn the player can click on a circle to trigger a mouse event which will
	 * drop a disc down to the lowest available spot in the grid
	 * We swap player turns at the end
	 * Here we make sure to disable the top layer of circles so the player cannot take more than one turn - drop more than one disc
	 * show the disk dropping
	 */
	public static void addMouseEvent() 
	{

		for(int i = 0; i < connectFour.getCols(); i++) 
		{
			final int nCol = i;	
			
		//disable the circle associated to a particular column once the column is full of discs
			if(CommonFX.getTracker()[nCol] < 0)
			{
				CommonFX.disable(nCol);
			}
			else
			{
				CommonFX.getCircles()[0][nCol].setOnMouseClicked(e-> 
				{	
					new Thread(()->{
						CommonFX.disable();
						
						Move obMove = new Move(nCol, CommonFX.getTracker()[nCol]);
						
						CommonFX.showTurn(connectFour.getCurrentPlayer(), obMove);
						connectFour.storeMove(obMove);
						sendMove(obMove);
						connectFour.swapPlayer();
						
						doTurn();
					}).start();
					
				});
			}
		}
	}

	/**
	 * Start the connection with the server
	 * We use the HOST name from the client splash screen and appropriate port number to
	 * establish a socket with the server
	 */
	public void initConnection()
	{
		try
		{
			obSocket = new Socket(HOST,CLIENT_PORT);
			
			System.out.println("Client: Connection with server established");
		} 
		catch (UnknownHostException  e)
		{
			e.printStackTrace();
			System.exit(-1);
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	/**
	 * This method sends the Move object to the server over the socket
	 * @param obToServer
	 */
	public static void sendMove(Move obToServer)
	{
		try
		{
			obOut = new ObjectOutputStream(obSocket.getOutputStream());
			obOut.writeObject(obToServer);
			System.out.printf("Client: Sending move: %s\n",obToServer.toString());
		} 
		catch (IOException e)
		{
			
			e.printStackTrace();
		}
	}
	
	/**
	 * This method recieves a Move object from the server over the socket
	 * @return
	 */
	public static Move receiveMove()
	{
		try
		{
			System.out.println("Client: Waiting for move from server.");
			obIn = new ObjectInputStream(obSocket.getInputStream());
			
			Move obFromServer = (Move) obIn.readObject();
			System.out.printf("Client: Receiving move: %s\n",obFromServer.toString());
			
			//obIn.close();
			return obFromServer;
		} 
		catch (IOException e)
		{
			
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e)
		{
			//object returned was not a Move
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * This method receives the game object from the server. Which will allows us to use the associated game methods on the client side
	 * @return
	 */
	public Game receiveGame()
	{
		try
		{
			System.out.println("Client: Waiting for game from server.");
			obIn = new ObjectInputStream(obSocket.getInputStream());
			
			Game obFromServer = (Game) obIn.readObject();
			System.out.printf("Client: Receiving game with first player: %d\n",obFromServer.getCurrentPlayerID());
			
	
			return obFromServer;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Using the information from the splash screen we created a new player using setClientPlayer
	 * This player will be sent to the server so the client player can be added to the game. 
	 * @param obToClient
	 */
	public static void sendPlayer(Player obToClient)
	{
		try
		{
			obOut = new ObjectOutputStream(obSocket.getOutputStream());
			obOut.writeObject(obToClient);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}
	
	/**
	 * Setting the clientPlayer object (creating the client player object).
	 * Based on the user input from the client splash screen.
	 * @param text
	 */
	public void setClientPlayer(String sColor, String sName)
	{
		clientPlayer = new Player(sColor, sName);	
	}
	
	/**
	 * Setting the Host name.
	 * Based on the user input from the client splash screen.
	 * @param text
	 */
	public void setHostName(String text)
	{
		HOST = text;
	}

	public static void main(String[] args) 
	{
		Application.launch(args);
	}

}

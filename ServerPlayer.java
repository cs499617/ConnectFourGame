package Game;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * This is the class for the Server Player
 * We will send game moves back and forth between the server and client in order to
 * play a game of connect-four over a socket. 
 *
 */
public class ServerPlayer extends Application
{
	private static final int SERVER_PORT = 19122;
	
	private static ObjectOutputStream obOut;
	private static ObjectInputStream obIn;
	
	private static Socket obSocket;
	
	private static Game connectFour;
	private static Player serverPlayer;
	private static Player clientPlayer;
	
	public BorderPane obBorPaneServer;
	public BorderPane waitingPaneServer;

	public static Stage obReplayStage = new Stage();
	public Stage obMainStage = new Stage();
	public Stage obSplashStage = new Stage();
	
	private static MyCon dbCon = new MyCon();

	
	@Override
	public void start(Stage obWaitingStage) throws Exception
	{	
		showWaitingScreen(obWaitingStage);
	
		new Thread(()->{
			startServer();
			
			//Receive client player object
			clientPlayer = receivePlayer();
		
			Platform.runLater(()->{
				obWaitingStage.hide();
				showSplashScreen(obSplashStage,clientPlayer);
			});
		}).start();
	}
	
	
	/**
	 * This method represents the start of a new game
	 * we make the new game object with the server and client Player
	 * send this game to the client
	 * and call doTurn()
	 */
	public void startGame() 
	{
		new Thread(()->{
			showMainScreen(obMainStage);
		
			//set player ID's retrieved from database
			//create player if player does not exist in database
			serverPlayer.setID(MyCon.getPlayerID(serverPlayer));
			clientPlayer.setID(MyCon.getPlayerID(clientPlayer));
						
			//make new game object
			connectFour = new Game(serverPlayer, clientPlayer);
		
			//create game in database
			connectFour.setID(MyCon.createGame(serverPlayer, clientPlayer));
	
			//send the Game object to the client
			sendGame(connectFour);
			
			doTurn();
			
		}).start();
	}
	
	/**
	 * The main game screen for server player connect four game
	 * set up the board using setBoard from the commonFX class
	 * @param obStage
	 */
	public void showMainScreen(Stage obStage)
	{
		Platform.runLater(()->{
			obSplashStage.hide();
			obBorPaneServer = CommonFX.setBoard("Starting Game");
			obStage.setScene(new Scene(obBorPaneServer, 600, 600));
			obStage.setTitle("Connect-Four: ");
			obStage.show();
		});
	}
	
	/**
	 * This is just a waiting splash screen
	 * Once the server is run, this screen is displayed until the client connects
	 * @param obStage
	 */
	public void showWaitingScreen(Stage obStage)
	{
		Platform.runLater(()->{
			waitingPaneServer = CommonFX.setWait("Waiting for client to connect.");
			obStage.setScene(new Scene(waitingPaneServer, 700, 300));
			obStage.show();
		});
	}
	
	/**
	 * This is the splash screen for the server where the server player will enter the required information
	 * Here we require the client player color chosen that we can eliminate that as an option for the server as
	 * we would not want both playing with the same color. 
	 * @param obStage
	 * @param clientPlayer
	 */
	public void showSplashScreen(Stage obStage, Player clientPlayer)
	{
		Platform.runLater(()->{
			obStage.setScene(new Scene(new WelcomeScreenServ(this,obStage,clientPlayer.getColor()), 500, 300));
			obStage.show();
		});
	}
	
	/**
	 * Setting/creating the server player from the information obtained through the server splash screen from the server user
	 * @param sColor
	 * @param sName
	 */
	public void setServerPlayer(String sColor, String sName) 
	{
		serverPlayer = new Player(sColor, sName);
	}
	
	/**
	 * This method is what performs each move. If it is the server's turn the ID playerID is 0 and
	 * at this point the addmouseevent is called to allow the player to make a move. Otherwise it is
	 * the clients turn and we receive their move and show it on our display. 
	 * We will swap players at the end of each turn
	 * each move that is shown we will check for a winning connect four or tie and do the winner stuff (including
	 * change color of disks that connect, making them pulse, and flashing text of winner's name)/tie stuff 
	 */
	public static void doTurn()
	{

		if(connectFour.isWinner()) 
		{
			connectFour.swapPlayer();
			
			//winner stuff that happens once a player wins
			CommonFX.setBottomText(connectFour.getCurrentPlayer().getName()+" wins!!!");
			CommonFX.highlightWinnerCircles(connectFour.getWinnerCoords());
			CommonFX.flashText();

			//update database for number of games played for each player
			//game is not recorded as played in the database until there is a winner
			dbCon.incrementGamesPlayed(clientPlayer);
			dbCon.incrementGamesPlayed(serverPlayer);
		
			//update number of wins to reflect who won the current game
			dbCon.incrementGamesWon(connectFour.getCurrentPlayer());
		}
		else
		{
			System.out.println("Server: Checking if tie");
//			if(connectFour.isTie())
//			{
//				//do tie stuff
//				//call method to store and display tie
//				CommonFX.setBottomText("It's a draw");
//				System.out.println("Server: Tie");
//			}
//			else
//			{
				//no winner or tie, do a turn
				//CommonFX.replay.setDisable(true);
				CommonFX.setBottomText(connectFour.getCurrentPlayer().getName()+" turn");
				
				//current player 0 means its the servers turn
				if(connectFour.getCurrentPlayerID() == 0) 
				{
						System.out.println("Server: Doing turn. Player is 0, add mouse event.");
						//enable top circles for the server player
						long timer = System.currentTimeMillis();
						long dur = System.currentTimeMillis() - 400;
						if(timer < dur);
						{
							CommonFX.enable();
							
						}
						timer = System.currentTimeMillis();
						dur = System.currentTimeMillis() - 400;
						
						//when circle is clicked by server event occurs
						addMouseEvent();
				}
				//current player 1 means its the clients turn
				else
				{
					System.out.println("Server: Doing turn. Player is 1, waiting for client.");
					//receive a move
					Move obRec = receiveMove();
				
					CommonFX.showTurn(connectFour.getCurrentPlayer(), obRec);
					
					//store client move in database
					MyCon.createMove(obRec, clientPlayer, connectFour);
					
					//store results here - used to check for a winner. 
					connectFour.storeMove(obRec);
	
					//switch player turn
					connectFour.swapPlayer();
	
					doTurn();
				}
				
			}
		}
		
//	}

	/**
	 * Allows the top row of circles to be clicked on. Once a circle is clicked on they are disabled so you could not take 2 turns.
	 * we show the move on the server display and send the move to the client so they can display it there. 
	 */
	public static void addMouseEvent() 
	{
		for(int i = 0; i < connectFour.getCols(); i++) 
		{
			final int nCol = i;	
			
			if(CommonFX.getTracker()[nCol] < 0) {
				CommonFX.disable(nCol);
			}
			else
			{	
				CommonFX.getCircles()[0][nCol].setOnMouseClicked(e-> 
				{
					new Thread(()->{
						//once a circle is clicked disable the circles so they cant take another turn
						CommonFX.disable();
					
						//create new move corresponding to row and column that server player chose
						Move obMove = new Move(nCol, CommonFX.getTracker()[nCol]);
						
						//store server move in database
						MyCon.createMove(obMove, serverPlayer, connectFour);
						
						//JavaFX to show the move happening - disk dropping
						CommonFX.showTurn(connectFour.getCurrentPlayer(), obMove);
						
						//store moves in game array
						connectFour.storeMove(obMove);
			
						//send the move to the client player so they can show it on their side
						sendMove(obMove);
						
						//change whose turn it is
						connectFour.swapPlayer();
					
						//call for new turn
						doTurn();
					
					}).start();
				});
			}	
		}
	}
	
	/**
	 * This is the server socket method that will wait to hear from the client and start a connection
	 */
	public void startServer()
	{
		ServerSocket obServer;
		try
		{
			obServer = new ServerSocket(SERVER_PORT);
			System.out.println("Server: Listening on port "+SERVER_PORT);

			obSocket = obServer.accept();
			System.out.println("Server: Client connected");		
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}
	
	/**
	 * This method allows us to send our move to the client so they can show it on thier side. 
	 * @param obToClient
	 */
	public static void sendMove(Move obToClient)
	{
		try
		{
			obOut = new ObjectOutputStream(obSocket.getOutputStream());
			obOut.writeObject(obToClient);
			System.out.printf("Server: Sending move: %s\n",obToClient.toString());
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}

	/**
	 * This method allows us to receive a move from the client so we can show it on our side
	 * @return
	 */
	public static Move receiveMove()
	{
		try
		{
			System.out.println("Server: Waiting for move from client.");
			obIn = new ObjectInputStream(obSocket.getInputStream());
			
			Move obFromClient = (Move) obIn.readObject();
			System.out.printf("Server: Receiving move: %s\n",obFromClient.toString());
			
			return obFromClient;
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
	 * Once we set up the Game object we can send it to the client to start the game
	 * @param obToClient
	 */
	public void sendGame(Game obToClient)
	{
		try
		{
			obOut = new ObjectOutputStream(obSocket.getOutputStream());
			obOut.writeObject(obToClient);
			System.out.printf("Server: Sending game with first player of: %d\n",obToClient.getCurrentPlayerID());
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}

	/**
	 * This method allows us to receive the Client Player object so that we can add it to the game and then 
	 * send the game back to the client. 
	 * @return
	 */
	public static Player receivePlayer()
	{
		try
		{
			obIn = new ObjectInputStream(obSocket.getInputStream());
			Player obFromClient = (Player) obIn.readObject();
			
			return obFromClient;
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
	
	public static void main(String[] args) 
	{
		Application.launch(args);
	}

}

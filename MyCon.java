package Game;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * This class contains all methods associated with updating and retrieving data from the database
 *
 */
public class MyCon 
{
	private final boolean TEMP = true;
	
	
	private final String JDBC = "jdbc:mysql://";
	private final String CST_HOST = "10.36.107.3";
	private final String CST_USER = "cst137";
	private final String CST_PASSWORD = "Cosc1902023%";
	private final String CST_DB = "cst137";;
	private static Connection dbCon;
	private static Statement dbStatement;
	
	//credentials for database we set up for testing while the cst database was not available
	private final String TEMP_HOST = "192.185.156.196";
	private final String TEMP_USER = "triton_cst137";
	private final String TEMP_PASSWORD = "Cosc1902023%";
	private final String TEMP_DB = "triton_cst137";

	public MyCon()
	{
		connectDB();
		installDB();
	}
	
	/**
	 * Connect to the database; called in the constructor of this class
	 * Depending on how temp boolean is set we connect to testing or CST database
	 */
	public void connectDB() 
	{	
		try { 
			if(TEMP) 
			{
				dbCon = DriverManager.getConnection(JDBC+TEMP_HOST+"/"+TEMP_DB,TEMP_USER,TEMP_PASSWORD);
				
			}
			else
			{
				dbCon = DriverManager.getConnection(JDBC+CST_HOST+"/"+CST_DB,CST_USER,CST_PASSWORD);
			}
			
			dbStatement = dbCon.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		} catch (SQLException e) {
			//used to see if connection error was occuring
			if(TEMP) 
			{
				System.out.println("Error connecting to: "+JDBC+TEMP_HOST+"/"+TEMP_DB);
			}
			else
			{
				System.out.println("Error connecting to: "+JDBC+CST_HOST+"/"+CST_DB);
			}
			e.printStackTrace();
		}
	}
	
	//install function called after db connect
	//will create tables that don't exist that connect four requires
	//this was created since CST database was down, so we needed to create a testing environment
	//and do not want to remake the tables again
	private static void installDB()
	{
		
		String sqlStatement;
		
		//create `game` table with statement generated by DBeaver
		try {
			sqlStatement = "CREATE TABLE IF NOT EXISTS `game` ("
					+ "  `id` mediumint(9) NOT NULL AUTO_INCREMENT,"
					+ "  `player1` tinyint(4) DEFAULT NULL,"
					+ "  `player2` tinyint(4) DEFAULT NULL,"
					+ "  PRIMARY KEY (`id`)"
					+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;";
			dbStatement.executeUpdate(sqlStatement);
		} 
		catch (SQLException e) 
		{
			System.out.println("Database: Error creating `game` table");
			e.printStackTrace();
		}
		try 
		{
			sqlStatement = "CREATE TABLE IF NOT EXISTS `player` ("
					+ "  `id` mediumint(9) NOT NULL AUTO_INCREMENT,"
					+ "  `playerName` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,"
					+ "  `gamesPlayed` tinyint(4) DEFAULT '0',"
					+ "  `gamesWon` mediumint(9) DEFAULT '0',"
					+ "  PRIMARY KEY (`id`)"
					+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;";
			dbStatement.executeUpdate(sqlStatement);
		}
		catch (SQLException e)
		{
			System.out.println("Database: Error creating `player` table");
			e.printStackTrace();
		}
		
		//create `move` table with statement generated by DBeaver
		try {
			sqlStatement = "CREATE TABLE IF NOT EXISTS `moves` ("
					+ "  `id` mediumint(9) NOT NULL AUTO_INCREMENT,"
					+ "  `row` tinyint(4) DEFAULT NULL,"
					+ "  `column` tinyint(4) DEFAULT NULL,"
					+ "  `playerID` tinyint(4) DEFAULT NULL,"
					+ "  `gameID` mediumint(9) DEFAULT NULL,"
					+ "  PRIMARY KEY (`id`)"
					+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;";
			dbStatement.executeUpdate(sqlStatement);
		} 
		catch (SQLException e)
		{
			System.out.println("Database: Error creating `player` table");
			e.printStackTrace();
		}
	}
	/**
	 * This method will return the player ID for either an existing player or use the createPlayer method
	 * to create a new player in the database and return the ID for the new player
	 * @param obPlayer
	 * @return
	 */
	public static int getPlayerID(Player obPlayer)
	{
		
		try {
			String sqlStatement = "SELECT id FROM player WHERE playerName = ?";
			PreparedStatement obPS = dbCon.prepareStatement(sqlStatement);
			obPS.setString(1, obPlayer.getName());
			
			ResultSet results = obPS.executeQuery();
			
			if(results.next())
			{
				//player found
				System.out.println("id is " + results.getInt("id"));
				return results.getInt("id");
			}
			else
			{
				//no player found
				return createPlayer(obPlayer);
			}	
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * this method will create the player in the database in the case that they are not already there. 
	 * We do not want duplicate players in the database that represent the same player. 
	 * @param obPlayer
	 * @return
	 */
	private static int createPlayer(Player obPlayer)
	{
		int nVal = 0;
		
		try {
			String sqlStatement = "INSERT INTO player (playerName) VALUES (?)";
			PreparedStatement obPS = dbCon.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
			obPS.setString(1, obPlayer.getName());   
			
			obPS.executeUpdate();
			
			ResultSet results = obPS.getGeneratedKeys();
			if (results.next()) {
			    nVal = results.getInt(1);
			}
			
			return  nVal;
			
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return nVal;
	}
	
	/**
	 * This method will insert and entry into the game table when a new game starts.
	 * The game database contains the IDs of both players 
	 * @param obPlayer1
	 * @param obPlayer2
	 * @return
	 */
	public static int createGame(Player obPlayer1, Player obPlayer2)
	{
		int nVal = 0;
		
		try {
			String sqlStatement = "INSERT INTO game (player1, player2) VALUES (?,?)";
			PreparedStatement obPS = dbCon.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
			obPS.setInt(1, obPlayer1.getID());   
			obPS.setInt(2, obPlayer2.getID()); 
			
			obPS.executeUpdate();
			
			ResultSet results = obPS.getGeneratedKeys();
			if (results.next())
			{
			    nVal = results.getInt(1);
			}
			
			return  nVal;
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return nVal;
	}
	
	/**
	 * This method will insert a move into the database
	 * each time a player makes a move we can call this method to record that move in the moves table in the database
	 * @param obMove
	 * @param obPlayer
	 * @param obGame
	 * @return
	 */
	public static int createMove(Move obMove, Player obPlayer, Game obGame)
	{
		int nVal = 0;
		
		try {
			String sqlStatement = "INSERT INTO moves (moves.row, moves.column, gameID, playerID) VALUES (?,?,?,?)";
			PreparedStatement obPS = dbCon.prepareStatement(sqlStatement);
			obPS.setInt(1, obMove.getRow());   
			obPS.setInt(2, obMove.getCol());  
			obPS.setInt(3, obGame.getID()); 
			obPS.setInt(4, obPlayer.getID());
			
			nVal = obPS.executeUpdate();
			
			return nVal;
		} 
		catch (SQLException e)
		{
			
			e.printStackTrace();
		}
		
		return nVal;
	}
	
	/**
	 * will add one to the games played for each player in the database
	 * this isn't really used for anything at the moment but was intended to be used to further the accuracy of the leaderboard
	 * @param obPlayer
	 */
	public void incrementGamesPlayed(Player obPlayer)
	{
		int nGamesPlayed = getGamesPlayed(obPlayer) + 1;
		
		try 
		{
			String sqlStatement = "UPDATE player SET gamesPlayed = ? WHERE id = ?";
			PreparedStatement obPS = dbCon.prepareStatement(sqlStatement);
			obPS.setInt(1, nGamesPlayed);   
			obPS.setInt(2, obPlayer.getID()); 
			
			System.out.println("Database: Increment player " + obPlayer.getID() + " to have " + nGamesPlayed + " games played.");
			
			obPS.executeUpdate();
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Which ever player won the game (once there is a winner) we update the player's games won by one
	 * @param obPlayerWinner
	 */
	public void incrementGamesWon(Player obPlayerWinner)
	{
		int nGamesWon = getGamesWon(obPlayerWinner) + 1;
		
		try 
		{
			String sqlStatement = "UPDATE player SET gamesWon = ? WHERE id = ?";
			PreparedStatement obPS = dbCon.prepareStatement(sqlStatement);
			obPS.setInt(1, nGamesWon);   
			obPS.setInt(2, obPlayerWinner.getID());
			
			System.out.println("Database: Increment player " + obPlayerWinner.getID() + " to have " + nGamesWon + " wins.");
			
			obPS.executeUpdate();
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * This gets the games played so it can be incremented by one above in the incrementGamesPlayed()
	 * @param obPlayer
	 * @return
	 */
	private static int getGamesPlayed(Player obPlayer) 
	{
		int nVal = 0;
		
		try 
		{
			String sqlStatement = "SELECT gamesPlayed FROM player WHERE id = ?";
			PreparedStatement obPS = dbCon.prepareStatement(sqlStatement);
			obPS.setInt(1, obPlayer.getID());
			
			ResultSet results = obPS.executeQuery();
			
			if(results.next())
			{
				//player found
				nVal = results.getInt("gamesPlayed");
				return nVal;
			}
		} 
		catch (SQLException e) 
		{
			
			e.printStackTrace();
		}
		
		return nVal;
	}
	

	/**
	 * This method returns the number of games won for a particular player
	 * used in the above method incrementGamesWon to increase the wins for the winner of each game
	 * @param obPlayer
	 * @return
	 */
	private static int getGamesWon(Player obPlayer) 
	{
		int nVal = 0;
		
		try
		{
			String sqlStatement = "SELECT gamesWon FROM player WHERE id = ?";
			PreparedStatement obPS = dbCon.prepareStatement(sqlStatement);
			obPS.setInt(1, obPlayer.getID());
			
			ResultSet results = obPS.executeQuery();
			
			if(results.next())
			{
				nVal = results.getInt("gamesWon");
				return nVal;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return nVal;
	}
	
	/**
	 * This will retrieve the top 10 players from the database in terms of wins
	 * This info will be used for the leaderboard which is shown when the leaderboard
	 * button is clicked on the splash screens 
	 * @return
	 */
	public static List<String[]> getLeaderBoardInfo() 
	{
		List<String[]> listLeader = new ArrayList<>();
		ResultSet results;
		
		try 
		{
			String sqlStatement = "SELECT * FROM player ORDER BY gamesWon DESC LIMIT 10";
			
			results = dbStatement.executeQuery(sqlStatement);
			while(results.next())
			{
				String[] eachPlayer = new String[2];
				eachPlayer[0] = results.getString("playerName");
				eachPlayer[1] = "" + results.getInt("gamesWon");
				listLeader.add(eachPlayer);
			}
		} 
		catch (SQLException e) 
		{	
			e.printStackTrace();
		}
		
		return listLeader;
	}
	

	/**
	 * Get the moves for a particular gameID and return them in a list of all the Moves that occurred in that game
	 * for the replay the last game button we pass in the gameID of the last game which we retrieve from the database below. 
	 * @param gameID
	 * @return
	 */
	public static List<Move> getMoves(int gameID) 
	{
		
		String sqlStatement = "SELECT * FROM moves WHERE gameID = ?";
		
		List<Move> listMoves = new ArrayList<>();
		ResultSet resultsMoves;
		
		try 
		{
			
			PreparedStatement obPS = dbCon.prepareStatement(sqlStatement);
			obPS.setInt(1, gameID);
			resultsMoves = obPS.executeQuery();
			while(resultsMoves.next())
			{
				listMoves.add(new Move(resultsMoves.getInt("column"), resultsMoves.getInt("row")));
			}
			
		} 
		catch (SQLException e) 
		{	
			e.printStackTrace();
		}
		
		return listMoves;
	}
	
	/**
	 * Select and return the ID of the last game that was played so we can get the Moves associated with that game to replay those moves.
	 * @return
	 */
	public static int getLastGameID() 
	{
		
		String sqlStatement = "SELECT * FROM game ORDER BY id DESC LIMIT 1";
		ResultSet results;
		int nVal = 0;
		try 
		{
		results = dbStatement.executeQuery(sqlStatement);
		
		while(results.next())
		{
			nVal = results.getInt("id");
		}
	} 
	catch (SQLException e) 
	{	
		e.printStackTrace();
	}
	
	return nVal;
	}

}



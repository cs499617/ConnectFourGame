package Game;

import java.io.Serializable;

import javafx.scene.paint.Color;


/**
 * This class allows us to create new players. 
 * Each game needs 2 players - server and client player
 * each player consists of a name and color 
 * Here we have getters and setters for player info. 
 *
 */
public class Player implements Serializable 
{

	private static final long serialVersionUID = 1L;
	private String color;
	private String name;
	private int playerID;
		
	public Player() 
	{
		
	}

	public Player(String sColor, String sName) 
	{
		this.color = sColor;
		this.name = sName;
	}

	public String getColor()
	{
		return color;
	}

	public String getName() 
	{
		return name;
	}
	public int getID() 
	{
		return playerID;
	}
	
	public void setID(int nVal) 
	{
		playerID = nVal;
	}

}

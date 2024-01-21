package Game;

import java.io.Serializable;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This class is used to create Move objects which simply consist of a row and column which corresponds to the position in the board where that "move" went
 * Getters and setters for row/columns are included for when we need that information
 *
 */
public class Move implements Serializable
{

	private static final long serialVersionUID = 1L;
	private int column;
	private int row; 
	
	public Move() 
	{
		
	}
	
	public Move(int column, int row) 
	{
		this.column = column;
		this.row = row;
	}
	
	public int getCol() 
	{
		return this.column;
	}
	public int getRow() 
	{
		return this.row;
	}

	public void setCol(int nCol) 
	{
		column = nCol;
	}
	
	public void setRow(int nRow) 
	{
		row = nRow;
	}
	
	public String toString() 
	{
		return String.format("" + this.column);
	}

}
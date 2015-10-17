package com.gameproject.graphicstest;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//
// this class handles the maze object
// 

public class Maze
{
	
	
	private final static int TILE_SIZE = 20;
	
	
	public static final int mazeWidth = 20;
	public static final int mazeHeight = 20;
	
	public final static int MAX_LEVELS = 10;
	
	public final static int PATH_TILE = 0;
	public final static int EXIT_TILE = 1;
	
	public static int xOffset, yOffset;
			
	//multidimensional array of MazeCells
	public static MazeCell[][] MazeCells = new MazeCell[mazeWidth][mazeHeight];
	

	public Maze()
	{
		
		
		
		for (int x = 0; x < mazeWidth; x++)
			for (int z = 0; z < mazeHeight; z++)
				MazeCells[x][z] = new MazeCell();
		
		GenerateMaze();
		
		
		xOffset = 20;
		yOffset = 50;
	}
	
		
	public void init()
	{
		
	}
	
	
	 public void GenerateMaze()
     {
         for (int x = 0; x < mazeWidth; x++)
             for (int z = 0; z < mazeHeight; z++)
             {
                 MazeCells[x][z].Walls[0] = true;
                 MazeCells[x][z].Walls[1] = true;
                 MazeCells[x][z].Walls[2] = true;
                 MazeCells[x][z].Walls[3] = true;
                 MazeCells[x][z].Visited = false;
                 
                 
             }
         MazeCells[0][0].Visited = true;
         EvaluateCell(new Vector2(0, 0));
         


     }
	 
     private static void EvaluateCell(Vector2 cell)
     {
         List<Integer> neighborCells = new ArrayList<Integer>();
         neighborCells.add(0);
         neighborCells.add(1);
         neighborCells.add(2);
         neighborCells.add(3);
         
         
         while (!neighborCells.isEmpty())
         {
     	 
        	 // pick a random neighbor cell 
             int pick = (int) (Math.random()*neighborCells.size());
             int selectedNeighbor = neighborCells.get(pick);
             neighborCells.remove(pick);
             
             Vector2 neighbor = new Vector2(cell.X, cell.Y);

             switch (selectedNeighbor)
             {
                 case 0: neighbor.add(new Vector2(0, -1));
                         break;
                 case 1: neighbor.add(new Vector2(1, 0));
                         break;
                 case 2: neighbor.add(new Vector2(0, 1));
                         break;
                 case 3: neighbor.add(new Vector2(-1, 0));
                         break;

             }
             
             if ((neighbor.X >= 0) &&
                  (neighbor.X < mazeWidth) &&
                  (neighbor.Y >= 0) &&
                  (neighbor.Y < mazeHeight))
             {
                 if (!MazeCells[(int)neighbor.X][ (int)neighbor.Y].Visited)
                 {
                     MazeCells[(int)neighbor.X][(int)neighbor.Y].Visited = true;
                     MazeCells[(int)cell.X][(int)cell.Y].Walls[selectedNeighbor] = false;
                     MazeCells[(int)neighbor.X][ (int)neighbor.Y].Walls[(selectedNeighbor + 2) % 4] = false;
                     
                     EvaluateCell(neighbor);
                 }
             }

         }

     }
	
	public void draw(Graphics g)
	{
 		int i = 0;
		
		 for (int x = 0; x < mazeWidth; x++)
		 {
			 i++;
			 
			 
             for (int z = 0; z < mazeHeight; z++)
             {
            	 int startX = x*TILE_SIZE+1 + xOffset;
    			 int stopX = startX + TILE_SIZE-1;
            	 int startY = z*TILE_SIZE+1 + yOffset;
            	 int stopY = startY + TILE_SIZE-1;
            	 
            	 if ((i % 2) == 0)
            		 g.setColor(Color.WHITE);
            	 else
            		 g.setColor(Color.BLACK);
            	 
    
            	 i++;
            	 
            	 
            	 if (MazeCells[x][z].Walls[0])
            	 {
            		 g.setColor(Color.red);
            		 g.drawLine(startX, startY, stopX, startY);
            	 }
            	 
            	 if (MazeCells[x][z].Walls[1])
            	 {
            		 g.setColor(Color.blue);
            		 g.drawLine(stopX, startY, stopX, stopY);
            	 }
            	 
            	 if (MazeCells[x][z].Walls[2])
            	 {
            		 g.setColor(Color.red);
            		 g.drawLine(startX, stopY, stopX, stopY);
            	 }
            	 
            	 if (MazeCells[x][z].Walls[3])
            	 {
            		 g.setColor(Color.blue);
            		 g.drawLine(startX, startY, startX, stopY);
            	 }
            	 g.setColor(Color.white);
            	 
             }
		
		 }
	
	}
	

	
}

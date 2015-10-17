package com.gameproject.graphicstest;


import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Locale;

import javax.swing.JPanel;

//
// this class makes a panel .. like the wormchase panel
//


public class TestPanel extends JPanel implements Runnable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static long MAX_STATS_INTERVAL = 1000000000L;  //this is 1 sec
	private static final int NO_DELAYS_PER_YIELD = 16;
	private static int MAX_FRAME_SKIPS = 5;
	private static final int NUM_FPS = 10;					//

	private int pWidth, pHeight;

	//used for gathering statistics
	private long statsInterval = 0L;
	private long prevStatsTime;
	private long totalElapsedTime = 0L;
	private long gameStartTime;
	private int timeSpentInGame = 0;

	private long frameCount = 0;				//total number of rendered frames (run calls)
	private double[] fpsStore;					//array used to calculate average fps
	private double[] upsStore;					//array used to calculate average ups

	private long statsCount = 0;
	private double averageFPS = 0.0;
	private long framesSkipped = 0L;
	private long totalFramesSkipped = 0L;
	private double averageUPS = 0.0;	

	private DecimalFormat df = new DecimalFormat("0.##");			//two decimals
	private DecimalFormat timedf = new DecimalFormat("0.####");		//four decimals

	private Locale locale;

	private Thread animator; // for the animation
	private volatile boolean running = false; // stops the animation
	private volatile boolean isPaused = false; // pauses the animation
	private volatile boolean gameOver = false; // for game termination

	private long period;

	// more variables, explained later


	private MainWindow mwTop;
	private Maze mMaze;
	//private Obstacles obs;

	//used at game termination
	private int score = 0;

	private Font font;
	private FontMetrics metrics;

	// global variables for off-screen rendering
	private Graphics dbg;
	private Image dbImage = null;
	
	
	
	public TestPanel(MainWindow mw, long period, int w, int h)
	{
		mwTop = mw;
		pWidth = w;
		pHeight = h;
		
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(pWidth,pHeight));
		
		locale = Locale.UK;
		
		setFocusable(true);
		requestFocus();
		readyForTermination();
		
		//create game objects
		
		mMaze = new Maze();
		//mMaze.init();
		
		gameOver = false;
		
		font = new Font("SansSerif",Font.BOLD,12);
		metrics = this.getFontMetrics(font);
		
		
	}
	
	public void addNotify()
	/* Wait for the JPanel to be added to the
	JFrame/JApplet before starting. */
	{
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}
	
	private void startGame()
	// initialise and start the thread
	{
		if (animator == null || !running) 
		{
			animator = new Thread(this);
			animator.start();
		}
	} // end of startGame()
	
	public void resumeGame()
	{
		isPaused = false;
	}
	public void pauseGame()
	{
		isPaused = true;
	}
	
	
	public void stopGame()
	{
		//called by the user to stop the game
		running = false;
	}
	
	@Override
	public void run()
	{
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;
		//Graphics g;
		
		
		//gameStartTime = J3DTimer.getValue();
		// note: J3DTimer is deprecated
		gameStartTime = System.nanoTime();
		prevStatsTime = gameStartTime;
		beforeTime = gameStartTime;
		
		running = true;
		
		while(running)
		{
			 gameUpdate(); // game state is updated
			  
			  gameRender(); // render to a buffer
			  
			  paintScreen(); //draw buffer to screen
			  
			  afterTime = System.nanoTime();
			  
			  timeDiff = afterTime - beforeTime;
			  sleepTime = (period - timeDiff) - overSleepTime;  // time left in this loop
			  
			  if (sleepTime > 0) // some time left in this cycle
			  {
				  try 
				  {
					Thread.sleep(sleepTime/10000000L); // sleep a bit nanosec-> msec
				  }
				  catch(InterruptedException ex) {}
				  overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			  }
			  else  //the frame took longer than one period
			  {
				  excess -= sleepTime;
				  overSleepTime = 0L;
				  
				  if (++noDelays >= NO_DELAYS_PER_YIELD)
				  {
					  Thread.yield();	//give way to other threads running
					  noDelays = 0;
					  
				  }
			  }
					  
			  beforeTime = System.nanoTime();
			  // if the frame animation is taking too long
			  // update the game state without rendering it 
			  //to get the updates per sec nearer to the required FPS
			  int skips = 0;
			  
			  while((excess > period) && (skips < MAX_FRAME_SKIPS) )
			  {
				  excess -= period;
				  gameUpdate();			//update but no render
				  skips++;
			  }
			  framesSkipped += skips;
			  
			  storeStats();
			}
			
			printStats();
			System.exit(0); // so enclosing JFrame/JApplet exits	
		
		
	}
	
	private void paintScreen() 
	{
		// actively render the buffer image to the screen
		
		Graphics g;
		try 
		{
			g = this.getGraphics( ); // get the panel's graphic context
			if ((g != null) && (dbImage != null))
				g.drawImage(dbImage, 0, 0, null);
			Toolkit.getDefaultToolkit( ).sync( ); // sync the display on some systems
			g.dispose( );
		}
		catch (Exception e)
		{ System.out.println("Graphics context error: " + e); }
	}

	private void gameUpdate()
	{ 
		if (!isPaused && !gameOver)
		{
			//keep updating the game...
			//fred.move();
		}
	
	}//end gameUpdate
	

	private void gameRender()
	// draw the current frame to an image buffer
	// this is where all the graphics is done
	{
		if (dbImage == null)
		{ // create the buffer
			dbImage = createImage(pWidth, pHeight);
			if (dbImage == null) 
			{
				System.out.println("dbImage is null");
				return;
			}
			else
				dbg = dbImage.getGraphics();
		}
		
		// clear the background
		dbg.setColor(Color.white);
		dbg.fillRect (0, 0, pWidth, pHeight);
		
		dbg.setColor(Color.blue);
		dbg.setFont(font);
		
		//report frame count and average FPS and UPS at top left of the screen
//		dbg.drawString("Average FPS/UPS:  " + 
//					   df.format(averageFPS) + ", " +    
//					   df.format(averageUPS),
//					    20, 25);
		dbg.drawString("Average FPS/UPS: " +
				 String.format(locale, "%4.2f",averageFPS) +
				" / " +		
				String.format(locale, "%4.2f",averageUPS), 
				20, 25);
		
		
		
		dbg.setColor(Color.black);
		// draw game elements
		// ...
	//	obs.draw(dbg);
	//	fred.draw(dbg);
		mMaze.draw(dbg);
		
		if (gameOver)
			gameOverMessage(dbg);
	} // end of gameRender( )
	
	private void gameOverMessage(Graphics g)
	// center the game-over message
	{ // code to calculate x and y...
		String msg = "Game Over. Your Score: " + score;
		int x = (pWidth - metrics.stringWidth(msg))/2;
		int y = (pHeight - metrics.getHeight())/2;
		g.setColor(Color.red);
		g.setFont(font);
		g.drawString(msg, x, y);
	} // end of gameOverMessage( )
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (dbImage != null)
			g.drawImage(dbImage, 0, 0, null);
	}
	
	
	//
	//this routine listens for key presses  
	// if the user wants to terminate 
	private void readyForTermination()
	{
		addKeyListener( new KeyAdapter()
		{
			// listen for esc, q, end, ctrl-c
			public void keyPressed(KeyEvent e)
			{ int keyCode = e.getKeyCode();
			if ((keyCode == KeyEvent.VK_ESCAPE) ||
				(keyCode == KeyEvent.VK_Q) ||
				(keyCode == KeyEvent.VK_END) ||
				((keyCode == KeyEvent.VK_C) && e.isControlDown()) ) 
				{
					running = false;
				}
			}
		});
	} // end of readyForTermination()
	
	//
	//
	//
	private void storeStats()
	{
		frameCount++;
		statsInterval += period;
		
		//record stats every MAX_STATS_INTERVAL
		if (statsInterval >= MAX_STATS_INTERVAL)
		{
			//
			long timeNow = System.nanoTime();
			timeSpentInGame = (int) ((timeNow - gameStartTime)/1000000000L);
			mwTop.setTimeSpent(timeSpentInGame);
			
			long realElapsedTime = timeNow - prevStatsTime;
			totalElapsedTime += realElapsedTime;
			
			double timingError = ((double) (realElapsedTime - statsInterval)/statsInterval) * 100.0;
			
			totalFramesSkipped += framesSkipped;
			
			double actualFPS = 0;
			double actualUPS = 0;
			if (totalElapsedTime > 0)
			{
				actualFPS = (((double) frameCount / totalElapsedTime) * 1000000000L);
				actualUPS = (((double)(frameCount + totalFramesSkipped) / totalElapsedTime) * 1000000000L);
				
			}
			
			//store the latest FPS and UPS
			fpsStore[ (int)statsCount%NUM_FPS] = actualFPS;
			upsStore[ (int)statsCount%NUM_FPS] = actualUPS;
			statsCount = statsCount + 1;
			
			double totalFPS = 0.0;
			double totalUPS = 0.0;
			for (int i = 0; i < NUM_FPS; i++)
			{
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}
			
			if (statsCount < NUM_FPS)
			{
				averageFPS = totalFPS / NUM_FPS;
				averageUPS = totalUPS / NUM_FPS;
			}
			
			// printout for debugging
			System.out.println(timedf.format((double)statsInterval/1000000000L) + " " +
							   timedf.format((double)realElapsedTime/1000000000L) + "s " + 
							   df.format(timingError) + "% " +
							   frameCount + "c " +
							   framesSkipped + "/" + totalFramesSkipped + " skip; " + 
							   df.format(actualFPS) + " " + df.format(averageFPS) + " afps; " +
							   df.format(actualUPS) + " " + df.format(actualUPS) + " aups;" 	);
			//
			
			
			framesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L;
			
			
		}
		
		
	}
	
	private void printStats()
	{
		System.out.println("Frame Count/Loss: " + frameCount + "/ " + totalFramesSkipped);
		System.out.println("Average FPS: " + df.format(averageFPS));
		System.out.println("Average FPS: " + String.format(locale,"%4.2f", averageFPS));
		System.out.println("Average UPS: " + df.format(averageUPS));
		System.out.println("Time Spent: " + timeSpentInGame + " secs");
		//System.out.println("Boxes used: " + obs.getNumObstacles());
		
	}
	
	

}

package com.gameproject.graphicstest;
//
// programma in Java om graphics te testen
//

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import javax.swing.JFrame;

public class MainWindow extends JFrame implements WindowListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_FPS = 80;

	private TestPanel tp;
	private JTextField jtfBox;
	private JTextField jtfTime;
	
	private int pWidth, pHeight;
	
	public MainWindow(long period)
	{
		super("MainWindow");
		
		makeGUI();
		
		pack();
		setResizable(false);
		calcSizes();
		setResizable(true);
		
		Container c = getContentPane();
		
		tp = new TestPanel(this,period,pWidth,pHeight);
		c.add(tp,"Center");
		pack();
		
		addWindowListener(this);
		
		addComponentListener( new ComponentAdapter()
		{
			public void componentMoved(ComponentEvent e)
			{
				setLocation(0,0);
			}
			
		});
		
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		
		setResizable(false);
		setVisible(true);
		
	}
	
	private void makeGUI()
	{
		Container c = getContentPane();
		
		JPanel ctrls = new JPanel();
		ctrls.setLayout(new BoxLayout(ctrls,BoxLayout.X_AXIS ));
		
		jtfBox = new JTextField("Boxes used: 0");
		jtfBox.setEditable(false);
		ctrls.add(jtfBox);
		
		jtfTime = new JTextField("Time Spent: 0 secs");
		jtfTime.setEditable(false);
		ctrls.add(jtfTime);
		
		c.add(ctrls,"South");
		
	}
	
	//calculate the size of the drawing panel but leaving room for 
	// title bar and textfields
	private void calcSizes()
	{
		GraphicsConfiguration gc = getGraphicsConfiguration();
		
		Rectangle screenRect = gc.getBounds();
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Insets desktopInsets = tk.getScreenInsets(gc);
		Insets frameInsets = getInsets();
		Dimension tfDim = jtfBox.getPreferredSize();
		
		pWidth = screenRect.width - (desktopInsets.left + desktopInsets.right)
								  - (frameInsets.left + frameInsets.right);
		pHeight = screenRect.height - (desktopInsets.top + desktopInsets.bottom)
									- (frameInsets.top + frameInsets.bottom)
									- tfDim.height ;
		
		
	}
	
	
	
	public static void main(String[] args)
	{
		int fps = DEFAULT_FPS;
		
		//fps = the requested frames per second
		if (args.length != 0)
		{
			fps = Integer.parseInt(args[0]);
			
		}
		//period = time period used by the timer in ms 
		long period = (long) 1000.0 / fps;
		System.out.println("fps: " + fps + "; period: " + period + "ms");
		
		//pass the time period in ns 
		new MainWindow(period*1000000L);
	}

	
	public void setBoxNumber(int no)
	{
		jtfBox.setText("Boxes used: " + no);
	}
	
	public void setTimeSpent(long t)
	{
		jtfTime.setText("Time Spent: " + t + " secs");
	}
	
	
	
	@Override
	public void windowActivated(WindowEvent arg0)
	{
		tp.resumeGame();
	}

	@Override
	public void windowClosed(WindowEvent arg0)
	{
		//DO_NOTHING_ON_CLOSE
	}

	@Override
	public void windowClosing(WindowEvent arg0)
	{
		tp.stopGame();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0)
	{
		tp.pauseGame();
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0)
	{
		tp.resumeGame();
	}

	@Override
	public void windowIconified(WindowEvent arg0)
	{
		tp.pauseGame();
	}

	@Override
	public void windowOpened(WindowEvent arg0)
	{
		// empty
	}





	public void setTimeSpent(int timeSpentInGame)
	{
		// TODO Auto-generated method stub
		
	}

}

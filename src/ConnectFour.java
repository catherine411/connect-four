//Catherine Hsu
//Description: The program will function as a game of connect four; each player is assigned a game piece (banana or strawberry) 
//and takes turn strategically placing their piece in a desired column. The first player to get four of their pieces to aligned 
//consecutively wins the game.  


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class ConnectFour extends JPanel implements ActionListener, MouseListener, KeyListener
{
	static JFrame frame;
	final int BANANA = -1;
	final int STRAWBERRY = 1;
	final int EMPTY = 0;
	final int SQUARE_SIZE = 60;
	final int TOP_OFFSET = 42;
	final int BORDER_SIZE = 4;

	Clip win, background1, background2;	

	int[] [] board;
	int currentPlayer;
	int currentColumn;
	Image firstImage, secondImage;

	Timer timer;

	// For drawing images offScreen (prevents Flicker)
	// These variables keep track of an off screen image object and
	// its corresponding graphics object
	Image offScreenImage;
	Graphics offScreenBuffer;

	boolean gameOver;

	public ConnectFour ()
	{
		//sounds (yay sound for the winner, and background music)
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File ("yay.wav"));
			win = AudioSystem.getClip();
			win.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("happy.wav"));
			background1 = AudioSystem.getClip();
			background1.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("sad.wav"));
			background2 = AudioSystem.getClip();
			background2.open(sound);
		} 
		catch (Exception e) {
		}

		// Setting the defaults for the panel
		setPreferredSize (new Dimension (7 * SQUARE_SIZE + 2 * BORDER_SIZE + 1, (6 + 1) * SQUARE_SIZE + TOP_OFFSET + BORDER_SIZE + 1));
		setLocation (100, 10);
		setBackground (new Color (200, 200, 200));
		setLayout (new BoxLayout (this, BoxLayout.PAGE_AXIS));

		board = new int [8] [9];

		// Set up the Menu
		// Set up the Game MenuItems
		// Set up Background MenuItems 
		JMenuItem newOption, exitOption, happyOption, sadOption, stopOption;
		newOption = new JMenuItem ("New");
		exitOption = new JMenuItem ("Exit");
		happyOption = new JMenuItem ("Happy");
		sadOption = new JMenuItem("Sad");
		stopOption = new JMenuItem("Stop Music");

		// Set up the Game Menu and background music menu
		JMenu gameMenu, backgroundMusicMenu;
		
		gameMenu = new JMenu ("Game");
		backgroundMusicMenu = new JMenu ("Background Music");
		// Add each MenuItem to the Game Menu (with a separator)
		gameMenu.add (newOption);
		gameMenu.addSeparator ();
		gameMenu.add (exitOption);
		
		//add each background music option to the background music menu
		backgroundMusicMenu.add(happyOption);
		backgroundMusicMenu.add(sadOption);		
		backgroundMusicMenu.addSeparator ();
		backgroundMusicMenu.add(stopOption);
		

		JMenuBar mainMenu = new JMenuBar ();
		mainMenu.add (gameMenu);
		mainMenu.add(backgroundMusicMenu);
		// Set the menu bar for this frame to mainMenu
		frame.setJMenuBar (mainMenu);

		// Use a media tracker to make sure all of the images are
		// loaded before we continue with the program
		MediaTracker tracker = new MediaTracker (this);
		firstImage = Toolkit.getDefaultToolkit ().getImage ("banana.gif");
		tracker.addImage (firstImage, 0);
		secondImage = Toolkit.getDefaultToolkit ().getImage ("strawberry.gif");
		tracker.addImage (secondImage, 1);

		//  Wait until all of the images are loaded
		try
		{
			tracker.waitForAll ();
		}
		catch (InterruptedException e)
		{
		}

		// Set up the icon image (Tracker not needed for the icon image)
		Image iconImage = Toolkit.getDefaultToolkit ().getImage ("banana.gif");
		frame.setIconImage (iconImage);

		// Start a new game and then make the window visible
		newGame ();

		newOption.setActionCommand ("New");
		newOption.addActionListener (this);
		exitOption.setActionCommand ("Exit");
		exitOption.addActionListener (this);
		happyOption.setActionCommand("Happy");
		happyOption.addActionListener(this);
		sadOption.setActionCommand("Sad");
		sadOption.addActionListener(this);
		stopOption.setActionCommand("Stop Music");
		stopOption.addActionListener(this);
		
		setFocusable (true); // Need this to set the focus to the panel in order to add the keyListener
		addKeyListener (this);

		addMouseListener (this);

	} // Constructor



	// To handle normal menu items
	public void actionPerformed (ActionEvent event)
	{
		String eventName = event.getActionCommand ();
		if (eventName.equals ("New"))
		{
			newGame ();
		}
		else if (eventName.equals ("Exit"))
		{
			System.exit (0);
		}
		else if(eventName.equals ("Happy")) {		//plays happy music if player choose a happy background music option
			background1.setFramePosition (0); 
			background1.start ();
			background2.stop();
		}
		else if(eventName.equals ("Sad")) {
			background2.setFramePosition (0); 	//plays sad music if player choose a sad background music option
			background2.start ();
			background1.stop();
		}
		else if(eventName.equals("Stop Music")) {	//stops all background music
			background1.stop();
			background2.stop();
		}

	}


	public void newGame ()
	{
		currentPlayer = BANANA;
		clearBoard (board);
		gameOver = false;
		currentColumn = 3;
		repaint ();
	}


	// This method will clear the board so that when the game starts there is nothing on the board
	// Parameters: board - the board used to play connect four
	// Returns: the board filled with 0s (an empty board)	
	public void clearBoard (int[] [] board) {
		for (int row = 1; row < board.length-1; row++) 
			for (int column = 1; column < board[row].length; column++) 
				board [row][column] = 0;
	}


	// This method will check if a game piece can be placed in the row that the player chose. If the max # of pieces is reached in that row, the method would return -1
	// Parameters: board - the board which consists of 1s, 0s, and -1s
	// 			   column - the column that the player has chosen to put their piece into
	// Returns: row -  the row the piece is going to be placed in, or -1 if there is no available space
	public int findNextRow (int[] [] board, int column) {
		int row = 0;

		for (int i = 1; i < board.length-1; i++) {
			if (board[i][column] != -1 && board[i][column] != 1) 
				row++;
			
		} 	
		if (row == 0) 
			row = -1;

		return row;
	}

	// This method will check the winner by going through every possibility of connecting four of the same pieces together
	// Parameters: board - board used to play the game  
	//			   row - last row that the a piece was placed into
	//			   column - last column that a piece was placed into
	// Returns: winner - the winner if there is a winner (-1 or 1) or 0 if there is no winner
	public int checkForWinner (int[] [] board, int lastRow, int lastColumn)   {
		int currentPlayer = board[lastRow][lastColumn];
		int checkRow1 = 0;
		int checkRow2 = 0;
		int checkColumn1 = lastColumn + 2;
		int checkColumn2 = lastColumn - 2;
		int num = 2; 	//tracks the number of rows away from lastRow
		int track1 = 2;	//tracks columns of the "first check" when direction is diagonal or vertical
		int track2 = 3; //tracks columns of the "second check" when direction is diagonal or vertical 
		int winner = 0;

		for (int direction = 1; direction <= 4; direction++) {	//checks each of the 4 directions
			if (direction < 4) { 	//when direction is 1, checks for diagonal right up; direction is 2, checks vertically; direction is 3, checks diagonal left up
				checkRow1 = lastRow-1; //row of the grid being checked first
				checkRow2 = lastRow+1; 	
				checkColumn1--;	//column of the grid being checked first
				checkColumn2++;
				num = 2;

			}
			else if (direction == 4) {	//when direction is 4, the horizontal direction is checked 
				checkRow1 = lastRow;
				checkRow2 = lastRow;
				checkColumn1 = lastColumn + 1;
				checkColumn2 = lastColumn - 1;
				track1 = 2;
				track2 = 3;
				num = 0;
			}

			if (board[checkRow1][checkColumn1] == currentPlayer && board[checkRow2][checkColumn2] == currentPlayer) {		//the 2 grids in both directions of the line are checked and if both of the grids contain the same pieces as the current player, only one more piece is needed to "connect four"
				if (board[lastRow - num][lastColumn + track1] == currentPlayer || board[lastRow + num][lastColumn - track1] == currentPlayer)  //continues checking grids in both directions of the line, if one of the two icons checked is the same as current player, current player wins 
					winner = currentPlayer;
			}

			else if (board[checkRow1][checkColumn1] == currentPlayer && board[checkRow2][checkColumn2] != currentPlayer) {		//when only one of two grids checked contains the same piece as the current player 
				if (board[lastRow - num][lastColumn + track1] == currentPlayer) {		//continues checking in one direction of the line, if two more icons in a row are the same as current player's, current player wins 
					num++;
					if (direction == 4)
						num = 0;
					if (board[lastRow - num][lastColumn + track2] == currentPlayer) 
						winner = currentPlayer;
				}
			}
			else if (board[checkRow1][checkColumn1]!= currentPlayer && board[checkRow2][checkColumn2] == currentPlayer) {
				if (board[lastRow + num][lastColumn - track1] == currentPlayer) {
					num++;
					if (direction == 4)
						num = 0;
					if (board[lastRow + num][lastColumn - track2] == currentPlayer)
						winner = currentPlayer;
				}
			}
			
			if (winner != 0) {		//plays a "yay" sound when the winner dialogue box appears
				win.setFramePosition (0); 
				win.start ();
			}

			track1=track1-2;
			track2=track2-3;

		}

		return winner;
	}

	//----------------------------------------------------//


	public void handleAction (int x, int y)
	{
		if (gameOver)
		{
			JOptionPane.showMessageDialog (this, "Please Select Game...New to start a new game",
					"Game Over", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int column = (x - BORDER_SIZE) / SQUARE_SIZE + 1;
		int row = findNextRow (board, column);
		if (row <= 0)
		{
			JOptionPane.showMessageDialog (this, "Please Select another Column",
					"Column is Full", JOptionPane.WARNING_MESSAGE);
			return;
		}

		animatePiece (currentPlayer, column, row);
		board [row] [column] = currentPlayer;

		int winner = checkForWinner (board, row, column);

		if (winner == BANANA)
		{
			gameOver = true;
			repaint ();
			JOptionPane.showMessageDialog (this, "Banana Wins!!!",
					"GAME OVER", JOptionPane.INFORMATION_MESSAGE);

		}
		else if (winner == STRAWBERRY)
		{
			gameOver = true;
			repaint ();
			JOptionPane.showMessageDialog (this, "Strawberry Wins!!!",
					"GAME OVER", JOptionPane.INFORMATION_MESSAGE);
		}
		else
			// Switch to the other player
			currentPlayer *= -1;
		currentColumn = 3;

		repaint ();
	}


	// MouseListener methods
	public void mouseClicked (MouseEvent e)
	{
		int x, y;
		x = e.getX ();
		y = e.getY ();

		handleAction (x, y);
	}


	public void mouseReleased (MouseEvent e)
	{
	}


	public void mouseEntered (MouseEvent e)
	{
	}


	public void mouseExited (MouseEvent e)
	{
	}


	public void mousePressed (MouseEvent e)
	{
	}


	//KeyListener methods
	public void keyPressed (KeyEvent kp)
	{
		if (kp.getKeyCode () == KeyEvent.VK_RIGHT)
		{
			if (currentColumn < 6)
				currentColumn++;
		}
		else if (kp.getKeyCode () == KeyEvent.VK_DOWN)
		{
			handleAction ((currentColumn) * SQUARE_SIZE + BORDER_SIZE, 0);
		}
		else if (kp.getKeyCode () == KeyEvent.VK_LEFT)
		{
			if (currentColumn > 0)
				currentColumn--;
		}
		else
			return;
		repaint ();
	}


	public void keyReleased (KeyEvent e)
	{
	}


	public void keyTyped (KeyEvent e)
	{
	}


	public void animatePiece (int player, int column, int finalRow)
	{
		Graphics g = getGraphics ();

		// Find the x and y positions for each row and column
		int xPos = (4 - 1) * SQUARE_SIZE + BORDER_SIZE;
		int yPos = TOP_OFFSET + 0 * SQUARE_SIZE;
		offScreenBuffer.clearRect (xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
		for (double row = 0 ; row < finalRow ; row += 0.10)
		{
			// Find the x and y positions for each row and column
			xPos = (column - 1) * SQUARE_SIZE + BORDER_SIZE;
			yPos = (int) (TOP_OFFSET + row * SQUARE_SIZE);
			// Redraw the grid for this column
			for (int gridRow = 1 ; gridRow <= 6 ; gridRow++)
			{
				// Draw the squares
				offScreenBuffer.setColor (Color.black);
				offScreenBuffer.drawRect ((column - 1) * SQUARE_SIZE + BORDER_SIZE,
						TOP_OFFSET + gridRow * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}

			// Draw each piece, depending on the value in board
			if (player == BANANA)
				offScreenBuffer.drawImage (firstImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);
			else if (player == STRAWBERRY)
				offScreenBuffer.drawImage (secondImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);

			// Transfer the offScreenBuffer to the screen
			g.drawImage (offScreenImage, 0, 0, this);
			delay (3);
			offScreenBuffer.clearRect (xPos + 1, yPos + 1, SQUARE_SIZE - 2, SQUARE_SIZE - 2);
		}
	}


	// Avoid flickering -- smoother graphics
	public void update (Graphics g)
	{
		paint (g);
	}


	public void paintComponent (Graphics g)
	{

		// Set up the offscreen buffer the first time paint() is called
		if (offScreenBuffer == null)
		{
			offScreenImage = createImage (this.getWidth (), this.getHeight ());
			offScreenBuffer = offScreenImage.getGraphics ();
		}

		// All of the drawing is done to an off screen buffer which is
		// then copied to the screen.  This will prevent flickering
		// Clear the offScreenBuffer first
		offScreenBuffer.clearRect (0, 0, this.getWidth (), this.getHeight ());

		// Redraw the board with current pieces
		for (int row = 1 ; row <= 6 ; row++)
			for (int column = 1 ; column <= 7 ; column++)
			{
				// Find the x and y positions for each row and column
				int xPos = (column - 1) * SQUARE_SIZE + BORDER_SIZE;
				int yPos = TOP_OFFSET + row * SQUARE_SIZE;

				// Draw the squares
				offScreenBuffer.setColor (Color.black);
				offScreenBuffer.drawRect (xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);

				// Draw each piece, depending on the value in board
				if (board [row] [column] == BANANA)
					offScreenBuffer.drawImage (firstImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);
				else if (board [row] [column] == STRAWBERRY)
					offScreenBuffer.drawImage (secondImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);
			}

		// Draw next player
		if (!gameOver)
			if (currentPlayer == BANANA)
				offScreenBuffer.drawImage (firstImage, currentColumn * SQUARE_SIZE + BORDER_SIZE, TOP_OFFSET, SQUARE_SIZE, SQUARE_SIZE, this);
			else
				offScreenBuffer.drawImage (secondImage, currentColumn * SQUARE_SIZE + BORDER_SIZE, TOP_OFFSET, SQUARE_SIZE, SQUARE_SIZE, this);

		// Transfer the offScreenBuffer to the screen
		g.drawImage (offScreenImage, 0, 0, this);
	}


	/** Purpose: To delay the given number of milliseconds
	 * @param milliSec The number of milliseconds to delay
	 */
	private void delay (int milliSec)
	{
		try
		{
			Thread.sleep (milliSec);
		}
		catch (InterruptedException e)
		{
		}
	}


	public static void main (String[] args)
	{
		frame = new JFrame ("Connect Four");
		ConnectFour myPanel = new ConnectFour ();

		frame.add (myPanel);
		frame.pack ();
		frame.setVisible (true);

	} // main method
} // ConnectFourWorking class
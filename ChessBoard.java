//Importing Packages for ChessBoard
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

//Importing Packages for the CLIENT
import java.lang.System;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.*;
import java.applet.*;
import java.awt.event.*;
import java.awt.*;
//import java.awt.event.KeyEvent;


/*The Following Applet Tag has been updated with CLIENT:Height as 150
being added to height of ChessBoard.java Applet 450 maiking total height as 600.
With width for both remaining same i.e 475.
*/


/*
<Applet code=ChessBoard width=682 height=621> 
<param name="HOST" value="127.0.0.1">
<param name="PORT" value="1234">
<param name="bpawn" value="Images/BPawn.gif">
<param name="bbishop" value="Images/BBishop.gif">
<param name="brook" value="Images/BRook.gif">
<param name="bknight" value="Images/BKnight.gif">
<param name="bking" value="Images/BKing.gif">
<param name="bqueen" value="Images/BQueen.gif">
<param name="wpawn" value="Images/WPawn.gif">
<param name="wbishop" value="Images/WBishop.gif">
<param name="wrook" value="Images/WRook.gif">
<param name="wknight" value="Images/WKnight.gif">
<param name="wking" value="Images/WKing.gif">
<param name="wqueen" value="Images/WQueen.gif">
<param name="move" value="Sounds/move.wav">
<param name="checkmate" value="Sounds/checkmate.wav">
<param name="stalemate" value="Sounds/stalemate.wav">
<param name="check" value="Sounds/check.wav">
</Applet>
*/

public class ChessBoard extends Applet
implements ActionListener, TextListener, MouseListener, MouseMotionListener, Constants

/*TextListener has been implemented to call repaint when movehistory is changed*/
/*CLIENT:uses ActionListener*/
{
	/**This variable is used to store the row of Board selected by player.
	*(or - Old Row AND r - Current Row)
	*It is used at various places in the whole project first its value is set in the mousePressed()
	*and refreshed at the end of the moveCompletion.
	*The y is converted to the row by pre-defined formula, which is calculated using formula: 
        *"7-(mouseY-30)/SIDE"
	**/
	public int or = -1,r = -1;

	/**This variable is used to store the column of Board selected by player.
	*(oc - Old Column AND c - Current Column) 
	*It is used at various places in the whole project first its value is set in the mousePressed()
	*and refreshed at the end of the moveCompletion.
	*The x is converted to the row by pre-defined formula, which is calculated using formula: 
	*"(mouseX-30)/SIDE"
	**/		
	public int oc = -1,c = -1; 
	
	/**This variable is used to store the code of the peice selected to move by the player.
	**/
	public byte piecetomove = -1;
	
	/**This variable is used to control the highlighting of the squares of the selected by player.
	**/
	public int orow = -1, ocol = -1, row = -1, col = -1;
	
	String msg="";

	/**This variable is used in the mousePressed().
	**/
	int mouseX = 0, mouseY = 0;

	/**This is a 8X8 Byte Array, for storing the Board Position. It is the Core of the ChessProject.
	*It stores Board Position by storing the code of the peice at the respective subscript position.
	**/
	public static byte Board[][] = new byte[N][N];
	
	String storerprc=""; //rprc stands for r-Ready, p-Piece, r-Row, c-Column
	boolean firstClick=false,secondClick=false;

	/**This variable is used to store the PeiceColor of the Player.
	*By default it is set to TRUE which means WHITE.
	*Once the user logs in it is set accordingly in the PortTalk class
	**/
	public static boolean PlayerColor=true;

//	public String PlayerName="";
	
	/**This variable stores the Opponent name.
	*Opponent name is passed by Server Once the server finds the Opponent for the current player.
	**/
	public String OpponentName="";

	TextField playerNameField;
	Label displayAction;
	Panel myPanel;
	static boolean playerNameFound = false;
	static boolean gameEnded = false;

	/**This variables is used for castling purpose.
	*Initially it is set to be TRUE. As only Fresh Rook can be involved in a Castling Operation.
	*Once Rook is moved it is set to be FALSE.
	**/
	public static boolean isLeftRookFresh=true;
	
	/**This variables is used for castling purpose.
	*Initially it is set to be TRUE. As only Fresh Rook can be involved in a Castling Operation.
	*Once Rook is moved it is set to be FALSE.
	**/
	public static boolean isRightRookFresh=true;

	/**This variables is used for castling purpose.
	*Initially it is set to be TRUE. As only Fresh King can be involved in a Castling Operation.
	*Once King is moved it is set to be FALSE.
	**/
	public static boolean isKingFresh=true;
	
	/**This variables is used for castling purpose.
	*To check whether castling has been performed. Initially it is set to be FALSE.
	**/
	public static boolean castlingDone=false;
	
	/**This variable controls the turn of the players.
	*In Chess two players make move alternately this is made possible by this variable.
	*Initially it is set to be FALSE.
	**/
	public static boolean startPlay=false; 

/**This variable keeps track whether the opponent has moved.
	*Initially it is set to be FALSE.
	**/
	public static boolean opponentMoved=false;
	
	/**This variable keeps track whether the current players King is under attack or not.
	*Initially it is set to be FALSE.
	*It updates this variable before each players turn.
	**/
	public static boolean CHECK=false;

	/**This variable keeps track whether the current players is Checkmated or not.
	*Initially it is set to be FALSE.
	*It updates this variable before each players turn.
	**/
	public static boolean CHECKMATE=false;
	
	/**This variable keeps track whether the current players is Stalemated or not.
	*Initially it is set to be FALSE.
	*It updates this variable before each players turn.
	*Stalemate is a draw.
	**/
	public static boolean STALEMATE=false;
     
	/**This variable keeps track of the previous move of the opponent.
	*It is used in case of an en-passant operation.
	**/
	public static String previousMoveOFOpponent="";
	/**This variable keeps track whether opponent performed en-passant or not.
	*It is set on the basis of the previousMoveOFOpponent.
	*It is used in case of an en-passant operation.
	*Initially it is set to be FALSE.
	**/
	public static boolean enpassantDone=false;
	
	/**This variable keeps track of the piece to which the opponent has promoted his pawn.
	*It is set on the basis of the previousMoveOFOpponent.
	*Initially it is set to be -1.
	**/
	public static byte peicepromotedto=-1;
	
        /* Variables used in CLIENT */
	TextField txtSend;
	String arrPortSetting[]={"127.0.0.1","1234"}; //{"A32","1234"}; when working at home and local Pc;
	String str=" ";
	String s=" ";
	//Button btnLogOff;
	PortTalk portTalk;
	String lname="";

	/**This method is called only once after applet gets loaded.
	**/
    	public void init()
    	{
		System.out.println("Document Base is:" + getDocumentBase());
		System.out.println("Code Base is:" + getCodeBase());
		//getting parameter values of HOST and PORT from Applet PARAM tag.
		//for Formatting - Color and fonts
		Color panelbackcolor=new Color(128,128,128);
		Color panelforecolor=new Color(0,0,0);
		setBackground(panelbackcolor);
		setForeground(panelforecolor);
		Font f= new Font("Times New Roman",Font.BOLD,13);
		setFont(f);
		
		//end of Formatting....


		playerNameField = new TextField(10);
		displayAction = new Label("Enter your LoginName(Max 6 Chars):");
		
		myPanel = new Panel();
		add("Center",myPanel);
		myPanel.add(displayAction);
		myPanel.add(playerNameField);
		//playerNameField.setSize(8);
		playerNameField.addActionListener(this);
		
		//This code is used for the purpose when current player RE-LOGINs
		//This is for the purpose of re-initializing the variables.
		opponentMoved=false;
		PlayerColor=true;
		gameEnded=false;
	}
	/**Displays the Login Panel.
	*Validates the Login of the Player and Invokes postValidateLogin().
	*/
	public void validateLogin(String str)
	{
		if(str.equals("") || str.length()>6)
			return;
		portTalk.PlayerName = str;
		playerNameFound = true;
		if(myPanel != null)
			remove(myPanel);
		
		postValidateLogin();
	}
	
	/**Peforms one time setup operation.
	*Passing player name to the server by creating a PortTalk class object.
	*Creating an object of Rules Class.
	*Invoking loadImage.
	*/
	public void postValidateLogin()
	{
		// Initializing Board Position to Empty.
		//arrPortSetting[0]=getParameter("HOST");
		//arrPortSetting[1]=getParameter("PORT");
		portTalk = new PortTalk(arrPortSetting,portTalk.PlayerName);
		portTalk.sendData(portTalk.PlayerName+"§Opponent");

		lname=portTalk.getLocalname();
		StartBoard sb = new StartBoard();
		sb.setBoard();

		//Since Login is Created now there is no need to Login again instead we will use empty
		setFont(fplain);
		
		System.out.println("After Initializing PortTalk but Before Loading Images...");
		System.out.println("Value of PlayerColor is:" + PlayerColor);
		
		setBackground(boardColor);
        	addMouseListener(this);
        	addMouseMotionListener(this);

		/* CLIENT Code for init() is below */
		setLayout(null);
		//btnLogOff	= new Button("LogOff");
		txtSend		= new TextField(100);
		
		/* Since CLIENT is combined with ChessBoard the Following setBounds data
		will be modified accordingly
		Note: here only y-coordinates will be modified by +450*/ 
			
		txtMessage.setBounds(30,450+10, 340,100);
		txtMessage.setEditable(false);
		//btnLogOff.setBounds(385,450+80,50,25);
		txtSend.setBounds(30,450+115,340,25);
		movehistory.setBounds(450,30,220,400);
		movehistory.setEditable(false);
		//add(btnLogOff);
			
		add(movehistory);
		add(txtMessage);
		add(txtSend);
		//btnLogOff.addActionListener(this);
		movehistory.addTextListener(this);
		txtSend.addActionListener(this);

		/*CLIENT Code for init() ends here. */
		loadImages();
		Rules r=new Rules(ChessBoard.Board);
		
		//This code is used for the purpose when current player RE-LOGINs
		//This is for the purpose of re-initializing the variables.
		txtMessage.setText("<--Welcome to the Chess-cum-Chat Applet-->");
		movehistory.setText("<--Move History-->");
		opponentMoved=false;
		PlayerColor=true;
		gameEnded=false;
	}
	/**Loads Images and stores in 2X6 Array of Images with name Pieces.
	*It also keeps track of piece to be displayed corresponding to player color.
	*/
	public void loadImages()
	{
               if(PlayerColor==true)
               {
                /*Importing Black Pieces from the Parameter tag in the Html file.*/
                Pieces[0][0]=getImage(getDocumentBase(),getParameter("bpawn"));
                Pieces[0][1]=getImage(getDocumentBase(),getParameter("brook"));
                Pieces[0][2]=getImage(getDocumentBase(),getParameter("bknight"));
                Pieces[0][3]=getImage(getDocumentBase(),getParameter("bbishop"));
                Pieces[0][4]=getImage(getDocumentBase(),getParameter("bqueen"));
                Pieces[0][5]=getImage(getDocumentBase(),getParameter("bking"));
		/*Importing White Pieces from the Parameter tag in the Html file.*/
                Pieces[1][0]=getImage(getDocumentBase(),getParameter("wpawn"));
                Pieces[1][1]=getImage(getDocumentBase(),getParameter("wrook"));
                Pieces[1][2]=getImage(getDocumentBase(),getParameter("wknight"));
                Pieces[1][3]=getImage(getDocumentBase(),getParameter("wbishop"));
                Pieces[1][4]=getImage(getDocumentBase(),getParameter("wqueen"));
                Pieces[1][5]=getImage(getDocumentBase(),getParameter("wking"));
               }
                if(PlayerColor==false)
               {
                /*Importing Black Pieces from the Parameter tag in the Html file.*/
                Pieces[1][0]=getImage(getDocumentBase(),getParameter("bpawn"));
                Pieces[1][1]=getImage(getDocumentBase(),getParameter("brook"));
                Pieces[1][2]=getImage(getDocumentBase(),getParameter("bknight"));
                Pieces[1][3]=getImage(getDocumentBase(),getParameter("bbishop"));
                Pieces[1][4]=getImage(getDocumentBase(),getParameter("bqueen"));
                Pieces[1][5]=getImage(getDocumentBase(),getParameter("bking"));
		/*Importing White Pieces from the Parameter tag in the Html file.*/
                Pieces[0][0]=getImage(getDocumentBase(),getParameter("wpawn"));
                Pieces[0][1]=getImage(getDocumentBase(),getParameter("wrook"));
                Pieces[0][2]=getImage(getDocumentBase(),getParameter("wknight"));
                Pieces[0][3]=getImage(getDocumentBase(),getParameter("wbishop"));
                Pieces[0][4]=getImage(getDocumentBase(),getParameter("wqueen"));
                Pieces[0][5]=getImage(getDocumentBase(),getParameter("wking"));
               }
	}

	/**Method to concatenate localhost name to String and also Append it with §Send
	*/
	public void sendMessage(String str)
	{
		//txtMessage.setText(txtMessage.getText() + str);
		if(str.length() > 0)
		{
			System.out.println("In sending message" + str);
			str=portTalk.PlayerName+" : " + str;
			portTalk.sendData(str+"§Send");
			txtMessage.append("\n"+str);
			txtSend.setText("");
		}        
		else
			return;
	}
	
	/**Method overriden for implementing ActionListener - 
	*Operation is to get the playerName at time of Login
	*AND to send message from the Chat Text Box.
	*/
        public void actionPerformed(ActionEvent ae)
	{
			Object source = ae.getSource();
			if(source == txtSend)
			{
				sendMessage(txtSend.getText());
			}
			if(source == playerNameField)
			{
				TextComponent tc = (TextComponent)source;
				validateLogin(tc.getText());
			}
	      	      s = ae.getActionCommand();
			if (s.equals("LogOff"))
			{
				portTalk.sendData(lname+"§Logoff");
			}
        	        repaint();
	}
	/**Performs the operation when text in MoveHistory is changed
	*/
	public void textChanged(TextEvent te)
	{
		//System.out.println("Inside textChanged. i.e movehistory changed");
                if(opponentMoved)
                {
                	System.out.println("Inside Mouse Moved and Also Opponent Moved!!");
                	repaint();
                	opponentMoved=false;
			ChessBoard.startPlay=true; //to set the startPlay when repaint is called
                }
	}
	/**Peforms operations when MoveHistory is changed
	*This method reflects the Move of the Opponent in the Current Players Board.
	*/
	public void textValueChanged(TextEvent te)
	{
		//System.out.println("Inside textValueChanged. i.e movehistory changed");
                if(opponentMoved)
                {
                	Check ch=new Check();
			byte LocalBoard[][]=new byte[8][8];
			for(int i=0;i<=7;i++)
			{
				for(int j=0;j<=7;j++)
					LocalBoard[i][j]=ChessBoard.Board[i][j];
			}
			System.out.println("King is at before validating:" + ch.getKingPos(ChessBoard.Board));
			if(ch.isKingInAttack(LocalBoard))
			{
				CheckMate cm=new CheckMate(ChessBoard.Board);
				System.out.println("Before Validating: King is UNDER CHECK :-( ");
				if(CHECKMATE)
				{
					//code for Playing Sound when opponent checkmates
					System.out.println("When Opponent Move: Before Playing CheckMate");
					play(getCodeBase(), "Sounds/checkmate.wav");
					portTalk.sendData(portTalk.PlayerName + " is CHECKMATED§Send");
					txtMessage.append("\nYou are CHECKMATED!!!");
					startPlay=false;
					repaint();					
					return;
				}
				play(getCodeBase(), "Sounds/check.wav");				
				CHECK=true;
			}
			else
			{
				System.out.println("Inside Stalemate!!");
				CheckMate cm=new CheckMate(ChessBoard.Board);
				System.out.println("Before Validating: King is not UNDER CHECK :-( ");
				if(STALEMATE)
				{
					//code for Playing Sound when opponent stalemates
					System.out.println("When Opponent Move: Before Playing StaleMate");
					play(getCodeBase(), "Sounds/stalemate.wav");
					portTalk.sendData(portTalk.PlayerName+ " is STALEMATED§Send");
					txtMessage.append("\nSTALEMATE!!!");
					startPlay=false;
					repaint();					
					return;
				}
				else
				{
					//code for Playing Sound when opponent moved
					System.out.println("When Opponent Move: Before Playing Sound");
					play(getCodeBase(), "Sounds/move.wav");
	                               	//System.out.println("Inside Mouse Moved and Also Opponent Moved!!");
				}
			}
			repaint();
                	opponentMoved=false;
			ChessBoard.startPlay=true; //to set the startPlay when repaint is called
                }
	}
	/**This method disconnects the Current Player from the Server.
	*It sends message to Server When Applet is Closed as lname+§Logoff
	*/
	public void destroy()
	{
		portTalk.sendData(lname+"§Logoff");
	}

	/**This method performs all Graphic related update job
	*It also invokes drawBoard and highlightSquare methods.
	*/
	 public void update(Graphics g)
	 {
  		System.out.println("Inside Update");
		showStatus(msg);
		if(gameEnded)
		{
			portTalk.sendData(lname+"§Logoff");
			gameEnded=false;
			//repaint();
			/*remove(btnLogOff);
			remove(movehistory);
			remove(txtMessage);
			remove(txtSend);
			repaint();
			Font f = new Font("Game Ended", Font.BOLD,30);
			System.out.println("Game Ended");	
			g.setColor(Color.yellow);
			g.drawString("Game Ended", 200,200);*/
		}
		if(playerNameFound)
		{
			drawBoard(g); //draws the Board
			highlightSquare(g);
			//For displaying the Top Text
			Color black = new Color(0,0,0);
			g.setColor(black);
			g.drawString(Developers,30,610);
			g.drawString(portTalk.PlayerName,220,450);
			g.drawString(portTalk.OpponentName,220,20);
		}
     	}
        
        /**Draws the Chess Squares i.e Black and White Square.
        *It also places the Images simultaneously by invoking placeImage method.
        */
	public void drawBoard(Graphics g)
	{
        	System.out.println("Inside drawBoard");
                Color gr=new Color(120,120,120);
                Color wh=new Color(225,225,225);
                for(int i=0;i<=N-1;i++)
                {
                        for(int j=0;j<=N-1;j++)
                        {
                                if((i+j)%2==0)
                                        g.setColor(gr);
                                else
                                        g.setColor(wh);
                                        
                                 /*to avoid the color of the red and blue which remains previously 
                                 so redraw the rect here every time update is called */

				//formula for drawing the squares
                                //g.drawRect(j*SIDE+30,(30+(7-i)*SIDE),SIDE,SIDE);
                                //g.draw3DRect(j*SIDE+30,(30+(7-i)*SIDE),SIDE,SIDE,true);
				g.drawRoundRect(j*SIDE+30,(30+(7-i)*SIDE),SIDE,SIDE,15,15);

				//formula filling the squares
				//g.fillRect(j*SIDE+30,(30+(7-i)*SIDE),SIDE,SIDE);
				//g.fill3DRect(j*SIDE+30,(30+(7-i)*SIDE),SIDE,SIDE,true);
				g.fillRoundRect(j*SIDE+30,(30+(7-i)*SIDE),SIDE,SIDE,15,15);

				//placing Image
                                placeImage(g, i, j);
			}
		}
	}
	
	/**Draws Red Square around the Square that is active during firstClick
	*It invokes completeMove method during secondClick.
	*/
	public void highlightSquare(Graphics g)
	{
                if(row>=0 && col>=0)
                {
			if(firstClick==false)
			{
				drawBoard(g);
			}
                	else if(firstClick==true && secondClick==false)
                	{
                                Color re=new Color(255,0,0);
                                g.setColor(re);
				store(r,c);
	                        g.drawRoundRect(col*SIDE+30,(30+(7-row)*SIDE),SIDE,SIDE,15,15);
			}
			else if(secondClick==true)
                        {
				Color re=new Color(255,0,0);
                               	g.setColor(re);
                               	//formula for highlighting the RED Square, the source square
				if((orow>=0 && orow<=7) && (ocol>=0 && ocol<=7))
				{
					g.drawRoundRect(ocol*SIDE+30,(30+(7-orow)*SIDE),SIDE,SIDE,15,15);
				}
				completeMove(g);
                        }
                }
	}
	/**It Stores the or and oc of previous moves and also the piecetomove*/
	public void store(int r,int c)
	{
		piecetomove = Board[r][c];
	        or=r;
		oc=c;
	}
	
	/**Checks the type of piece and validates the move*/
	public void completeMove(Graphics g)
	{
	        Check ch=new Check();
		byte LocalBoard[][]=new byte[8][8];
		for(int i=0;i<=7;i++)
		{
			for(int j=0;j<=7;j++)
				LocalBoard[i][j]=ChessBoard.Board[i][j];
		}
		System.out.println("Getting Inside: completeMove()");
		System.out.println("Before Validating:  King is SAFE :-)");
		
		//condition for capture must be include here later and also condition for check.
		//Last condition is to avoid capturing the opponents King b'cos you cannot capture opponent king but can only trap it i.e mate it.
		if((LocalBoard[r][c] == -1 || getPieceOnSquare.isBlackOnSquare(LocalBoard,r,c)) && (LocalBoard[r][c]!=5))
		{
			String moveDetail=or+"`"+oc+":"+r+"`"+c;
			Rules localrule=new Rules(LocalBoard);
			if(localrule.validateAsPerRules(piecetomove, moveDetail))
			{
				if(piecetomove==15) 	//start of code for performing operations when King is Moved
				{
					kingMoveOperation(moveDetail);
				}
				LocalBoard[r][c] = piecetomove;
				LocalBoard[or][oc] = -1;
				
				if(ChessBoard.enpassantDone)
				{
					System.out.println("Board Updated after Enpassant");
					LocalBoard[4][c]=-1;
				}
				//code for PROMOTION
				if(r==7 && piecetomove==10)
				{
					System.out.println("Inside CompleteMove()..");
					promotionDialog pd=new promotionDialog((new Frame()),"Promotion");
					pd.setVisible(true);
					LocalBoard[r][c] = ChessBoard.peicepromotedto;
				}
			
				/*checks kings safety before finalising but should also include special case of attack on skipped square
				at the time of castling.*/				
				if(!ch.isKingInAttack(LocalBoard))
				{
					System.out.println("After making Move, King is Safe: It is safe to Make Move!! :::---)))");
					updateBoardOnMoveCompletion(g,piecetomove,moveDetail);

					if(ch.isKingInAttack(ChessBoard.Board))
						System.out.println("After Move Completion: King is STILL Under Attack :-( ");
					else
						System.out.println("After Move Completion: King is SAFE :-)");
 	  			} //end of checking for kings safety
 	  			else
 	  			{
 	  				System.out.println("Inside else, It is not safe to make move: King is NOT SAFE!! :::--(((");
 	  			}
			}//end of check of validate

			//done to restore the firstClick and secondClick values and also
			resetDefaultValues();
			piecetomove=-1;
			
			CHECK=false; //value again set to false for move to continue
		}					
	}

	/**Performs the King move Operations.*/
	public void kingMoveOperation(String moveDetail)
	{

		//Whenever King is moved for the first time set isKingFresh to false
		isKingFresh=false;

		StringTokenizer st=new StringTokenizer(moveDetail,"`:`");
		int lor=Integer.parseInt(st.nextToken());
		int loc=Integer.parseInt(st.nextToken());
		int lr=Integer.parseInt(st.nextToken());
		int lc=Integer.parseInt(st.nextToken());

		//tests if it is a CASTLING and updates castlingDone flag accoringly.
		//to keep track of the castling info and pass("+§RCastling") from completeMove to Opponent using Chat.

		if(lc==loc+2)
		{
			castlingDone=true;
			System.out.println("Right Castling...");
		}
		//to keep track of the castling info and pass("+§LCastling") from updateBoardOnMoveCompletion to Opponent using Chat.
		if(lc==loc-2)
		{
			castlingDone=true;
			System.out.println("Left Castling...");
		}
	}
	
	/**It redraws the ChessBoard and updates positions.
	*It also repainting the Board when  the Move has been in finalized
	*/
	public void updateBoardOnMoveCompletion(Graphics g, byte piecetomove, String moveDetail)
	{
		//condition for PROMOTION
		if(ChessBoard.peicepromotedto==-1)
		{
			ChessBoard.Board[r][c] = piecetomove;
			ChessBoard.Board[or][oc] = -1;
		}
		else
		{
			ChessBoard.Board[r][c] = ChessBoard.peicepromotedto;
			ChessBoard.Board[or][oc] = -1;
		}
		
		//code for Playing Sound when current player makes a move
		System.out.println("When Current Player Moved: Before Playing Sound");
		play(getCodeBase(), "Sounds/move.wav");
		
		if(ChessBoard.enpassantDone)
		{
			System.out.println("Board Updated after Enpassant");
			ChessBoard.Board[4][c]=-1;
			ChessBoard.enpassantDone=false; //reseting the flag to false;
		}

		//To check whether castling has been done if yes then send ("+§Board") of Rook to Opponent
		if(castlingDone)
		{
			System.out.println("Inside Castling Done in updateBoardOnMoveCompletion()");
			castlingOperation(moveDetail);
		} //end of check of castling

		 //when normal move is made i.e if no castling done.
		else
		{
	
			//In case during the normal move any rook is moved set the Freshness of the Rook as false
				StringTokenizer st=new StringTokenizer(moveDetail,"`:`");
				String lor=st.nextToken();
				String loc=st.nextToken();
				if((lor+loc).equals("00"))
				{
					isLeftRookFresh=false;
					System.out.println("Left Rook Moved--");
				}
				if((lor+loc).equals("07"))
				{
					isRightRookFresh=false;
					System.out.println("Right Rook Moved--");
				}
				
			// This code Update the CHESSBOARD movehistory textarea
			if(ChessBoard.peicepromotedto==-1)
			{
				movehistory.append("\n" + portTalk.PlayerName + " \t" + piecetomove + "\t" + or + "," + oc + "\t" + r + "," + c);
				msg=piecetomove+"`"+or+"`"+oc+":"+r+"`"+c;
 				portTalk.sendData(msg+"§Board");
 			}
 			else //for processing in case of PROMOTION
 			{
				movehistory.append("\n" + portTalk.PlayerName + " \t" + piecetomove + "\t" + or + "," + oc + "\t" + r + "," + c);
				msg=piecetomove+"`"+or+"`"+oc+":"+r+"`"+c+":"+ChessBoard.peicepromotedto;
 				portTalk.sendData(msg+"§Promotion");
 				ChessBoard.peicepromotedto=-1;
			} 				
 	  	}

		//Once move is made wait for opponent to make move
		startPlay=false;
		//formula for highlighting the Blue Square, the destination square
		Color bl=new Color(0,0,255);
		g.setColor(bl);
                g.drawRoundRect(col*SIDE+30,(30+(7-row)*SIDE),SIDE,SIDE,15,15);		
		drawBoard(g);
	}
	/**It performs the castling operations.
	*It also sees whether the castling performed is a Short Castling or the Long Castling.
	*It updates the Castling variables accordingly.
	*/
	public void castlingOperation(String moveDetail)
	{
			StringTokenizer st=new StringTokenizer(moveDetail,"`:`");
			// Local or,oc,r,c
			int lor=Integer.parseInt(st.nextToken());
			int loc=Integer.parseInt(st.nextToken());
			int lr=Integer.parseInt(st.nextToken());
			int lc=Integer.parseInt(st.nextToken());

			if(lc>loc && (7-loc)==4) // Right Castling long
			{
				//Updating the position of Right Rook after castling explicitly but only for current player
				//to update position of opponent Changes has to be made in completeMove method.
				ChessBoard.Board[0][loc+1]=ChessBoard.Board[0][7];
				ChessBoard.Board[0][7]=-1;
	
				portTalk.sendData("o-o-o"+"§RCastling");
				movehistory.append("\n" + portTalk.PlayerName+ " \t\t" + "o-o-o");
			}
			else if(lc>loc && (7-loc)==3) // Right Castling short
			{
				//Updating the position of Right Rook after castling explicitly but only for current player
				//to update position of opponent Changes has to be made in completeMove method.
				ChessBoard.Board[0][loc+1]=ChessBoard.Board[0][7];
				ChessBoard.Board[0][7]=-1;

				portTalk.sendData("o-o"+"§RCastling");
				movehistory.append("\n" +  portTalk.PlayerName + " \t\t"  + "o-o");
			}
			else if((lc < loc) && ((loc-0)==4)) // Left Castling long
			{
				//Updating the position of Left Rook after castling explicitly but only for current player
				//to update position of opponent Changes has to be made in completeMove method.
				ChessBoard.Board[0][loc-1]=ChessBoard.Board[0][0];
				ChessBoard.Board[0][0]=-1;

				portTalk.sendData("o-o-o"+"§LCastling");
				movehistory.append("\n" +  portTalk.PlayerName + " \t\t"  + "o-o-o");
			}
			else if((lc < loc) && ((loc-0)==3)) // Left Castling short
			{
				//Updating the position of Left Rook after castling explicitly but only for current player
				//to update position of opponent Changes has to be made in completeMove method.
				ChessBoard.Board[0][loc-1]=ChessBoard.Board[0][0];
				ChessBoard.Board[0][0]=-1;

				portTalk.sendData("o-o"+"§LCastling");
				movehistory.append("\n" +  portTalk.PlayerName + " \t\t"  + "o-o");
			}
			ChessBoard.isKingFresh=false;
			ChessBoard.isRightRookFresh=false;
			ChessBoard.isLeftRookFresh=false;

			//to Reset it to false so that control does not enter this code again
			//this code has to be executed only once for each player.
			castlingDone=false;
	}
	
	//Called when repaint is called actually calls update(g)
	public void paint(Graphics g)
  	{
  		System.out.println("Inside Paint..");
    		update(g);
	}

	/**It is the methods which is initiates the start of the move by the player.
	*/
        public void mousePressed(MouseEvent me)
        {
        	if(!startPlay)
        	{
        		msg="Board is Disabled";
        	}
        	if(startPlay)
        	{
                	mouseX=me.getX();
                	mouseY=me.getY();
			System.out.println("Clicked:" + mouseX + "," + mouseY);
                	if(mouseX>=30 && mouseY>=30)
                	{
                		r=7-(mouseY-30)/SIDE; //-30 is for deducting top free space alloted for displaying Opponents Name
	                	c=(mouseX-30)/SIDE;
	                }
        	        if(r<=7 && c<=7 && r>=0 && c>=0)
			{
				msg="Clicked:" + mouseX + "," + mouseY + ", Square:" + r + "," + c;
				System.out.println(msg);
				String str=r+"`"+c;
				System.out.println("Inside MousePressed:Before clickMethod Called");
				clickMethod(PlayerColor,r,c); //WHITE is True and BLACK is False
				System.out.println("Inside MousePressed:After clickMethod Called");			
			}
		        else
        	        {
                	        row = col = -1;
                        	r = c = -1;
                        	firstClick=false;
                        	msg="Clicked:" + mouseX + "," + mouseY + ", Outside Board!!" ;
                	}
                }
                System.out.println("Inside MousePressed:Before showStatus");
                showStatus(msg);
                System.out.println("Inside MousePressed:After showStatus");
        }
        /**This method keeps track of click variables.
        *It invokes the activateSquare method.
        */
	public void clickMethod(boolean cl,int a, int b) //a,b are row and columns respt.
	{
		if(firstClick==false)
		{
			//Player can move only his peice from square which is not empty
			if(getPieceOnSquare.isWhiteOnSquare(ChessBoard.Board,a,b))
			{
				System.out.println("You Clicked on your peice.");
	                	activateSquare(a,b);
				firstClick=true;
				//System.out.println("firstClick is set TRUE");
				//repaint();
			}
		}
		else
		{
			if(secondClick==false)
			{
				if(getPieceOnSquare.isBlackOnSquare(ChessBoard.Board,a,b) || getPieceOnSquare.isEmptySquare(ChessBoard.Board,a,b))
				{
					System.out.println("You Clicked on Empty or Opponent Peice");
					activateSquare(a,b);
					firstClick=true;
					secondClick=true;
				}
				else
				{
					resetDefaultValues();
				}
			}
		}
		repaint();
	}
	/**It Resets the row and columns variables.
	*/
	public void resetDefaultValues()
	{
		firstClick=secondClick=false;
		or=oc=r=c=orow=ocol=row=col=-1;
	}
	/**This method keeps track of rows and columns variable to hightlight the square.
	*It also helps in deactivation of squares.
	*/
        public void activateSquare(int x,int y) 
        {
		//if same square clicked again deactivate the firstClick or dehighlight the red square.
                if(row==x && col==y)  
		{
                        row=col=-1;
                        firstClick=false;
                }
                else
                {
                        orow=row;
                        ocol=col;
                        row=x;
                        col=y;
                }
        }
	/**Displays the Square which is clicked by user*/
	public void displaySquare(String sqsel)
	{
		StringTokenizer st=new StringTokenizer(sqsel,"`");
		String localrow=st.nextToken();
		String localcolumn=st.nextToken();
		System.out.println("Square Selected:" + localrow + "," + localcolumn);
	}

	/**It draws the Image of piece on the Board.
	*It scans the Board Array and then place the approriate Image at the respective square on Board
	*It is invoked by drawBoard.
	*/
        public void placeImage(Graphics g,int i, int j)
        {
        	if(Board[i][j]>=0)
        	{
			switch(Board[i][j])
			{

	        		// ------------ Position of Black Pieces ---------------

				case 0:			// position of black pawns
				       	g.drawImage(Pieces[0][0],j*SIDE+30+9,(30+(7-i)*SIDE)+9,this);
				       	break;
				case 1:			// position of black rooks
					g.drawImage(Pieces[0][1],j*SIDE+30+9,(30+(7-i)*SIDE)+10,this);
			       		break;
				case 2:			//position of black knight
					g.drawImage(Pieces[0][2],j*SIDE+30+8,(30+(7-i)*SIDE)+7,this);
					break;
				case 3:			//position of black bishop
					g.drawImage(Pieces[0][3],j*SIDE+30+6,(30+(7-i)*SIDE+6),this);
					break;
				case 4:			// position of black king
					g.drawImage(Pieces[0][4],j*SIDE+30+6,(30+(7-i)*SIDE+5),this);
					break;
				case 5:			// position of black queen
					g.drawImage(Pieces[0][5],j*SIDE+30+6,(30+(7-i)*SIDE+4),this);
					break;
					
                	                // ------------Position of White Pieces -----------------
	
				case 10:			// position of white pawns
				       	g.drawImage(Pieces[1][0],j*SIDE+30+9,(30+(7-i)*SIDE)+9,this);
				       	break;
				case 11:			// position of white rooks
					g.drawImage(Pieces[1][1],j*SIDE+30+9,(30+(7-i)*SIDE)+10,this);
					break;
				case 12:			//position of white knight
					g.drawImage(Pieces[1][2],j*SIDE+30+8,(30+(7-i)*SIDE)+7,this);
					break;
				case 13:			//position of white bishop
					g.drawImage(Pieces[1][3],j*SIDE+30+6,(30+(7-i)*SIDE+6),this);
					break;
				case 14:			// position of white king
					g.drawImage(Pieces[1][4],j*SIDE+30+6,(30+(7-i)*SIDE+5),this);
					break;
				case 15:			// position of white queen
					g.drawImage(Pieces[1][5],j*SIDE+30+6,(30+(7-i)*SIDE+4),this);
					break;
			}
		}			
	}
	/**It displays the x,y co-ordinates on Status Bar when mouse is moved.
	*/
        public void mouseMoved(MouseEvent me)
        {
                mouseX=me.getX();
                mouseY=me.getY();
                msg="Moved:"+mouseX+","+mouseY;
                showStatus(msg);
        }
        public void mouseClicked(MouseEvent me)
        {}
        public void mouseEntered(MouseEvent me)
        {}                
        public void mouseExited(MouseEvent me)
        {}
        public void mouseReleased(MouseEvent me)
        {}
        public void mouseDragged(MouseEvent me)
        {}
}

/**This Class brings the ChessBoard at its starting initiale position.
*ChessBoard is made of 8X8 Byte Array.
*With bottom most row of the Board  from left to right indicating the subscript values from [0][0] to [0][7].
*AND the top most row of the Board  from left to right indicating the subscript values from [7][0] to [7][7].
*Here each Chess Piece is assigned a unique code.
*There are 12 different Chess Pieces in ChessBoard. Their codes are:
* 00 - BPawn, 01 - BRook, 02 - BKnight, 03 - BBishop, 04 - BQueen,  05 - BKing;
* 10 - WPawn, 11 - WRook, 12 - WKnight, 13 - WBishop, 14 - WQueen,  15 - WKing;
*/
class StartBoard implements Constants
{
	public void setBoard()
	{
		int i = 0, j = 0;
       		for(int a=0;a<=(N-1);a++)
		{
			for(int b=0;b<=(N-1);b++)
			{
					ChessBoard.Board[a][b] = -1;
			}
		}

		for(i=0;i<=7;i++)
		{
			for(j=0;j<=7;j++)
			{
				// ------------Position of Black Pieces -----------------
			
                               	if(i==6) // to position the black pawns
                                	ChessBoard.Board[i][j]=0;
				if( (i==7 && j==0 ) || (i==7 && j==7) ) // to position the black rooks
					ChessBoard.Board[i][j]=1;
				if( (i==7 && j==1 ) || (i==7 && j==6) ) //to position black knight
					ChessBoard.Board[i][j]=2;
                		if( (i==7 && j==2 ) || (i==7 && j==5) ) //to position black bishop
					ChessBoard.Board[i][j]=3;
				if( (i==7 && j==3 ))  // to position black queen
					ChessBoard.Board[i][j]=4;
				if( (i==7 && j==4 )) // to position black king
					ChessBoard.Board[i][j]=5;

                                // ------------Position of White Pieces -----------------
                 
                		if(i==1) // to position the white pawns
					ChessBoard.Board[i][j]=10;
				if( (i==0 && j==0 ) || (i==0 && j==7) ) // to position the white rooks
					ChessBoard.Board[i][j]=11;
				if( (i==0 && j==1 ) || (i==0 && j==6) ) // to position the white knight
					ChessBoard.Board[i][j]=12;
                		if( (i==0 && j==2 ) || (i==0 && j==5) ) // to position the white bishop
					ChessBoard.Board[i][j]=13;
				if( (i==0 && j==3 )) // to position the white Queen
					ChessBoard.Board[i][j]=14;
				if( (i==0 && j==4 )) // to position the white King
					ChessBoard.Board[i][j]=15;
                        }
                }
	}
}

/**This Class performs the calculation part to see whether Current Players King in under CHECK.
*/
class Check implements Constants
{
	byte LocalBoard[][];
	/**This method is first of the overloaded method to find whether the current players King is under attack.
	*It takes the Board Array as argument and return TRUE if king is under attack.
	*It first invokes the member function getKingPos to find the location of king of current player.
	*Then it scans for opponent peice in the Board Array which is attacking the current player king.
	*If such a piece is found, it returns TRUE.
	*else it returns FALSE.
	*/
	public boolean isKingInAttack(byte [][] b)
	{
		LocalBoard=b;
		String kingpos=getKingPos(LocalBoard);
		String moveLeadToKing="";
		Rules r=new Rules(LocalBoard);
		for(int i=0;i<=7;i++)
		{
			for(int j=0;j<=7;j++)
			{
				moveLeadToKing=i+"`"+j+":"+kingpos;
				//System.out.print(i+","+j+":"+LocalBoard[i][j]+" --- ");
				if(LocalBoard[i][j]!=-1)
				{
					switch(LocalBoard[i][j])
					{
						case 0:
							if(r.pawnMoveB(moveLeadToKing))
							{
								//System.out.println("Opponent PAWN attacking King:" + moveLeadToKing);
								return true;
							}
							break;
						case 1:
							if(r.rookMove(moveLeadToKing))
							{
								//System.out.println("Opponent ROOK attacking King:" + moveLeadToKing);
								return true;
							}
							break;
						case 2:
							if(r.knightMove(moveLeadToKing))
							{
								//System.out.println("Opponent KNIGHT attacking King:" + moveLeadToKing);
								return true;
							}
							break;
						case 3:
							if(r.bishopMove(moveLeadToKing))
							{
								//System.out.println("Opponent BISHOP attacking King:" + moveLeadToKing);
								return true;
							}
							break;
						case 4:
							if(r.queenMove(moveLeadToKing))
							{
								//System.out.println("Opponent QUEEN attacking King:" + moveLeadToKing);
								return true;
							}
							break;
						case 5:
							if(r.kingMove(moveLeadToKing))
							{
								//System.out.println("Opponent KING attacking King:" + moveLeadToKing);
								return true;
							}
					}
				}
			}
		}
		return false;
	}
	/**This method is the second of the overloaded method to find whether the current players King is under attack.
	*It takes the Board Array along with two int variables as arguments and return TRUE if king is under attack.
	*Unlike the first method it does not invoke the method getKingPos  as they are already provided by the calling function.
	*Then it scans for opponent peice in the Board Array which is attacking the current player king.
	*If such a piece is found, it returns TRUE.
	*else it returns FALSE.
	*/
	public boolean isKingInAttack(byte [][] b,int lor,int loc)
	{
		LocalBoard=b;
		String kingpos=lor+"`"+loc;
		String moveLeadToKing="";
		Rules r=new Rules(LocalBoard);
		for(int i=0;i<=7;i++)
		{
			for(int j=0;j<=7;j++)
			{
				moveLeadToKing=i+"`"+j+":"+kingpos;
				//System.out.print(i+","+j+":"+LocalBoard[i][j]+" --- ");
				if(LocalBoard[i][j]!=-1)
				{
					switch(LocalBoard[i][j])
					{
						case 0:
							if(r.pawnMoveB(moveLeadToKing))
							{
								//System.out.println("Opponent PAWN attacking King:" + moveLeadToKing);
								return true;
							}
							break;
						case 1:
							if(r.rookMove(moveLeadToKing))
							{
								//System.out.println("Opponent ROOK attacking King:" + moveLeadToKing);
								return true;
							}
							break;
						case 2:
							if(r.knightMove(moveLeadToKing))
							{
								//System.out.println("Opponent KNIGHT attacking King:" + moveLeadToKing);
								return true;
							}
							break;
						case 3:
							if(r.bishopMove(moveLeadToKing))
							{
								//System.out.println("Opponent BISHOP attacking King:" + moveLeadToKing);
								return true;
							}
							break;
						case 4:
							if(r.queenMove(moveLeadToKing))
							{
								//System.out.println("Opponent QUEEN attacking King:" + moveLeadToKing);
								return true;
							}
							break;
						case 5:
							if(r.kingMove(moveLeadToKing))
							{
								//System.out.println("Opponent KING attacking King:" + moveLeadToKing);
								return true;
							}
					}
				}
			}
		}
		return false;
	}
	/**This method find the location of the current players king and returns a String.
	*It takes Board Array as argument and scans the array for King code of current player.
	*/
	public String getKingPos(byte [][]b)
	{
		for(int i=0;i<=7;i++)
		{
			for(int j=0;j<=7;j++)
			{
				if(b[i][j]==15)
				{
					return (i+"`"+j);
				}
			}
		}
		return "`";
	}
}

/**This class performs the calculation to see whether current player is CheckMated.
*It is instantiated only when the current player is under CHECK.
*/
class CheckMate implements Constants
{
	byte LocalBoard[][]=new byte[N][N];
	int i=0,j=0;

	/**This method initiates the calculations for checking whether current player is CheckMated.
	*It takes Board Array as argument and invokes helpFromOtherMembers method.
	*If no help is found it set the static variable CHECKMATE as TRUE (OR) STALEMATE as TRUE.
	*/
	public CheckMate(byte [][]b)
	{
		//System.out.println("Inside Checkmate Constructor..");
		for(i=0;i<=7;i++)
		{
			for(j=0;j<=7;j++)
			{
				LocalBoard[i][j]=b[i][j];
			}
		}
		//System.out.println("LocalBoard created...");
		System.out.println("YOUR Peices details are:");
		for(i=0;i<=7;i++)
		{
			for(j=0;j<=7;j++)
			{
				if(getPieceOnSquare.isWhiteOnSquare(LocalBoard,i,j))
				{
					System.out.print( LocalBoard[i][j] + ":" + i + "," +j+" -- ");
				}
			}
		}		
		System.out.println("\nCommencing Checkmate Calculations ........");
		if(!helpFromOtherMembers())
		{
			System.out.println("CHECKMATED!!!!!! :-(=");
			ChessBoard.CHECKMATE=true;
			ChessBoard.STALEMATE=true;
			//System.out.println("OPPONENT Peices is details are:");
			for(i=0;i<=7;i++)
			{
				for(j=0;j<=7;j++)
				{
					if(getPieceOnSquare.isBlackOnSquare(LocalBoard,i,j))
					{
						//System.out.print(LocalBoard[i][j] + ":" + i + "," +j+" -- ");
					}
				}
			}
		}
	}
	
	/**This method tries to get help from pieces other than King.
	*It generates all possible moves of all the peices of the current player and invokes doesThereExistSafeMove method for each such move.
	*Even if for each move there exist a move in which  of king is not under attack return FALSE. i.e no CheckMate
	*/	
	public boolean helpFromOtherMembers()
	{
		for(i=0;i<=7;i++)
		{
			for(j=0;j<=7;j++)
			{
				//find all the peice of the current player and pass it to the function doesthereExistSafeMove for that peice.
				if(getPieceOnSquare.isWhiteOnSquare(LocalBoard,i,j))
				{
					//System.out.print(LocalBoard[i][j] + ":" + i + "," +j+"\t");
					if(doesThereExistSafeMove(i,j))
					{
						System.out.println("Peice to Help is " + LocalBoard[i][j] + " and is at:" + i + "," +j);
						return true;
					}
				}
			}
		}
		return false;
	}
	/**This method Checks whether there exist some safe move for the current players King.
	*It takes two integer variables as arguments and invokes isKingInAttack of the class Check.
	*It returns TRUE if there exist such safe move,
	*else returns FALSE.
	*/
	public boolean doesThereExistSafeMove(int a,int b)
	{
		//System.out.println("Entering doesThereExistSafeMove()");
		byte [][]tempBoard=new byte[N][N];
		Rules localrule=new Rules(LocalBoard);
		Check ch=new Check();
		//trying all possible moves of the peice on a,b.
		for(int i=0;i<=7;i++)
		{
			for(int j=0;j<=7;j++)
			{
				//to avoid the further processing when we know that peice cannot stay on its on square
				// this is useful for stalemate processing.
				if(i==a && j==b)
					continue; 
					
				//copying Board again for fresh for temporary
				for(int p=0;p<=7;p++)
				{
					for(int q=0;q<=7;q++)
					{
						tempBoard[p][q]=LocalBoard[p][q];
					}
				}
								
				String moveDetail=a+"`"+b+":"+i+"`"+j;
				if(getPieceOnSquare.isBlackOnSquare(LocalBoard,i,j) || getPieceOnSquare.isEmptySquare(LocalBoard,i,j))
				{
					if(localrule.validateAsPerRules(LocalBoard[a][b],moveDetail))
					{
						tempBoard[i][j]=tempBoard[a][b];
						tempBoard[a][b]=-1;	
						if(!ch.isKingInAttack(tempBoard))
						{
							System.out.println("Move to be made to avoid mate:" + moveDetail);
							return true;
						}
					}
				}
			}
		}
		//System.out.println("Leaving doesThereExistSafeMove() Unsuccessfully!!");		
		return false;
	}
}

/**This class is used to located the content of the Board at any given location.
*/
class getPieceOnSquare
{
	/**This static method is used to find whether square at a given location of the Board contains WHITE or not.
	*It takes Board Array as argument along with two integer variables.
	*It returns TRUE if WHITE piece is found, 
	*else it returns FALSE.
	*/
	public static boolean isWhiteOnSquare(byte [][]b, int x,int y)
	{
		byte peicecode=ChessBoard.Board[x][y];
		//checks whether the square contains WHITE peice
		switch(peicecode)
		{
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				return true;
		}
		return false;
	}
	/**This static method is used to find whether square at a given location of the Board contains BLACK or not.
	*It takes Board Array as argument along with two integer variables.
	*It returns TRUE if BLACK piece is found, 
	*else it returns FALSE.
	*/
	public static boolean isBlackOnSquare(byte [][]b, int x, int y)
	{
		byte peicecode=ChessBoard.Board[x][y];
		//checks whether the square contains BLACK peice
		{
			switch(peicecode)
			{
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					return true;
			}
		}
		return false;
	}
	/**This static method is used to find whether square at a given location of the Board is EMPTY or not.
	*It takes Board Array as argument along with two integer variables.
	*It returns TRUE if EMPTY Square is found, 
	*else it returns FALSE.
	*/
	public static boolean isEmptySquare(byte [][]b, int x,int y)
	{
		byte peicecode=ChessBoard.Board[x][y];
		if(peicecode==-1)
			return true;
		else
			return false;
	}
}

/**This Class defines the rules for each Chess Piece.
*/
class Rules implements Constants
{
	int or,oc,r,c;
	byte LocalBoard[][];
	/**This constructor takes Board Array as argument and initializes its LocalBoard.
	*/
	public Rules(byte [][] b)
	{
		LocalBoard=b;
		//System.out.println("Local Board Declared..");
		/*for(int i=0;i<=7;i++)
		{
			for(int j=0;j<=7;j++)
			{
				//System.out.println("Local Board["+i+"]["+j+"]:"+LocalBoard[i][j]);
			}
		}*/
	}
	/**This method checks the validity of a KNIGHT Move.
	*It takes String variable as moveDetail and returns a boolean variable.
	*KNIGHT has eight possible moves.
	*If the move is a valid one it returns TRUE,
	*else it returns FALSE.
	*/
	public boolean knightMove(String s) //String s contains or, oc, r, c
	{
		extract(s);
		//System.out.println("Testing in knight:"+or+","+oc+"--->"+r+","+c);
		if((r==or+2 && c==oc+1) || (r==or+1 && c==oc+2) || (r==or-1 && c==oc+2) || (r==or-2 && c==oc+1) || (r==or+2 && c==oc-1) ||  (r==or+1 && c==oc-2) || (r==or-1 && c==oc-2) || (r==or-2 && c==oc-1))
		{
			if(LocalBoard[r][c]!=-1)
			{
				valid("KNIGHT: -- Captures");
				return true;
			}
			else
			{
				valid("KNIGHT:");
				return true;
			}
		}
		error("KNIGHT:");
		return false;		
	}
	/**This method checks the validity of a PAWN move of Opponent.
	*It takes String variable as moveDetail and returns a boolean variable.
	*It has 5 possible cases. (Move, Two-Square Move, Attack, Promotion and en-passant)
	*If the move is a valid one it returns TRUE,
	*else it returns FALSE.
	*/
	public boolean pawnMoveB(String s) //String s contains or, oc, r, c
	{
		extract(s);
		//System.out.println("Testing in Blackpawn:"+or+","+oc+"--->"+r+","+c);		
		if(c==oc && (r+1)==or)
		{
			if(LocalBoard[r][c]==-1) //if empty
			{
				valid("BlackPawn");
				return true;
			}
		}
		//Exception case for pawns intial positions
		if(or==6 && c==oc && (r+2)==or) 
		{
			if((LocalBoard[5][c]==-1) && (LocalBoard[4][c]==-1)) //if empty
			{
				valid("BlackPawn");
				return true;
			}
		}
		//Checking the case of pawn attack.
		if((r==or-1 && c==oc-1) || (r==or-1 && c==oc+1))
		{
			valid("BlackPawn");
			return true;
		}
		error("BlackPawn");
		return false;			
	}
	/**This method checks the validity of a PAWN move of current Player.
	*It takes String variable as moveDetail and returns a boolean variable.
	*It has 5 possible cases. (Move, Two-Square Move, Attack, Promotion and en-passant)
	*If the move is a valid one it returns TRUE,
	*else it returns FALSE.
	*/
	public boolean pawnMoveW(String s) //String s contains or, oc, r, c
	{
		extract(s); //called to update values of or,oc,r,c
		//System.out.println("Testing:"+or+","+oc+"--->"+r+","+c);
		if(c==oc && r==(or+1))
		{
			if(LocalBoard[r][c]==-1) //if empty
			{
				valid("WhitePawn");
				return true;
			}
		}
		//case of moving 2 steps from pawns intial positions
		if(or==1 && c==oc && r==(or+2))
		{
			if((LocalBoard[2][c]==-1) && (LocalBoard[3][c]==-1)) //if empty
			{
				valid("WhitePawn");
				return true;
			}
		}
		//condition when pawn captures opponent peice i.e diagonal moving.
		if((r==or+1 && c==oc-1) || (r==or+1 && c==oc+1))
		{
			
			//System.out.println("Inside PawnAttack..");
			//condition for checking the en-passant possibility
			if(ChessBoard.previousMoveOFOpponent!="")
			{
				StringTokenizer st=new StringTokenizer(ChessBoard.previousMoveOFOpponent,"`:`");
				int prev_or=Integer.parseInt(st.nextToken());
				int prev_oc=Integer.parseInt(st.nextToken());
				int prev_r=Integer.parseInt(st.nextToken());
				int prev_c=Integer.parseInt(st.nextToken());
				
				System.out.println("PreviousMoveOFOpponent was of the Opponent was: "+ChessBoard.previousMoveOFOpponent);				
				
				//following condition checks the elligibility of en-passant
				//check if previous move was a pawn move AND its old position was at row 6 AND column of the pawn has not changed
				if(LocalBoard[prev_r][prev_c]==0 && prev_or==6 && prev_oc==prev_c)
				{
					System.out.println("PreviousMoveOFOpponent was of Fresh Pawn: "+ChessBoard.previousMoveOFOpponent);
					//currentplayers pawn is somewhere in row 5 i.e r=5 AND 
					//currentplayers pawn is moving at the column as the opponents previous moves pawn.
					if((r==5 && c==prev_c))
					{
						valid("White Pawn is Elligible for en-passant!!!");
						//setting it to null so it does not enters this code when called by CHECK AND CHECKMATE CLASS
						ChessBoard.previousMoveOFOpponent="";
						ChessBoard.enpassantDone=true;
						valid("WhitePawn");
						return true;
					}
				}
			}
			//if not empty i.e if there is some peice whether BLACK or WHITE is checked before itself			
			if(LocalBoard[r][c]!=-1) 
			{
				valid("WhitePawn");
				return true;
			}
		}
		error("WhitePawn");
		return false;			
	}
	/**This method checks the validity of a BISHOP Move of current Player.
	*It takes String variable as moveDetail and returns a boolean variable.
	*It has 4 possible cases. (Moving Top-Left, Top-Right, Bottom-Left, Bottom-Right).
	*If the move is a valid one it returns TRUE,
	*else it returns FALSE.
	*/
	public boolean bishopMove(String s) //String s contains or , oc, r, c
	{
		extract(s);
		//System.out.println("Testing:"+or+","+oc+"--->"+r+","+c);
		
		/*Bottom-Left*/
		if((r<or && c<oc) && (or-r)==(oc-c)) //II condition used to avoid cases(4,5-->1,4)
		{
			for(int x=or-1, y=oc-1;(x>=r) && y>=c;x--,y--)
			{
				if(x==r && y==c)  //if testing x,y becomes equal to recent r,c then validate
				{
					valid("BISHOP:Bottom-Left");
					return true;				
				}				
				if(LocalBoard[x][y]!=-1) //is not empty
				{
					error("BISHOP:Bottom-Left");
					return false;
				}
			}
		}
		/*Top-Left*/
		if((r>or && c<oc) && (r-or)==(oc-c)) // II condition used to avoid cases(1,6-->4,5)
		{
			for(int x=or+1,y=oc-1;x<=r  && y>=c;x++,y--)
			{
				if(x==r && y==c)  //if testing x,y becomes equal to recent r,c then validate
				{
					valid("BISHOP:Top-Left");
					return true;				
				}				
				if(LocalBoard[x][y]!=-1) //is not empty
				{
					error("BISHOP:Top-Left");
					return false;
				}
			}
		}
		
		/*Top-Right*/
		if((r>or && c>oc) && (r-or)==(c-oc)) // II condition used to avoid cases(1,1-->4,2)
		{
			for(int x=or+1,y=oc+1;x<=r && y<=c;x++,y++)
			{
				if(x==r && y==c)  //if testing x,y becomes equal to recent r,c then validate
				{
					valid("BISHOP:Top-Right");
					return true;				
				}				
				if(LocalBoard[x][y]!=-1) //is not empty
				{
					error("BISHOP:Top-Right");
					return false;
				}
			}
		}
		/*Bottom-Right*/
		if((r<or && c>oc) && (or-r)==(c-oc)) //II condition used to avoid cases(4,4-->1,5)
		{
			for(int x=or-1,y=oc+1;x>=r && y<=c;x--,y++)
			{
				if(x==r && y==c) //if testing x,y becomes equal to recent r,c then validate
				{
					valid("BISHOP:Bottom-Right");
					return true;				
				}				
				if(LocalBoard[x][y]!=-1) //is not empty
				{
					error("BISHOP:Bottom-Right");
					return false;
				}
			}
		}
		return false;
	}
	/**This method checks the validity of a ROOK Move of current Player.
	*It takes String variable as moveDetail and returns a boolean variable.
	*It has 4 possible cases. (Moving Top, Right, Left, Bottom).
	*If the move is a valid one it returns TRUE,
	*else it returns FALSE.
	*/
        public boolean rookMove(String s) //String s contains or , oc, r, c
	{
		extract(s);
		//System.out.println("Testing:"+or+","+oc+"--->"+r+","+c);		
		/*---- TOP(difference from Top affecting i) ------*/
		if(r>or && c==oc)
		{
			for(int x=or+1;x<=r;x++)
			{
				if(x==r)   //if testing x becomes equal to recent r then validate
				{
					valid("ROOK:Top -- Captures");
					return true;
				}				
				if(LocalBoard[x][c]!=-1)
				{
					error("ROOK:Top");
					return false;
				}
			}
		}

		//---- BOTTOM (difference from Bottom)------
		if(r<or && c==oc)
		{
			for(int x=or-1;x>=r;x--)
			{
				if(x==r)   //if testing x becomes equal to recent r then validate
				{
					valid("ROOK:Bottom");
					return true;
				}				
				if(LocalBoard[x][c]!=-1)
				{
					error("ROOK:Bottom");
					return false;
				}
			}
		}

		//---- LEFT (difference from Left affecting j)------
		if(r==or && c<oc)
		{
			for(int x=oc-1;x>=c;x--)
			{
				if(x==c)   //if testing x becomes equal to recent c then validate
				{
					valid("ROOK:Left");
					return true;
				}
				if(LocalBoard[r][x]!=-1)
				{
					error("ROOK:Left");
					return false;
				}
			}
		}

		//---- RIGHT (difference from Right)------
		if(r==or && c>oc)
		{
			for(int x=oc+1;x<=c;x++)
			{
				if(x==c)   //if testing x becomes equal to recent c then validate
				{
					valid("ROOK:Right");
					return true;
				}				
				if(LocalBoard[r][x]!=-1)
				{
					error("ROOK:Right");
					return false;
				}
			}
		}
		return false;
	}
	/**This method checks the validity of a QUEEN Move of current Player.
	*It takes String variable as moveDetail and returns a boolean variable.
	*It combines the Move Logic of ROOK and BISHOP and performs an OR Operation.
	*If the move is a valid one it returns TRUE,
	*else it returns FALSE.
	*/
	public boolean queenMove(String s) //String s contains or , oc, r, c
	{
		extract(s);
		//System.out.println("Testing in queenMove:"+or+","+oc+"--->"+r+","+c);
		if(rookMove(s) || bishopMove(s))
		{
			valid("Queen");
			return true;
		}
		error("Queen");
		return false;
	}
	/**This method checks the validity of a KING Move of current Player.
	*It takes String variable as moveDetail and returns a boolean variable.
	*It three special case. (Normal Move in 8 directions, Left-Castling and Right-Castling).
	*If the move is a valid one it returns TRUE,
	*else it returns FALSE.
	*/
	public boolean kingMove(String s) //String s contains or, oc, r, c
	{
		extract(s);
		//System.out.println("Testing in King:"+or+","+oc+"--->"+r+","+c);
		if((r==or+1 && c==oc) || (r==or+1 && c==oc+1)  || (r==or && c==oc+1)  || (r==or-1 && c==oc+1)  || (r==or-1 && c==oc)  || (r==or-1 && c==oc-1)  || (r==or && c==oc-1)  || (r==or+1 && c==oc-1))
		{
				//condition for check will and danger will be checked before hand
				if(LocalBoard[r][c]!=-1)
				{
					valid("KING: -- Captures");
				}
				else
				{
					valid("KING:");
				}
				return true;
		}
		
		//Code for Castling begins......
		//Code for Right Castling....
		if(!ChessBoard.CHECK) //to avoid castling when king is under CHECK
		{
			Check ch=new Check();
			if(ChessBoard.isKingFresh)
			{
				//castling right side and Check whether rook is fresh AND whether square skipped by king is safe.
				if(or==0 && c==oc+2 && ChessBoard.isRightRookFresh && (!ch.isKingInAttack(LocalBoard,or,oc+1))) 
				{
					for(int i=1;i<=(7-oc-1);i++)
					{
						if(LocalBoard[0][oc+i]!=-1)
						{
								error("KING:Invalid Right Castling Attempt!! There are peices in between");
								return false;
						}
					}
					return true;
				}
				//Code for Left Castling.....
				//castling left side and Check whether rook is fresh AND whether square skipped by king is safe.
				else if(or==0 && c==oc-2 && ChessBoard.isLeftRookFresh && (!ch.isKingInAttack(LocalBoard,or,oc-1)))
				{
					for(int i=(oc-1);i>=1;i--)
					{
						if(LocalBoard[0][oc-i]!=-1)
						{
								error("KING:Invalid Left Castling Attempt!! There are peices in between");
								return false;
						}
					}
					return true;
				}
				else
				{
					error("KING:Invalid Castling Attempt!! Either Rook has Moved or King Move is Invalid or Square your trying skip is under attack!!");
					//System.out.println("KING:Invalid Castling Attempt!! Either Rook has Moved or King Move is Invalid or Square your trying skip is under attack!!");
					return false;
				}
			}
		}
		error("KING:");
		return false;
	}

	/**It initiates the Validity of any move and invokes other member methods corresponding to the piece type.
	*It takes peice type as argument along with String containing moveDetails.
	*It returns TRUE if it is legal move,
	*else it returns FALSE.
	*/
	boolean validateAsPerRules(byte p,String s)
	{
		//System.out.println("Inside Class Rules:validateAsPerRules -- value of p is:"+p);
		switch(p)
		{
			/*----------For Black Pawn-----------*/			
			case 0:
				if(pawnMoveB(s))
				{
					//System.out.println("pawnMoveB validated.");
					return true;
				}
				else
					return false;
			/*----------For White Pawn-----------*/
			case 10:
				if(pawnMoveW(s))
				{
					//System.out.println("pawnMoveW validated.");
					return true;
				}
				else
					return false;
			/*----------For Bishops-----------*/
			case 3:
			case 13:
				if(bishopMove(s))
				{
					//System.out.println("BishopMove validated.");
					return true;
				}
				else
					return false;
			/*----------For Knights-----------*/		
			case 2:
			case 12:
				if(knightMove(s))
				{
					//System.out.println("knightMove validated.");
					return true;
				}
				else
					return false;
			/*----------For Rooks-----------*/		
			case 1:
			case 11:
				if(rookMove(s))
				{
					//System.out.println("rookMove validated.");
					return true;
				}
				else
					return false;
			/*-----------For Queens-----------*/
			case 4:
			case 14:
				if(queenMove(s))
				{
					//System.out.println("QueenMove validated.");
					return true;
				}
				else
					return false;
			/*-----------For Kings-----------*/
			case 5:
			case 15:
				if(kingMove(s))
				{
					//System.out.println("KingMove validated.");
					return true;
				}
				else
					return false;
			default:
				return true;
		}
	}	//end of validateAsPerRules
	void extract(String s)
	{
		StringTokenizer st=new StringTokenizer(s,"`:`");
		or=Integer.parseInt(st.nextToken());
		oc=Integer.parseInt(st.nextToken());
		r=Integer.parseInt(st.nextToken());
		c=Integer.parseInt(st.nextToken());
	}	
	void error(String s)
	{
		//System.out.println(s+" -- Illegal Move!!");
	}
	void valid(String s)
	{
		//System.out.println(s+" -- Moved:)");
	}
}

/*Code for ReverseBoard
class ReverseBoard
{
	public ReverseBoard()
	{
		int i=0,j=0;
		byte temp=0;
		for(i=0;i<=3;i++)
		{
			for(j=0;j<=7;j++)
			{
				temp=ChessBoard.Board[i][j];
				ChessBoard.Board[i][j]=ChessBoard.Board[7-i][7-j];
				ChessBoard.Board[7-i][7-j]=temp;
			}
		}
	}
}
End of ReverseBoard*/	

/**This Class performs the operations of establishing and maintaining client connection with Server.
*It sends and receives message to and from the Server
*/
class PortTalk extends Thread implements Constants
{
	Socket connection;
	DataOutputStream outStream;
	PrintWriter pw;
	BufferedReader inStream;
	boolean flag=false;
	boolean opponentNameSend = false;
	static String OpponentName="";
	static String PlayerName="";

	String localname;
	String destname;

	/**This constructor establishes connection with the Server at specified Port and Address and initializes various input and output stream variables.
	*It takes two arguments, first one being an array of String containing the Address and Port, second being String of PlayerName.
	*Once the Connection is established it starts the Thread and keeps watch on the input and output streams.
	*/
        public PortTalk(String args[], String PlayerName)
        {
		super();
		System.out.println("Hello");
		if (args.length != 2)
                       	error("usage : java client host port");
                
           //Getting the HostName from the client (See ChessBoard Applet init())

            String destination = args[0];
            int port = 0;

		//Getting the port number from the client (See ChessBoard Applet init())
             try
             {
        	     port = Integer.valueOf(args[1]).intValue();
             }
             catch(NumberFormatException e)
             {
            	 error("Invalid number port");
             }
                try
                {
                        connection = new Socket(destination, port);

				/*COde for Passing Username and IPAddress with other information to Server*/

                        InetAddress strAdd[] = InetAddress.getAllByName(destination);
                }
                catch(UnknownHostException ex)
                {
                        error("Unknown Host");
                }
                catch(IOException ex)
                {
                        error("IO error creating socket");
                }
                try
                {
                        inStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                        outStream = new DataOutputStream(connection.getOutputStream());
						pw = new PrintWriter(connection.getOutputStream(), true);
                }
                catch(IOException ex)
                {
                        error("IO error getting streams");
                }
		displayDestinationParameters();
		displayLocalParameters();


                System.out.println("Connected to " + destination + " at port " +  port);      

		//Start reading thread
		sendData(PlayerName+"§Opponent");
		System.out.println("get started");
		start();
		System.out.println("After Calling Start.");
	}

	/**This is invoked when start method is called by PortTalk constructor.
	*It performs runs an indefinite loop to receive and send messages until the player disconnects.
	*It invokes msgReceive method and initiateChanges method.
	*/
	public void run()
	{
		System.out.println("Inside run.");
		while(currentThread().isAlive())
		{
			//System.out.println("Before Receive");
			String str = msgReceive();
			try
			{
				sleep(100);
			}
			catch(InterruptedException it)
			{	
				
			}
			if(str.length() != 0 && flag)
			{
				System.out.println("In Run "+ str);
				System.out.println("Before Initiate Changes");
				initiateChanges(str);
				System.out.println("Hello");
				//earlier it was Client.txtMessage
				//ChessBoard.txtMessage.setText(ChessBoard.txtMessage.getText() + str); 
				flag=false;
			}
		}
	}
	/**This method sends messages to the Server.
	*It takes a String variable as an argument.
	*/
        public void sendData(String sendLine)
        {
                BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in));
                boolean finished = false;
				sendLine="" + sendLine;
				try
				{
					pw.println(sendLine);
				}
				catch(Exception e)
				{
					System.out.println(e);
				}
				System.out.println("Message Sent:"+sendLine);
        }  
        /**This method performs the reading of String from inStream variable.
        *It is invoked by the run method.
        */
	public String msgReceive()
	{
			//System.out.print("***");
			try
			{
				//System.out.println("Does program comes here");
				if (inStream.ready())
				{
					String msgRec =  new String();
					msgRec = inStream.readLine();
					flag=true;
					return msgRec;
				}
				//System.out.println("After program comes here");
			}
			catch(Exception e)
			{
				currentThread().stop();
				System.out.println("Exception is: " + e);
			}
			return "";
		
	}
	/**This method initiates the Changes when message is received.
	*It takes String variable as argument, which contains a special field seperators.
	*It is invoked by the run method.
	*/
	public void initiateChanges(String msg)
	{
		performOperations(getOnlyMsg(msg),getSourceOfMsg(msg));
	}
	
	/**It extracts the Source of Message from the String by dividing the String using field seperators.
	*/
	public String getSourceOfMsg(String s)
	{
		//Alt+0172: ¬ , Alt+0178: ²,Alt+0179: ³, Alt+21: §
		StringTokenizer st=new StringTokenizer(s,"§");
		String Text=st.nextToken();
		String Source=st.nextToken();
		System.out.println("Source is:" + Source);
		return Source;
	}
	/**It extracts the only the Message from String by dividing the String using field seperators.
	*/
	public String getOnlyMsg(String s)
	{
		//Alt+0172: ¬ , Alt+0178: ²,Alt+0179: ³, Alt+21: §
		StringTokenizer st=new StringTokenizer(s,"§");
		String Text=st.nextToken();
		String Source=st.nextToken();
		System.out.println("Message is:" + Text);
		return Text;
	}
	
	/**This method performs various operations on the basis of the Source of Message.
	*It takes two String variables as arguments, one the Message and other the Source.
	*Once the Source is known it performs operations accordingly; like Send, Server, LogOff, RCastling, LCastling, Promotion etc.
	*/
	public void performOperations(String m,String s)
	{
		if(s.equals("Board"))
		{
				int or=-1,oc=-1,r=-1,c=-1;
				StringTokenizer st=new StringTokenizer(m,"``,:,`");
				String a=st.nextToken();
				or=Integer.parseInt(st.nextToken());
				oc=Integer.parseInt(st.nextToken());
				r=Integer.parseInt(st.nextToken());
				c=Integer.parseInt(st.nextToken());
				
				//condition to check whether it was en-passant
				//checking whether Pawn Moved.
				if(ChessBoard.Board[7-or][7-oc]==0)
				{
					System.out.println("Opponent Moved Pawn");
					if( ((r==or+1 && c==oc-1) || (r==or+1 && c==oc+1)) && r==5)
					{
						System.out.println("Opponent performed en-passant!!");
						if(ChessBoard.Board[7-r][7-c]==-1) //found to be empty where the opponent pawn attacked diagonally
						{
							System.out.println("Updating Board after en-passantt!!");
							ChessBoard.Board[7-r+1][7-c]=-1;
						}
					}
				}
						
				
				//perform code for reversing the Moves (Since white is below always)
				ChessBoard.Board[7-r][7-c]=ChessBoard.Board[7-or][7-oc];
				ChessBoard.Board[7-or][7-oc]=-1;
				
				//evaluated to be used for en-passant.
				ChessBoard.previousMoveOFOpponent=(7-or) + "`" + (7-oc) + ":" + (7-r) + "`" + (7-c);
				
				//Once opponent has made move enable the opponentMoved of current players Board
				ChessBoard.opponentMoved=true;
				movehistory.append("\n" + OpponentName + " \t" + a + "\t" + (7-or) + "," + (7-oc) + "\t" + (7-r) + "," + (7-c));
		}				
		if(s.equals("Send"))
		{
			System.out.println("Before Send in Operations");
			txtMessage.append("\n"+m);
		}
		if(s.equals("Server"))
		{
			System.out.println("Inside performOperations and s=Server");
			if(m.equals("b"))
			{
				System.out.println("If m=b");
				//means the current players peice color is BLACK false means BLACK
				ChessBoard.PlayerColor=false; 				
				//Changing the Peices of the White to Black since new player is Having Black as per Server.
				for(int i=0; i<=5;i++)
				{
					Image temp;
					temp=Pieces[0][i];
					Pieces[0][i]=Pieces[1][i];
					Pieces[1][i]=temp;
				}
				
				//To reverse the Position of Black and White King and Queen
				// Rest peices are symmterical except Queen and King position
				
				ChessBoard.Board[0][3]=15; //for BLACK King
				ChessBoard.Board[0][4]=14; //for BLACK Queen
				ChessBoard.Board[7][3]=05; //for WHITE King
				ChessBoard.Board[7][4]=04; //for WHITE Queen
				movehistory.append("\nWhite to Start");
				movehistory.append("\nPLAYER\tPIECE\tFROM\tTO");
			}
			// to enable making Move as soon as the player is found
			if(m.equals("w"))
			{
				//Once it has been decided that current players peice is White enable startPlay
				//Do NOT CHANGE as Moves Start from here onwards alternately
				ChessBoard.startPlay=true;
				movehistory.append("\nWhite to Start");
				movehistory.append("\nPLAYER\tPIECE\tFROM\tTO");
			}
			if(m.equalsIgnoreCase("Kill"))
			{
				ChessBoard.startPlay = false;
				ChessBoard.playerNameFound = false;
				OpponentName = "";
				ChessBoard.gameEnded = true;				
			}
		}
		if(s.equals("Logoff"))
		{
			txtMessage.append(m);
		}
		//Code for Castling to be updated in the opponents Board
		if(s.equals("RCastling"))
		{
			if(m.equals("o-o")) //short castling
			{
				ChessBoard.opponentMoved=true;
				movehistory.append("\n" + OpponentName + " \t\t"+"o-o");
				ChessBoard.Board[7-0][7-6]=ChessBoard.Board[7-0][7-4]; //King from 0,4 to 0,6
				ChessBoard.Board[7-0][7-5]=ChessBoard.Board[7-0][7-7]; //Rook from 0,7 to 0,5
				ChessBoard.Board[7-0][7-4]=ChessBoard.Board[7-0][7-7]=-1; //making previous position blank
			}
			else if(m.equals("o-o-o")) //long castling
			{
				ChessBoard.opponentMoved=true;
				movehistory.append("\n" + OpponentName + " \t\t"+"o-o-o");
				ChessBoard.Board[7-0][7-5]=ChessBoard.Board[7-0][7-3]; //King from 0,3 to 0,5
				ChessBoard.Board[7-0][7-4]=ChessBoard.Board[7-0][7-7]; //Rook from 0,7 to 0,4
				ChessBoard.Board[7-0][7-3]=ChessBoard.Board[7-0][7-7]=-1; //making previous position blank
			}
			ChessBoard.opponentMoved=true;
		}
		if(s.equals("LCastling"))
		{
			if(m.equals("o-o")) //short castling
			{
				ChessBoard.opponentMoved=true;
				movehistory.append("\n" + OpponentName + " \t\t"+"o-o");
				ChessBoard.Board[7-0][7-1]=ChessBoard.Board[7-0][7-3]; //King from 0,3 to 0,1
				ChessBoard.Board[7-0][7-2]=ChessBoard.Board[7-0][7-0]; //Rook from 0,0 to 0,2
				ChessBoard.Board[7-0][7-3]=ChessBoard.Board[7-0][7-0]=-1; //making previous position blank
			}
			else if(m.equals("o-o-o")) //long castling
			{
				ChessBoard.opponentMoved=true;
				movehistory.append("\n" + OpponentName + " \t\t"+"o-o-o");
				ChessBoard.Board[7-0][7-2]=ChessBoard.Board[7-0][7-4]; //King from 0,4 to 0,2
				ChessBoard.Board[7-0][7-3]=ChessBoard.Board[7-0][7-0]; //Rook from 0,0 to 0,3
				ChessBoard.Board[7-0][7-4]=ChessBoard.Board[7-0][7-0]=-1; //making previous position blank
			}
		}
		if(s.equals("Promotion"))
		{
			int or=-1,oc=-1,r=-1,c=-1,promotedto=-1;
			StringTokenizer st=new StringTokenizer(m,"``,:,`:");
			String a=st.nextToken();
			or=Integer.parseInt(st.nextToken());
			oc=Integer.parseInt(st.nextToken());
			r=Integer.parseInt(st.nextToken());
			c=Integer.parseInt(st.nextToken());
			promotedto=Integer.parseInt(st.nextToken());
			//to convert it to the opponent peice code we need to subtract 10.
			promotedto=promotedto-10;
			ChessBoard.Board[7-or][7-oc]=-1; //empty the old square.
			ChessBoard.Board[7-r][7-c]=(byte)promotedto; //placing the promoted peice to.
			ChessBoard.opponentMoved=true;
			movehistory.append("\n" + OpponentName + " \t" + a + "\t" + (7-or) + "," + (7-oc) + "\t" + (7-r) + "," + (7-c));
		}
		if(s.equals("Opponent"))
		{
			OpponentName = m;
			if(!opponentNameSend)
			{
				sendData(PlayerName+"§Opponent");			
				opponentNameSend = true;				
			}
		}
	}

	/**This method displays the Destination Parameters i.e the Information of the Server.
	*It invokes the displayParameters method.
	*/
        public void displayDestinationParameters()
        {
                InetAddress destAddress = connection.getInetAddress();
                destname = destAddress.getHostName();
                byte ipAddress[] = destAddress.getAddress();
                int port = connection.getPort();
                displayParameters("Destination ",destname,ipAddress, port);
        }
	/**This method displays the Local Parameters i.e the Information of the Player.
	*It invokes the displayParameters method.
	*/
        public void displayLocalParameters()
        {
                InetAddress localAddress = null;
                try
                {
                        localAddress = InetAddress.getLocalHost();
                }
                catch(UnknownHostException ex)
                {
                        error("Error getting local host");
                }
                localname = localAddress.getHostName();
                byte ipAddress[] = localAddress.getAddress();
                int port = connection.getLocalPort();
                displayParameters("Local ", localname, ipAddress, port);              
        }
	/**It displays the formated String passed as input.
	*It receives four arguments, String Host, String Name, byte array of ipAddress and int Port.
	*/
        public void displayParameters(String s, String name, byte ipAddress[], int port)
        {
                System.out.println(s+" host is:"+ localname + ".");
                System.out.print(s+" IP Address is:");             
                for(int i = 0; i < ipAddress.length; ++i  )
			System.out.print((ipAddress[i]+256)%256 + ".");
                System.out.println();
                System.out.println(s+" port number is "+port+".");
        }
        /**This method performs the shutdown operation.
        */
        public void shutdown()
        {
                try
                {
                        connection.close();
                }
                catch(IOException ex)
                {
                        error("IO error closing socket");
                }
        }
        /**This method is invoked whenever an error is accepted.
        *It takes a String as argument. That is the Error Message.
        */
	public void error(String s)
        {
                System.out.println(s);
                System.exit(1);
        }
        /**This method is used to return localname as String to the calling method.
        */
        public String getLocalname()
        {
        	return localname;
        }
}
/*End of Coding of PortTalk which is used in CLIENT*/

/**This class performs the operations of formatting of Dialog Box displayed at the time of Pawn Promotion.
*It extend the Dialog class and implements appropriate Listeners.
*/
class promotionDialog extends Dialog implements ActionListener,MouseListener,MouseMotionListener, Constants
{
    Button btnKnight, btnBishop,btnRook,btnQueen;
    /**The constructor passes parameters to the super class constructor i.e Dialog.
    *It accepts two argument first is the Frame object and second is a String for title.
    */
    public promotionDialog(Frame CB, String title)
    {
    	super(CB,title,true);
	System.out.println("Inside promotionDialog()..");
	setLayout(new FlowLayout());
    	setSize(220,120);
    	btnRook=new Button("ROOK");
    	btnKnight=new Button("KNIGHT");
    	btnBishop=new Button("BISHOP");
    	btnQueen=new Button("QUEEN");
    	/*add(btnKnight);
    	add(btnBishop);
    	add(btnRook);
    	add(btnQueen);
	btnRook.addActionListener(this);
	btnKnight.addActionListener(this);
	btnBishop.addActionListener(this);
	btnQueen.addActionListener(this);*/
       	addMouseMotionListener(this);
	addMouseListener(this);
	repaint();
   }
   /**It performs the paint operations
   *It accept a Graphics Object as argument.
   *It draws, fill rectangles and drawImage.
   */
   public void paint(Graphics g)
   {
   	for(int j=0;j<=3;j++)
   	{
   		g.setColor(new Color(0,0,0));
   		g.drawRect(j*SIDE+15,SIDE,SIDE,SIDE);
   		g.setColor(new Color(128,128,128));   		
   		g.fillRect(j*SIDE+15,SIDE,SIDE,SIDE);
   	}
	g.drawImage(Pieces[1][1],0*SIDE+15+9,(SIDE+10),this);
	g.drawImage(Pieces[1][2],1*SIDE+15+8,(SIDE+7),this);
	g.drawImage(Pieces[1][3],2*SIDE+15+6,(SIDE+6),this);
	g.drawImage(Pieces[1][4],3*SIDE+15+6,(SIDE+6),this);
  }
	/**It keeps track peice clicked by the current Player.
	*/
	public void actionPerformed(ActionEvent ae)
    	{
        	String s = ae.getActionCommand();
        	if(s.equals("KNIGHT"))
		{
			ChessBoard.peicepromotedto=12;
			dispose();
        	}
        	else if(s.equals("BISHOP"))
        	{
			ChessBoard.peicepromotedto=13;
			dispose();
        	}
	        else if(s.equals("ROOK"))
        	{
			ChessBoard.peicepromotedto=11;
			dispose();
	        }
        	else if(s.equals("QUEEN"))
        	{
			ChessBoard.peicepromotedto=14;
			dispose();
        	}
	}
	/**This method is Overridden since MouseListener is implemented.*/
	public void mouseClicked(MouseEvent me)
    	{
	}
	/**This method is Overridden since MouseListener is implemented.*/
	public void mouseDragged(MouseEvent me)
	{
	}
	/**This method is Overridden since MouseListener is implemented.*/	
	public void mouseEntered(MouseEvent me)
	{
	}
	/**This method is Overridden since MouseListener is implemented.*/	
	public void mouseExited(MouseEvent me)
	{
	}
	/**This method is Overridden since MouseListener is implemented.*/	
	public void mouseMoved(MouseEvent me)
	{
    	}
	/**This method is Overridden since MouseListener is implemented.
	*It performs the Operation on mousePressed. It assigns the code of the piece clicked by the user and disposes the Dialog window.
	*/
    	public void mousePressed(MouseEvent me) 
    	{
		int x,y;
		x=me.getX();
		y=me.getY();
		if(x>=(0*SIDE+15) && x<=((0*SIDE+15)+SIDE) && (y>=SIDE && y<=SIDE*2) )
		{
			ChessBoard.peicepromotedto=11;
			dispose();
		}
		if(x>=(1*SIDE+15) && x<=((1*SIDE+15)+SIDE) && (y>=SIDE && y<=SIDE*2)  )
		{
			ChessBoard.peicepromotedto=12;
			dispose();
		}
		if(x>=(2*SIDE+15) && x<=((2*SIDE+15)+SIDE) && (y>=SIDE && y<=SIDE*2) )
		{
			ChessBoard.peicepromotedto=13;
			dispose();
		}
		if(x>=(3*SIDE+15) && x<=((3*SIDE+15)+SIDE) && (y>=SIDE && y<=SIDE*2)  )
		{
			ChessBoard.peicepromotedto=14;
			dispose();
		}
    	}
	/**This method is Overridden since MouseListener is implemented.*/    	
    	public void mouseReleased(MouseEvent me)
    	{
    	}
}

interface Constants
{
	int userCount=2;
	/*Used in StartBoard Class */
	
        public static final int N = 8; 
        //Matrix which stores the Board Position of all pieces on the Board with
        // the respective square denoting the [row][column]=pieceCode

	/* Used in ChessBoard Class*/
	TextArea movehistory= new TextArea("<--Move History-->",7,25,TextArea.SCROLLBARS_VERTICAL_ONLY); 
	static int SIDE=50;
	Color boardColor=new Color(128,128,128);
	public static Font fplain=new Font("Dialog",Font.PLAIN,12);
	public static Font fbold=new Font("Dialog",Font.BOLD,12);
        String Developers = "Developed by Aditya, Ajay and Vijay, BIT, Mesra Ranchi - MCA IV.";

        public static Image Pieces[][] = new Image[2][6];
        /*
        	00 - BPawn, 01 - BRook, 02 - BKnight, 03 - BBishop, 04 - BQueen,  05 - BKing;
        	10 - WPawn, 11 - WRook, 12 - WKnight, 13 - WBishop, 14 - WQueen,  15 - WKing;
        */
         
         // Used by CLIENT
	TextArea txtMessage	= new TextArea("<--Welcome to the Chess-cum-Chat Applet-->",4,30,TextArea.SCROLLBARS_VERTICAL_ONLY); 
}
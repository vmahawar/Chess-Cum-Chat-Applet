import java.lang.System;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.*;

public class ReverServerApp
{
        public static void main(String args[])
        {
			try
			{
	           	ServerSocket server = new ServerSocket(1234);
			while(true)
			{
				new instanceThread(server).start();
			}
			}
			catch(IOException io)
			{
				System.out.println(io);
			}
        }
}

class instanceThread extends Thread
{	
	static Vector v = new Vector();
	static Vector vstatus = new Vector();
	static Vector vOpponent =  new Vector();
	int localport;
	int destport;
	ServerSocket server;
	Socket client;
	String destname; 
	boolean finished;
	BufferedReader lstream,inStream; 
	PrintWriter outStream; 
	ClientInfo clinfo;
	String compinfo;
	/* Initialisation settings	 */
	instanceThread(ServerSocket server)
	{		
			try
			{		
					this.server = server;
					localport = this.server.getLocalPort();
					System.out.println("Reverse Server is listening on port "+ localport);
					client = this.server.accept();
					//Addming Player to the Server in 'v' and the status of the player in 'vstatus'
					v.addElement(client);
					System.out.println(client);
					destname = ((Socket)v.lastElement()).getInetAddress().getHostName();
					destport = ((Socket)v.lastElement()).getPort();
					System.out.println("Accepted " + v.size() + " connection to "+ destname + " on port "+ destport);
					//System.out.println("Thread activeCount:" + Thread.activeCount());
					//System.out.println("Details of Current Thread:"+Thread.currentThread());
					vstatus.addElement("I");
					int opponentIndex = searchOpponent();
					initilizeBoard(opponentIndex, v.size() - 1 ); // as the last player index of 10 player is 9
					summery();
			}
			catch(IOException ex)
			{
					System.out.println("IOException Occured. ");
			}	
	}
	public void summery()
	{
		for(int i = 0 ; i < v.size() ; i++)
		{
			System.out.println("Player : " + v.elementAt(i) + ", Status : " + vstatus.elementAt(i) + ", Opponent : " + vOpponent.elementAt(i) );
		}
	}
	
	public int searchOpponent()
	{
		for(int i = 0 ; i < v.size() - 1 ; i++)
		{
				if(vstatus.elementAt(i) == "I")
				{
					return i;
				}
		}
		vOpponent.addElement(null);
		return -1;
	}
	
	public void initilizeBoard(int i, int j) 
	{
		if(i != -1)
		{
			vstatus.setElementAt("P",i);
			vOpponent.setElementAt((Socket)v.lastElement(),i);
			vstatus.setElementAt("P", j);
			vOpponent.setElementAt((Socket)v.elementAt(i), j );
			new sendToCl(((Socket)v.elementAt(i)), "Opponent Found. Start Game."+"§Send").start();
			new sendToCl(((Socket)v.elementAt(i)), "w"+"§Server").start();
			new sendToCl(((Socket)v.elementAt(j)), "Opponent Found. Start Game."+"§Send").start();
			new sendToCl(((Socket)v.elementAt(j)), "b"+"§Server").start();					
		}
		else
		{
			vOpponent.addElement(null);
			new sendToCl(((Socket)v.lastElement()), "No Opponent is Found please wait" + "§Send").start();
		}
		
	}
	
	
	public void run()
	{
		Socket current = client;
		int playerIndex = v.indexOf(client);
		System.out.println("PlayerIndex is " + playerIndex);
		try
		{

			String inLine = new String();
			inStream = new BufferedReader(new InputStreamReader(current.getInputStream()));
			while(true)
			{
					//System.out.println("Client is " + client);
					if(inStream.ready())
					{
						inLine = inStream.readLine();
						String source = getSourceOfMsg(inLine);
						String txt = getOnlyMsg(inLine);
						if(source.equals("Logoff"))
						{
							removePlayer(current);
							break;	
						}
				//		for(int j = 0; j < v.size(); j++)
				//		{
				//			if(((Socket)v.elementAt(j))!=current)
							if((vOpponent.elementAt(playerIndex)) != null)
							{
								new sendToCl(((Socket)vOpponent.elementAt(playerIndex)), inLine).start();
							}
					//	}
					}
					try
					{
						sleep(100);
					}
					catch(InterruptedException ie)
					{
					}

			}
		}
		catch(IOException io)
		{
				removePlayer(current);
		}
	}
	public String getSourceOfMsg(String s)
	{
		//Alt+0172: ¬ , Alt+0178: ²,Alt+0179: ³, Alt+21:
		//System.out.println("Before st in getSourceOfMsg()" + s);
		StringTokenizer st=new StringTokenizer(s,"§");	
		String Text=st.nextToken();
		String Source=st.nextToken();
		System.out.println("Source is:" + Source);
		return Source;
	}
	public String getOnlyMsg(String s)
	{
		//Alt+0172: ¬ , Alt+0178: ²,Alt+0179: ³, Alt+21: §
		//System.out.println("Before st in getOnlyMsg()");		
		StringTokenizer st=new StringTokenizer(s,"§");
		String Text=st.nextToken();
		String Source=st.nextToken();
		System.out.println("Message is:" + Text);
		return Text;
	}

	public void removePlayer(Socket current)
	{
		//System.out.println("Client Disconnected:" + io);
		//Loop  continues endlessly so to avoid it the following code is generated
		System.out.println("Client " + client + " Disconnected!!");
		//System.out.println("Thread activeCount:" + Thread.activeCount());
		//System.out.println("Details of Current Thread:"+Thread.currentThread());	
		try
		{
			current.close();
			//new sendToCl(((Socket)vOpponent.elementAt(playerIndex)),"").start();
			
			int playerIndex = vOpponent.indexOf(current);
			int leavingPlayerIndex = v.indexOf(current);
			System.out.println("index  of Leaving PLayer in Existing Player: " + playerIndex + " leaving Player index : " + leavingPlayerIndex);

			if(playerIndex == -1) 
			{
				vstatus.removeElementAt(leavingPlayerIndex);
				vOpponent.removeElementAt(leavingPlayerIndex);
				v.removeElementAt(leavingPlayerIndex);			
	// 			ClientInfo.Clients.remove(current);			
				summery();
			}
			else
			{
				new sendToCl(((Socket)v.elementAt(playerIndex)),"Opponent has left. Game Ended. Start again to play."+"§Send").start();
				new sendToCl(((Socket)v.elementAt(playerIndex)),"Kill"+"§Server").start(); 
				vstatus.setElementAt("I",playerIndex);
				vOpponent.setElementAt(null,playerIndex);

				//Remove the details of leaving player. as well as its opponent from the plays
				v.removeElementAt(leavingPlayerIndex);
				vstatus.removeElementAt(leavingPlayerIndex);
				vOpponent.removeElementAt(leavingPlayerIndex);
				v.removeElementAt(playerIndex);
				vstatus.removeElementAt(playerIndex);
				vOpponent.removeElementAt(playerIndex);

			}
//				summery();
		}
		catch(Exception e)
		{
		}
//		catch)

	}
	public void sendMessage(Socket cl, String inLine)
	{
		try
		{
			outStream = new PrintWriter(cl.getOutputStream(),true);
			//System.out.println("Received " + inLine);
			String outLine = new ReverseString(inLine.trim()).getString();
			outStream.println(outLine + "|");
			//System.out.println("Sent " + outLine + " to " + cl);
		}
		catch(IOException io)
		{
			System.out.println(io);
		}
	}
	public void showClient()
	{	
		System.out.println("List of clients");
		for(int i = 0; i < v.size(); i++)
		{
			System.out.println("Client " + i +" is " + v.elementAt(i));
		}
	}
	/*pubilc void setClientInfo(String str)
	{
		StringTokenizer st=new StringTokenizer(str,":");
		while(st.hasMoreTokens())
		{
			st.nextToken()
		}
	}*/
	public void getClientInfo()
	{
	
	}
}

class sendToCl extends Thread
{
	PrintWriter pw;
	Socket dest;
	String msg;
	sendToCl(Socket dest, String msg)
	{
		this.dest = dest;
		try
		{
			pw = new PrintWriter(dest.getOutputStream(), true);
			//System.out.println(msg);
		}
		catch(Exception e)
		{
		}
		this.msg = msg;				
	}
	
	public void run()
	{
		pw.println(msg);
	}
}

class ReverseString
{
        String s;
        public ReverseString(String in)
        {
                int len = in.length();
                char outChars[] = new char[len];
                for(int i = 0; i < len ; i++)
                {
                        outChars[len - 1 - i]  = in.charAt(i);
                }
                s = String.valueOf(outChars);
        }

        public String getString()
        {
                return s;
        }
}
class ClientInfo
{
	static Hashtable Clients=new Hashtable();
	Socket sock;
	ClientInfo(Socket cl, String clcompinfo)
	{
		Clients.put(cl,clcompinfo);
		System.out.println("New Client Added:)");
	}
	public void getClientInfo(Socket cl)
	{
		Enumeration names;
		names = Clients.keys();
		while(names.hasMoreElements())
		{
			sock=(Socket)names.nextElement();
			System.out.println(sock + " - " + Clients.get(sock));
		}
	}
	public void deleteClientInfo(Socket cl)
	{
		if(!(Clients.isEmpty()))
		{
			System.out.println("Client Removed:-"+ Clients.remove(cl));
		}			
	}
}	

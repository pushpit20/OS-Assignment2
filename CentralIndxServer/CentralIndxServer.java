
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class begin
{
	String filename;          
	int peerid;
	String ipAddress;
}

class PortListener implements Runnable {

	ServerSocket server;
	Socket connection;
	BufferedReader br = null;
	Boolean flag;
	public String strVal;
	int port;
	static int maxsize = 0;
	static begin[] myIndexArray = new begin[9000];           

	public PortListener(int port) {
		this.port = port;
		flag = true;
		strVal = "Waiting For PEER Connection";
	}

	public void run() {
		if(port==2001)                                  
		{
			try {
				server = new ServerSocket(2001);
				while (true) {
					connection = server.accept();			
					System.out.println("Connection Received From " +connection.getInetAddress().getHostName()+ " For Registration");    				   				
					ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
					strVal = (String)in.readObject();
					System.out.println(strVal);
					System.out.println("<====Registered====>\n");
					String[] var;
					var = strVal.split(" ");
					int aInt = Integer.parseInt(var[0]);
					String ipstrtmp = connection.getInetAddress().getHostName();
					for(int x = 1; x < var.length ; x++){

						begin myitem = new begin();
						myitem.filename = var[x];                                 
						myitem.peerid = aInt  ;
						myitem.ipAddress = ipstrtmp;
						myIndexArray[maxsize] = myitem;
						maxsize++;
					}

					in.close();
					connection.close();   				
				}
			} 

			catch(ClassNotFoundException noclass){                                   
				System.err.println("Data Received in Unknown Format");
			}
			catch(IOException ioException){                                          
				ioException.printStackTrace();
			} finally {
			}

		}
		if(port==2002)                                
		{
			try {
				server = new ServerSocket(2002);

				while (true) {
					connection = server.accept();			
					System.out.println("Connection Received From " +connection.getInetAddress().getHostName()+ " For Search");    				   				
					ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
					strVal = (String)in.readObject();
					String retval = "";

					for (int idx =0; idx < maxsize ;idx++)                                           
					{                
						if (myIndexArray[idx].filename.equals(strVal))                            
						{
							retval = retval + myIndexArray[idx].peerid + "("+myIndexArray[idx].ipAddress +")\n\r ";                  
						}	
					} 
					if (retval == "") 
					{
						retval = "File Not Found\n";
					} 
					System.out.println(retval);
					System.out.println("<=====Searched=====>\n");

					ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
					out.flush();			
					out.writeObject(retval);                      
					out.flush();			
					in.close();
					out.close();
					connection.close();   				
				}
			} 

			catch(ClassNotFoundException noclass){                                      
				System.err.println("Data Received in Unknown Format");
			}
			catch(IOException ioException){                                            
				ioException.printStackTrace();
			} finally {
			}

		}		
	}
}


public class CentralIndxServer {

	public CentralIndxServer() {
		RegisterRequestThread();                           
		SearchRequestThread();
	}

	public static void main(String[] args) {

		System.out.println("||========================================================================================||");
		System.out.println("||                           PEER-TO-PEER FILE SHARING SYSTEM                             ||");
		System.out.println("||                       ========================================                         ||");
		System.out.println("||========================================================================================||");
		System.out.println("\n <CENTRAL INDEX SERVER IS UP AND RUNNING....>");
		System.out.println(" ============================================\n");


		CentralIndxServer mainFrame = new CentralIndxServer();

	}
	public void RegisterRequestThread()
	{
		Thread rthread = new Thread (new PortListener(2001));                    
		rthread.setName("Listen For Register");
		rthread.start();
	}
	public void SearchRequestThread()
	{
		Thread sthread = new Thread (new PortListener(2002));                   
		sthread.setName("Listen For Search");
		sthread.start();

	}
}

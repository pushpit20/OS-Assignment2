
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

//PeerServer
class PortListenerSend implements Runnable {

	int port;
	public String strVal;
	Boolean flag;                            
	ServerSocket server;
	Socket connection;
	BufferedReader br = null;

	public PortListenerSend(int port) {
		this.port = port;
		flag = true;
		strVal = "Waiting For PEER Connection";
	}
	public void run() {
		try {
			server = new ServerSocket(port);

			while (true) {                                                                       
				connection = server.accept();			
				System.out.println("Connection Received From " + connection.getInetAddress().getHostName()+" For Download\n");    				   				
				ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
				strVal = (String)in.readObject();

				ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();		

				String str="";

				try
				{
					FileReader fr = new FileReader(strVal);                 
					BufferedReader br = new BufferedReader(fr);		
					String value=new String();
					while((value=br.readLine())!=null)                
						str=str+value+"\r\n";                       
					br.close();
					fr.close();
				} catch(Exception e){
					System.out.println("Cannot Open File");
				}

				out.writeObject(str);
				out.flush();
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
}

public class PeerServer {

	public String CIS_ip = "localhost";       
	public String Clientid = "1001";
	String regmessage,searchfilename;
	ObjectOutputStream out;
	Socket requestSocket;

	public PeerServer() {


		try
		{
			FileReader fr = new FileReader("indxip.txt");
			String val1=new String();
			BufferedReader br = new BufferedReader(fr);	
			val1 = br.readLine();
			System.out.println("IndexServer IP is:" + val1);
			CIS_ip = val1;
			br.close();
			fr.close();
		} catch(Exception e){
			System.out.println("Could not read indexserver ip from indxip.txt");
		}


		System.out.println("||========================================================================================||");
		System.out.println("||                           PEER-TO-PEER FILE SHARING SYSTEM                             ||");
		System.out.println("||                       ========================================                         ||");
		System.out.println("||                                       MENU:                                            ||");
		System.out.println("||========================================================================================||");

		while (true){

			System.out.println("============================================================================================\n");
			System.out.println("Enter The Option :\n==================\n1. Registering the File \n \n2. Searching On CentralIndxServer \n \n3. Downloading From Peer Server \n \n4. Exit\n");	
			Scanner in = new Scanner(System.in);
			regmessage = in.nextLine();
			if (regmessage.equals("1")){

				System.out.println("Enter the String in Format: 4Digit id and File Names separated by Space"); 
				regmessage = in.nextLine();
				String[] val;
				val = regmessage.split(" ");                        
				int PearPort  = Integer.parseInt(val[0]);			
				RegisterWithIServer();                          
				AttendFileDownloadRequest(PearPort);	
			}		
			if (regmessage.equals("2")){
				SearchWithIServer();                         
			}
			if (regmessage.equals("3")){
				DownloadFromPeerServer();                    
			}
			if (regmessage.equals("4")){
				System.out.println("Exiting.");
				System.exit(0);   		
			}
		}
	}

	public static void main(String[] args) {

		PeerServer psFrame = new PeerServer();

	}
	public void RegisterWithIServer()                             
	{
		try{
			requestSocket = new Socket(CIS_ip, 2001);
			System.out.println("\nConnected to Register on CentralIndxServer on port 2001\n");
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();			
			out.writeObject(regmessage);
			out.flush();
			System.out.println("Registered Successfully!!\n");
		}
		catch(UnknownHostException unknownHost){                                            
			System.err.println("Cannot Connect to an Unknown Host!");
		}
		catch(IOException ioException){                                                    
			ioException.printStackTrace();
		} 
		finally{
			try{
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}

	}
	public void SearchWithIServer()                             
	{
		try{
			System.out.println("Enter the File Name to Search");
			Scanner in1 = new Scanner(System.in);                                       
			searchfilename = in1.nextLine();
			requestSocket = new Socket(CIS_ip, 2002);
			System.out.println("\nConnected to Search on CentralIndxServer on port 2002\n");
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();			
			out.writeObject(searchfilename);                                            
			out.flush();
			ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
			String strVal = (String)in.readObject();
			if  (strVal.equals("File Not Found\n")) {

				System.out.println("FILE Does Not Exist !!\n");
			}
			else {
				System.out.println( "File:'"+searchfilename+ "' found at peers:"+strVal+"\n");     
			}		

		}
		catch(UnknownHostException unknownHost){                                           
			System.err.println("Cannot Connect to an Unknown Host!");
		}
		catch(IOException ioException){                                                  
			ioException.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally{
			try{
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}

	public void writetoFile(String s)
	{
		try
		{  
			String fname = searchfilename;
			FileWriter fw = new FileWriter(fname,true);
			fw.write(s);                                      
			fw.close();

		} catch(Exception e){
			System.out.println("");
		}

	}

	public void DownloadFromPeerServer()                           
	{

		System.out.println("Enter Peer id:");                       
		Scanner in1 = new Scanner(System.in);                       
		String peerid = in1.nextLine();

		System.out.println("Enter pear IP Address to download file:");
		String ipadrs = in1.nextLine();
		System.out.println("Enter the File Name to be Downloaded:");      
		searchfilename = in1.nextLine();                             


		int peerid1 = Integer.parseInt(peerid);
		try{

			requestSocket = new Socket(ipadrs, peerid1);
			System.out.println("\nConnected to peerid : "+peerid1+"\n");
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();			
			out.writeObject(searchfilename);
			out.flush();
			ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
			String strVal = (String)in.readObject();
			System.out.println( searchfilename+": Downloaded\n");
			writetoFile(strVal);
		}
		catch(UnknownHostException unknownHost){                                            
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){                                                    

			System.err.println("FILE not Found at the Following PEER !!");      
			System.err.println("Please enter a valid PEER ID!");      
			DownloadFromPeerServer();                   
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally{
			try{
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	public void AttendFileDownloadRequest(int peerid)                                  
	{
		Thread dthread = new Thread (new PortListenerSend(peerid));
		dthread.setName("AttendFileDownloadRequest");
		dthread.start();
	}
}

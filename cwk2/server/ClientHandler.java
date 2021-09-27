package cwk2.server;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cwk2.server.Protocol;
import cwk2.server.ServerConstants;

class ClientHandler implements Runnable{  
	
	private Socket socket = null;  
     
	/**
     * Constructor of ClienrHandler with parameter server socket
     */
	public ClientHandler(Socket socket){  
		this.socket = socket;  
	} 
	
	/**
     * This method inputs a string from client's command
     * @return an String[] type array
     */
     public String[] getArgs(String command) {
    	 String[] args = command.split(" "); 
    	 return args;
     }
     /**
      * This function checks whether a file exists
      * @return flag boolean  False for file not exist  True for file exist
      * */
     public boolean haveFile(String filePath) {
     	File file = new File(filePath);
     	if(file.exists()){
 			return true;
 		}
     	return false;
     }
     /**
      * This method read and send file from filePath
      * @param socket current socket
      * @param filePath file path of the file we want to send.
      * */
     private static void sendFile(Socket socket, final String filePath) {
     	
     	System.out.println("Start sending file:" + filePath);  
 		File file = new File(filePath);
 		
 		int bufferSize = 8192;  
 		byte[] buf = new byte[bufferSize];  
 		try {  
 			DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));  
 			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());  
 				dos.writeUTF(file.getName());  
 				dos.flush();  
 				dos.writeLong(file.length());  
 				dos.flush();  
                 int read = 0;  
                 int passedlen = 0;  
                 long length = file.length();    // Fetch the length of file that need to be transfered.  
                 while ((read = fis.read(buf)) != -1) {  
                 	passedlen += read;  
                 	System.out.println("Sending [" + file.getName() + "]Percentage: " + passedlen * 100L/ length + "%");  
                 	dos.write(buf, 0, read);  
                 }  
                 
                 dos.flush();  
                 fis.close();  
                 dos.close();  
                 
                 System.out.println("File " + filePath + " Transfer is Complete!");  
             } catch (Exception e) {  
             	e.printStackTrace();  
             }  

     }
     /**
      * This method read and send String
      * @param socket current socket
      * @param filePath file path of the file we want to send.
      * */
     private static void sendFileList(Socket socket, final String data) {
     	
     	System.out.println("Start sending file List:");  

 		try {  
 			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
 			dos.writeUTF(data);  
 			dos.flush();  
 			dos.close(); 
                   
            System.out.println("File List Transfer is Complete!");  
        } catch (Exception e) {  
        	e.printStackTrace();  
        }  

     }
     /**
      * This method inputs a string as file path.
      * @return All the files' name in given file path.
      */
     public String getFileList(String path) {
     	
     	String allFileNames = "";
     	
     	File file = new File(path);
     	
     	File[] fileList = file.listFiles();
        
     	if (fileList != null){
     		for (int i = 0; i < fileList.length; i++) {
     			if (fileList[i].isFile()) {
     				String fileName = fileList[i].getName();
 		            System.out.println("File：" + fileName);
 		            allFileNames = allFileNames + " " + fileName;
         		}              
 		    }
 		}
         return allFileNames;
     }
      
	public void run() {  
                  
		System.out.println("New connection accepted " + socket.getInetAddress() + ":" + socket.getPort());  
                  
		// Server data stream for files
		DataInputStream dis = null;  
		DataOutputStream dos = null; 
		
		// Splited user command 
		String[] userCommand = null;
		
		try {
			/* Server side reader and writer for Server-Client communication */
			// Input and output streams to/from the client.
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
			/* Logging */
			File logFile =new File(ServerConstants.LOG_FILE_PATH+"log.txt");	   
			if(!logFile.exists()){
				logFile.createNewFile();
			}
			InetAddress inet = socket.getInetAddress();
			System.out.println("Connection made from " + inet.getHostAddress() );
			String clientIpAddress = inet.getHostAddress().toString();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy:h:m:s aa",Locale.ENGLISH);//set date format
			String date = dateFormat.format(new Date()).toString();
			System.out.println("Date: "+ date);
			
			String data = date+":"+clientIpAddress;
			
				    
			/* Create one instance of protocol per client */
			String inputLine, outputLine;
			Protocol protocol = new Protocol();
			outputLine = protocol.processInput(null);
			out.println(outputLine);
			
			/* Sequential protocol */
			while( (inputLine = in.readLine())!=null ) {
				// Log data
				System.out.println("Request: "+ inputLine);
				data = data + ":"+ inputLine;
				// Write log into file 
				FileWriter fileWriter = new FileWriter(ServerConstants.LOG_FILE_PATH+"log.txt",true);
			    fileWriter.write(data);
			    fileWriter.write(System.getProperty("line.separator"));
			    fileWriter.flush();
			    fileWriter.close();
			    // Get client's command
			    userCommand = getArgs(inputLine);
			    
			    // Check whether required file is exist
			    // Check Server's file
			    if (userCommand[0].equals("get")) {
			    	String tempFilePath = ServerConstants.SEND_SERVER_FILE_PATH + userCommand[1];
			    	if (!haveFile(tempFilePath)) {
			    		outputLine = protocol.processInput("no server source file");
			    	}
			    	else {
			    		// Server speak to client
				    	outputLine = protocol.processInput(userCommand[0]);
			    	}// Check Client's file
			    }else if (inputLine.equalsIgnoreCase("client file not exist")) {
			    	outputLine = protocol.processInput("client file not exist");
			    }else {
			    	// Server speak to client
			    	outputLine = protocol.processInput(userCommand[0]);
				}
				out.println(outputLine);
				System.out.println(outputLine);

				/* Server do preparations for receiving data */
				if (outputLine.equals("Server receiving data")) {
					
					int bufferSize = 8192;  
					byte[] buf = new byte[bufferSize];  
					
					try {  
						// Server Data stream for files
						dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));  
						String savePath = ServerConstants.RECEIVE_FILE_PATH + dis.readUTF();  
						long lengthOfData = dis.readLong();  
						dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(savePath)));          
						
						// Read file data from buffer
						int read = 0;  
						long passedlen = 0;  
						while ((read = dis.read(buf)) != -1) {  
							passedlen += read;  
							dos.write(buf, 0, read);  
							System.out.println("File[" + savePath + "]has been accepted: " + passedlen * 100L/ lengthOfData + "%");  
							}  
						System.out.println("File: " + savePath + " Successfully accepted!");  
					
						
					} catch (Exception e) {  
						e.printStackTrace();  
						System.out.println("Failed to accept file!");  
					}finally{ 
						// Flush & Close buffer data 
						try {  
							if(dos != null){  
								dos.close();  
							}  
							if(dis != null){  
								dis.close();  
							}  
							if(socket != null){  
//								socket.close();  
							}  
						} catch (Exception e) {  
							e.printStackTrace();  
						}  
					}  
				}else if (outputLine.equals("Server sending data")) {
					String sourcePath = ServerConstants.SEND_SERVER_FILE_PATH;
					sendFile(socket,sourcePath+userCommand[1]);
				}else if (outputLine.equals("Server File")) {
					String sourcePath = ServerConstants.SEND_SERVER_FILE_PATH;
					sendFileList(socket,getFileList(sourcePath));
				}
			}
					
				
			// Free up resources for this connection. 
			out.close();
			in.close();
			socket.close();
			
			
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}              

	}
}
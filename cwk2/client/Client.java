package cwk2.client;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.File;  
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;  
import java.util.Random;  
import java.util.Vector;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;

import cwk2.client.ClientConstants;



      
public class Client { 
	
	private String[] applicationServed = { "Server successfully recieve data" };
	
	private static ArrayList<String> fileList = new ArrayList<String>();  
	
	private String sendFilePath = ClientConstants.SEND_FILE_PATH; 
	
	private Socket socket = null;
	
	private PrintWriter socketOutput = null;
	
    private BufferedReader socketInput = null;
    
    private String[] userCommand = null;
    
     
    
          
    /** 
     * Constructor of Client，User set the folder used for file upload 
     * @param filePath 
     */  
    public Client(String filePath){  
    	getFilePath(filePath);  
    }  
          
    /** 
     * Constructor of Client without parameter
     * User default folder for file upload 
     */  
    public Client(){  
    	getFilePath(sendFilePath);  
    }  
            
          
    private boolean createConnection() {
    	
		try {  
			socket = new Socket("localhost", 8888);  
			System.out.println("Successfully connect server！");  
			// Chain a writing stream.
            socketOutput = new PrintWriter( socket.getOutputStream(), true );

            // Chain a reading stream.
            socketInput = new BufferedReader(
            				new InputStreamReader(socket.getInputStream()) );
			return true;  
		} catch( UnknownHostException e ) {
            System.err.println( "Unknown client host." );
            return false;   
        } catch (Exception e) {  
			System.out.println("Failed to connect server.");  
			return false;  
		}
		

	}  

    private void getFilePath(String dirPath){  
    	File dir = new File(dirPath);  
    	File[] files = dir.listFiles();  
    	if(files == null){  
    		return;  
    	}  
    	for(int i = 0; i < files.length; i++){  
    		if(files[i].isDirectory()){  
    			getFilePath(files[i].getAbsolutePath());  
    		}  
    		else {  
    			fileList.add(files[i].getAbsolutePath());  
    		}  
        }  
    }  
//          
//    private Vector<Integer> getRandom(int size){  
//    	Vector<Integer> v = new Vector<Integer>();  
//    	Random r = new Random();  
//    	boolean b = true;  
//    	while(b){  
//    		int i = r.nextInt(size);  
//    		if(!v.contains(i))  
//    			v.add(i);  
//    		if(v.size() == size)  
//    			b = false;  
//            }  
//            return v;  
//    } 
    
    /**
     * This method inputs a string from client's command
     * @return an String[] type array
     */
     public String[] getArgs(String command) {
    	 String[] args = command.split(" "); 
    	 return args;
     }
     /**
      * This method receives file list from server 
      * @param socket  Client socket
      * */
     private static void getFileList(Socket socket) {
     	
     	System.out.println("Start receiving file list");
     	// Client data stream for files
     	DataInputStream dis = null; 		
 		try {  
 			// Client Data stream for files
 			dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));  
 			System.out.println(dis.readUTF());  
 			
 			
 		} catch (Exception e) {  
 			e.printStackTrace();  
 			System.out.println("Failed to accept file list!");  
 		}finally{ 
 			// Flush & Close buffer data 
 			try {  
 				
 				if(dis != null){  
 					dis.close();  
 				}  

 			} catch (Exception e) {  
 				e.printStackTrace();  
 			}  
 		}
     }

    /**
     * This method receives data from server and store into client files
     * */
    private static void getFile(Socket socket) {
    	
    	System.out.println("Start receiving file");
    	// Client data stream for files
    	DataInputStream dis = null;
    	DataOutputStream dos = null;
    	
    	int bufferSize = 8192;  
		byte[] buf = new byte[bufferSize];  
		
		try {  
			// Client Data stream for files
			dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));  
			String savePath = ClientConstants.RECEIVE_SERVER_FILE_PATH + dis.readUTF();  
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
//					socket.close();  
				}  
			} catch (Exception e) {  
				e.printStackTrace();  
			}  
		}
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


    public void service(){
    	 
        
        	
        	if (createConnection()) {
        		
        		// Chain a reader from the keyboard input
                BufferedReader stdIn = new BufferedReader(
                						new InputStreamReader(System.in) );
                String fromServer;
                String fromUser;
                
        		// Read from server
                try
                { 
                  while( (fromServer=socketInput.readLine())!=null )
                  {
                	  boolean sendToServer = true;
                      // Echo server string.
                      System.out.println( "Server: " + fromServer );
                      if (fromServer.equals("Server receiving data")) {
                    	  sendFile(socket,sendFilePath+userCommand[1]);
                    	  System.out.println("Client Quit");
                    	  break;
                    	  
                      }else if(fromServer.equals("Server sending data")) {
                    	  getFile(socket);
                    	  System.out.println("Client Quit");
                    	  break;
                    	  
                      }else if(fromServer.equals("Server File")) {
                    	  getFileList(socket);
                    	  System.out.println("Client Quit");
                    	  break;
                      } else if(fromServer.equals("no source file")) {
                    	  System.out.println("Please check your filename.");
                      }
                      
                      // Client types in response 
                      fromUser = stdIn.readLine();
                             
            	      if( fromUser!=null )
            	      {
                          // Echo client string.
                          System.out.println( "Client: " + fromUser );
                          
                          // Strip user command
                          userCommand =  getArgs(fromUser);
                          
                          // Check send exist file
                          if (userCommand[0].equalsIgnoreCase("put")) {
                        	  if (!haveFile(sendFilePath+userCommand[1])) {
                        		  System.out.println( "No file '" + userCommand[1] + "' in client source folder" );
                        		  sendToServer = false;
                        	  }
                          }

                          if (sendToServer) {
	                          // Write to server.
	                          socketOutput.println(fromUser);
                          }else {
                        	  socketOutput.println("client file not exist");
                          }
                      }
                  }
                  socketOutput.close();
                  socketInput.close();
                  stdIn.close();
                  socket.close();
        		  
        		}catch (Exception e) {
        			e.printStackTrace();

                }
        	} 
        } 
    
    
    public static void main(String[] args){
    	// Start a client
    	new Client().service();  
    }  
}
    
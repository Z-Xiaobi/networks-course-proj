package cwk2.server;
import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.FileOutputStream;  
import java.net.ServerSocket;  
import java.net.Socket;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;

import cwk2.server.*;

      
public class Server {  
	
      private int defaultBindPort = ServerConstants.DEFAULT_BINDING_PORT;    // Set default listening port 8888 
      
      private int tryBindTimes = 0;           // Set initial number of times for binding to port as 0  
          
      private ServerSocket serverSocket;      //  Server Sockets
          
      private ExecutorService executorService;    // Thread pool  

      
      private boolean listening = true; 
          
      /** 
       * Constructor of server，use default port number 
       * @throws Exception 
       */  
      public Server() throws Exception{  
    	  try {  
    		  this.bindToServerPort(defaultBindPort);  
    		  executorService = Executors.newFixedThreadPool(ServerConstants.TREAD_POOL_SIZE);
              System.out.println("Open up " + ServerConstants.TREAD_POOL_SIZE + " threads.");  
          } catch (Exception e) {  
                throw new Exception("Failed to bind server with port" + defaultBindPort);  
           }  
        }  
          
        /** 
         * Constructor for server，with parameter
         * Use User-Specified port number 
         * @param port 
         * @throws Exception 
         */  
        public Server(int port) throws Exception{  
            try {  
                this.bindToServerPort(port);  
                executorService = Executors.newFixedThreadPool(ServerConstants.TREAD_POOL_SIZE);  
            } catch (Exception e) {  
                throw new Exception("Fail in binding to server!");  
            }  
        }  
        /**
         * Bind server to a port
         * @param port specific port number
         */  
        private void bindToServerPort(int port) throws Exception{  
            try {  
                serverSocket = new ServerSocket(port);  
                System.out.println("Request server port: "+ port);  
                System.out.println("Server Start!");  
            } catch (Exception e) {  
                this.tryBindTimes = this.tryBindTimes + 1;  
                port = port + this.tryBindTimes;
                System.out.println("Try Port: "+ port);
                if(this.tryBindTimes >= 20){  
                    throw new Exception("Server have tried many times，but still can not be bind to port! Please choose other port numbers!");  
                }  
                //  Bind to server port recursively
                this.bindToServerPort(port);  
            }  
        }  
        // Main service of Server
        public void service(){  
            Socket socket = null;  
            while (listening) {  
                try {  
                    socket = serverSocket.accept();  
                    executorService.execute(new ClientHandler(socket));  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
          
      
          
          
        public static void main(String[] args) throws Exception{  
            new Server().service();  
        }  
    }  
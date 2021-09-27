package cwk2.server;

public interface ServerConstants {
	
	// Path for server to store received files
	public final static String RECEIVE_FILE_PATH = "cwk2/server/serverFiles/";  
             
	// Path for server to read file
	public final static String SEND_SERVER_FILE_PATH = "cwk2/server/serverFiles/";
	
	// Path for server to read file
	public final static String LOG_FILE_PATH = "cwk2/server/";
          
    // Set default binding port
	public final static int DEFAULT_BINDING_PORT = 8888;  
	
	// Set default tread pool size
	public final static int TREAD_POOL_SIZE = 10;

}

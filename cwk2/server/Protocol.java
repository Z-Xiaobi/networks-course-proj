package cwk2.server;

import java.net.*;
import java.io.*;


public class Protocol {
    private static final int WAITING = 0;
    private static final int CHOICES = 1;


    private int state = WAITING;
    
   
    
//    private String optionInfo = "Server performs several applications: list  --- which lists all of the files on the server’s folder serverFiles.\n"
//    		+ "get fname --- which requests the server send the file fname. This should then be read\n" + 
//    		"              and saved to the client’s local folder clientFiles.\n"
//    		+ "put fname --- which sends the file fname from the client’s local folder clientFiles and\n" + 
//    		"              sends it to the server (to be placed in serverFiles).\n";

    private String optionInfo = "Server Applications Commands: list, get fname, put fname";
    
	public Protocol()
	{
//		currentJoke = (int) ( Math.random()*NUMJOKES );
	}

	/**
     * This method inputs a string as file path.
     * @return All the files' name in given file path.
     */
    public String getFileList(String path) {
    	
    	String allFileNames = "";
    	
    	File file = new File(path);
//        System.out.println(file.getAbsolutePath());
        File[] fileList = file.listFiles();
//        System.out.println(file.isDirectory());
       
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
    
    
    
    /**
     * This method inputs a string from client input.
     * @return server's reply to client
     */
    public String processInput(String theInput) {
        String theOutput = null;
        
        if (state == WAITING) {
            theOutput = optionInfo;
            state = CHOICES;        
        } 
        else if (state == CHOICES) {
        	if (theInput.equalsIgnoreCase("list")) {
//        		theOutput = getFileList("src/cw2/server/serverFiles");
        		theOutput = "Server File";
            }else if (theInput.equalsIgnoreCase("get")) {
            	theOutput = "Server sending data";
            }else if (theInput.equalsIgnoreCase("put")) {
            	theOutput = "Server receiving data";
            }else if (theInput.equalsIgnoreCase("no server source file")) {
            	theOutput = "Error: no source file";
            } 
            else if (theInput.equalsIgnoreCase("client file not exist")) {
            	theOutput = "Error: no source file";
            	
            }else if(theInput.equalsIgnoreCase("finish")){
            	theOutput = "Finish service";
            	
            }else {
            	theOutput = "No such option";
            	state = WAITING;
            }
        } 
        
        return theOutput;
    }
    
}
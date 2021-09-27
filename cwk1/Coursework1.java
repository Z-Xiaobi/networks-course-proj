import java.net.*;

import java.io.*;

import java.util.regex.Pattern;
import java.util.ArrayList;

/**
 * Coursework1  Class for outputting the host information 
 * @author Xiaobi Zhang
 * */


public class Coursework1 {
	
	// Internet address
	private InetAddress inet = null;
	// contructors
	public Coursework1() {
		
	}
	public Coursework1(InetAddress inet) {
		this.inet = inet;
	}
	// Regex of IPv4 
    private static final Pattern IPV4_REGEX = 
        Pattern.compile(
                "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    
    // Regex of standard IPv6
    private static final Pattern IPV6_STD_REGEX = 
        Pattern.compile(
                "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");    

    // Regex of compressed IPv6
    private static final Pattern IPV6_COMPRESS_REGEX = 
        Pattern.compile(
                "^((?:[0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4})*)?)::((?:([0-9A-Fa-f]{1,4}:)*[0-9A-Fa-f]{1,4})?)$");
	
    // Regex for illegal compression of ip address in IPv6 
    /* The problem is the compressed address has two more double colons 
     * Such as Double double problem:
     * Compressed address 2001:630::53::
     * Can be either 2001:630:0:0:0:53:0:0
     * or 2001:630:0:0:53:0:0:0
     * */
    private static final Pattern IPV6_COMPRESS_REGEX_BORDER = 
            Pattern.compile(
                "^(::(?:[0-9A-Fa-f]{1,4})(?::[0-9A-Fa-f]{1,4}){5})|((?:[0-9A-Fa-f]{1,4})(?::[0-9A-Fa-f]{1,4}){5}::)$");

    
    /** get an IP address of a host
     * @param hostname  The name of host
	 * @return ip address of input host name
	 *         null for no IP address is fetched 
	 */
    public String getIP (String hostname) {
    	try {
    		inet = InetAddress.getByName(hostname);
    		return inet.getHostAddress();
    	}catch(UnknownHostException e){
			e.printStackTrace();
		}
    	return null;
    }

	/** print IP address of a host
	 * @param hostname  The name of host
	 * @return null
	 */
	public void printIP(String hostname){
		try {
			inet = InetAddress.getByName(hostname);
			System.out.println("IP Address:" +inet.getHostAddress());			
			
		}
		catch(UnknownHostException e){
			e.printStackTrace();
		}
	}
	

	/** print canonical host name of a host
	 * @param hostname  The name of host
	 * @return null
	 */
	public void printCanonicalHostname(String hostname) {
		try {
			inet = InetAddress.getByName(hostname);
			System.out.println("Canonical host name:" +inet.getCanonicalHostName());
		}
		catch(UnknownHostException e){
			e.printStackTrace();
		}
		
	}
	/** check whether a host is reachable
	 * @param hostname  The name of host
	 * @return a {@code boolean} indicating if the address is reachable.
	 * */
	public boolean isHostReachable (String hostname) {
		try {
			inet = InetAddress.getByName(hostname);
			return inet.isReachable(3000); // 3000 ms
		}
		catch(UnknownHostException e){
			e.printStackTrace();
		}catch (IOException e) {
            e.printStackTrace();
        }
		return false;
		
	}
	/**
	 * Judge whether an IP address is IPv4
	 * @param ipAddress The IP address of a host
	 * @return true for an IP address using IPv4
	 *         false for an IP address not using IPv4
	 */
	public static boolean isIPv4Address(final String ipAddress){
    	return IPV4_REGEX.matcher(ipAddress).matches();
    }
	/**
	 * Judge whether an ip address is IPv6
	 * @return true for an ip using IPv6
	 *         false for an ip not using IPv6
	 */
    public static boolean isIPv6Address(final String ipAddress) {
    	int num = 0;
    	for(int i = 0;i<ipAddress.length();i++){
    		if(ipAddress.charAt(i) == ':')num++;
    	}
    	if(num > 7) return false;
    	if(IPV6_STD_REGEX.matcher(ipAddress).matches()){
    		return true;
    		}
    	if(num == 7){
    		return IPV6_COMPRESS_REGEX_BORDER.matcher(ipAddress).matches();
    	}
    	else{
    		return  IPV6_COMPRESS_REGEX.matcher(ipAddress).matches();
    	}
    }

    /**
     * Highest common level of IPv4 IP addresses
     * @param ipAddresses  An array contains IPv4 addresses
     * @return a string of highest common level of IPv4 IP addresses
     *         in the form: 129.11.*.* for the common 2 levels
     *                      129.11.*.17 is not allowed
     */ 
    public static String getHightestCommonIPv4Level(String[] ipAddresses) {
    	
    	// highest common level  of input
    	String highestCommonLevel = "";
    	
    	// counter for level
    	int count=-1;
    	
    	// 2-d array to store splitted strings 
    	String[][] splittedStrings = new String[ipAddresses.length][];
 
    	// split strings by "."
    	for (int i = 0; i < ipAddresses.length; i++) {
    		splittedStrings[i] = ipAddresses[i].split("\\.");
    	}    	
    	/* Find the highest common level by 
    	 * comparing all substrings level by level
    	 * For example: 11.22.33.44, and 11.55.66.77
    	 * We first compare "11" and "11"
    	 * Then compare "22" and "55"
    	 */
    	// number of sub strings 
    	// for IPv4 is 4
    	int numOfSubStr = 4;
    	// Count for the number of slices equal to flag in i-loop
    	int numOfEqual = 0;
    	// Check whether j-loop should break
    	int breakFlag = 1;
    	// j : column number of the array / level number 
    	for (int j = 0; j < numOfSubStr; j++ ) {
    		
    		String flag = splittedStrings[0][j];
    		
    		
    		if (breakFlag == 0) {
    			break;
    		}

    		// sub strings in the same level
    		for (int i = 0; i < ipAddresses.length;i++) {
    			
    			if (splittedStrings[i][j].equals(flag)){
    				numOfEqual+=1;
    			}else {
    				breakFlag = 0;
    				break;
    			}

    			if (numOfEqual == ipAddresses.length) {
    				count = j;

    			}
        		 
    		 }
    	 }
    	for (int j = 0; j< numOfSubStr;j++) {
    		if (j<=count) {
    			highestCommonLevel+=splittedStrings[0][j];
    		}else if(count == -1){
    			highestCommonLevel+="No common level";
    			break;
    		}else {
    			highestCommonLevel+="*";
    		}
    		if (j < 3) {
    			highestCommonLevel+=".";
    		}
    	}
    	return highestCommonLevel;
    }
	
	/** Main function 
	 * @return null
	 * */
	public static void main(String args[]) {
		System.out.println("Call function 'main'.");
		// Initialise Coursework1 instance
		Coursework1 c;
		// Store IPv4 IP addresses
		ArrayList<String> ipv4Addresses = new ArrayList<String>();
		
		
		try {
			String[] hosts = args;
			String host;
			System.out.println(hosts.length);
			for (int i = 0; i < hosts.length; i++) {
				// intialize one host
				host = hosts[i];
				c = new Coursework1();
				System.out.println("Host: "+ host);
				c.printIP(host);
				c.printCanonicalHostname(host);
				System.out.println("Host is reachable? "+ c.isHostReachable(host));
				System.out.println("IP is in IPv4? "+ c.isIPv4Address(c.getIP(host)));
				System.out.println("IP is in IPv6? "+ c.isIPv6Address(c.getIP(host)));
				System.out.println("\n");
				// add ipv4 address
				if (c.isIPv4Address(c.getIP(host)) == true)
					ipv4Addresses.add(c.getIP(host));
			}// End of host loop
			
			// convert ArrayList to String[]
		    String[] ipv4AddressString = new String[ipv4Addresses.size()];			 
		    ipv4Addresses.toArray(ipv4AddressString);
		    // output the highest levels of the hierarchy that ipv4 addresses all share
		    String level = getHightestCommonIPv4Level(ipv4AddressString);
		    System.out.println("Hightest Level:"+level);

		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}



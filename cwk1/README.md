# Description
A Java class called Coursework1 that reads in multiple hostnames and converts each to an instance of InetAddress from java.net. Your class should contain a public main() method that reads in one or more hostnames as command line arguments. For each hostname, the following information should be output:
- The IP address.
- The canonical hostname.
- Whether or not the host is reachable, using the `isReachable(int timeout)` method. De- pending on your local configuration you may find this always returns false, but you should still choose a sensible timeout.
- Whether the address is IPv4 or IPv6.


In addition, if there are two or more IPv4 addresses, output the highest levels of the hierarchy that they all share. 

For instance, if two hostnames resolve to 129.11.1.17 and 129.11.2.17, code should output 129.11.*.* (not 129.11.*.17!) 

Only whole bytes need be considered, not consider individual bits.

# Description 
Two applications, a server and a client, that transfer files between each other using sockets. 

The server application :
- Run continuously.
- Use an Executor to manage a fixed thread-pool with 10 connections.
- Following a request by a client, query the local folder serverFiles and return a list of the
files found there to the same client.
- Send a file from serverFiles to a client that requests it.
- Read a file from a client and place it in the serverFiles folder.
- Log every request by a client in a local file log.txt with the format:
`date:time:client IP address:request`



The client application should:
- Accept one of the follow commands as command line arguments, and performs the stated task:
- - `list`, which lists all of the files on the server’s folder serverFiles.
- - `get fname`, which requests the server send the file fname. This should then be read
and saved to the client’s local folder clientFiles.
- - `put fname`, which sends the file fname from the client’s local folder `clientFiles` and
sends it to the server (to be placed in `serverFiles`).
- Quits after completing each command.
 
The listening port: 8888. 

Both the client and the server should run on the same machine, i.e. with hostname `localhost`. 
They should not attempt to access each other’s disk space; all communication must be via sockets.
Both applications have basic error handling.

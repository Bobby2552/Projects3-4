/*
 * Simple server program to handle multiple clients.
 * Requires the class BoilerRequestHandler to handle the interaction with each client in a 
 * separate thread.
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class BoilerServer {

	static HashMap<Integer, Socket> clientSockets = new HashMap<Integer, Socket>(); //collection of sockets to currently connected clients. Stored with client IDs as key
	static HashMap<Integer, BoilerRequestHandler> handlers = new HashMap<Integer, BoilerRequestHandler>(); //collection of request handlers -- one per client socket stored with same key as clientSockets
	static int nextClientID = 0; // id of next client

	public static void main(String[] args) {
		int portNumber;

		if(args.length !=1) {
			System.err.println("Usage: java BoilerServer <port number>");
			System.exit(1);
		}
		portNumber = Integer.parseInt(args[0]);
	
		try { 
			serveClients(portNumber);
		} catch (IOException e) {
			System.err.println("Unexpected error: " + e.getMessage());
		}
	}
	
	private static void serveClients(int portNumber) throws IOException {
		ServerSocket serverSocket = null;

		try{
			System.out.println("Creating Socket");
			serverSocket = new ServerSocket(portNumber);
            
			System.out.println("Listening");
			while(true){
				clientSockets.put(nextClientID, serverSocket.accept());
				System.out.println("Got a request: " + clientSockets.get(nextClientID).getPort());
				handlers.put(nextClientID, new BoilerRequestHandler(clientSockets.get(nextClientID), nextClientID));
				handlers.get(nextClientID).start();
				nextClientID++;
				checkForFinishedClients();
			}
		} catch (MalformedURLException e) {
			System.err.println("Unable to connect:\n" + e.getMessage());
			System.exit(3);
		} finally {
			if(serverSocket != null)
				serverSocket.close();
			for (Socket s : clientSockets.values())
				if(s != null && !s.isClosed())
					s.close();
		}
	}

	private static void checkForFinishedClients(){
		Socket socket = null;
		BoilerRequestHandler handler = null;

		for(Integer cID : clientSockets.keySet()){
			socket = clientSockets.get(cID);
			if(socket.isClosed()){
				handler = handlers.get(cID);
				//should we wait for this handler thread to join?
				handlers.remove(cID);
				clientSockets.remove(cID);
			}
		}
	}
}

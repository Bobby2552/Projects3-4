/*
 * Simple server program
 */

import java.net.*;
import java.io.*;

public class BoilerRequestHandler extends Thread {
	private Socket clientSocket;
	private int clientID;
	
	public BoilerRequestHandler(Socket clientSocket, int clientID){
		this.clientSocket = clientSocket;
		this.clientID = clientID;
	}


	public void run() {
		try{
			interactWithClient();
		} catch (IOException e) {
			System.out.println("Unexpected IO exception for client " + clientID);
		}
	}

	private void interactWithClient () throws IOException {

		PrintWriter outToClient = null;
		BufferedReader inFromClient = null;
		String inputLine, outputLine;
		int inCount = 0, outCount = 0;


		try{
			outToClient = new PrintWriter(clientSocket.getOutputStream(), true);
            
			inFromClient = new BufferedReader( 
						new InputStreamReader(clientSocket.getInputStream()));
            
			outToClient.println("Message " + ++outCount + " from Server to client " + clientID);
			while( !((inputLine = inFromClient.readLine()).equals("LEAVE")) ) {
				System.out.println("Received message " + ++inCount + " from client " + clientID + ":\n" + inputLine);
				outToClient.println("Message " + ++outCount + " from Server to client " + clientID);
			}
			System.out.println("Client " + clientID + " disconnected");
	
			
		} catch (IOException e) {
			System.err.println("Error with IO with Client\n" + clientID + e.getMessage());

			e.printStackTrace();
		} finally {
			if(outToClient != null)
				outToClient.close();
			if(inFromClient != null)
				inFromClient.close();
			clientSocket.close();
		}
	}
}

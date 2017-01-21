import java.net.*;
import java.io.*;
import java.util.Random;

public class BoilerClient {
	public static void main(String args[]) throws IOException {
		int serverPort;
		Socket socket = null;
		PrintWriter outToServer = null;
		BufferedReader inFromServer = null;
		int inCount = 0, outCount = 0;
		String inputLine;
		Random rand = new Random(System.currentTimeMillis());
		int maxMessages;
		
		if(args.length != 3) {
			System.err.println("Usage: java BoilerClient <serverHost> <serverPortNumber> <Number of messages to send>");
			System.exit(1);
		}
		
		serverPort = Integer.parseInt(args[1]);
		maxMessages = Integer.parseInt(args[2]);

		if(serverPort < 0 || maxMessages < 0) {
			System.err.println("Usage: java BoilerClient <serverHost> <serverPortNumber> <Number of messages to send>");
			System.exit(1);
		}

		try{
			System.out.println("trying to connect to " + args[0] + " port: " + serverPort);
			socket = new Socket(args[0], serverPort);
			System.out.println("trying to create input and output");
			outToServer = new PrintWriter (socket.getOutputStream(), true);
			inFromServer = new BufferedReader( new InputStreamReader (socket.getInputStream()));
			while ( (inputLine = inFromServer.readLine()) != null) {
				try{
					Thread.sleep(rand.nextInt(1000));
				} catch (InterruptedException e){
					System.out.println("unexected interrupt");
				}
				System.out.println("Received message  " + ++inCount + " from Server:\n" + inputLine);
				if(outCount < maxMessages)
					outToServer.println("Message " + ++outCount + " to server from client");
				else
					outToServer.println("LEAVE");
			}
			System.out.println("Closing up client");
		} catch (MalformedURLException e) {
			System.err.println("Error with URL:\n" + e.getMessage());
			System.exit(2);
		} catch (IOException e) {
			System.err.println("Error with IO:\n" + e.getMessage());
			System.exit(3);
		} finally {
			if(socket != null)
				socket.close();
			if(outToServer != null)
				outToServer.close();
			if(inFromServer != null)
				inFromServer.close();
		}
	}
}

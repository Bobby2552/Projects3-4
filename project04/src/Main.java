import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * This class is what is run. It creates threads and listens for new clients. It facilitates any communication between
 * Server (class) threads.
 * <p>
 * DEFUALT PORT IS 9999
 */
public class Main {

	private static int PORT = 9999;

	/**
	 * Here we will check for new users who want to log in. Loging in and registering will be here.
	 *
	 * @param args
	 */
	public static void main (String args[]) throws IOException {

		User.loadUsers ();
		Question.loadQuestions ();
		ServerSocket listener = new ServerSocket (9999);
		System.out.printf ("Listening for new users on %s:%s\n", InetAddress.getLocalHost ().getHostAddress (), PORT);
		try {
			while (true) {
				Socket socket = listener.accept ();
				System.out.println ("Connected to new client!!");

				InputStreamReader isr = new InputStreamReader (socket.getInputStream ());
				BufferedReader in = new BufferedReader (isr);

				PrintWriter out = new PrintWriter (socket.getOutputStream (), true);

				Server server = new Server (socket, isr, in, out);
				Thread thread = new Thread (server);
				thread.start ();
				Shared.servers.add (server);

			}
		} finally {
			listener.close ();
		}
	}

}

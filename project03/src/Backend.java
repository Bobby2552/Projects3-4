import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Lots of ways you can interface with the server. This class takes care of communication, and
 * storage of relevant game info. Can be used by leaders and non-leaders.
 *
 * @author Ben Scholer
 */
class Backend {
	
	private final String IP = "127.0.0.1";
	private final int serverPort = 9999;
	private Socket socket;
	private PrintWriter out;
	private InputStreamReader isr;
	private BufferedReader in;
	private String userToken;
	private String gameToken;
	private String username;
	private Question question;
	private ArrayList<Participant> participants;
	private boolean isLeader;
	
	public static void main (String[] args) {
		
		Backend backend = new Backend( );
		Login login = new Login(backend);
		login.setSize(300, 500);
		login.setVisible(true);
	}
	
	/**
	 * Backend constructor
	 */
	public Backend ( ) {
		
		try {
			socket = new Socket(this.IP, this.serverPort);
			out = new PrintWriter(socket.getOutputStream( ), true);
			isr = new InputStreamReader(socket.getInputStream( ));
			in = new BufferedReader(isr);
		} catch (Exception e) {
			e.printStackTrace( );
		}
		
		participants = new ArrayList<>( );
	}
	
	/**
	 * Used to communicate between client and server.
	 * <p>
	 * This method is used heavily by the other methods in this class. It offers a clean and easy way to converse with the server from the client using String arrays.
	 *
	 * @param args
	 * 		The String array of arguments to be passed to the server.
	 *
	 * @return The String array of resultant replies.
	 */
	public String[] communicate (String[] args) {
		
		String message = "";
		for (int i = 0; i < args.length; i++) {
			message += "--" + args[i];
		}
		message = message.substring(2);
		String reply = "";
		try {
			out.println(message);
			reply = in.readLine( );
		} catch (Exception e) {
			e.printStackTrace( );
		}
		String[] ret = reply.split("--");
		return ret;
	}
	
	public void writeToServer (String[] args) {
		
		String message = "";
		for (int i = 0; i < args.length; i++) {
			message += "--" + args[i];
		}
		message = message.substring(2);
		String reply = "";
		try {
			out.println(message);
		} catch (Exception e) {
			e.printStackTrace( );
		}
	}
	
	/**
	 * Read a response from the server.
	 *
	 * @return A String [] with the elements of the reply
	 */
	public String[] readFromServer ( ) {
		
		String reply = "";
		try {
			if (in.ready( )) {
				reply = in.readLine( );
			} else {
				return new String[0];
			}
		} catch (Exception e) {
			e.printStackTrace( );
		}
		String[] ret = reply.split("--");
		return ret;
	}
	
	/**
	 * Used to create a new user.
	 *
	 * @param userName
	 * 		The username of the new user to be created.
	 * @param password
	 * 		The password of the new user to be created.
	 *
	 * @return The status of the call. See project sheet for more details.
	 */
	public String newUser (String userName, String password) {
		
		String[] args = new String[] {"CREATENEWUSER", userName, password};
		String[] reply = communicate(args);
		return reply[2];
	}
	
	/**
	 * Used to login to the server.
	 *
	 * @param userName
	 * 		The username of the user to login.
	 * @param password
	 * 		The password of the user to login.
	 *
	 * @return The status of the call, or the user token if call was successful. See project sheet for more details.
	 */
	public String login (String userName, String password) {
		
		String[] args = new String[] {"LOGIN", userName, password};
		String[] reply = communicate(args);
		if (reply[2].equals("SUCCESS")) {
			this.username = userName;
			this.userToken = reply[3];
			return reply[3];
		}
		return reply[2];
	}
	
	/**
	 * Logout
	 *
	 * @return The status.
	 */
	public String logout ( ) {
		
		String[] args = new String[] {"LOGOUT", ""};
		String[] reply = communicate(args);
		return reply[2];
	}
	
	/**
	 * Used to start new game.
	 *
	 * @param userToken
	 * 		The user token used for user authentication
	 *
	 * @return The status of the call, or game token if call was successful. See project sheet for more details.
	 */
	public String startNewGame (String userToken) {
		
		String[] args = new String[] {"STARTNEWGAME", userToken};
		String[] reply = communicate(args);
		if (reply[2].equals("SUCCESS")) {
			isLeader = true;
			print(reply[3]);
			this.gameToken = reply[3];
			return reply[3];
		} else {
			return reply[2];
		}
	}
	
	/**
	 * Used to start new game, using user token created by {@code login()}.
	 *
	 * @return The status of the call, or game token if call was successful. See project sheet for more details.
	 */
	public String startNewGame ( ) {
		
		String[] args = new String[] {"STARTNEWGAME", this.userToken};
		String[] reply = communicate(args);
		if (reply[2].equals("SUCCESS")) {
			isLeader = true;
			print(reply[3]);
			this.gameToken = reply[3];
			return reply[3];
		} else {
			return reply[2];
		}
	}
	
	/**
	 * Used to join a game.
	 *
	 * @param userToken
	 * 		The user token of the user who wishes to join a game.
	 * @param gameToken
	 * 		The game token of the game the user wishes to join.
	 *
	 * @return The status of the call. See project sheet for more details.
	 */
	public String joinGame (String userToken, String gameToken) {
		
		String[] args = new String[] {"JOINGAME", userToken, gameToken};
		String[] reply = communicate(args);
		isLeader = false;
		return reply[2];
	}
	
	/**
	 * Used to join a game.
	 *
	 * @return The status of the call. See project sheet for more details.
	 */
	public String joinGame ( ) {
		
		String[] args = new String[] {"JOINGAME", this.userToken, this.gameToken};
		String[] reply = communicate(args);
		isLeader = false;
		return reply[2];
	}
	
	/**
	 * Used to launch a game.
	 *
	 * @param userToken
	 * 		The user token of the user who wishes to launch a game.
	 * @param gameToken
	 * 		The game token of the game the user wishes to launch.
	 *
	 * @return A question object, or an error code String
	 */
	public Object launchGame (String userToken, String gameToken) {
		
		String[] args = new String[] {"ALLPARTICIPANTSHAVEJOINED", userToken, gameToken};
		String[] reply = communicate(args);
		if (reply[0].equals("NEWGAMEWORD")) {
			this.question = new Question(reply[1], reply[2], new String[] {reply[2]});
			return question;
		} else {
			return reply[0];
		}
	}
	
	/**
	 * Used to launch a game.
	 *
	 * @return A question object, or an error code String
	 */
	public Object launchGame ( ) {
		
		String[] args = new String[] {"ALLPARTICIPANTSHAVEJOINED", this.userToken, this.gameToken};
		String[] reply = communicate(args);
		if (reply[0].equals("NEWGAMEWORD")) {
			this.question = new Question(reply[1], reply[2], new String[] {reply[2]});
			return question;
		} else {
			return reply[0];
		}
	}
	
	/**
	 * Used to send a player's suggestion
	 *
	 * @param userToken
	 * 		The user token
	 * @param gameToken
	 * 		The game token
	 * @param suggestion
	 * 		The suggestion
	 *
	 * @return The status
	 */
	public String sendPlayerSuggestion (String userToken, String gameToken, String suggestion) {
		
		String[] args = new String[] {"PLAYERSUGGESTION", userToken, gameToken, suggestion};
		String[] reply = communicate(args);
		return reply[2];
	}
	
	/**
	 * Used to send a player's suggestion
	 *
	 * @param suggestion
	 * 		The suggestion
	 *
	 * @return The status
	 */
	public void sendPlayerSuggestion (String suggestion) {
		
		String[] args = new String[] {"PLAYERSUGGESTION", userToken, gameToken, suggestion};
		writeToServer(args);
		readFromServer();
		
	}
	
	/**
	 * Used to send a player's choice
	 *
	 * @param choice
	 * 		The choice
	 *
	 * @return The status
	 */
	public void sendPlayerChoice (String choice) {
		
		String[] args = new String[] {"PLAYERCHOICE", userToken, gameToken, choice};
		writeToServer(args);
	}
	
	/**
	 * Check for any new participants. Designed to be used repetitively, and by the game leader.
	 *
	 * @return Any new participants.
	 */
	public Participant checkForNewParticipants ( ) {
		
		String[] part = readFromServer( );
		if (part.length == 0) {
			return null;
		} else {
			Participant p = new Participant(part[1], Integer.parseInt(part[2]));
			participants.add(p);
			return p;
		}
	}
	
	/**
	 * Check if the game has started
	 *
	 * @return A question object if the game has started, null any other case.
	 */
	public Question checkIfGameHasStarted ( ) {
		
		
		String[] ret = readFromServer( );
		if (ret.length == 0) {
			return null;
		} else {
			if (ret[0].equals("NEWGAMEWORD")) {
				this.question = new Question(ret[1], ret[2], new String[] {ret[2]});
				return question;
			}
		}
		return null;
		
	}
	
	/**
	 * Check to see if all participants have submitted their answers.
	 *
	 * @return A completed Question object.
	 */
	public Question checkForSubmissions ( ) {
		
		String[] part = readFromServer( );
		if (part.length == 0) {
			return null;
		} else {
			for (int i = 1; i < part.length; i++) {
				if (! part[i].equals(question.getCorrectAnswer( ))) question.addAnswer(part[i]);
			}
			return question;
		}
	}
	
	/**
	 * Check to see if all participants have submitted their answers.
	 *
	 * @return An ArrayList of completed Participant objects.
	 */
	public ArrayList<Participant> checkForRoundResults ( ) {
		
		String[] part = readFromServer( );
		if (part.length < 2) {
			return null;
		} else {
			if (part[0].equals("ROUNDRESULT")) {
				participants = new ArrayList<>();
				for (int i = 1; i < part.length; i += 5) {
					Participant participant = new Participant(part[i], part[i + 1], Integer.parseInt(part[i + 2]),
							Integer.parseInt(part[i + 3]), Integer.parseInt(part[i + 4]));
					participants.add(participant);
					System.out.println(participant.toString() );
				}
				System.out.println(participants.size( ));
				
				String[] ret = readFromServer( );
				if (ret.length == 0) {
					return null;
				} else {
					if (ret[0].equals("NEWGAMEWORD")) {
						this.question = new Question(ret[1], ret[2], new String[] {ret[2]});
						
					} else if (ret[0].equals("GAMEOVER")) this.question = null;
				}
				
				return participants;
			} else return null;
		}
	}
	
	
	/**
	 * Get question
	 *
	 * @return The current round's question
	 */
	public Question getQuestion ( ) {
		
		return this.question;
	}
	
	/**
	 * Get username
	 *
	 * @return The username
	 */
	public String getUsername ( ) {
		
		return this.username;
	}
	
	/**
	 * Get the ArrayList of participants.
	 *
	 * @return ArrayList of participants
	 */
	public ArrayList<Participant> getParticipants ( ) {
		
		return this.participants;
	}
	
	/**
	 * Used to get the user token generated by {@code login()}.
	 *
	 * @return The user token generated by {@code login()}.
	 */
	public String getUserToken ( ) {
		
		return this.userToken;
	}
	
	/**
	 * Used to get the game token generated by {@code startNewGame()}.
	 *
	 * @return The game token generated by {@code startNewGame()}.
	 */
	public String getGameToken ( ) {
		
		return this.gameToken;
	}
	
	/**
	 * Used to set the user token.
	 *
	 * @param userToken
	 * 		The user token to take the place of the existing user token.
	 */
	public void setUserToken (String userToken) {
		
		this.userToken = userToken;
	}
	
	/**
	 * Check if the current user is the leader.
	 *
	 * @return Is this the leader?
	 */
	public boolean isLeader ( ) {
		
		return isLeader;
	}
	
	/**
	 * Set if the current user is the leader.
	 *
	 * @param leader
	 * 		Is this the leader?
	 */
	public void setLeader (boolean leader) {
		
		isLeader = leader;
	}
	
	/**
	 * Used to set the game token.
	 * <p>
	 * This method should be used when a user wishes to join an existing game, as the game token will be null without
	 * calling this method.
	 * <p>
	 * Either use methods that have game token as a parameter, or use this method.
	 *
	 * @param gameToken
	 * 		The game token to take the place of the existing game token.
	 */
	public void setGameToken (String gameToken) {
		
		this.gameToken = gameToken;
	}
	
	private void print (String message) {
		
		System.out.printf("BACKEND:\t\t%s", message.toUpperCase( ));
	}
}

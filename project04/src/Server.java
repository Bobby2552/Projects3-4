import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This class is what actually communicates with the client, and also communicates with the Main class to communicate
 * with other threads.
 */
public class Server implements Runnable {

	private Socket socket;
	private InputStreamReader isr;
	private BufferedReader in;
	private PrintWriter out;
	private ArrayList<User> participants = new ArrayList<>();
	private User user;


	public Server() {

	}

	public Server(Socket socket, InputStreamReader isr, BufferedReader in, PrintWriter out) {

		this.socket = socket;
		this.isr = isr;
		this.in = in;
		this.out = out;
	}

	@Override
	public void run() {

		System.out.println("New Thread made!!");
		while (true) {
			try {
				if (! socket.isConnected()) return;
				String message = in.readLine();
				if (message == null) return;
				try {
					System.out.printf("From %s's client : %s\n", this.user.getUsername(), message);
				} catch (NullPointerException e) {
					System.out.printf("From null's client : %s\n", message);
				}
				String[] args = Shared.deocde(message);
				if (args[0].equals("CREATENEWUSER")) {
					createNewUser(args);
				} else if (args[0].equals("LOGIN")) {
					login(args);
				} else if (args[0].equals("STARTNEWGAME")) {
					startNewGame(args);
				} else if (args[0].equals("JOINGAME")) {
					joinGame(args);
				} else if (args[0].equals("ALLPARTICIPANTSHAVEJOINED")) {
					launchGame(args);
				} else if (args[0].equals("PLAYERSUGGESTION")) {
					addPlayerSuggestion(args);
				} else if (args[0].equals("PLAYERCHOICE")) {
					addPlayerAnswer(args);
				} else if (args[0].equals("LOGOUT")) {
					logout();
					return;
				} else {
					respond(new String[] {"INVALIDMESSAGEFORMAT"});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void login(String[] args) {

		String command = "LOGIN";
		if (args.length != 3) respond(new String[] {command, "INVALIDMESSAGEFORMAT"});
		String username = args[1];
		String password = args[2];
		boolean inDatabase = false;
		synchronized (Shared.usersInDatabase) {
			for (User user : Shared.usersInDatabase) {
				if (user.getUsername().equals(username)) {
					inDatabase = true;
					for (Map.Entry<String, User> userEntry : Shared.usersLoggedIn.entrySet()) {
						if (userEntry.getValue().getUsername().equals(username)) {
							respond(new String[] {command, "USERALREADYLOGGEDIN"});
						}
					}
					if (inDatabase) {
						if (user.getPassword().equals(password)) {
							this.user = user;
							this.user.setUserToken(randomString(10, true));
							Shared.usersLoggedIn.put(this.user.getUserToken(), this.user);
							respond(new String[] {command, "SUCCESS", this.user.getUserToken()});
						} else respond(new String[] {command, "INVALIDUSERPASSWORD"});
					}
					break;
				}
			}
		}
		if (! inDatabase) respond(new String[] {command, "UNKNOWNUSER"});
	}

	private void createNewUser(String[] args) {

		String command = "CREATENEWUSER";
		if (args.length != 3) respond(new String[] {command, "INVALIDMESSAGEFORMAT"});
		String username = args[1];
		String password = args[2];
		Pattern p = Pattern.compile("[^a-zA-Z0-9_]");
		boolean hasSpecialChar = p.matcher(username).find();
		if (username.length() != 0 && username.length() < 10 && ! hasSpecialChar) {
			p = Pattern.compile("[^a-zA-Z0-9#&$*]");
			hasSpecialChar = p.matcher(password).find();
			boolean hasCapital = false;
			for (int i = 0; i < password.length(); i++) {
				if (password.charAt(i) > 64 && password.charAt(i) < 91) {
					hasCapital = true;
					break;
				}
			}
			boolean hasNumber = false;
			for (int i = 0; i < password.length(); i++) {
				if (password.charAt(i) > 47 && password.charAt(i) < 58) {
					hasNumber = true;
					break;
				}
			}
			if (password.length() != 0 && password.length() < 10 && ! hasSpecialChar && hasCapital && hasNumber) {
				for (User user : Shared.usersInDatabase) {
					if (user.getUsername().equals(username))
						respond(new String[] {command, "USERALREADYEXISTS"});
				}
				User.createUser(username, password);
				respond(new String[] {command, "SUCCESS"});
			} else respond(new String[] {command, "INVALIDPASSWORD"});
		} else respond(new String[] {command, "INVALIDUSERNAME"});
	}

	private void startNewGame(String[] args) {

		String command = "STARTNEWGAME";
		String token = args[1];
		if (! Shared.usersLoggedIn.containsKey(token)) {
			respond(new String[] {command, "USERNOTLOGGEDIN"});
		} else {
			String gameKey = randomString(3, false);
			if (Shared.usersLoggedIn.get(token).getGameToken() != null) {
				respond(new String[] {command, "FAILURE"});
			} else {
				ArrayList<Server> servers = new ArrayList<>();
				servers.add(this);
				Shared.games.add(new Game(servers, gameKey));
				user.setGameToken(gameKey);
				Shared.usersLoggedIn.get(token).setGameToken(gameKey);
				user.setLeader(true);
				Shared.usersLoggedIn.get(token).setLeader(true);
				participants.add(user);
				respond(new String[] {command, "SUCCESS", gameKey});
			}
		}
	}

	private void joinGame(String[] args) {

		String command = "JOINGAME";
		String userToken = args[1];
		String gameToken = args[2];
		if (! Shared.usersLoggedIn.containsKey(userToken)) {
			respond(new String[] {command, "USERNOTLOGGEDIN"});
		} else {

			boolean gameExists = false;
			for (Game game : Shared.games) {
				if (game.getGameKey().equals(game.getGameKey())) {
					gameExists = true;
					break;
				}
			}

			if (! gameExists) {
				respond(new String[] {command, "GAMEKEYNOTFOUND"});
			} else {
				if (Shared.usersLoggedIn.get(userToken).getGameToken() != null) {
					respond(new String[] {command, "FAILURE"});
				} else {
					for (Server server : Shared.servers) {
						if (server.getUser().isLeader() && server.getUser().getGameToken().equals(gameToken)) {
							server.useSocket(Shared.encode(new String[] {"NEWPARTICIPANT", this.user.getUsername(),
									this.user.getScore() + ""}));
						}
					}

					Server server = new Server();
					for (Server serverFound : Shared.servers) {
						if (serverFound.getUser().getUsername().equals(this.user.getUsername())) {
							server = serverFound;
							break;
						}
					}

					System.out.printf("\t\t\tI found %s's server.", server.getUser().getUsername());

					for (Game game : Shared.games) {
						if (game.getGameKey().equals(gameToken)) {
							game.getServers().add(server);
							break;
						}
					}
					respond(new String[] {command, "SUCCESS", gameToken});
				}
			}
		}
	}

	private void launchGame(String[] args) {

		String command = "ALLPARTICIPANTSHAVEJOINED";
		String userToken = args[1];
		String gameToken = args[2];
		if (! Shared.usersLoggedIn.containsKey(userToken)) {
			// check if the user is logged in (USERNOTLOGGEDIN)
			respond(new String[] {command, "USERNOTLOGGEDIN"});
		} else {
			//check if the game token is in Shared.games (INVALIDGAMETOKEN)

			boolean gameExists = false;
			for (Game game : Shared.games) {
				if (game.getGameKey().equals(game.getGameKey())) {
					gameExists = true;
					break;
				}
			}

			if (! gameExists) {
				respond(new String[] {command, "INVALIDGAMETOKEN"});
			} else {
				//check if the user is a game leader (USERNOTGAMELEADER)
				if (! Shared.usersLoggedIn.get(userToken).isLeader()) {
					respond(new String[] {command, "USERNOTGAMELEADER"});
				} else {
					sendNewQuestion();
				}
			}
		}
	}

	private void addPlayerSuggestion(String[] args) {

		String command = "PLAYERSUGGESTION";
		String userToken = args[1];
		String gameToken = args[2];
		String suggestion = args[3];
		if (! Shared.usersLoggedIn.containsKey(userToken)) {
			// check if the user is logged in (USERNOTLOGGEDIN)
			respond(new String[] {command, "USERNOTLOGGEDIN"});
		} else {
			//check if the game token is in Shared.games (INVALIDGAMETOKEN)

			boolean gameExists = false;
			for (Game game : Shared.games) {
				if (game.getGameKey().equals(game.getGameKey())) {
					gameExists = true;
					break;
				}
			}

			if (! gameExists) {
				respond(new String[] {command, "INVALIDGAMETOKEN"});
			} else {
				Game game = new Game();
				for (Game gameInList : Shared.games) {
					if (gameInList.equals(gameToken)) {
						game = gameInList;
						break;
					}
				}

				synchronized (game) {
					game.getQuestion().addAnswer(this.user, suggestion);
				}

				if (this.user.isLeader()) {
					while (game.getQuestion().getAnswers().size() < game.getServers().size() + 1) {
						Thread.yield();
					}

					System.out.println("Everybody has submitted options!");

					String[] strings = new String[game.getQuestion().getAnswers().size() + 1];
					strings[0] = "ROUNDOPTIONS";
					List<String> suggestions = new ArrayList<>();
					for (int i = 1; i < strings.length; i++) {
						suggestions.add((String) game.getQuestion().getAnswers().values().toArray()[i - 1]);
					}
					Collections.shuffle(suggestions);
					System.out.println("Shuffled!!");
					for (int i = 1; i < strings.length; i++) {
						strings[i] = (String) suggestions.toArray()[i - 1];
					}
					for (Server server : game.getServers()) {
						server.useSocket(Shared.encode(strings));
					}
				}
			}
		}
	}

	private void addPlayerAnswer(String[] args) {

		String command = "PLAYERCHOICE";
		String userToken = args[1];
		String gameToken = args[2];
		String answer = args[3];
		if (! Shared.usersLoggedIn.containsKey(userToken)) {
			// check if the user is logged in (USERNOTLOGGEDIN)
			respond(new String[] {command, "USERNOTLOGGEDIN"});
		} else {
			//check if the game token is in Shared.games (INVALIDGAMETOKEN)

			boolean gameExists = false;
			for (Game game : Shared.games) {
				if (game.getGameKey().equals(game.getGameKey())) {
					gameExists = true;
					break;
				}
			}

			if (! gameExists) {
				respond(new String[] {command, "INVALIDGAMETOKEN"});
			} else {
				Game game = new Game();
				for (Game gameInList : Shared.games) {
					if (gameInList.equals(gameToken)) {
						game = gameInList;
						break;
					}
				}

				synchronized (game) {
					game.getPlayerAnswers().put(this.user, answer);
				}

				if (this.user.isLeader()) {
					while (game.getPlayerAnswers().size() < game.getServers().size()) {
					}

					score(game);

					String response[] = new String[game.getServers().size() * 5 + 1];
					response[0] = "ROUNDRESULT";
					for (int i = 0; i < game.getServers().size(); i++) {
						response[(i * 5) + 1] = game.getServers().get(i).getUser().getUsername();
						response[(i * 5) + 2] = game.getMessages().get(game.getServers().get(i).getUser());
						response[(i * 5) + 3] = game.getServers().get(i).getUser().getScore() + "";
						response[(i * 5) + 4] = game.getServers().get(i).getUser().getFools() + "";
						response[(i * 5) + 5] = game.getServers().get(i).getUser().getFooled() + "";
					}

					for (Server server : game.getServers()) {
						server.useSocket(Shared.encode(response));
					}

					game.setPlayerAnswers(new HashMap<>());
					game.setMessages(new HashMap<>());

					sendNewQuestion();
					updateUsers();
				}
			}
		}
	}

	private void logout() {

		if (! Shared.usersLoggedIn.containsKey(this.user.getUserToken())) {
			respond(new String[] {"LOGOUT", "USERNOTLOGGEDIN"});
			return;
		}
		updateUsers();
		Shared.servers.remove(this);
		Shared.usersLoggedIn.remove(this.user.getUserToken());
		for (Game game : Shared.games) {
			game.getServers().remove(this);
			game.getPlayerAnswers().remove(this.user);
			game.getMessages().remove(this.user);
			game.getQuestion().getAnswers().remove(this.user);
		}
		respond(new String[] {"LOGOUT", "SUCCESS"});
	}

	private void updateUsers() {

		BufferedWriter out = null;
		String line = null;
		ArrayList<String> lines = new ArrayList<>();

		try {
			out = new BufferedWriter(new FileWriter(new File("Resources/UserDatabase")));

				for (HashMap.Entry entry : Shared.usersLoggedIn.entrySet()) {
					User user = (User) entry.getValue();
					lines.add(String.format("%s:%s:%d:%d:%d", user.getUsername(), user.getPassword(),
							user.getScore(), user.getFools(), user.getFooled()));
				}

			for (User user : Shared.usersInDatabase) {
				boolean isLoggedIn = false;
				for (HashMap.Entry entry : Shared.usersLoggedIn.entrySet()) {
					User second = (User) entry.getValue();
					if (user.getUsername().equals(second.getUsername())) {
						isLoggedIn = true;
						break;
					}
				}
				if (!isLoggedIn) {
					lines.add(String.format("%s:%s:%d:%d:%d", user.getUsername(), user.getPassword(),
							user.getScore(), user.getFools(), user.getFooled()));
				}
			}

				for (String string : lines) {
					out.write(string + "\n");
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void score(Game game) {

		for (int i = 0; i < game.getPlayerAnswers().size(); i++) {
			if (game.getPlayerAnswers().values().toArray()[i].equals(game.getQuestion().getCorrectAnswer())) {
				User user = (User) game.getPlayerAnswers().keySet().toArray()[i];
				user.setScore(user.getScore() + 10);
				game.getMessages().put(user, "You got it right! ");
			} else {
				//Search for who fooled you.
				User userFooled = (User) game.getPlayerAnswers().keySet().toArray()[i];
				for (HashMap.Entry entry : game.getQuestion().getAnswers().entrySet()) {
					if (entry.getValue().equals(game.getPlayerAnswers().values().toArray()[i])) {
						User userY = (User) entry.getKey();
						userY.setScore(userY.getScore() + 5);
						userY.setFools(userY.getFools() + 1);
						userFooled.setFooled(userFooled.getFooled() + 1);
						String messageFooled = String.format("You were fooled by %s. ", userY.getUsername());
						String messageY = String.format("You fooled %s. ", userFooled.getUsername());

						if (game.getMessages().get(userY) != null) {
							String str = game.getMessages().get(userY);
							str += messageY;
							game.getMessages().put(userY, str);
						} else {
							game.getMessages().put(userY, messageY);
						}

						if (game.getMessages().get(userFooled) != null) {
							String str = game.getMessages().get(userFooled);
							str += messageFooled;
							game.getMessages().put(userFooled, str);
						} else {
							game.getMessages().put(userFooled, messageFooled);
						}
					}
				}
			}
		}
//		for (User user : Shared.usersInDatabase) {
//			boolean isLoggedIn = false;
//			for (HashMap.Entry entry : Shared.usersLoggedIn.entrySet()) {
//				User loggedIn = (User) entry.getKey();
//				if (loggedIn.getUsername().equals(user.getUsername())) {
//					user = loggedIn;
//					break;
//				}
//			}
//		}
	}

	public void sendNewQuestion() {

		//Find the correct game in the list of games. We can assume it will always be there, because it is always
		// checked before this method is called.
		String gameToken = this.user.getGameToken();
		Game game = new Game();
		for (Game gameInList : Shared.games) {
			if (gameInList.equals(gameToken)) {
				game = gameInList;
				break;
			}
		}
		System.out.print("Users in the game: ");
		for (Server server : game.getServers()) {
			System.out.print(server.getUser().getUsername() + ", ");
		}
		System.out.println();

		Question question = game.increment();

		// Check if its the last question
		if (question == null) {
			for (Server server : game.getServers()) {
				server.useSocket(Shared.encode(new String[] {"GAMEOVER"}));
			}
			Shared.games.remove(game);
		} else { //Otherwise, send a new game word.
			for (Server server : game.getServers()) {
				server.useSocket(Shared.encode(new String[] {"NEWGAMEWORD", question.getQuestion(),
						question.getCorrectAnswer()}));
			}
		}
	}

	private void respond(String[] message) {

		String[] args = new String[message.length + 1];
		args[0] = "RESPONSE";
		for (int i = 0; i < message.length; i++) {
			args[i + 1] = message[i];
		}
		try {
			System.out.println("\n" + this.user.getUsername() + " : " + Shared.encode(args));
		} catch (NullPointerException e) {
			System.out.println("\nnull : " + Shared.encode(args));
		}
		out.println(Shared.encode(args));
	}

	private String randomString(int len, boolean numbers) {

		SecureRandom rnd = new SecureRandom();
		String AB;
		if (numbers) AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		else AB = "abcdefghijklmnopqrstuvwxyz";
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	public void useSocket(String message) {

		System.out.printf("To %s : %s", this.user.getUsername(), message);
		out.println(message);
	}

	public User getUser() {

		return user;
	}
}

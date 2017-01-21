import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class stores global variables, such as ArrayLists of users and servers, and other things.
 * <p>
 * *************************************************************
 * NO LOGIC SHOULD BE IN THIS CLASS!!!!             *
 * *************************************************************
 */
public class Shared {

	//User key, User
	public static HashMap<String, User> usersLoggedIn = new HashMap<> ();
	public static ArrayList<User> usersInDatabase = new ArrayList<> ();
	public static ArrayList<Server> servers = new ArrayList<> ();
	public static ArrayList<Game> games = new ArrayList<> ();
	public static ArrayList<Question> questions = new ArrayList<> ();

	public static String encode (String[] args) {

		String ret = "";
		for (String arg : args) {
			ret += arg + "--";
		}
		return ret;
	}

	public static String[] deocde (String message) {

		return message.split ("--");
	}
}

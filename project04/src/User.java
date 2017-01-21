import java.io.*;

/**
 * Created by Beverly on 11/29/2016.
 */
public class User {

	private String username;
	private String password;
	private int score; //user's score
	private int fools; //number of times the user has been fooled
	private int fooled; //How many players you have fooled
	private String userToken;
	private String gameToken;
	private boolean isLeader;

	public User() {

	}

	public User(String username, String password) {

		this.username = username;
		this.password = password;
	}

	public User(String username, String password, int score, int fools, int fooled) {

		this.username = username;
		this.password = password;
		this.score = score;
		this.fools = fools;
		this.fooled = fooled;
	}

	public static void loadUsers() {

		BufferedReader bufferedReader = null;
		File file;
		String line = null;
		try {
			//This is the file where users are located
			file = new File("Resources/UserDatabase");
			//Make a new FileReader and BufferedReader
			FileReader fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);

			//Read in each line from the file, and process the file line by line.
			while ((line = bufferedReader.readLine()) != null) {
				//Split up the parts of the file into a String []
				String[] components = line.split(":");
				//Create a new user from those components. The order of components can be found in
				//the project instructions.
				User user = new User(components[0], components[1], Integer.parseInt(components[2]),
						Integer.parseInt(components[3]), Integer.parseInt(components[4]));
				//Add the user to Shared.usersInDatabase
				Shared.usersInDatabase.add(user);
				//Alert everybody that you found a new user!! (Optional, but recommended.
				System.out.printf("NEW USER FOUND!\n\t%s\n", user.toString());
			}
		} catch (Exception e) {
			//Just in case something broke along the way.
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try { bufferedReader.close(); } catch (Exception e) { e.printStackTrace(); }
			}
		}
	}

	public static User createUser(String username, String password) {

		User user = new User(username, password);
		Shared.usersInDatabase.add(user);
		BufferedWriter bw = null;

		try {
			FileWriter fw = new FileWriter(new File("Resources/UserDatabase"), true);
			  bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println(user.getUsername() + ":" + user.getPassword() + ":" + "0:0:0");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try { bw.close(); } catch (Exception e) { e.printStackTrace(); }
			}
		}

		return user;

	}

	public String toString() {

		return String.format("%s : %s : %d : %d : %d", username, password, score, fools, fooled);
	}

	public String getUsername() {

		return username;
	}

	public void setUsername(String username) {

		this.username = username;
	}

	public String getPassword() {

		return password;
	}

	public void setPassword(String password) {

		this.password = password;
	}

	public int getScore() {

		return score;
	}

	public void setScore(int score) {

		this.score = score;
	}

	public int getFools() {

		return fools;
	}

	public void setFools(int fools) {

		this.fools = fools;
	}

	public int getFooled() {

		return fooled;
	}

	public void setFooled(int fooled) {

		this.fooled = fooled;
	}

	public String getUserToken() {

		return userToken;
	}

	public void setUserToken(String userToken) {

		this.userToken = userToken;
	}

	public String getGameToken() {

		return gameToken;
	}

	public void setGameToken(String gameToken) {

		this.gameToken = gameToken;
	}

	public boolean isLeader() {

		return isLeader;
	}

	public void setLeader(boolean leader) {

		isLeader = leader;
	}
}

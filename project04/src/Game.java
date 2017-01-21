import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ben Scholer on 12/5/2016.
 */
public class Game {
	private int index = 0;
	private ArrayList<Server> servers;
	private String gameKey;
	private Question question;
	private HashMap<User, String> playerAnswers;
	private HashMap<User, String> messages;

	public Question getQuestion() {

		return question;
	}

	public void setPlayerAnswers(HashMap<User, String> playerAnswers) {

		this.playerAnswers = playerAnswers;
	}

	public void setMessages(HashMap<User, String> messages) {

		this.messages = messages;
	}

	public HashMap<User, String> getPlayerAnswers() {


		return playerAnswers;
	}

	public HashMap<User, String> getMessages() {

		return messages;
	}

	public void setQuestion(Question question) {

		this.question = question;
	}

	public Game() {

		this.playerAnswers = new HashMap<>();
		this.messages = new HashMap<User, String>();
	}

	public Game(ArrayList<Server> servers, String gameKey) {

		this.servers = servers;
		this.gameKey = gameKey;
		this.playerAnswers = new HashMap<>();
		this.messages = new HashMap<User, String>();
	}

	public ArrayList<Server> getServers() {

		return servers;
	}

	public String getGameKey() {

		return gameKey;
	}

	public void setGameKey(String gameKey) {

		this.gameKey = gameKey;
	}

	public Question increment() {

		if (index + 1 >= Shared.questions.size()) {
			return null;
		} else {
			index++;
			this.question = new Question(Shared.questions.get(index - 1));

			return Shared.questions.get(index - 1);
		}
	}

	public boolean equals(Object object) {

		String str = (String) object;
		return gameKey.equals(str);
	}
}

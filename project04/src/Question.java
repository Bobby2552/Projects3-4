import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringJoiner;

public class Question {

	private String question;
	private String correct;
	private HashMap<User, String> answers;

	public Question(Question question) {
		this.question = question.question;
		this.correct = question.correct;
		this.answers = question.answers;
	}

	/**
	 * Constructor for Question class.
	 *
	 * @param answers
	 * 		The list of answers, INCLUDING the correct answer.
	 * @param question
	 * 		The question.
	 * @param correct
	 * 		The correct answer.
	 */
	public Question (String question, String correct, HashMap<User, String> answers) {

		this.question = question;
		this.correct = correct;
		this.answers = answers;
	}

	/**
	 * Constructor for Question class.
	 *
	 * @param question
	 * 		The question.
	 * @param correct
	 * 		The correct answer.
	 */
	public Question (String question, String correct) {

		this.question = question;
		this.correct = correct;
		this.answers = new HashMap<>();
		this.answers.put(new User(), correct);
	}

	/**
	 * Constructor for the Question class
	 */
	public Question () {

	}

	public static void loadQuestions () {

		File file;
		String line = null;
		try {
			//This is the file where users are located
			file = new File ("Resources/WordleDeck");
			//Make a new FileReader and BufferedReader
			FileReader fileReader = new FileReader (file);
			BufferedReader bufferedReader = new BufferedReader (fileReader);

			//Read in each line from the file, and process the file line by line.
			while ((line = bufferedReader.readLine ()) != null) {
				//Split up the parts of the file into a String []
				String[] components = line.split (":");
				//Create a new user from those components. The order of components can be found in
				//the project instructions.
				//public Question (String question, String correct)
				Question q = new Question (components[0], components[1]);
				//Add the user to Shared.questions
				Shared.questions.add (q);
				//Alert everybody that you added a new question!! (Optional, but recommended.
				System.out.printf ("NEW QN ADDED!\n\t%s\n", q.toString ());
			}
		} catch (Exception e) {
			//Just in case something broke along the way.
			e.printStackTrace ();
		}
	}

	/**
	 * Used to check to see if an answer is correct.
	 *
	 * @param ans
	 * 		The answer to be checked. Case agnostic.
	 *
	 * @return True if they are the same, false otherwise.
	 */
	public boolean check (String ans) {

		return correct.equalsIgnoreCase (ans);
	}

	/**
	 * Used to retrieve the question.
	 *
	 * @return The question.
	 */
	public String getQuestion () {

		return this.question;
	}

	/**
	 * Used to set the question.
	 *
	 * @param q
	 * 		The question to be set.
	 */
	public void setQuestion (String q) {

		this.question = q;
	}

	/**
	 * Used to retrieve the correct answer.
	 *
	 * @return The correct answer.
	 */
	public String getCorrectAnswer () {

		return this.correct;
	}

	/**
	 * Used to set the correct answer.
	 *
	 * @param c
	 * 		The correct answer to be set.
	 */
	public void setCorrectAnswer (String c) {

		this.correct = c;
	}

	/**
	 * Used to retrieve the answer list.
	 *
	 * @return The list of answers to the question.
	 */
	public HashMap<User, String> getAnswers () {

		return this.answers;
	}

	/**
	 * Used to set the list of answers.
	 *
	 * @param l
	 * 		The list of answers to be set, INCLUDING the correct answer.
	 */
	public void setAnswers (HashMap<User, String> l) {

		this.answers = l;
	}

	/**
	 * Add a new answer to the answers []
	 *
	 * @param answer
	 * 		The answer you wish to add
	 */
	public void addAnswer (User user, String answer) {

		this.answers.put(user, answer);
	}

	/**
	 * Used to generate a human readable String from the question.
	 *
	 * @return A String in the format question-correctAnswer-a0-a1-a2... where a is an answer from the list of answers.
	 */
	public String toString () {

		String ret = "";
		ret = String.format ("%s-%s", this.question, this.correct);
		if (this.answers != null) {
			for (HashMap.Entry entry : this.answers.entrySet()) {
				ret += "-" + entry.getValue();
			}
		}
		return ret;
	}
}

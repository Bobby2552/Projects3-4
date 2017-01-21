import java.util.Arrays;

public class Question {
	
	private String question;
	private String correct;
	private String[] answers;
	
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
	public Question (String question, String correct, String[] answers) {
		
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
	}
	
	/**
	 * Constructor for the Question class
	 */
	public Question ( ) {
		
	}
	
	;
	
	/**
	 * Used to check to see if an answer is correct.
	 *
	 * @param ans
	 * 		The answer to be checked. Case agnostic.
	 *
	 * @return True if they are the same, false otherwise.
	 */
	public boolean check (String ans) {
		
		return correct.equalsIgnoreCase(ans);
	}
	
	/**
	 * Used to retrieve the question.
	 *
	 * @return The question.
	 */
	public String getQuestion ( ) {
		
		return this.question;
	}
	
	/**
	 * Used to retrieve the correct answer.
	 *
	 * @return The correct answer.
	 */
	public String getCorrectAnswer ( ) {
		
		return this.correct;
	}
	
	/**
	 * Used to retrieve the answer list.
	 *
	 * @return The list of answers to the question.
	 */
	public String[] getAnswers ( ) {
		
		return this.answers;
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
	 * Used to set the correct answer.
	 *
	 * @param c
	 * 		The correct answer to be set.
	 */
	public void setCorrectAnswer (String c) {
		
		this.correct = c;
	}
	
	/**
	 * Used to set the list of answers.
	 *
	 * @param l
	 * 		The list of answers to be set, INCLUDING the correct answer.
	 */
	public void setAnswers (String[] l) {
		
		this.answers = l;
	}
	
	/**
	 * Add a new answer to the answers []
	 *
	 * @param answer
	 * 		The answer you wish to add
	 */
	public void addAnswer (String answer) {
		
		answers = Arrays.copyOf(answers, answers.length + 1);
		answers[answers.length - 1] = answer;
	}
	
	/**
	 * Used to generate a human readable String from the question.
	 *
	 * @return A String in the format question-correctAnswer-a0-a1-a2... where an is an answer from the list of answers.
	 */
	public String toString ( ) {
		
		String ret = "";
		ret = String.format("%s-%s", this.question, this.correct);
		if (this.answers != null) {
			for (String str : this.answers) {
				ret += "-" + str;
			}
		}
		return ret;
	}
}

/**
 * This class will store information about a given participant.
 *
 * @author Ben Scholer
 */
public class Participant {
	
	private String username;
	private String message;
	private int cumulativeScore;
	private int playersFooled;
	private int timesFooled;
	
	/**
	 * Creates a new Participant
	 *
	 * @param username
	 * 		The participant's username
	 * @param message
	 * 		The message to the participant
	 * @param cumulativeScore
	 * 		The cumulative score
	 * @param playersFooled
	 * 		Number of players fooled
	 * @param timesFooled
	 * 		Amount of times participant was fooled
	 */
	public Participant (String username, String message, int cumulativeScore, int playersFooled, int timesFooled) {
		
		this.username = username;
		this.message = message;
		this.cumulativeScore = cumulativeScore;
		this.playersFooled = playersFooled;
		this.timesFooled = timesFooled;
	}
	
	/**
	 * Creates a new Participant
	 *
	 * @param username
	 * 		The participant's username
	 * @param cumulativeScore
	 * 		The cumulative score
	 * @param playersFooled
	 * 		Number of players fooled
	 * @param timesFooled
	 * 		Amount of times participant was fooled
	 */
	public Participant (String username, int cumulativeScore, int playersFooled, int timesFooled) {
		
		this.username = username;
		this.cumulativeScore = cumulativeScore;
		this.playersFooled = playersFooled;
		this.timesFooled = timesFooled;
	}
	
	public Participant (String username, int cumulativeScore) {
		
		this.username = username;
		this.cumulativeScore = cumulativeScore;
	}
	
	/**
	 * Get username
	 *
	 * @return Participant's username
	 */
	public String getUsername ( ) {
		
		return username;
	}
	
	/**
	 * Set username
	 *
	 * @param username
	 * 		Participant's username
	 */
	public void setUsername (String username) {
		
		this.username = username;
	}
	
	/**
	 * Get message
	 *
	 * @return Message to user
	 */
	public String getMessage ( ) {
		
		return message;
	}
	
	/**
	 * Set message
	 *
	 * @param message
	 * 		Message to user
	 */
	public void setMessage (String message) {
		
		this.message = message;
	}
	
	/**
	 * Get cumulative score
	 *
	 * @return Cumulative score
	 */
	public int getCumulativeScore ( ) {
		
		return cumulativeScore;
	}
	
	/**
	 * Set cumulative score
	 *
	 * @param cumulativeScore
	 * 		Cumulative score
	 */
	public void setCumulativeScore (int cumulativeScore) {
		
		this.cumulativeScore = cumulativeScore;
	}
	
	/**
	 * Get number of players fooled by participant
	 *
	 * @return Number of fooled players
	 */
	public int getPlayersFooled ( ) {
		
		return playersFooled;
	}
	
	/**
	 * Set number of players fooled by participant
	 *
	 * @param playersFooled
	 * 		Number of fooled players
	 */
	public void setPlayersFooled (int playersFooled) {
		
		this.playersFooled = playersFooled;
	}
	
	/**
	 * Get number of times participant was fooled
	 *
	 * @return Number of times participant was fooled
	 */
	public int getTimesFooled ( ) {
		
		return timesFooled;
	}
	
	/**
	 * Set the number of times participant was fooled
	 *
	 * @param timesFooled
	 * 		Number of times participant was fooled
	 */
	public void setTimesFooled (int timesFooled) {
		
		this.timesFooled = timesFooled;
	}
	
	/**
	 * Returns a String containing details about the participant
	 *
	 * @return username => Score : score | Fooled : fooled player(s) | Fooled by : timesFooled player(s)
	 */
	@Override
	public String toString ( ) {
		
		return String.format("%s => Score : %d | Fooled : %d player(s) | " +
						"Fooled by : %d player(s)", this.username, this.cumulativeScore,
				this.playersFooled, this.timesFooled);
	}
}

/**
 * Created by Beverly on 12/1/2016.
 */
public class Register {
    /*(a) Username
    * - Cannot be empty
    * -Length has to be <10 char
    * -Can only have alphanumeric characters/ underscores
    *
    * (b) Password
    * -Cannot be empty
    * -Length has to be <10char
    * -Can only have alphanumeric char, # & $ or *
    * -Should have >= 1 uppercase
    * -Should have >= 1 digit
    * */

    /*TODO: Settle the response parts?????*/

	private String username;
	private String password;

	public Register (User user) {

		this.username = user.getUsername ();
		this.password = user.getPassword ();
	}

	public boolean isUser () {

		if (this.username == null) {
			return false;
		} else if (this.username.length () < 10) {
			return false;
		} else if (! this.username.matches ("^.*[^a-zA-Z0-9 ].*$") && ! this.username.contains ("_")) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isPass () {

		char ch = this.password.charAt (0);

		if (this.password == null) {
			return false;
		} else if (this.password.length () < 10) {
			return false;
		} else if (! this.password.matches ("^.*[^a-zA-Z0-9 ].*$") && ! this.username.contains ("#") && ! this.username.contains ("&") && ! this.username.contains ("$") && ! this.username.contains ("*")) {
			return false;
		} else if (! (Character.isUpperCase (ch))) {
			for (int i = 1; i < password.length (); i++) {
				ch = password.charAt (i);

				if (! Character.isUpperCase (ch)) {
					return false;
				}
			}
		} else if (! (Character.isDigit (ch))) {
			for (int i = 1; i < password.length (); i++) {
				ch = password.charAt (i);

				if (! Character.isDigit (ch)) {
					return false;
				}
			}
		}
		return true;
	}
}

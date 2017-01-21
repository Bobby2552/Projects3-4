import javax.swing.*;
import java.awt.*;

/**
 * Created by bscholer on 11/2/16.
 */

//Extend JPanel, that way we can put these inside of the JFrame created by GUI.java
public class Login extends JFrame {
	
	//Creating a new GridBagConstraints object
	GridBagConstraints constraints = new GridBagConstraints( );
	//Each JPanel needs a backend object, that way we can communicate with the server
	Backend backend;
	JLabel passwordLabel;
	//The JTextFields where the user inputs username and password.
	JTextField username;
	//JPasswordField works the same as JTextField, except it makes the input show up as dots.
	JPasswordField password;
	//The login and register JButtons
	JButton login;
	JButton register;
	
	public Login (Backend backend) {
		
		super("FoilMaker");
		this.backend = backend;
		setLayout(new BorderLayout( ));
		JPanel panel = new JPanel(new GridBagLayout( ));
		panel.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		JLabel topLabel = new JLabel("FoilMaker!");
		JLabel bottomLabel = new JLabel("Welcome!");
		
		//Setting margins
		constraints.insets.right = 10;
		constraints.insets.left = 10;
		constraints.insets.top = 10;
		constraints.insets.bottom = 10;
		
		JLabel usernameLabel = new JLabel("Username");
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.insets.top = 100;
		constraints.insets.bottom = 5;
		panel.add(usernameLabel, constraints);
		
		passwordLabel = new JLabel("Password");
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets.top = 5;
		constraints.insets.bottom = 10;
		panel.add(passwordLabel, constraints);
		
		username = new JTextField( );
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.insets.top = 100;
		constraints.insets.bottom = 5;
		panel.add(username, constraints);
		
		password = new JPasswordField( );
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.insets.top = 5;
		constraints.insets.bottom = 10;
		panel.add(password, constraints);
		
		login = new JButton("Login");
		login.addActionListener(e -> {
			String ret = backend.login(username.getText( ), password.getText( ));
			if (ret.equals("UNKNOWNUSER")) {
				bottomLabel.setText("Invalid Username!");
			} else if (ret.equals("INVALIDUSERPASSWORD")) {
				bottomLabel.setText("Invalid Password!");
			} else if (ret.equals("USERALREADYLOGGEDIN")) {
				bottomLabel.setText("User already logged in!");
			} else {
				setVisible(false);
				Play play = new Play(backend);
				play.setBounds(getBounds( ));
				play.setVisible(true);
			}
			
		});
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.PAGE_END;
		constraints.insets.bottom = 100;
		panel.add(login, constraints);
		
		register = new JButton("Register");
		register.addActionListener(e -> {
			// This sends a CREATENEWUSER request to the server and sets 'ret' to the message returned.
			String ret = backend.newUser(username.getText( ), password.getText( ));
			// This checks what the server returns against the list of possible errors.
			if (ret.equals("INVALIDUSERNAME")) {
				bottomLabel.setText("Username field empty!");
			} else if (ret.equals("INVALIDUSERPASSWORD")) {
				bottomLabel.setText("Password field empty!");
			} else if (ret.equals("USERALREADYEXISTS")) {
				bottomLabel.setText("User already exists. Please Login.");
			} else {
				bottomLabel.setText("New User Created!");
			}
		});
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.PAGE_END;
		constraints.weighty = 1;
		constraints.insets.bottom = 100;
		panel.add(register, constraints);
		
		add(panel, BorderLayout.CENTER);
		add(topLabel, BorderLayout.PAGE_START);
		topLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(bottomLabel, BorderLayout.SOUTH);
	}
}

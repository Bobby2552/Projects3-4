import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by bscholer on 11/9/16.
 */
public class JoinGame extends JFrame {
	
	//Creating a new GridBagConstraints object
	GridBagConstraints constraints = new GridBagConstraints( );
	//Each JPanel needs a backend object, that way we can communicate with the server
	Backend backend;
	JLabel enterGameKey = new JLabel("Enter the game key to join a game");
	JTextField gameKey;
	JButton join;
	
	public JoinGame (Backend backend) {
		
		super("FoilMaker");
		this.backend = backend;
		
		// Log the user out automatically.
		addWindowListener(new WindowAdapter( ) {
			
			@Override
			public void windowClosing (WindowEvent e) {
				
				super.windowClosing(e);
				backend.logout( );
			}
		});
		
		setLayout(new BorderLayout( ));
		JPanel panel = new JPanel(new GridBagLayout( ));
		panel.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		JLabel topLabel = new JLabel(backend.getUsername( ));
		JLabel bottomLabel = new JLabel("Welcome!");
		
		//Setting margins
		constraints.insets.right = 10;
		constraints.insets.left = 10;
		constraints.insets.top = 10;
		constraints.insets.bottom = 10;
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		enterGameKey.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(enterGameKey, constraints);
		
		gameKey = new JTextField( );
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1;
		gameKey.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(gameKey, constraints);
		
		join = new JButton("Start Game");
		join.addActionListener(e -> {
			backend.setGameToken(gameKey.getText( ));
			String ret = backend.joinGame( );
			if (ret.equals("GAMEKEYNOTFOUND")) {
				bottomLabel.setText("Invalid game key!");
			} else if (ret.equals("USERNOTLOGGEDIN")) {
				bottomLabel.setText("User isn't logged in!");
			} else if (ret.equals("FAILURE")) {
				bottomLabel.setText("User already playing the game");
			} else {
				setVisible(false);
				Waiting waiting = new Waiting(backend);
				waiting.setBounds(getBounds( ));
				waiting.setVisible(true);
			}
		});
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 3;
		join.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(join, constraints);
		
		add(panel, BorderLayout.CENTER);
		add(topLabel, BorderLayout.PAGE_START);
		topLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(bottomLabel, BorderLayout.SOUTH);
	}
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by bscholer on 11/4/16.
 */
public class Play extends JFrame {
	
	//Creating a new GridBagConstraints object
	GridBagConstraints constraints = new GridBagConstraints( );
	//Each JPanel needs a backend object, that way we can communicate with the server
	Backend backend;
	//Start and Join buttons
	JButton start;
	JButton join;
	
	public Play (Backend backend) {
		
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
		
		// Setup the inner panel
		JPanel panel = new JPanel(new GridBagLayout( ));
		panel.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		//Set up the labels
		JLabel topLabel = new JLabel(backend.getUsername( ));
		JLabel bottomLabel = new JLabel("Welcome!");
		
		//Setting margins
		constraints.insets.right = 10;
		constraints.insets.left = 10;
		constraints.insets.top = 10;
		constraints.insets.bottom = 10;
		
		start = new JButton("Start New Game");
		start.addActionListener(e -> {
			String ret = backend.startNewGame( );
			if (ret.equals("FAILURE")) {
				bottomLabel.setText("Something went wrong!");
			} else if (ret.equals("USERNOTLOGGEDIN")) {
				bottomLabel.setText("User isn't logged in!");
			} else {
				setVisible(false);
				StartNewGame startNewGame = new StartNewGame(backend);
				startNewGame.setBounds(getBounds( ));
				startNewGame.setVisible(true);
			}
			
		});
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		panel.add(start, constraints);
		
		join = new JButton("Join a Game");
		join.addActionListener(e -> {
			setVisible(false);
			JoinGame joinGame = new JoinGame(backend);
			joinGame.setBounds(getBounds( ));
			joinGame.setVisible(true);
		});
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 1;
		constraints.gridy = 0;
		panel.add(join, constraints);
		
		
		add(panel, BorderLayout.CENTER);
		add(topLabel, BorderLayout.PAGE_START);
		topLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(bottomLabel, BorderLayout.SOUTH);
	}
	
}

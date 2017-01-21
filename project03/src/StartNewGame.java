import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by bscholer on 11/7/16.
 */
public class StartNewGame extends JFrame {
	
	//Creating a new GridBagConstraints object
	GridBagConstraints constraints = new GridBagConstraints( );
	//Each JPanel needs a backend object, that way we can communicate with the server
	Backend backend;
	//Info JLabel
	JLabel others = new JLabel("Others should use this key to join your game");
	JLabel gameKey;
	JPanel participants;
	JButton start;
	JTextArea textArea;
	String errorMessage;
	JLabel bottomLabel = new JLabel("Game started: You are the leader.");
	
	public StartNewGame (Backend backend) {
		
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
		
		//Setting margins
		constraints.insets.right = 10;
		constraints.insets.left = 10;
		constraints.insets.top = 10;
		constraints.insets.bottom = 10;
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		others.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(others, constraints);
		
		gameKey = new JLabel(backend.getGameToken( ));
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1;
		gameKey.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(gameKey, constraints);
		
		participants = new JPanel(new GridLayout( ));
		participants.setBorder(new TitledBorder(new EtchedBorder( ), "Participants"));
		textArea = new JTextArea(10, 10);
		textArea.setBackground(Color.pink);
		textArea.setEditable(false);
		participants.add(textArea);
		constraints.gridx = 0;
		constraints.gridy = 2;
		gameKey.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(participants, constraints);
		
		start = new JButton("Start Game");
		start.addActionListener(e -> {
			//Kill the thread, don't accept any more new participants
			Shared.runParticipantChecking = false;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
			}
			
			try {
				String ret = (String) backend.launchGame( );
				if (ret.equals("INVALIDGAMETOKEN")) {
					bottomLabel.setText("Invalid game key!");
				} else if (ret.equals("USERNOTLOGGEDIN")) {
					bottomLabel.setText("User isn't logged in!");
				} else if (ret.equals("USERNOTGAMELEADER")) {
					bottomLabel.setText("User is not game leader!");
				}
			} catch (ClassCastException exception) {
				setVisible(false);
				NewSuggestion newSuggestion = new NewSuggestion(backend);
				newSuggestion.setBounds(getBounds( ));
				newSuggestion.setVisible(true);
			}
		});
		start.setEnabled(false);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 3;
		start.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(start, constraints);
		
		add(panel, BorderLayout.CENTER);
		add(topLabel, BorderLayout.PAGE_START);
		topLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(bottomLabel, BorderLayout.SOUTH);
		
		MyThread thread = new MyThread("Thread");
		thread.start( );
	}
	
	class MyThread implements Runnable {
		
		private Thread t;
		private String threadName;
		
		public MyThread (String threadName) {
			
			this.threadName = threadName;
		}
		
		@Override
		public void run ( ) {
			
			Participant participant = null;
			while (Shared.runParticipantChecking) {
				participant = backend.checkForNewParticipants( );
				if (participant == null) continue;
				textArea.setText(textArea.getText( ) + participant.getUsername( ) + "\n");
				if (backend.getParticipants( ).size( ) >= 1) {
					start.setEnabled(true);
					bottomLabel.setText("Press <Start Game> to start game");
					System.out.println("New participant.");
				} else start.setEnabled(false);
			}
		}
		
		public void start ( ) {
			
			if (t == null) {
				t = new Thread(this, threadName);
				t.start( );
			}
		}
	}
}

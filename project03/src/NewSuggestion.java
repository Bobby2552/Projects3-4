import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by bscholer on 11/10/16.
 */
public class NewSuggestion extends JFrame {
	
	//Creating a new GridBagConstraints object
	GridBagConstraints constraints = new GridBagConstraints( );
	//Each JPanel needs a backend object, that way we can communicate with the server
	Backend backend;
	JLabel whatIs = new JLabel("What is the word for:");
	JPanel suggestion;
	JTextField yourSuggestion;
	JButton submit;
	JLabel bottomLabel = new JLabel("Enter your suggestion");
	
	public NewSuggestion (Backend backend) {
		
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
		constraints.insets.top = 50;
		constraints.insets.bottom = 50;
		
		whatIs = new JLabel(String.format("What is the word for: \n%s",
				backend.getQuestion( ).getQuestion( )));
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		whatIs.setHorizontalAlignment(SwingConstants.CENTER);
		whatIs.setVerticalAlignment(SwingConstants.TOP);
		panel.add(whatIs, constraints);
		
		suggestion = new JPanel(new GridBagLayout( ));
		suggestion.setBorder(new TitledBorder(new EtchedBorder( ), "Your Suggestion"));
		GridBagConstraints inner = new GridBagConstraints( );
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1;
		yourSuggestion = new JTextField( );
		inner.fill = GridBagConstraints.HORIZONTAL;
		inner.insets.top = 10;
		inner.insets.bottom = 10;
		inner.insets.left = 10;
		inner.insets.right = 10;
		inner.gridx = 0;
		inner.gridy = 0;
		inner.weightx = 1;
		inner.weighty = 1;
		yourSuggestion.setHorizontalAlignment(SwingConstants.CENTER);
		suggestion.add(yourSuggestion, inner);
		panel.add(suggestion, constraints);
		
		submit = new JButton("Submit Suggestion");
		submit.addActionListener(e -> {
			backend.sendPlayerSuggestion(yourSuggestion.getText( ));
			String ret = "";
			if (ret.equals("USERNOTLOGGEDIN")) {
				bottomLabel.setText("User not logged in!");
			} else if (ret.equals("INVALIDGAMETOKEN")) {
				bottomLabel.setText("Invalid Game Token!");
			} else if (ret.equals("UNEXPECTEDMESSAGETYPE")) {
				bottomLabel.setText("Invalid Message Type!");
			} else if (ret.equals("UNEXPECTEDMESSAGEFORMAT")) {
				bottomLabel.setText("Invalid Message Format!");
			} else {
				SuggestionThread thread = new SuggestionThread("Thread");
				thread.start( );
				bottomLabel.setText("Waiting for all users to make suggestions");
			}
		});
		constraints.gridy = 2;
		submit.setHorizontalAlignment(SwingConstants.CENTER);
		submit.setVerticalAlignment(SwingConstants.TOP);
		panel.add(submit, constraints);
		
		add(panel, BorderLayout.CENTER);
		add(topLabel, BorderLayout.PAGE_START);
		topLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(bottomLabel, BorderLayout.SOUTH);
	}
	
	class SuggestionThread implements Runnable {
		
		private Thread t;
		private String threadName;
		
		public SuggestionThread (String threadName) {
			
			this.threadName = threadName;
		}
		
		@Override
		public void run ( ) {
			
			Question question;
			while (Shared.runSuggestionsSubmittedChecking) {
				question = backend.checkForSubmissions( );
				if (question == null) continue;
				Shared.runSuggestionsSubmittedChecking = false;
				setVisible(false);
				Choose choose = new Choose(backend);
				choose.setBounds(getBounds( ));
				choose.setVisible(true);
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

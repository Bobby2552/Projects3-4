import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by bscholer on 11/10/16.
 */
public class Waiting extends JFrame {
	
	//Creating a new GridBagConstraints object
	GridBagConstraints constraints = new GridBagConstraints( );
	//Each JPanel needs a backend object, that way we can communicate with the server
	Backend backend;
	JLabel bottomLabel = new JLabel("Joined Game: Waiting for leader");
	JLabel waiting = new JLabel("Waiting for leader ...");
	
	public Waiting (Backend backend) {
		
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
		waiting.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(waiting, constraints);
		
		add(panel, BorderLayout.CENTER);
		add(topLabel, BorderLayout.PAGE_START);
		topLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(bottomLabel, BorderLayout.SOUTH);
		
		WaitingThread thread = new WaitingThread("Thread");
		thread.start( );
	}
	
	class WaitingThread implements Runnable {
		
		private Thread t;
		private String threadName;
		
		public WaitingThread (String threadName) {
			
			this.threadName = threadName;
		}
		
		@Override
		public void run ( ) {
			
			Question question;
			while (Shared.runGameStartedChecking) {
				question = backend.checkIfGameHasStarted( );
				if (question == null) continue;
				Shared.runGameStartedChecking = false;
				setVisible(false);
				NewSuggestion newSuggestion = new NewSuggestion(backend);
				newSuggestion.setBounds(getBounds( ));
				newSuggestion.setVisible(true);
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

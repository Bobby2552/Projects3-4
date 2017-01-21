import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bscholer on 11/11/16.
 */
public class Choose extends JFrame {
	
	//Creating a new GridBagConstraints object
	GridBagConstraints constraints = new GridBagConstraints( );
	//Each JPanel needs a backend object, that way we can communicate with the server
	Backend backend;
	ArrayList<String> answers;
	JLabel chooseAnswer = new JLabel("Pick your option below");
	ArrayList<JRadioButton> jButtons = new ArrayList<>( );
	JPanel buttonPanel;
	ButtonGroup buttonGroup = new ButtonGroup( );
	JButton submit;
	JLabel bottomLabel = new JLabel("Pick your choice");
	String selected = "";
	
	
	public Choose (Backend backend) {
		
		super("FoilMaker");
		this.backend = backend;
		answers = new ArrayList<>( );
		String[] answerArray = backend.getQuestion( ).getAnswers( );
		for (String str : answerArray) {
			answers.add(str);
		}
		Collections.shuffle(answers);
		
		//Log the user out automatically.
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
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.NORTH;
		chooseAnswer.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(chooseAnswer, constraints);
		
		buttonPanel = new JPanel( );
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		buttonPanel.setBorder(new TitledBorder(new EtchedBorder( ), "Answers"));
		for (int i = 0; i < answers.size( ); i++) {
			String text = answers.get(i);
			JRadioButton button = new JRadioButton(text);
			button.addActionListener(e -> selected = text);
			button.setAlignmentX(Component.CENTER_ALIGNMENT);
			jButtons.add(button);
			buttonGroup.add(jButtons.get(i));
			buttonPanel.add(jButtons.get(i));
		}
		panel.add(buttonPanel, constraints);
		
		submit = new JButton("Submit Answer");
		submit.addActionListener(e -> {
			backend.sendPlayerChoice(selected);
			String str = "";
			if (str.equals("USERNOTLOGGEDIN")) {
				bottomLabel.setText("User not logged in!");
			} else if (str.equals("INVALIDGAMETOKEN")) {
				bottomLabel.setText("Invalid Game Token!");
			} else if (str.equals("UNEXPECTEDMESSAGETYPE")) {
				bottomLabel.setText("Invalid Message Type!");
			} else if (str.equals("UNEXPECTEDMESSAGEFORMAT")) {
				bottomLabel.setText("Invalid Message Format!");
			} else {
				bottomLabel.setText("Waiting for all users to make choices");
				ChooseThread thread = new ChooseThread("Thread");
				thread.start( );
			}
		});
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.SOUTH;
		chooseAnswer.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(submit, constraints);
		
		add(panel, BorderLayout.CENTER);
		add(topLabel, BorderLayout.PAGE_START);
		topLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(bottomLabel, BorderLayout.SOUTH);
	}
	
	class ChooseThread implements Runnable {
		
		private Thread t;
		private String threadName;
		
		public ChooseThread (String threadName) {
			
			this.threadName = threadName;
		}
		
		@Override
		public void run ( ) {
			
			ArrayList<Participant> participants;
			while (Shared.runChoiceChecking) {
				participants = backend.checkForRoundResults( );
				if (participants == null) continue;
				Shared.runChoiceChecking = false;
				setVisible(false);
				Results results = new Results(backend);
				results.setBounds(getBounds( ));
				results.setVisible(true);
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

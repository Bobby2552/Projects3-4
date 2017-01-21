import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Results extends JFrame {
    
    //Creating a new GridBagConstraints object
    GridBagConstraints constraints = new GridBagConstraints( );
    //Each JPanel needs a backend object, that way we can communicate with the server
    Backend backend;
    JPanel round;
    JScrollPane scrollPane;
    JTextArea message;
    JPanel overall;
    JTextArea overallResults;
    JButton next;
    Participant me;
    JLabel bottomLabel = new JLabel("Click <Next Round> when ready");
    
    
    public Results(Backend backend) {
        super("FoilMaker");
        this.backend = backend;
        for (int i = 0; i < backend.getParticipants().size(); i++) {
            if (backend.getParticipants().get(i).getUsername().equals(backend.getUsername())) {
                me = backend.getParticipants( ).get(i);
            }
        }
    
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
        
        round = new JPanel(new GridLayout());
        round.setBorder(new TitledBorder(new EtchedBorder(), "Round Result"));
        message = new JTextArea(5, 10);
        message.setBackground(Color.pink);
        message.setEditable(false);
        message.setText(me.getMessage());
        round.add(message);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.weightx = 1;
        panel.add(round, constraints);
    
        overall = new JPanel(new GridLayout( ));
        overall.setBorder(new TitledBorder(new EtchedBorder( ), "Overall Results"));
        overallResults = new JTextArea(10, 10);
        overallResults.setBackground(Color.pink);
        overallResults.setEditable(false);
        scrollPane = new JScrollPane(overallResults);
        overall.add(scrollPane);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 1;
        constraints.weightx = 1;
        panel.add(overall, constraints);
        
        String result = "";
        for (int i = 0; i < backend.getParticipants().size(); i++) {
            result += backend.getParticipants().get(i).toString() + "\n";
        }
        overallResults.setText(result);
    
        next = new JButton("Next Round");
        if (backend.getQuestion() == null){
            next.setEnabled(false);
            bottomLabel.setText("Game over!");
        }
        next.addActionListener(e -> {
            setVisible(false);
            Shared.runChoiceChecking = true;
            Shared.runSuggestionsSubmittedChecking = true;
            NewSuggestion newSuggestion = new NewSuggestion(backend);
            newSuggestion.setBounds(getBounds( ));
            newSuggestion.setVisible(true);
        });
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        next.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(next, constraints);
        
        add(panel, BorderLayout.CENTER);
        add(topLabel, BorderLayout.PAGE_START);
        topLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(bottomLabel, BorderLayout.SOUTH);
    }
}
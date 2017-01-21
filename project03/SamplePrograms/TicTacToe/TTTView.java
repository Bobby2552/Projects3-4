import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * The View Class of the Tic Tac Toe program. This class handles all user interaction. It connects to the controller
 * to inform it of user choices and acts in response to requests from the controller.
 *
 * Created by sunil on 10/14/16.
 */
public class TTTView extends JFrame implements ActionListener {
    private JButton[][]  buttons = new JButton[3][3];  //the 9 buttons for the game.
    private JLabel statusMessage = new JLabel("Welcome!"); //a status message for the user
    private TicTacToe controller;  //the controller object handling this view

    public TTTView (TicTacToe controller){
        this.controller = controller;
        setUpGUI();
        this.pack();
        this.setVisible(true);
    }

    public void setStatusMessage(String msg){
        statusMessage.setText(msg);
    }

    /**
     * This method sets up the Tic Tac Toe view frame;
     */
    private void setUpGUI(){
        Container contentPane;
            //set the default size of this window
        this.setSize(200,200);

            //get the contentPane and set its layout to BorderLayout
        contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());

            //add the status message to appear at the bottom of the window
        contentPane.add(statusMessage, BorderLayout.SOUTH);

            //create the button panel using GridLayout
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new GridLayout(3,3));

            //create each of the buttons and add it to button panel
        for(int i=0; i< 3; i++)
            for(int j=0; j<3; j++){
                buttons[i][j] = new JButton(" ");
                buttons[i][j].addActionListener(this);
                tempPanel.add(buttons[i][j]);
            }

            //add the button panel to the window's contentpane
        contentPane.add(tempPanel, BorderLayout.CENTER);
    }

    /**
     * The actionPerfomed method that is called when any button in clicked by the user.
     * @param e
     */
    public void actionPerformed(ActionEvent e){
            // determine the row and column number for the clicked button
            // inform the controller about this user action
        JButton clicked = (JButton) e.getSource();
        for(int row=0; row< 3; row++)
            for(int col=0; col<3; col++){
                if(clicked == buttons[row][col]){
                    controller.cellPicked(row, col);
                }
            }
    }

    /**
     *  This method is called by the controller to inform the view to update its display to indicate the user that
     * has picked this cell. The corresponding button's label is changed and it is disabled so that the user can no
     * longer click it.
     * @param row the row number for the picked cell (0 - 2)
     * @param col the column number for the picked cell (0 - 2)
     * @param sym the character symbol to use for changing the display
     */
    public void changeLabel(int row, int col, char sym){
        buttons[row][col].setText("" + sym);
        buttons[row][col].setEnabled(false);
    }

    /**
     * This method is called by the controller when a player has won the gam.e
     * @param symbol the symbol of the player that has won the game.
     */
    public void weHaveAWinner(char symbol) {

        gameOver("Player " + symbol + " has WON!!!!");
    }

    /**
     * This method is called by the controller when the game is a draw.
     */
    public void weHaveADraw(){
        gameOver("Game is DRAWN!!!!");
    }

    /**
     * This method is called to reset the view to initialize a new game.
     */

    public void startNewGame(){
        for(int row=0; row< 3; row++)
            for(int col=0; col<3; col++) {
                buttons[row][col].setText("" + ' ');
                buttons[row][col].setEnabled(true);
            }
    }


    /**
     * This method is called to end the game. All buttons are disabled, and the corresponding message is displayed to
     * the user.
     * The user is also prompted to ask if they would like to play again. If yes, then the controller is informed of
     * this choice, else the game is terminated.
     * @param msg
     */
    public void gameOver(String msg){

        setStatusMessage(msg);
        for(int i=0; i< 3; i++)
            for(int j=0; j<3; j++)
                buttons[i][j].setEnabled(false);
        if( JOptionPane.showConfirmDialog(null, msg + " Play again?")
            != JOptionPane.NO_OPTION){
            controller.startNewGame();
        } else
            controller.endGame();
    }



}

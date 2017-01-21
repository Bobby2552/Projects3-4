/**
 * This class defines the main Controller class for a simple GUI-based Tic Tac Toe game. The solution is build
 * using the MVC pattern, with the controller mediating access to the model and the view. The game is played by two
 * players, who alternate to take turns.
 *
 * Created by sunil on 10/14/16.
 */
public class TicTacToe {
    private TTTModel model;
    private TTTView view;

    public TicTacToe (){
        model = new TTTModel(this);
        view = new TTTView(this);
    }

    /**
     * The method called by the view when a player picks a cell in the view. The view is asked to
     * change the reflect the user's choice, and the model informed of this choice. The model is also
     * checked to see the current status of the game, which is used to adjust the message prompt for the
     * user on the view.
     *
     * @param row the row number on which the cell was picked (0 -- 2)
     * @param col the column number on which the cell was picked (0 -- 2)
     */
    public void cellPicked(int row, int col){
        TTTModel.STATUS status;
        char currentSymbol;

            //save the symbol of the current user
        currentSymbol = model.getPlayerSymbol();

            //made the update to the model
        model.cellPicked(row, col);

            //adjust the view to indicate the user's choice
        view.changeLabel(row, col, currentSymbol);
        showNextPlayerMessage();

            //check the current status of the game
        status = model.checkCurrentStatus();
        switch (status) {
            case PLAYER0WINS:
            case PLAYER1WINS:
                view.weHaveAWinner(currentSymbol);
                break;
            case DRAW:
                view.weHaveADraw();
                break;
        }

    }

    private void showNextPlayerMessage(){
        view.setStatusMessage("Player " + model.getPlayerSymbol() + "'s turn.");
    }
    /**
     * Reset and start a new game.
     *
     */
    public void startNewGame(){
        model.startNewGame();
        view.startNewGame();
        showNextPlayerMessage();
    }

    public void endGame(){
        System.exit(0);
    }
    /**
     * Main method: create a game object and start a new game.
     * @param args
     */
    public static void main(String[] args) {
        TicTacToe game;
        game = new TicTacToe();
        game.startNewGame();
    }


}

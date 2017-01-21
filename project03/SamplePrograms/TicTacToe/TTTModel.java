/**
 * The Model class for the Tic Tac Toe program. This class manages all the relevant state of the game:
 *  the current choices for each cell, the next player to play.
 * Created by sunil on 10/14/16.
 */
public class TTTModel {

        //the STATUS data type.
    public static enum STATUS {
        INPLAY,
        DRAW,
        PLAYER0WINS,
        PLAYER1WINS
    }

    private char[][] cells = new char[3][3]; //the contents of each cell indicating which cell has been filled by which player
    private int nextPlayer; // the index of the next player to play: 0 or 1
    TicTacToe controller;   // a reference to the controller object (not really used).
    private char[] symbols = {'X', 'O'};  // the symbols for each player


    public TTTModel(TicTacToe controller) {
        this.controller = controller;
        startNewGame();
    }

    /**
     * Starting a new game: reset all the entries in the cells to blank, and set next player to player 0.
     */
    public void startNewGame(){
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                cells[i][j] = ' ';

        nextPlayer = 0;
    }


    public char getPlayerSymbol() {
        return symbols[nextPlayer];
    }

    /**
     * This method handles the current user picking a given cell as their next move
     * @param row the row number for the cell (0 - 2)
     * @param col the column number for the cell (0 - 2)
     */
    public void cellPicked(int row, int col) {
        if(cells[row][col] != ' '){
            System.out.println("Something is wrong!"); //should not have already been picked
        }
        cells[row][col] = symbols[nextPlayer];
        nextPlayer = (nextPlayer + 1) % 2; //switch to the other player
    }

    /**
     * This method determines and returns the current status of the game: is it in play, has one of the players
     * won, or is it a draw with no more options for play
     * @return the current status of the game.
     */
    public STATUS checkCurrentStatus() {
        STATUS status = STATUS.INPLAY;
        int row, col;

        //check each row
        for (row = 0; row < 3; row++) {
            status = checkLine(row, 0, 0, 1);
            if (status != STATUS.INPLAY)
                return status;
        }

        //check each col
        for (col = 0; col < 3; col++) {
            status = checkLine(0, col, 1, 0);
            if (status != STATUS.INPLAY)
                return status;
        }
        //check both diagonals
        status = checkLine(0, 0, 1, 1);
        if (status != STATUS.INPLAY)
            return status;

        status = checkLine(2, 0, -1, 1);
        if (status != STATUS.INPLAY)
            return status;

        //check if game is drawn.
        for(row = 0 ; row < 3; row++)
            for(col = 0; col < 3; col++)
                if(cells[row][col] == ' ')
                    return STATUS.INPLAY;

        return STATUS.DRAW;
    }


    /**
     * This method returns the status of the game with respect to a single
     * line of three cells which may be a row, column or diagonal.
     * @param startRow the row number of the first cell in the line
     * @param startCol the column number of the first cell in the line
     * @param rowStep the increment to compute the row number of the next cell in the line
     * @param colStep the increment to compute the column number of the next cell in the line
     * @return the status of the game with respect to JUST this one line. This method does not check for, or return
     * STATUS.DRAW
     */
    private STATUS checkLine(int startRow, int startCol, int rowStep, int colStep){
        char c1, c2, c3;
        int row, col;
        row = startRow;
        col  = startCol;
        c1 = cells[row][col];

        row += rowStep;
        col += colStep;
        c2 = cells[row][col];

        row += rowStep;
        col += colStep;
        c3 = cells[row][col];

        if( (c1 == c2) && (c2 == c3))
            if(c1 == symbols[0])
                return STATUS.PLAYER0WINS;
            else if (c1 == symbols[1])
                return STATUS.PLAYER1WINS;

        return STATUS.INPLAY;
    }
}
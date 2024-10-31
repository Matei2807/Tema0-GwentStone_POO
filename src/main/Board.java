package main;

public final class Board {
    public static final int ROWS = 4;
    public static final int COLUMNS = 5;
    public static final int PLAYER1_FRONT_ROW = 2;
    public static final int PLAYER2_FRONT_ROW = 1;
    public static final int PLAYER1_BACK_ROW = 3;
    public static final int PLAYER2_BACK_ROW = 0;

    private final Card[][] board;

    public Board() {
        this.board = new Card[ROWS][COLUMNS];
    }

    /**
     * Adds a card to the board.
     * @param card the card to be added
     * @param player the player that adds the card
     * @return true if the card was added, false otherwise
     */
    public boolean addCard(final Card card, final int player) {
        int row;
        if (player == 1 && card.isLastRow()) {
            row = PLAYER1_BACK_ROW;
        } else if (player == 2 && card.isLastRow()) {
            row = PLAYER2_BACK_ROW;
        } else if (player == 1) {
            row = PLAYER1_FRONT_ROW;
        } else {
            row = PLAYER2_FRONT_ROW;
        }
        for (int i = 0; i < COLUMNS; i++) {
            if (board[row][i] == null) {
                board[row][i] = new Card(card);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes all dead cards from the board
     */
    public void checkDeadCards() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (board[i][j] != null && board[i][j].getHealth() <= 0) {
                    board[i][j] = null;
                }
                for (int k = j; k < COLUMNS - 1; k++) {
                    if (board[i][k] == null && board[i][k + 1] != null) {
                        board[i][k] = board[i][k + 1];
                        board[i][k + 1] = null;
                    }
                }
            }
        }
    }

    /**
     * Unfreezes all cards on the board
     * @param currentPlayer the current player
     */
    public void checkForzenCards(final int currentPlayer) {
        if (currentPlayer == 1) {
            for (int i = PLAYER1_FRONT_ROW; i <= PLAYER1_BACK_ROW; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    if (board[i][j] != null) {
                        board[i][j].setFrozen(false);
                    }
                }
            }
        } else {
            for (int i = PLAYER2_BACK_ROW; i <= PLAYER2_FRONT_ROW; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    if (board[i][j] != null) {
                        board[i][j].setFrozen(false);
                    }
                }
            }
        }
    }

    /**
     * Resets the hasAttacked attribute of all cards on the board
     * @param currentPlayer the current player
     */
    public void checkUsedCards(final int currentPlayer) {
        if (currentPlayer == 1) {
            for (int i = PLAYER1_FRONT_ROW; i <= PLAYER1_BACK_ROW; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    if (board[i][j] != null) {
                        board[i][j].setHasAttacked(false);
                    }
                }
            }
        } else {
            for (int i = PLAYER2_BACK_ROW; i <= PLAYER2_FRONT_ROW; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    if (board[i][j] != null) {
                        board[i][j].setHasAttacked(false);
                    }
                }
            }
        }
    }

    /**
     * Checks if a tank is present on the board for the opponent
     * @param currentPlayer the current player
     * @return true if a tank is present, false otherwise
     */
    public boolean checkTank(final int currentPlayer) {
        if (currentPlayer == 1) { // check row 1
            for (int j = 0; j < COLUMNS; j++) {
                if (board[PLAYER2_FRONT_ROW][j] != null && board[PLAYER2_FRONT_ROW][j].isTank()) {
                    return true;
                }
            }
        } else { // check row 2
            for (int j = 0; j < COLUMNS; j++) {
                if (board[PLAYER1_FRONT_ROW][j] != null && board[PLAYER1_FRONT_ROW][j].isTank()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a card from the board
     * @param row the row of the card
     * @param column the column of the card
     * @return the card
     */
    public Card getCard(final int row, final int column) {
        return board[row][column];
    }

    /**
     * Returns a row from the board
     * @param row the row
     * @return the row
     */
    public Card[] getRow(final int row) {
        return board[row];
    }
}

package main;

public class Board {
    private final Card[][] board;

    public Board() {
        this.board = new Card[4][5];
    }

    public boolean addCard(final Card card, final int player) {
        return false;
    }

    public void checkDeadCards() {
    }

    public void checkFrozenCards() {
    }

    public void checkUsedCards(final int currentPlayer) {
    }

    public void checkForzenCards(int currentPlayer) {
    }
}

package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.ActionsInput;
import fileio.GameInput;
import fileio.StartGameInput;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.min;

public class Game {
    private final StartGameInput startGame;
    private final ArrayList<ActionsInput> actions;
    private final Player playerOne;
    private final Player playerTwo;
    private final Board board;
    private int currentPlayer;
    private boolean gameOver;

    public Game(final GameInput gameInput, final Player playerOne, final Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.board = new Board();
        this.startGame = gameInput.getStartGame();
        this.actions = gameInput.getActions();
        this.currentPlayer = startGame.getStartingPlayer();
        this.gameOver = false;
    }

    public void playGame(final ArrayNode output){
        int round = 1;
        int turnFromRound = 1;
        boolean isStartofRound = true;

        playerOne.makeCards(startGame, 1);
        playerTwo.makeCards(startGame, 2);

        for (ActionsInput action : actions) {
            if (gameOver) {
                playCommand(action, output);
                continue;
            }

            if (isStartofRound) { // Start of round
                addMana(round);
                pullCard();
                isStartofRound = false;
            }
            if (Objects.equals(action.getCommand(), "endPlayerTurn")) { // End of player round
                board.checkForzenCards(currentPlayer);
                board.checkUsedCards(currentPlayer);
                checkUsedHero();

                currentPlayer = currentPlayer == 1 ? 2 : 1;
                if (turnFromRound == 2) { // End of round
                    isStartofRound = true;
                    round++;
                    turnFromRound = 1;
                } else { // Start of new turn
                    turnFromRound++;
                }
            } else {
                playCommand(action, output);
            }
        }
        
        
    }

    private void playCommand(ActionsInput action, ArrayNode output) {
        switch (action.getCommand()) {
            case "placeCard":
                placeCard(action, output);
                break;
            case "cardUsesAttack":
                cardUsesAttack(action, output);
                break;
            case "cardUsesAbility":
                cardUsesAbility(action, output);
                break;
            case "useAttackHero":
                useAttackHero(action, output);
                break;
            case "useHeroAbility":
                useHeroAbility(action, output);
                break;
            case "getCardsInHand":
                getCardsinHand(action, output);
                break;
            case "getPlayerDeck":
                getPlayerDeck(action, output);
                break;
            case "getCardsOnTable":
                getCardsOnTable(output);
                break;
            case "getPlayerTurn":
                getPlayerTurn(output);
                break;
            case "getPlayerHero":
                getPlayerHero(action, output);
                break;
            case "getCardAtPosition":
                getCardAtPosition(action, output);
                break;
            case "getPlayerMana":
                getPlayerMana(action, output);
                break;
            case "getFrozenCardsOnTable":
                getFrozenCardsOnTable(output);
                break;
            case "getTotalGamesPlayed":
                getTotalGamesPlayed(output);
                break;
            case "getPlayerOneWins":
                getPlayerOneWins(output);
                break;
            case "getPlayerTwoWins":
                getPlayerTwoWins(output);
                break;
            default:
                break;
        }
    }

    private void placeCard(ActionsInput action, ArrayNode output) {
    }

    private void cardUsesAttack(ActionsInput action, ArrayNode output) {
    }

    private void cardUsesAbility(ActionsInput action, ArrayNode output) {
    }

    private void useAttackHero(ActionsInput action, ArrayNode output) {
    }

    private void useHeroAbility(ActionsInput action, ArrayNode output) {
    }

    private void getCardsinHand(ActionsInput action, ArrayNode output) {
    }

    private void getPlayerDeck(ActionsInput action, ArrayNode output) {
    }

    private void getCardsOnTable(ArrayNode output) {
    }

    private void getPlayerTurn(ArrayNode output) {
    }

    private void getPlayerHero(ActionsInput action, ArrayNode output) {
    }

    private void getCardAtPosition(ActionsInput action, ArrayNode output) {
    }

    private void getPlayerMana(ActionsInput action, ArrayNode output) {
    }

    private void getFrozenCardsOnTable(ArrayNode output) {
    }

    private void getTotalGamesPlayed(ArrayNode output) {
    }

    private void getPlayerOneWins(ArrayNode output) {
    }

    private void getPlayerTwoWins(ArrayNode output) {
    }

    private void checkUsedHero() {
        Player player = currentPlayer == 1 ? playerOne : playerTwo;
        player.getHero().setHasAttacked(false);
    }

    private void addMana(int round) {
        int mana = min(10, round);
        playerOne.addMana(mana);
        playerTwo.addMana(mana);
    }
    
    private void pullCard() {
        playerOne.pullCard();
        playerTwo.pullCard();
    }


}

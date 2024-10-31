package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.GameInput;
import fileio.StartGameInput;
import fileio.Coordinates;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.min;

public final class Game {
    public static final int MAX_MANA = 10;

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

    /**
     * Plays the game according to the actions given in the input.
     * @param output the output of the game
     */
    public void playGame(final ArrayNode output) {
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

    private void addMana(final int round) {
        int mana = min(MAX_MANA, round);
        playerOne.addMana(mana);
        playerTwo.addMana(mana);
    }

    private void pullCard() {
        playerOne.pullCard();
        playerTwo.pullCard();
    }

    private void playCommand(final ActionsInput action, final ArrayNode output) {
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

    private void placeCard(final ActionsInput action, final ArrayNode output) {
        if (gameOver) {
            return;
        }

        int handIdx = action.getHandIdx();
        Player player = currentPlayer == 1 ? playerOne : playerTwo;
        Card card = player.getCard(handIdx);
        ObjectNode errorNode = output.objectNode();

        if (!enoughMana(player, card)) {
            errorNode.put("command", "placeCard");
            errorNode.put("handIdx", handIdx);
            errorNode.put("error", "Not enough mana to place card on table.");
            output.add(errorNode);
            return;
        } else if (!board.addCard(card, currentPlayer)) {
            errorNode.put("command", "placeCard");
            errorNode.put("handIdx", handIdx);
            errorNode.put("error", "Cannot place card on table since row is full.");
            output.add(errorNode);
            return;
        }

        player.decreaseMana(card.getMana());
        player.removeCardFromHand(handIdx);
    }

    private void cardUsesAttack(final ActionsInput action, final ArrayNode output) {
        if (gameOver) {
            return;
        }

        Coordinates attacker = action.getCardAttacker();
        Coordinates attacked = action.getCardAttacked();
        Card attackerCard = board.getCard(attacker.getX(), attacker.getY());
        Card attackedCard = board.getCard(attacked.getX(), attacked.getY());
        ObjectNode errorNode = output.objectNode();

        if (!isEnemyCard(attacker, attacked)) {
            errorNode.put("command", "cardUsesAttack");
            errorNode.putObject("cardAttacker").put("x", attacker.getX()).put("y", attacker.getY());
            errorNode.putObject("cardAttacked").put("x", attacked.getX()).put("y", attacked.getY());
            errorNode.put("error", "Attacked card does not belong to the enemy.");
            output.add(errorNode);
            return;
        } else if (attackerCard.getHasAttacked()) {
            errorNode.put("command", "cardUsesAttack");
            errorNode.putObject("cardAttacker").put("x", attacker.getX()).put("y", attacker.getY());
            errorNode.putObject("cardAttacked").put("x", attacked.getX()).put("y", attacked.getY());
            errorNode.put("error", "Attacker card has already attacked this turn.");
            output.add(errorNode);
            return;
        } else if (attackerCard.isFrozen()) {
            errorNode.put("command", "cardUsesAttack");
            errorNode.putObject("cardAttacker").put("x", attacker.getX()).put("y", attacker.getY());
            errorNode.putObject("cardAttacked").put("x", attacked.getX()).put("y", attacked.getY());
            errorNode.put("error", "Attacker card is frozen.");
            output.add(errorNode);
            return;
        } else if (board.checkTank(currentPlayer) && !attackedCard.isTank()) {
            errorNode.put("command", "cardUsesAttack");
            errorNode.putObject("cardAttacker").put("x", attacker.getX()).put("y", attacker.getY());
            errorNode.putObject("cardAttacked").put("x", attacked.getX()).put("y", attacked.getY());
            errorNode.put("error", "Attacked card is not of type 'Tank'.");
            output.add(errorNode);
            return;
        }

        attackerCard.setHasAttacked(true);
        attackedCard.decreaseHealth(attackerCard.getAttackDamage());
        board.checkDeadCards();
    }

    private void cardUsesAbility(final ActionsInput action, final ArrayNode output) {
        if (gameOver) {
            return;
        }

        Coordinates attacker = action.getCardAttacker();
        Coordinates attacked = action.getCardAttacked();
        Card attackerCard = board.getCard(attacker.getX(), attacker.getY());
        Card attackedCard = board.getCard(attacked.getX(), attacked.getY());
        ObjectNode errorNode = output.objectNode();

        if (attackerCard.isFrozen()) {
            errorNode.put("command", "cardUsesAbility");
            errorNode.putObject("cardAttacker").put("x", attacker.getX()).put("y", attacker.getY());
            errorNode.putObject("cardAttacked").put("x", attacked.getX()).put("y", attacked.getY());
            errorNode.put("error", "Attacker card is frozen.");
            output.add(errorNode);
            return;
        } else if (attackerCard.getHasAttacked()) {
            errorNode.put("command", "cardUsesAbility");
            errorNode.putObject("cardAttacker").put("x", attacker.getX()).put("y", attacker.getY());
            errorNode.putObject("cardAttacked").put("x", attacked.getX()).put("y", attacked.getY());
            errorNode.put("error", "Attacker card has already attacked this turn.");
            output.add(errorNode);
            return;
        } else if (attackerCard.getName().equals("Disciple") && isEnemyCard(attacker, attacked)) {
            errorNode.put("command", "cardUsesAbility");
            errorNode.putObject("cardAttacker").put("x", attacker.getX()).put("y", attacker.getY());
            errorNode.putObject("cardAttacked").put("x", attacked.getX()).put("y", attacked.getY());
            errorNode.put("error", "Attacked card does not belong to the current player.");
            output.add(errorNode);
            return;
        } else if (attackerCard.getName().equals("The Cursed One")
                || attackerCard.getName().equals("Miraj")
                || attackerCard.getName().equals("The Ripper")) {
            if (!isEnemyCard(attacker, attacked)) {
                errorNode.put("command", "cardUsesAbility");
                errorNode.putObject("cardAttacker").put("x", attacker.getX())
                        .put("y", attacker.getY());
                errorNode.putObject("cardAttacked").put("x", attacked.getX())
                        .put("y", attacked.getY());
                errorNode.put("error", "Attacked card does not belong to the enemy.");
                output.add(errorNode);
                return;
            } else if (board.checkTank(currentPlayer) && !attackedCard.isTank()) {
                errorNode.put("command", "cardUsesAbility");
                errorNode.putObject("cardAttacker").put("x", attacker.getX())
                        .put("y", attacker.getY());
                errorNode.putObject("cardAttacked").put("x", attacked.getX())
                        .put("y", attacked.getY());
                errorNode.put("error", "Attacked card is not of type 'Tank'.");
                output.add(errorNode);
                return;
            }
        }

        attackerCard.setHasAttacked(true);
        switch (attackerCard.getName()) {
            case "The Cursed One" -> { // swap health and attack damage of attacked card
                int health = attackedCard.getHealth();
                int attackDamage = attackedCard.getAttackDamage();
                attackedCard.setHealth(attackDamage);
                attackedCard.setAttackDamage(health);
                board.checkDeadCards();
            }
            case "Miraj" -> { // swap health of attacker and attacked card
                int health = attackedCard.getHealth();
                attackedCard.setHealth(attackerCard.getHealth());
                attackerCard.setHealth(health);
            }
            case "The Ripper" ->  // decrease attack damage of attacked card by 2
                    attackedCard.decreaseAttackDamage(2);
            case "Disciple" ->  // increase health of attacked card by 2
                    attackedCard.increaseHealth(2);
            default ->
                    System.out.println("Invalid card name: " + attackerCard.getName());

        }
    }

    private void useAttackHero(final ActionsInput action, final ArrayNode output) {
        if (gameOver) {
            return;
        }

        Coordinates attacker = action.getCardAttacker();
        Card attackerCard = board.getCard(attacker.getX(), attacker.getY());
        ObjectNode errorNode = output.objectNode();

        if (attackerCard == null) {
            errorNode.put("command", "useAttackHero");
            errorNode.putObject("cardAttacker").put("x", attacker.getX()).put("y", attacker.getY());
            errorNode.put("error", "No card available at that position.");
            output.add(errorNode);
            return;
        }

        if (attackerCard.isFrozen()) {
            errorNode.put("command", "useAttackHero");
            errorNode.putObject("cardAttacker").put("x", attacker.getX()).put("y", attacker.getY());
            errorNode.put("error", "Attacker card is frozen.");
            output.add(errorNode);
            return;
        } else if (attackerCard.getHasAttacked()) {
            errorNode.put("command", "useAttackHero");
            errorNode.putObject("cardAttacker").put("x", attacker.getX()).put("y", attacker.getY());
            errorNode.put("error", "Attacker card has already attacked this turn.");
            output.add(errorNode);
            return;
        } else if (board.checkTank(currentPlayer)) { // Not need to check if attacked card is tank
            errorNode.put("command", "useAttackHero");
            errorNode.putObject("cardAttacker").put("x", attacker.getX()).put("y", attacker.getY());
            errorNode.put("error", "Attacked card is not of type 'Tank'.");
            output.add(errorNode);
            return;
        }

        attackerCard.setHasAttacked(true);
        Player enemy = currentPlayer == 1 ? playerTwo : playerOne;
        enemy.getHero().decreaseHealth(attackerCard.getAttackDamage());
        if (enemy.getHero().getHealth() <= 0) {
            String winner = currentPlayer == 1 ? "one" : "two";
            ObjectNode gameEnded = output.objectNode();
            gameEnded.put("gameEnded", "Player " + winner + " killed the enemy hero.");
            output.add(gameEnded);
            gameOver = true;
            Player winnerPlayer = currentPlayer == 1 ? playerOne : playerTwo;
            winnerPlayer.addWin();
        }
    }

    private void useHeroAbility(final ActionsInput action, final ArrayNode output) {
        if (gameOver) {
            return;
        }

        int affectedRow = action.getAffectedRow();
        Player player = currentPlayer == 1 ? playerOne : playerTwo;
        Hero hero = player.getHero();
        ObjectNode errorNode = output.objectNode();

        if (player.getMana() < hero.getMana()) {
            errorNode.put("command", "useHeroAbility");
            errorNode.put("affectedRow", affectedRow);
            errorNode.put("error", "Not enough mana to use hero's ability.");
            output.add(errorNode);
            return;
        } else if (hero.getHasAttacked()) {
            errorNode.put("command", "useHeroAbility");
            errorNode.put("affectedRow", affectedRow);
            errorNode.put("error", "Hero has already attacked this turn.");
            output.add(errorNode);
            return;
        } else if (hero.getUsesAbilityOnEnemy() && !heroAttacksEnemy(affectedRow)) {
            errorNode.put("command", "useHeroAbility");
            errorNode.put("affectedRow", affectedRow);
            errorNode.put("error", "Selected row does not belong to the enemy.");
            output.add(errorNode);
            return;
        } else if (!hero.getUsesAbilityOnEnemy() && heroAttacksEnemy(affectedRow)) {
            errorNode.put("command", "useHeroAbility");
            errorNode.put("affectedRow", affectedRow);
            errorNode.put("error", "Selected row does not belong to the current player.");
            output.add(errorNode);
            return;
        }

        hero.ability(board.getRow(affectedRow));
        board.checkDeadCards();
        player.decreaseMana(hero.getMana());
        hero.setHasAttacked(true);
    }

    private void getCardsinHand(final ActionsInput action, final ArrayNode output) {
        int playerIdx = action.getPlayerIdx();
        Player player = playerIdx == 1 ? playerOne : playerTwo;

        ObjectNode commandOutput = output.objectNode();
        commandOutput.put("command", "getCardsInHand");
        commandOutput.put("playerIdx", playerIdx);

        ArrayNode cardsArray = commandOutput.putArray("output");
        for (Card card : player.getHand()) {
            ObjectNode cardNode = getCardNodeJson(card, cardsArray);
            cardsArray.add(cardNode);
        }
        output.add(commandOutput);
    }

    private void getPlayerDeck(final ActionsInput action, final ArrayNode output) {
        int playerIdx = action.getPlayerIdx();
        Player player = playerIdx == 1 ? playerOne : playerTwo;

        ObjectNode commandOutput = output.objectNode();
        commandOutput.put("command", "getPlayerDeck");
        commandOutput.put("playerIdx", playerIdx);

        ArrayNode cardsArray = commandOutput.putArray("output");
        for (Card card : player.getDeck()) {
            ObjectNode cardNode = getCardNodeJson(card, cardsArray);
            cardsArray.add(cardNode);
        }
        output.add(commandOutput);
    }

    private void getCardsOnTable(final ArrayNode output) {
        //All cards on the table (both players) [0][0] -> [3][4]
        ObjectNode commandOutput = output.objectNode();
        commandOutput.put("command", "getCardsOnTable");

        ArrayNode rowsArray = commandOutput.putArray("output");
        for (int i = 0; i < Board.ROWS; i++) {
            ArrayNode rowArray = rowsArray.addArray();
            for (int j = 0; j < Board.COLUMNS; j++) {
                Card card = board.getCard(i, j);
                if (card != null) {
                    ObjectNode cardNode = getCardNodeJson(card, rowArray);
                    rowArray.add(cardNode);
                }
            }
        }
        output.add(commandOutput);
    }

    private void getPlayerTurn(final ArrayNode output) {
        ObjectNode commandOutput = output.objectNode();
        commandOutput.put("command", "getPlayerTurn");
        commandOutput.put("output", currentPlayer);
        output.add(commandOutput);
    }

    private void getPlayerHero(final ActionsInput action, final ArrayNode output) {
        int playerIdx = action.getPlayerIdx();
        Player player = playerIdx == 1 ? playerOne : playerTwo;
        Hero hero = player.getHero();

        ObjectNode commandOutput = output.objectNode();
        commandOutput.put("command", "getPlayerHero");
        commandOutput.put("playerIdx", playerIdx);

        ObjectNode heroNode = commandOutput.putObject("output");
        heroNode.put("mana", hero.getMana());
        heroNode.put("description", hero.getDescription());

        ArrayNode colorsArray = heroNode.putArray("colors");
        for (String color : hero.getColors()) {
            colorsArray.add(color);
        }

        heroNode.put("name", hero.getName());
        heroNode.put("health", hero.getHealth());

        output.add(commandOutput);
    }

    private void getCardAtPosition(final ActionsInput action, final ArrayNode output) {
        int x = action.getX();
        int y = action.getY();
        Card card = board.getCard(x, y);

        ObjectNode commandOutput = output.objectNode();
        commandOutput.put("command", "getCardAtPosition");
        commandOutput.put("x", x);
        commandOutput.put("y", y);

        if (card == null) {
            commandOutput.put("output", "No card available at that position.");
        } else {
            ObjectNode cardNode = getCardNodeJson(card, output);
            commandOutput.put("output", cardNode);
        }
        output.add(commandOutput);
    }

    private void getPlayerMana(final ActionsInput action, final ArrayNode output) {
        int playerIdx = action.getPlayerIdx();
        Player player = playerIdx == 1 ? playerOne : playerTwo;
        ObjectNode commandOutput = output.objectNode();
        commandOutput.put("command", "getPlayerMana");
        commandOutput.put("playerIdx", playerIdx);
        commandOutput.put("output", player.getMana());
        output.add(commandOutput);
    }

    private void getFrozenCardsOnTable(final ArrayNode output) {
        ObjectNode commandOutput = output.objectNode();
        commandOutput.put("command", "getFrozenCardsOnTable");

        ArrayNode cardsArray = commandOutput.putArray("output");
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLUMNS; j++) {
                Card card = board.getCard(i, j);
                if (card != null && card.isFrozen()) {
                    ObjectNode cardNode = getCardNodeJson(card, cardsArray);
                    cardsArray.add(cardNode);
                }
            }
        }
        output.add(commandOutput);
    }

    private void getTotalGamesPlayed(final ArrayNode output) {
        ObjectNode commandOutput = output.objectNode();
        commandOutput.put("command", "getTotalGamesPlayed");
        commandOutput.put("output", playerOne.getWins() + playerTwo.getWins());
        output.add(commandOutput);
    }

    private void getPlayerOneWins(final ArrayNode output) {
        ObjectNode commandOutput = output.objectNode();
        commandOutput.put("command", "getPlayerOneWins");
        commandOutput.put("output", playerOne.getWins());
        output.add(commandOutput);
    }

    private void getPlayerTwoWins(final ArrayNode output) {
        ObjectNode commandOutput = output.objectNode();
        commandOutput.put("command", "getPlayerTwoWins");
        commandOutput.put("output", playerTwo.getWins());
        output.add(commandOutput);
    }

    private boolean enoughMana(final Player player, final Card card) {
        return player.getMana() >= card.getMana();
    }

    private boolean isEnemyCard(final Coordinates attacker, final Coordinates attacked) {
        return !((attacker.getX() <= 1 && attacked.getX() <= 1)
                || (attacker.getX() >= 2 && attacked.getX() >= 2));
    }

    private boolean heroAttacksEnemy(final int affectedRow) {
        return currentPlayer == 1 ? affectedRow <= 1 : affectedRow >= 2;
    }

    private void checkUsedHero() {
        Player player = currentPlayer == 1 ? playerOne : playerTwo;
        player.getHero().setHasAttacked(false);
    }

    private ObjectNode getCardNodeJson(final Card card, final ArrayNode cardsArray) {
        ObjectNode cardNode = cardsArray.objectNode();
        cardNode.put("mana", card.getMana());
        cardNode.put("attackDamage", card.getAttackDamage());
        cardNode.put("health", card.getHealth());
        cardNode.put("description", card.getDescription());

        ArrayNode colorsArray = cardNode.putArray("colors");
        for (String color : card.getColors()) {
            colorsArray.add(color);
        }
        cardNode.put("name", card.getName());
        return cardNode;
    }
}

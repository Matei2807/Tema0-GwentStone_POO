package main;

import fileio.CardInput;
import fileio.DecksInput;
import fileio.StartGameInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class Player {
    private final ArrayList<ArrayList<Card>> decks;
    private ArrayList<Card> deck;
    private ArrayList<Card> hand;
    private Hero hero;
    private int mana;
    private int wins;

    public Player(final DecksInput decks) {
        this.decks = new ArrayList<ArrayList<Card>>(decks.getNrDecks());
        for (ArrayList<CardInput> deckIterator : decks.getDecks()) {
            ArrayList<Card> newDeck = new ArrayList<Card>(deckIterator.size());
            for (CardInput card : deckIterator) {
                newDeck.add(new Card(card));
            }
            this.decks.add(newDeck);
        }
        this.wins = 0;
        //the rest of the fields are initialized when the game starts
    }

    /**
     * Makes the cards for the player and initializes relevant fields
     * @param startGame the input for the game
     * @param player the player for which the cards are made
     */
    public void makeCards(final StartGameInput startGame, final int player) {
        int deckIdx;
        String heroName;
        CardInput heroCard;

        if (player == 1) {
            deckIdx = startGame.getPlayerOneDeckIdx();
            heroName = startGame.getPlayerOneHero().getName();
            heroCard = startGame.getPlayerOneHero();
        } else {
            deckIdx = startGame.getPlayerTwoDeckIdx();
            heroName = startGame.getPlayerTwoHero().getName();
            heroCard = startGame.getPlayerTwoHero();
        }

        this.deck = new ArrayList<Card>(decks.get(deckIdx));
        Collections.shuffle(deck, new Random(startGame.getShuffleSeed()));
        this.hand = new ArrayList<Card>();
        this.mana = 0;

        switch (heroName) {
            case "Lord Royce":
                this.hero = new LordRoyce(heroCard);
                break;
            case "Empress Thorina":
                this.hero = new EmpressThorina(heroCard);
                break;
            case "King Mudface":
                this.hero = new KingMudface(heroCard);
                break;
            case "General Kocioraw":
                this.hero = new GeneralKocioraw(heroCard);
                break;
            default:
                System.out.println("Invalid hero name: " + heroName);
        }
    }

    /**
     * Draws a card from the deck.
     */
    public void drawCard() {
        if (!deck.isEmpty()) {
            hand.add(deck.remove(0));
        }
    }

    /**
     * Adds mana to the player.
     * @param addedMana the amount of mana to be added
     */
    public void addMana(final int addedMana) {
        mana += addedMana;
    }

    /**
     * Returns the card at the given index.
     * @param idx the index of the card
     */
    public Card getCard(final int idx) {
        return hand.get(idx);
    }

    /**
     * Decreases the mana of the player.
     * @param decreasedMana the amount of mana to be decreased
     */
    public void decreaseMana(final int decreasedMana) {
        mana -= decreasedMana;
    }

    /**
     * Removes a card from the hand.
     * @param idx the index of the card to be removed
     */
    public void removeCardFromHand(final int idx) {
        hand.remove(idx);
    }

    /**
     * Adds a win to the player
     */
    public void addWin() {
        wins++;
    }

    public int getMana() {
        return mana;
    }

    public Hero getHero() {
        return hero;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public int getWins() {
        return wins;
    }
}

package main;

import fileio.CardInput;
import fileio.DecksInput;
import fileio.StartGameInput;

import java.util.ArrayList;

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

    public void makeCards(final StartGameInput startGame, final int player){

    }

    public void pullCard(){

    }

    public ArrayList<ArrayList<Card>> getDecks() {
        return decks;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void addMana(int mana) {
        this.mana += mana;
    }
}

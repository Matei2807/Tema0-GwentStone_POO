package main;

import fileio.CardInput;

import java.util.ArrayList;

public final class Card {
    private final int mana;
    private final String description;
    private final ArrayList<String> colors;
    private final String name;
    private final boolean isTank;
    private final boolean isLastRow;
    private int attackDamage;
    private int health;
    private boolean frozen;
    private boolean hasAttacked;

    public Card(final CardInput cardInput) {
        this.mana = cardInput.getMana();
        this.attackDamage = cardInput.getAttackDamage();
        this.health = cardInput.getHealth();
        this.description = cardInput.getDescription();
        this.colors = cardInput.getColors();
        this.name = cardInput.getName();
        this.frozen = false;
        this.hasAttacked = false;
        if (cardInput.getName().equals("Goliath")
                || cardInput.getName().equals("Warden")) {
            isTank = true;
        } else {
            isTank = false;
        }
        if (cardInput.getName().equals("The Ripper")
                || cardInput.getName().equals("Miraj")
                || isTank) {
            this.isLastRow = false;
        } else {
            this.isLastRow = true;
        }
    }

    public Card(final Card card) {
        this.mana = card.getMana();
        this.attackDamage = card.getAttackDamage();
        this.health = card.getHealth();
        this.description = card.getDescription();
        this.colors = card.getColors();
        this.name = card.getName();
        this.frozen = card.isFrozen();
        this.hasAttacked = card.getHasAttacked();
        this.isTank = card.isTank();
        this.isLastRow = card.isLastRow();
    }

    /**
     * Decreases the health of the card.
     * @param damage the amount by which the health is decreased
     */
    public void decreaseHealth(final int damage) {
        health -= damage;
    }

    /**
     * Increases the health of the card.
     * @param healthGain the amount by which the health is increased
     */
    public void increaseHealth(final int healthGain) {
        health += healthGain;
    }

    /**
     * Decreases the attack of the card.
     * @param attackDamageDecrease the amount by which the attack damage is decreased
     */
    public void decreaseAttackDamage(final int attackDamageDecrease) {
        attackDamage -= attackDamageDecrease;
        if (attackDamage < 0) {
            attackDamage = 0;
        }
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(final boolean isFrozen) {
        this.frozen = isFrozen;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getMana() {
        return mana;
    }

    public boolean getHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public boolean isTank() {
        return isTank;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public boolean isLastRow() {
        return isLastRow;
    }
}

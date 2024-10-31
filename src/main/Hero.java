package main;

import fileio.CardInput;

import java.util.ArrayList;

public class Hero {
    public static final int MAX_HEALTH = 30;

    private final int mana;
    private final String description;
    private final ArrayList<String> colors;
    private final String name;
    private int health;
    private boolean hasAttacked;
    protected boolean usesAbilityOnEnemy;

    public Hero(final CardInput cardInput) {
        this.mana = cardInput.getMana();
        this.health = MAX_HEALTH;
        this.description = cardInput.getDescription();
        this.colors = cardInput.getColors();
        this.name = cardInput.getName();
        this.hasAttacked = false;
    }

    /**
     * The ability of the hero.
     * @param affectedCardRow the row of cards affected by the ability
     */
    public void ability(final Card[] affectedCardRow) {
    }

    /**
     * Decreases the health of the hero.
     * @param damage the amount by which the health is decreased
     */
    public final void decreaseHealth(final int damage) {
        this.health -= damage;
    }

    public final int getHealth() {
        return health;
    }

    public final int getMana() {
        return mana;
    }

    public final boolean getHasAttacked() {
        return hasAttacked;
    }

    public final boolean getUsesAbilityOnEnemy() {
        return usesAbilityOnEnemy;
    }

    public final void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final ArrayList<String> getColors() {
        return colors;
    }
}

class LordRoyce extends Hero {
    LordRoyce(final CardInput cardInput) {
        super(cardInput);
        this.usesAbilityOnEnemy = true;
    }

    @Override
    public void ability(final Card[] affectedCardRow) { // Freezes the given row of cards
        for (int i = 0; i < Board.COLUMNS; i++) {
            if (affectedCardRow[i] != null) {
                affectedCardRow[i].setFrozen(true);
            }
        }
    }
}

class EmpressThorina extends Hero {
    EmpressThorina(final CardInput cardInput) {
        super(cardInput);
        this.usesAbilityOnEnemy = true;
    }

    @Override
    public void ability(final Card[] affectedCardRow) {
        Card affectedCard = affectedCardRow[0];
        int maxHealth = 0;
        for (int i = 0; i < Board.COLUMNS; i++) { // Kills the first card with the highest health
            if (affectedCardRow[i] != null && affectedCardRow[i].getHealth() > maxHealth) {
                maxHealth = affectedCardRow[i].getHealth();
                affectedCard = affectedCardRow[i];
            }
        }
        affectedCard.setHealth(0);
    }
}

class KingMudface extends Hero {
    KingMudface(final CardInput cardInput) {
        super(cardInput);
        this.usesAbilityOnEnemy = false;
    }

    @Override
    public void ability(final Card[] affectedCardRow) { // Increases health of cards in the row by 1
        for (int i = 0; i < Board.COLUMNS; i++) {
            if (affectedCardRow[i] != null) {
                affectedCardRow[i].setHealth(affectedCardRow[i].getHealth() + 1);
            }
        }
    }
}

class GeneralKocioraw extends Hero {
    GeneralKocioraw(final CardInput cardInput) {
        super(cardInput);
        this.usesAbilityOnEnemy = false;
    }

    @Override
    public void ability(final Card[] affectedCardRow) { // Increases attack of cards in the row by 1
        for (int i = 0; i < Board.COLUMNS; i++) {
            if (affectedCardRow[i] != null) {
                affectedCardRow[i].setAttackDamage(affectedCardRow[i].getAttackDamage() + 1);
            }
        }
    }
}

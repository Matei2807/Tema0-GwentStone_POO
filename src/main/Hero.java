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

    public void ability(final Card[] affectedCardRow) {
    }

    public void setHasAttacked(boolean b) {
    }
}

class LordRoyce extends Hero {
    LordRoyce(final CardInput cardInput) {
        super(cardInput);
    }

    @Override
    public void ability(final Card[] affectedCardRow) {
    }
}

class EmpressThorina extends Hero {
    EmpressThorina(final CardInput cardInput) {
        super(cardInput);
    }

    @Override
    public void ability(final Card[] affectedCardRow) {
    }
}

class KingMudface extends Hero {
    KingMudface(final CardInput cardInput) {
        super(cardInput);
    }

    @Override
    public void ability(final Card[] affectedCardRow) {
    }
}

class GeneralKocioraw extends Hero {
    GeneralKocioraw(final CardInput cardInput) {
        super(cardInput);
    }

    @Override
    public void ability(final Card[] affectedCardRow) {
    }
}

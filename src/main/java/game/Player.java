package game;

import game.Croupier;
import model.Action;
import model.Stick;

import java.util.Random;
import java.util.UUID;

public class Player {

    private UUID id;
    private int points;
    private Action action;
    private Stick guess;
    private boolean result;

    public Player(UUID id) {
        this.id = id;
        points = 0;
    }

    public void guess() {
        guess = (new Random().nextBoolean()) ? Stick.SHORT : Stick.LONG;
    }

    public void givePoint() {
        points++;
    }

    public int getPoints() {
        return points;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public Stick getGuess() { return guess; }

    public UUID getId() { return id; }
}

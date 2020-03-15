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

    public Player(UUID id) {
        this.id = id;
        points = 0;
    }

    public Stick draw() {

        int i = new Random().nextInt(6);

        return Croupier.getSticks()[i];
    }

    public void guess() {
        guess = (new Random().nextBoolean()) ? Stick.SHORT : Stick.LONG;
    }

    public int getPoints() {
        return points;
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

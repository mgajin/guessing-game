package server;

import model.Action;
import model.Stick;

import java.util.Random;
import java.util.UUID;

public class Player {

    private UUID id;
    private int points;
    private Action action;

    public Player(UUID id) {
        this.id = id;
        points = 0;
    }

    public Stick draw() {

        int i = new Random().nextInt(6);

        return Croupier.getSticks()[i];
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

    public UUID getId() { return id; }
}

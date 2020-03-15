package server;

import java.util.UUID;

public class Player {

    private UUID id;
    private int points;
    private String action;

    public Player(UUID id) {
        this.id = id;
        points = 0;
    }

    public int getPoints() {
        return points;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public UUID getId() { return id; }
}

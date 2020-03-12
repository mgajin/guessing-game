package server;

import java.util.UUID;

public class Player {

    private UUID id;
    private int points;

    public Player(UUID id) {
        this.id = id;
        points = 0;
    }

    public int getPoints() {
        return points;
    }
}

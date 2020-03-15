package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Table {

    private Semaphore semaphore;
    private static List<Player> players;

    public Table(int n) {
        semaphore = new Semaphore(n);
        players = new ArrayList<Player>();
    }

    public boolean acquireSeat(Player player) {

        if (semaphore.tryAcquire()) {
            players.add(player);

            return true;
        }

        return false;
    }

    public void releaseSeat(Player player) {
        players.remove(player);
        semaphore.release();
    }

    public static List<Player> getPlayers() {
        return players;
    }
}

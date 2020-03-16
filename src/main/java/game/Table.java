package game;

import game.Croupier;
import game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Table {

    public static final int TOTAL_PLAYERS = 6;
    private static List<Player> players;

    private Semaphore semaphore;
    private CyclicBarrier barrier;


    public Table(int n) {
        players = new ArrayList<Player>();
        semaphore = new Semaphore(n);
        barrier = new CyclicBarrier(n, Croupier.startGame());
    }

    public void await() {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean acquireSeat(Player player) {

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

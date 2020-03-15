package game;

import game.Croupier;
import game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Table {

    private Semaphore semaphore;
    private CyclicBarrier barrier;

    private Croupier croupier;
    private static List<Player> players;
    private static AtomicBoolean availableSeats;

    public Table(int n) {
        croupier = new Croupier();
        semaphore = new Semaphore(n);
        barrier = new CyclicBarrier(n, croupier);
        players = new ArrayList<Player>();
        availableSeats = new AtomicBoolean(true);
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
            if (players.toArray().length == 6) {
                availableSeats.set(false);
            }

            return true;
        }

        return false;
    }

    public void releaseSeat(Player player) {
        players.remove(player);
        availableSeats.set(true);
        semaphore.release();
    }

    public static boolean isFull() {
        return availableSeats.get();
    }

    public static List<Player> getPlayers() {
        return players;
    }
}

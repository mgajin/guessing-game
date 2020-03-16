package game;

import game.Croupier;
import game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Table {

    public static final int TOTAL_PLAYERS = 6;
    private static List<Player> players;
    private static int round;

    private Semaphore semaphore;
    private CyclicBarrier barrier;


    public Table() {
        players = new ArrayList<Player>();
        semaphore = new Semaphore(TOTAL_PLAYERS);
        barrier = new CyclicBarrier(TOTAL_PLAYERS, Croupier.startGame());
        round = 0;
    }

    public void await() {
        try {
            barrier.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            semaphore.release();
        } catch (TimeoutException e) {
            Croupier.setRunning(false);
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

    public static int getRound() {
        return round;
    }

    public static void next() {
        round++;
    }

    public static void reset() {
        round = 0;
    }

    public static List<Player> getPlayers() {
        return players;
    }
}

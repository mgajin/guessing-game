package game;

import model.Action;
import model.Stick;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class Croupier {

    private CyclicBarrier barrier;

    private static Stick[] sticks;
    private Stick stick;

    private static AtomicBoolean running;

    public Croupier() {
        sticks = new Stick[Table.TOTAL_PLAYERS];
        barrier = new CyclicBarrier(Table.TOTAL_PLAYERS, checkResults());

        running = new AtomicBoolean(false);
    }

    public static Runnable startGame() {
        return new Runnable() {
            @Override
            public void run() {
                running.set(true);
                System.out.println("Game has begun!");
                shuffleSticks();
                giveInstructions();
            }
        };
    }

    private Runnable checkResults() {
        return new Runnable() {
            @Override
            public void run() {
                List<Player> players = Table.getPlayers();

                boolean result = stick != Stick.SHORT;

                players.get(0).setResult(result);

                for (int i = 1; i < Table.TOTAL_PLAYERS; i++) {
                    Player player = players.get(i);
                    if (player.getGuess() == stick) {
                        player.setResult(true);
                        player.givePoint();
                        System.out.println("Correct");
                    } else {
                        player.setResult(false);
                        System.out.println("Wrong");
                    }
                }

                running.set(result);
            }
        };
    }

    public static void shuffleSticks() {
        int stick = new Random().nextInt(Table.TOTAL_PLAYERS);

        for (int i = 0; i < Table.TOTAL_PLAYERS; i++) {
            sticks[i] = (i == stick) ? Stick.SHORT : Stick.LONG;
        }
    }

    public static void giveInstructions() {

        List<Player> players = Table.getPlayers();

        players.get(0).setAction(Action.DRAW);

        for (int i = 1; i < players.toArray().length; i++) {
            players.get(i).setAction(Action.GUESS);
        }
    }

    public void await() {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            System.err.println("Croupier barrier error");
        }
    }

    public void setStick(Stick stick) {
        this.stick = stick;
    }

    public static Stick[] getSticks() {
        return sticks;
    }

    public static boolean isRunning() {
        return running.get();
    }
}
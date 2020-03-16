package game;

import model.Action;
import model.Stick;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Croupier {

    private CyclicBarrier barrier;

    private static Stick[] sticks;
    private Stick stick;

    private static AtomicBoolean running;

    public Croupier() {
        sticks = new Stick[Table.TOTAL_PLAYERS];
        barrier = new CyclicBarrier(Table.TOTAL_PLAYERS, checkResults());

        running = new AtomicBoolean(true);
    }

    public static Runnable startGame() {
        return new Runnable() {
            @Override
            public void run() {
//                running.set(true);
                if (Table.getRound() == 0) {
                    System.out.println("Game has begun!");
                    shuffleSticks();
                } else {
                    System.out.println("Next round! " + Table.getRound());
                }

                giveInstructions();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Runnable checkResults() {
        return new Runnable() {
            @Override
            public void run() {

                List<Player> players = Table.getPlayers();

                boolean nextRound = stick != Stick.SHORT;

                for (Player player : players) {
                    if (player.getAction() == Action.GUESS) {
                        if (player.getGuess() == stick) {
                            player.setResult(true);
                            player.givePoint();
                        } else {
                            player.setResult(false);
                        }
                    } else {
                        player.setResult(nextRound);
                    }
                }

                if (nextRound) {
                    Table.next();
                } else {
                    printWinner();
                    Table.reset();
                }

                if (Table.getRound() == 6) {
                    running.set(false);
                }
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

        int i = 0;
        for (Player player : players) {
            Action action = (i == Table.getRound()) ? Action.DRAW : Action.GUESS;
            player.setAction(action);
            i++;
        }
    }

    public Stick giveStick() {

        int[] arr = new int[Table.TOTAL_PLAYERS - Table.getRound()];

        for (int i = 0, j = 0; i < Table.TOTAL_PLAYERS; i++) {
            if (sticks[i] != null) {
                arr[j++] = i;
            }
        }
        int i = new Random().nextInt(arr.length);

        Stick stick = sticks[arr[i]];
        sticks[arr[i]] = null;

        setStick(stick);

        if (stick == null) System.out.println("stick error");

        return stick;
    }

    public void printWinner() {

        List<Player> players = Table.getPlayers();
        Player winner = players.get(0);

        for (Player player : players) {
            winner = (player.getPoints() > winner.getPoints()) ? player : winner;
        }

        System.out.println("Winner is player [" + winner.getId() + "] with " + winner.getPoints() + " points");
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

    public static void setRunning(boolean isRunning) {
        running.set(isRunning);
    }
}
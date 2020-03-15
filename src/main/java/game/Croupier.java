package game;

import model.Action;
import model.Stick;

import java.util.List;
import java.util.Random;

public class Croupier implements Runnable{

    private static Stick[] sticks;

    public Croupier() {
        sticks = new Stick[6];
    }

    @Override
    public void run() {
        System.out.println("Game has begun!");
        shuffleSticks();
        giveInstructions();
    }

    public void giveInstructions() {

        List<Player> players = Table.getPlayers();

        System.out.println(players.toArray().length);

        players.get(0).setAction(Action.DRAW);

        for (int i = 1; i < players.toArray().length; i++) {
            players.get(i).setAction(Action.GUESS);
        }
    }

    public void shuffleSticks() {
        int stick = new Random().nextInt(6);

        for (int i = 0; i < sticks.length; i++) {
            sticks[i] = (i == stick) ? Stick.SHORT : Stick.LONG;
        }
    }

    public static Stick[] getSticks() {
        return sticks;
    }
}

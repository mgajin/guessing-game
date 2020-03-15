package game;

import model.Action;
import model.Stick;

import java.util.List;
import java.util.Random;

public class Croupier {

    private static Stick[] sticks;
    private static Stick stick;

    public Croupier() {
        sticks = new Stick[6];
    }

    public static void startGame() {
        System.out.println("Game has begun!");
        shuffleSticks();
        giveInstructions();
    }

    public static void giveInstructions() {

        List<Player> players = Table.getPlayers();

        players.get(0).setAction(Action.DRAW);

        for (int i = 1; i < players.toArray().length; i++) {
            players.get(i).setAction(Action.GUESS);
        }
    }

    public static void shuffleSticks() {
        int stick = new Random().nextInt(6);

        for (int i = 0; i < sticks.length; i++) {
            sticks[i] = (i == stick) ? Stick.SHORT : Stick.LONG;
        }
    }

    public static void setStick(Stick stick) {
        Croupier.stick = stick;
    }

    public static Stick[] getSticks() {
        return sticks;
    }
}
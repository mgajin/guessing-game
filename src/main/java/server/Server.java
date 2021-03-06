package server;

import com.google.gson.Gson;
import game.Croupier;
import game.Player;
import game.Table;
import model.*;

import java.io.*;
import java.net.Socket;

public class Server implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Response response;
    private Table table;
    private Player player;
    Croupier croupier;

    private Gson gson;

    public Server(Socket socket, Table table, Croupier croupier) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        response = new Response();
        this.table = table;
        this.croupier = croupier;

        gson = new Gson();
    }

    @Override
    public void run() {
        try {
//            First request is JOIN
            Request request = getRequest();
            player = new Player(request.getId());
            response.setResult(Result.SUCCESS);
            response.setMessage("Welcome");
            sendResponse(response);
//            Wait for player's request for seat
            request = getRequest();
            if (request.getAction() == Action.REQUEST_SEAT) {
                enterQue();
            }

//            System.out.println("Player: " + player.getId() + " joined");

            player.setInGame(true);
//            Constantly get requests from client
            while (player.isInGame()) {
//                Wait for instructions from Croupier
                table.await();
//                Check if there is enough players
                if (!Croupier.isRunning()) {
                    response.setResult(Result.FAILURE);
                    response.setMessage("Not enough players");
                    sendResponse(response);
                    break;
                }
//                Send instructions to client
                response.setMessage(player.getAction().toString());
                sendResponse(response);

                request = getRequest();
//                Game has begun
                if (request.getAction() == Action.DRAW) {
                    Stick stick = croupier.giveStick();
                    response.setMessage("You got " + stick);
                    sendResponse(response);
                    System.out.println("Player " + player.getId() + " draw " + stick.toString());
//                    Wait for other players to complete their action
                    croupier.await();
                    getResults();
                } else if (request.getAction() == Action.GUESS) {
                    player.guess();
                    response.setMessage("Your guess: " + player.getGuess());
                    sendResponse(response);
                    System.out.println("Player " + player.getId() + " guessed: " + player.getGuess());
//                    Wait for other players to complete their action
                    croupier.await();
                    getResults();
                }
//                if (!Croupier.isRunning()) break;
            }

//            Wait for client to confirm that he is leaving
            request = getRequest();
            if (request.getAction() == Action.LEAVE) {
                leaveTable();
                System.out.println("Player [" + player.getId() + "] left");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

//    Get results from croupier and sand them to client
    public void getResults() {

        if (player.getAction() == Action.GUESS) {
            if (player.getResult()) {
                response.setResult(Result.SUCCESS);
                response.setMessage("Correct!");
            } else {
                response.setResult(Result.FAILURE);
                response.setMessage("Wrong!");
            }
        } else {
            if (player.getResult()) {
                response.setResult(Result.SUCCESS);
                response.setMessage("You are safe!");
            } else {
                player.setInGame(false);
                response.setResult(Result.FAILURE);
                response.setMessage("You lost!");
            }
        }
        sendResponse(response);
    }

//    Wait for available seat at the table
    public void enterQue() {
        while (true) {
            if (table.acquireSeat(player)) {
                break;
            }
        }
        response.setResult(Result.SUCCESS);
        response.setMessage("Take your seat");
        sendResponse(response);
    }

//    Leave table
    public void leaveTable() {
        response.setResult(Result.SUCCESS);
        response.setMessage("You left the table");
        sendResponse(response);
        table.releaseSeat(player);
    }

//    Get request from client
    public Request getRequest() throws IOException {
        return gson.fromJson(in.readLine(), Request.class);
    }

//    Send response to client
    public void sendResponse(Response response) {
        String responseString = gson.toJson(response);
        out.println(responseString);
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
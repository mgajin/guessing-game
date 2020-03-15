package server;

import com.google.gson.Gson;
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

    private Gson gson;

    public Server(Socket socket, Table table) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        response = new Response();
        this.table = table;

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

//            Constantly get requests from client
            while (true) {
                request = getRequest();
                if (request.getAction() == Action.REQUEST_SEAT) {
                    enterQue();
//                    Wait for game to start
                    response.setMessage("Waiting for other players to take their seats...");
                    sendResponse(response);
//                    Wait for instructions from Croupier
                    table.await();
//                    Send instructions to client
                    response.setMessage(player.getAction().toString());
                    sendResponse(response);
                } else if (request.getAction() == Action.DRAW) {
                    Stick stick = player.draw();
                    System.out.println("Player " + player.getId() + " draw " + stick.toString());
                } else if (request.getAction() == Action.GUESS) {
                    System.out.println("Player " + player.getId() + " " + player.getAction());
                } else if (request.getAction() == Action.LEAVE) {
                    leaveTable();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

//    Wait for available seat at the table
    public void enterQue() {
        while (true) {
            if (table.acquireSeat(player)) {
                response.setResult(Result.SUCCESS);
                response.setMessage("Take your seat");
                sendResponse(response);
                break;
            }
        }
    }

//    Leave table
    public void leaveTable() {
        table.releaseSeat(player);
        response.setResult(Result.SUCCESS);
        response.setMessage("You left the table");
        sendResponse(response);
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
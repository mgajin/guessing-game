package client;

import com.google.gson.Gson;
import game.Croupier;
import model.Action;
import model.Request;
import model.Response;
import model.Result;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Client implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Gson gson;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        gson = new Gson();
    }

    @Override
    public void run() {
        UUID id = UUID.randomUUID();
        Request request = new Request();
        request.setId(id);

//        First request must be JOIN
        request.setAction(Action.JOIN);
        sendRequest(request);

        try {
            Response response = getResponse();
            if (response.getResult() == Result.FAILURE) {
                System.err.println("Something went wrong");
            }
//            Enter que for the table
            request.setAction(Action.REQUEST_SEAT);
            sendRequest(request);
            response = getResponse();
            if (response.getResult() == Result.SUCCESS) {
                System.out.println("Server: " + response.getMessage());
            }

            while (true) {
//                Wait for instructions from Server
                response = getResponse();

                if (response.getMessage().equals("Not enough players")) {
//                    System.out.println(response.getMessage());
                    request.setAction(Action.LEAVE);
                    sendRequest(request);
                    break;
                }

                if (response.getMessage().equals("DRAW")) {
                    request.setAction(Action.DRAW);
                } else {
                    request.setAction(Action.GUESS);
                }
                sendRequest(request);
//            Wait for server response
                response = getResponse();
                System.out.println("Server: " + response.getMessage() + " [" + id + "]");
//            Wait for results from croupier
                response = getResponse();
                System.out.println("Croupier: " + response.getMessage() + " [" + id + "]");

                if (request.getAction() == Action.DRAW && response.getResult() == Result.FAILURE) {
                    request.setAction(Action.LEAVE);
                    sendRequest(request);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            close();
        }
    }

    public void sendRequest(Request request) {
        String requestString = gson.toJson(request);
        out.println(requestString);
    }

    public Response getResponse() throws IOException {
        return gson.fromJson(in.readLine(), Response.class);
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

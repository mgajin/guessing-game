package client;

import com.google.gson.Gson;
import model.Action;
import model.Request;
import model.Response;
import model.Result;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;

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

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(Request request) {
        String requestString = gson.toJson(request);
        out.println(requestString);
    }

    @Override
    public void run() {
        UUID id = UUID.randomUUID();
        Request request = new Request();
        request.setId(id);

        while (true) {
            try {
                request.setAction(Action.REQUEST_CHAIR);
                sendRequest(request);

                Response response = gson.fromJson(in.readLine(), Response.class);

                if (response.getResult() == Result.SUCCESS) {
                    System.out.println("Player: " + id.toString() + " took a seat");
                    Thread.sleep(new Random().nextInt(1000));
                    request.setAction(Action.LEAVE);
                    sendRequest(request);
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        close();
    }
}

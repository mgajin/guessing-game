package client;

import com.google.gson.Gson;
import model.Action;
import model.Request;
import model.Response;
import model.Result;

import java.io.*;
import java.net.Socket;
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

    @Override
    public void run() {
        try {
            UUID id = UUID.randomUUID();
            Request request = new Request();
            request.setId(id);
            request.setAction(Action.REQUEST_CHAIR);
            String requestString = gson.toJson(request);
//            Send request to server
            out.println(requestString);
//            Get response from server
            Response response = gson.fromJson(in.readLine(), Response.class);

            if (response.getResult() == Result.SUCCESS) {
                System.out.println("Player: " + id.toString() + " connected");
            } else {
                System.out.println("Player: " + id.toString() + " can't access the table");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}

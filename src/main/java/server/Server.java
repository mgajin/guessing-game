package server;

import com.google.gson.Gson;
import model.Request;
import model.Response;
import model.Result;

import java.io.*;
import java.net.Socket;

public class Server implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Gson gson;

    public Server(Socket socket) throws IOException {
        this.socket = socket;
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

    public Request getRequest() throws IOException {
        return gson.fromJson(in.readLine(), Request.class);
    }

    public void sendResponse(Response response) {
        String responseString = gson.toJson(response);
        out.println(responseString);
    }

    @Override
    public void run() {
        try {
            Request request = getRequest();

            Player player = new Player(request.getId());

            Response response = new Response();
            response.setResult(Result.FAILURE);

            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}
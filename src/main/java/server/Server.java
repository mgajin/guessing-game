package server;

import com.google.gson.Gson;
import model.Action;
import model.Request;
import model.Response;
import model.Result;

import java.io.*;
import java.net.Socket;

public class Server implements Runnable {

    private Table table;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Gson gson;

    public Server(Socket socket, Table table) throws IOException {
        this.table = table;
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
        Response response = new Response();

        while (true) {
            try {
                Request request = getRequest();
                Player player = new Player(request.getId());

                if (request.getAction() == Action.REQUEST_CHAIR) {
                    if (table.acquireSeat()) {
                        response.setResult(Result.SUCCESS);
                        sendResponse(response);
                    }
                } else if (request.getAction() == Action.LEAVE) {
                    System.out.println("Leaving...");
                    table.releaseSeat();
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        close();
    }
}
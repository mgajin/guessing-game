package server;

import com.google.gson.Gson;
import model.Action;
import model.Request;
import model.Response;
import model.Result;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

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
            AtomicReference<Request> request = new AtomicReference<Request>(getRequest());
            player = new Player(request.get().getId());
            response.setResult(Result.SUCCESS);
            response.setMessage("Welcome");
            sendResponse(response);

//            Constantly get requests from client
            while (true) {
                request.set(getRequest());
                if (request.get().getAction() == Action.REQUEST_SEAT) {
                    enterQue();
                } else if (request.get().getAction() == Action.LEAVE) {
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
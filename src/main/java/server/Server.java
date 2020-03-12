package server;

import java.io.*;
import java.net.Socket;

public class Server implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean running;

    public Server(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        running = false;
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
        running = true;

        out.println("Welcome");

        close();
        System.out.println("Server closed...");
    }
}
package client;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean online;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        online = false;
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
        online = true;

        try {
            System.out.println("Server: " + in.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}

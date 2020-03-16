package server;

import game.Croupier;
import game.Table;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerRunner {

    public static final int PORT = 9000;

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService executor = Executors.newCachedThreadPool();
        Table table = new Table();
        Croupier croupier = new Croupier();

        while (true) {
            Socket socket = serverSocket.accept();
            executor.submit(new Server(socket, table, croupier));
        }

//        executor.shutdown();
    }
}

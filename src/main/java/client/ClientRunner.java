package client;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientRunner {

    public static final String HOST = "localhost";
    public static final int PORT = 9000;

    public static void main(String[] args) {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

        try {

            for (int i = 0; i < 10; i++) {
                executor.schedule(new Client(HOST, PORT), 500, TimeUnit.MILLISECONDS);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}

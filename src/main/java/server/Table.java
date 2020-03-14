package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Table {

    private Semaphore semaphore;
    private CyclicBarrier barrier;
    private List<Player> players;

    public Table(int n) {
        semaphore = new Semaphore(n);
        barrier = new CyclicBarrier(n);
        players = new ArrayList<Player>();
    }

    public boolean acquireSeat() {
        return semaphore.tryAcquire();
    }

    public void releaseSeat() {
        semaphore.release();
    }

    public void takeSeat() {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

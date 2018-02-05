import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

class LiftOff {
    String msg;
    void run() {
        System.out.println("LiftOff run info:" + msg);
    }
}

class LiftOffRunner implements Runnable {
    private BlockingDeque<LiftOff> rockets;

    public LiftOffRunner(BlockingDeque<LiftOff> queue) {
        this.rockets = queue;
    }

    public void add(LiftOff lo) {
        try {
            rockets.put(lo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                LiftOff lo = rockets.take();
                lo.run();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Exit LiftOffRunner...");
    }
}

public class TestBlockingQueues {
    static void getkey() {
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void getkey(String msg) {
        System.out.println(msg);
        getkey();
    }

    static void test(String msg, BlockingDeque<LiftOff> q) {
        LiftOffRunner runner = new LiftOffRunner(q);
        Thread t = new Thread(runner);
        t.start();
        for (int i = 0; i < 5; i++) {
            runner.add();
            
        }o
    }
}

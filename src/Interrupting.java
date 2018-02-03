import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class SleepBlocked implements Runnable {
    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException SleepBlocked");
        }

        System.out.println("Exiting SleepBlocked.run().\n\n");
    }
}

class IOBlocked implements Runnable {
    private InputStream in;

    public IOBlocked(InputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        System.out.println("Waiting for read():");
        try {
            in.read();
        } catch (IOException e) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Interrupted from blocked I/O");
            } else {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Exiting SleepBlocked.run().\n\n");
    }
}

class SynchronizedBlocked implements Runnable {
    public synchronized void f() {
        while (true) {
            Thread.yield();
        }
    }

    public SynchronizedBlocked() {
        new Thread() {
            @Override
            public void run() {
                f();
            }
        };
    }

    @Override
    public void run() {
        System.out.println("Trying to call f()");
        f();
        System.out.println("Exiting SynchronizedBlocked.run().\n\n");
    }
}

public class Interrupting {
    private static ExecutorService exec = Executors.newCachedThreadPool();
    static void test(Runnable r) throws InterruptedException {
        Future<?> f = exec.submit(r);
        TimeUnit.SECONDS.sleep(1);
        System.out.println("Interrupting " + r.getClass().getName());
        f.cancel(true);
        TimeUnit.SECONDS.sleep(1);
        System.out.println("Interrput sent to " + r.getClass().getName());
    }

    public static void main(String[] args) throws Exception {
        //test(new SleepBlocked());
        //test(new IOBlocked(System.in));
        test(new SynchronizedBlocked());
        System.out.println("Aborting with System.exit(0)");
        System.exit(0);
    }
}

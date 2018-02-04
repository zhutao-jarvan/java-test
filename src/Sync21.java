import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Sync21_1 implements Runnable {
    private boolean cond = false;

    public synchronized void f1() throws InterruptedException {
        System.out.println("11111111111111111111111111111");
        wait();
        System.out.println("4444444444444444444444444");
        if (cond) {
            System.out.println("f2() is notify!");
        }
    }

    public synchronized void f2() {
        cond = true;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                f1();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Sync21 {
    public static void main(String[] args) throws InterruptedException {
        Sync21_1 t = new Sync21_1();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(t);
        System.out.println("2222222222222222222222");
        synchronized (t) {
            t.f2();
            t.notifyAll();
        }
        System.out.println("3333333333");
        TimeUnit.SECONDS.sleep(3);
        exec.shutdownNow();
    }
}

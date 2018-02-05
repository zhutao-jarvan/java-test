import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BusyDelay {
    private boolean flag = false;

    class Task1 implements Runnable {
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    TimeUnit.MILLISECONDS.sleep(500);
                    System.out.println("Set flag to true");
                    TimeUnit.MILLISECONDS.sleep(500);
                    synchronized (BusyDelay.this) {
                        flag = true;
                        BusyDelay.this.notify();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Exit task1");
        }
    }

    class Task2 implements Runnable {
        @Override
        public void run() {
            synchronized (BusyDelay.this) {
                try {
                    while (!Thread.interrupted()) {
                        BusyDelay.this.wait();
                        if (flag) {
                            System.out.println("Set flag to false");
                            flag = false;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Exit task2");
        }
    }
    protected Task1 t1 = new Task1();
    protected Task2 t2 = new Task2();

    public static void main(String[] args) throws InterruptedException {
        BusyDelay bd = new BusyDelay();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(bd.t1);
        exec.execute(bd.t2);
        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}

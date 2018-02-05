import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BusyDelay {
    private boolean flag = false;

    class Task1 implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    synchronized (BusyDelay.this) {
                        flag = true;
                        BusyDelay.this.notifyAll();
                    }
                    System.out.println("Set flag to true");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out.println("Exit task1");
        }
    }

    class Task2 implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                synchronized (BusyDelay.this) {
                    try {
                        BusyDelay.this.wait();
                        if (flag) {
                            System.out.println("Set flag to false");
                            flag = false;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
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

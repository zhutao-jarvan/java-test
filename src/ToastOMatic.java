import java.sql.Time;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class Toast {
    public enum Status {DRY, BUTTERED, JAMMED }
    private Status status = Status.DRY;
    private final int id;

    public Toast(int id) { this.id = id; }
    public void butter() {status = Status.BUTTERED;}
    public void jam() {status = Status.JAMMED; }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Toast" + id + " Status: " + status;
    }
}

class ToastQueue extends LinkedBlockingQueue<Toast>{}

class Toaster implements Runnable {
    private ToastQueue toastQueue;
    private int count = 0;
    private Random random = new Random(47);

    public Toaster(ToastQueue toastQueue) {
        this.toastQueue = toastQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                TimeUnit.MILLISECONDS.sleep(100+ random.nextInt(500));
                Toast t = new Toast(count++);
                System.out.println(t);
                toastQueue.put(t);
            }
        } catch (InterruptedException e) {
            System.out.println("Toaster interrupted!");
        }
        System.out.println("Toaster off!");
    }
}

class Butter implements Runnable {
    private ToastQueue dryQueue, butterQueue;

    public Butter(ToastQueue dryQueue, ToastQueue butterQueue) {
        this.dryQueue = dryQueue;
        this.butterQueue = butterQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Toast toast = dryQueue.take();
                toast.butter();
                System.out.println(toast);
                butterQueue.put(toast);
            }
        } catch (InterruptedException e) {
            System.out.println("Butter interrupted!");
        }
        System.out.println("Butter off!");
    }
}

class Jammer implements Runnable {
    private ToastQueue butterQueue, jammerQueue;

    public Jammer(ToastQueue butterQueue, ToastQueue jammerQueue) {
        this.butterQueue = butterQueue;
        this.jammerQueue = jammerQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Toast toast = butterQueue.take();
                toast.jam();
                System.out.println(toast);
                jammerQueue.put(toast);
            }
        } catch (InterruptedException e) {
            System.out.println("Jammer interrupted!");
        }
        System.out.println("Jammer off!");
    }
}

class Eater implements Runnable {
    private ToastQueue finishedQueue;
    private int counter = 0;

    public Eater(ToastQueue finishedQueue) {
        this.finishedQueue = finishedQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Toast t = finishedQueue.take();
                if (t.getId() != counter++ || t.getStatus() != Toast.Status.JAMMED) {
                    System.out.println(">>>>> Error: " + t);
                    System.exit(-1);
                } else {
                    System.out.println("Chomp! " + t);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Eater interrupted!");
        }
        System.out.println("Eater off!");
    }
}

public class ToastOMatic {
    public static void main(String[] args) throws InterruptedException {
        ToastQueue toastQueue = new ToastQueue(),
                butterQueue = new ToastQueue(),
                jammerQueue = new ToastQueue();

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new Toaster(toastQueue));
        exec.execute(new Butter(toastQueue, butterQueue));
        exec.execute(new Jammer(butterQueue, jammerQueue));
        exec.execute(new Eater(jammerQueue));
        TimeUnit.SECONDS.sleep(2);
        exec.shutdownNow();
    }
}

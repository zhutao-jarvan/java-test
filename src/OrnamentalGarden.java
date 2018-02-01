import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Count {
    private int count;
    private Random ran = new Random(47);

    public int inc() {
        int temp = count;
        if (ran.nextBoolean()) {
            Thread.yield();
        }
        count += 1;
        return temp;
    }
    public int value() {return count;}
}

class Entrance implements Runnable {
    private static Count count = new Count();
    private static List<Entrance> entrances = new ArrayList<Entrance>();
    private int number = 0;
    private final int id;
    private static volatile boolean isCanncel = false;

    public static void cancel() {isCanncel = true;}

    public Entrance(int id) {
        this.id = id;
        entrances.add(this);
    }

    @Override
    public void run() {
        while (!isCanncel) {
            synchronized (this) {
                ++number;
            }
            System.out.println(this + " Total: " + count.inc());

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e) {
                System.out.println("Sleep interrupted");
            }
        }
    }

    public int getValue() { return number;}

    @Override
    public String toString() {
        return "Entrace" + id + ": " + getValue();
    }

    public static int getTotalCount() {
        return count.value();
    }

    public static int sumEntrances() {
        int sum = 0;
        for (Entrance en: entrances) {
            sum++;
        }
        return sum;
    }
}

public class OrnamentalGarden {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            exec.execute(new Entrance(i));
        }
        TimeUnit.SECONDS.sleep(1);
        Entrance.cancel();
        exec.shutdown();
        if (exec.awaitTermination(250, TimeUnit.MILLISECONDS));
            System.out.println("Some tasks were not terminated");

        System.out.println("Total: " + Entrance.getTotalCount());
        System.out.println("Sum of Entrance: "+ Entrance.sumEntrances());
    }
}

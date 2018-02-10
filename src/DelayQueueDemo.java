import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

class DelayedTask implements Runnable, Delayed {
    private static int count = 0;
    private final int id = count++;
    private final int delta;
    private final long trigger;
    protected static List<DelayedTask> sequence = new ArrayList<DelayedTask>();

    public DelayedTask(int delayMs) {
        this.delta = delayMs;
        trigger = System.currentTimeMillis() + delayMs;
        sequence.add(this);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return trigger - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        DelayedTask that = (DelayedTask) o;

        if (trigger < that.trigger) return -1;
        if (trigger > that.trigger) return 1;
        return 0;
    }

    @Override
    public void run() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return String.format("[%1$-4d]", delta) + " Task " + id;
    }

    public String summary() {
        return "(" + id + ":" + delta + ")";
    }

    public static class EndSentinel extends DelayedTask {
        private ExecutorService exec;

        public EndSentinel(int delayMs, ExecutorService exec) {
            super(delayMs);
            this.exec = exec;
        }

        @Override
        public void run() {
            for (DelayedTask pt : sequence) {
                System.out.print(pt.summary() + " ");
            }
            System.out.print("\n" + this + "Calling shutdownNow()");
            exec.shutdownNow();
        }
    }
}
class DelayTaskConsumer implements Runnable {
    private DelayQueue<DelayedTask> q;

    public DelayTaskConsumer(DelayQueue<DelayedTask> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                q.take().run();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Finished DelayedTaskConsumer");
    }
}

public class DelayQueueDemo {
    public static void main(String[] args) {
        Random rand = new Random(47);
        ExecutorService exec = Executors.newCachedThreadPool();
        DelayQueue<DelayedTask> queue = new DelayQueue<DelayedTask>();

        for (int i = 0; i < 20; i++) {
            queue.put(new DelayedTask(rand.nextInt(5000)));
        }
        queue.add(new DelayedTask.EndSentinel(5000, exec));
        exec.execute(new DelayTaskConsumer(queue));
    }
}

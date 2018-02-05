import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Car2 extends Car {
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    @Override
    public void waxed() {
        System.out.println("Car2 waxed");
        lock.lock();
        try {
            waxOn = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void buffed() {
        System.out.println("Car2 buffed");
        lock.lock();
        try {
            waxOn = false;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void waitForWaxing() throws InterruptedException {
        System.out.println("Car2 waitForWaxing");
        lock.lock();
        try {
            while (!waxOn)
                condition.await();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void waitForBuffing() throws InterruptedException {
        System.out.println("Car2 waitForBuffing");
        lock.lock();
        try {
            while (waxOn)
                condition.await();
        } finally {
            lock.unlock();
        }
    }
}

public class WaxOMatic2 {
    public static void main(String[] args) throws InterruptedException {
        Car2 car = new Car2();
        ExecutorService exec = Executors.newCachedThreadPool();

        exec.execute(new WaxOn(car));
        exec.execute(new WaxOff(car));
        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}



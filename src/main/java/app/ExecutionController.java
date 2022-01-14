package app;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExecutionController {
    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();

    static private final int defaultAnimationTimeStamp = 1;
    static private final int defaultSimulationTimeStamp = 1;

    private int animationTimeStamp;
    private int simulationTimeStamp;
    private boolean endExecution = false;

    private boolean wait = false;

    public ExecutionController() {
        this.animationTimeStamp = defaultAnimationTimeStamp;
        this.simulationTimeStamp = defaultSimulationTimeStamp;
    }

    public void waitIfEnabled() {
        lock.lock();
        if (wait)
            try {
                cond.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        lock.unlock();
    }
    public void setWaitTrue() {
        lock.lock();
        wait = true;
        lock.unlock();
    }
    public void releaseAll() {
        lock.lock();
        wait = false;
        cond.signalAll();
        lock.unlock();
    }

    private void end() {
        endExecution = true;
        releaseAll();
    }

    public boolean checkIfEnd() {
        return endExecution;
    }

    public void setAnimationTimeStamp(int animationTimeStamp) {
        this.animationTimeStamp = animationTimeStamp;
    }

    public void setSimulationTimeStamp(int simulationTimeStamp) {
        this.simulationTimeStamp = simulationTimeStamp;
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored){}
    }

    public void sleepAnimation() {
        sleep(animationTimeStamp);
    }
    public void sleepSimulation() {
        sleep(simulationTimeStamp);
    }
}

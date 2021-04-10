package progiii.common.util;

import java.util.concurrent.locks.ReentrantLock;

public class ClosableLock extends ReentrantLock {
    public ClosableRes lockAsResource() {
        lock();
        return this::unlock;
    }
}

package progiii.common.util;

import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Permette di ottenere il lock di una generica risorsa e rilasciarlo al termine dell’uso
 */
public class ClosableLock extends ReentrantLock {
    public ClosableRes lockAsResource() {
        lock();
        return this::unlock;
    }
}

package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
	int serialNumber;
	boolean available;

	public Ewok(int serialNumber) {
	    this.serialNumber = serialNumber;
	    this.available = true;
    }
  
    /**
     * Acquires an Ewok
     */
    public void acquire() {
        synchronized (this) {
            available = false;
        }
    }

    //If ewok is occupied, waiting till it released
    //If the thread gets interruption during the wait - aborting the mission
    public synchronized void tryAcquire() throws InterruptedException {
        if (isAvailable()) {
            acquire();
        }
        else {
            try {
                wait();
                acquire();
            }
            catch (InterruptedException e) {
                throw new InterruptedException();
            }
        }
    }

    /**
     * release an Ewok
     */
    public void release() {
        synchronized (this) {
            available = true;
            notifyAll();
        }
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public boolean isAvailable() {
        synchronized (this) {
            return available;
        }
    }
}

package bgu.spl.mics.application.passiveObjects;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static Ewoks ewoksInstance = null;
    private Ewok[] ewoks;
    //public static final Object acquireKey = new Object();
    //public static final Object releaseKey = new Object();

    private Ewoks() {

    }

    public static Ewoks getInstance() {
        if (ewoksInstance == null)
            ewoksInstance = new Ewoks();
        return ewoksInstance;
    }

    public synchronized void allocateEwoks (int numberOfEwoks) {
        ewoks = new Ewok[numberOfEwoks];
        for (int i=0; i< numberOfEwoks; i++) {
            ewoks[i] = new Ewok(i + 1);
        }
    }

    public synchronized void acquireEwok(int ewokSerialNumber) {
        while (!ewoks[ewokSerialNumber - 1].isAvailable()) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ewoks[ewokSerialNumber - 1].acquire();
    }

    public synchronized void releaseEwok(int ewokSerialNumber) {
        ewoks[ewokSerialNumber - 1].release();
        notifyAll();
    }

}

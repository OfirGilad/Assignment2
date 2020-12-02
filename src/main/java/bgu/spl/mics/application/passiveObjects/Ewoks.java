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
    private final Ewok[] ewoks;

    private Ewoks(int numberOfEwoks) {
        ewoks = new Ewok[numberOfEwoks];
        for (int i=0; i< numberOfEwoks; i++) {
            ewoks[i] = new Ewok(i + 1);
        }
    }

    public static Ewoks getInstance() {
        if (ewoksInstance == null)
            ewoksInstance = new Ewoks(Input.getEwoks());
        return ewoksInstance;
    }

    public void acquireEwok(int ewokSerialNumber) {
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

    public void releaseEwok(int ewokSerialNumber) {
        ewoks[ewokSerialNumber - 1].release();
        notifyAll();
    }

}

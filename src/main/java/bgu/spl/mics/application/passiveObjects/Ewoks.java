package bgu.spl.mics.application.passiveObjects;

import java.util.List;

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

    public synchronized static Ewoks getInstance() {
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

    public synchronized Boolean acquireEwoks(List<Integer> ewokSerialNumbers) {
        int ewokIndex = 0;

        //Checking if all needed ewoks are available
        while (ewokIndex < ewokSerialNumbers.size()) {

            //If at least one needed ewok is unavailable, entering waiting room
            //After notification received checking all over again if all the needed ewoks are available
            //InterruptedException causing a mission failure
            if (!ewoks[ewokSerialNumbers.get(ewokIndex)].isAvailable()) {
                try {
                    wait();
                    ewokIndex = 0;
                } catch (InterruptedException e) {
                    return false;
                }
            }
            else {
                ewokIndex++;
            }
        }
        for (Integer ewokSerialNumber : ewokSerialNumbers) {
            ewoks[ewokSerialNumber].acquire();
        }
        return true;
    }

    public synchronized void releaseEwoks(List<Integer> ewokSerialNumbers) {
        for (Integer ewokSerialNumber : ewokSerialNumbers) {
            ewoks[ewokSerialNumber].release();
        }
        notifyAll();
    }

}

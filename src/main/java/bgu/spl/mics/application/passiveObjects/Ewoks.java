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

    private Ewoks() {

    }

    public synchronized static Ewoks getInstance() {
        if (ewoksInstance == null)
            ewoksInstance = new Ewoks();
        return ewoksInstance;
    }

    public synchronized void allocateEwoks (int numberOfEwoks) {
        ewoks = new Ewok[numberOfEwoks];
        for (int i = 0; i < numberOfEwoks; i++) {
            ewoks[i] = new Ewok(i + 1);
        }
    }

    public Boolean acquireEwoks(List<Integer> ewokSerialNumbers) {
        int ewokIndex = 0;
        ewokSerialNumbers.sort(Integer::compareTo);

        //Checking if all needed ewoks are available
        while (ewokIndex < ewokSerialNumbers.size()) {

            //If at least one needed ewok is unavailable, entering waiting room
            //InterruptedException causing a mission failure
            synchronized (this) {
                if (!ewoks[ewokSerialNumbers.get(ewokIndex) - 1].isAvailable()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        releaseEwoks(ewokSerialNumbers);
                        return false;
                    }
                } else {
                    ewoks[ewokSerialNumbers.get(ewokIndex) - 1].acquire();
                    ewokIndex++;
                }
            }
        }
        return true;
    }

    public void releaseEwoks(List<Integer> ewokSerialNumbers) {
        for (Integer ewokSerialNumber : ewokSerialNumbers) {
            synchronized(this) {
                ewoks[ewokSerialNumber - 1].release();
                notifyAll();
            }
        }
    }
}
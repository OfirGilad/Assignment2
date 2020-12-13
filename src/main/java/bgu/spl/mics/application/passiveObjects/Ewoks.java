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
    private Ewok[] ewoks;

    private static class SingletonHolder {
        private static final Ewoks instance = new Ewoks();
    }

    private Ewoks() {

    }

    public static Ewoks getInstance() {
        return SingletonHolder.instance;
    }

    public synchronized void allocateEwoks (int numberOfEwoks) {
        ewoks = new Ewok[numberOfEwoks];
        for (int i = 0; i < numberOfEwoks; i++) {
            ewoks[i] = new Ewok(i + 1);
        }
    }

    public Boolean acquireEwoks(List<Integer> ewokSerialNumbers) {
        int ewokIndex = 0;

        //Checking if all needed ewoks are available
        while (ewokIndex < ewokSerialNumbers.size()) {
            try {

                //If at least one needed ewok is unavailable, entering waiting room (at Ewok class)
                //InterruptedException causing a mission failure
                ewoks[ewokSerialNumbers.get(ewokIndex) - 1].tryAcquire();
                ewokIndex++;
            }
            catch (InterruptedException e) {
                releaseEwoks(ewokSerialNumbers);
                return false;
            }
        }
        return true;
    }

    public void releaseEwoks(List<Integer> ewokSerialNumbers) {
        for (Integer ewokIndex : ewokSerialNumbers) {
            ewoks[ewokIndex - 1].release();
        }
    }
}
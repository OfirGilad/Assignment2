package bgu.spl.mics.application.passiveObjects;


/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    static int totalAttacks;
    static long HanSoloFinish;
    static long C3POFinish;
    static long R2D2Deactivate;
    static long LeiaTerminate;
    static long HanSoloTerminate;
    static long C3POTerminate;
    static long R2D2Terminate;
    static long LandoTerminate;

    public Diary() { }

    public static int getTotalAttacks() {
        return totalAttacks;
    }

    public static long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public static long getC3POFinish() {
        return C3POFinish;
    }

    public static long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public static long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public static long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public static long getC3POTerminate() {
        return C3POTerminate;
    }

    public static long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public static long getLandoTerminate() {
        return LandoTerminate;
    }

    public static void setTotalAttacks(int totalAttacks) {
        Diary.totalAttacks = totalAttacks;
    }

    public static void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public static void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public static void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate;
    }

    public static void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public static void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public static void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    public static void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    public static void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }
}
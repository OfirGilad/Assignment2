package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.passiveObjects.JsonInputReader;
import bgu.spl.mics.application.services.*;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		Input input = JsonInputReader.getInputFromJson("input.json");
		//System.out.println(input.getEwoks());

		Ewoks ewoks = Ewoks.getInstance();
		ewoks.allocateEwoks(input.getEwoks());
		Diary diary = Diary.getInstance();
		diary.setTotalAttacks(input.getAttacks().length);

		CountDownLatch waitForAllToSubEvents = new CountDownLatch(4);
		LeiaMicroservice Leia = new LeiaMicroservice(input.getAttacks());
		HanSoloMicroservice HanSolo = new HanSoloMicroservice(waitForAllToSubEvents);
		C3POMicroservice C3PO = new C3POMicroservice(waitForAllToSubEvents);
		R2D2Microservice R2D2 = new R2D2Microservice(input.getR2D2(), waitForAllToSubEvents);
		LandoMicroservice Lando = new LandoMicroservice(input.getLando(), waitForAllToSubEvents);

		HanSolo.run();
		C3PO.run();
		R2D2.run();
		Lando.run();
		waitForAllToSubEvents.await();
		Leia.run();

	}
}
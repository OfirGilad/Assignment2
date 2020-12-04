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

		//Importing data from json input
		Ewoks ewoks = Ewoks.getInstance();
		ewoks.allocateEwoks(input.getEwoks());

		//Microservices construction
		CountDownLatch waitForAllToSubEvents = new CountDownLatch(4);
		LeiaMicroservice Leia = new LeiaMicroservice(input.getAttacks());
		HanSoloMicroservice HanSolo = new HanSoloMicroservice(waitForAllToSubEvents);
		C3POMicroservice C3PO = new C3POMicroservice(waitForAllToSubEvents);
		R2D2Microservice R2D2 = new R2D2Microservice(input.getR2D2(), waitForAllToSubEvents);
		LandoMicroservice Lando = new LandoMicroservice(input.getLando(), waitForAllToSubEvents);

		//Threads declaration
		Thread LeiaThread = new Thread(Leia);
		Thread HanSoloThread = new Thread(HanSolo);
		Thread C3POThread = new Thread(C3PO);
		Thread R2D2Thread = new Thread(R2D2);
		Thread LandoThread = new Thread(Lando);

		//Threads activation
		HanSoloThread.start();
		C3POThread.start();
		R2D2Thread.start();
		LandoThread.start();
		waitForAllToSubEvents.await();
		LeiaThread.start();

		HanSoloThread.join();
		C3POThread.join();
		R2D2Thread.join();
		LandoThread.join();
		LeiaThread.join();



	}
}
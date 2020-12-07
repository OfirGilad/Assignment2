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
	public static final CountDownLatch waitForAllToSubEvents = new CountDownLatch(4);

	public static void main(String[] args) throws IOException, InterruptedException {
		Input input = JsonInputReader.getInputFromJson("input.json");
		Diary diary = Diary.getInstance();

		//Importing data from json input
		Ewoks ewoks = Ewoks.getInstance();
		ewoks.allocateEwoks(input.getEwoks());

		//Microservices construction
		LeiaMicroservice Leia = new LeiaMicroservice(input.getAttacks());
		HanSoloMicroservice HanSolo = new HanSoloMicroservice();
		C3POMicroservice C3PO = new C3POMicroservice();
		R2D2Microservice R2D2 = new R2D2Microservice(input.getR2D2());
		LandoMicroservice Lando = new LandoMicroservice(input.getLando());

		//Threads declaration
		Thread LeiaThread = new Thread(Leia);
		Thread HanSoloThread = new Thread(HanSolo);
		Thread C3POThread = new Thread(C3PO);
		Thread R2D2Thread = new Thread(R2D2);
		Thread LandoThread = new Thread(Lando);

		System.out.println("A long time ago in a galaxy far, far away....");
		System.out.println("An attack on the empire star destroyer ship has started");

		//Threads activation
		LeiaThread.start();
		HanSoloThread.start();
		C3POThread.start();
		R2D2Thread.start();
		LandoThread.start();

		HanSoloThread.join();
		C3POThread.join();
		R2D2Thread.join();
		LandoThread.join();
		LeiaThread.join();

		System.out.println("Mission Complete!~");
		System.out.println("A time of peace has returned to the galaxy, until the next time another threat will show up...");
	}
}
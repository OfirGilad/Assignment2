package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<MicroService, MicSerQueue> servises;
	private ConcurrentHashMap<Class <? extends Event>,BlockingQueue<MicroService>> round_robin;
	private ConcurrentHashMap<Event, Future> future;

	public MessageBusImpl()
	{
		servises = new ConcurrentHashMap<>();
		round_robin = new ConcurrentHashMap<>();
		future = new ConcurrentHashMap<>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//TODO:check if micrcoservice in registered
		round_robin.get(type).add(m);
		servises.get(m).subscribed_event.offer(type);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		//TODO:check if micrcoservice in registered
		servises.get(m).subscribed_broadcast.offer(type);
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		Future f = future.get(e);
		if (f!=null)
			f.resolve(result);
		future.remove(e,f);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		
        return null;
	}

	@Override
	public void register(MicroService m) {
		servises.putIfAbsent(m,new MicSerQueue());
	}

	@Override
	public void unregister(MicroService m) {
		servises.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		
		return null;
	}

	//TODO: Implement class to be Singleton
	//Code Not final
	public static MessageBusImpl getInstance() {
		return new MessageBusImpl();
	}

}
class MicSerQueue{
	public BlockingQueue<Event> eventQ;
	public BlockingQueue<Class <? extends Event>> subscribed_event;
	public BlockingQueue<Class <? extends Broadcast>> subscribed_broadcast;


	public MicSerQueue()
	{
		this.eventQ= new LinkedBlockingQueue<Event>();
		this.subscribed_event = new LinkedBlockingQueue<Class <? extends Event>>();
		this.subscribed_broadcast = new LinkedBlockingQueue<Class <? extends Broadcast>>();
	}
}

package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Ewoks;

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
	private static MessageBusImpl messageBusImplInstance = null;
	private ConcurrentHashMap<MicroService, MicSerQueue> servises;
	private ConcurrentHashMap<Class <? extends Event>,BlockingQueue<MicroService>> round_robin;
	private ConcurrentHashMap<Event, Future> future;
	private ConcurrentHashMap<Class <? extends Broadcast>, BlockingQueue<MicroService>> broadcast_services;// hash map of queue of microservices for each broadcast

	public MessageBusImpl()
	{
		servises = new ConcurrentHashMap<>();
		round_robin = new ConcurrentHashMap<>();
		future = new ConcurrentHashMap<>();
		broadcast_services = new ConcurrentHashMap<>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//TODO:check if micrcoservice in registered
		round_robin.putIfAbsent(type, new LinkedBlockingQueue<>());
		round_robin.get(type).add(m);
		servises.get(m).subscribed_event.offer(type);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		//TODO:check if micrcoservice in registered
		broadcast_services.putIfAbsent(type, new LinkedBlockingQueue<>());
		if (!broadcast_services.get(type).contains(m))
			broadcast_services.get(type).add(m);
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
		for(MicroService m : this.broadcast_services.get(b.getClass()))
			this.servises.get(m).messageQ.add(b);// maybe offer inted of add (need to check)
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> ret = new Future<>();
		BlockingQueue<MicroService> round_robin_e = round_robin.get(e.getClass());
		if (round_robin_e != null) {
			synchronized (round_robin_e) {
				MicroService m1 = round_robin_e.remove();
				if (m1 != null) {
					if (servises.get(m1) != null) {
						future.put(e, ret);
						round_robin_e.add(m1);
						servises.get(m1).messageQ.offer(e);
						return ret;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		servises.putIfAbsent(m,new MicSerQueue());
	}

	@Override
	public void unregister(MicroService m) {
		for(Class <? extends Event> e : this.servises.get(m).subscribed_event)//remove m from roundrobins
		{
			round_robin.get(e).remove(m);
		}
		for(Class <? extends Broadcast> b : this.servises.get(m).subscribed_broadcast)//remove m from roundrobins
		{
			broadcast_services.get(b).remove(m);
		}

		for (Message msg : servises.get(m).messageQ)
			complete((Event)msg,null);

		servises.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (servises.get(m)==null)
			throw new IllegalStateException("awaitMessage function failed !! ,service: " + m.getName() + "is not registered");
		return servises.get(m).messageQ.take();
	}

	//TODO: Implement class to be Singleton
	public synchronized static MessageBusImpl getInstance() {
		if (messageBusImplInstance == null) {
			messageBusImplInstance = new MessageBusImpl();
		}
		return messageBusImplInstance;
	}

}
class MicSerQueue{
	public BlockingQueue<Message> messageQ;
	public BlockingQueue<Class <? extends Event>> subscribed_event;
	public BlockingQueue<Class <? extends Broadcast>> subscribed_broadcast;


	public MicSerQueue()
	{
		this.messageQ= new LinkedBlockingQueue<>();
		this.subscribed_event = new LinkedBlockingQueue<>();
		this.subscribed_broadcast = new LinkedBlockingQueue<>();
	}
}

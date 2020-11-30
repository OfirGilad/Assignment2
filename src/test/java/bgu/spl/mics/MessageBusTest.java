package bgu.spl.mics;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MessageBusTest {

    private MessageBusImpl messageBus;
    private ExampleBroadcastListenerService firstBroadcastListenerService;
    private ExampleBroadcastListenerService secondBroadcastListenerService;
    private ExampleEventHandlerService eventHandlerService;
    private ExampleMessageSenderService messageSenderService;
    private ExampleBroadcast broadcast;
    private ExampleEvent event;
    private Future<String> future;

    @BeforeEach
    public void setUp(){
        messageBus = new MessageBusImpl();
        firstBroadcastListenerService = new ExampleBroadcastListenerService("broadcastListenerService", new String[]{"1"});
        secondBroadcastListenerService = new ExampleBroadcastListenerService("broadcastListenerService", new String[]{"1"});
        eventHandlerService = new ExampleEventHandlerService("eventHandlerService", new String[]{"1"});
        future = new Future<>();
    }

    @Test
    public void testSubscribeEvent()
    {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        messageBus.register(eventHandlerService);
        assertNull(messageBus.sendEvent(event));
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        assertNotNull(messageBus.sendEvent(event));
        Message message = null;
        try {
            message = messageBus.awaitMessage(eventHandlerService);
        }
        catch (InterruptedException e) {
            fail("Microservice failed to receive the event");
        }
        assertNotNull(message);
        assertEquals(message, event);
    }

    @Test
    public void testSubscribeBroadcast() {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"broadcast"});
        broadcast = new ExampleBroadcast(messageSenderService.getName());
        messageBus.register(firstBroadcastListenerService);
        messageBus.register(secondBroadcastListenerService);
        messageBus.subscribeBroadcast(broadcast.getClass(), firstBroadcastListenerService);
        messageBus.subscribeBroadcast(broadcast.getClass(), secondBroadcastListenerService);
        messageBus.sendBroadcast(broadcast);
        Message message1 = null;
        Message message2 = null;
        try {
            message1 = messageBus.awaitMessage(firstBroadcastListenerService);
        }
        catch (InterruptedException e) {
            fail("First microservice failed to receive the broadcast");
        }
        try {
            message2 = messageBus.awaitMessage(secondBroadcastListenerService);
        }
        catch (InterruptedException e) {
            fail("Second microservice failed to receive the broadcast");
        }
        assertNotNull(message1);
        assertEquals(message1, broadcast);
        assertNotNull(message2);
        assertEquals(message2, broadcast);
    }

    @Test
    public void testComplete() {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        messageBus.register(eventHandlerService);
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        future = messageBus.sendEvent(event);
        messageBus.complete(event, "completed");
        assertEquals(future.get(), "completed");
    }

    @Test
    public void testSendBroadcast() {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"broadcast"});
        broadcast = new ExampleBroadcast(messageSenderService.getName());
        messageBus.register(firstBroadcastListenerService);
        messageBus.register(secondBroadcastListenerService);
        messageBus.subscribeBroadcast(broadcast.getClass(), firstBroadcastListenerService);
        messageBus.subscribeBroadcast(broadcast.getClass(), secondBroadcastListenerService);
        messageBus.sendBroadcast(broadcast);
        Message message1 = null;
        Message message2 = null;
        try {
            message1 = messageBus.awaitMessage(firstBroadcastListenerService);
        }
        catch (InterruptedException e) {
            fail("First microservice failed to receive the broadcast");
        }
        try {
            message2 = messageBus.awaitMessage(secondBroadcastListenerService);
        }
        catch (InterruptedException e) {
            fail("Second microservice failed to receive the broadcast");
        }
        assertNotNull(message1);
        assertEquals(message1, broadcast);
        assertNotNull(message2);
        assertEquals(message2, broadcast);
    }

    @Test
    public void testSendEvent() {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        messageBus.register(eventHandlerService);
        assertNull(messageBus.sendEvent(event));
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        assertNotNull(messageBus.sendEvent(event));
        Message message = null;
        try {
            message = messageBus.awaitMessage(eventHandlerService);
        }
        catch (InterruptedException e) {
            fail("Microservice failed to receive the event");
        }
        assertNotNull(message);
        assertEquals(message, event);
    }

    @Test
    public void testRegister() {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        messageBus.register(eventHandlerService);
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        future = messageBus.sendEvent(event);
        assertNotNull(future);
        messageBus.unregister(eventHandlerService);
        future = messageBus.sendEvent(event);
        assertNull(future);
        try {
            messageBus.unregister(eventHandlerService);
            fail("Unregistered Microservice was received but IllegalStateException wasn't sent");
        }
        catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testUnregister() {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        messageBus.register(eventHandlerService);
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        future = messageBus.sendEvent(event);
        assertNotNull(future);
        messageBus.unregister(eventHandlerService);
        future = messageBus.sendEvent(event);
        assertNull(future);
        try {
            messageBus.unregister(eventHandlerService);
            fail("Unregistered Microservice was received but IllegalStateException wasn't sent");
        }
        catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAwaitMessage() {
        try {
            messageBus.awaitMessage(eventHandlerService);
            fail("Unregistered Microservice was received but no exception was sent");
        }
        catch (IllegalStateException | InterruptedException e) {
            assertTrue(e instanceof  IllegalStateException);
        }
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        messageBus.register(eventHandlerService);
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        messageBus.sendEvent(event);
        Message message = null;
        try {
            message = messageBus.awaitMessage(eventHandlerService);
        } catch (InterruptedException e) {
            fail("Microservice failed to receive the event");
        }
        assertNotNull(message);
        assertEquals(message, event);
    }
}
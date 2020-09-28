package no.fractal.socket.send;

import java.io.BufferedOutputStream;
import java.util.Vector;

/**
 * Message dispatcher is responsible for delivering messages/payloads to a
 * outputstream provided to the dispatcher. The dispatcher is running on its own
 * thread, so it will not block while sending data. Messages are added to a
 * queue, and each message is written to the stream in FIFO order. Adding a
 * message will wake the thread if it is asleep and it will start stream the
 * message.
 */
public class MessageDispatcher {

    /**
     * Message queue for messages to be sent
     */
    private Vector<AbstractMessage> messageQueue = new Vector<AbstractMessage>();;

    /**
     * The stream it will write to
     */
    private BufferedOutputStream outputStream;

    /**
     * Thread the dispatcher is running on
     */
    private Thread dispatchThread;

    /**
     * Binds the outputstream and start the dispatchet thread.
     * 
     * @param bufferedOutputStream the stream we want to transfer to
     */
    public MessageDispatcher(BufferedOutputStream bufferedOutputStream) {

        this.outputStream = bufferedOutputStream;

        this.dispatchThread = new Thread(this::run);
        dispatchThread.start();
    }

    /**
     * Adds a message to the queue, it wakes the thread when message is added.
     * 
     * @param message the message to send to the client
     */
    public synchronized void addMessage(AbstractMessage message) {
        messageQueue.add(message);
        this.notifyAll();
    }

    /**
     * Infinite loops over message queue, and streams the messages while awake. if
     * the queue is empty, the thread waits.
     */
    private synchronized void run() {
        while (true) {
            try {
                if (messageQueue.isEmpty()) {
                    wait();
                }

                AbstractMessage message = messageQueue.remove(0);
                message.writeToStream(this.outputStream);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}

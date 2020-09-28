package no.fractal.socket.send;

import java.io.BufferedOutputStream;
import java.util.Vector;

public class MessageDispatcher {

    private Vector<AbstractMessage> messageQueue;
    private BufferedOutputStream outputStream;

    private Thread dispatchThread;

    public MessageDispatcher(BufferedOutputStream bufferedOutputStream) {
        this.outputStream = bufferedOutputStream;
        this.dispatchThread = new Thread(this::run);
        dispatchThread.start();
    }


    public void addMessage(AbstractMessage message){
        messageQueue.add(message);
        this.notifyAll();
    }

    private void run(){
        while (true){
            try {
                if (messageQueue.isEmpty()) wait();

                AbstractMessage message =  messageQueue.remove(0);
                message.writeToStream(this.outputStream);

            } catch (Exception e){
                e.printStackTrace();
            }



        }
    }



}

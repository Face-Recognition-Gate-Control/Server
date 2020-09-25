package no.fractal.socket.send;

import no.fractal.util.StreamUtils;

import java.io.BufferedOutputStream;
import java.lang.annotation.Target;
import java.util.Vector;

public class MessageDispatcher {

    private Vector<AbstractMessage> messageQue;
    private BufferedOutputStream outputStream;

    private Thread dispatchThread;

    public MessageDispatcher(BufferedOutputStream bufferedOutputStream) {
        this.outputStream = bufferedOutputStream;
        this.dispatchThread = new Thread(this::run);
        dispatchThread.start();
    }


    public void addMessage(AbstractMessage message){
        messageQue.add(message);
        this.notifyAll();
    }

    private void run(){
        while (true){
            try {
                if (messageQue.isEmpty()) wait();

                AbstractMessage message =  messageQue.remove(0);
                message.writeToStream(this.outputStream);

            } catch (Exception e){
                e.printStackTrace();
            }



        }
    }



}

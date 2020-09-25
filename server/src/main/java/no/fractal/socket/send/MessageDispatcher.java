package no.fractal.socket.send;

import java.io.BufferedOutputStream;
import java.lang.annotation.Target;
import java.util.Vector;

public class MessageDispatcher {

    private Vector<AbstractMessage> messageQue;
    private BufferedOutputStream outputStream;

    public MessageDispatcher(BufferedOutputStream bufferedOutputStream) {
        this.outputStream = bufferedOutputStream;
    }

    public void addTask(AbstractMessage message){
        messageQue.add(message);
        this.notifyAll();
    }

    private void run(){
        while (true){
            try {
                if (messageQue.isEmpty()) wait();

                AbstractMessage messageMessage =  messageQue.remove(0);



            } catch (Exception e){
                e.printStackTrace();
            }



        }
    }



}

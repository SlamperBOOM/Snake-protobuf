package model.threads.client;

import model.threads.client.ClientThread;

public class PingThread extends  Thread{
    private boolean isRunning = true;
    private final ClientThread parentThread;
    private final int delay;

    public PingThread(ClientThread parent, int delay){
        this.parentThread = parent;
        this.delay = delay;
    }

    @Override
    public void run(){
        while(isRunning){
            try{
                sleep(delay);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            parentThread.sendPing();
        }
    }

    public void setStopped(){
        isRunning = false;
    }
}

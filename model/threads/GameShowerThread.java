package model.threads;

import model.Model;

public class GameShowerThread extends Thread{
    private boolean isRunning = true;
    private final Model model;

    public GameShowerThread(Model model){
        this.model = model;
    }

    @Override
    public void run(){
        while(isRunning){
            try{
                sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            model.showActiveGames();
        }
    }

    public void setStopped(){
        isRunning = false;
    }
}

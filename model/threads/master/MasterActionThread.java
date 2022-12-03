package model.threads.master;

import model.FieldInfo;
import model.Move;

import java.util.ArrayList;
import java.util.List;

public class MasterActionThread extends Thread{
    private boolean isRunning = true;
    private final int delay;
    private final FieldInfo fieldInfo;
    private final List<Move> moveQueue;

    public MasterActionThread(FieldInfo fieldInfo, int delay){
        this.fieldInfo = fieldInfo;
        this.delay = delay;

        moveQueue = new ArrayList<>();
    }

    @Override
    public void run(){
        while (isRunning){
            try{
                sleep(delay);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            synchronized (moveQueue){
                List<Integer> playersID = new ArrayList<>();
                for(int i = moveQueue.size()-1; i>=0; --i){
                    Move move = moveQueue.get(i);
                    boolean moveMade = false;
                    for(Integer playerID : playersID){
                        if(move.getPlayerID() == playerID){
                            moveQueue.remove(move);
                            moveMade = true;
                            break;
                        }
                    }
                    if(!moveMade){
                        playersID.add(move.getPlayerID());
                        fieldInfo.makeMove(move);
                    }
                }
                moveQueue.clear();
                moveQueue.notifyAll();
            }
            fieldInfo.updateField();
        }
    }

    public void addMove(Move move){
        synchronized (moveQueue){
            moveQueue.add(move);
            moveQueue.notifyAll();
        }
    }

    public void setStopped(){
        isRunning = false;
    }
}

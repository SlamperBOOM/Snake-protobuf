package model;

import graphics.View;
import model.threads.MainThread;
import model.threads.GameShowerThread;
import model.threads.GamesListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private final View view;
    private GamesListener listener;
    private final GameShowerThread gameShower;
    private MainThread mainThread = null;
    private final List<GameInfo> activeGames;

    public Model(){
        view = new View(this);
        try {
            listener = new GamesListener(this);
            listener.start();
        } catch (IOException e){
            e.printStackTrace();
        }
        activeGames = new ArrayList<>();

        gameShower = new GameShowerThread(this);
        gameShower.start();
    }

    public void showActiveGames(){
        synchronized (activeGames) {
            view.showActiveGames(activeGames);
            activeGames.clear();
            activeGames.notifyAll();
        }
    }

    public void addActiveGame(GameInfo gameInfo){
        synchronized (activeGames){
            activeGames.add(gameInfo);
            activeGames.notifyAll();
        }
    }

    public GameInfo createGame(String gameName, String playerName){
        try {
            mainThread = new MainThread(this, new GameInfo(gameName), playerName);
            mainThread.newGame();
            return mainThread.getGameInfo();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public void connect(GameInfo game, String playerName){
        try{
            mainThread = new MainThread(this, game, playerName);
            mainThread.connectToGame(SnakesProto.NodeRole.NORMAL);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void drawField(List<Player> players, List<Point> foods, String playerName){
        view.drawField(players, foods, playerName);
    }

    public void closeGameFrame(){
        view.disconnect();
    }

    public void disconnect(){
        mainThread.disconnect();
        mainThread.stop();
        mainThread = null;
    }

    public void makeMove(KeyEvent e){
        if(mainThread == null){
            return;
        }
        switch (e.getKeyCode()){
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:{
                mainThread.makeMove(SnakesProto.Direction.UP);
                break;
            }
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:{
                mainThread.makeMove(SnakesProto.Direction.LEFT);
                break;
            }
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:{
                mainThread.makeMove(SnakesProto.Direction.RIGHT);
                break;
            }
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:{
                mainThread.makeMove(SnakesProto.Direction.DOWN);
                break;
            }
        }
    }

    public void showMessage(String message){
        view.showMessage(message);
    }

    public void stop(){
        listener.setStopped();
        gameShower.setStopped();
        if(mainThread != null){
            mainThread.stop();
        }
    }

}

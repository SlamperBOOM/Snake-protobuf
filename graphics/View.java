package graphics;

import graphics.game.GameFrame;
import graphics.mainMenu.MainMenu;
import model.GameInfo;
import model.Model;
import model.Player;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class View{
    private final Model model;
    private final MainMenu menu;
    private GameFrame gameFrame;

    public View(Model model){
        this.model = model;

        menu = new MainMenu(this);
        menu.show();
    }

    public void showMessage(String message){
        gameFrame.showMessage(message);
    }

    public void showActiveGames(List<GameInfo> games){
        menu.showGames(games);
    }

    public void createGame(String gameName, String playerName){
        GameInfo gameInfo;
        if((gameInfo = model.createGame(gameName, playerName)) == null){
            menu.showDialog("Нет свободных сокетов");
        }else{
            menu.close();
            gameFrame = new GameFrame(this, gameInfo,
                    gameInfo.getConfig().getWidth(), gameInfo.getConfig().getHeight());
        }
    }

    public void drawField(List<Player> players, List<Point> foods, String playerName){
        gameFrame.drawField(players, foods, playerName);
    }

    public void makeMove(KeyEvent e){
        model.makeMove(e);
    }

    public void disconnect(){
        model.disconnect();
        gameFrame.close();
        menu.show();
    }

    public void enterGame(GameInfo game, String gameName){
        menu.dispose();
        gameFrame = new GameFrame(this, game,
                game.getConfig().getWidth(), game.getConfig().getHeight());
        model.connect(game, gameName);
    }

    public void exit(){
        model.stop();
        menu.close();
    }
}

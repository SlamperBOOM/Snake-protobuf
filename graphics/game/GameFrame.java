package graphics.game;

import graphics.View;
import model.GameInfo;
import model.Player;
import model.SnakesProto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GameFrame{
    private final View view;
    private final JFrame mainWindow;
    private FieldPanel field;
    private PlayerInfoPanel playerArea;

    private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED
                    || e.getID() == KeyEvent.KEY_TYPED) {
                view.makeMove(e);
            }
            return false;
        }
    }

    public GameFrame(View view, GameInfo gameInfo, int width, int height) {
        this.view = view;

        mainWindow = new JFrame("Snake (" + gameInfo.getGameName() + ")");

        JPanel mainPanel = getMainPanel(width, height);

        mainWindow.setContentPane(mainPanel);
        mainWindow.setResizable(false);
        mainWindow.setSize(new Dimension(1000, 700));
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                super.windowClosing(e);
            }
        });
        mainWindow.setVisible(true);
        mainWindow.setLocation(new Point(300, 150));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyDispatcher());
    }

    public void drawField(List<Player> playerList, List<Point> foods, String playerName){
        field.reset();
        playerArea.removeAll();
        for(Player player : playerList){
            if (!player.isAlive()) {
                playerArea.addPlayer(player.getColor(), player.getNickname() + "(zombie)", player.getScore());
            }else if(player.getRole() == SnakesProto.NodeRole.VIEWER &&
            player.getSnake() == null){
                //don't add player to playerList
            } else if (player.getNickname().equals(playerName)) {
                playerArea.addPlayer(player.getColor(), player.getNickname() + "(you)", player.getScore());
            } else if (player.getRole() == SnakesProto.NodeRole.MASTER) {
                playerArea.addPlayer(player.getColor(), player.getNickname() + "(master)", player.getScore());
            } else {
                playerArea.addPlayer(player.getColor(), player.getNickname(), player.getScore());
            }
            field.drawSnake(player.getSnake(), player.getColor());
        }
        playerArea.draw();
        for(Point food : foods){
            field.drawFood(food);
        }
    }

    private JPanel getMainPanel(int width, int height) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        field = new FieldPanel(width, height);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.NONE;
        field.setPreferredSize(new Dimension(650, 650));
        field.setMinimumSize(new Dimension(650, 650));
        panel.add(field, constraints);
        field.updateConstants();

        playerArea = new PlayerInfoPanel();
        playerArea.setPreferredSize(new Dimension(250, 250));
        constraints.gridx = 3;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(playerArea, constraints);

        return panel;
    }

    public void close(){
        mainWindow.dispose();
    }

    private void disconnect(){
        view.disconnect();
    }

    public void showMessage(String message){
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainWindow, message, "Error", JOptionPane.ERROR_MESSAGE));
    }
}

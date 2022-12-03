package graphics.mainMenu;

import graphics.View;
import model.GameInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel implements ActionListener {
    private final GameInfo gameInfo;
    private final View view;
    private final JFrame mainWindow;

    public GamePanel(GameInfo game, View view, JFrame parent){
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        this.view = view;
        this.gameInfo = game;
        mainWindow = parent;

        JLabel gameNameLabel = new JLabel(game.getGameName());
        gameNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        constraints.gridy = 1;
        constraints.gridx = 1;
        constraints.weightx = 0.5;
        add(gameNameLabel, constraints);

        JLabel playersLabel = new JLabel("Players count: " + game.getPlayerCount());
        gameNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        constraints.gridy = 1;
        constraints.gridx = 2;
        add(playersLabel, constraints);

        JButton button = new JButton("---->");
        button.setActionCommand("connect");
        button.addActionListener(this);
        button.setSize(30, 30);
        constraints.gridx = 3;
        add(button, constraints);

        //setMinimumSize(new Dimension(200, 50));
        setPreferredSize(new Dimension(200, 50));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("connect")){
            String playerName = JOptionPane.showInputDialog(mainWindow, "Введите никнейм");
            view.enterGame(gameInfo, playerName);
        }
    }
}

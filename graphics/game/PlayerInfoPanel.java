package graphics.game;

import javax.swing.*;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {
    private final JPanel players;
    private final JScrollPane scrollPane;

    public PlayerInfoPanel(){
        setPreferredSize(new Dimension(250, 425));
        setMinimumSize(new Dimension(250, 425));
        players = new JPanel();
        players.setLayout(new BoxLayout(players, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(players);
        scrollPane.setPreferredSize(new Dimension(250, 400));
        add(scrollPane);
    }

    public void addPlayer(Color color, String nickname, int score){
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.X_AXIS));
        playerPanel.setMinimumSize(new Dimension(250, 50));

        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(color);
        colorPanel.setMaximumSize(new Dimension(30, 30));
        playerPanel.add(colorPanel);

        JLabel nameLabel = new JLabel(nickname);
        playerPanel.add(nameLabel);

        JLabel scoreLabel = new JLabel("  "+score);
        playerPanel.add(scoreLabel);

        players.add(playerPanel);
    }

    public void removeAll(){
        players.removeAll();
        players.updateUI();
    }

    public void draw(){
        scrollPane.revalidate();
    }
}

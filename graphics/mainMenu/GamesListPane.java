package graphics.mainMenu;

import graphics.View;
import model.GameInfo;

import javax.swing.*;
import java.awt.*;

public class GamesListPane extends JPanel {
    private final JScrollPane scrollPane;
    private final JPanel activeGames;

    public GamesListPane(){
        setPreferredSize(new Dimension(250, 300));
        setMinimumSize(new Dimension(250, 300));

        activeGames = new JPanel();
        activeGames.setLayout(new BoxLayout(activeGames, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(activeGames);
        scrollPane.setPreferredSize(new Dimension(250, 275));
        add(scrollPane);

        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void removeGames(){
        activeGames.removeAll();
        activeGames.updateUI();
    }

    public void addGame(GameInfo info, View view, JFrame parent){
        activeGames.add(new GamePanel(info, view, parent));
    }

    public void draw(){
        scrollPane.revalidate();
    }
}

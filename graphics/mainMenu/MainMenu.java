package graphics.mainMenu;

import graphics.View;
import model.GameInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainMenu implements ActionListener {
    private final View view;
    private final JFrame mainWindow;
    private GamesListPane activeGames;

    public MainMenu(View view){
        this.view = view;

        mainWindow = new JFrame("Snake");

        JPanel mainPanel = createGamesPanel();

        mainWindow.setContentPane(mainPanel);
        mainWindow.setResizable(false);
        mainWindow.setSize(new Dimension(500, 500));
        mainWindow.setLocation(new Point(300, 150));
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
                super.windowClosing(e);
            }
        });
    }

    private JPanel createGamesPanel(){
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel title = new JLabel("Snake");
        title.setFont(new Font("TimesRoman", Font.PLAIN, 48));
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(title, constraints);

        JLabel gamesTitle = new JLabel("Доступные игры");
        gamesTitle.setFont(new Font("TimesRoman", Font.PLAIN, 24));
        constraints.gridwidth = 1;
        constraints.gridy = 2;
        constraints.gridx = 2;
        panel.add(gamesTitle, constraints);

        activeGames = new GamesListPane();
        constraints.gridx = 2;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(activeGames, constraints);

        JButton newGameButton = new JButton("Новая игра");
        newGameButton.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        newGameButton.setSize(60, 15);
        newGameButton.addActionListener(this);
        newGameButton.setActionCommand("createGame");
        constraints.gridx = 2;
        constraints.gridy = 7;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(newGameButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 8;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.weighty = 0.5;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), constraints);

        return panel;
    }

    public void showDialog(String message){
        JOptionPane.showMessageDialog(mainWindow, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showGames(List<GameInfo> games){
        activeGames.removeGames();
        if(games.size() == 0){
            activeGames.draw();
        }else {
            for (GameInfo gameInfo : games) {
                activeGames.addGame(gameInfo, view, mainWindow);
            }
            activeGames.draw();
        }

    }

    public void show(){
        mainWindow.setVisible(true);
    }

    public void dispose(){
        mainWindow.setVisible(false);
    }

    public void close(){
        mainWindow.dispose();
    }

    private void exit(){
        view.exit();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("createGame".equals(command)) {
            CreateGameWindow gameWindow = new CreateGameWindow(view);
            gameWindow.show();
        }
    }
}

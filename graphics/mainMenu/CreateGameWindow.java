package graphics.mainMenu;

import graphics.View;
import model.GameInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateGameWindow implements ActionListener {
    private final View view;
    private final JFrame mainWindow;
    private final JTextField gameName;
    private final JTextField playerName;

    public CreateGameWindow(View view){
        this.view = view;
        mainWindow = new JFrame("Создание игры");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel gameNameTitle = new JLabel("Название игры:");
        gameNameTitle.setFont(new Font("TimesRoman", Font.PLAIN, 16));
        panel.add(gameNameTitle);

        gameName = new JTextField(30);
        panel.add(gameName);

        JLabel playerNameTitle = new JLabel("Имя игрока:");
        playerNameTitle.setFont(new Font("TimesRoman", Font.PLAIN, 16));
        panel.add(playerNameTitle);

        playerName = new JTextField(30);
        panel.add(playerName);

        JButton button = new JButton("Создать");
        button.setActionCommand("create");
        button.addActionListener(this);
        panel.add(button);

        mainWindow.setContentPane(panel);
        mainWindow.setSize(new Dimension(250, 200));
    }

    public void show(){
        mainWindow.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("create")){
            view.createGame(gameName.getText(), playerName.getText());
            mainWindow.dispose();
        }
    }
}

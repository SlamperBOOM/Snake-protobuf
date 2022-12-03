package graphics.game;

import model.Snake;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.image.BufferedImage;

public class FieldPanel extends JPanel {
    private BufferedImage image;
    private final int width;
    private final int height;
    private double widthOfBlock;
    private double heightOfBlock;
    private Point leftTop;
    private Point leftBottom;
    private Point rightTop;
    private Point rightBottom;

    public FieldPanel(int width, int height){
        setPreferredSize(new Dimension(700, 700));
        this.width = width;
        this.height = height;
    }

    public void updateConstants(){
        Dimension d = getPreferredSize();

        leftTop = new Point(0,0);
        leftBottom = new Point(0, d.height);
        rightBottom = new Point(d.width, d.height);
        rightTop = new Point(d.width, 0);
        widthOfBlock = 1.0*(rightTop.x - leftTop.x)/width;
        heightOfBlock = 1.0*(rightBottom.y - rightTop.y)/height;
    }

    public void drawSnake(Snake snake, Color color){
        if(snake == null){
            return;
        }
        List<Point> pointList = snake.getPoints();
        Graphics g = image.getGraphics();
        g.setColor(color);

        for(Point point : pointList){
            drawSquare(point, g);
        }
        repaint();
    }

    private void drawCircle(Point point, Graphics g){
        g.fillOval((int)(leftTop.x + point.x*widthOfBlock),
                (int)(leftTop.y + point.y*heightOfBlock),
                (int)widthOfBlock,
                (int)heightOfBlock);
    }

    private void drawSquare(Point point, Graphics g){
        g.fillRect((int)(leftTop.x + point.x*widthOfBlock),
                (int)(leftTop.y + point.y*heightOfBlock),
                (int)widthOfBlock,
                (int)heightOfBlock);
    }

    public void drawFood(Point point){
        Graphics g = image.getGraphics();
        g.setColor(Color.RED);
        drawCircle(point, g);
        repaint();
    }

    private void drawField(){
        Graphics g = image.getGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.drawLine(leftTop.x, leftTop.y, leftBottom.x, leftBottom.y);
        g.drawLine(leftBottom.x, leftBottom.y, rightBottom.x, rightBottom.y);
        g.drawLine(rightBottom.x, rightBottom.y, rightTop.x, rightTop.y);
        g.drawLine(rightTop.x, rightTop.y, leftTop.x, leftTop.y);

        repaint();
    }

    public void reset(){
        image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        drawField();
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}

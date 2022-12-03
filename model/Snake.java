package model;

import java.awt.*;
import java.util.List;

public class Snake {
    private List<Point> points;
    private SnakesProto.Direction direction;

    public Snake(List<Point> initialPoints, SnakesProto.Direction direction){
        points = initialPoints;
        this.direction = direction;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public void setDirection(SnakesProto.Direction direction) {
        this.direction = direction;
    }

    public List<Point> getPoints(){
        return points;
    }

    public SnakesProto.Direction getDirection() {
        return direction;
    }
}

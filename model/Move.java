package model;

public class Move {
    private final SnakesProto.Direction direction;
    private final int playerID;

    public Move(SnakesProto.Direction direction, int playerID) {
        this.direction = direction;
        this.playerID = playerID;
    }

    public SnakesProto.Direction getDirection() {
        return direction;
    }

    public int getPlayerID() {
        return playerID;
    }
}

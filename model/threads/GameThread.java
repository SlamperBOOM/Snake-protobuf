package model.threads;

import model.SnakesProto;

public interface GameThread {
    void repeatMessage(SnakesProto.GameMessage gameMessage);
}

package model;

import model.threads.master.MasterActionThread;
import model.threads.master.MasterThread;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FieldInfo {
    private final List<Player> players;
    private final int width;
    private final int height;
    private MasterActionThread thread;
    private final int foodCount;
    private final int delay;
    private final List<Point> foods;
    private final int[][] freeSpaceOnField;
    private MasterThread masterThread;
    private int stateOrder = 0;

    public FieldInfo(SnakesProto.GameConfig config) {
        width = config.getWidth();
        height = config.getHeight();
        foodCount = config.getFoodStatic();
        delay = config.getStateDelayMs();

        players = new ArrayList<>();
        foods = new ArrayList<>();
        freeSpaceOnField = new int[width][height];
        clearFillField();
    }

    public void setMasterThread(MasterThread masterThread){
        this.masterThread = masterThread;
    }

    public void newGame(){
        for(int i=0 ; i< foodCount; ++i){
            Point point = findSquare();
            if(point != null){
                foods.add(point);
                freeSpaceOnField[point.x][point.y] = 1;
            }
        }
    }

    private void clearFillField(){
        for(int i=0; i<width; ++i){
            Arrays.fill(Arrays.stream(freeSpaceOnField[i]).toArray(), 0);
        }
    }

    public void setField(SnakesProto.GameState state){//client
        List<SnakesProto.GamePlayer> gamePlayers = state.getPlayers().getPlayersList();
        List<SnakesProto.GameState.Coord> gotFoods = state.getFoodsList();
        List<SnakesProto.GameState.Snake> snakes = state.getSnakesList();

        stateOrder = state.getStateOrder();
        synchronized (players) {
            List<Player> exitedPlayers = new ArrayList<>();
            for (Player player : players) {
                boolean stillExists = false;
                for (SnakesProto.GamePlayer gamePlayer : gamePlayers) {
                    if (player.getID() == gamePlayer.getId()) {
                        stillExists = true;
                        break;
                    }
                }
                if (!stillExists) {
                    exitedPlayers.add(player);
                }
            }
            players.removeAll(exitedPlayers);
            for (SnakesProto.GamePlayer gamePlayer : gamePlayers) {
                boolean doesntExist = true;
                Player player = null;
                for (Player player1 : players) {
                    if (gamePlayer.getId() == player1.getID()) {
                        doesntExist = false;
                        player = player1;
                        break;
                    }
                }
                if (doesntExist) {
                    Player newPlayer = new Player(gamePlayer.getId(), gamePlayer.getName());
                    Snake snake;
                    SnakesProto.GameState.Snake gotSnake = null;
                    for (SnakesProto.GameState.Snake listSnake : snakes) {
                        if (listSnake.getPlayerId() == gamePlayer.getId()) {
                            gotSnake = listSnake;
                            break;
                        }
                    }
                    if (gotSnake == null) {
                        continue;
                    }
                    if (gotSnake.getState() == SnakesProto.GameState.Snake.SnakeState.ZOMBIE) {
                        newPlayer.setZombie();
                    } else {
                        newPlayer.setAlive();
                    }
                    List<SnakesProto.GameState.Coord> coords = gotSnake.getPointsList();
                    List<Point> points = new ArrayList<>();
                    for (SnakesProto.GameState.Coord coord : coords) {
                        points.add(new Point(coord.getX(), coord.getY()));
                    }
                    snake = new Snake(points, gotSnake.getHeadDirection());
                    newPlayer.setSnake(snake);
                    newPlayer.setScore(gamePlayer.getScore());
                    newPlayer.setRole(gamePlayer.getRole());
                    newPlayer.setAddress(gamePlayer.hasIpAddress() ? gamePlayer.getIpAddress() : "");
                    newPlayer.setPort(gamePlayer.hasPort() ? gamePlayer.getPort() : 0);
                    players.add(newPlayer);
                } else {
                    Snake snake;
                    SnakesProto.GameState.Snake gotSnake = null;
                    for (SnakesProto.GameState.Snake listSnake : snakes) {
                        if (listSnake.getPlayerId() == gamePlayer.getId()) {
                            gotSnake = listSnake;
                            break;
                        }
                    }
                    if (gotSnake == null) {
                        player.setSnake(null);
                    } else {
                        if (gotSnake.getState() == SnakesProto.GameState.Snake.SnakeState.ZOMBIE) {
                            player.setZombie();
                        } else {
                            player.setAlive();
                        }
                        List<SnakesProto.GameState.Coord> coords = gotSnake.getPointsList();
                        List<Point> points = new ArrayList<>();
                        for (SnakesProto.GameState.Coord coord : coords) {
                            points.add(new Point(coord.getX(), coord.getY()));
                        }
                        snake = new Snake(points, gotSnake.getHeadDirection());
                        player.setSnake(snake);
                    }
                    player.setScore(gamePlayer.getScore());
                    player.setRole(gamePlayer.getRole());
                    player.setAddress(gamePlayer.hasIpAddress() ? gamePlayer.getIpAddress() : "");
                    player.setPort(gamePlayer.hasPort() ? gamePlayer.getPort() : 0);
                }
            }
            players.notifyAll();
        }

        foods.clear();
        for(SnakesProto.GameState.Coord food : gotFoods){
            foods.add(new Point(food.getX(), food.getY()));
        }
    }

    public void masterStart() {
        thread = new MasterActionThread(this, delay);
        thread.start();
    }

    public void masterStop(){
        if(thread != null) {
            thread.setStopped();
            thread = null;
        }
    }

    public int addPlayer(Player player){
        Point startPoint = findSquare();
        if(startPoint != null){
            Random random = new Random(System.currentTimeMillis());
            int choice = random.nextInt(4);
            Point secondPoint;
            SnakesProto.Direction direction;
            switch (choice){
                case 0:{
                    secondPoint = new Point(
                            startPoint.x - 1 >= 0 ? startPoint.x - 1 : width - 1,
                            startPoint.y);
                    direction = SnakesProto.Direction.RIGHT;
                    break;
                }
                case 1:{
                    secondPoint = new Point(
                            startPoint.x,
                            startPoint.y - 1 >= 0 ? startPoint.y - 1 : height - 1);
                    direction = SnakesProto.Direction.DOWN;
                    break;
                }
                case 2:{
                    secondPoint = new Point(
                            startPoint.x + 1 < width ? startPoint.x + 1 : 0,
                            startPoint.y);
                    direction = SnakesProto.Direction.LEFT;
                    break;
                }
                case 3:{
                    secondPoint = new Point(
                            startPoint.x,
                            startPoint.y + 1 < height ? startPoint.y + 1 : 0);
                    direction = SnakesProto.Direction.UP;
                    break;
                }
                default:{
                    return 1;
                }
            }
            List<Point> points = new ArrayList<>();
            points.add(startPoint);
            points.add(secondPoint);
            player.setSnake(new Snake(points, direction));
            synchronized (players) {
                players.add(player);
                players.notifyAll();
            }
            fillSquareAround(startPoint);
            fillSquareAround(secondPoint);
            return 0;
        }else{
            return 1;
        }
    }

    private void fillSquareAround(Point point){//need for putting new snakes
        for(int i= point.x - 2; i <= point.x + 2; ++i){
            int I = i >= 0 ? i : width + i;
            I = I < width ? I : I % width;
            for(int j = point.y - 2; j <= point.y +2; ++j){
                int J = j >= 0 ? j : height + j;
                J = J < height ? J : J % height;
                freeSpaceOnField[I][J] = 1;
            }
        }
    }

    private Point findSquare(){
        int iterCount = 0;
        int max = width*height*10;
        while(true) {
            Random random = new Random(System.currentTimeMillis());
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            if (freeSpaceOnField[x][y] == 0) {
                return new Point(x, y);
            }
            iterCount++;
            if(iterCount > max){
                return null;
            }
        }
    }

    public void deletePlayer(int playerID){//player exited
        synchronized (players) {
            for (Player player : players) {
                if (player.getID() == playerID) {
                    players.remove(player);
                    break;
                }
            }
            players.notifyAll();
        }
    }

    public void addMove(Move move){
        thread.addMove(move);
    }

    public void makeMove(Move move){//changing direction
        Player player = getPlayerByID(move.getPlayerID());
        if(player.getSnake() == null)
        {
            return;
        }
        SnakesProto.Direction direction = player.getSnake().getDirection();
        switch (move.getDirection()){
            case UP:{
                if(!(direction == SnakesProto.Direction.DOWN)){
                    player.getSnake().setDirection(move.getDirection());
                }
                break;
            }
            case DOWN:{
                if(!(direction == SnakesProto.Direction.UP)){
                    player.getSnake().setDirection(move.getDirection());
                }
                break;
            }
            case LEFT:{
                if(!(direction == SnakesProto.Direction.RIGHT)){
                    player.getSnake().setDirection(move.getDirection());
                }
                break;
            }
            case RIGHT:{
                if(!(direction == SnakesProto.Direction.LEFT)){
                    player.getSnake().setDirection(move.getDirection());
                }
                break;
            }
        }
    }

    public void updateField(){ //master
        if(masterThread == null){
            return;
        }
        clearFillField();
        synchronized (players) {
            for (Player player : players) {//moving snakes
                Snake snake = player.getSnake();
                if (snake == null) {
                    continue;
                }
                SnakesProto.Direction direction = snake.getDirection();
                List<Point> points = snake.getPoints();
                int x = points.get(0).x;
                int y = points.get(0).y;
                switch (direction) {
                    case RIGHT: {
                        if (x == width - 1) {
                            points.add(0, new Point(0, y));
                            if (!checkFood(new Point(0, y))) {
                                points.remove(points.size() - 1);
                            } else {
                                player.incrementScore();
                            }
                        } else {
                            points.add(0, new Point(x + 1, y));
                            if (!checkFood(new Point(x + 1, y))) {
                                points.remove(points.size() - 1);
                            } else {
                                player.incrementScore();
                            }
                        }
                        break;
                    }
                    case LEFT: {
                        if (x == 0) {
                            points.add(0, new Point(width - 1, y));
                            if (!checkFood(new Point(width - 1, y))) {
                                points.remove(points.size() - 1);
                            } else {
                                player.incrementScore();
                            }
                        } else {
                            points.add(0, new Point(x - 1, y));
                            if (!checkFood(new Point(x - 1, y))) {
                                points.remove(points.size() - 1);
                            } else {
                                player.incrementScore();
                            }
                        }
                        break;
                    }
                    case UP: {
                        if (y == 0) {
                            points.add(0, new Point(x, height - 1));
                            if (!checkFood(new Point(x, height - 1))) {
                                points.remove(points.size() - 1);
                            } else {
                                player.incrementScore();
                            }
                        } else {
                            points.add(0, new Point(x, y - 1));
                            if (!checkFood(new Point(x, y - 1))) {
                                points.remove(points.size() - 1);
                            } else {
                                player.incrementScore();
                            }
                        }
                        break;
                    }
                    case DOWN: {
                        if (y == height - 1) {
                            points.add(0, new Point(x, 0));
                            if (!checkFood(new Point(x, 0))) {
                                points.remove(points.size() - 1);
                            } else {
                                player.incrementScore();
                            }
                        } else {
                            points.add(0, new Point(x, y + 1));
                            if (!checkFood(new Point(x, y + 1))) {
                                points.remove(points.size() - 1);
                            } else {
                                player.incrementScore();
                            }
                        }
                        break;
                    }
                }
                for (Point point : points) {
                    fillSquareAround(point);
                    freeSpaceOnField[point.x][point.y] = 2;
                }
            }
            if(masterThread == null){
                return;
            }
            for (Player player1 : players) {//checking for lose
                Snake snake1 = player1.getSnake();
                if (snake1 == null) {
                    continue;
                }
                for (Player player2 : players) {
                    Snake snake2 = player2.getSnake();
                    if (snake2 == null) {
                        continue;
                    }
                    Point head1 = snake1.getPoints().get(0);
                    Point head2 = snake2.getPoints().get(0);
                    if (player1.getID() != player2.getID() && head1.equals(head2)) {
                        player2.setRole(SnakesProto.NodeRole.VIEWER);
                        player1.setRole(SnakesProto.NodeRole.VIEWER);
                        masterThread.sendLose(player1.getID());
                        masterThread.sendLose(player2.getID());
                        player1.setLost();
                        player2.setLost();
                    }
                    for (int i = 1; i < snake2.getPoints().size(); ++i) {
                        if (head1.equals(snake2.getPoints().get(i))) {
                            masterThread.sendLose(player1.getID());
                            player1.setLost();
                            player1.setRole(SnakesProto.NodeRole.VIEWER);
                            player2.incrementScore();
                            break;
                        }
                    }
                    for (int i = 1; i < snake1.getPoints().size(); ++i) {
                        if (head2.equals(snake1.getPoints().get(i))) {
                            masterThread.sendLose(player2.getID());
                            player2.setLost();
                            player2.setRole(SnakesProto.NodeRole.VIEWER);
                            player1.incrementScore();
                            break;
                        }
                    }
                }
            }
            List<Player> exitedPlayers = new ArrayList<>();
            for (Player player : players) {
                if (player.isLost()) {
                    player.setSnake(null);
                }
                if (player.isDisconnected() && player.getSnake() == null) {
                    exitedPlayers.add(player);
                }
            }
            players.removeAll(exitedPlayers);
            for (int i = foods.size(); i < foodCount + players.size(); ++i) {
                while (true) {
                    Random random = new Random(System.currentTimeMillis());
                    int x = random.nextInt(width);
                    int y = random.nextInt(height);
                    Point newFood = new Point(x, y);
                    boolean isCollide = false;
                    for (Point food : foods) {
                        if (newFood.equals(food)) {
                            isCollide = true;
                            break;
                        }
                    }
                    if (!isCollide && freeSpaceOnField[x][y] != 2) {
                        foods.add(newFood);
                        break;
                    }
                }
            }
            players.notifyAll();
        }
        if(masterThread == null){
            return;
        }
        masterThread.updateState();
    }

    private boolean checkFood(Point point){
        for(Point food : foods){
            if(food.equals(point)){
                foods.remove(food);
                return true;
            }
        }
        return false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public SnakesProto.GameState getGameState(){ //master
        SnakesProto.GameState.Builder state = SnakesProto.GameState.newBuilder();

        SnakesProto.GamePlayers.Builder gamePlayers = SnakesProto.GamePlayers.newBuilder();
        synchronized (players) {
            for (Player player : players) {
                gamePlayers.addPlayers(
                        SnakesProto.GamePlayer.newBuilder()
                                .setId(player.getID())
                                .setRole(player.getRole())
                                .setScore(player.getScore())
                                .setName(player.getNickname())
                                .setIpAddress(player.getAddress())
                                .setPort(player.getPort())
                );
                if(player.getSnake() == null){
                    continue;
                }
                SnakesProto.GameState.Snake.Builder snake = SnakesProto.GameState.Snake.newBuilder();
                if (player.isAlive()) {
                    snake.setState(SnakesProto.GameState.Snake.SnakeState.ALIVE);
                } else {
                    snake.setState(SnakesProto.GameState.Snake.SnakeState.ZOMBIE);
                }
                for (Point point : player.getSnake().getPoints()) {
                    snake.addPoints(SnakesProto.GameState.Coord.newBuilder().setX(point.x).setY(point.y));
                }
                snake.setHeadDirection(player.getSnake().getDirection());
                snake.setPlayerId(player.getID());
                state.addSnakes(snake);
            }
            players.notifyAll();
        }
        state.setPlayers(gamePlayers);
        for(Point point : foods){
            state.addFoods(SnakesProto.GameState.Coord.newBuilder().setX(point.x).setY(point.y));
        }
        state.setStateOrder(stateOrder);
        stateOrder++;
        return state.build();
    }

    public Player getPlayerByID(int ID){
        Player returnPlayer = null;
        synchronized (players){
            for(Player player : players){
                if(player.getID() == ID){
                    returnPlayer = player;
                }
            }
            players.notifyAll();
        }
        return returnPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Integer> getPlayersID(){
        List<Integer> playersID = new ArrayList<>();
        synchronized (players) {
            for (Player player : players) {
                if (player.isAlive()) {
                    playersID.add(player.getID());
                }
            }
            players.notifyAll();
        }
        return playersID;
    }

    public int getMasterID(){
        int masterID = 0;
        synchronized (players){
            for(Player player : players){
                if(player.getRole() == SnakesProto.NodeRole.MASTER){
                    players.notifyAll();
                    masterID = player.getID();
                    break;
                }
            }
        }
        return masterID;
    }

    public int getDeputyID(){
        int deputyID = 0;
        synchronized (players){
            for(Player player : players){
                if(player.getRole() == SnakesProto.NodeRole.DEPUTY){
                    players.notifyAll();
                    deputyID = player.getID();
                    break;
                }
            }
        }
        return deputyID;
    }

    public List<Point> getFoods() {
        return foods;
    }
}

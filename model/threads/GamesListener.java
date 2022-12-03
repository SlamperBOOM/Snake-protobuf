package model.threads;

import com.google.protobuf.InvalidProtocolBufferException;
import model.GameInfo;
import model.Model;
import model.SnakesProto;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class GamesListener extends Thread{
    private final MulticastSocket server;
    private boolean isRunning = true;
    private final Model model;

    public GamesListener(Model model) throws IOException {
        server = new MulticastSocket(9192);
        server.joinGroup(InetAddress.getByName("239.192.0.4"));
        server.setSoTimeout(1000);
        this.model = model;
    }

    @Override
    public void run(){
        while (isRunning){
            DatagramPacket packet = new DatagramPacket(new byte[1500], 1500);
            try{
                server.receive(packet);
            }catch (SocketTimeoutException e){
                continue;
            }
            catch (IOException e){
                e.printStackTrace();
                continue;
            }
            SnakesProto.GameMessage message;
            try {
                byte[] data = packet.getData();
                message = SnakesProto.GameMessage.parseFrom(Arrays.copyOfRange(data, 0, packet.getLength()));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                continue;
            }
            if(message.getTypeCase() != SnakesProto.GameMessage.TypeCase.ANNOUNCEMENT){
                continue;
            }
            var games = message.getAnnouncement().getGamesList();
            for(SnakesProto.GameAnnouncement gameAnnouncement : games){
                if(gameAnnouncement.hasCanJoin() && !gameAnnouncement.getCanJoin()){
                    continue;
                }
                GameInfo gameInfo = new GameInfo(gameAnnouncement);
                gameInfo.setGameAddress(packet.getAddress().getHostAddress());
                gameInfo.setGamePort(packet.getPort());
                model.addActiveGame(gameInfo);
            }
        }
    }

    public void setStopped(){
        isRunning = false;
    }
}

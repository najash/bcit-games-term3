package network;

import entities.*;
import packets.*;

class ClientManager implements Runnable {

    public ClientEntityForManagementOnServer client;
    private final Server server;
    private final GameSetting setting;
    public ClientManager(Server server, ClientEntityForManagementOnServer client, GameSetting setting) {
        this.client = client;
        this.server = server;
        this.setting = setting;
    }

    @Override
    public void run() {
        Object obj = Utils.incoming(client.socket);

        Type type = ((Packet) obj).type;

        if (type == Type.LoginClient) {
            handleClientLogin((PacketClientLogin) obj);
        } else if (type == Type.LogoutClient) {
            handleClientLogout((Packet) obj);
        } else if (type == Type.StartGame) {
            server.broadcast((Packet) obj);     
        } else {
            //ignore invalid packets
            System.err.println("received unknown packet.");
        }
    }

    private void handleClientLogin(PacketClientLogin packet) {
        // Register the username
        client.setUsername(packet.username);

        // Send confirmation to client
        Utils.outgoing(new PacketLoginConfirmation(client.id), client.socket);

        server.broadcast(new PacketLoginBroadcast(client.id, packet.username));
        System.out.println("Connected Clients: ");
        for(ClientEntityForManagementOnServer c : server.getAllClients()) {
            System.out.println(c.username);
        }
        if(server.getClientCount() >= setting.getMaxPlayer()) {
            server.startGame();
        }
    }

    private void handleClientLogout(Packet packet) {
        server.removeClient(client);
        System.out.println("total clients: " + server.getClientCount());
        server.broadcast(packet);
    }

}
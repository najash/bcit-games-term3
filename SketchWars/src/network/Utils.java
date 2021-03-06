package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;

import entities.ClientEntityForManagementOnServer;
import packets.Packet;

// NOTE :: ALL FUNCTIONS IN THIS FILE CAN AND SHOULD BE DONE IN THEIR OWN THREADS SO AS TO IMPROVE OVERALL
// PERFORMANCE. and so that the main thread doesn't hangup waiting for these to finish.


public class Utils {
    // Incoming packet
    public static Object incoming(Socket socket) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Object obj = inputStream.readObject();

            return obj;

        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    // Sending a packet
    public static void outgoing(Packet packet, Socket socket)
    {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(packet);
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // send packet to everyone
    public static void broadcast(Packet packet, Collection<ClientEntityForManagementOnServer> clients)
    {
        for(ClientEntityForManagementOnServer client : clients)
        {
                Utils.outgoing(packet, client.socket);
        }
    }
	
}

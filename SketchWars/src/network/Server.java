package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Collection;

import entities.ClientEntityForManagementOnServer;
import packets.*;

public class Server implements Runnable {
	public int port;
	public int currentClientId = 0;
	public ServerSocket socket = null;
	public boolean isRunning = false;
	public Thread runner = null;
	public HashMap<Integer, ClientEntityForManagementOnServer> clients = null;

	public Server(int port) {
		this.port = port;
		clients = new HashMap<Integer, ClientEntityForManagementOnServer>();
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		synchronized (this) {
			runner = Thread.currentThread();
			isRunning = true;
		}

		while (isRunning()) {

			try {
				ClientEntityForManagementOnServer client = new ClientEntityForManagementOnServer(currentClientId,
						socket.accept());

				clients.put(currentClientId, client);

				new Thread(new ClientManager(this, client)).start();

				currentClientId++;  

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void startGame() {
		PeerInfo[] arrayOfPeers = new PeerInfo[clients.size()];
		int i = 0;
		for(ClientEntityForManagementOnServer client : clients.values()) {
			arrayOfPeers[i++] = new PeerInfo(client.socket.getInetAddress(), client.socket.getPort(), client.username, client.id);
		}
		PacketStart packet = new PacketStart(arrayOfPeers);
		Utils.broadcast(packet, clients.values());
	}

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void stop() {
		isRunning = false;

		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void broadcast(Packet packet) {
        Utils.broadcast(packet, clients.values());
	}

	public synchronized Collection<ClientEntityForManagementOnServer> getAllClients() {
		return clients.values();
	}

	public synchronized void removeClient(ClientEntityForManagementOnServer client) {
		clients.remove(client.id);
	}

	public synchronized int getClientCount() {
		return clients.size();
	}

}
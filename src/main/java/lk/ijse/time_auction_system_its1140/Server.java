package lk.ijse.time_auction_system_its1140;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    static ServerSocket serverSocket;
    static List<Socket> clients = new ArrayList<>();
    static List<String> clientNames = new ArrayList<>();

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(6000);
            System.out.println(" Auction Server started on port 6000...");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        String name = "Unknown";
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            name = in.readUTF();

            synchronized (clients) {
                clients.add(socket);
                clientNames.add(name);
            }

            System.out.println(name + " joined. Total: " + clients.size());
            broadcast(">> " + name + " joined the chat.", socket);

            while (true) {
                String message = in.readUTF();
                System.out.println(message);
                broadcast(message, socket);
            }

        } catch (IOException e) {
            // Client disconnected
            synchronized (clients) {
                int idx = clients.indexOf(socket);
                if (idx >= 0) {
                    clientNames.remove(idx);
                    clients.remove(idx);
                }
            }
            System.out.println(name + " disconnected. Remaining: " + clients.size());
            broadcast(">> " + name + " left the chat.", socket);
        }
    }

    private static void broadcast(String message, Socket sender) {
        synchronized (clients) {
            for (Socket s : clients) {
                if (s != sender) {
                    try {
                        DataOutputStream out = new DataOutputStream(s.getOutputStream());
                        out.writeUTF(message);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
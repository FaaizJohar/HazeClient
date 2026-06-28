package com.hazeclient.ipc;

import com.hazeclient.HazeClientMod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HazeIPCClient {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static Thread listenerThread;

    public static void initialize() {
        String portStr = System.getProperty("hazeclient.ipc.port");
        if (portStr == null || portStr.isEmpty()) {
            HazeClientMod.LOGGER.warn("No IPC port provided. HazeClient will run in standalone mode.");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            HazeClientMod.LOGGER.error("Invalid IPC port: " + portStr, e);
            return;
        }

        try {
            socket = new Socket("127.0.0.1", port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            HazeClientMod.LOGGER.info("Successfully connected to Haze Launcher IPC on port " + port);

            // Start listening for incoming packets from the Launcher
            listenerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        handlePacket(line);
                    }
                } catch (Exception e) {
                    HazeClientMod.LOGGER.error("IPC listener thread error", e);
                }
            });
            listenerThread.setDaemon(true);
            listenerThread.start();

            // Send handshake
            sendPacket("{\"type\": \"handshake\", \"client\": \"HazeClient In-Game\"}");
            
        } catch (Exception e) {
            HazeClientMod.LOGGER.error("Failed to connect to IPC Server on port " + port, e);
        }
    }

    public static void sendPacket(String json) {
        if (out != null) {
            out.println(json);
        }
    }

    private static void handlePacket(String json) {
        HazeClientMod.LOGGER.info("Received IPC Packet: " + json);
        // Here we would parse the JSON and update themes/modules live
    }
}

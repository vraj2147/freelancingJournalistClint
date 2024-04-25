package com.example;

import java.io.*;
import java.net.*;

public class EditorServer {
    private static final int SERVER_PORT = 8080;
    private static final String PROCESSING_SERVER_HOST = "localhost";
    private static final int PROCESSING_SERVER_PORT = 10001;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Editor Server is running and listening on port " + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create a new thread to handle the client connection
                Thread clientThread = new Thread(() -> handleClientConnection(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("An error occurred in the Editor Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClientConnection(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             Socket processingSocket = new Socket(PROCESSING_SERVER_HOST, PROCESSING_SERVER_PORT);
             PrintWriter processingWriter = new PrintWriter(processingSocket.getOutputStream(), true)) {

            System.out.println("Connected to Processing Centre Server: " + processingSocket);

            String story;
            while ((story = reader.readLine()) != null) {
                System.out.println("Received story from client: " + story);
                // Perform editing or processing of the story
                String editedStory = processStory(story);
                // Send the edited story to the Processing Centre Server
                processingWriter.println(editedStory);
                System.out.println("Sent edited story to Processing Centre Server: " + editedStory);
            }

            System.out.println("Client disconnected: " + clientSocket);
        } catch (IOException e) {
            System.err.println("An error occurred while handling client connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String processStory(String story) {
        // Perform editing or processing logic here
        return story + " (Edited)";
    }
}
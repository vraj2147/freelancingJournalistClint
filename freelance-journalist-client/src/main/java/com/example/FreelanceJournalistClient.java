package com.example;



import java.io.*;
import java.net.*;

public class FreelanceJournalistClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to the Editor Server.");

            while (true) {
                System.out.print("Enter the story (or 'quit' to exit): ");
                String story = reader.readLine();

                if (story.equalsIgnoreCase("quit")) {
                    break;
                }

                writer.println(story);
                System.out.println("Story sent to the Editor Server.");
            }

            System.out.println("Disconnected from the Editor Server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

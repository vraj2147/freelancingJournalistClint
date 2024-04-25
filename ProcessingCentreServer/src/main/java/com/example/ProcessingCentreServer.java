package com.example;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.*;
import java.net.*;

public class ProcessingCentreServer {
    private static final Logger logger = LoggerFactory.getLogger(ProcessingCentreServer.class);

    private static final int SERVER_PORT = 10001;
    private static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "PublishedStoriesQueue";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Processing Centre Server is running and listening on port " + SERVER_PORT);

            Connection connection = createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = createMessageProducer(session);

            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Editor Server connected: " + clientSocket);

                    Thread clientThread = new Thread(() -> handleEditorServerConnection(clientSocket, session, producer));
                    clientThread.start();
                }
            } finally {
                connection.close();
                System.out.println("ActiveMQ connection closed");
            }
        } catch (IOException | JMSException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleEditorServerConnection(Socket clientSocket, Session session, MessageProducer producer) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String story;
            while ((story = reader.readLine()) != null) {
                System.out.println("Received story from Editor Server: " + story);
                publishStory(session, producer, story);
            }
            System.out.println("Editor Server disconnected: " + clientSocket);
        } catch (IOException | JMSException e) {
            System.err.println("An error occurred while handling Editor Server connection: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client socket closed");
            } catch (IOException e) {
                System.err.println("An error occurred while closing client socket: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static Connection createConnection() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        System.out.println("ActiveMQ connection established");
        return connection;
    }

    private static MessageProducer createMessageProducer(Session session) throws JMSException {
        Destination destination = session.createQueue(QUEUE_NAME);
        MessageProducer producer = session.createProducer(destination);
        System.out.println("MessageProducer created for queue: " + QUEUE_NAME);
        return producer;
    }

    private static void publishStory(Session session, MessageProducer producer, String storyDetails) throws JMSException {
        TextMessage message = session.createTextMessage(storyDetails);
        producer.send(message);
        System.out.println("Published story: " + storyDetails);
    }
}

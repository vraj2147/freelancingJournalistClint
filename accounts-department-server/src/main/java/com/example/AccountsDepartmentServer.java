package com.example;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.HashSet;
import java.util.Set;

public class AccountsDepartmentServer {
    private static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "PublishedStoriesQueue";
    private static final Set<String> processedStories = new HashSet<>();

    public static void main(String[] args) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(QUEUE_NAME);
        MessageConsumer consumer = session.createConsumer(destination);

        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        String storyDetails = textMessage.getText();
                        processPublishedStory(storyDetails);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        System.out.println("Accounts Department Server started. Waiting for published stories...");

        // Keep the server running
        while (true) {
            // Do nothing, let the message listener handle incoming messages
        }
    }

    private static void processPublishedStory(String storyDetails) {
        // Extract relevant information from the storyDetails string
        String[] details = storyDetails.split(",");
        String journalistId = details[0].trim();
        String storyId = details[1].trim();
        double paymentAmount = Double.parseDouble(details[2].trim());

        // Check if the story has already been processed
        if (processedStories.contains(storyId)) {
            System.out.println("Duplicate story received. Skipping payment processing for story ID: " + storyId);
            return;
        }

        System.out.println("Received published story: " + storyDetails);

        // Process the payment for the journalist
        boolean paymentSuccess = initiatePayment(journalistId, storyId, paymentAmount);

        if (paymentSuccess) {
            System.out.println("Payment processed successfully for journalist: " + journalistId);
            // Update the payment records in the database or perform any other necessary actions
            updatePaymentRecords(journalistId, storyId, paymentAmount);
            // Add the story ID to the set of processed stories
            processedStories.add(storyId);
        } else {
            System.out.println("Payment processing failed for journalist: " + journalistId);
            // Handle the payment failure scenario, such as retrying or sending a notification
            handlePaymentFailure(journalistId, storyId, paymentAmount);
        }
    }

    private static boolean initiatePayment(String journalistId, String storyId, double paymentAmount) {
        // Implement the logic to initiate the payment through a payment gateway or API
        // Return true if the payment is successful, false otherwise
        // For demonstration purposes, we'll assume the payment is always successful
        System.out.println("Initiating payment for journalist: " + journalistId + ", story ID: " + storyId + ", amount: " + paymentAmount);
        return true;
    }

    private static void updatePaymentRecords(String journalistId, String storyId, double paymentAmount) {
        // Implement the logic to update the payment records in the database
        // You can use JDBC or an ORM framework to interact with the database
        // For demonstration purposes, we'll just print a message
        System.out.println("Payment records updated for journalist: " + journalistId + ", story ID: " + storyId + ", amount: " + paymentAmount);
    }

    private static void handlePaymentFailure(String journalistId, String storyId, double paymentAmount) {
        // Implement the logic to handle payment failures, such as retrying or sending a notification
        // For demonstration purposes, we'll just print a message
        System.out.println("Handling payment failure for journalist: " + journalistId + ", story ID: " + storyId + ", amount: " + paymentAmount);
    }
}
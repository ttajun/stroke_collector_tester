package ksb.strokesos.tester.io;

import ksb.strokesos.tester.common.Constants;
import ksb.strokesos.tester.common.IConfigurable;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.krb5.internal.crypto.Des;

import javax.jms.*;
import java.util.Random;

public class ActiveMQClient implements MessageListener {
    private final static Logger log = LoggerFactory.getLogger(ActiveMQClient.class);

    private static ActiveMQClient INSTANCE = null;

    private Session session;
    private Destination tempDest;
    private MessageProducer producer;

    public synchronized static ActiveMQClient getInstance(IConfigurable config) {
        if (INSTANCE == null) {
            INSTANCE = new ActiveMQClient(config);
            return INSTANCE;
        } else {
            return INSTANCE;
        }
    }

    private ActiveMQClient(IConfigurable config) {
        String activeMQ = config.getSetting(Constants.CONF_ACTIVE_MQ_URL);
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQ);
        connectionFactory.setUseAsyncSend(true);
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            String clientQueueName = "SENSOR";
            Destination adminQueue = session.createQueue(clientQueueName);

            //Setup a message producer to send message to the queue the server is consuming from
            producer = session.createProducer(adminQueue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            //Create a temporary queue that this client will listen for responses on then create a consumer
            //that consumes message from this temporary queue...for a real application a client should reuse
            //the same temp queue for each message to the server...one temp queue per client
            tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);
            //This class will handle the messages to the temp queue as well
            responseConsumer.setMessageListener(this);

        } catch (Exception e) {
            log.error(e.getMessage() + ", Cause: " + e.getCause());
        }
    }

    public void sendJMS(String message) {
        try {
            TextMessage txtMessage = session.createTextMessage();
            txtMessage.setText(message);

            //Set the reply to field to the temp queue you created above, this is the queue the server
            //will respond to
            txtMessage.setJMSReplyTo(tempDest);

            //Set a correlation ID so when you get a response you know which sent message the response is for
            //If there is never more than one outstanding message to the server then the
            //same correlation ID can be used for all the messages...if there is more than one outstanding
            //message to the server you would presumably want to associate the correlation ID with this
            //message somehow...a Map works good
            String correlationId = this.createRandomString();
            txtMessage.setJMSCorrelationID(correlationId);
            producer.send(txtMessage);
        } catch (Exception e) {
            log.error(e.getMessage() + ", Cause: " + e.getCause());
        }
    }

    private String createRandomString() {
        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();
        return Long.toHexString(randomLong);
    }

    public void onMessage(Message message) {
        String messageText = null;
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                messageText = textMessage.getText();
                log.info("messageText = " + messageText);
            }
        } catch (JMSException e) {
            log.error(e.getMessage() + ", Cause: " + e.getCause());
            //Handle the exception appropriately
        }
    }
}

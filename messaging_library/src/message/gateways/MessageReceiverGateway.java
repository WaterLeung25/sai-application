package message.gateways;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class MessageReceiverGateway {
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageConsumer consumer;

    public MessageReceiverGateway(String channelName){
        try {
            Properties properties = new Properties();
            properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            properties.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            properties.put(("queue." + channelName), channelName);
            Context jndiContext = new InitialContext(properties);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
                    .lookup("ConnectionFactory");

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            destination = (Destination) jndiContext.lookup(channelName);
            consumer = session.createConsumer(destination);

            connection.start();

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    public void setListener(MessageListener ml){
        try{
            consumer.setMessageListener(ml);
        } catch (JMSException e){
            e.printStackTrace();
        }
    }
}

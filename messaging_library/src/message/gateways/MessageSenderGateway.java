package message.gateways;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class MessageSenderGateway {
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;
    private Message requestMsg;

    public MessageSenderGateway(String channel){
        try{
            Properties properties = new Properties();
            properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            properties.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            properties.put(("queue." + channel), channel);
            Context jndiContext = new InitialContext(properties);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
                    .lookup("ConnectionFactory");

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            destination = (Destination) jndiContext.lookup(channel);
            producer = session.createProducer(destination);

            connection.start();

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    public Message createTextMessage(String body){
        try {
            requestMsg = session.createTextMessage(body);
            requestMsg.setJMSReplyTo(destination);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return requestMsg;
    }

    public void send(Message msg){
        try {
            producer.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}

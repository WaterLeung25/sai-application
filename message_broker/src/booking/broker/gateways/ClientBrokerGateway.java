package booking.broker.gateways;

import booking.client.gateway.serializer.BookingSerializer;
import booking.client.model.ClientBookingReply;
import booking.client.model.ClientBookingRequest;
import jdk.jshell.EvalException;
import message.gateways.*;
import org.springframework.expression.EvaluationException;

import javax.jms.*;
import java.util.HashMap;

public abstract class ClientBrokerGateway {
    private MessageSenderGateway sender = new MessageSenderGateway("bookingReplyR1");
    private MessageReceiverGateway receiver = new MessageReceiverGateway("bookingRequestD1");
    private BookingSerializer serializer = new BookingSerializer();

    HashMap<ClientBookingRequest, String> mapForReceivingMessage = new HashMap<>();

    public void receiveBookingRequest(){
        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message != null){
                    System.out.println("New bookingRequest is received");
                    try {
                        String id = message.getJMSMessageID();
                        System.out.println("The received bookingRequest messageId is " + id);
                        TextMessage textMessage = (TextMessage) message;
                        String text = textMessage.getText();

                        //deserialize a bookingRequest from the text of the received message
                        ClientBookingRequest bookingRequest = serializer.requestFromString(text);
                        System.out.println("The received bookingRequest is " + bookingRequest.toString());

                        if (id != null){
                            //map the id with the received bookingRequest as the key
                            mapForReceivingMessage.put(bookingRequest, id);
                            System.out.println("id " + id + " is mapped to " + bookingRequest);
                        }

                        onBookingRequestArrived(bookingRequest);

                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public abstract void onBookingRequestArrived(ClientBookingRequest request) throws EvaluationException;

    public void sendBookingReply(ClientBookingReply bookingReply, ClientBookingRequest bookingRequest){
        if (bookingReply != null){
            String bookingReplyStr = serializer.replyToString(bookingReply);
            Message bookingReplyMsg = sender.createTextMessage(bookingReplyStr);
            String id = mapForReceivingMessage.get(bookingRequest);
            try {
                bookingReplyMsg.setJMSCorrelationID(id);
                System.out.println("New bookingReply message CorrelationID is " + id);
                sender.send(bookingReplyMsg);
                System.out.println("New bookingReply message is sent");
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}

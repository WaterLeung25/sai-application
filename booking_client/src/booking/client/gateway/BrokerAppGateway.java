package booking.client.gateway;

import booking.client.gateway.serializer.BookingSerializer;
import booking.client.model.ClientBookingReply;
import booking.client.model.ClientBookingRequest;
import message.gateways.MessageReceiverGateway;
import message.gateways.MessageSenderGateway;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;

public abstract class BrokerAppGateway {
    private MessageSenderGateway sender = new MessageSenderGateway("bookingRequestD1");
    private MessageReceiverGateway receiver = new MessageReceiverGateway("bookingReplyR1");
    private BookingSerializer serializer = new BookingSerializer();

    HashMap<String, ClientBookingRequest> mapForReceivingMessage = new HashMap<>();

    public void sendBookingRequest(ClientBookingRequest request){
        try{
            String bookingRequestStr = serializer.requestToString(request);
            Message requestMsg = sender.createTextMessage(bookingRequestStr);
            sender.send(requestMsg);
            System.out.println("New bookingRequest is sent \n" + requestMsg);

            String messageId = requestMsg.getJMSMessageID();
            System.out.println("New bookingRequest messageId is " + messageId);

            //map the new booking request with its messageId as the key
            mapForReceivingMessage.put(messageId,request);
            System.out.println("New bookingRequest " + request.toString() + " is mapped to " + messageId);

        } catch (JMSException e){
            e.printStackTrace();
        }
    }

    public void receiveBookingReply(){
        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message != null){
                    try{
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("New bookingReply is received");
                        String id = message.getJMSCorrelationID();
                        System.out.println("New bookingReply message CorrelationID is " + id);
                        String text = textMessage.getText();
                        ClientBookingReply bookingReply = serializer.replyFromString(text);
                        System.out.println("The received bookingReply is " + bookingReply);
                        if (id != null){
                            ClientBookingRequest bookingRequest = mapForReceivingMessage.get(id);
                            System.out.println("The bookingRequest is " + bookingRequest);

                            onBookingReplyArrived(bookingRequest, bookingReply);
                        }
                    } catch (JMSException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public abstract void onBookingReplyArrived(ClientBookingRequest request, ClientBookingReply reply);

}

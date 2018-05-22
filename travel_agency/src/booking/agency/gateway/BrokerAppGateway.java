package booking.agency.gateway;

import booking.agency.gateway.serializer.AgencySerializer;
import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import message.gateways.MessageReceiverGateway;
import message.gateways.MessageSenderGateway;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;

public abstract class BrokerAppGateway {
    private MessageSenderGateway sender = new MessageSenderGateway("agencyReplyR1");
    private MessageReceiverGateway receiver;
    private AgencySerializer serializer = new AgencySerializer();

    HashMap<AgencyRequest, Message> mapForReceivingMessage = new HashMap<>();

    //private int aggregationId = 1;

    public BrokerAppGateway(String agencyName){
        receiver = new MessageReceiverGateway(agencyName + "_agencyRequestD1");
    }

    public void receiveAgencyRequest(){
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

                        //deserialize a agencyRequest from the text of the received message
                        AgencyRequest agencyRequest = serializer.requestFromString(text);
                        System.out.println("The received agencyRequest is " + agencyRequest.toString());

                        if (id != null){
                            //map the id with the received agencyRequest as the key
                            mapForReceivingMessage.put(agencyRequest, message);
                            System.out.println("id " + id + " is mapped to " + agencyRequest);
                        }

                        onAgencyRequestArrived(agencyRequest);

                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public abstract void onAgencyRequestArrived(AgencyRequest request);

    public void sendAgencyReply(AgencyRequest request, AgencyReply reply){
        String agencyReplyStr = serializer.replyToString(reply);
        Message agencyReplyMsg = sender.createTextMessage(agencyReplyStr);
        try {
            Message agencyRequestMessage = mapForReceivingMessage.get(request);
            agencyReplyMsg.setJMSCorrelationID(agencyRequestMessage.getJMSMessageID());
            System.out.println("New agencyReply message correlationID is " + mapForReceivingMessage.get(request));
            agencyReplyMsg.setIntProperty("AggregationId", agencyRequestMessage.getIntProperty("AggregationId"));
            System.out.println("New agencyReply AggregationId is " + agencyRequestMessage.getIntProperty("AggregationId"));
           // aggregationId++;
            sender.send(agencyReplyMsg);
            System.out.println("New agencyReply is sent");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}

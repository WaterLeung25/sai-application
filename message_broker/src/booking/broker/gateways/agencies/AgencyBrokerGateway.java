package booking.broker.gateways.agencies;

import booking.agency.gateway.serializer.AgencySerializer;
import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import message.gateways.*;
import net.sourceforge.jeval.Evaluator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.HashMap;


public abstract class AgencyBrokerGateway {
   //private MessageSenderGateway sender;
    private MessageReceiverGateway receiver = new MessageReceiverGateway("agencyReplyR1");;
    private AgencySerializer serializer = new AgencySerializer();

    private int aggregationId = 1;
    private static int counter = 0;
    //private List<LowestTotalPrice> priceList = new ArrayList<>();
    private HashMap<Integer, LowestTotalPrice> mapForReceivingPrice = new HashMap<>();

    private HashMap<String, AgencyRequest> mapForReceivingMessage = new HashMap<>();

    private ArrayList<AgencySender> agencySenders = new ArrayList<>();

    public AgencyBrokerGateway(){
        //agency rules
        String Fast = "#{transferDistance} == 0.0";
        String Good = "#{transferDistance} >= 0.0 && #{transferDistance} <= 40.0";
        String Cheap = "#{transferDistance} >= 10.0 && #{transferDistance} <= 50.0";

        agencySenders.add(new AgencySender("Fast_agencyRequestD1", Fast));
        agencySenders.add(new AgencySender("Good Service_agencyRequestD1", Good));
        agencySenders.add(new AgencySender("Cheap_agencyRequestD1", Cheap));

    }


    public void sendAgencyRequest(AgencyRequest request){
        if (request != null){
            //create evaluator for evaluation of the agency rules
            Evaluator evaluator = new Evaluator();
            Double distance = request.getTransferDistance();
            evaluator.putVariable("transferDistance", distance.toString());
            //get the evaluated result
            String result;
            try {
                for (AgencySender a : agencySenders){
                    if (a.evaluateAgency(request)){
                        this.sendRequestMsg(a, request);
                    }
                }

//                result = evaluator.evaluate(FAST);
//                boolean fastRule = result.equals("1.0");
//                result = evaluator.evaluate(GOOD);
//                boolean goodRule = result.equals("1.0");
//                result = evaluator.evaluate(CHEAP);
//                boolean cheapRule = result.equals("1.0");
//                //set the sender gateway
//                if (fastRule){
//                    sender = new MessageSenderGateway("Fast_agencyRequestD1");
//                    sendRequestMsg(request);
//                }
//                if (goodRule){
//                    sender = new MessageSenderGateway("Good Service_agencyRequestD1");
//                    sendRequestMsg(request);
//                }
//                if (cheapRule){
//                    sender = new MessageSenderGateway("Cheap_agencyRequestD1");
//                    sendRequestMsg(request);
//                }
            } catch (net.sourceforge.jeval.EvaluationException e) {
                e.printStackTrace();
            }

            aggregationId++;
            //reset the counter
            counter = 0;
        }
    }

    private void sendRequestMsg(AgencySender agencySender, AgencyRequest request){
        String agencyRequestStr = serializer.requestToString(request);
        Message requestMsg = agencySender.createTextMessage(agencyRequestStr);
        try {
            requestMsg.setIntProperty("AggregationId", aggregationId);
            System.out.println("New agencyRequest AggregationId is " + aggregationId);
            agencySender.sendMessage(requestMsg);
            System.out.println("New agencyRequest is sent \n" + requestMsg);
            counter++;
            LowestTotalPrice lowestTotalPrice = new LowestTotalPrice(aggregationId);
            lowestTotalPrice.setCounter(counter);
            System.out.println("There are " + counter + " sent agencyRequests");
            //priceList.add(lowestTotalPrice);
            mapForReceivingPrice.put(aggregationId, lowestTotalPrice);

            String messageId = requestMsg.getJMSMessageID();
            System.out.println("New bookingRequest messageId is " + messageId);

            //map the new agency request with its messageId as the key
            mapForReceivingMessage.put(messageId,request);
            System.out.println("New bookingRequest " + request.toString() + " is mapped to " + messageId);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void receiveAgencyReply(){
        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message != null){
                    System.out.println("New agencyReply is received");
                    try {
                        String id = message.getJMSCorrelationID();
                        System.out.println("The received agencyReply message CorrelationID is " + id);
                        int received_aggregationId = message.getIntProperty("AggregationId");
                        System.out.println("The received agencyReply message AggregationId is " + received_aggregationId);

                        TextMessage textMessage = (TextMessage) message;
                        String text = textMessage.getText();

                        //deserialize a agencyReply from the text of the received message
                        AgencyReply agencyReply = serializer.replyFromString(text);
                        System.out.println("The received bookingRequest is " + agencyReply.toString());

                        LowestTotalPrice price = mapForReceivingPrice.get(received_aggregationId);
                        boolean finished = price.addNewAgencyReply(agencyReply);
                        if (finished){
                            AgencyReply lowest = new AgencyReply();
                            lowest.setTotalPrice(price.getLowestPrice());
                            lowest.setNameAgency(price.getLowestPriceAgency());
                            //set the agencyReply to the one who gave the lowest totalPrice
                            agencyReply = lowest;

                            if (id != null){
                                //get the agencyRequest by the agencyReply message correlationID
                                AgencyRequest agencyRequest = mapForReceivingMessage.get(id);
                                System.out.println("The agencyRequest is " + agencyRequest.toString());

                                onAgencyReplyArrived(agencyRequest, agencyReply);
                            }
                        }

//                        for (LowestTotalPrice p : priceList){
//                            if (p.getAggregationId() == received_aggregationId){
//                                double totalPrice =  agencyReply.getTotalPrice();
//
//                                //set lowest totalPrice
//                                if (p.getLowestPrice() == 0 || p.getLowestPrice() > totalPrice){
//                                    p.setLowestPrice(totalPrice);
//                                    p.setLowestPriceAgency(agencyReply.getNameAgency());
//                                }
//                                //count down if it still need to wait other agencyReplies
//                                if (p.getCounter() >= 1){
//                                    p.receiveAgencyReply();
//                                    System.out.println("Waiting " + p.getCounter() + " agencyReplies");
//                                }
//                                //after received all the agencyReplies
//                                else if (p.getCounter() == 0) {
//                                    AgencyReply lowest = new AgencyReply();
//                                    lowest.setTotalPrice(p.getLowestPrice());
//                                    lowest.setNameAgency(p.getLowestPriceAgency());
//                                    //set the agencyReply to the one who gave the lowest totalPrice
//                                    agencyReply = lowest;
//
//                                    if (id != null){
//                                        //get the agencyRequest by the agencyReply message correlationID
//                                        AgencyRequest agencyRequest = mapForReceivingMessage.get(id);
//                                        System.out.println("The agencyRequest is " + agencyRequest.toString());
//
//                                        onAgencyReplyArrived(agencyRequest, agencyReply);
//                                    }
//                                }
//                            }
//                        }

                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public abstract void onAgencyReplyArrived(AgencyRequest agencyRequest, AgencyReply reply);
}

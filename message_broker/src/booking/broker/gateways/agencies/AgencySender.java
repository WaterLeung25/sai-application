package booking.broker.gateways.agencies;

import booking.agency.model.AgencyRequest;
import message.gateways.MessageSenderGateway;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import javax.jms.Message;

public class AgencySender {
    private MessageSenderGateway sender;

    //agency rules
//    String Fast = "#{transferDistance} == 0.0";
//    String Good = "#{transferDistance} >= 0.0 && #{transferDistance} <= 40.0";
//    String Cheap = "#{transferDistance} >= 10.0 && #{transferDistance} <= 50.0";

    Evaluator evaluator = new Evaluator();
    String rule = null;

    public AgencySender(String queueName, String rule){
        sender = new MessageSenderGateway(queueName);
        this.rule = rule;
    }

    public boolean evaluateAgency(AgencyRequest request) throws EvaluationException {
        Double distance = request.getTransferDistance();
        evaluator.putVariable("transferDistance", distance.toString());
        return evaluator.evaluate(rule).equals("1.0");
    }

    public Message createTextMessage(String text){
        return sender.createTextMessage(text);
    }

    public void sendMessage(Message message){
        sender.send(message);
    }
}

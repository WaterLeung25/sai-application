package booking.agency.gateway.serializer;

import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import com.owlike.genson.Genson;

public class AgencySerializer {
    private Genson genson = new Genson();

    //serialize an agency request to a string
    public String requestToString(AgencyRequest request){
        return genson.serialize(request);
    }

    //deserialize an agency request from a string
    public AgencyRequest requestFromString(String str){
        return genson.deserialize(str, AgencyRequest.class);
    }

    //serialize an agency reply to a string
    public String replyToString(AgencyReply reply){
        return genson.serialize(reply);
    }

    //deserialize an agency reply from a string
    public AgencyReply replyFromString(String str){
        return genson.deserialize(str, AgencyReply.class);
    }
}

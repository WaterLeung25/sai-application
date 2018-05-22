package booking.client.gateway.serializer;

import booking.client.model.ClientBookingReply;
import booking.client.model.ClientBookingRequest;
import com.owlike.genson.Genson;

public class BookingSerializer {
    private Genson genson = new Genson();

    //serialize a booking request to a string
    public String requestToString(ClientBookingRequest request){
        return genson.serialize(request);
    }

    //deserialize a booking request from a string
    public ClientBookingRequest requestFromString(String str){
        return genson.deserialize(str, ClientBookingRequest.class);
    }

    //serialize a booking reply to a string
    public String replyToString(ClientBookingReply reply){
        return genson.serialize(reply);
    }

    //deserialize a booking reply from a string
    public ClientBookingReply replyFromString(String str){
        return genson.deserialize(str, ClientBookingReply.class);
    }
}

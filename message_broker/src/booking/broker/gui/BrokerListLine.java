package booking.broker.gui;
import booking.client.model.*;
import booking.agency.model.*;

public class BrokerListLine {
    private ClientBookingRequest bookingRequest;
    private ClientBookingReply bookingReply;
    private AgencyRequest agencyRequest;
    private AgencyReply agencyReply;

    public BrokerListLine(ClientBookingRequest bookingRequest) {
        setBookingRequest(bookingRequest);
    }

    public ClientBookingRequest getBookingRequest(){
        return this.bookingRequest;
    }

    public void setBookingRequest(ClientBookingRequest request){
        this.bookingRequest = request;
    }

    public ClientBookingReply getBookingReply() {
        return bookingReply;
    }

    public void setBookingReply(ClientBookingReply bookingReply) {
        this.bookingReply = bookingReply;
    }

    public AgencyRequest getAgencyRequest() {
        return agencyRequest;
    }

    public void setAgencyRequest(AgencyRequest agencyRequest) {
        this.agencyRequest = agencyRequest;
    }

    public AgencyReply getAgencyReply() {
        return agencyReply;
    }

    public void setAgencyReply(AgencyReply agencyReply) {
        this.agencyReply = agencyReply;
    }

    @Override
    public String toString(){
        return this.bookingRequest.toString() + " || " + ((agencyReply != null) ? agencyReply.toString() : "waiting for reply...");
    }
}

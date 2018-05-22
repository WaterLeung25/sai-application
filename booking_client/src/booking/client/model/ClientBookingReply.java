package booking.client.model;

/**
 *
 * This class stores all information about a client booking reply.
 */
public class ClientBookingReply {


    private String agencyName;
    private double totalPrice;

    public ClientBookingReply() {
        super();
        setAgencyName(null);
        setTotalPrice(0);
    }

    public ClientBookingReply(String agencyName, double totalPrice) {
        super();
        setAgencyName(agencyName);
        setTotalPrice(totalPrice);
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String reasonRejected) {
        this.agencyName = reasonRejected;
    }
    
        public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double costs) {
        this.totalPrice = costs;
    }

    @Override
    public String toString() {
        return  Double.toString(totalPrice) + ", " + agencyName;
    }
}

package booking.agency.model;

/**
 * This class stores information about the agency reply for a booking.
 */
public class AgencyReply {
    
    private String nameAgency;
    private double totalPrice;
    
    public AgencyReply() {
        super();
        
    }
    
    public AgencyReply(String nameAgency, double price) {
        super();
        setNameAgency(nameAgency);
        setTotalPrice(price);
    }
    
    public String getNameAgency() {
        return nameAgency;
    }
    
    public void setNameAgency(String nameAgency) {
        this.nameAgency = nameAgency;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    @Override
    public String toString() {
        return Double.toString(totalPrice) + "-" + nameAgency;
    }
}

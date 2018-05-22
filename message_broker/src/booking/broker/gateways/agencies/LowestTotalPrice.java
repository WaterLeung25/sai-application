package booking.broker.gateways.agencies;

import booking.agency.model.AgencyReply;

public class LowestTotalPrice {
    private int aggregationId;
    private int counter;
    private double lowestPrice;
    private String lowestPriceAgency = null;

    public LowestTotalPrice(int id){
        setAggregationId(id);
    }

    public void setAggregationId(int aggregationId) {
        this.aggregationId = aggregationId;
    }

    public int getAggregationId() {
        return aggregationId;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

//    public void setLowestPrice(double lowestPrice) {
//        this.lowestPrice = lowestPrice;
//    }

    public double getLowestPrice() {
        return lowestPrice;
    }

    public boolean addNewAgencyReply(AgencyReply reply){
        if (lowestPriceAgency == null){
            lowestPriceAgency = reply.getNameAgency();
            lowestPrice = reply.getTotalPrice();
        } else {
            if (reply.getTotalPrice() < lowestPrice){
                lowestPrice = reply.getTotalPrice();
                lowestPriceAgency = reply.getNameAgency();
            }
        }
        if (counter >= 1){
            this.counter--; //count down when new agency reply arrived
            if (counter == 0){
                return true; //if the calculation is done
            }
        }
        return false;
    }

//    public void setLowestPriceAgency(String lowestPriceAgency) {
//        this.lowestPriceAgency = lowestPriceAgency;
//    }

    public String getLowestPriceAgency() {
        return lowestPriceAgency;
    }

    //calculate how many replies need to wait
//    public void receiveAgencyReply(){
//        this.counter--;
//    }
}

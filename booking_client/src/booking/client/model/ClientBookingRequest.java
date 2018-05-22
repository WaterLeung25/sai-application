package booking.client.model;

/**
 *
 * This class stores all information about a client booking request.
 *
 */
public class ClientBookingRequest {

    private String originAirport;
    private String destinationAirport;
    private Address transferToAddress;
    private int numberOfTravellers;

    public ClientBookingRequest() {
        super();
        setOriginAirport("");
        setDestinationAirport("");
        setTransferToAddress(null);
        setNumberOfTravellers(1);
    }

    public ClientBookingRequest(String originAirport, String destinationAirport, int nrTravellers, Address transfer) {
        super();
        setOriginAirport(originAirport);
        setDestinationAirport(destinationAirport);
        setTransferToAddress(transfer);
        setNumberOfTravellers(nrTravellers);
    }

    public ClientBookingRequest(String originAirport, String destinationAirport, int nrTravellers) {
        super();
        setOriginAirport(originAirport);
        setDestinationAirport(destinationAirport);
        setTransferToAddress(null);
        setNumberOfTravellers(nrTravellers);
    }

    @Override
    public String toString() {
        return originAirport + "-" + destinationAirport + "-" + numberOfTravellers + "-"+(transferToAddress != null ? ("transfer to " + transferToAddress) : "");
    }

    public String getOriginAirport() {
        return originAirport;
    }

    public void setOriginAirport(String originAirport) {
        this.originAirport = originAirport;
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public Address getTransferToAddress() {
        return transferToAddress;
    }

    public void setTransferToAddress(Address transferToAddress) {
        this.transferToAddress = transferToAddress;
    }

    public int getNumberOfTravellers() {
        return numberOfTravellers;
    }

    public void setNumberOfTravellers(int numberOfTravellers) {
        this.numberOfTravellers = numberOfTravellers;
    }
}

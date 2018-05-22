package booking.agency.model;

/**
 *
 * This class stores information about the agency request for a booking.
 */
public class AgencyRequest {

    private String fromAirport; 
    private String toAirport;
    private double transferDistance; // necesarry transfer distance in kilometers

    public AgencyRequest() {
        super();
        setToAirport(null);
        setFromAirport(null);
        setTransferDistance(0);
    }

    public AgencyRequest(String toAirport, String fromAirport, double transferDistance) {
        super();
        setToAirport(toAirport);
        setFromAirport(fromAirport);
        setTransferDistance(transferDistance);
    }

    public String getFromAirport() {
        return fromAirport;
    }

    public void setFromAirport(String fromAirport) {
        this.fromAirport = fromAirport;
    }

    public String getToAirport() {
        return toAirport;
    }

    public void setToAirport(String toAirport) {
        this.toAirport = toAirport;
    }

    public double getTransferDistance() {
        return transferDistance;
    }

    public void setTransferDistance(double transferDistance) {
        this.transferDistance = transferDistance;
    }

    @Override
    public String toString() {
        return fromAirport + "-" + toAirport + "-" + transferDistance;
    }
}

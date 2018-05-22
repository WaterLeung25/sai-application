package booking.agency.gui;

import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;

class BookingAgencyListLine {
	
	private AgencyRequest request;
	private AgencyReply reply;
	
	public BookingAgencyListLine(AgencyRequest request,  AgencyReply reply) {
		setRequest(request);
		setReply(reply);
	}	
	
	public AgencyRequest getRequest() {
		return request;
	}
	
	private void setRequest(AgencyRequest request) {
		this.request = request;
	}
	
	public AgencyReply getReply() {
		return reply;
	}
	
	public void setReply(AgencyReply reply) {
		this.reply = reply;
	}
	
	@Override
	public String toString() {
	   return request.toString() + "  --->  " + ((reply!=null)?reply.toString():"waiting...");
	}
	
}

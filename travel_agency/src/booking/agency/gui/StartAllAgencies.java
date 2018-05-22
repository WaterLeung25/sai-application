package booking.agency.gui;

import java.awt.EventQueue;


public class StartAllAgencies {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BookingAgencyFrame  frame = new BookingAgencyFrame ("Book Fast");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BookingAgencyFrame  frame = new BookingAgencyFrame ("Book Cheap");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BookingAgencyFrame  frame = new BookingAgencyFrame ("Book Good Service");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

}

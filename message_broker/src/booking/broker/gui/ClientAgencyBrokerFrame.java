package booking.broker.gui;

import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;
import booking.broker.gateways.DistanceGateway;
import booking.broker.gateways.agencies.AgencyBrokerGateway;
import booking.broker.gateways.ClientBrokerGateway;
import booking.client.model.Address;
import booking.client.model.ClientBookingReply;
import booking.client.model.ClientBookingRequest;
import org.springframework.expression.EvaluationException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ClientAgencyBrokerFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private DefaultListModel<BrokerListLine> listModel = new DefaultListModel<>();
    private JList<BrokerListLine> list;

    //gateways
    private ClientBrokerGateway clientBrokerGateway;
    private AgencyBrokerGateway agencyBrokerGateway;
    private DistanceGateway distanceGateway;

    //local storage
    private List<ClientBookingRequest> bookingRequestList = new ArrayList<>();
    private AgencyRequest agencyRequest;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ClientAgencyBrokerFrame frame = new ClientAgencyBrokerFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public ClientAgencyBrokerFrame() {
        setTitle("Loan Broker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
        gbl_contentPane.rowHeights = new int[]{233, 23, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 7;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        contentPane.add(scrollPane, gbc_scrollPane);

        list = new JList<>(listModel);
        scrollPane.setViewportView(list);

        clientBrokerGateway = new ClientBrokerGateway() {
            @Override
            public void onBookingRequestArrived(ClientBookingRequest request) throws EvaluationException {
                if (request != null){
                    bookingRequestList.add(request);
                    addBrokerListLine(request);
                    sendAgencyRequest(request);
                }
            }
        };

        agencyBrokerGateway = new AgencyBrokerGateway() {
            @Override
            public void onAgencyReplyArrived(AgencyRequest agencyRequest, AgencyReply reply) {
                if (reply != null){
                    ClientBookingRequest bookingRequest = null;
                    for (ClientBookingRequest b : bookingRequestList){
                        if (b.getTransferToAddress() == null){
                            if (b.getOriginAirport() == agencyRequest.getFromAirport() && b.getDestinationAirport() == agencyRequest.getToAirport() && agencyRequest.getTransferDistance() == 0.0)
                                bookingRequest = b;
                        }
                        else {
                            if (b.getOriginAirport() == agencyRequest.getFromAirport() && b.getDestinationAirport() == agencyRequest.getToAirport())
                                bookingRequest = b;
                        }
                    }
                    if (bookingRequest != null){
                        addBrokerListLine(bookingRequest, reply);
                        sendBookingReply(reply, bookingRequest);
                    }
                }
            }
        };

        clientBrokerGateway.receiveBookingRequest();
        agencyBrokerGateway.receiveAgencyReply();
    }

    private BrokerListLine getRequestReply(ClientBookingRequest request){
        for (int i = 0; i < listModel.getSize(); i++){
            BrokerListLine line = listModel.get(i);
            if (line.getBookingRequest() == request){
                return line;
            }
        }
        return null;
    }

    private void addBrokerListLine(ClientBookingRequest bookingRequest){
        listModel.addElement(new BrokerListLine(bookingRequest));
    }

    private void addBrokerListLine(ClientBookingRequest bookingRequest, AgencyReply agencyReply){
        BrokerListLine line = getRequestReply(bookingRequest);
        if (line != null && agencyReply != null){
            line.setAgencyReply(agencyReply);
            list.repaint();
        }
    }

    private void sendAgencyRequest(ClientBookingRequest bookingRequest){
        if (bookingRequest != null){
            double distance = 0.0;

            if (bookingRequest.getTransferToAddress() != null){
                distanceGateway = new DistanceGateway();
                String destinationAirport = bookingRequest.getDestinationAirport();
                Address transferAddress = bookingRequest.getTransferToAddress();
                //get distance from Distance gateway
                String result = distanceGateway.getTransferDistance(destinationAirport, transferAddress);
                JsonReader jsonReader = Json.createReader(new StringReader(result));
                JsonObject resultMatrix = jsonReader.readObject();
                jsonReader.close();
                resultMatrix.get("rows");
                JsonArray arr = resultMatrix.getJsonArray("rows");
                int disValue = arr.getJsonObject(0).getJsonArray("elements").getJsonObject(0).getJsonObject("distance").getInt("value");
                distance = disValue / 1000;


                //distance = 51.0; //default
            }
            if (distance <= 50.0){
                agencyRequest = new AgencyRequest(bookingRequest.getDestinationAirport(), bookingRequest.getOriginAirport(), distance);
                agencyBrokerGateway.sendAgencyRequest(agencyRequest);
            }
            else {
                JOptionPane.showMessageDialog(null, "The maximum transfer is longer than 50.0km");
            }

        }
    }

    private void sendBookingReply(AgencyReply agencyReply, ClientBookingRequest bookingRequest){
        if (agencyReply != null){
            ClientBookingReply bookingReply= new ClientBookingReply(agencyReply.getNameAgency(), agencyReply.getTotalPrice());
            clientBrokerGateway.sendBookingReply(bookingReply, bookingRequest);
        }
    }
}

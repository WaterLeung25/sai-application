/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package booking.agency.gui;


import booking.agency.gateway.BrokerAppGateway;
import booking.agency.model.AgencyReply;
import booking.agency.model.AgencyRequest;

import javax.swing.*;

/**
 *
 * @author mpesic
 */
public class BookingAgencyFrame extends javax.swing.JFrame {


    private DefaultListModel<BookingAgencyListLine> listModel = new DefaultListModel<>();
    private String agencyName;
    private BrokerAppGateway brokerAppGateway;

    /**
     * Creates new form TravelApprovalFrame
     *
     * @param agencyName
     */
    public BookingAgencyFrame(String agencyName) {
        initComponents();
        setTitle(agencyName);
        this.agencyName = agencyName.substring(5);

        //start up connection to the broker
        brokerAppGateway = new BrokerAppGateway(this.agencyName) {
            @Override
            public void onAgencyRequestArrived(AgencyRequest request) {
                BookingAgencyListLine listLine = new BookingAgencyListLine(request, null);
                if (listLine != null){
                    listModel.addElement(listLine);
                }
            }
        };

        //receive agencyRequest from broker
        brokerAppGateway.receiveAgencyRequest();
    }

    @SuppressWarnings("unchecked")
     private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jbSendAgencyReply = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtfTotalPrice = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWidths = new int[] {0, 4, 0, 4, 0, 4, 0};
        layout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0};
        getContentPane().setLayout(layout);

        jList1.setModel(listModel);
        jScrollPane2.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 2.0;
        getContentPane().add(jScrollPane2, gridBagConstraints);

        jbSendAgencyReply.setText("send price offer");
        jbSendAgencyReply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSendAgencyReplyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        getContentPane().add(jbSendAgencyReply, gridBagConstraints);

        jLabel1.setText("total price for this booking:");
        jPanel1.add(jLabel1);
        jLabel1.getAccessibleContext().setAccessibleName("total price:");

        jtfTotalPrice.setMinimumSize(new java.awt.Dimension(100, 100));
        jtfTotalPrice.setName("");
        jtfTotalPrice.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel1.add(jtfTotalPrice);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }

    private void jbSendAgencyReplyActionPerformed(java.awt.event.ActionEvent evt) {
        BookingAgencyListLine jListLine = jList1.getSelectedValue();
        double price = Double.parseDouble(this.jtfTotalPrice.getText());

        AgencyReply reply = new AgencyReply(this.agencyName, price);

        if (jListLine != null && reply != null) {
            jListLine.setReply(reply);
            jList1.repaint();

            AgencyRequest request = jListLine.getRequest();
            //send agencyReply to broker
            brokerAppGateway.sendAgencyReply(request, reply);
        }

    }


    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<BookingAgencyListLine> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbSendAgencyReply;
    private javax.swing.JTextField jtfTotalPrice;
}

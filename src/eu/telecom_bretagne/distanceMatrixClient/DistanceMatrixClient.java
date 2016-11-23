package eu.telecom_bretagne.distanceMatrixClient;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.xml.bind.JAXBException;

import eu.telecom_bretagne.distanceMatrixClient.DistanceMatrixFacade.ModeTransport;
import eu.telecom_bretagne.distanceMatrixClient.DistanceMatrixFacade.OutputType;

public class DistanceMatrixClient extends JFrame {
    //-----------------------------------------------------------------------------

    private static final long serialVersionUID = -388305758322170827L;

    private JPanel contentPane;
    private JTextField tfOrigins;
    private JTextField tfDestinations;
    private final ButtonGroup bgMode,
            bgOutput;
    private JLabel lblTopLevelStatusDisplay,
            lblElementLevelStatusDisplay,
            lblOriginAddressDisplay,
            lblDestinationAddressDisplay,
            lblDistanceDisplay,
            lblDurationDisplay;
    private JTextArea taSource;
    private JTextField tfURL;

    private DistanceMatrixFacade distanceMatrixFacade;
    //-----------------------------------------------------------------------------

    public static void main(String[] args) throws JAXBException, IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new DistanceMatrixClient();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //-----------------------------------------------------------------------------

    /**
     * Création de l'interface.
     */
    public DistanceMatrixClient() {
        setResizable(false);
        this.distanceMatrixFacade = new DistanceMatrixFacade();
        setTitle("Distance Matrix Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1056, 455);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblOrigins = new JLabel("Origin(s):");
        lblOrigins.setBounds(10, 11, 108, 14);
        contentPane.add(lblOrigins);

        JLabel lblDestinations = new JLabel("Destination(s):");
        lblDestinations.setBounds(10, 36, 108, 14);
        contentPane.add(lblDestinations);

        tfOrigins = new JTextField();
        tfOrigins.setText("Télécom Bretagne, Plouzané");
        tfOrigins.setBounds(128, 8, 331, 20);
        contentPane.add(tfOrigins);
        tfOrigins.setColumns(10);

        tfDestinations = new JTextField();
        tfDestinations.setText("Brest");
        tfDestinations.setBounds(128, 33, 331, 20);
        contentPane.add(tfDestinations);
        tfDestinations.setColumns(10);

        JButton btnInvocation = new JButton("Invocation Distance Matric API");
        btnInvocation.addActionListener(new DistanceMatrixInvocationActionListener());
        btnInvocation.setBounds(109, 139, 251, 23);
        contentPane.add(btnInvocation);

        JPanel panelResult = new JPanel();
        panelResult.setBorder(new TitledBorder(null, "Response", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelResult.setBounds(10, 173, 449, 198);
        contentPane.add(panelResult);
        panelResult.setLayout(null);

        JLabel lblTopLevelStatus = new JLabel("Top-level Status:");
        lblTopLevelStatus.setBounds(10, 27, 160, 14);
        panelResult.add(lblTopLevelStatus);

        lblTopLevelStatusDisplay = new JLabel("---");
        lblTopLevelStatusDisplay.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblTopLevelStatusDisplay.setBounds(180, 27, 259, 14);
        panelResult.add(lblTopLevelStatusDisplay);

        JLabel lblDistance = new JLabel("Distance:");
        lblDistance.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblDistance.setBounds(10, 148, 160, 14);
        panelResult.add(lblDistance);

        lblDistanceDisplay = new JLabel("---");
        lblDistanceDisplay.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblDistanceDisplay.setBounds(180, 148, 259, 14);
        panelResult.add(lblDistanceDisplay);

        JLabel lblDuration = new JLabel("Duration:");
        lblDuration.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblDuration.setBounds(10, 173, 160, 14);
        panelResult.add(lblDuration);

        lblDurationDisplay = new JLabel("---");
        lblDurationDisplay.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblDurationDisplay.setBounds(180, 173, 259, 14);
        panelResult.add(lblDurationDisplay);

        JLabel lblOriginAddress = new JLabel("Origin Address:");
        lblOriginAddress.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblOriginAddress.setBounds(10, 98, 160, 14);
        panelResult.add(lblOriginAddress);

        lblOriginAddressDisplay = new JLabel("---");
        lblOriginAddressDisplay.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblOriginAddressDisplay.setBounds(180, 98, 259, 14);
        panelResult.add(lblOriginAddressDisplay);

        JLabel lblDestinationAddress = new JLabel("Destination Address:");
        lblDestinationAddress.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblDestinationAddress.setBounds(10, 123, 160, 14);
        panelResult.add(lblDestinationAddress);

        lblDestinationAddressDisplay = new JLabel("---");
        lblDestinationAddressDisplay.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblDestinationAddressDisplay.setBounds(180, 123, 259, 14);
        panelResult.add(lblDestinationAddressDisplay);

        JLabel lblElementLevelStatus = new JLabel("Element-level Status:");
        lblElementLevelStatus.setBounds(10, 52, 160, 14);
        panelResult.add(lblElementLevelStatus);

        lblElementLevelStatusDisplay = new JLabel("---");
        lblElementLevelStatusDisplay.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblElementLevelStatusDisplay.setBounds(180, 52, 259, 14);
        panelResult.add(lblElementLevelStatusDisplay);

        bgMode = new ButtonGroup();

        JRadioButton rdbtnDriving = new JRadioButton("Driving");
        rdbtnDriving.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                distanceMatrixFacade.setModeTransport(ModeTransport.driving);
            }
        });
        rdbtnDriving.setSelected(true);
        bgMode.add(rdbtnDriving);
        rdbtnDriving.setBounds(10, 60, 106, 23);
        contentPane.add(rdbtnDriving);

        JRadioButton rdbtnWalking = new JRadioButton("Walking");
        rdbtnWalking.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                distanceMatrixFacade.setModeTransport(ModeTransport.walking);
            }
        });
        bgMode.add(rdbtnWalking);
        rdbtnWalking.setBounds(10, 86, 106, 23);
        contentPane.add(rdbtnWalking);

        JRadioButton rdbtnBicycling = new JRadioButton("Bicycling");
        rdbtnBicycling.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                distanceMatrixFacade.setModeTransport(ModeTransport.bicycling);
            }
        });
        bgMode.add(rdbtnBicycling);
        rdbtnBicycling.setBounds(10, 109, 106, 23);
        contentPane.add(rdbtnBicycling);

        bgOutput = new ButtonGroup();

        JRadioButton rdbtnXml = new JRadioButton("XML");
        rdbtnXml.setSelected(true);
        rdbtnXml.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                distanceMatrixFacade.setOutputType(OutputType.xml);
            }
        });
        bgOutput.add(rdbtnXml);
        rdbtnXml.setBounds(149, 60, 109, 23);
        contentPane.add(rdbtnXml);

        JRadioButton rdbtnJson = new JRadioButton("JSON");
        rdbtnJson.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                distanceMatrixFacade.setOutputType(OutputType.json);
            }
        });
        bgOutput.add(rdbtnJson);
        rdbtnJson.setBounds(149, 86, 109, 23);
        contentPane.add(rdbtnJson);

        taSource = new JTextArea();
        taSource.setEditable(false);
        taSource.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane jScrollPane = new JScrollPane(taSource);
        jScrollPane.setBounds(469, 6, 561, 365);
        contentPane.add(jScrollPane);

        tfURL = new JTextField();
        tfURL.setText("---");
        tfURL.setEditable(false);
        tfURL.setFont(new Font("Courier New", Font.PLAIN, 11));
        tfURL.setBounds(10, 382, 1020, 31);
        contentPane.add(tfURL);
        tfURL.setColumns(10);

        setLocationRelativeTo(null);
        setVisible(true);
    }
    //-----------------------------------------------------------------------------

    /**
     * Traitement de l'événement lors de l'appui du bouton.
     */
    private class DistanceMatrixInvocationActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // Récupération des valeurs saisies et mise à jour de la classe DistanceMatrixFacade 
            String origin = tfOrigins.getText().replace(" ", "+");
            distanceMatrixFacade.setOrigin(origin);
            String destination = tfDestinations.getText().replace(" ", "+");
            distanceMatrixFacade.setDestination(destination);

            // Réinitialisation de l'affichage
            lblTopLevelStatusDisplay.setForeground(Color.black);
            lblElementLevelStatusDisplay.setForeground(Color.black);
            lblTopLevelStatusDisplay.setText("---");
            lblElementLevelStatusDisplay.setText("---");
            lblOriginAddressDisplay.setText("---");
            lblDestinationAddressDisplay.setText("---");
            lblDistanceDisplay.setText("---");
            lblDurationDisplay.setText("---");
            taSource.setText("");
            tfURL.setText("---");

            try {
                // 0 : origin_addresses
                // 1 : destination_addresses
                // 2 : top level status
                // 3 : Element-level status
                // 4 : distance (text)
                // 5 : distance (value)
                // 6 : duration (text)
                // 7 : duration (value)
                // 8 : la source de la réponse
                // 9 : l'URL d'invocation du Web Service
                String[] response = distanceMatrixFacade.getResponse();

                //String topLevelStatus = response[2];
                if (response[2] != null && response[2].equals("OK")) {
                    lblTopLevelStatusDisplay.setForeground(new Color(38, 127, 0));
                    lblTopLevelStatusDisplay.setText(response[2]);
                    lblOriginAddressDisplay.setText(response[0].equals("") ? "???" : response[0]);
                    lblDestinationAddressDisplay.setText(response[1].equals("") ? "???" : response[1]);
                    if (response[3].equals("OK")) {
                        lblElementLevelStatusDisplay.setForeground(new Color(38, 127, 0));
                        lblElementLevelStatusDisplay.setText(response[3]);
                        lblDistanceDisplay.setText(response[5] + " (" + response[4] + ")");
                        lblDurationDisplay.setText(response[7] + " (" + response[6] + ")");
                    } else {
                        lblElementLevelStatusDisplay.setForeground(Color.red);
                        lblElementLevelStatusDisplay.setText(response[3]);
                    }
                } else {
                    lblTopLevelStatusDisplay.setForeground(Color.red);
                    lblTopLevelStatusDisplay.setText((response[2] == null ? "error!" : response[2]));
                }
                // Dans tous les cas, XML ou JSON, on affiche la source de la réponse et l'URL
                taSource.setText(response[8]);
                tfURL.setText(response[9]);
            } catch (IOException | JAXBException exception) {
                exception.printStackTrace();
            }
        }
    }
    //-----------------------------------------------------------------------------
}

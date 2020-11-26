package Terrain;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TerrainGUI extends JPanel implements ActionListener {

    private static JFrame frame = new JFrame("Terrain Mover");
    private static Toolkit tk;
    private static Dimension screenDimension, frameDimension;

    private Grid grid;

    private Vehicle vehicle;

    private DefaultListModel<String> databaseChoiceModel, intelligenceChoiceModel;
    private JList<String> databaseChoiceList, intelligenceChoiceList;

    private DrawPanel mainPanel;
    private JPanel southPanel;
    private JButton loadGridButton, loadManualButton, loadAutomatedButton;
    private JLabel stepsTakenLabel;

    public TerrainGUI() {
        super(new BorderLayout());

        Timer timer = new Timer(100, this);
        timer.start();

        setupPanelElements();
    }

    public static void main(String[] args) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new TerrainGUI());
        frame.pack();

        tk = Toolkit.getDefaultToolkit();
        screenDimension = tk.getScreenSize();
        frameDimension = frame.getSize();

        frame.setLocation((screenDimension.width - frameDimension.width) / 2, (screenDimension.height - frameDimension.height) / 2);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public void setupPanelElements() {
        databaseChoiceModel = new DefaultListModel<>();
        intelligenceChoiceModel = new DefaultListModel<>();

        databaseChoiceModel.addElement("5x5");
        databaseChoiceModel.addElement("7x4");
        databaseChoiceModel.addElement("3x3");
        databaseChoiceModel.addElement("10x10");
        databaseChoiceModel.addElement("20x30");
        databaseChoiceModel.addElement("40x50");

        intelligenceChoiceModel.addElement("0%");
        intelligenceChoiceModel.addElement("20%");
        intelligenceChoiceModel.addElement("40%");
        intelligenceChoiceModel.addElement("60%");
        intelligenceChoiceModel.addElement("80%");
        intelligenceChoiceModel.addElement("100%");

        databaseChoiceList = new JList<>(databaseChoiceModel);
        databaseChoiceList.setBackground(Color.lightGray);
        intelligenceChoiceList = new JList<>(intelligenceChoiceModel);
        intelligenceChoiceList.setBackground(Color.lightGray);

        mainPanel = new DrawPanel();
        southPanel = new JPanel();

        loadGridButton = new JButton("              Load Grid              ");
        loadGridButton.addActionListener(this);
        loadGridButton.setFocusable(false);

        loadManualButton = new JButton("   Load Manual Vehicle    ");
        loadManualButton.addActionListener(this);
        loadManualButton.setFocusable(false);

        loadAutomatedButton = new JButton("Load Automated Vehicle");
        loadAutomatedButton.addActionListener(this);
        loadAutomatedButton.setFocusable(false);

        stepsTakenLabel = new JLabel("             Steps Taken: 0");

        Box box = Box.createVerticalBox();
        box.add(loadGridButton);
        box.add(Box.createVerticalStrut(3));
        box.add(loadManualButton);
        box.add(Box.createVerticalStrut(3));
        box.add(loadAutomatedButton);
        box.add(Box.createVerticalStrut(3));
        box.add(stepsTakenLabel);
        southPanel.add(box);

        southPanel.add(databaseChoiceList);
        southPanel.add(intelligenceChoiceList);
        southPanel.setBackground(Color.lightGray);
        add(mainPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        boolean correctInput = false;

        if(source == loadManualButton) {
            if(grid != null) {
                String userChoice;
                int userNumberChoice = 0;

                while(!correctInput) {
                    userChoice = JOptionPane.showInputDialog("Please enter starting position between 1 and " + grid.getNumCols() + ".");

                    if(userChoice == null) {
                        return;
                    }

                    userNumberChoice = Integer.parseInt(userChoice) - 1;

                    if(userNumberChoice >= 0 && userNumberChoice < grid.getNumCols()) {
                        correctInput = true;
                    }
                }

                grid.setStoredMoves(0, true);

                vehicle = new Vehicle(grid);
                vehicle.setPosition(grid.getNumRows() - 1, userNumberChoice);

                mainPanel.addKeyListener(vehicle);
                mainPanel.setFocusable(true);
                mainPanel.requestFocus();

            } else {
                return;
            }

        } else if(source == loadGridButton) {
            String userChoice = databaseChoiceList.getSelectedValue();

            if(userChoice == null) {
                JOptionPane.showMessageDialog(mainPanel, "Please select a terrain to load.");
                return;

            } else {
                if(userChoice.equals("5x5")) {
                    frame.setSize(305, 405);
                }

                if(userChoice.equals("40x50")) {
                    frame.setSize(620, 905);
                }

                if(userChoice.equals("20x30")) {
                    frame.setSize(525, 910);
                }

                if(userChoice.equals("10x10")) {
                    frame.setSize(515, 660);
                }

                if(userChoice.equals("7x4")) {
                    frame.setSize(365, 360);
                }

                if(userChoice.equals("3x3")) {
                    frame.setSize(325, 310);
                }

                tk = Toolkit.getDefaultToolkit();
                screenDimension = tk.getScreenSize();
                frameDimension = frame.getSize();
                frame.setLocation((screenDimension.width - frameDimension.width) / 2, (screenDimension.height - frameDimension.height) / 2);
            }
            vehicle = null;
            grid = TerrainGenerator.loadTerrain(userChoice);

        } else if(source == loadAutomatedButton) {
            if(grid != null) {
                String userChoice = intelligenceChoiceList.getSelectedValue();

                if(userChoice == null) {
                    JOptionPane.showMessageDialog(mainPanel, "Please select a Intelligence level.");
                    return;
                }

                userChoice = userChoice.substring(0, userChoice.length() - 1);

                vehicle = new Vehicle(grid, Integer.parseInt(userChoice));
                grid.setStoredMoves(0, true);
            }
        }

        if(grid != null) {
            stepsTakenLabel.setText("         Steps Taken: " + grid.getStoredMoves());
        }

        if(mainPanel != null) {
            mainPanel.repaint();
        }
    }

    public class DrawPanel extends JPanel {

        public DrawPanel() {
            super();
            super.setBackground(Color.WHITE);
            super.setPreferredSize(new Dimension(500, 500));
        }

        public void vehicleDrawing(Graphics g, Vehicle vehicle) {
            if(vehicle != null) {
                vehicle.drawVehicle(g);
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if(grid != null) {
                grid.drawGrid(g);
            }

            if(vehicle != null && vehicle.vehicleCanDraw()) {
                vehicleDrawing(g, vehicle);
            }
        }
    }
}

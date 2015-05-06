/**
 * This skeleton is provided for the Software Laboratory 5 course. Its structure
 * should provide a general guideline for the students. Though we were trying to
 * create a good example application here, the code is probably not suitable for
 * a real life application.
 *
 * Written by
 * 	Gergely J. Horváth
 * 	Richárd Milanovits
 * Based on the previous version by
 * 	Ádám Kollár
 * Revised by
 * 	Roland Kamaras
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

// Application class
public class MySzorakApplication extends javax.swing.JFrame {

    // As suggested by the Swing model, we'll have a GUI + controllers class
    // (this one, that also includes the entry point) and a model.
    protected MySzorakModel model;

    // GUI components

    // Connection panel
    protected JPanel connectionPanel;
    protected JTextField userNameField;
    protected JTextField passwordField;
    protected JButton connectButton;
    protected JLabel connectionStateLabel;

    // Tabbed pane
    protected JTabbedPane tabbedPane;

    // Log tab
    protected JPanel logTab;
    protected JScrollPane logScrollPanel;
    protected JTextArea logTextArea;

    // Search tab
    protected JPanel searchTab;
    protected JPanel searchInputPanel;
    protected JTextField keywordField;
    protected JButton searchButton;
    protected JScrollPane searchScrollPanel;
    protected JTable searchTable;

    // Modify tab
    protected JPanel modifyTab;
    protected JTextField idField;
    protected JTextField nameField;
    protected JTextField addressField;
    protected JTextField phoneField;
    protected JTextField incomeField;
    protected JTextField movieField;
    protected JComboBox hobbyComboBox;
    protected JCheckBox providePlaceCheckBox;
    protected JTextField placeIDField;
    protected JButton submitButton;
    protected JButton submitAllButton;

    // Class constructor
    public MySzorakApplication() {
        model = new MySzorakModel(this);
    }

    // Entry point for the application
    public static void main(String[] args) {

        // Create a new instance of the Runnable class and show it
        SwingUtilities.invokeLater(new

                                           // Create an embedded Runnable class
                                           Runnable() {

                                               // Display GUI window
                                               public void run() {
                                                   MySzorakApplication instance = new MySzorakApplication();

                                                   // Populate the JFrame with a nice GUI
                                                   instance.createGUI();

                                                   // Make it reasonably big
                                                   instance.setSize(600, 500);

                                                   // Center the window and show it
                                                   instance.setLocationRelativeTo(null);
                                                   instance.setVisible(true);
                                               }

                                           }

        );
    }

    // Create the GUI
    protected void createGUI() {

        // Create all the GUI components

        // Connection panel
        connectionPanel = new JPanel();
        userNameField = new JTextField();
        passwordField = new JTextField();
        connectButton = new JButton();
        connectionStateLabel = new JLabel();

        // Tabbed pane
        tabbedPane = new JTabbedPane();

        // Log tab
        logTab = new JPanel();
        logScrollPanel = new JScrollPane();
        logTextArea = new JTextArea();

        // Search tab
        searchTab = new JPanel();
        searchInputPanel = new JPanel();
        keywordField = new JTextField();
        searchButton = new JButton();
        searchScrollPanel = new JScrollPane();
        searchTable = new JTable();

        // Set the CloseOperation to call dispose() on close
        // !TODO: dispose_on_close visszaírása
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Set the minimum window size
        setMinimumSize(new java.awt.Dimension(600, 500));
        setPreferredSize(new java.awt.Dimension(600, 500));

        // Placing GUI elements

        // This is the panel for the connection options
        connectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        connectionPanel.setLayout(new BoxLayout(connectionPanel, BoxLayout.LINE_AXIS));

        // Username field
        userNameField.setMaximumSize(new java.awt.Dimension(200, 27));
        userNameField.setMinimumSize(new java.awt.Dimension(100, 27));
        userNameField.setPreferredSize(new java.awt.Dimension(150, 27));

        userNameField.setText("awxuc6");

        // Add username field to the connection panel
        connectionPanel.add(userNameField);

        // Password field
        passwordField.setMaximumSize(new java.awt.Dimension(200, 27));
        passwordField.setMinimumSize(new java.awt.Dimension(100, 27));
        passwordField.setPreferredSize(new java.awt.Dimension(150, 27));

        passwordField.setText("zizi");

        // Add password field to the connection panel
        connectionPanel.add(passwordField);

        // Connect button
        connectButton.setMnemonic('c');
        connectButton.setText("Connect");

        // Connect button action listener
        connectButton.addActionListener(new java.awt.event.ActionListener() {

            // Action performed method
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        // Add connect button to the connection panel
        connectionPanel.add(connectButton);

        // Little label to show the connection status
        connectionStateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        connectionStateLabel.setText("<html>Connection: <font color=\"red\">disconnected</font>");
        connectionStateLabel.setMinimumSize(new java.awt.Dimension(200, 17));
        connectionStateLabel.setPreferredSize(new java.awt.Dimension(200, 17));

        // Add connection status label to the connection panel
        connectionPanel.add(connectionStateLabel);

        // Finally put the connection panel on the window
        getContentPane().add(connectionPanel, java.awt.BorderLayout.PAGE_START);

        // Now we create a tabbed pane for the tabs
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // First a nice tab for logging
        logTab.setLayout(new java.awt.BorderLayout());

        // Log scroll panel
        logScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Log text area
        logTextArea.setEditable(false);
        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        logTextArea.setTabSize(4);

        // Locate the tab
        logScrollPanel.setViewportView(logTextArea);
        logTab.add(logScrollPanel, java.awt.BorderLayout.CENTER);
        tabbedPane.addTab("Log", logTab);

        // Then a search tab for searching
        searchTab.setLayout(new BoxLayout(searchTab, BoxLayout.PAGE_AXIS));

        // Search input panel
        searchInputPanel.setLayout(new BoxLayout(searchInputPanel, BoxLayout.LINE_AXIS));

        // Search keyword field
        searchInputPanel.add(keywordField);

        // Search button
        searchButton.setMnemonic('k');
        searchButton.setText("Search");

        // Search button action listener
        searchButton.addActionListener(new java.awt.event.ActionListener() {

            // Action performed method
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        // Add search button to the input panel
        searchInputPanel.add(searchButton);

        // Add search input panel to the search tab
        searchTab.add(searchInputPanel);

        // Search table
        searchTable.setModel(new DefaultTableModel(
                                     new Object [][] {
                                             {null, null, null},
                                             {null, null, null},
                                             {null, null, null},
                                             {null, null, null}
                                     },
                                     new String [] {
                                             "Name", "Address", "Phone"
                                     }
                             )
                             {
                                 // Is cell editable
                                 @Override
                                 public boolean isCellEditable(int rowIndex, int colIndex) {
                                     return false;
                                 }
                             }
        );

        // Locate the tab
        searchScrollPanel.setViewportView(searchTable);
        searchTab.add(searchScrollPanel);
        tabbedPane.addTab("Search", searchTab);

        // Modify tab
        modifyTab = new JPanel();
        modifyTab.setLayout(new FlowLayout());

        // Select in Modify tab
        JPanel selectPanel = new JPanel();
        selectPanel.setLayout(new FlowLayout());
        providePlaceCheckBox = new JCheckBox("I provide a place ID to visit");
        providePlaceCheckBox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                providePlaceCheckBoxActionPerformed(e);
            }
        });
        selectPanel.add(providePlaceCheckBox);
        selectPanel.setPreferredSize(new Dimension(550, 25));
        modifyTab.add(selectPanel);

        // ID panel in Modify tab
        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.LINE_AXIS));
        idPanel.add(new JLabel("ID:* "));
        idField = new JTextField();
        idField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                idFieldChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                idFieldChanged(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                idFieldChanged(e);
            }
        });
        idPanel.add(idField);
        idPanel.setPreferredSize(new Dimension(550, 30));
        modifyTab.add(idPanel);

        // Name panel in Modify tab
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.LINE_AXIS));
        namePanel.add(new JLabel("Name:* "));
        nameField = new JTextField();
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                nameFieldChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                nameFieldChanged(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                nameFieldChanged(e);
            }
        });
        namePanel.add(nameField);
        namePanel.setPreferredSize(new Dimension(550, 25));
        modifyTab.add(namePanel);

        // Address panel in Modify tab
        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new BoxLayout(addressPanel, BoxLayout.LINE_AXIS));
        addressPanel.add(new JLabel("Address: "));
        addressField = new JTextField();
        addressPanel.add(addressField);
        addressPanel.setPreferredSize(new Dimension(550, 25));
        modifyTab.add(addressPanel);

        // Phone panel in Modify tab
        JPanel phonePanel = new JPanel();
        phonePanel.setLayout(new BoxLayout(phonePanel, BoxLayout.LINE_AXIS));
        phonePanel.add(new JLabel("Phone: "));
        phoneField = new JTextField();
        phonePanel.add(phoneField);
        phonePanel.setPreferredSize(new Dimension(550, 20));
        modifyTab.add(phonePanel);

        // Phone format in Modify tab
        JPanel formatPanel = new JPanel();
        formatPanel.add(new JLabel("Required format: +3630-1234567"));
        formatPanel.setPreferredSize(new Dimension(550, 25));
        modifyTab.add(formatPanel);

        // Income panel in Modify tab
        JPanel incomePanel = new JPanel();
        incomePanel.setLayout(new BoxLayout(incomePanel, BoxLayout.LINE_AXIS));
        incomePanel.add(new JLabel("Income: "));
        incomeField = new JTextField();
        incomePanel.add(incomeField);
        incomePanel.setPreferredSize(new Dimension(550, 25));
        modifyTab.add(incomePanel);

        // Favourite movie panel in Modify tab
        JPanel moviePanel = new JPanel();
        moviePanel.setLayout(new BoxLayout(moviePanel, BoxLayout.LINE_AXIS));
        moviePanel.add(new JLabel("Favourite movie: "));
        movieField = new JTextField();
        moviePanel.add(movieField);
        moviePanel.setPreferredSize(new Dimension(550, 25));
        modifyTab.add(moviePanel);

        // Hobby panel in Modify tab
        JPanel hobbyPanel = new JPanel();
        hobbyPanel.setLayout(new BoxLayout(hobbyPanel, BoxLayout.LINE_AXIS));
        hobbyPanel.add(new JLabel("Hobby: "));
        hobbyComboBox = new JComboBox();
        hobbyComboBox.addItem("Choose a hobby!");
        hobbyComboBox.addItem("mozi");
        hobbyComboBox.addItem("sport");
        hobbyComboBox.addItem("varrás");
        hobbyPanel.add(hobbyComboBox);
        hobbyPanel.setPreferredSize(new Dimension(550, 25));
        modifyTab.add(hobbyPanel);

        // Submit button in Modify tab
        JPanel submitPanel = new JPanel();
        submitPanel.setLayout(new FlowLayout());
        submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 30));
        // the submit button should only be enabled when all mandatory parameters are provided
        submitButton.setEnabled(false);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitButtonActionPerformed(e);
            }
        });
        submitPanel.add(submitButton);
        submitPanel.setPreferredSize(new Dimension(550, 30));
        modifyTab.add(submitPanel);

        // Place in Modify tab
        JPanel placePanel = new JPanel();
        placePanel.setLayout(new BoxLayout(placePanel, BoxLayout.LINE_AXIS));
        placePanel.add(new JLabel("Place ID:* "));
        placeIDField = new JTextField();
        placeIDField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                placeIDFieldChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        // this field should only be enabled when providePlaceCheckBox is selected
        placeIDField.setEnabled(false);
        placePanel.add(placeIDField);
        placePanel.setPreferredSize(new Dimension(550, 25));
        modifyTab.add(placePanel);

        // Submit All button in Modify tab
        JPanel submitAllPanel = new JPanel();
        submitPanel.setLayout(new FlowLayout());
        submitAllButton = new JButton("Submit all");
        submitAllButton.setPreferredSize(new Dimension(100, 30));
        // the submit all button should only be enabled when providePlaceIDCheckBox is selected, and all mandatory parameters are provided
        submitAllButton.setEnabled(false);
        submitAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitAllButtonActionPerformed(e);
            }
        });
        submitAllPanel.add(submitAllButton);
        submitAllPanel.setPreferredSize(new Dimension(550, 30));
        modifyTab.add(submitAllPanel);

        tabbedPane.addTab("Modify", modifyTab);


        // Finally add the tabbed pane to the window and pack the layout
        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);
        pack();
        // this pane will be enabled after connecting to the DBMS
        tabbedPane.setEnabled(false);

        // Here you could set the look and feel, etc.
    }

    // this is what happens when submit all button gets clicked
    private void submitAllButtonActionPerformed(ActionEvent e) {
        try {
            model.visitPlace(placeIDField.getText(), idField.getText());
            JOptionPane.showMessageDialog(null, "Your changes have been saved.");
        }
        catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(null, "Place ID does not exist.\n" +
                                                "Please submit person again.");
            log(ex.toString());
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "An error occured.\n" +
                                                "More information can be found on the Log tab.\n" +
                                                "Make sure to select the checkbox before submit is hit.");
            log(ex.toString());
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Place ID should be an integer");
        }
    }

    // this is what happens when submit button gets clicked
    private void submitButtonActionPerformed(ActionEvent e) {
        //validating phone number, if provided
        if (phoneField.getText().length() != 0) {
            try {
                model.validatePhone(phoneField.getText());
            }
            catch (PhoneNumberNotValidException ex) {
                JOptionPane.showMessageDialog(null, "The format of the phone number is not correct");
                return;
            }
        }
        // validting income, if provided
        if (incomeField.getText().length() != 0) {
            try {
                model.validateIncome(incomeField.getText());
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Income should be an integer");
                return;
            }
            catch (IncomeValueNotValidException ex) {
                JOptionPane.showMessageDialog(null, "Income should be between " + model.getMinIncome() + " and " + model.getMaxIncome());
                return;
            }
        }
        // getting hobby
        String hobby = "";
        if (hobbyComboBox.getSelectedIndex() != 0) {
            hobby = (String)hobbyComboBox.getSelectedItem();
        }

        Map<String, String> paramteres = new HashMap<String, String>();
        paramteres.put("ID", idField.getText());
        paramteres.put("Name", nameField.getText());
        paramteres.put("Address", addressField.getText());
        paramteres.put("Phone", phoneField.getText());
        paramteres.put("Income", incomeField.getText());
        paramteres.put("Hobby", hobby);
        paramteres.put("FavMovie", movieField.getText());

        try {
            boolean autocommit = !providePlaceCheckBox.isSelected();
            System.out.print(autocommit);
            MySzorakModel.ModifyResult modifyResult = model.modifyVisitor(paramteres, autocommit);
            // place ID is not required
            if (autocommit) {
                if (modifyResult == MySzorakModel.ModifyResult.InsertOccured) {
                    JOptionPane.showMessageDialog(null, "A new person has been inserted.");
                }
                else {
                    JOptionPane.showMessageDialog(null, "An existing person has been modified");
                }
            }
            // place ID is not required
            else {
                if (modifyResult == MySzorakModel.ModifyResult.InsertOccured) {
                    JOptionPane.showMessageDialog(null, "A new person will be inserted after providing a valid place ID");
                }
                else {
                    JOptionPane.showMessageDialog(null, "An existing person will be modified after providing a valid place ID");
                }
            }
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "An error occured.\nMore information can be found on the Log tab.");
            log(ex.toString());
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Name ID should be an integer");
        }
    }

    // place id action listener
    private void placeIDFieldChanged(DocumentEvent e) {
        // the submitAllButton should only be enabled when all mandatory parameters are provided
        if (placeIDField.getText().length() != 0 && nameField.getText().length() != 0 && idField.getText().length() != 0) {
            submitAllButton.setEnabled(true);
        }
        else {
            submitAllButton.setEnabled(false);
        }
    }

    // check box action listener
    private void providePlaceCheckBoxActionPerformed(ActionEvent e) {
        // if this checkbox is selected a place ID is required, so the placeIDField textfield will be enabled
        if (providePlaceCheckBox.isSelected()) {
            placeIDField.setEnabled(true);
        } else {
            placeIDField.setEnabled(false);
        }
    }

    // the submit button should only be enabled when all mandatory parameters are provided
    private void idFieldChanged(DocumentEvent e) {
        if (idField.getText().length() != 0 && nameField.getText().length() != 0) {
            submitButton.setEnabled(true);
            // the submitAllButton should only be enabled when providePlaceCheckBox is enabled, and all mandatory parameter are provided
            if (providePlaceCheckBox.isSelected() && placeIDField.getText().length() != 0) {
                submitAllButton.setEnabled(true);
            }
        }
        else {
            submitButton.setEnabled(false);
            submitAllButton.setEnabled(false);
        }
    }

    // name field action listener
    private void nameFieldChanged(DocumentEvent e) {
        // the submit button should only be enabled when name is provided
        if (nameField.getText().length() != 0 && idField.getText().length() != 0) {
            submitButton.setEnabled(true);
            // the submitAllButton should only be enabled when providePlaceCheckBox is enabled, and all mandatory parameter are provided
            if (providePlaceCheckBox.isSelected() && placeIDField.getText().length() != 0) {
                submitAllButton.setEnabled(true);
            }
        }
        else {
            submitButton.setEnabled(false);
            submitAllButton.setEnabled(false);
        }
    }

    /**
     * This is called whenever the connect button is pressed.
     * @param event Contains details about the AWT event.
     */
    protected void connectButtonActionPerformed(java.awt.event.ActionEvent event) {

        try {

            // The model's connect method will do everything for us, just call it
            model.connect(userNameField.getText(), passwordField.getText());
            connectionStateLabel.setText("<html>Connection: <font color=\"green\">created</font>");

            // Test the connection
            if (model.testConnection()) {
                log("Connection seems to be working.");
            } else {
                log("It's a TRAP!");
            }
            tabbedPane.setEnabled(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "An error occured while trying to connect to the database. \n More information can be found on the Log tab.");
            log(e.toString());

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "An error occured while trying to connect to the database. \n More information can be found on the Log tab.");
            log(e.toString());

        }

    }

    /**
     * This is called whenever the search button is pressed.
     * @param event Contains details about the AWT event.
     */
    protected void searchButtonActionPerformed(java.awt.event.ActionEvent event) {
        String placeName = keywordField.getText();

        // Let's grab the TableModel
        DefaultTableModel tableModel = (DefaultTableModel) searchTable.getModel();
        // Clear all the rows
        tableModel.setRowCount(0);
        try {
            ResultSet resultSet = null;
            if (keywordField.getText().length() == 0) {
                resultSet = model.searchPlaces();
            }
            else {
                resultSet = model.searchPlaces(keywordField.getText());
            }
            while (resultSet.next()) {
                tableModel.addRow(new Object[] {resultSet.getString(1), resultSet.getString(2), resultSet.getString(3)});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "An error occured. \n More information can be found on the Log tab.");
            log(e.toString());
        }
    }

    /**
     * Appends the message (with a line break added) to the log.
     * @param message The message to be logged.
     */
    protected void log(String message) {
        logTextArea.append(message + "\n");
    }

    // Dispose method
    @Override
    public void dispose() {
        super.dispose();
    }

}

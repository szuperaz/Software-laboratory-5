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

import oracle.jdbc.proxy.annotation.Pre;

import java.sql.*;
import java.util.Map;

// Model class
public class MySzorakModel {

    // As suggested by the Swing model, we'll have a model (this one) and a
    // GUI + controllers class.
    protected MySzorakApplication gui;

    // DataBase driver and URL
    protected static final String driverName = "oracle.jdbc.driver.OracleDriver";
    protected static final String url = "jdbc:oracle:thin:@rapid.eik.bme.hu:1521:szglab";

    // Connection object
    protected Connection connection = null;

    //special characters to be escaped
    private String[] specialCharacters;

    //min and max values for income
    protected final int minIncome = 15000;
    protected final int maxIncome = 200000000;

    // Enum structure for Exercise #2
    protected enum ModifyResult {
        InsertOccured,
        UpdateOccured
    }

    // Class constructor
    public MySzorakModel(MySzorakApplication application) {
        gui = application;
        specialCharacters = new String[] {"\\", "%", "_", "{", "}"};
    }

    /**
     * Tries to connect to the database.
     * @param userName user who has access to the database
     * @param password user's password
     */
    public void connect(String userName, String password) throws SQLException, ClassNotFoundException {

        // If connection status is disconnected
        if (connection == null || !connection.isValid(30)) {

            if (connection == null) {

                // Load the specified database driver
                Class.forName(driverName);

                if (java.lang.System.getProperty("java.vendor").equals("Microsoft Corp.")) {
                    DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                }

            } else {

                connection.close();

            }

            // Create new connection
            connection = DriverManager.getConnection(url, userName, password);
            DatabaseMetaData dbmd = connection.getMetaData();
            gui.log("Connected to: '" + url + "'");
            gui.log(String.format("DBMS: %s, version: %s", dbmd.getDatabaseProductName(), dbmd.getDatabaseProductVersion()));

        }

    }

    /**
     * Tests the database connection by submitting a query.
     * @return true on success, false on fail
     */
    public boolean testConnection() throws SQLException {

        // If user input has to be processed, use PreparedStatement instead
        Statement stmt = connection.createStatement();
        ResultSet rset = stmt.executeQuery("SELECT count(*) FROM oktatas.igazolvanyok");

        while (rset.next()) {
            gui.log(String.format("Total number of rows in 'Igazolvanyok' table in 'Oktatas' schema: %s", rset.getString(1)));
        }

        stmt.close();

        return true;
    }

    /**
     * Method for Exercise #1
     * @return result of the query
     */
    public ResultSet searchPlaces(String placeName) throws SQLException {
            placeName = escape(placeName);
            String selectStatement = "select name, address, phone from places where name like ? ESCAPE'\\'";
            PreparedStatement statement = connection.prepareStatement(selectStatement);
            statement.setString(1, "%" + placeName + "%");
            ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }

    public ResultSet searchPlaces () throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select name, address, phone from places");
        return resultSet;
    }

    /**
     * Method for Exercise #2
     * @return type of action has been performed
     */
    public ModifyResult modifyVisitor(Map data, Boolean autocommit) throws SQLException, NumberFormatException {
        ModifyResult returnValue = ModifyResult.InsertOccured;
        connection.setAutoCommit(autocommit);
        String idString = (String)data.get("ID");
        int id = Integer.parseInt(idString);
        String insertStatement = "insert into persons values (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insertStatement);
        statement.setInt(1, id);
        statement.setString(2, (String) data.get("Name"));
        statement.setString(3, (String)data.get("Address"));
        statement.setString(4, (String) data.get("Phone"));
        statement.setString(5, (String)data.get("Income"));
        statement.setString(6, (String)data.get("Hobby"));
        statement.setString(7, (String)data.get("FavMovie"));
        try {
            statement.execute();
        }
        catch (SQLIntegrityConstraintViolationException ex) {
            returnValue = ModifyResult.UpdateOccured;
            String updateStatement = "update persons set name = ?, address = ?, phone = ?, income = ?, hobby = ?, favourite_movie = ? where person_id = ?";
            statement = connection.prepareStatement(updateStatement);
            statement.setInt(7, id);
            statement.setString(1, (String) data.get("Name"));
            statement.setString(2, (String)data.get("Address"));
            statement.setString(3, (String) data.get("Phone"));
            statement.setString(4, (String)data.get("Income"));
            statement.setString(5, (String)data.get("Hobby"));
            statement.setString(6, (String)data.get("FavMovie"));
            statement.execute();
        }

        return returnValue;
    }

    public void visitPlace (String placeIDString, String personIDString) throws SQLException, NumberFormatException {
        int placeID = Integer.parseInt(placeIDString);
        int personID = Integer.parseInt(personIDString);
        String insertStatement = "insert into visits (place_id, person_id) values (?, ?)";
        PreparedStatement statement = connection.prepareStatement(insertStatement);
        statement.setInt(1, placeID);
        statement.setInt(2, personID);
        try {
            statement.execute();
        }
        catch (SQLException ex) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw ex;
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Method for Exercise #4
     * @return true on success, false on fail
     */
    public boolean commit() {
        return false;
    }

    /**
     * Method for Exercise #5
     * @return result of the query
     */
    public ResultSet getStatistics() {
        return null;
    }

    public String escape (String string) {
        String newString = string;
        for (String specialChar : specialCharacters) {
            newString = newString.replaceAll("\\" + specialChar, "\\\\" + specialChar);
        }

        return newString;
    }
    public void validatePhone (String phone) throws PhoneNumberNotValidException {
        // regular expression for Hungarian phone numbers
        if (!phone.matches("\\+36(([0-9]{2}-[0-9]{7})|([1-9][0-9]-[0-9]{6})|([0-9]-[0-9]{7}))")) {
            throw new PhoneNumberNotValidException();
        }
    }

    public void validateIncome (String income) throws NumberFormatException, IncomeValueNotValidException {
        if (!income.matches("[0-9]*")) {
            throw new NumberFormatException();
        }
        int value = Integer.parseInt(income);
        if (value < minIncome || value > maxIncome) {
            throw new IncomeValueNotValidException();
        }
    }

    public int getMinIncome() {
        return minIncome;
    }

    public int getMaxIncome() {
        return maxIncome;
    }

    // Finalize method
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

}

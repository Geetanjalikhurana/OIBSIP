package com.trainreserve.db;

import com.trainreserve.model.Reservation;
import com.trainreserve.model.User;

import java.sql.*;

/**
 * Singleton database manager.
 * Handles connection to SQLite, schema initialization, seed data, and all CRUD operations.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:train_reserve.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found.", e);
        }
        connection = DriverManager.getConnection(DB_URL);
        Statement st = connection.createStatement();
        st.execute("PRAGMA foreign_keys = ON");
        st.close();
        initSchema();
        seedData();
    }

    public static synchronized DatabaseManager getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() { return connection; }

    private void initSchema() throws SQLException {
        String createUsers =
            "CREATE TABLE IF NOT EXISTS users (" +
            "  id       INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  username TEXT    UNIQUE NOT NULL," +
            "  password TEXT    NOT NULL" +
            ")";

        String createTrains =
            "CREATE TABLE IF NOT EXISTS trains (" +
            "  train_number TEXT PRIMARY KEY," +
            "  train_name   TEXT NOT NULL" +
            ")";

        String createReservations =
            "CREATE TABLE IF NOT EXISTS reservations (" +
            "  pnr              TEXT PRIMARY KEY," +
            "  passenger_name   TEXT NOT NULL," +
            "  train_number     TEXT NOT NULL," +
            "  train_name       TEXT NOT NULL," +
            "  class_type       TEXT NOT NULL," +
            "  date_of_journey  TEXT NOT NULL," +
            "  source_station   TEXT NOT NULL," +
            "  dest_station     TEXT NOT NULL," +
            "  booked_at        TEXT NOT NULL" +
            ")";

        Statement st = connection.createStatement();
        st.execute(createUsers);
        st.execute(createTrains);
        st.execute(createReservations);
        st.close();
    }

    private void seedData() throws SQLException {
        // Default admin user
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users");
        if (rs.next() && rs.getInt(1) == 0) {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO users (username, password) VALUES (?, ?)");
            ps.setString(1, "admin");
            ps.setString(2, "admin123");
            ps.executeUpdate();
            ps.close();
        }
        rs.close();

        // Sample trains - EXPANDED LIST including 22479
        rs = st.executeQuery("SELECT COUNT(*) FROM trains");
        if (rs.next() && rs.getInt(1) == 0) {
            String[][] trains = {
                {"12301", "Howrah Rajdhani Express"},
                {"12302", "New Delhi Rajdhani Express"},
                {"12951", "Mumbai Rajdhani Express"},
                {"11001", "Deccan Express"},
                {"22691", "Bengaluru Rajdhani Express"},
                {"12259", "Sealdah Duronto Express"},
                {"12009", "Mumbai Shatabdi Express"},
                {"14311", "Ala Hazrat Express"},
                {"15001", "Muzaffarpur Express"},
                {"22119", "Mumbai CSMT AC Express"},
                {"22479", "Bikaner Coimbatore SF Express"}, // User specifically asked for this
                {"12627", "Karnataka Express"},
                {"12628", "Karnataka Express"},
                {"12431", "Rajdhani Express (Trivandrum)"},
                {"12432", "Rajdhani Express (Trivandrum)"},
                {"12953", "August Kranti Rajdhani"},
                {"12954", "August Kranti Rajdhani"},
                {"12001", "Bhopal Shatabdi"},
                {"12002", "Bhopal Shatabdi"},
                {"12003", "Lucknow Swarna Shatabdi"},
                {"12004", "Lucknow Swarna Shatabdi"},
                {"12213", "Yesvantpur Duronto Express"},
                {"12214", "Yesvantpur Duronto Express"},
                {"12859", "Gitanjali Express"},
                {"12860", "Gitanjali Express"},
                {"12615", "Grand Trunk Express"},
                {"12616", "Grand Trunk Express"},
                {"12839", "Howrah Chennai Mail"},
                {"12840", "Howrah Chennai Mail"},
                {"12723", "Telangana Express"},
                {"12724", "Telangana Express"}
            };
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO trains (train_number, train_name) VALUES (?, ?)");
            for (String[] t : trains) {
                ps.setString(1, t[0]);
                ps.setString(2, t[1]);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        }
        rs.close();
        st.close();
    }

    public User authenticate(String username, String password) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
            "SELECT id, username FROM users WHERE username = ? AND password = ?");
        ps.setString(1, username.trim());
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        User user = null;
        if (rs.next()) {
            user = new User(rs.getInt("id"), rs.getString("username"));
        }
        rs.close();
        ps.close();
        return user;
    }

    public String getTrainName(String trainNumber) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
            "SELECT train_name FROM trains WHERE train_number = ?");
        ps.setString(1, trainNumber.trim());
        ResultSet rs = ps.executeQuery();
        String name = null;
        if (rs.next()) name = rs.getString("train_name");
        rs.close();
        ps.close();
        return name;
    }

    public void insertReservation(Reservation r) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
            "INSERT INTO reservations " +
            "(pnr, passenger_name, train_number, train_name, class_type," +
            " date_of_journey, source_station, dest_station, booked_at)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, r.getPnr());
        ps.setString(2, r.getPassengerName());
        ps.setString(3, r.getTrainNumber());
        ps.setString(4, r.getTrainName());
        ps.setString(5, r.getClassType());
        ps.setString(6, r.getDateOfJourney());
        ps.setString(7, r.getSourceStation());
        ps.setString(8, r.getDestStation());
        ps.setString(9, r.getBookedAt());
        ps.executeUpdate();
        ps.close();
    }

    public Reservation getReservationByPNR(String pnr) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
            "SELECT * FROM reservations WHERE pnr = ?");
        ps.setString(1, pnr.trim().toUpperCase());
        ResultSet rs = ps.executeQuery();
        Reservation r = null;
        if (rs.next()) {
            r = new Reservation(
                rs.getString("pnr"),
                rs.getString("passenger_name"),
                rs.getString("train_number"),
                rs.getString("train_name"),
                rs.getString("class_type"),
                rs.getString("date_of_journey"),
                rs.getString("source_station"),
                rs.getString("dest_station"),
                rs.getString("booked_at")
            );
        }
        rs.close();
        ps.close();
        return r;
    }

    public boolean cancelReservation(String pnr) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
            "DELETE FROM reservations WHERE pnr = ?");
        ps.setString(1, pnr.trim().toUpperCase());
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    public boolean pnrExists(String pnr) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
            "SELECT 1 FROM reservations WHERE pnr = ?");
        ps.setString(1, pnr);
        ResultSet rs = ps.executeQuery();
        boolean exists = rs.next();
        rs.close();
        ps.close();
        return exists;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {}
    }
}

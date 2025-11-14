package dao;

import model.Buyer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuyerDAO {
    private final Connection conn;
    private static BuyerDAO instance;

    private static final String INSERT_SQL =
            "INSERT INTO buyer(username, email, password, registerdAt, balance) VALUES(?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL =
            "SELECT idbuyer, username, email, password, registerdAt, balance FROM buyer";
    private static final String SELECT_BY_EMAIL_PASSWORD_SQL =
            "SELECT * FROM buyer WHERE email=? AND password=?";
    private static final String DELETE_SQL =
            "DELETE FROM buyer WHERE idbuyer=?";
    private static final String UPDATE_SQL =
            "UPDATE buyer SET username = ?, email = ?, password = ?, balance = ? WHERE idbuyer = ?";

    // primim conexiunea singleton în constructor
    public BuyerDAO(Connection conn) {
        this.conn = conn;
    }

    public BuyerDAO() throws SQLException {
        this(DatabaseConnectionManager.getInstance().getConnection());
    }


    public Buyer addBuyer(Buyer b) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getUsername());
            ps.setString(2, b.getEmail());
            ps.setString(3, b.getPassword());
            ps.setTimestamp(4, Timestamp.valueOf(b.getRegisteredAt()));
            ps.setDouble(5, b.getBalance());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    b.setId(keys.getLong(1));
                }
            }
        }
        return b;
    }

    public List<Buyer> getAllBuyers() throws SQLException {
        List<Buyer> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                list.add(new Buyer.Builder()
                        .id(rs.getLong("idbuyer"))
                        .username(rs.getString("username"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .registeredAt(rs.getTimestamp("registerdAt").toLocalDateTime())
                        .balance(rs.getDouble("balance"))
                        .build());
            }
        }
        return list;
    }

    public Buyer findByEmailAndPassword(String email, String password) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_EMAIL_PASSWORD_SQL)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Buyer.Builder()
                            .id(rs.getLong("idbuyer"))
                            .username(rs.getString("username"))
                            .email(rs.getString("email"))
                            .password(rs.getString("password"))
                            .registeredAt(rs.getTimestamp("registerdAt").toLocalDateTime())
                            .balance(rs.getDouble("balance"))
                            .build();
                }else {
                    return null;
                }
            }
        }
    }

    public static synchronized BuyerDAO getInstance() {
        if (instance == null) {
            try {
                instance = new BuyerDAO();
            } catch (SQLException e) {
                // Într-un proiect real ai arunca o excepție custom sau ai face logging.
                throw new RuntimeException("Nu am putut inițializa BuyerDAO: " + e.getMessage(), e);
            }
        }
        return instance;
    }

    public void delete(Long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void update(Buyer b) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, b.getUsername());
            ps.setString(2, b.getEmail());
            ps.setString(3, b.getPassword());
            ps.setDouble(4, b.getBalance());
            ps.setLong(5, b.getId());
            ps.executeUpdate();
        }
    }
}

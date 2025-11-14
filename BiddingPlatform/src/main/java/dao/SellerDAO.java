package dao;

import model.Buyer;
import model.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerDAO {
    private final Connection conn;
    private static SellerDAO instance;

    private static final String INSERT_SQL =
            "INSERT INTO seller(username, email, password, registerdAt, profit) VALUES(?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL =
            "SELECT idseller, username, email, password, registerdAt, profit FROM seller";
    private static final String SELECT_BY_EMAIL_PASSWORD_SQL =
            "SELECT * FROM seller WHERE email=? AND password=?";
    private static final String UPDATE_SQL =
            "UPDATE seller SET username = ?, email = ?, password = ?, profit = ? WHERE idseller = ?";

    public SellerDAO(Connection conn) {
        this.conn = conn;
    }

    public SellerDAO() throws SQLException {
        this(DatabaseConnectionManager.getInstance().getConnection());
    }

    public Seller addSeller(Seller s) throws SQLException {
        try(PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getUsername());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPassword());
            ps.setTimestamp(4, Timestamp.valueOf(s.getRegisteredAt()));
            ps.setDouble(5, s.getProfit());
            ps.executeUpdate();

            try(ResultSet keys = ps.getGeneratedKeys()) {
                if(keys.next()) {
                    s.setId(keys.getLong(1));
                }
            }
        }
        return s;
    }

    public List<Seller> getAllSellers() throws SQLException {
        List<Seller> list = new ArrayList<>();
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            while(rs.next()) {
                list.add(new Seller.Builder()
                        .id(rs.getLong("idseller"))
                        .username(rs.getString("username"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .registeredAt(rs.getTimestamp("registerdAt").toLocalDateTime())
                        .profit(rs.getDouble("profit"))
                        .build());
            }
        }
        return list;
    }

    public Seller findByEmailAndPassword(String email, String password) throws SQLException {
        try(PreparedStatement ps = conn.prepareStatement(SELECT_BY_EMAIL_PASSWORD_SQL)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return new Seller.Builder()
                            .id(rs.getLong("idseller"))
                            .username(rs.getString("username"))
                            .email(rs.getString("email"))
                            .password(rs.getString("password"))
                            .registeredAt(rs.getTimestamp("registerdAt").toLocalDateTime())
                            .profit(rs.getDouble("profit"))
                            .build();
                } else {
                    return null;
                }
            }
        }
    }

    public static synchronized SellerDAO getInstance() {
        if(instance == null) {
            try {
                instance = new SellerDAO();
            } catch (SQLException e) {
                // Într-un proiect real ai arunca o excepție custom sau ai face logging.
                throw new RuntimeException("Nu am putut inițializa SellerDAO: " + e.getMessage(), e);
            }
        }
        return instance;
    }

    public void update(Seller b) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, b.getUsername());
            ps.setString(2, b.getEmail());
            ps.setString(3, b.getPassword());
            ps.setDouble(4, b.getProfit());
            ps.setLong(5, b.getId());
            ps.executeUpdate();
        }
    }

}

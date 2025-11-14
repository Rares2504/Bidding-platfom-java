package dao;

import model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    private final Connection conn;
    private static final String INSERT_SQL = "INSERT INTO item(title, description, idseller) VALUES(?, ?, ?)";
    private static final String SELECT_BY_IDSELLER_SQL = "SELECT * FROM item WHERE idseller=?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM item WHERE iditem=?";
    private static final String DELETE_BY_ID_SQL = "DELETE FROM item WHERE iditem=?";

    public ItemDAO(Connection conn) {this.conn = conn;}

    public ItemDAO() throws SQLException {
        this(DatabaseConnectionManager.getInstance().getConnection());
    }

    public Item addItem(Item i) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, i.getTitle());
            ps.setString(2, i.getDescription());
            ps.setLong(3, i.getSellerId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    i.setId(keys.getLong(1));
                }
            }
        }
        return i;
    }

    public List<Item> getItemsBySellerID(Long id) throws SQLException {
        List<Item> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_IDSELLER_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Item.Builder()
                            .id(rs.getLong("iditem"))
                            .title(rs.getString("title"))
                            .description(rs.getString("description"))
                            .sellerId(rs.getLong("idseller"))
                            .build()
                    );
                }
            }
        }
        return list;
    }

    public Item getItemById(Long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Item.Builder()
                            .id(rs.getLong("iditem"))
                            .title(rs.getString("title"))
                            .description(rs.getString("description"))
                            .sellerId(rs.getLong("idseller"))
                            .build();
                }
                else {
                    return null;
                }
            }
        }
    }

    public boolean deleteItem(Long id) throws SQLException {
        try(PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID_SQL)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    // trebuie verificata


}

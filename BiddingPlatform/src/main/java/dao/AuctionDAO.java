package dao;

import model.Auction;
import model.AuctionStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionDAO {
    private final Connection conn;

    private static final String INSERT_SQL =
            "INSERT INTO auction(iditem, idseller, startprice, bidstep, status, starttime, endtime) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL =
            "SELECT * FROM auction WHERE idauction = ?";
    private static final String SELECT_BY_SELLER_SQL =
            "SELECT * FROM auction WHERE idseller = ?";
    private static final String SELECT_BY_ITEM_SQL =
            "SELECT * FROM auction WHERE iditem = ?";
    private static final String UPDATE_STATUS_SQL =
            "UPDATE auction SET status = ? WHERE idauction = ?";
    private static final String SELECT_BY_STATUS_SQL =
            "SELECT * FROM auction WHERE status = ?";
    private static final String DELETE_SQL =
            "DELETE FROM auction WHERE idauction = ?";

    public AuctionDAO(Connection conn) {
        this.conn = conn;
    }

    public AuctionDAO() throws SQLException {
        this(DatabaseConnectionManager.getInstance().getConnection());
    }

    /**
     * Inserează o nouă licitație și setează id-ul generat în obiect.
     */
    public Auction createAuction(Auction a) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, a.getIdItem());
            ps.setLong(2, a.getIdSeller());
            ps.setDouble(3, a.getStartPrice());
            ps.setInt(4, a.getBidStep().intValue());
            ps.setString(5, a.getStatus().name());
            ps.setTimestamp(6, Timestamp.valueOf(a.getStartTime()));
            ps.setTimestamp(7, Timestamp.valueOf(a.getEndTime()));

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    a.setId(keys.getLong(1));
                }
            }
        }
        return a;
    }

    /**
     * Găsește o licitație după id.
     */
    public Auction getAuctionById(Long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAuction(rs);
                }
                return null;
            }
        }
    }

    public List<Auction> getAuctionsByStatus(AuctionStatus status) throws SQLException {
        List<Auction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATUS_SQL)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Auction auc = new Auction.Builder()
                            .id(rs.getLong("idauction"))
                            .idItem(rs.getLong("iditem"))
                            .idSeller(rs.getLong("idseller"))
                            .startPrice(rs.getDouble("startprice"))
                            .bidStep(rs.getLong("bidstep"))
                            .startTime(rs.getTimestamp("starttime").toLocalDateTime())
                            .endTime(rs.getTimestamp("endtime").toLocalDateTime())
                            .status(AuctionStatus.valueOf(rs.getString("status")))
                            .build();
                    list.add(auc);
                }
            }
        }
        return list;
    }

    /**
     * Listează toate licitațiile unui seller.
     */
    public List<Auction> getAuctionsBySellerId(long sellerId) throws SQLException {
        List<Auction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_SELLER_SQL)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Auction.Builder()
                            .id(rs.getLong("idauction"))
                            .idItem(rs.getLong("iditem"))
                            .idSeller(rs.getLong("idseller"))
                            .startPrice(rs.getDouble("startprice"))
                            .bidStep(rs.getLong("bidstep"))
                            .startTime(rs.getTimestamp("starttime").toLocalDateTime())
                            .endTime(rs.getTimestamp("endtime").toLocalDateTime())
                            .status(AuctionStatus.valueOf(rs.getString("status")))
                            .build()
                    );
                }
            }
        }
        return list;
    }

    /**
     * Listează toate licitațiile pentru un item.
     */
    public List<Auction> getAuctionsByItemId(Long itemId) throws SQLException {
        List<Auction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ITEM_SQL)) {
            ps.setLong(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAuction(rs));
                }
            }
        }
        return list;
    }

    /**
     * Actualizează statusul unei licitații.
     * @return true dacă a fost modificată o înregistrare.
     */
    public boolean updateStatus(Long auctionId, AuctionStatus newStatus) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS_SQL)) {
            ps.setString(1, newStatus.name());
            ps.setLong(2, auctionId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    /**
     * Șterge licitația cu id-ul dat.
     * @return true dacă a fost ștearsă o înregistrare.
     */
    public boolean deleteAuction(Long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    /**
     * Mapare din ResultSet în obiect Auction.
     */
    private Auction mapRowToAuction(ResultSet rs) throws SQLException {
        Auction.Builder builder = new Auction.Builder()
                .id(rs.getLong("idauction"))
                .idItem(rs.getLong("iditem"))
                .idSeller(rs.getLong("idseller"))
                .startPrice(rs.getDouble("startprice"))
                .bidStep((long) rs.getInt("bidstep"))
                .status(AuctionStatus.valueOf(rs.getString("status")))
                .startTime(rs.getTimestamp("starttime").toLocalDateTime())
                .endTime(rs.getTimestamp("endtime").toLocalDateTime());
        return builder.build();
    }

    public void extendAuctionEndTime(Auction auction, int i) {
        auction.setEndTime(auction.getEndTime().plusMinutes(i));
    }
}

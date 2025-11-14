package dao;

import model.Auction;
import model.Bid;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class BidDAO {
    private final Connection conn;

    private static final String INSERT_SQL =
            "INSERT INTO bid(idbuyer, idauction, price) VALUES(?,?,?)";
    private static final String DELETE_ALL_BIDS_FROM_AUCTION_SQL =
            "DELETE FROM bid WHERE idauction = ?";
    private static  final String SELECT_BY_AUCTIONID_SQL =
            "SELECT * FROM bid WHERE idauction = ? ORDER BY price DESC";
    private static  final String SELECT_BY_BUYER_SQL =
            "SELECT * FROM bid WHERE idbuyer = ? ORDER BY price DESC";


    public BidDAO(Connection conn) throws SQLException {
        this.conn = conn;
    }

    public BidDAO() throws SQLException {
        this(DatabaseConnectionManager.getInstance().getConnection());
    }

    public Bid createBid(Bid b) throws SQLException {
        // vreu o verificare astfel incat daca mai exista un bid cu acelasi idauction si
        // acelasi idbuyer sa se modifice doar pretul
        try(PreparedStatement ps = conn.prepareStatement(INSERT_SQL,  Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, b.getIdBuyer());
            ps.setLong(2, b.getIdAuction());
            ps.setDouble(3, b.getPrice());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    b.setId(keys.getLong(1));
                }
            }
        }
        return b;
    }


    public List<Bid> getBidsByAuctionId(Long auctionId) throws SQLException {
        List<Bid> bids = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_AUCTIONID_SQL)) {
            ps.setLong(1, auctionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bid bid = new Bid.Builder()
                            .id(rs.getLong("idbid"))
                            .idAuction(rs.getLong("idauction"))
                            .idBuyer(rs.getLong("idbuyer"))
                            .price(rs.getDouble("price"))
                            .build();
                    bids.add(bid);
                }
            }
        }
        return bids;
    }

    public List<Bid> getBidsByBuyer(Long buyerId) throws SQLException {
        List<Bid> bids = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_BUYER_SQL)) {
            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bid bid = new Bid.Builder()
                            .id(rs.getLong("idbid"))
                            .idAuction(rs.getLong("idauction"))
                            .idBuyer(rs.getLong("idbuyer"))
                            .price(rs.getDouble("price"))
                            .build();
                    bids.add(bid);
                }
            }
        }
        return bids;
    }

    public SortedSet<Bid> getSortedBidsForAuction(Long auctionId) throws SQLException {
        List<Bid> bidList = getBidsByAuctionId(auctionId);
        // TreeSet le va ordona automat conform lui Bid.compareTo()
        return new TreeSet<>(bidList);
    }
}
package session;

import dao.BuyerDAO;
import dao.SellerDAO;
import model.Buyer;
import model.Seller;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthenticationService {
    private final BuyerDAO buyerDAO;
    private final SellerDAO sellerDAO;

    public AuthenticationService(Connection conn) {
        this.buyerDAO  = new BuyerDAO(conn);
        this.sellerDAO = new SellerDAO(conn);
    }

    /** Returnează un Buyer autentificat sau null dacă nu există */
    public Buyer loginBuyer(String email, String password) throws SQLException {
        return buyerDAO.findByEmailAndPassword(email, password);
    }

    /** Returnează un Seller autentificat sau null dacă nu există */
    public Seller loginSeller(String email, String password) throws SQLException {
        return sellerDAO.findByEmailAndPassword(email, password);
    }
}


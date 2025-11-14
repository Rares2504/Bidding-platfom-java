package service;

import dao.BuyerDAO;
import dao.SellerDAO;
import model.Buyer;
import model.Seller;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private BuyerDAO buyerDAO;
    private SellerDAO sellerDAO;

    public UserService() throws SQLException {
        // apelează constructorul no-arg din BuyerDB,
        // care intern face getInstance().getConnection()
        this.sellerDAO = new SellerDAO();
        this.buyerDAO = new BuyerDAO();
    }

    public List<Buyer> listAllBuyers() throws SQLException {
        return buyerDAO.getAllBuyers();
    }

    public Buyer createBuyer(Buyer buyer) throws SQLException {
        buyerDAO.addBuyer(buyer);
        return buyer;
    }

    public List<Seller> listAllSellers() throws SQLException {
        return sellerDAO.getAllSellers();
    }

    public Seller createSeller(Seller seller) throws SQLException {
        sellerDAO.addSeller(seller);
        return seller;
    }


//    private List<User> users = new ArrayList<>();
//
//
//    public void addBuyer(Buyer buyer) {
//        users.add(buyer);
//    }
//
//    public void addSeller(Seller seller) {
//        users.add(seller);
//    }
//
//    public void printUsers() {
//        for (User user : users) {
//            System.out.print(user.toString());
//        }
    }

    //autentificare buyer
//    public Optional<Buyer> authenticateBuyer(String username, String password) {
//        return users.stream()
//                .filter(user -> user instanceof Buyer)
//                .filter(user -> user.getUsername().equals(username) &&
//                        user.getPassword().equals(password))
//                .map(user -> (Buyer) user)
//                .findFirst();
//    }






import dao.*;
import model.*;
import session.AuthenticationService;
import session.SessionManager;
import util.AuditService;    // import for the audit service

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class Main2 {
    public static void main(String[] args) throws SQLException {
        // Open singleton database connection
        DatabaseConnectionManager mgr = DatabaseConnectionManager.getInstance();
        Connection conn = mgr.getConnection();
        Scanner scanner = new Scanner(System.in);

        // Initialize authentication service
        AuthenticationService authService = new AuthenticationService(conn);

        System.out.println("=== Authentication ===");
        System.out.println("1 - Buyer Login");
        System.out.println("2 - Seller Login");
        System.out.print("Select an option: ");
        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 1) {
            // Buyer login
            System.out.print("Buyer Email: ");
            String emailBuyer = scanner.nextLine();
            System.out.print("Password: ");
            String pwdBuyer = scanner.nextLine();
            Buyer buyer = authService.loginBuyer(emailBuyer, pwdBuyer);
            if (buyer == null) {
                System.out.println("Invalid buyer credentials. Exiting.");
                // Log failed login
                AuditService.logAction("LOGIN_FAIL_BUYER, email=" + emailBuyer);
            } else {
                SessionManager.login(buyer);
                System.out.println("Welcome, " + buyer.getUsername() + "!");

                // Log successful login
                AuditService.logAction("LOGIN_SUCCESS_BUYER, buyerId=" + buyer.getId());

                AuctionDAO auctionDAO = new AuctionDAO(conn);
                BidDAO bidDAO = new BidDAO(conn);
                ItemDAO itemDAO = new ItemDAO(conn);

                boolean runningBuyer = true;
                while (runningBuyer) {
                    System.out.println("\n=== Buyer Menu ===");
                    System.out.println("1 - View all available auctions");
                    System.out.println("2 - Place a bid");
                    System.out.println("3 - View auctions you've bid on");
                    System.out.println("4 - Edit your profile");
                    System.out.println("5 - Exit");
                    System.out.print("Select an option: ");
                    int optBuyer = Integer.parseInt(scanner.nextLine());

                    switch (optBuyer) {
                        case 1:
                            List<Auction> openAuctions = auctionDAO.getAuctionsByStatus(AuctionStatus.OPEN);
                            if (openAuctions.isEmpty()) {
                                System.out.println("No open auctions available.");
                            } else {
                                System.out.println("Open Auctions:");
                                for (Auction auc : openAuctions) {
                                    Item itm = itemDAO.getItemById(auc.getIdItem());
                                    List<Bid> bids = bidDAO.getBidsByAuctionId(auc.getId());
                                    double currentPrice = bids.isEmpty()
                                            ? auc.getStartPrice()
                                            : bids.get(0).getPrice();
                                    System.out.printf(
                                            "Auction ID: %d | Item: %s | Current Price: %.2f | Ends at: %s%n",
                                            auc.getId(), itm.getTitle(), currentPrice, auc.getEndTime()
                                    );
                                }
                            }
                            // Log view open auctions
                            AuditService.logAction("VIEW_OPEN_AUCTIONS, buyerId=" + buyer.getId());
                            break;

                        case 2:
                            List<Auction> auctionsList = auctionDAO.getAuctionsByStatus(AuctionStatus.OPEN);
                            if (auctionsList.isEmpty()) {
                                System.out.println("No open auctions to bid on.");
                                break;
                            }
                            System.out.println("Select an auction to bid on:");
                            for (int i = 0; i < auctionsList.size(); i++) {
                                Auction auc = auctionsList.get(i);
                                Item itm = itemDAO.getItemById(auc.getIdItem());
                                System.out.printf("%d. %s (Auction ID: %d)%n",
                                        i + 1, itm.getTitle(), auc.getId());
                            }
                            System.out.print("Enter number: ");
                            int idx = Integer.parseInt(scanner.nextLine()) - 1;
                            if (idx < 0 || idx >= auctionsList.size()) {
                                System.out.println("Invalid selection.");
                                break;
                            }
                            Auction selectedAuc = auctionsList.get(idx);
                            List<Bid> existingBids = bidDAO.getBidsByAuctionId(selectedAuc.getId());
                            double minAllowed = existingBids.isEmpty()
                                    ? selectedAuc.getStartPrice()
                                    : (existingBids.get(0).getPrice() + selectedAuc.getBidStep());
                            System.out.printf("Enter your bid amount (minimum %.2f): ", minAllowed);
                            double bidAmount = Double.parseDouble(scanner.nextLine());
                            if (bidAmount <= minAllowed) {
                                System.out.println("Invalid amount. Must be greater than " + minAllowed);
                                break;
                            }
                            Bid newBid = new Bid.Builder()
                                    .idAuction(selectedAuc.getId())
                                    .idBuyer(buyer.getId())
                                    .price(bidAmount)
                                    .build();
                            bidDAO.createBid(newBid);
                            auctionDAO.extendAuctionEndTime(selectedAuc, 1);
                            System.out.println("Bid placed successfully!");

                            // Log place bid
                            AuditService.logAction("PLACE_BID, auctionId=" + selectedAuc.getId()
                                    + ", buyerId=" + buyer.getId()
                                    + ", amount=" + bidAmount);
                            break;

                        case 3:
                            List<Bid> myBids = bidDAO.getBidsByBuyer(buyer.getId());
                            if (myBids.isEmpty()) {
                                System.out.println("You haven't placed any bids yet.");
                            } else {
                                System.out.println("Your bid status:");
                                Set<Long> auctionIds = new HashSet<>();
                                for (Bid b : myBids) {
                                    auctionIds.add(b.getIdAuction());
                                }
                                for (Long aucId : auctionIds) {
                                    Auction auc = auctionDAO.getAuctionById(aucId);
                                    Item itm = itemDAO.getItemById(auc.getIdItem());
                                    List<Bid> bidsForAuc = bidDAO.getBidsByAuctionId(aucId);
                                    Bid top = bidsForAuc.get(0);
                                    if (top.getIdBuyer().equals(buyer.getId())) {
                                        System.out.printf("Item: %s -> You are the HIGHEST BIDDER%n", itm.getTitle());
                                    } else {
                                        System.out.printf("Item: %s -> You have been outbid%n", itm.getTitle());
                                    }
                                }
                            }
                            // Log view own bids
                            AuditService.logAction("VIEW_OWN_BIDS, buyerId=" + buyer.getId());
                            break;

                        case 4:
                            editProfileFlow(buyer);
                            break;

                        case 5:
                            runningBuyer = false;
                            System.out.println("Goodbye, " + buyer.getUsername() + "!");
                            // Log buyer logout
                            AuditService.logAction("LOGOUT_BUYER, buyerId=" + buyer.getId());
                            break;

                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                }
            }

        } else if (choice == 2) {
            // Seller login
            System.out.print("Seller Email: ");
            String emailSeller = scanner.nextLine();
            System.out.print("Password: ");
            String pwdSeller = scanner.nextLine();
            Seller seller = authService.loginSeller(emailSeller, pwdSeller);
            if (seller == null) {
                System.out.println("Invalid seller credentials. Exiting.");
                // Log failed login
                AuditService.logAction("LOGIN_FAIL_SELLER, email=" + emailSeller);
            } else {
                SessionManager.login(seller);
                System.out.println("Welcome, " + seller.getUsername() + "!");

                // Log successful seller login
                AuditService.logAction("LOGIN_SUCCESS_SELLER, sellerId=" + seller.getId());

                ItemDAO itemDAO = new ItemDAO(conn);
                AuctionDAO auctionDAO = new AuctionDAO(conn);
                BidDAO bidDAO = new BidDAO(conn);

                boolean runningSeller = true;
                while (runningSeller) {
                    System.out.println("\n=== Seller Menu ===");
                    System.out.println("1 - Add Item");
                    System.out.println("2 - List My Items");
                    System.out.println("3 - Delete Item by ID");
                    System.out.println("4 - Create Auction");
                    System.out.println("5 - List My Auctions");
                    System.out.println("6 - Edit your profile");
                    System.out.println("7 - Exit");
                    System.out.print("Select an option: ");
                    int opt = Integer.parseInt(scanner.nextLine());

                    switch (opt) {
                        case 1:
                            System.out.print("Title: ");
                            String title = scanner.nextLine();
                            System.out.print("Description: ");
                            String description = scanner.nextLine();
                            Item newItem = new Item.Builder()
                                    .title(title)
                                    .description(description)
                                    .sellerId(seller.getId())
                                    .build();
                            itemDAO.addItem(newItem);
                            System.out.println("Item created: " + newItem);
                            // Log create item
                            AuditService.logAction("CREATE_ITEM, itemId=" + newItem.getId()
                                    + ", sellerId=" + seller.getId());
                            break;

                        case 2:
                            List<Item> items = itemDAO.getItemsBySellerID(seller.getId());
                            if (items.isEmpty()) {
                                System.out.println("You have no items.");
                            } else {
                                System.out.println("Your Items:");
                                items.forEach(System.out::println);
                            }
                            // Log list own items
                            AuditService.logAction("VIEW_MY_ITEMS, sellerId=" + seller.getId());
                            break;

                        case 3:
                            // Delete only items the seller has created
                            System.out.print("Enter Item ID to delete: ");
                            long delId = Long.parseLong(scanner.nextLine());
                            boolean deleted = itemDAO.deleteItem(delId);
                            System.out.println(deleted ? "Item deleted." : "Item not found.");
                            // Log delete item
                            if (deleted) {
                                AuditService.logAction("DELETE_ITEM, itemId=" + delId
                                        + ", sellerId=" + seller.getId());
                            } else {
                                AuditService.logAction("DELETE_ITEM_FAILED, itemId=" + delId
                                        + ", sellerId=" + seller.getId());
                            }
                            break;

                        case 4:
                            // Create Auction for an item
                            List<Item> sellerItems = itemDAO.getItemsBySellerID(seller.getId());
                            if (sellerItems.isEmpty()) {
                                System.out.println("You have no items to create an auction.");
                                break;
                            }
                            System.out.println("Select an item for auction:");
                            for (int i = 0; i < sellerItems.size(); i++) {
                                System.out.printf("%d. %s (ID: %d)%n",
                                        i + 1, sellerItems.get(i).getTitle(), sellerItems.get(i).getId());
                            }
                            System.out.print("Enter number: ");
                            int itemIdx = Integer.parseInt(scanner.nextLine()) - 1;
                            if (itemIdx < 0 || itemIdx >= sellerItems.size()) {
                                System.out.println("Invalid selection.");
                                break;
                            }
                            Item selected = sellerItems.get(itemIdx);
                            List<Auction> existingAuctions = auctionDAO.getAuctionsByItemId(selected.getId());
                            boolean hasActive = existingAuctions.stream()
                                    .anyMatch(a -> a.getStatus() == AuctionStatus.OPEN
                                            && a.getEndTime().isAfter(LocalDateTime.now()));
                            if (hasActive) {
                                System.out.println("An active auction already exists for this item.");
                                break;
                            }
                            System.out.print("Start Price: ");
                            double startPrice = Double.parseDouble(scanner.nextLine());
                            System.out.print("Bid Step: ");
                            long bidStep = Long.parseLong(scanner.nextLine());
                            LocalDateTime startTime = LocalDateTime.now();
                            System.out.print("Duration (minutes): ");
                            long duration = Long.parseLong(scanner.nextLine());
                            LocalDateTime endTime = startTime.plusMinutes(duration);
                            Auction auction = new Auction.Builder()
                                    .idItem(selected.getId())
                                    .idSeller(seller.getId())
                                    .startPrice(startPrice)
                                    .bidStep(bidStep)
                                    .status(AuctionStatus.OPEN)
                                    .startTime(startTime)
                                    .endTime(endTime)
                                    .build();
                            auctionDAO.createAuction(auction);
                            System.out.println("Auction created: " + auction);
                            // Log create auction
                            AuditService.logAction("CREATE_AUCTION, auctionId=" + auction.getId()
                                    + ", sellerId=" + seller.getId());
                            break;

                        case 5:
                            // List all auctions created by this seller, with bids sorted descending
                            List<Auction> myAuctions = auctionDAO.getAuctionsBySellerId(seller.getId());
                            if (myAuctions.isEmpty()) {
                                System.out.println("You have no auctions at the moment.");
                            } else {
                                System.out.println("Your Auctions and Bids:");
                                for (Auction myAuction : myAuctions) {
                                    // Display auction line
                                    Item auctionItem = null;
                                    try {
                                        auctionItem = itemDAO.getItemById(myAuction.getIdItem());
                                    } catch (SQLException e) {
                                        System.err.printf("Error reading Item (ID: %d): %s%n",
                                                myAuction.getIdItem(), e.getMessage());
                                    }
                                    String itemTitle = (auctionItem != null) ? auctionItem.getTitle() : "Unknown Item";
                                    System.out.printf(
                                            "Auction ID: %d | Item: %s | Start Price: %.2f | Status: %s | Ends at: %s%n",
                                            myAuction.getId(),
                                            itemTitle,
                                            myAuction.getStartPrice(),
                                            myAuction.getStatus(),
                                            myAuction.getEndTime()
                                    );

                                    // Fetch sorted bids (descending by price)
                                    SortedSet<Bid> bids = null;
                                    try {
                                        bids = bidDAO.getSortedBidsForAuction(myAuction.getId());
                                    } catch (SQLException e) {
                                        System.err.printf("Error loading bids for Auction ID %d: %s%n",
                                                myAuction.getId(), e.getMessage());
                                    }

                                    // Display each bid
                                    if (bids == null) {
                                        System.out.println("  Unable to load bids for this auction.");
                                    } else if (bids.isEmpty()) {
                                        System.out.println("  No bids for this auction.");
                                    } else {
                                        for (Bid bid : bids) {
                                            System.out.printf(
                                                    "  Bid ID: %d | Buyer ID: %d | Price: %.2f%n",
                                                    bid.getId(), bid.getIdBuyer(), bid.getPrice()
                                            );
                                        }
                                    }
                                    System.out.println();
                                }
                            }
                            // Log view own auctions
                            AuditService.logAction("VIEW_MY_AUCTIONS, sellerId=" + seller.getId());
                            break;

                        case 6:
                            editProfileFlow(seller);
                            break;

                        case 7:
                            runningSeller = false;
                            System.out.println("Goodbye, " + seller.getUsername() + "!");
                            // Log seller logout
                            AuditService.logAction("LOGOUT_SELLER, sellerId=" + seller.getId());
                            break;

                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                }
            }
        } else {
            System.out.println("Invalid choice. Exiting.");
            // Log invalid main menu choice
            AuditService.logAction("INVALID_MAIN_CHOICE, choice=" + choice);
        }

        // Cleanup
        SessionManager.logout();
        mgr.closeConnection();
        scanner.close();
    }

    private static void editProfileFlow(User user) throws SQLException {
        System.out.println("=== EDIT PROFILE ===");
        System.out.println("1. Change username");
        System.out.println("2. Change password");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.print("Enter new username: ");
                String newUser = scanner.nextLine().trim();
                user.setUsername(newUser);
                if (user instanceof Buyer) {
                    BuyerDAO.getInstance().update((Buyer) user);
                    System.out.println("Username updated for Buyer.");
                    // Log username change buyer
                    AuditService.logAction("UPDATE_USERNAME_BUYER, buyerId=" + user.getId());
                } else if (user instanceof Seller) {
                    SellerDAO.getInstance().update((Seller) user);
                    System.out.println("Username updated for Seller.");
                    // Log username change seller
                    AuditService.logAction("UPDATE_USERNAME_SELLER, sellerId=" + user.getId());
                }
                break;
            case "2":
                System.out.print("Enter new password: ");
                String newPass = scanner.nextLine().trim();
                user.setPassword(newPass);
                if (user instanceof Buyer) {
                    BuyerDAO.getInstance().update((Buyer) user);
                    System.out.println("Password updated for Buyer.");
                    // Log password change buyer
                    AuditService.logAction("UPDATE_PASSWORD_BUYER, buyerId=" + user.getId());
                } else if (user instanceof Seller) {
                    SellerDAO.getInstance().update((Seller) user);
                    System.out.println("Password updated for Seller.");
                    // Log password change seller
                    AuditService.logAction("UPDATE_PASSWORD_SELLER, sellerId=" + user.getId());
                }
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid option!");
                // Log invalid editProfile choice
                AuditService.logAction("INVALID_EDIT_CHOICE, userId=" + user.getId());
        }
    }
}

//package service;
//
//import model.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//public class ItemService {
//    private List<Item> items = new ArrayList<Item>();
//
//    public void addItem(Item item) {
//        items.add(item);
//    }
//
//    public void printAllItems() {
//        for (Item item : items) {
//            System.out.print(item.toString());
//        }
//    }
//
//    public void printItemById(Long id) {
//        items.stream()
//                .filter(i -> Objects.equals(i.getId(), id))
//                .findFirst()
//                .ifPresentOrElse(
//                        i -> System.out.println(i),
//                        () -> System.out.println("Item cu id = " + id + " nu a fost găsit.")
//                );
//    }
//
//    public void printSellerItems(Long sellerId) {
//        for (Item item : items) {
//            if (Objects.equals(item.getSellerId(), sellerId)) {
//                System.out.print(item.toString());
//            }
//        }
//    }
//    //auction part
//    private List<Auction> auctions = new ArrayList<Auction>();
//
//    public void addAuction(Auction auction) {
//        auctions.add(auction);
//    }
//
//    public void printAllAuctions() {
//        for (Auction auction : auctions) {
//            printItemById(auction.getIdItem());
//            System.out.print(auction.toString());
//        }
//    }
//
//    public Auction getAuctionById(Long auctionId) {
//        return auctions.stream()
//                .filter(a -> Objects.equals(a.getId(), auctionId))
//                .findFirst()
//                .orElse(null);
//    }
//
//    public void addNewBid(Auction auction, double price, Long idBuyer) {
//        // 1. prețul minim admis
//        List<Bid> bids = auction.getBids();  // nu va fi null
//        double minAllowed;
//        if (bids.isEmpty()) {
//            // lista goală → compar cu startPrice+step
//            minAllowed = auction.getStartPrice() + auction.getBidStep();
//        } else {
//            // deja există bids → compar cu ultimul bid+step
//            minAllowed = bids.get(0).getPrice() + auction.getBidStep();
//        }
//
//        // 2. validare și adăugare
//        if (price > minAllowed) {
//            Bid newBid = new Bid.Builder()
//                    .price(price)
//                    .idBuyer(idBuyer)
//                    .build();
//            auction.addBid(newBid);           // adaugă direct în model
//            auction.sortBidsDescending();     // păstrează ordinea descrescătoare
//            System.out.println("Bid adăugat: " + newBid);
//        } else {
//            System.out.println("Bid prea mic! Preț minim admis: " + minAllowed);
//        }
//    }
//}

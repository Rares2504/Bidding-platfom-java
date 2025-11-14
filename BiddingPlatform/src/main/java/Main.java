//import model.*;
//import service.ItemService;
//import service.UserService;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Scanner;
//
////TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
//// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//public class Main {
//    public static void main(String[] args) {
//        UserService userService = new UserService();
//        ItemService itemService = new ItemService();
//        Scanner scanner = new Scanner(System.in);
//
//        // ==== DATE DE TEST ====
//        // 1) Creează un seller și un buyer
//        Seller seller1 = new Seller.Builder()
//                .username("seller1")
//                .email("s1@mail.com")
//                .password("pass")
//                .registeredAt(LocalDateTime.now())
//                .profit(0.0)
//                .build();
//
//        userService.addSeller(seller1);
//        Buyer  buyer1  = new Buyer.Builder()
//                .username("buyer1")
//                .email("b1@mail.com")
//                .password("pass")
//                .registeredAt(LocalDateTime.now())
//                .balance(1000.0)
//                .build();
//        userService.addBuyer(buyer1);
//
//        // 2) Creează un item și o licitație pe el
//        Item item1 = new Item.Builder()
//                .title("Laptop")
//                .description("Un laptop second- hand cu 8GB de RAM")
//                .sellerId(seller1.getId())
//                .build();
//
//        itemService.addItem(item1);
//        Auction auction1 = new Auction.Builder()
//                .idItem(item1.getId())
//                .idSeller(seller1.getId())
//                .startPrice(500.0)
//                .bidStep(50L)
//                .endTime(LocalDateTime.now().plusHours(24))
//                .build();
//
//        itemService.addAuction(auction1);
//
//        // 3) Adaugă câteva bid-uri valide
//        itemService.addNewBid(auction1, 600.0, buyer1.getId());  // > 500+50 OK
//        itemService.addNewBid(auction1, 700.0, buyer1.getId());  // > 600+50 OK
//
//        // 4) Încearcă un bid prea mic
//        itemService.addNewBid(auction1, 720.0, buyer1.getId());  // 720 < 700+50 → mesaj de eroare
//
//        // 5) Afișează bid-urile curente înainte de startul meniului
//        System.out.println("\n--- Bid-urile licitației #" + auction1.getId() + " înainte de meniu:");
//        List<Bid> bids = auction1.getBids();
//        if (bids == null || bids.isEmpty()) {
//            System.out.println("  (nu există bid-uri)");
//        } else {
//            bids.forEach(b -> System.out.println("  " + b));
//        }
//        // ==== SFÂRȘIT DATE DE TEST ====
//
//        while (true) {
//            System.out.println("\n=== MENIU APLICAȚIE LICITAȚII ===");
//            System.out.println("1. Creare cont (buyer/seller)");
//            System.out.println("2. Listare toate conturile");
//            System.out.println("3. Adăugare item");
//            System.out.println("4. Listare toate item-urile");
//            System.out.println("5. Afișare item după ID");
//            System.out.println("6. Afișare item-uri ale unui seller");
//            System.out.println("7. Adăugare licitație");
//            System.out.println("8. Listare toate licitațiile");
//            System.out.println("9. Plasare bid");
//            System.out.println("10. Listare bid-uri licitație");
//            System.out.println("0. Ieșire");
//            System.out.print("Alege opțiunea: ");
//
//            String choice = scanner.nextLine();
//            switch (choice) {
//                case "1" -> {  // conturi
//                    System.out.println("Tip cont: 1=Buyer  2=Seller  0=Înapoi");
//                    String t = scanner.nextLine();
//                    if ("1".equals(t)) {
//                        System.out.print("Username: ");
//                        String u = scanner.nextLine();
//                        System.out.print("Email: ");
//                        String e = scanner.nextLine();
//                        System.out.print("Password: ");
//                        String p = scanner.nextLine();
//                        System.out.print("Balance: ");
//                        double bal = Double.parseDouble(scanner.nextLine());
//                        Buyer b = new Buyer.Builder()
//                                .username(u)
//                                .email(e)
//                                .password(p)
//                                .registeredAt(LocalDateTime.now())
//                                .balance(bal)
//                                .build();
//                        userService.addBuyer(b);
//                    } else if ("2".equals(t)) {
//                        System.out.print("Username: ");
//                        String u = scanner.nextLine();
//                        System.out.print("Email: ");
//                        String e = scanner.nextLine();
//                        System.out.print("Password: ");
//                        String p = scanner.nextLine();
//                        System.out.print("Profit: ");
//                        double pr = Double.parseDouble(scanner.nextLine());
//                        Seller s = new Seller.Builder()
//                                .username(u)
//                                .email(e)
//                                .password(p)
//                                .registeredAt(LocalDateTime.now())
//                                .profit(pr)
//                                .build();
//                        userService.addSeller(s);
//                    }
//                }
//
//                case "2" -> {  // listare conturi
//                    userService.printUsers();
//                }
//
//                case "3" -> {  // adăugare item
//                    System.out.print("Titlu item: ");
//                    String title = scanner.nextLine();
//                    System.out.print("Descriere: ");
//                    String desc = scanner.nextLine();
//                    System.out.print("Seller ID: ");
//                    Long sid = Long.parseLong(scanner.nextLine());
//                    Item item = new Item.Builder()
//                            .title(title)
//                            .description(desc)
//                            .sellerId(sid)
//                            .build();
//                    itemService.addItem(item);
//                }
//
//                case "4" -> {  // listare item-uri
//                    itemService.printAllItems();
//                }
//
//                case "5" -> {  // afişare item după ID
//                    System.out.print("ID item: ");
//                    Long iid = Long.parseLong(scanner.nextLine());
//                    itemService.printItemById(iid);
//                }
//
//                case "6" -> {  // afişare item-uri ale unui seller
//                    System.out.print("Seller ID: ");
//                    Long sellId = Long.parseLong(scanner.nextLine());
//                    itemService.printSellerItems(sellId);
//                }
//
//                case "7" -> {  // adăugare licitație
//                    System.out.print("Item ID: ");
//                    Long iid2 = Long.parseLong(scanner.nextLine());
//                    System.out.print("Seller ID: ");
//                    Long sell2 = Long.parseLong(scanner.nextLine());
//                    System.out.print("Start price: ");
//                    Double sp = Double.parseDouble(scanner.nextLine());
//                    System.out.print("Bid step: ");
//                    Long bs = Long.parseLong(scanner.nextLine());
//                    System.out.print("Durata (ore): ");
//                    long dur = Long.parseLong(scanner.nextLine());
//                    LocalDateTime start = LocalDateTime.now();
//                    LocalDateTime end = start.plusHours(dur);
//                    Auction auc = new Auction.Builder()
//                            .idItem(iid2)
//                            .idSeller(sell2)
//                            .startPrice(sp)
//                            .bidStep(bs)
//                            .endTime(end)
//                            .build();
//
//                    itemService.addAuction(auc);
//                }
//
//                case "8" -> {  // listare licitații
//                    itemService.printAllAuctions();
//                }
//
//                case "9" -> {  // plasare bid
//                    System.out.print("Auction ID: ");
//                    Long aid = Long.parseLong(scanner.nextLine());
//                    Auction a = itemService.getAuctionById(aid);
//                    if (a == null) {
//                        System.out.println("Licitația nu există!");
//                        break;
//                    }
//                    System.out.print("Buyer ID: ");
//                    Long bidBuyer = Long.parseLong(scanner.nextLine());
//                    System.out.print("Preț bid: ");
//                    double price = Double.parseDouble(scanner.nextLine());
//                    itemService.addNewBid(a, price, bidBuyer);
//                }
//
//                case "10" -> {  // listare bid-uri licitație
//                    System.out.print("Auction ID: ");
//                    Long aid2 = Long.parseLong(scanner.nextLine());
//                    Auction a2 = itemService.getAuctionById(aid2);
//                    if (a2 == null) {
//                        System.out.println("Licitația nu există!");
//                    } else {
//                        for (Bid b : a2.getBids()) {
//                            System.out.print(b.toString());
//                        }
//                    }
//                }
//
//                case "0" -> {
//                    System.out.println("La revedere!");
//                    System.exit(0);
//                }
//
//                default -> System.out.println("Opțiune invalidă, încercați din nou.");
//            }
//        }
//    }
//}
//
//// am ramas sa fac filele pt conexiunea la baza de date
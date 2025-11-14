package model;

public class Bid implements Comparable<Bid>{
    private Long id;
    private double price;
    private Long idBuyer;
    private Long idAuction;

    private Bid(Builder builder) {
        this.id = builder.id;
        this.price = builder.price;
        this.idBuyer = builder.idBuyer;
        this.idAuction = builder.idAuction;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public double getPrice() {
        return price;
    }
    public Long getIdBuyer() {
        return idBuyer;
    }
    public Long getIdAuction() {return idAuction;}

    @Override
    public int compareTo(Bid other) {
        int cmp;
        cmp = Double.compare(other.price, this.price);
        if (cmp!=0) return cmp;
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "\nBid ID: " + id + "\nPrice: " + price + "\nBuyer ID: " + idBuyer + "\n";
    }

    public static class Builder {
        // câmpuri corespunzătoare clasei exterioare
        private Long id;
        private double price;
        private Long idBuyer;
        private Long idAuction;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder idBuyer(Long idBuyer) {
            this.idBuyer = idBuyer;
            return this;
        }

        public Builder idAuction(Long idAuction) {
            this.idAuction = idAuction;
            return this;
        }

        public Bid build() {
            if (price <= 0 || idBuyer == null) {
                throw new IllegalStateException("Lipsește un câmp obligatoriu pentru Bid");
            }
            return new Bid(this);
        }
    }

}

package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import static model.AuctionStatus.OPEN;

public class Auction {
    private Long id;
    private Long idItem;
    private Long idSeller;
    private Double startPrice;
    private Long bidStep;
    private AuctionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Auction(Builder builder) {
        this.id = builder.id;
        this.idItem = builder.idItem;
        this.idSeller = builder.idSeller;
        this.startPrice = builder.startPrice;
        this.bidStep = builder.bidStep;
        this.status = builder.status;
        this.startTime = (builder.startTime != null)
                ? builder.startTime
                : LocalDateTime.now();
        this.endTime = builder.endTime;
    }


    public void setId(Long id) {this.id = id; }

    public Long getId() {return id; }

    public Long getIdItem() {
        return idItem;
    }

    public Long getIdSeller() {
        return idSeller;
    }

    public Double getStartPrice() {
        return startPrice;
    }

    public Long getBidStep() {
        return bidStep;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");
        String formattedEnd = endTime.format(fmt);

        return "Auction ID: " + id +
                "\nStart Price: " + startPrice +
                "\nBid Step: " + bidStep +
                "\nStatus: " + status +
                "\nEnd Time: " + formattedEnd +
                "\n";
    }

    public static class Builder {
        // câmpuri corespunzătoare clasei exterioare
        private Long id;
        private Long idItem;
        private Long idSeller;
        private Double startPrice;
        private Long bidStep;
        private AuctionStatus status = AuctionStatus.OPEN;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder idItem(Long idItem) {
            this.idItem = idItem;
            return this;
        }

        public Builder idSeller(Long idSeller) {
            this.idSeller = idSeller;
            return this;
        }

        public Builder startPrice(Double startPrice) {
            this.startPrice = startPrice;
            return this;
        }

        public Builder bidStep(Long bidStep) {
            this.bidStep = bidStep;
            return this;
        }

        public Builder status(AuctionStatus status) {
            this.status = status;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Auction build() {
            if (idItem == null || idSeller == null || startPrice == null || bidStep == null || endTime == null) {
                throw new IllegalStateException("Lipsește un câmp obligatoriu pentru Auction");
            }
            return new Auction(this);
        }
    }
}

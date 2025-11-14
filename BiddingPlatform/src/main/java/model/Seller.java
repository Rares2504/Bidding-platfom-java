package model;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Seller extends User{
    private Double profit;

    private Seller(Builder builder) {
        super(  builder.id,
                builder.username,
                builder.email,
                builder.password,
                builder.registeredAt);
        this.profit = builder.profit;
    }

    public double getProfit() {
        return profit;
    }
    public void setProfit(double profit) {
        this.profit = profit;
    }
    @Override
    public String toString() {
        return super.toString() + "\nProfit: " + profit + "\n";
    }

    public static class Builder {
        // câmpuri corespunzătoare clasei exterioare
        private Long id;
        private String username;
        private String email;
        private String password;
        private LocalDateTime registeredAt;
        private Double profit;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder registeredAt(LocalDateTime registeredAt) {
            this.registeredAt = registeredAt;
            return this;
        }

        public Builder profit(Double profit) {
            this.profit = profit;
            return this;
        }

        public Seller build() {
            if (username == null || email == null || password == null) {
                throw new IllegalStateException("Lipsește un câmp obligatoriu pentru Seller");
            }
            return new Seller(this);
        }
    }
}

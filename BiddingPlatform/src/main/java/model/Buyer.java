package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Buyer extends User{
    private double balance;

    private Buyer(Builder builder) {
        super(
                builder.id,
                builder.username,
                builder.email,
                builder.password,
                builder.registeredAt);
        this.balance = builder.balance;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return super.toString() + "\nBalance: " + balance + "\n";
    }

    public static class Builder {
        // câmpuri corespunzătoare clasei exterioare
        private Long id;
        private String username;
        private String email;
        private String password;
        private LocalDateTime registeredAt;
        private double balance;

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
        public Builder balance(double balance) {
            this.balance = balance;
            return this;
        }

        public Buyer build() {
            if (username == null || email == null || password == null) {
                throw new IllegalStateException("Lipsește un câmp obligatoriu pentru Buyer");
            }
            return new Buyer(this);
        }
    }
}

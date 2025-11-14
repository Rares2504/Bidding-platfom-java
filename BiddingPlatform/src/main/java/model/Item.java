package model;

public class Item {
    private Long id;
    private String title;
    private String description;
    private Long sellerId;

    private Item(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.sellerId = builder.sellerId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getSellerId() {
        return sellerId;
    }

    @Override
    public String toString() {
        return "\nTitle: " + title + "\nDescription: " + description + "\nSeller ID: " + sellerId;
    }

    public static class Builder {
        // câmpuri corespunzătoare clasei exterioare
        private Long id;
        private String title;
        private String description;
        private Long sellerId;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder sellerId(Long sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public Item build() {
            if (title == null || sellerId == null) {
                throw new IllegalStateException("Lipsește un câmp obligatoriu pentru Item");
            }
            return new Item(this);
        }

    }


}

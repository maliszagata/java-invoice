package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

public abstract class Product {
    private final String name;

    private final BigDecimal price;

    private final BigDecimal taxPercent;

    protected Product(String name, BigDecimal price, BigDecimal tax) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Name can not be null or empty!");
        }
        if (price == null || price.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("Price can not by null or lower than zero!");
        }
        this.name = name;
        this.price = price;
        this.taxPercent = tax;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public BigDecimal getPriceWithTax() {
        return price.add(taxPercent.multiply(price));
    }
}

package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class BottleOfWine extends Product {

    private static final BigDecimal excise = BigDecimal.valueOf(5.56);

    public BottleOfWine(String name, BigDecimal price) {
        super(name, price, new BigDecimal("0.08"));
    }

    public BigDecimal getPriceWithTax() {
        return this.getPrice().multiply(this.getTaxPercent()).add(this.getPrice()).add(excise);
    }
}

package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class FuelCanister extends Product {

    private static final BigDecimal excise = BigDecimal.valueOf(5.56);

    public FuelCanister(String name, BigDecimal price) {
        super(name, price, new BigDecimal("0.0"));
    }

    public BigDecimal getPriceWithTax() {
        return this.getPrice().multiply(this.getTaxPercent()).add(this.getPrice()).add(excise);
    }
}

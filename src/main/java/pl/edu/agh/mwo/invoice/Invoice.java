package pl.edu.agh.mwo.invoice;

import pl.edu.agh.mwo.invoice.product.Product;

import java.math.BigDecimal;
import java.util.HashMap;

import static java.math.BigDecimal.ZERO;

public class Invoice {
    private HashMap<Product, Integer> products = new HashMap<>();

    public void addProduct(Product product) {
        this.addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (quantity.equals(0) || Integer.signum(quantity) == -1) {
            throw new IllegalArgumentException("Quantity can not be zero or negative!");
        }
        products.put(product, quantity);
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = BigDecimal.valueOf(products.get(product));
            BigDecimal tempSubtotal = product.getPrice().multiply(quantity);
            subtotal = subtotal.add(tempSubtotal);
        }
        return subtotal;
    }

    public BigDecimal getTax() {
        BigDecimal tax = ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = BigDecimal.valueOf(products.get(product));
            BigDecimal tempTax = product.getPrice().multiply(product.getTaxPercent()).multiply(quantity);
            tax = tax.add(tempTax);
        }
        return tax;
    }

    public BigDecimal getTotal() {
        BigDecimal total = ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = BigDecimal.valueOf(products.get(product));
            BigDecimal tempTotal = product.getPrice().add(product.getPrice().multiply(product.getTaxPercent())).multiply(quantity);
            total = total.add(tempTotal);
        }
        return total;
    }
}

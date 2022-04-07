package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private Map<Product, Integer> products = new HashMap<Product, Integer>();
    private final int number;
    private static int counter = 0;

    public Invoice() {
        ++counter;
        this.number = counter;
    }

    public Map<Product, Integer> getProducts() {
        return Collections.unmodifiableMap(products);
    }

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }

        for (Product p : products.keySet()) {
            if (p.getName().equals(product.getName())) {
                products.compute(p, (key, value) ->  value == null ? quantity : value + quantity);
                return;
            }
        }
        products.put(product, quantity);
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }

    public int getNumber() {
        return number;
    }

    public String print() {

        return "Numer faktury: " + this.getNumber() + "\n"
                + products.keySet()
                .stream()
                .map(p -> productRowPrintMaker(p, products.get(p)))
                .collect(Collectors.joining(""))
                + "Liczba pozycji: " + products.size() + ".";
    }

    private String productRowPrintMaker(Product product, Integer quantity) {
        return "Produkt: " + product.getName() + ", ilość: " + quantity
                + ", cena: " + product.getPrice().multiply(BigDecimal.valueOf(quantity)) + ".\n";
    }

}

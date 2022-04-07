package pl.edu.agh.mwo.invoice;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.mwo.invoice.product.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }

    @Test
    public void testInvoiceHasNumberGreaterThan0() {
        int number = invoice.getNumber();
        Assert.assertTrue(number > 0);
    }

    @Test
    public void testInvoiceHasUniqueNumber() {
        Invoice newInvoice = new Invoice();
        Assert.assertTrue(newInvoice.getNumber() != invoice.getNumber());
    }

    @Test
    public void testInvoiceCanPrintAllProducts() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 2);
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);

        String print = invoice.print();
        System.out.println(invoice.print());

        Assert.assertEquals(invoice.getProducts().keySet().stream().map(Product::getName)
                .filter(print::contains).count(), invoice.getProducts().size());
    }

    @Test
    public void testInvoiceCanPrintInvoiceNumber() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 2);
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);

        String print = invoice.print();
        Assert.assertTrue(print.contains(String.valueOf(invoice.getNumber())));
    }

    @Test
    public void testInvoiceCanPrintNumberOfPrintedProducts() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 2);
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);

        String print = invoice.print();
        Assert.assertTrue(print.contains(String.valueOf(invoice.getProducts().size())));
    }

    @Test
    public void testInvoiceCanGetMultipleSameProducts() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 2);
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 3);

        String print = invoice.print();
        System.out.println(print);
        Assert.assertEquals(1, invoice.getProducts().keySet().stream().filter(p -> p.getName().equals("Tablet")).count());
    }

    @Test
    public void testInvoiceAddProductWithExcise() {
        invoice.addProduct(new BottleOfWine("Wisienka", new BigDecimal(20.0)), 3);
        invoice.addProduct(new FuelCanister("Super Canister", new BigDecimal(120.0)), 2);

        Assert.assertTrue(invoice.getProducts().keySet().stream().map(p -> p.getName())
                .collect(Collectors.toList()).containsAll(Arrays.asList("Wisienka", "Super Canister")));
    }

    @Test
    public void testInvoiceProductWithExcisePrice() {
        BottleOfWine wine = new BottleOfWine("Wisienka", new BigDecimal(20.0));
        invoice.addProduct(wine, 3);

        Assert.assertEquals(wine.getPriceWithTax().multiply(BigDecimal.valueOf(invoice.getProducts().get(wine))),
                BigDecimal.valueOf(81.48));
    }

    @Test
    public void testInvoiceFuelPrice() {
        FuelCanister fuelCanister = new FuelCanister("Super fuel", new BigDecimal(10.0));
        invoice.addProduct(fuelCanister, 3);

        Assert.assertEquals(fuelCanister.getPriceWithTax().multiply(BigDecimal.valueOf(invoice.getProducts().get(fuelCanister))),
                BigDecimal.valueOf(46.68));
    }

}

package backend.dto.saleDTO;

public class SaleConfirmationDTO {
    private Integer idSale;

    private String sellerFirstname;

    private String sellerLastname;

    private String customerFirstname;

    private String customerLastname;

    private String saleDate;

    private String paymentMethod;

    private Integer idPaymentMethod;

    private String headquarterName;

    private Integer idHeadquarter;

    public Integer getIdSale() {
        return idSale;
    }

    public void setIdSale(Integer idSale) {
        this.idSale = idSale;
    }

    public String getSellerFirstname() {
        return sellerFirstname;
    }

    public void setSellerFirstname(String sellerFirstname) {
        this.sellerFirstname = sellerFirstname;
    }

    public String getSellerLastname() {
        return sellerLastname;
    }

    public void setSellerLastname(String sellerLastname) {
        this.sellerLastname = sellerLastname;
    }

    public String getCustomerFirstname() {
        return customerFirstname;
    }

    public void setCustomerFirstname(String customerFirstname) {
        this.customerFirstname = customerFirstname;
    }

    public String getCustomerLastname() {
        return customerLastname;
    }

    public void setCustomerLastname(String customerLastname) {
        this.customerLastname = customerLastname;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getIdPaymentMethod() {
        return idPaymentMethod;
    }

    public void setIdPaymentMethod(Integer idPaymentMethod) {
        this.idPaymentMethod = idPaymentMethod;
    }

    public String getHeadquarterName() {
        return headquarterName;
    }

    public void setHeadquarterName(String headquarterName) {
        this.headquarterName = headquarterName;
    }

    public Integer getIdHeadquarter() {
        return idHeadquarter;
    }

    public void setIdHeadquarter(Integer idHeadquarter) {
        this.idHeadquarter = idHeadquarter;
    }
}

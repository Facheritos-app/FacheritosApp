package facheritosfrontendapp.objectRowView.saleRowView;

public class SaleRequestSingleRowView {
    private Integer idModel;

    private String name;

    private Integer quantity;

    private String price;

    private String multipliedPrice;

    public SaleRequestSingleRowView(Integer idModel, String name, Integer quantity, String price, String multipliedPrice) {
        this.idModel = idModel;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.multipliedPrice = multipliedPrice;
    }

    public Integer getIdModel() {
        return idModel;
    }

    public void setIdModel(Integer idModel) {
        this.idModel = idModel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMultipliedPrice() {
        return multipliedPrice;
    }

    public void setMultipliedPrice(String multipliedPrice) {
        this.multipliedPrice = multipliedPrice;
    }
}

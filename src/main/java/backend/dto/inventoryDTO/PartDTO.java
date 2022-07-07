package backend.dto.inventoryDTO;

public class PartDTO {

    private Integer idPart;

    private String name;

    private Double price;

    private String description;

    private Integer id_headquarter;

    private Integer quantity;

    public Integer getIdPart() {
        return idPart;
    }

    public void setIdPart(Integer idPart) {
        this.idPart = idPart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId_headquarter() {
        return id_headquarter;
    }

    public void setId_headquarter(Integer id_headquarter) {
        this.id_headquarter = id_headquarter;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

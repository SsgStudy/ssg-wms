package vo;

import lombok.Data;
import util.enumcollect.SalesStatus;

import java.sql.Date;

@Data
public class Product {
    private String ProductCode;
    private String ProductId;
    private String ProductName;
    private int ProductPrice;
    private String ProductBrand;
    private String ProductOrign;
    private String Manufactor;
    private SalesStatus PrductStatus;
    private Date ProductManufactorsDate;
    private String CategoryCode;
}

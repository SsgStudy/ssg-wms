package vo;

import lombok.Data;
import util.enumcollect.SalesStatus;

import java.sql.Date;

@Data
public class Product {
    private String productCode;
    private String productId;
    private String productName;
    private int productPrice;
    private String productBrand;
    private String productOrign;
    private String manufactor;
    private SalesStatus prductStatus;
    private Date productManufactorsDate;
    private String categoryCode;
}

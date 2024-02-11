package vo;

import lombok.Data;

@Data
public class Category {
    private String categoryCode;
    private String categoryName;
    private String categoryParentCode;
}

package vo;

import lombok.Data;

@Data
public class CategoryVO {
    private String categoryCode;
    private String categoryName;
    private String categoryParentCode;
}

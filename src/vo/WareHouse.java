package vo;


public class WareHouse {
    private String warehouseCode;
    private String warehouseName;
    private String warehouseLocation;
    private String warehouseType;
    private int memberSeq;

    public int getMemberSeq() {
        return memberSeq;
    }

    public void setMemberSeq(int memberSeq) {
        this.memberSeq = memberSeq;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public String getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(String warehouseType) {
        this.warehouseType = warehouseType;
    }

    @Override
    public String toString() {
        return "WareHouse{" +
                "warehouseCode='" + warehouseCode + '\'' +
                ", warehouseName='" + warehouseName + '\'' +
                ", warehouseLocation='" + warehouseLocation + '\'' +
                ", warehouseType='" + warehouseType + '\'' +
                ", memberSeq=" + memberSeq +
                '}';
    }
}

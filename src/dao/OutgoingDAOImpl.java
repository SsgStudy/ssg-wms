package dao;

import controller.OutgoingController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import util.DbConnection;
import vo.InventoryVO;
import vo.OutgoingInstVO;
import vo.OutgoingVO;

public class OutgoingDAOImpl implements OutgoingDAO {

    private static OutgoingDAOImpl instance;

    private static Logger log = Logger.getLogger(OutgoingDAOImpl.class.getName());

    public OutgoingDAOImpl() {
    }

    public static synchronized OutgoingDAOImpl getInstance() {
        if (instance == null) {
            instance = new OutgoingDAOImpl();
        }
        return instance;
    }

    @Override
    public List<OutgoingInstVO> getAllOutgoingInsts() {
        List<OutgoingInstVO> outgoingInsts = new ArrayList<>();
        String sql = "SELECT PK_SHOP_PURCHASE_SEQ, V_OUTGOING_INTS_STATUS FROM TB_OUTGOING_INST WHERE V_OUTGOING_INTS_STATUS = 'INSTRUCTION'";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                OutgoingInstVO vo = new OutgoingInstVO();
                vo.setShopPurchaseSeq(rs.getLong("PK_SHOP_PURCHASE_SEQ"));
                vo.setOutgoingInstStatus(rs.getString("V_OUTGOING_INTS_STATUS"));
                outgoingInsts.add(vo);
            }
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
        return outgoingInsts;
    }

    @Override
    public void insertOutgoingProduct(OutgoingVO outgoingVO) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DbConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO TB_OUTGOING_PRODUCT (V_OUTGOING_STATUS, N_OUTGOING_CNT, PK_SHOP_PURCHASE_DETAIL_SEQ, PK_SHOP_PURCHASE_SEQ, V_PRODUCT_CD) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, outgoingVO.getOutgoingStatus());
            pstmt.setInt(2, outgoingVO.getOutgoingCnt());
            pstmt.setLong(3, outgoingVO.getShopPurchaseDetailSeq());
            pstmt.setLong(4, outgoingVO.getShopPurchaseSeq());
            pstmt.setString(5, outgoingVO.getProductCd());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("출고 등록 실패");
            }

            String updateStatusSql = "UPDATE TB_OUTGOING_INST SET V_OUTGOING_INTS_STATUS = 'PROGRESS' WHERE PK_SHOP_PURCHASE_SEQ = ?";
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateStatusSql)) {
                updatePstmt.setLong(1, outgoingVO.getShopPurchaseSeq());
                updatePstmt.executeUpdate();
            }

            conn.commit();
            System.out.println("출고 등록이 성공적으로 추가되었습니다.");

        } catch (Exception e) {
            log.info("출고 등록 중 예외 발생: " + e.getMessage());

            if (conn != null) {
                try {
                    log.info("롤백 시도...");
                    conn.rollback();
                    log.info("롤백 완료");
                } catch (SQLException ex) {
                    log.warning("롤백 중 예외 발생: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    log.warning(e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    log.warning(e.getMessage());
                }
            }
        }
    }

    public void processOutgoingProducts(int shopSeq) throws Exception {
        List<OutgoingVO> outgoingProducts = new ArrayList<>();
        String query = "SELECT spd.PK_SHOP_PURCHASE_DETAIL_SEQ, spd.PK_SHOP_PURCHASE_SEQ, spd.N_PRODUCT_CNT, spd.V_PRODUCT_CD, sp.PK_SHOP_CD, sp.V_SHOP_PURCHASE_NM, sp.V_SHOP_PURCHASE_ADDR, sp.V_SHOP_PURCHASE_TEL, sp.V_SHOP_PURCHASE_STATUS, sp.DT_SHOP_PURCHASE_DATE FROM TB_SHOP_PURCHASE sp JOIN TB_SHOP_PURCHASE_DETAIL spd ON sp.PK_SHOP_PURCHASE_SEQ = spd.PK_SHOP_PURCHASE_SEQ WHERE sp.PK_SHOP_PURCHASE_SEQ = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, shopSeq);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("결과가 없습니다. shopSeq: " + shopSeq);
                return;
            }

            do {
                OutgoingVO outgoingVO = new OutgoingVO();
                outgoingVO.setShopPurchaseSeq(rs.getLong("PK_SHOP_PURCHASE_SEQ"));
                outgoingVO.setShopPurchaseDetailSeq(rs.getLong("PK_SHOP_PURCHASE_DETAIL_SEQ"));
                outgoingVO.setOutgoingCnt(rs.getInt("N_PRODUCT_CNT"));
                outgoingVO.setProductCd(rs.getString("V_PRODUCT_CD"));
                outgoingVO.setOutgoingDate(LocalDateTime.now());
                outgoingVO.setOutgoingStatus("READY");
                outgoingProducts.add(outgoingVO);
            } while (rs.next());

        } catch (SQLException e) {
            throw new SQLException("DB 조회 에러", e);
        }

        for (OutgoingVO product : outgoingProducts) {
            try {
                insertOutgoingProduct(product);
            } catch (SQLException e) {
                throw new SQLException("DB 삽입 에러", e);
            }
        }
    }

    // 출고 상태 및 출고일자 업데이트
    public void updateOutgoingProductStatusAndDate(Long pkOutgoingId, LocalDateTime date, String status) throws Exception {
        String sql = "UPDATE TB_OUTGOING_PRODUCT SET V_OUTGOING_STATUS = ?, DT_OUTGOING_DATE = ? WHERE PK_OUTGOING_ID = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setString(2, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt.setLong(3, pkOutgoingId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("출고 상태 업데이트 실패: PK_OUTGOING_ID = " + pkOutgoingId);
            }
        }
    }

    // 출고 수량 조회
    public int getOutgoingProductQuantity(Long pkOutgoingId) throws Exception {
        String sql = "SELECT N_OUTGOING_CNT FROM TB_OUTGOING_PRODUCT WHERE PK_OUTGOING_ID = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, pkOutgoingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("N_OUTGOING_CNT");
                } else {
                    throw new SQLException("출고 수량 조회 실패: PK_OUTGOING_ID = " + pkOutgoingId);
                }
            }
        }
    }

    // 창고 코드 및 구역 코드에 따른 재고 조회
    public List<InventoryVO> getInventoryByProductCodeAndQuantity(String productCd, int quantity) throws Exception {
        List<InventoryVO> inventories = new ArrayList<>();
        String sql = "SELECT V_WAREHOUSE_CD, V_ZONE_CD, N_INVENTORY_CNT FROM TB_INVENTORY WHERE V_PRODUCT_CD = ? AND N_INVENTORY_CNT >= ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, productCd);
            pstmt.setInt(2, quantity);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    InventoryVO inventory = new InventoryVO();
                    inventory.setWarehouseCd(rs.getString("V_WAREHOUSE_CD"));
                    inventory.setZoneCd(rs.getString("V_ZONE_CD"));
                    inventory.setInventoryCnt(rs.getInt("N_INVENTORY_CNT"));
                    inventories.add(inventory);
                }
            }
        }
        return inventories;
    }

    public void updateOutgoingProduct(Long pkOutgoingId, int newQuantity, String warehouseCd, String zoneCd) throws Exception {
        String sql = "UPDATE TB_OUTGOING_PRODUCT SET N_OUTGOING_CNT = ?, V_WAREHOUSE_CD = ?, V_ZONE_CD = ? WHERE PK_OUTGOING_ID = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity);
            pstmt.setString(2, warehouseCd);
            pstmt.setString(3, zoneCd);
            pstmt.setLong(4, pkOutgoingId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("출고 상품 업데이트 실패: PK_OUTGOING_ID = " + pkOutgoingId);
            }
        }
    }

    @Override
    public String getProductCodeByOutgoingId(Long pkOutgoingId) throws Exception {
        String sql = "SELECT V_PRODUCT_CD FROM TB_OUTGOING_PRODUCT WHERE PK_OUTGOING_ID = ?";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, pkOutgoingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("V_PRODUCT_CD");
                } else {
                    throw new SQLException("상품 코드 조회 실패: PK_OUTGOING_ID = " + pkOutgoingId);
                }
            }
        }
    }

    //출고 테이블 전체 조회
    public List<OutgoingVO> getAllOutgoings() throws Exception {
        List<OutgoingVO> outgoings = new ArrayList<>();
        String sql = "SELECT PK_OUTGOING_ID, V_OUTGOING_STATUS, DT_OUTGOING_DATE, N_OUTGOING_CNT, V_PRODUCT_CD, V_WAREHOUSE_CD, V_ZONE_CD FROM TB_OUTGOING_PRODUCT";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                OutgoingVO outgoing = new OutgoingVO();
                outgoing.setOutgoingId(rs.getLong("PK_OUTGOING_ID"));
                outgoing.setOutgoingStatus(rs.getString("V_OUTGOING_STATUS"));
                Timestamp outgoingDateTimestamp = rs.getTimestamp("DT_OUTGOING_DATE");
                if (outgoingDateTimestamp != null) {
                    outgoing.setOutgoingDate(outgoingDateTimestamp.toLocalDateTime());
                } else {
                    outgoing.setOutgoingDate(null);
                }
                outgoing.setOutgoingCnt(rs.getInt("N_OUTGOING_CNT"));
                outgoing.setProductCd(rs.getString("V_PRODUCT_CD"));
                outgoing.setWarehouseCd(rs.getString("V_WAREHOUSE_CD"));
                outgoing.setZoneCd(rs.getString("V_ZONE_CD"));
                outgoings.add(outgoing);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return outgoings;
    }

}
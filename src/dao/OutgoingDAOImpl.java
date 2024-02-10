package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import util.DbConnection;
import vo.OutgoingInstVO;
import vo.OutgoingVO;

public class OutgoingDAOImpl implements OutgoingDAO {

    private static OutgoingDAOImpl instance;

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
            e.printStackTrace();
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

            String sql = "INSERT INTO TB_OUTGOING_PRODUCT (V_OUTGOING_STATUS, DT_OUTGOING_DATE, N_OUTGOING_CNT, PK_SHOP_PURCHASE_DETAIL_SEQ, PK_SHOP_PURCHASE_SEQ, V_PRODUCT_CD) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, outgoingVO.getOutgoingStatus());
            pstmt.setString(2, outgoingVO.getOutgoingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt.setInt(3, outgoingVO.getOutgoingCnt());
            pstmt.setLong(4, outgoingVO.getShopPurchaseDetailSeq());
            pstmt.setLong(5, outgoingVO.getShopPurchaseSeq());
            pstmt.setString(6, outgoingVO.getProductCd());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("출고 등록 실패");
            }

            String updateStatusSql = "UPDATE TB_OUTGOING_INST SET V_OUTGOING_INTS_STATUS = 'COMPLETE' WHERE PK_SHOP_PURCHASE_SEQ = ?";
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateStatusSql)) {
                updatePstmt.setLong(1, outgoingVO.getShopPurchaseSeq());
                updatePstmt.executeUpdate();
            }

            conn.commit();
            System.out.println("출고 등록이 성공적으로 추가되었습니다.");

        } catch (Exception e) {
            System.out.println("출고 등록 중 예외 발생: " + e.getMessage());
            if (conn != null) {
                try {
                    System.out.println("롤백 시도...");
                    conn.rollback();
                    System.out.println("롤백 완료");
                } catch (SQLException ex) {
                    System.out.println("롤백 중 예외 발생: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
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

}
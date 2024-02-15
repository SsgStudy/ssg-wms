package service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import vo.DetailedIncomingVO;
import vo.IncomingVO;

/**
 * The interface Incomig service.
 */
public interface IncomigService {

    /**
     * Gets all incoming products with details.
     *
     * @return the all incoming products with details
     */
    List<IncomingVO> getAllIncomingProductsWithDetails();

    /**
     * Update incoming product details.
     *
     * @param seq      the seq
     * @param zoneCode the zone code
     * @param count    the count
     * @param price    the price
     * @throws SQLException the sql exception
     */
    void updateIncomingProductDetails(long seq, String zoneCode, int count, int price) throws SQLException;

    /**
     * Approve incoming product.
     *
     * @param seq the seq
     * @throws Exception the exception
     */
    void approveIncomingProduct(long seq) throws Exception;

    /**
     * Gets incoming products by month.
     *
     * @param year  the year
     * @param month the month
     * @return the incoming products by month
     * @throws SQLException the sql exception
     */
    List<IncomingVO> getIncomingProductsByMonth(int year, int month) throws SQLException;

    /**
     * Gets incoming products by date range.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the incoming products by date range
     * @throws SQLException the sql exception
     */
    List<IncomingVO> getIncomingProductsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * Gets incoming product details with product info.
     *
     * @param seq the seq
     * @return the incoming product details with product info
     * @throws Exception the exception
     */
    DetailedIncomingVO getIncomingProductDetailsWithProductInfo(long seq) throws Exception;
}

package service;

import java.time.LocalDateTime;
import java.util.List;
import vo.InventoryVO;
import vo.OutgoingInstVO;
import vo.OutgoingProductVO;
import vo.OutgoingVO;

/**
 * The interface Outgoing service.
 */
public interface OutgoingService {

    /**
     * Gets all outgoing insts.
     *
     * @return the all outgoing insts
     */
    List<OutgoingInstVO> getAllOutgoingInsts();

    /**
     * Add outgoing product.
     *
     * @param shopSeq the shop seq
     * @throws Exception the exception
     */
    void addOutgoingProduct(int shopSeq) throws Exception;

    /**
     * Update outgoing product status and date.
     *
     * @param pkOutgoingId the pk outgoing id
     * @param date         the date
     * @param status       the status
     * @throws Exception the exception
     */
    void updateOutgoingProductStatusAndDate(Long pkOutgoingId, LocalDateTime date, String status) throws Exception;

    /**
     * Gets outgoing product quantity.
     *
     * @param pkOutgoingId the pk outgoing id
     * @return the outgoing product quantity
     * @throws Exception the exception
     */
    int getOutgoingProductQuantity(Long pkOutgoingId) throws Exception;

    /**
     * Gets inventory by product code and quantity.
     *
     * @param productCd the product cd
     * @param quantity  the quantity
     * @return the inventory by product code and quantity
     * @throws Exception the exception
     */
    List<InventoryVO> getInventoryByProductCodeAndQuantity(String productCd, int quantity) throws Exception;

    /**
     * Update outgoing product.
     *
     * @param pkOutgoingId the pk outgoing id
     * @param newQuantity  the new quantity
     * @param warehouseCd  the warehouse cd
     * @param zoneCd       the zone cd
     * @throws Exception the exception
     */
    void updateOutgoingProduct(Long pkOutgoingId, int newQuantity, String warehouseCd, String zoneCd) throws Exception;

    /**
     * Gets product code by outgoing id.
     *
     * @param pkOutgoingId the pk outgoing id
     * @return the product code by outgoing id
     * @throws Exception the exception
     */
    String getProductCodeByOutgoingId(Long pkOutgoingId) throws Exception;

    /**
     * Gets all outgoings.
     *
     * @return the all outgoings
     * @throws Exception the exception
     */
    List<OutgoingVO> getAllOutgoings() throws Exception;

    /**
     * Gets outgoing product row by outgoing id.
     *
     * @param pkOutgoingId the pk outgoing id
     * @return the outgoing product row by outgoing id
     * @throws Exception the exception
     */
    OutgoingProductVO getOutgoingProductRowByOutgoingId(Long pkOutgoingId) throws Exception;
}

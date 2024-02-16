package service;

import util.enumcollect.PurchaseEnum;
import vo.PurchaseVO;

import java.util.List;

/**
 * The interface Purchase service.
 */
public interface PurchaseService {

    /**
     * Integrate shop purchases list.
     *
     * @param date      the date
     * @param shopIdxes the shop idxes
     * @return the list
     */
    List<Long> integrateShopPurchases(String date, List<Integer> shopIdxes);

    /**
     * Update purchase to confirmed int.
     *
     * @param selectedPurchaseSeq the selected purchase seq
     * @return the int
     */
    int updatePurchaseToConfirmed(List<Long> selectedPurchaseSeq);

    /**
     * Process purchase to cancel or return string.
     *
     * @param purchaseSeq the purchase seq
     * @return the string
     */
    String processPurchaseToCancelOrReturn(Long purchaseSeq);

    /**
     * Read all purchases list.
     *
     * @return the list
     */
    List<PurchaseVO> readAllPurchases();

    /**
     * Integrate shop claims list.
     *
     * @param date      the date
     * @param shopIdxes the shop idxes
     * @return the list
     */
    List<Long> integrateShopClaims(String date, List<Integer> shopIdxes);

    /**
     * Gets shoppingmall list.
     *
     * @return the shoppingmall list
     */
    List<String> getShoppingmallList();

    /**
     * Gets purchase list by purchase seq.
     *
     * @param shopPurchaseSeqList the shop purchase seq list
     * @return the purchase list by purchase seq
     */
    List<PurchaseVO> getPurchaseListByPurchaseSeq(List<Long> shopPurchaseSeqList);

    /**
     * Gets purchase claim list by purchase seq.
     *
     * @param claimSeqList the claim seq list
     * @return the purchase claim list by purchase seq
     */
    List<PurchaseVO> getPurchaseClaimListByPurchaseSeq(List<Long> claimSeqList);

    /**
     * Update new purchase status list.
     *
     * @param shopPurchaseSeqList the shop purchase seq list
     * @return the list
     */
    List<Long> updateNewPurchaseStatus(List<Long> shopPurchaseSeqList);

    /**
     * Create purchase return long.
     *
     * @param purchaseSeq the purchase seq
     * @return the long
     */
    Long createPurchaseReturn(Long purchaseSeq);

    /**
     * Update purchase status to return int.
     *
     * @param purchaseSeq  the purchase seq
     * @param returnStatus the return status
     * @return the int
     */
    int updatePurchaseStatusToReturn(Long purchaseSeq, PurchaseEnum returnStatus);

    /**
     * Update purchase status ex int.
     *
     * @param purchaseSeq    the purchase seq
     * @param purchaseStatus the purchase status
     * @return the int
     */
    int updatePurchaseStatusEx(Long purchaseSeq, PurchaseEnum purchaseStatus);
}

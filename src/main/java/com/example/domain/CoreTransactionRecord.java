package com.example.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class CoreTransactionRecord {

    @Id
    @GeneratedValue
    private Long id;

    /**股票/公司代码**/
    @Column(name = "code", nullable = false, length = 6)
    private String code;

    /**记录日期**/
    @Column(name = "record_date", nullable = false)
    private Date recordDate;

    /**开盘价(单位为分)**/
    @Column(name = "opening_price", nullable = false)
    private Integer openingPrice;

    /**收盘价(单位为分)**/
    @Column(name = "closing_price", nullable = false)
    private Integer closingPrice;

    /**最高价(单位为分)**/
    @Column(name = "highest", nullable = false)
    private Integer highest;

    /**最低价(单位为分)**/
    @Column(name = "lowest", nullable = false)
    private Integer lowest;

    /**成交量**/
    @Column(name = "transaction_volume", nullable = false)
    private Long transactionVolume;

    /**成交量**/
    @Column(name = "transaction_amount", nullable = false)
    private Long transactionAmount;

    /**波动金额**/
    @Column(name = "fluctuation_amount", nullable = false)
    private Integer fluctuationAmount;

    /**波动百分比**/
    @Column(name = "fluctuation_percentage", nullable = false)
    private Integer fluctuationPercentage;

    /**最大差额（高低差）**/
    @Column(name = "max_balance", nullable = false)
    private Integer maxBalance;

    /**创建时间**/
    @Column(name = "gmt_create", updatable = false)
    private Date gmtCreate;

    /**最后修改时间**/
    @Column(name = "gmt_modified")
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(Integer openingPrice) {
        this.openingPrice = openingPrice;
    }

    public Integer getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(Integer closingPrice) {
        this.closingPrice = closingPrice;
    }

    public Integer getHighest() {
        return highest;
    }

    public void setHighest(Integer highest) {
        this.highest = highest;
    }

    public Integer getLowest() {
        return lowest;
    }

    public void setLowest(Integer lowest) {
        this.lowest = lowest;
    }

    public Long getTransactionVolume() {
        return transactionVolume;
    }

    public void setTransactionVolume(Long transactionVolume) {
        this.transactionVolume = transactionVolume;
    }

    public Long getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Long transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Integer getFluctuationAmount() {
        return fluctuationAmount;
    }

    public void setFluctuationAmount(Integer fluctuationAmount) {
        this.fluctuationAmount = fluctuationAmount;
    }

    public Integer getFluctuationPercentage() {
        return fluctuationPercentage;
    }

    public void setFluctuationPercentage(Integer fluctuationPercentage) {
        this.fluctuationPercentage = fluctuationPercentage;
    }

    public Integer getMaxBalance() {
        return maxBalance;
    }

    public void setMaxBalance(Integer maxBalance) {
        this.maxBalance = maxBalance;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}

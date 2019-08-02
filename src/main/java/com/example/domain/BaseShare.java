package com.example.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import java.util.Date;

@Entity
public class BaseShare {

    @Id
    @TableGenerator(
        name = "AppSeqStore",
        initialValue = 0,
        allocationSize = 1 )
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "AppSeqStore")
    private Integer id;

    /**公司代码**/
    @Column(name = "code", nullable = false, length = 6)
    private String code;

    /**公司简称**/
    @Column(name = "name", nullable = false, length = 6)
    private String name;

    /**上市时间**/
    @Column(name = "public_date")
    private Date publicDate;

    /**总股本**/
    @Column(name = "total_stock", nullable = false)
    private long totalStock;

    /**流通股**/
    @Column(name = "floating_stock", nullable = false)
    private long floatingStock;

    /**创建时间**/
    @Column(name = "gmt_create", updatable = false)
    private Date gmtCreate;

    /**最后修改时间**/
    @Column(name = "gmt_modified")
    private Date gmtModified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getPublicDate() {
        return publicDate;
    }

    public void setPublicDate(Date publicDate) {
        this.publicDate = publicDate;
    }

    public long getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(long totalStock) {
        this.totalStock = totalStock;
    }

    public long getFloatingStock() {
        return floatingStock;
    }

    public void setFloatingStock(long floatingStock) {
        this.floatingStock = floatingStock;
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

    @Override
    public String toString() {
        return "BaseShare{" +
            "id=" + id +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", publicDate=" + publicDate +
            ", totalStock=" + totalStock +
            ", floatingStock=" + floatingStock +
            ", gmtCreate=" + gmtCreate +
            ", gmtModified=" + gmtModified +
            '}';
    }
}

package com.example.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.util.Date;

@Entity
@Table(name = "base_district")
public class BaseDistrict {

    @Id
    @Column( name = "id" )
    @TableGenerator(
        name = "AppSeqStore",
        initialValue = 0,
        allocationSize = 1 )
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "AppSeqStore")
    private Integer id;

    /**行政区划唯一标识**/
    @Column(name = "code", unique=true, nullable = false, length = 6)
    private String code;

    /**简称**/
    @Column(name = "name", length = 4)
    private String name;

    /**全称**/
    @Column(name = "fullname", nullable = false, length = 30)
    private String fullname;

    /**行政区划拼音**/
    @Column(name = "pinyin", length = 50)
    private String pinyin;

    /**经度**/
    @Column(name = "lng", nullable = false, precision = 15, scale = 12)
    private Double lng;

    /**纬度**/
    @Column(name = "lat", nullable = false, precision = 15, scale = 12)
    private Double lat;

    /**纬度**/
    @Column(name = "level", nullable = false)
    private Integer level;

    /**纬度**/
    @Column(name = "parent_id", nullable = false)
    private Integer parentId;

    /**创建时间**/
    @Column(name = "gmt_create", updatable = false)
    private Date gmtCreate;

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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}

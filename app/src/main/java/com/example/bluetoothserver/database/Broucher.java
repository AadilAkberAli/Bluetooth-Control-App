package com.example.bluetoothserver.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Broucher")
public class Broucher {

    @PrimaryKey(autoGenerate = true)
    public int mId;

    public String division;
    public Integer divisionId;
    public String divisionName;
    public String filesName;
    public String filesPath;
    public String group;
    public Integer groupId;
    public String groupName;
    public Integer id;
    public Boolean isMobileDownload;
    public String productMaster;
    public Integer productMasterId;
    public String productName;
    public Boolean exist;
    public String expiryDate;
    public Boolean isFavorite;
    public Boolean isMerged;

    public Broucher(String division, Integer divisionId, String divisionName, String filesName,
                    String filesPath, String group, Integer groupId, String groupName, Integer id,
                    Boolean isMobileDownload, String productMaster, Integer productMasterId,
                    String productName, Boolean exist, String expiryDate, boolean isFavorite, boolean isMerged) {
        this.division = division;
        this.divisionId = divisionId;
        this.divisionName = divisionName;
        this.filesName = filesName;
        this.filesPath = filesPath;
        this.group = group;
        this.groupId = groupId;
        this.groupName = groupName;
        this.id = id;
        this.isMobileDownload = isMobileDownload;
        this.productMaster = productMaster;
        this.productMasterId = productMasterId;
        this.productName = productName;
        this.exist = exist;
        this.expiryDate = expiryDate;
        this.isFavorite = isFavorite;
        this.isMerged = isMerged;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public Integer getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Integer divisionId) {
        this.divisionId = divisionId;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public String getFilesName() {
        return filesName;
    }

    public void setFilesName(String filesName) {
        this.filesName = filesName;
    }

    public String getFilesPath() {
        return filesPath;
    }

    public void setFilesPath(String filesPath) {
        this.filesPath = filesPath;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getMobileDownload() {
        return isMobileDownload;
    }

    public void setMobileDownload(Boolean mobileDownload) {
        isMobileDownload = mobileDownload;
    }

    public String getProductMaster() {
        return productMaster;
    }

    public void setProductMaster(String productMaster) {
        this.productMaster = productMaster;
    }

    public Integer getProductMasterId() {
        return productMasterId;
    }

    public void setProductMasterId(Integer productMasterId) {
        this.productMasterId = productMasterId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Boolean getExist() {
        return exist;
    }

    public void setExist(Boolean exist) {
        this.exist = exist;
    }

    public Boolean getMerged() {
        return isMerged;
    }

    public void setMerged(Boolean merged) {
        isMerged = merged;
    }
}

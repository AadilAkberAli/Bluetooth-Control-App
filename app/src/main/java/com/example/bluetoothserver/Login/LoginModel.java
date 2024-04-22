package com.example.bluetoothserver.Login;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LoginModel implements Serializable {
    @SerializedName("success")
    private Boolean success;
    @SerializedName("data")
    private Data data;
    @SerializedName("message")
    private String message;
    @SerializedName("detailsMessage")
    private Object detailsMessage;

    public class Data implements Serializable {
        @SerializedName("userId")
        private Integer userId;
        @SerializedName("roleId")
        private Integer roleId;
        @SerializedName("designationId")
        private Integer designationId;
        @SerializedName("password")
        private String password;
        @SerializedName("designation")
        private String designation;
        @SerializedName("regionId")
        private Integer regionId;
        @SerializedName("regionName")
        private String regionName;
        @SerializedName("subRegionId")
        private Integer subRegionId;
        @SerializedName("subRegionName")
        private String subRegionName;
        @SerializedName("areaId")
        private Integer areaId;
        @SerializedName("areaName")
        private String areaName;
        @SerializedName("subAreaId")
        private Integer subAreaId;
        @SerializedName("subAreaName")
        private String subAreaName;
        @SerializedName("cityId")
        private Integer cityId;
        @SerializedName("cityName")
        private String cityName;
        @SerializedName("firstName")
        private String firstName;
        @SerializedName("lastName")
        private String lastName;
        @SerializedName("userName")
        private String userName;
        @SerializedName("email")
        private String email;
        @SerializedName("mobileNo")
        private String mobileNo;
        @SerializedName("reportToId")
        private Integer reportToId;
        @SerializedName("reportTo")
        private String reportTo;
        @SerializedName("managerId")
        private ArrayList<Integer> managerId;
        @SerializedName("token")
        private String token;
        @SerializedName("userGroupList")
        private ArrayList<UserGroupList> userGroupList;
        @SerializedName("userTerritoryList")
        private ArrayList<UserTerritoryList> userTerritoryList;
        @SerializedName("isEmailUpdated")
        private int isEmailUpdated;

        public Integer getReportToId() {
            return reportToId == null ? 0 : reportToId;
        }

        public void setReportToId(Integer reportToId) {
            this.reportToId = reportToId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getRoleId() {
            return roleId;
        }

        public void setRoleId(Integer roleId) {
            this.roleId = roleId;
        }

        public Integer getDesignationId() {
            return designationId;
        }

        public void setDesignationId(Integer designationId) {
            this.designationId = designationId;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public Integer getRegionId() {
            return regionId;
        }

        public void setRegionId(Integer regionId) {
            this.regionId = regionId;
        }

        public String getRegionName() {
            return regionName;
        }

        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        public Integer getSubRegionId() {
            return subRegionId;
        }

        public void setSubRegionId(Integer subRegionId) {
            this.subRegionId = subRegionId;
        }

        public String getSubRegionName() {
            return subRegionName;
        }

        public void setSubRegionName(String subRegionName) {
            this.subRegionName = subRegionName;
        }

        public Integer getAreaId() {
            return areaId;
        }

        public void setAreaId(Integer areaId) {
            this.areaId = areaId;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public Integer getSubAreaId() {
            return subAreaId;
        }

        public void setSubAreaId(Integer subAreaId) {
            this.subAreaId = subAreaId;
        }

        public String getSubAreaName() {
            return subAreaName;
        }

        public void setSubAreaName(String subAreaName) {
            this.subAreaName = subAreaName;
        }

        public Integer getCityId() {
            return cityId;
        }

        public void setCityId(Integer cityId) {
            this.cityId = cityId;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getReportTo() {
            return reportTo;
        }

        public void setReportTo(String reportTo) {
            this.reportTo = reportTo;
        }

        public ArrayList<Integer> getManagerId() {
            return managerId;
        }

        public void setManagerId(ArrayList<Integer> managerId) {
            this.managerId = managerId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public ArrayList<UserGroupList> getUserGroupList() {
            return userGroupList;
        }

        public void setUserGroupList(ArrayList<UserGroupList> userGroupList) {
            this.userGroupList = userGroupList;
        }

        public ArrayList<UserTerritoryList> getUserTerritoryList() {
            return userTerritoryList;
        }

        public void setUserTerritoryList(ArrayList<UserTerritoryList> userTerritoryList) {
            this.userTerritoryList = userTerritoryList;
        }

        public int getIsEmailUpdated() {
            return isEmailUpdated;
        }

        public void setIsEmailUpdated(int isEmailUpdated) {
            this.isEmailUpdated = isEmailUpdated;
        }
    }

    public class StaffList implements Serializable{
        @SerializedName("employeeId")
        private String employeeId;
        @SerializedName("employeeCode")
        private String employeeCode;
        @SerializedName("employeeName")
        private String employeeName;
        @SerializedName("designation")
        private String designation;

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getEmployeeCode() {
            return employeeCode;
        }

        public void setEmployeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }
    }

    public class UserGroupList implements Serializable
    {
        @SerializedName("divisionId")
        private Integer divisionId;
        @SerializedName("divisionName")
        private String divisionName;
        @SerializedName("groupId")
        private Integer groupId;
        @SerializedName("groupName")
        private String groupName;
        private final static long serialVersionUID = 7037865817411779682L;

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

    }

    public class UserTerritoryList implements Serializable
    {
        @SerializedName("territoryId")
        private Integer territoryId;
        @SerializedName("territoryName")
        private String territoryName;
        private final static long serialVersionUID = -3373647248105615114L;

        public Integer getTerritoryId() {
            return territoryId;
        }

        public void setTerritoryId(Integer territoryId) {
            this.territoryId = territoryId;
        }

        public String getTerritoryName() {
            return territoryName;
        }

        public void setTerritoryName(String territoryName) {
            this.territoryName = territoryName;
        }

    }


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getDetailsMessage() {
        return detailsMessage;
    }

    public void setDetailsMessage(Object detailsMessage) {
        this.detailsMessage = detailsMessage;
    }
}

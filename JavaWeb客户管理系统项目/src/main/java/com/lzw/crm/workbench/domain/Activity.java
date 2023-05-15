package com.lzw.crm.workbench.domain;

/**
 * 市场活动表
 */
public class Activity {
    //主键
    private String id;

    //市场活动的所有者：是所有用户中的一个，在表中的类型是char(32)，是一个UUID，也是一个外键，关联用户表的主键
    private String owner;

    //市场活动名
    private String name;

    //市场活动的开始日期，表中类型是char(10)，说明只有年月日
    private String startDate;

    //市场活动结束日期，表中类型是char(10)，说明只有年月日
    private String endDate;

    //市场活动的成本
    private String cost;

    //市场活动描述
    private String description;

    //当前对象对应的表记录的创建时间，数据库中的类型是char(19)
    private String createTime;

    //当前对象对应的表记录的创建者
    private String createBy;

    //当前对象对应的表记录的修改时间，数据库中的类型是char(19)
    private String editTime;

    //当前对象对应的表记录的修改者
    private String editBy;

    public Activity() {

    }

    public Activity(String id, String owner, String name, String startDate, String endDate, String cost,
                    String description, String createTime, String createBy, String editTime, String editBy) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.description = description;
        this.createTime = createTime;
        this.createBy = createBy;
        this.editTime = editTime;
        this.editBy = editBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getEditTime() {
        return editTime;
    }

    public void setEditTime(String editTime) {
        this.editTime = editTime;
    }

    public String getEditBy() {
        return editBy;
    }

    public void setEditBy(String editBy) {
        this.editBy = editBy;
    }
}
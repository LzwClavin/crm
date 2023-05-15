package com.lzw.crm.workbench.domain;

public class Tran {

    private String id;//主键
    private String owner;//外键，用户表id
    private String money;    //交易金额
    private String name;    //交易名称
    private String expectedDate;    //预计成交日期，10位日期
    private String customerId;//外键，客户id
    private String stage;    //交易阶段，其中资质审查到成交是交易的正常阶段，丢失的线索和因竞争丢失关闭是交易失败的阶段
    private String type;    //交易类型
    private String source;    //交易来源
    private String activityId;//外键，市场活动id
    private String contactsId;//外键，联系人id
    private String createBy;//创建者
    private String createTime;//创建时间
    private String editBy;//修改者
    private String editTime;//修改时间
    private String description;//描述
    private String contactSummary;    //联系纪要
    private String nextContactTime;    //下次联系时间

    /*
    扩展可能性属性，给前端使用；如果创建一个vo，保存原生的交易对象和可能性，开销较大，因为仅扩展了一个字段
    如果扩充的字段较多，再考虑使用vo
    */
    private String possibility;

    public String getPossibility() {
        return possibility;
    }

    public void setPossibility(String possibility) {
        this.possibility = possibility;
    }

    public Tran() {

    }

    public Tran(String id, String owner, String money, String name, String expectedDate, String customerId,
                String stage, String type, String source, String activityId, String contactsId, String createBy,
                String createTime, String editBy, String editTime, String description, String contactSummary,
                String nextContactTime) {
        this.id = id;
        this.owner = owner;
        this.money = money;
        this.name = name;
        this.expectedDate = expectedDate;
        this.customerId = customerId;
        this.stage = stage;
        this.type = type;
        this.source = source;
        this.activityId = activityId;
        this.contactsId = contactsId;
        this.createBy = createBy;
        this.createTime = createTime;
        this.editBy = editBy;
        this.editTime = editTime;
        this.description = description;
        this.contactSummary = contactSummary;
        this.nextContactTime = nextContactTime;
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

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(String expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getContactsId() {
        return contactsId;
    }

    public void setContactsId(String contactsId) {
        this.contactsId = contactsId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEditBy() {
        return editBy;
    }

    public void setEditBy(String editBy) {
        this.editBy = editBy;
    }

    public String getEditTime() {
        return editTime;
    }

    public void setEditTime(String editTime) {
        this.editTime = editTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactSummary() {
        return contactSummary;
    }

    public void setContactSummary(String contactSummary) {
        this.contactSummary = contactSummary;
    }

    public String getNextContactTime() {
        return nextContactTime;
    }

    public void setNextContactTime(String nextContactTime) {
        this.nextContactTime = nextContactTime;
    }


}
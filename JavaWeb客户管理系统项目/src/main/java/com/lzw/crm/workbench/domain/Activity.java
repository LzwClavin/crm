package com.lzw.crm.workbench.domain;

/**
 * �г����
 */
public class Activity {
    //����
    private String id;

    //�г���������ߣ��������û��е�һ�����ڱ��е�������char(32)����һ��UUID��Ҳ��һ������������û��������
    private String owner;

    //�г����
    private String name;

    //�г���Ŀ�ʼ���ڣ�����������char(10)��˵��ֻ��������
    private String startDate;

    //�г���������ڣ�����������char(10)��˵��ֻ��������
    private String endDate;

    //�г���ĳɱ�
    private String cost;

    //�г������
    private String description;

    //��ǰ�����Ӧ�ı��¼�Ĵ���ʱ�䣬���ݿ��е�������char(19)
    private String createTime;

    //��ǰ�����Ӧ�ı��¼�Ĵ�����
    private String createBy;

    //��ǰ�����Ӧ�ı��¼���޸�ʱ�䣬���ݿ��е�������char(19)
    private String editTime;

    //��ǰ�����Ӧ�ı��¼���޸���
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
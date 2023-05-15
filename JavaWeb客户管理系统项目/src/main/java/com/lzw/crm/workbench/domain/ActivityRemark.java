package com.lzw.crm.workbench.domain;

/**
 * 市场互动备注表
 */
public class ActivityRemark {
    //主键
    private String id;

    //备注信息
    private String noteContent;

    //当前对象对应的表记录的创建时间，数据库中的类型是char(19)
    private String createTime;

    //当前对象对应的表记录的创建者
    private String createBy;

    //当前对象对应的表记录的修改时间，数据库中的类型是char(19)
    private String editTime;

    //当前对象对应的表记录的修改者
    private String editBy;

    //标记，标记该备注是否被修改过，表中类型是char(1)，这里1表示被修改过，0表示没有被修改过
    private String editFlag;

    //外键，表中类型是char(32)，是一个UUID，关联市场活动表的主键
    private String activityId;

    public ActivityRemark() {

    }

    public ActivityRemark(String id, String noteContent, String createTime, String createBy, String editTime,
                          String editBy, String editFlag, String activityId) {
        this.id = id;
        this.noteContent = noteContent;
        this.createTime = createTime;
        this.createBy = createBy;
        this.editTime = editTime;
        this.editBy = editBy;
        this.editFlag = editFlag;
        this.activityId = activityId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
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

    public String getEditFlag() {
        return editFlag;
    }

    public void setEditFlag(String editFlag) {
        this.editFlag = editFlag;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
}
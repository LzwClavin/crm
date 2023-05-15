package com.lzw.crm.workbench.domain;

/**
 * �г�������ע��
 */
public class ActivityRemark {
    //����
    private String id;

    //��ע��Ϣ
    private String noteContent;

    //��ǰ�����Ӧ�ı��¼�Ĵ���ʱ�䣬���ݿ��е�������char(19)
    private String createTime;

    //��ǰ�����Ӧ�ı��¼�Ĵ�����
    private String createBy;

    //��ǰ�����Ӧ�ı��¼���޸�ʱ�䣬���ݿ��е�������char(19)
    private String editTime;

    //��ǰ�����Ӧ�ı��¼���޸���
    private String editBy;

    //��ǣ���Ǹñ�ע�Ƿ��޸Ĺ�������������char(1)������1��ʾ���޸Ĺ���0��ʾû�б��޸Ĺ�
    private String editFlag;

    //���������������char(32)����һ��UUID�������г���������
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
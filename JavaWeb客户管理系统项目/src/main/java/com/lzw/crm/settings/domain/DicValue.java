package com.lzw.crm.settings.domain;

/**
 * �ֵ�ֵ��
 */
public class DicValue {
    //����
    private String id;

    //��ʾ���ӵ������б���ѡ��ť��ѡ���valueֵ
    private String value;

    //��ʾ���ӵ������б���ѡ��ť��ѡ����ı�����
    private String text;

    //����ţ�����ǰ�ֵ�ֵ���ڵ������б���ѡ��ť��ѡ���е�λ�����
    private String orderNo;

    //����������ֵ����ͱ������code
    private String typeCode;

    public DicValue() {

    }

    public DicValue(String id, String value, String text, String orderNo, String typeCode) {
        this.id = id;
        this.value = value;
        this.text = text;
        this.orderNo = orderNo;
        this.typeCode = typeCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
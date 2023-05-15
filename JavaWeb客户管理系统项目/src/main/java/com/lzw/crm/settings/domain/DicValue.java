package com.lzw.crm.settings.domain;

/**
 * 字典值表
 */
public class DicValue {
    //主键
    private String id;

    //表示连接的下拉列表、单选按钮或复选框的value值
    private String value;

    //表示连接的下拉列表、单选按钮或复选框的文本内容
    private String text;

    //排序号，即当前字典值所在的下拉列表、单选按钮或复选框中的位置序号
    private String orderNo;

    //外键，引用字典类型表的主键code
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
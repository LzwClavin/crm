package com.lzw.crm.settings.domain;

/**
 * �ֵ����ͱ�
 */
public class DicType {
    /*
    ��������ʹ��32λ��UUID��ʹ���ֵ����ͱ��룬��name�ֶε�Ӣ�ı�ʾ��ʽ
    ʵ�ʿ�����Ҳ��ʹ����������ֶ���Ϊ������ֻ�Ǹ��ֶ���һ����ʶ����������ÿ�м�¼�����ʣ���ҵ�񲻹ҹ�
    �����Ϲ���ʱ����һ��������¼�����Խ����ɵĶ�������Ϊ��������Ϊ�����űض��Ƿǿպ�Ψһ��
    */
    private String code;

    //�ֵ�������
    private String name;

    //�ֵ���������
    private String description;

    public DicType() {

    }

    public DicType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
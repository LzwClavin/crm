package com.lzw.crm.settings.domain;

/**
 * 字典类型表
 */
public class DicType {
    /*
    主键，不使用32位的UUID，使用字典类型编码，是name字段的英文表示形式
    实际开发中也会使用有意义的字段作为主键，只是该字段是一个标识，用以区分每行记录的特质，与业务不挂钩
    如网上购物时生成一个订单记录，可以将生成的订单号作为主键，因为订单号必定是非空和唯一的
    */
    private String code;

    //字典类型名
    private String name;

    //字典类型描述
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
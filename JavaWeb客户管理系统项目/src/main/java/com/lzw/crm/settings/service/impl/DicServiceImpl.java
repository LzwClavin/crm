package com.lzw.crm.settings.service.impl;

import com.lzw.crm.settings.dao.*;
import com.lzw.crm.settings.domain.*;
import com.lzw.crm.settings.service.*;
import com.lzw.crm.utils.*;

import java.util.*;

public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    @Override
    public Map<String, List<DicValue>> getAll() {
        //定义需要返回给监听器的数据字典map
        Map<String, List<DicValue>> dictionary = new HashMap<>();

        //获取字典类型列表
        List<DicType> types = dicTypeDao.getTypes();

        //将字典类型列表遍历
        for (DicType type : types) {
            //获取每个字典类型的主键编码
            String code = type.getCode();

            //根据每个字典类型编码获取对应的字典值列表
            List<DicValue> values = dicValueDao.getValues(code);

            //将每个字典类型编码及其对应的字典值列表封装到map中
            dictionary.put(code, values);
        }

        return dictionary;
    }
}
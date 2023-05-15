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
        //������Ҫ���ظ��������������ֵ�map
        Map<String, List<DicValue>> dictionary = new HashMap<>();

        //��ȡ�ֵ������б�
        List<DicType> types = dicTypeDao.getTypes();

        //���ֵ������б����
        for (DicType type : types) {
            //��ȡÿ���ֵ����͵���������
            String code = type.getCode();

            //����ÿ���ֵ����ͱ����ȡ��Ӧ���ֵ�ֵ�б�
            List<DicValue> values = dicValueDao.getValues(code);

            //��ÿ���ֵ����ͱ��뼰���Ӧ���ֵ�ֵ�б��װ��map��
            dictionary.put(code, values);
        }

        return dictionary;
    }
}
package com.lzw.crm.workbench.test;

import com.lzw.crm.utils.*;
import com.lzw.crm.workbench.domain.*;
import com.lzw.crm.workbench.service.*;
import com.lzw.crm.workbench.service.impl.*;
import org.junit.*;

public class ActivityTest {
    /**
     * ��������г��
     */
    @Test
    public void testSave() {
        System.out.println("����г��");
        Activity acticity = new Activity();
        acticity.setId(UUIDUtil.getUUID());
        acticity.setName("������");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.save(acticity);
        System.out.println(flag);
        Assert.assertEquals(flag, true);

        //Assert.assertEquals(flag, false);
    }

    /**
     * �����޸��г��
     */
    @Test
    public void testUpdate() {
        System.out.println("�޸��г��");
        //int i = 1 / 0;
    }
}
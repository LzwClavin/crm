package com.lzw.crm.workbench.test;

import com.lzw.crm.utils.*;
import com.lzw.crm.workbench.domain.*;
import com.lzw.crm.workbench.service.*;
import com.lzw.crm.workbench.service.impl.*;
import org.junit.*;

public class ActivityTest {
    /**
     * 测试添加市场活动
     */
    @Test
    public void testSave() {
        System.out.println("添加市场活动");
        Activity acticity = new Activity();
        acticity.setId(UUIDUtil.getUUID());
        acticity.setName("拍卖会");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.save(acticity);
        System.out.println(flag);
        Assert.assertEquals(flag, true);

        //Assert.assertEquals(flag, false);
    }

    /**
     * 测试修改市场活动
     */
    @Test
    public void testUpdate() {
        System.out.println("修改市场活动");
        //int i = 1 / 0;
    }
}
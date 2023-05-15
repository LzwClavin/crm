package com.lzw.crm.workbench.service.impl;

import com.lzw.crm.utils.*;
import com.lzw.crm.workbench.dao.*;
import com.lzw.crm.workbench.domain.*;
import com.lzw.crm.workbench.service.*;

import java.util.*;

public class TranServiceImpl implements TranService {
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran tran, String customerName) {
        //初始化标记默认为添加成功
        boolean flag = true;

        //使用客户表根据客户名对客户表精确查询，如果客户存在，获取客户id，封装到tran中；否则新建客户，并获取新客户的id，封装到tran中
        Customer customer = customerDao.getCustomerByName(customerName);
        if (null == customer) {
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateTime(tran.getCreateTime());
            customer.setCreateBy(tran.getCreateBy());

            //可以从tran中获取一些与customer匹配的信息填充到customer中；对于未能添加的信息，需要用户在客户模块中自行补全
            customer.setOwner(tran.getOwner());
            customer.setDescription(tran.getDescription());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());
            int count = customerDao.save(customer);

            //如果添加失败，返回0，设置标记为false
            if (count != 1) {
                flag = false;
            }
        }

        //程序执行到此，说明客户必定已经存在，获取客户id封装到交易对象中
        String customerId = customer.getId();
        tran.setCustomerId(customerId);

        //添加交易记录
        int count = tranDao.save(tran);
        if (count != 1) {
            flag = false;
        }

        //使用交易历史表添加交易历史记录，交易历史对象中的数据都从交易对象中获取
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateTime(tran.getCreateTime());
        tranHistory.setCreateBy(tran.getCreateBy());
        tranHistory.setTranId(tran.getId());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setStage(tran.getStage());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        count = tranHistoryDao.save(tranHistory);
        if (count != 1) {
            flag = false;
        }

        return flag;//如果在添加过程中标记一直保持为true，说明添加顺利，返回的是true；一旦有一次添加失败，都会返回false
    }

    @Override
    public Tran detail(String id) {
        Tran tran = tranDao.detail(id);
        return tran;
    }

    @Override
    public List<TranHistory> getHistorysByTId(String tranId) {
        //使用交易历史表
        List<TranHistory> histories = tranHistoryDao.getHistorysByTId(tranId);
        return histories;
    }

    @Override
    public boolean changeStage(Tran tran) {
        //定义一个返回的影响记录条数，初始值为0
        int count = 0;

        //修改交易阶段，追加返回的影响记录条数，如果修改成功，追加1
        count += tranDao.changeStage(tran);

        //生成交易历史记录，不用生成可能性
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateBy(tran.getEditBy());//交易历史的创建者是交易的修改者，因为两个需求是同步进行的
        tranHistory.setCreateTime(tran.getEditTime());//交易历史的创建时间是交易的修改时间，因为两个需求是同步进行的
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setStage(tran.getStage());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setTranId(tran.getId());

        //添加交易历史记录，追加返回的影响记录条数，如果添加成功，追加1
        count += tranHistoryDao.save(tranHistory);
        return count == 2;//如果最终的count是2，表明上面的两个操作都成功，返回true，否则返回false
    }

    @Override
    public Map<String, Object> getCharts() {
        //获取total
        int total = tranDao.getTotal();

        /*
        获取统计数据，这是一个列表，列表中的每个map都存储了”name=某个阶段名“和”value=某个阶段名的数量“
        最终生成JSON的格式是“[{name:stage1,value:count1},{name:stage2,value:count2},...]”供前端识别
        */
        List<Map<String, Object>> datas = tranDao.getCharts();

        //将total和统计数据封装到map中并返回
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("datas", datas);
        return map;
    }
}
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
        //��ʼ�����Ĭ��Ϊ��ӳɹ�
        boolean flag = true;

        //ʹ�ÿͻ�����ݿͻ����Կͻ���ȷ��ѯ������ͻ����ڣ���ȡ�ͻ�id����װ��tran�У������½��ͻ�������ȡ�¿ͻ���id����װ��tran��
        Customer customer = customerDao.getCustomerByName(customerName);
        if (null == customer) {
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateTime(tran.getCreateTime());
            customer.setCreateBy(tran.getCreateBy());

            //���Դ�tran�л�ȡһЩ��customerƥ�����Ϣ��䵽customer�У�����δ����ӵ���Ϣ����Ҫ�û��ڿͻ�ģ�������в�ȫ
            customer.setOwner(tran.getOwner());
            customer.setDescription(tran.getDescription());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());
            int count = customerDao.save(customer);

            //������ʧ�ܣ�����0�����ñ��Ϊfalse
            if (count != 1) {
                flag = false;
            }
        }

        //����ִ�е��ˣ�˵���ͻ��ض��Ѿ����ڣ���ȡ�ͻ�id��װ�����׶�����
        String customerId = customer.getId();
        tran.setCustomerId(customerId);

        //��ӽ��׼�¼
        int count = tranDao.save(tran);
        if (count != 1) {
            flag = false;
        }

        //ʹ�ý�����ʷ����ӽ�����ʷ��¼��������ʷ�����е����ݶ��ӽ��׶����л�ȡ
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

        return flag;//�������ӹ����б��һֱ����Ϊtrue��˵�����˳�������ص���true��һ����һ�����ʧ�ܣ����᷵��false
    }

    @Override
    public Tran detail(String id) {
        Tran tran = tranDao.detail(id);
        return tran;
    }

    @Override
    public List<TranHistory> getHistorysByTId(String tranId) {
        //ʹ�ý�����ʷ��
        List<TranHistory> histories = tranHistoryDao.getHistorysByTId(tranId);
        return histories;
    }

    @Override
    public boolean changeStage(Tran tran) {
        //����һ�����ص�Ӱ���¼��������ʼֵΪ0
        int count = 0;

        //�޸Ľ��׽׶Σ�׷�ӷ��ص�Ӱ���¼����������޸ĳɹ���׷��1
        count += tranDao.changeStage(tran);

        //���ɽ�����ʷ��¼���������ɿ�����
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateBy(tran.getEditBy());//������ʷ�Ĵ������ǽ��׵��޸��ߣ���Ϊ����������ͬ�����е�
        tranHistory.setCreateTime(tran.getEditTime());//������ʷ�Ĵ���ʱ���ǽ��׵��޸�ʱ�䣬��Ϊ����������ͬ�����е�
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setStage(tran.getStage());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setTranId(tran.getId());

        //��ӽ�����ʷ��¼��׷�ӷ��ص�Ӱ���¼�����������ӳɹ���׷��1
        count += tranHistoryDao.save(tranHistory);
        return count == 2;//������յ�count��2����������������������ɹ�������true�����򷵻�false
    }

    @Override
    public Map<String, Object> getCharts() {
        //��ȡtotal
        int total = tranDao.getTotal();

        /*
        ��ȡͳ�����ݣ�����һ���б��б��е�ÿ��map���洢�ˡ�name=ĳ���׶������͡�value=ĳ���׶�����������
        ��������JSON�ĸ�ʽ�ǡ�[{name:stage1,value:count1},{name:stage2,value:count2},...]����ǰ��ʶ��
        */
        List<Map<String, Object>> datas = tranDao.getCharts();

        //��total��ͳ�����ݷ�װ��map�в�����
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("datas", datas);
        return map;
    }
}
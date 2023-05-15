package com.lzw.crm.workbench.service.impl;

import com.lzw.crm.utils.*;
import com.lzw.crm.workbench.dao.*;
import com.lzw.crm.workbench.domain.*;
import com.lzw.crm.workbench.service.*;

import java.util.*;

public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean save(Clue clue) {
        int count = clueDao.save(clue);
        return count == 1;
    }

    @Override
    public Clue detail(String id) {
        Clue clue = clueDao.detail(id);
        return clue;
    }

    @Override
    public boolean unbund(String id) {
        //dao��һ�ű��Ӧһ���ӿں�һ��xml����Ϊ�ǲ�����ϵ�����Ա���ʹ�ù�ϵ���dao�ӿ�
        int count = clueActivityRelationDao.unbund(id);
        return count == 1;
    }

    @Override
    public boolean bund(String cid, String[] aids) {
        boolean flag = true;

        //�����г��id���飬ȡ��ÿ���г��id���ֱ�������id����
        for (String aid : aids) {
            //����һ��UUID��Ϊ��ϵ���id
            String id = UUIDUtil.getUUID();

            //��Ϊ������ϵ������һ����¼��������Ҫʹ�õ���ϵ���Ӧ��domain����ʼ�����ԣ�����dao�㴫�ݹ�ϵ���������
            ClueActivityRelation clueActivityRelation = new ClueActivityRelation(id, cid, aid);

            /*
            dao��һ�ű��Ӧһ���ӿں�һ��xml����Ϊ�ǲ�����ϵ�����Ա���ʹ�ù�ϵ���dao�ӿ�
            ����һ��ѭ����ӵĹ��̣�ÿ����һ����ϵ����󣬾�Ҫ���һ����ϵ���¼�����Ե���dao�����ѭ������
            */
            int count = clueActivityRelationDao.bund(clueActivityRelation);

            /*
            ����ʹ�ò�������ж����������ǳɹ���ʧ�ܣ����ʹ��count��ÿ����ӳɹ�������1������ж�ʱֻ���ж����һ����ӳɹ�
            ������ȷ��������Ӷ��ɹ���ʹ�ò�����Ǻ󣬳�ʼֵ��true��һ�����ʧ�ܣ�count����0���޸ı��Ϊfalse�����շ���false
            ֻ��������Ӷ��ɹ���count����1�����ܱ��ֱ�ǵ�ֵ��true�����շ���true
            */
            if (count != 1) {
                flag = false;
            }
        }

        return flag;
    }

    @Override
    public boolean convert(String cid, Tran tran, String createBy) {
        //���ɹ��õĴ�����¼��ʱ�䣬���ⲻ�����ɹ���id��id����ÿ����¼��������һ����ͬ��id
        String createTime = DateTimeUtil.getSysTime();

        //����һ������ֵ��Ĭ��ת���ɹ�
        boolean flag = true;

        //��������id��ȡ������Ϣ������һ����������
        Clue clue = clueDao.getById(cid);

        /*
        ͨ������������ȡ���е���ʵ�ͻ���Ϣ�������еĹ�˾������ȷƥ�䣬����ͻ������ڣ��½��ͻ����������½�
        �ͻ���������Ϊ��˾�й���ͻ����ŶӺ͵��г���������ƹ㡢�ռ��������Ŷ�֮��Ľ����п��ܲ�����һЩ��Ϣ����
        �ͻ��͹�˾�п��������й����ף����ռ��������ŶӲ�����иÿͻ��Ĵ��ڣ����ÿͻ�������һ����������
        */
        String company = clue.getCompany();

        //ʹ�ÿͻ���ͨ����˾����ѯ�ͻ���¼
        Customer customer = customerDao.getCustomerByName(company);

        /*
        ������صĿͻ���¼�����ڣ���Ҫ�����ͻ������½��ͻ��������е����ݴ����������л�ȡ����������ת���������޸�ʱ����޸��߶���Ҫ��ȡ
        ���ڻ�ȡ���������ݣ����Ե��ͻ�ģ���б༭����
        */
        if (null == customer) {
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(company);//ע�������ǻ�ȡ���Ĺ�˾��
            customer.setCreateTime(createTime);
            customer.setCreateBy(createBy);
            customer.setOwner(clue.getOwner());
            customer.setPhone(clue.getPhone());
            customer.setAddress(clue.getAddress());
            customer.setWebsite(clue.getWebsite());
            customer.setDescription(clue.getDescription());
            customer.setContactSummary(clue.getContactSummary());
            customer.setNextContactTime(clue.getNextContactTime());

            //��ӿͻ���¼
            int count = customerDao.save(customer);

            //������ʧ�ܣ�����0�����ñ��Ϊfalse
            if (count != 1) {
                flag = false;
            }
        }

        /*
        ͨ������������ȡ���е���ϵ����Ϣ��������ϵ���Ƿ���ڣ���Ҫ�½���ϵ�ˣ���Ϊ��ϵ��ʹ�õ����˵�ȫ���������������ظ���
        ����˾���������ظ�����ϵ�˴��ڵ�ԭ��ͬ�ϣ���ϵ�˶����е����ݴ����������л�ȡ����������ת���������޸�ʱ����޸��߶���Ҫ��ȡ
        ���ڻ�ȡ���������ݣ����Ե���ϵ��ģ���б༭����
        */
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setFullname(clue.getFullname());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setOwner(clue.getOwner());
        contacts.setMphone(clue.getMphone());
        contacts.setEmail(clue.getEmail());
        contacts.setAddress(clue.getAddress());
        contacts.setJob(clue.getJob());
        contacts.setCustomerId(customer.getId());//��д��ϵ����������ҵ��������Ŀͻ�id
        contacts.setAppellation(clue.getAppellation());
        contacts.setSource(clue.getSource());
        contacts.setDescription(clue.getDescription());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setNextContactTime(clue.getNextContactTime());

        //ʹ����ϵ�˱������ϵ�˼�¼
        int count = contactsDao.save(contacts);

        //������ʧ�ܣ�����0�����ñ��Ϊfalse
        if (count != 1) {
            flag = false;
        }

        //ʹ��������ע���ѯ��ǰ���������ı�ע��Ϣ�б�
        List<ClueRemark> clueRemarks = clueRemarkDao.getListByCId(cid);

        //������ע�б�
        for (ClueRemark clueRemark : clueRemarks) {
            /*
            ����noteContent������Ҫת������Ϊid�������ߺʹ���ʱ���Զ����ɣ��޸��ߺ��޸�ʱ��û�У�editFlagΪ0��clueId�Ѵ���
            ��������ֻ���ȡÿһ����ע��Ϣ����
            */
            String noteContent = clueRemark.getNoteContent();

            //�����ͻ���ע������ӿͻ���ע��¼
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setEditFlag("0");
            customerRemark.setCustomerId(customer.getId());//��д��ע��������ҵ��������Ŀͻ�id
            customerRemark.setNoteContent(noteContent);

            //ʹ�ÿͻ���ע����ӿͻ���ע��¼
            count = customerRemarkDao.save(customerRemark);

            //������ʧ�ܣ�����0�����ñ��Ϊfalse
            if (count != 1) {
                flag = false;
            }

            //������ϵ�˱�ע���������ϵ�˱�ע��¼
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setEditFlag("0");
            contactsRemark.setContactsId(contacts.getId());//��д��ע��������ϵ�ˣ����������ϵ��id
            contactsRemark.setNoteContent(noteContent);

            //ʹ�ÿͻ���ע����ӿͻ���ע��¼
            count = contactsRemarkDao.save(contactsRemark);

            //������ʧ�ܣ�����0�����ñ��Ϊfalse
            if (count != 1) {
                flag = false;
            }
        }

        //�������г���Ĺ�����ϵת������ϵ�˺��г���Ĺ�����ϵ
        //��ѯ����ǰ�����������г����ʹ�������г����ϵ���ѯ
        List<ClueActivityRelation> clueActivityRelations = clueActivityRelationDao.getListByCId(cid);

        //���������г����ϵ�б�
        for (ClueActivityRelation clueActivityRelation : clueActivityRelations) {
            //��ȡ�����г����ϵ�����е��г��id
            String activityId = clueActivityRelation.getActivityId();

            //������ϵ���г����ϵ���������ɵ���ϵ��id���ȡ�����г��id����
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setActivityId(activityId);

            //ʹ����ϵ���г����ϵ����ӹ�ϵ��¼
            count = contactsActivityRelationDao.save(contactsActivityRelation);

            //������ʧ�ܣ�����0�����ñ��Ϊfalse
            if (count != 1) {
                flag = false;
            }
        }

        /*
        ����������������Ľ��׶�����null��������Ҫ�������ף����ڽ��׶����װ�����ݣ�����û�����϶࣬��Ҫ�����׼�¼���װһЩ��Ϣ
        �����ȡ����������clue�е�һЩ��Ϣ���Է�װ��tran�У�û���޸��ߺ��޸�ʱ��
        */
        if (null != tran) {
            tran.setOwner(clue.getOwner());
            tran.setSource(clue.getSource());
            tran.setDescription(clue.getDescription());
            tran.setContactSummary(clue.getContactSummary());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setCustomerId(customer.getId());
            tran.setContactsId(contacts.getId());

            //ʹ�ý��ױ���ӽ��׼�¼
            count = tranDao.save(tran);

            //������ʧ�ܣ�����0�����ñ��Ϊfalse
            if (count != 1) {
                flag = false;
            }

            //�������׳ɹ��󣬴���һ��������ʷ��¼
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(createTime);

            //�ӽ��ױ��л�ȡ���׽����׽׶κ�Ԥ�Ƴɽ�����
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setStage(tran.getStage());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setTranId(tran.getId());//������������ӽ��ױ������id

            //ʹ�ý�����ʷ����ӽ�����ʷ��¼
            count = tranHistoryDao.save(tranHistory);

            //������ʧ�ܣ�����0�����ñ��Ϊfalse
            if (count != 1) {
                flag = false;
            }
        }

        //ɾ��������ע��ʹ��������ע����ݱ��е��������idƥ������ɾ��
        count = clueRemarkDao.deleteByCId(cid);

        //��Ϊ������ɾ�������ɾ��ʧ�ܣ����ص�һ������������ע�б�ĳ��ȣ����ñ��Ϊfalse
        if (count != clueRemarks.size()) {
            flag = false;
        }

        //ɾ���������г���Ĺ�����ϵ��������ϵ�б�ʹ�ù�ϵ������ɾ�������ݹ�ϵ����id����󷽷�ƥ��ɾ��
        for (ClueActivityRelation clueActivityRelation : clueActivityRelations) {
            count = clueActivityRelationDao.unbund(clueActivityRelation.getId());

            //���ɾ��ʧ�ܣ�����0�����ñ��Ϊfalse
            if (count != 1) {
                flag = false;
            }
        }

        //����idɾ������
        count = clueDao.delete(cid);

        //���ɾ��ʧ�ܣ�����0�����ñ��Ϊfalse
        if (count != 1) {
            flag = false;
        }

        return flag;//�����ת�������б��һֱ����Ϊtrue��˵��ת��˳�������ص���true��һ����һ�����ڳ������᷵��false
    }
}
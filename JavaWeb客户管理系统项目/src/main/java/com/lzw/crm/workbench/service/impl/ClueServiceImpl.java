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
        //dao层一张表对应一个接口和一个xml，因为是操作关系表，所以必须使用关系表的dao接口
        int count = clueActivityRelationDao.unbund(id);
        return count == 1;
    }

    @Override
    public boolean bund(String cid, String[] aids) {
        boolean flag = true;

        //遍历市场活动id数组，取出每个市场活动id，分别与线索id关联
        for (String aid : aids) {
            //生成一个UUID作为关系表的id
            String id = UUIDUtil.getUUID();

            //因为是往关系表新增一条记录，所以需要使用到关系表对应的domain，初始化属性，再往dao层传递关系表对象新增
            ClueActivityRelation clueActivityRelation = new ClueActivityRelation(id, cid, aid);

            /*
            dao层一张表对应一个接口和一个xml，因为是操作关系表，所以必须使用关系表的dao接口
            这是一个循环添加的过程，每生成一个关系表对象，就要添加一条关系表记录，所以调用dao层放在循环体中
            */
            int count = clueActivityRelationDao.bund(clueActivityRelation);

            /*
            必须使用布尔标记判断整个过程是成功或失败，如果使用count，每次添加成功都返回1，最后判断时只能判断最后一次添加成功
            并不能确保所有添加都成功；使用布尔标记后，初始值是true，一旦添加失败，count必是0，修改标记为false，最终返回false
            只有所有添加都成功，count都是1，才能保持标记的值是true，最终返回true
            */
            if (count != 1) {
                flag = false;
            }
        }

        return flag;
    }

    @Override
    public boolean convert(String cid, Tran tran, String createBy) {
        //生成公用的创建记录的时间，另外不能生成公用id，id必须每条记录独自生成一个不同的id
        String createTime = DateTimeUtil.getSysTime();

        //定义一个布尔值，默认转换成功
        boolean flag = true;

        //根据线索id获取线索信息，返回一个线索对象
        Clue clue = clueDao.getById(cid);

        /*
        通过线索对象提取其中的真实客户信息，即其中的公司名，精确匹配，如果客户不存在，新建客户，否则不用新建
        客户存在是因为公司中管理客户的团队和到市场活动中宣传推广、收集线索的团队之间的交流有可能产生了一些信息错误
        客户和公司有可能曾经有过交易，但收集线索的团队不清楚有该客户的存在，将该客户当成了一个线索处理
        */
        String company = clue.getCompany();

        //使用客户表通过公司名查询客户记录
        Customer customer = customerDao.getCustomerByName(company);

        /*
        如果返回的客户记录不存在，需要创建客户对象新建客户，对象中的数据从线索对象中获取，进行线索转换；除了修改时间和修改者都需要获取
        对于获取不到的数据，可以到客户模块中编辑补充
        */
        if (null == customer) {
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(company);//注意这里是获取到的公司名
            customer.setCreateTime(createTime);
            customer.setCreateBy(createBy);
            customer.setOwner(clue.getOwner());
            customer.setPhone(clue.getPhone());
            customer.setAddress(clue.getAddress());
            customer.setWebsite(clue.getWebsite());
            customer.setDescription(clue.getDescription());
            customer.setContactSummary(clue.getContactSummary());
            customer.setNextContactTime(clue.getNextContactTime());

            //添加客户记录
            int count = customerDao.save(customer);

            //如果添加失败，返回0，设置标记为false
            if (count != 1) {
                flag = false;
            }
        }

        /*
        通过线索对象提取其中的联系人信息，无论联系人是否存在，都要新建联系人，因为联系人使用的是人的全名，人名允许有重复的
        而公司名不允许重复；联系人存在的原因同上；联系人对象中的数据从线索对象中获取，进行线索转换；除了修改时间和修改者都需要获取
        对于获取不到的数据，可以到联系人模块中编辑补充
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
        contacts.setCustomerId(customer.getId());//填写联系人所属的企业，即上面的客户id
        contacts.setAppellation(clue.getAppellation());
        contacts.setSource(clue.getSource());
        contacts.setDescription(clue.getDescription());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setNextContactTime(clue.getNextContactTime());

        //使用联系人表添加联系人记录
        int count = contactsDao.save(contacts);

        //如果添加失败，返回0，设置标记为false
        if (count != 1) {
            flag = false;
        }

        //使用线索备注表查询当前线索关联的备注信息列表
        List<ClueRemark> clueRemarks = clueRemarkDao.getListByCId(cid);

        //遍历备注列表
        for (ClueRemark clueRemark : clueRemarks) {
            /*
            除了noteContent都不需要转换，因为id、创建者和创建时间自动生成，修改者和修改时间没有，editFlag为0，clueId已存在
            所以这里只需获取每一条备注信息即可
            */
            String noteContent = clueRemark.getNoteContent();

            //创建客户备注对象，添加客户备注记录
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setEditFlag("0");
            customerRemark.setCustomerId(customer.getId());//填写备注所属的企业，即上面的客户id
            customerRemark.setNoteContent(noteContent);

            //使用客户备注表添加客户备注记录
            count = customerRemarkDao.save(customerRemark);

            //如果添加失败，返回0，设置标记为false
            if (count != 1) {
                flag = false;
            }

            //创建联系人备注对象，添加联系人备注记录
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setEditFlag("0");
            contactsRemark.setContactsId(contacts.getId());//填写备注所属的联系人，即上面的联系人id
            contactsRemark.setNoteContent(noteContent);

            //使用客户备注表添加客户备注记录
            count = contactsRemarkDao.save(contactsRemark);

            //如果添加失败，返回0，设置标记为false
            if (count != 1) {
                flag = false;
            }
        }

        //线索和市场活动的关联关系转换成联系人和市场活动的关联关系
        //查询出当前线索关联的市场活动，使用线索市场活动关系表查询
        List<ClueActivityRelation> clueActivityRelations = clueActivityRelationDao.getListByCId(cid);

        //遍历线索市场活动关系列表
        for (ClueActivityRelation clueActivityRelation : clueActivityRelations) {
            //获取线索市场活动关系对象中的市场活动id
            String activityId = clueActivityRelation.getActivityId();

            //创建联系人市场活动关系对象，让生成的联系人id与获取到的市场活动id关联
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setActivityId(activityId);

            //使用联系人市场活动关系表添加关系记录
            count = contactsActivityRelationDao.save(contactsActivityRelation);

            //如果添加失败，返回0，设置标记为false
            if (count != 1) {
                flag = false;
            }
        }

        /*
        如果控制器传过来的交易对象不是null，表明需要创建交易；对于交易对象封装的数据，如果用户需求较多，需要给交易记录多封装一些信息
        上面获取的线索对象clue中的一些信息可以封装到tran中；没有修改者和修改时间
        */
        if (null != tran) {
            tran.setOwner(clue.getOwner());
            tran.setSource(clue.getSource());
            tran.setDescription(clue.getDescription());
            tran.setContactSummary(clue.getContactSummary());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setCustomerId(customer.getId());
            tran.setContactsId(contacts.getId());

            //使用交易表添加交易记录
            count = tranDao.save(tran);

            //如果添加失败，返回0，设置标记为false
            if (count != 1) {
                flag = false;
            }

            //创建交易成功后，创建一条交易历史记录
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(createTime);

            //从交易表中获取交易金额、交易阶段和预计成交日期
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setStage(tran.getStage());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setTranId(tran.getId());//这是外键，连接交易表的主键id

            //使用交易历史表添加交易历史记录
            count = tranHistoryDao.save(tranHistory);

            //如果添加失败，返回0，设置标记为false
            if (count != 1) {
                flag = false;
            }
        }

        //删除线索备注：使用线索备注表根据表中的外键线索id匹配批量删除
        count = clueRemarkDao.deleteByCId(cid);

        //因为是批量删除，如果删除失败，返回的一定不是线索备注列表的长度，设置标记为false
        if (count != clueRemarks.size()) {
            flag = false;
        }

        //删除线索和市场活动的关联关系：遍历关系列表使用关系表逐条删除，传递关系对象id给解绑方法匹配删除
        for (ClueActivityRelation clueActivityRelation : clueActivityRelations) {
            count = clueActivityRelationDao.unbund(clueActivityRelation.getId());

            //如果删除失败，返回0，设置标记为false
            if (count != 1) {
                flag = false;
            }
        }

        //根据id删除线索
        count = clueDao.delete(cid);

        //如果删除失败，返回0，设置标记为false
        if (count != 1) {
            flag = false;
        }

        return flag;//如果在转换过程中标记一直保持为true，说明转换顺利，返回的是true；一旦有一个环节出错，都会返回false
    }
}
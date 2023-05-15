package com.lzw.crm.settings.domain;

/**
 * �û���
 */
public class User {
    //��������ţ����ݿ��е�������char(32)����Ϊ�Ƕ���32λ��UUID
    private String id;

    //��¼�˺�
    private String loginAct;

    //�û���ʵ����
    private String name;

    /*
    ��¼���룬���ݱ���ʹ��md5����ת��Ϊ�������룬��Ϊ���Ĳ�����ʾ�����ݱ��У���������İ�ȫ������һ��������ܱ�����Ա��ȡ
    �����������ϵͳʹ�����ģ������ܺڿ����֣������ݱ�����������ȡ���е�����
    ����ѧ��һ�ָ߼���ѧ��md5�㷨�������е����ļ��ܷ�ʽ��md5���ܺ���������벻�ܷ�����Ϊ�������룬��Ϊmd5�ǲ������
    */
    private String loginPwd;

    //����
    private String email;

    /*
    �ַ�����ʽ��ʧЧʱ�䣬ʧЧʱ����ͣ����ĳ���û�����һ��ʱ�䣬��ʱ�䵽�󣬸��û���Ҫ�뿪��˾����ʱ�û��ٵ�¼ϵͳ���¼ʧ��
    ����ʱ�˺���Ϣ�����ڣ������ڹ�˾�ļ�¼��
    �г��ϳ��õ��������ڱ�����ʽ��
    �����գ�yyyy-MM-dd��10λ�ַ�
    ������ʱ���룺yyyy-MM-dd HH:mm:ss��19λ�ַ�
    ��Ϊ���ڵ��ַ����ȹ̶����������ݿ���Ӧ��ʹ��char���Ͷ��壬��ΪЧ�ʸߣ��������ݿ���ʹ����char(19)����ʾ����һ��19λ������
    */
    private String expireTime;

    //�����˺�״̬���ַ���0��ʾ�����˺ţ�1��ʾ�����˺ţ����ݿ��е�������char(1)�����û��зǷ���Ϊ��������Ա���֣�����Ա�����û��˺�
    private String lockState;

    //���ű��
    private String deptno;

    /*
    ������ʵ�IP��ַ�������õ�ǰ��Ŀ���ĸ�IP��Ӧ���������ܷ��ʣ�����������Ϸ��ʷ�����ʱ��������ڵ�����IP
    ʹ��request�������getRemoteAddr�������Ի�ȡ�������ԴIP
    ��ʹ�˺ź�������ȷ����IP����ȷ��Ҳ���ܷ��ʣ�����һ��ϵͳ��ȫ����
    ���ڱ��ֶε��ֶ�ֵ������һ����IP��ַ���м�ʹ�ö��ŷָ�����������������IP���ڰ������ڣ����ܵ�¼
    ����������ҵ����Ŀ������ҵ�������˶������Ӿ��������������ϵͳ��IP��ַ�Ѿ��̶��ˣ�������Աʹ��������IP�޷����ʣ���ʹ��ȡ���˺�����
    */
    private String allowIps;

    //��ǰ�����Ӧ�ı��¼�Ĵ���ʱ�䣬���ݿ��е�������char(19)
    private String createTime;

    //��ǰ�����Ӧ�ı��¼�Ĵ�����
    private String createBy;

    //��ǰ�����Ӧ�ı��¼���޸�ʱ�䣬���ݿ��е�������char(19)
    private String editTime;

    //��ǰ�����Ӧ�ı��¼���޸���
    private String editBy;

    public User() {

    }

    public User(String id, String loginAct, String name, String loginPwd, String email, String expireTime,
                String lockState, String deptno, String allowIps, String createTime, String createBy, String editTime,
                String editBy) {
        this.id = id;
        this.loginAct = loginAct;
        this.name = name;
        this.loginPwd = loginPwd;
        this.email = email;
        this.expireTime = expireTime;
        this.lockState = lockState;
        this.deptno = deptno;
        this.allowIps = allowIps;
        this.createTime = createTime;
        this.createBy = createBy;
        this.editTime = editTime;
        this.editBy = editBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginAct() {
        return loginAct;
    }

    public void setLoginAct(String loginAct) {
        this.loginAct = loginAct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getLockState() {
        return lockState;
    }

    public void setLockState(String lockState) {
        this.lockState = lockState;
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }

    public String getAllowIps() {
        return allowIps;
    }

    public void setAllowIps(String allowIps) {
        this.allowIps = allowIps;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getEditTime() {
        return editTime;
    }

    public void setEditTime(String editTime) {
        this.editTime = editTime;
    }

    public String getEditBy() {
        return editBy;
    }

    public void setEditBy(String editBy) {
        this.editBy = editBy;
    }
}
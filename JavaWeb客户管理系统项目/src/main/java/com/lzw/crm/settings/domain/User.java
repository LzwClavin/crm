package com.lzw.crm.settings.domain;

/**
 * 用户表
 */
public class User {
    //主键：编号，数据库中的类型是char(32)，因为是定长32位的UUID
    private String id;

    //登录账号
    private String loginAct;

    //用户真实姓名
    private String name;

    /*
    登录密码，数据表中使用md5加密转码为密文密码，因为明文不能显示在数据表中，有两方面的安全隐患，一是密码可能被程序员盗取
    二是如果国家系统使用明文，会遭受黑客入侵，将数据表拷贝出来，盗取其中的数据
    密码学是一种高级数学，md5算法是最流行的密文加密方式；md5加密后的密文密码不能反解析为明文密码，因为md5是不可逆的
    */
    private String loginPwd;

    //邮箱
    private String email;

    /*
    字符串形式的失效时间，失效时间解释：如给某个用户设置一个时间，该时间到后，该用户需要离开公司，此时用户再登录系统会登录失败
    但此时账号信息还存在，保留在公司的记录中
    市场上常用的两种日期表现形式：
    年月日：yyyy-MM-dd，10位字符
    年月日时分秒：yyyy-MM-dd HH:mm:ss，19位字符
    因为日期的字符长度固定，所以数据库中应该使用char类型定义，因为效率高，这里数据库中使用了char(19)，表示这是一个19位的日期
    */
    private String expireTime;

    //设置账号状态，字符串0表示锁定账号，1表示启用账号，数据库中的类型是char(1)；当用户有非法行为，被管理员发现，管理员锁定用户账号
    private String lockState;

    //部门编号
    private String deptno;

    /*
    允许访问的IP地址，即设置当前项目是哪个IP对应的主机才能访问，即在浏览器上访问服务器时浏览器所在的主机IP
    使用request对象调用getRemoteAddr方法可以获取请求的来源IP
    即使账号和密码正确，但IP不正确，也不能访问，这是一个系统安全设置
    关于本字段的字段值，就是一个个IP地址，中间使用逗号分隔，如果浏览器的主机IP不在白名单内，不能登录
    对于这种企业级项目，在企业中所有人都是连接局域网，允许访问系统的IP地址已经固定了，外来人员使用外来的IP无法访问，即使盗取了账号密码
    */
    private String allowIps;

    //当前对象对应的表记录的创建时间，数据库中的类型是char(19)
    private String createTime;

    //当前对象对应的表记录的创建者
    private String createBy;

    //当前对象对应的表记录的修改时间，数据库中的类型是char(19)
    private String editTime;

    //当前对象对应的表记录的修改者
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
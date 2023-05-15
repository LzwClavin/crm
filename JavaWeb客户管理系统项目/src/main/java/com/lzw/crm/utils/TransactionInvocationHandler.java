package com.lzw.crm.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.ibatis.session.SqlSession;

public class TransactionInvocationHandler implements InvocationHandler {

    private Object target;

    public TransactionInvocationHandler(Object target) {

        this.target = target;

    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        SqlSession session = null;

        Object obj = null;

        try {
            session = SqlSessionUtil.getSqlSession();

            obj = method.invoke(target, args);

            session.commit();
        } catch (Exception e) {
            session.rollback();
            //e.printStackTrace();

            /*
            处理的是什么异常，继续往上抛什么异常；使业务层抛出的异常通过代理类继续抛给控制器，由控制器处理异常，解决登录验证问题
            可以随便抛出一个异常，如RuntimeException，只要能实现使控制器的代码走分支即可，因为抛出任何异常给控制器
            控制器都能捕捉到并跳转到catch域，但是往后获取异常信息时是不正确的信息；所以必须抛出e.getCause
            */
            throw e.getCause();
        } finally {
            SqlSessionUtil.myClose(session);
        }

        return obj;
    }

    public Object getProxy() {

        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);

    }

}
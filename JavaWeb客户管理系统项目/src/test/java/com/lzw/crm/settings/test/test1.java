package com.lzw.crm.settings.test;

import com.lzw.crm.utils.*;

import java.text.*;
import java.util.*;

/**
 * ��֤�˺��Ƿ�ʧЧ����������֤IP��ַ�Ƿ����ޡ�md5��������
 */
public class test1 {
    public static void main(String[] args) {
        //�˺�ʧЧʱ�䣬ʵ��Ϊ���ݱ��е��ֶ�ֵ��User�����е�����ֵ
        String expireTime = "2024-05-01 08:00:00";

        //��ȡ��ǰϵͳʱ��
        Date date = new Date();

        //��ʽ����ǰϵͳʱ��
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(date);
        System.out.println(currentTime);

        //ʹ�ù������ȡ��ʽ����ǰϵͳʱ��
        currentTime = DateTimeUtil.getSysTime();
        System.out.println(currentTime);

        /*
        ����compareTo�����Ƚ�ʧЧʱ��͵�ǰϵͳʱ��
        �ڸ�ʽ��ͬ��ǰ���£��������ַ����Ӹ�λ����λ����ַ��Ƚϣ���ʵ���ַ�����ʽ��ʱ��Ƚ�
        ����һ���������������0����ʾ�����ߴ��˺�δʧЧ�����С��0����ʾ�������˺���ʧЧ���������0��������ȣ��˺���ʧЧ
        */
        int compare = expireTime.compareTo(currentTime);
        System.out.println(compare);
        expireTime = "2022-05-01 08:00:00";
        compare = expireTime.compareTo(currentTime);
        System.out.println(compare);

        //�˺�״̬����Ϊ����
        String lockState = "0";

        //����ʹ���ַ�����������equals������ʹ��lockState����equals����Ϊ���lockState�����ݿ��в�ѯ����null������ֿ�ָ���쳣
        if ("0".equals(lockState)) {
            System.out.println("�˺��ѱ��������޷���¼��");
        }

        //���������������˵�IP��ַ
        String ip = "192.168.1.1";

        //���ݱ���������ʵ�IP��ַȺ�����IP��ַ֮��ʹ�ö��ŷָ�
        String allowIps = "192.168.1.10,127.0.0.1";

        //���ַ���ʹ�ö����и��һ���ַ������飬���ÿ����ЧIP
        String[] ais = allowIps.split(",");

        //���ַ������ַ����������Ƿ����������IP��ַ������У�����Ԫ�ص��±�
        int index = Arrays.binarySearch(ais, ip);

        //������ص��±��Ǹ�����˵���������IP��ַ���ڰ������ڣ��޷���¼
        if (index < 0) {
            System.out.println("IP��ַ���ޣ���¼ʧ�ܣ�");
        }

        //����
        String pwd = "123";

        //md5����
        String md5Pwd = MD5Util.getMD5(pwd);
        System.out.println(md5Pwd);//202cb962ac59075b964b07152d234b70
        System.out.println(MD5Util.getMD5("lzw695"));//f187acdcde072ff7a551908f127ef985
    }
}
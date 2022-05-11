package com.example.demo.utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 短信发送工具类
 * @create 2022-05-06-11:45
 */
public class SMSUtils {

    public static final String SECRETID = "AKIDjSv83i6D2bssb4HEuKUNzKPEl11kCNGt";
    public static final String SECRETKEY = "NTIq77qITO2PbXCahX6OPZS3NREjxvc2";
    public static final String APPID = "1400674857";
    public static final String SIGNNAME = "造火箭的程序员";
    public static final String TEMPLATEID = "1392901";

    public void sendSms(String phone, String code, String ttl) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(SECRETID, SECRETKEY);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet = {"+86" +  phone};
            req.setPhoneNumberSet(phoneNumberSet);

            req.setSmsSdkAppId(APPID);

            req.setSignName(SIGNNAME);
            req.setTemplateId(TEMPLATEID);

            String[] templateParamSet = {code, ttl};
            req.setTemplateParamSet(templateParamSet);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }
}
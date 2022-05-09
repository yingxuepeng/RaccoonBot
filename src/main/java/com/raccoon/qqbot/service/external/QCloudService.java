package com.raccoon.qqbot.service.external;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.nlp.v20190408.NlpClient;
import com.tencentcloudapi.nlp.v20190408.models.TextClassificationRequest;
import com.tencentcloudapi.nlp.v20190408.models.TextClassificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QCloudService {
    @Value("${com.raccoon.qqbot.qcloud.secretid}")
    private String secretId;
    @Value("${com.raccoon.qqbot.qcloud.secretkey}")
    private String secretKey;

    public TextClassificationResponse getMsgLabel(String message) throws TencentCloudSDKException {
        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
        // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
        Credential cred = new Credential(secretId, secretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("nlp.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        NlpClient client = new NlpClient(cred, "ap-guangzhou", clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        TextClassificationRequest req = new TextClassificationRequest();
        req.setText(message);
        // 返回的resp是一个TextClassificationResponse的实例，与请求对象对应
        TextClassificationResponse resp = client.TextClassification(req);
        // 输出json格式的字符串回包
        return resp;
//            System.out.println(TextClassificationResponse.toJsonString(resp));

    }
}

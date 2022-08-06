package com.raccoon.qqbot.service.external;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public class QCloudCosService {
    @Value("${com.raccoon.qqbot.qcloud.secretid}")
    private String secretId;
    @Value("${com.raccoon.qqbot.qcloud.secretkey}")
    private String secretKey;

    @Value("${com.raccoon.qqbot.qcloud.cos.bucketName}")
    private String cosBucketName;
    @Value("${com.raccoon.qqbot.qcloud.cos.region}")
    private String cosRegion;

    public PutObjectResult uploadFile(File file, String cosPath) {

        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region(cosRegion);
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosClient = new COSClient(cred, clientConfig);

        PutObjectRequest putObjectRequest = new PutObjectRequest(cosBucketName, cosPath, file);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        return putObjectResult;
    }
}

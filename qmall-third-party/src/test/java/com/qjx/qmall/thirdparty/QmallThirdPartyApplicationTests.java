package com.qjx.qmall.thirdparty;

import ClickSend.Api.SmsApi;
import ClickSend.ApiClient;
import ClickSend.ApiException;
import ClickSend.Model.SmsMessage;
import ClickSend.Model.SmsMessageCollection;
import com.aliyun.oss.OSSClient;
import com.qjx.qmall.thirdparty.component.SmsComponent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class QmallThirdPartyApplicationTests {

	@Resource
	OSSClient ossClient;

	@Resource
	SmsComponent smsComponent;

	@Test
	public void testSendSms() {
		smsComponent.sendSmsCode("+61416810777","8989");
	}

	@Test
	public void sendSms() {
		ApiClient defaultClient = new ApiClient();
		defaultClient.setUsername("zmryanq@gmail.com");
		defaultClient.setPassword("Laizi8171!");
		SmsApi apiInstance = new SmsApi(defaultClient);

		SmsMessage smsMessage=new SmsMessage();
		smsMessage.body("your verify code is 8888, expire in 20 minutes");
		smsMessage.to("+61416810777");
		smsMessage.source("+61411111111");

		List<SmsMessage> smsMessageList= Arrays.asList(smsMessage);
		// SmsMessageCollection | SmsMessageCollection model
		SmsMessageCollection smsMessages = new SmsMessageCollection();
		smsMessages.messages(smsMessageList);
		try {
			String result = apiInstance.smsSendPost(smsMessages);
			System.out.println(result);
		} catch (ApiException e) {
			System.err.println("Exception when calling SmsApi#smsSendPost");
			e.printStackTrace();
		}
	}



	@Test
	public void upload() throws FileNotFoundException {
		// yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
		String endpoint = "oss-ap-southeast-2.aliyuncs.com";
// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//		String accessKeyId = "LTAI5tK14G174nbYRjpvXnE1";
//		String accessKeySecret = "rgfuEdkSzl4FReTpRkwoghg9GeM4pv";

// 创建OSSClient实例。
//		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
		InputStream inputStream = new FileInputStream("C:\\Users\\Ryan.Q\\Desktop\\1.jpg");
// 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
		ossClient.putObject("gulimall-hostgov", "1.jpg", inputStream);

// 关闭OSSClient。
		ossClient.shutdown();
		System.out.println("上传成功");
	}

	@Test
	void contextLoads() {
	}

}

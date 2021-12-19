package com.qjx.qmall.thirdparty.component;

import ClickSend.Api.SmsApi;
import ClickSend.ApiClient;
import ClickSend.ApiException;
import ClickSend.Model.SmsMessage;
import ClickSend.Model.SmsMessageCollection;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Ryan
 * 2021-11-14-14:57
 */
@ConfigurationProperties(prefix = "clicksend.sms")
@Data
@Component
public class SmsComponent {

	private String username;
	private String password;
	private String msgTmp;
	private String fromPhoneNum;

	public void sendSmsCode(String phone, String code) {
		ApiClient defaultClient = new ApiClient();
		defaultClient.setUsername(username);
		defaultClient.setPassword(password);
		SmsApi apiInstance = new SmsApi(defaultClient);

		SmsMessage smsMessage=new SmsMessage();
		smsMessage.body(MessageFormat.format(msgTmp, code));
		smsMessage.to(phone);
		smsMessage.source(fromPhoneNum);

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
}

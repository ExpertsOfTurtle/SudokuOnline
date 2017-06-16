package com.turtlebone.top.test;

import org.junit.Test;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class SendEmail {
	private static final String ACCESS_KEY = "LTAIv5K5cvw46q0U";
	private static final String SECRET = "WEG1KpNAu8hYbrv7YHu1fHyouBQHAF";

	@Test
	public void sample() {
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY, SECRET);
		IAcsClient client = new DefaultAcsClient(profile);
		SingleSendMailRequest request = new SingleSendMailRequest();
		try {
			request.setAccountName("postmaster@turtlebone.top");
			request.setFromAlias("Turtle");
			request.setAddressType(1);
			request.setTagName("");
			request.setReplyToAddress(true);
//			request.setToAddress("fengrenchang86@vip.qq.com");
			request.setToAddress("postmaster@turtlebone.top");
			request.setSubject("我的第一封邮件");
			request.setHtmlBody("没有什么正文的，看标题");
			SingleSendMailResponse httpResponse = client.getAcsResponse(request);
			System.out.println(httpResponse.getEnvId());
			System.out.println(httpResponse.getRequestId());
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
}

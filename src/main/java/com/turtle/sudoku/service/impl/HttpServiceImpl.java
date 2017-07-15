package com.turtle.sudoku.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.turtle.sudoku.service.HttpService;

@Service
public class HttpServiceImpl implements HttpService {

	@Override
	public <T> T sendAndGetResponse(String url, Map<String, String> header, String body, Class<T> clz) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		StringEntity entity = new StringEntity(body, "UTF-8");
		httppost.setEntity(entity);
		T rs = null;
		if (header != null) {
			for (Entry<String, String>entry : header.entrySet()) {
				httppost.addHeader(entry.getKey(), entry.getValue());
			}
		}
		try {
			CloseableHttpResponse response = httpclient.execute(httppost);
			HttpEntity rspEntity = response.getEntity();
			String result = EntityUtils.toString(rspEntity);
			System.out.println(result);
			rs = JSON.parseObject(result, clz);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}

}

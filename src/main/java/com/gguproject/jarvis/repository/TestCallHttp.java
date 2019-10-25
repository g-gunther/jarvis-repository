package com.gguproject.jarvis.repository;

import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class TestCallHttp {

	public static void main(final String... args) throws Exception {
		
		String downloadedJarFileName = "sdkfhsdf-sdfkjsdf.sdf.sdf.sdf.tmp".substring(0, "sdkfhsdf-sdfkjsdf.sdf.sdf.sdf.tmp".lastIndexOf("."));
		System.out.println(downloadedJarFileName);
		
//		URL url = new URL("http://localhost:8080/jar/jarvis-module-app/download");
//		
//		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
//		String fileName = httpConnection.getHeaderField("Content-Disposition").replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
//		
//		ReadableByteChannel readableByteChannel = Channels.newChannel(httpConnection.getInputStream());
//		FileOutputStream fileOutputStream = new FileOutputStream(fileName);
////		FileChannel fileChannel = fileOutputStream.getChannel();
//		fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
	}
}

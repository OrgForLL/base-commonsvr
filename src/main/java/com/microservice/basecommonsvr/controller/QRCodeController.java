package com.microservice.basecommonsvr.controller;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.basecommonsvr.tool.QRCodeUtil;

@RestController
public class QRCodeController {

	/**
	 * 生成二维码接口
	 * @param content 要生成二维码的内容
	 * @param qrCodeSize 二维码的尺寸大小
	 * @param response web的response对象
	 */
	@GetMapping("/generateQRCode")
	private void generateQRCode(@RequestParam(required=true) String content,@RequestParam(required=false) Integer qrCodeSize,HttpServletResponse response) {
		ServletOutputStream outputStream = null;
		if(qrCodeSize==null) {
			qrCodeSize = 60;
		}
		try {
			outputStream=response.getOutputStream();
			QRCodeUtil.createQrCode(outputStream, content, qrCodeSize, "JPEG");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(outputStream!=null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

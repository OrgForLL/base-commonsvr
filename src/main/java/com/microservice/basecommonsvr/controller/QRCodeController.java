package com.microservice.basecommonsvr.controller;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.basecommonsvr.tool.QRCodeUtil;


@RestController
public class QRCodeController {
	
	private final Logger logger = LogManager.getLogger(QRCodeController.class);
	
	/**
	 * 生成二维码接口
	 * @param c 要生成二维码的内容
	 * @param w 二维码的尺寸大小
	 * @param response web的response对象
	 */
	@GetMapping("/generateQRCode")
	private void generateQRCode(@RequestParam(required=true) String c,@RequestParam(required=false) Integer w,HttpServletResponse response) {
		ServletOutputStream outputStream = null;
		if(w==null) {
			w = 60;
		}
		try {
			outputStream=response.getOutputStream();
			QRCodeUtil.createQrCode(outputStream, c, w, "JPEG");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(" 生成二维码接口\n generateQRCode\n 生成二维码异常\n"+e);
		} finally {
			if(outputStream!=null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error(" 生成二维码接口\n generateQRCode\n 关闭流异常\n"+e);
				}
			}
		}
	}
}

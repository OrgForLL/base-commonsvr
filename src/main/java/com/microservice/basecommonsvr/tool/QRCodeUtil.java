package com.microservice.basecommonsvr.tool;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码工具类
 * @author cjj
 */
public class QRCodeUtil {
	
	private static final Logger logger = LogManager.getLogger(QRCodeUtil.class);
	
	/**
	 * 生成包含字符串信息的二维码图片
     * @param outputStream 输出流
     * @param content 二维码携带信息
     * @param qrCodeSize 二维码图片大小
     * @param imageFormat 二维码的格式
	 */
    public static void createQrCode(OutputStream outputStream, String content, int qrCodeSize, String imageFormat) {  
        //设置二维码参数
        Hashtable<EncodeHintType, Object> hintMap = new Hashtable<EncodeHintType, Object>();  
        //矫错级别  
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hintMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hintMap.put(EncodeHintType.MARGIN, 2);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();  
        //创建比特矩阵(位矩阵)的QR码编码的字符串  
        BitMatrix byteMatrix;
		try {
			byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);
			MatrixToImageWriter.writeToStream(byteMatrix, imageFormat, outputStream);
		} catch (WriterException e) {
			e.printStackTrace();
			logger.error(" 生成包含字符串信息的二维码图片\n createQrCode\n 写流异常\n"+e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(" 生成包含字符串信息的二维码图片\n createQrCode\n io流异常\n"+e);
		}
    }  
}

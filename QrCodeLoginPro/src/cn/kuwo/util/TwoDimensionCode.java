package cn.kuwo.util;  
  
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.swetake.util.Qrcode;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.exception.DecodingFailedException;  
  
public class TwoDimensionCode {  
	//��ά�� SIZE
	private static final int CODE_IMG_SIZE = 235;
	// LOGO SIZE (Ϊ�˲���ͼƬ�������ԣ�����ѡ�������м���룬���ҳ�����Ϊ������ά���1/5��1/4)
    private static final int INSERT_IMG_SIZE = CODE_IMG_SIZE/5;  
      
    /** 
     * ���ɶ�ά��(QRCode)ͼƬ 
     * @param content �洢���� 
     * @param imgPath ��ά��ͼƬ�洢·�� 
     * @param imgType ͼƬ���� 
     * @param insertImgPath logoͼƬ·��
     */  
    public void encoderQRCode(String content, String imgPath, String imgType, String insertImgPath) {  
        try {  
            BufferedImage bufImg = this.qRCodeCommon(content, imgType, insertImgPath);  
              
            File imgFile = new File(imgPath);
    		if (!imgFile.exists())
    		{
    			imgFile.mkdirs();
    		}
            // ���ɶ�ά��QRCodeͼƬ  
            ImageIO.write(bufImg, imgType, imgFile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }   
  
    /** 
     * ���ɶ�ά��(QRCode)ͼƬ 
     * @param content �洢���� 
     * @param output ����� 
     * @param imgType ͼƬ���� 
     */  
    public void encoderQRCode(String content, OutputStream output, String imgType) {  
        try {  
            BufferedImage bufImg = this.qRCodeCommon(content, imgType, null);  
            // ���ɶ�ά��QRCodeͼƬ  
            ImageIO.write(bufImg, imgType, output);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    
    /**
     * @param content
     * @param imgType
     * @param size
     * @param imgPath	Ƕ��ͼƬ������
     * @return
     */
    private BufferedImage qRCodeCommon(String content, String imgType, String imgPath){
    	BufferedImage bufImg = null;  
        try {  
            Qrcode qrcodeHandler = new Qrcode();  
            // ���ö�ά���Ŵ��ʣ���ѡL(7%)��M(15%)��Q(25%)��H(30%)���Ŵ���Խ�߿ɴ洢����ϢԽ�٣����Զ�ά�������ȵ�Ҫ��ԽС  
            qrcodeHandler.setQrcodeErrorCorrect('M');  
            qrcodeHandler.setQrcodeEncodeMode('B');  
            // �������ö�ά��ߴ磬ȡֵ��Χ1-40��ֵԽ��ߴ�Խ�󣬿ɴ洢����ϢԽ��  
            qrcodeHandler.setQrcodeVersion(15);  
            // ������ݵ��ֽ����飬���ñ����ʽ  
            byte[] contentBytes = content.getBytes("utf-8");  
            // ͼƬ�ߴ�  
            int imgSize = CODE_IMG_SIZE;  
            bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);  
            Graphics2D gs = bufImg.createGraphics();  
            // ���ñ�����ɫ  
            gs.setBackground(Color.WHITE);  
            gs.clearRect(0, 0, imgSize, imgSize);  
  
            // �趨ͼ����ɫ> BLACK  
            gs.setColor(Color.BLACK);  
            // ����ƫ�����������ÿ��ܵ��½�������  
            final int pixoff = 2;  
            final int sz = 3;
            // �������> ��ά��  
            if (contentBytes.length > 0 && contentBytes.length < 800) {  
                boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);  
                for (int i = 0; i < codeOut.length; i++) {  
                    for (int j = 0; j < codeOut.length; j++) {  
                        if (codeOut[j][i]) {  
                            gs.fillRect(j * sz + pixoff, i * sz + pixoff, sz, sz);  
                        }  
                    }  
                }  
            } else {  
                throw new Exception("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");  
            }  
            //Ƕ��logo
            if(imgPath != null)
            	this.insertImage(bufImg, imgPath, true);
            gs.dispose();  
            bufImg.flush();
            System.out.println(bufImg.getWidth() + " " + bufImg.getHeight());
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return bufImg;  
    }
    
    private void insertImage(BufferedImage source, String imgPath,  
            boolean needCompress) throws Exception {  
        File file = new File(imgPath);  
        if (!file.exists()) {  
            System.err.println(""+imgPath+"   ���ļ������ڣ�");  
            return;  
        }  
        Image src = ImageIO.read(new File(imgPath));  
        int width = src.getWidth(null);  
        int height = src.getHeight(null);  
        if (needCompress) { // ѹ��LOGO  
            if (width > INSERT_IMG_SIZE) {  
                width = INSERT_IMG_SIZE;  
            }  
            if (height > INSERT_IMG_SIZE) {  
                height = INSERT_IMG_SIZE;  
            }  
            Image image = src.getScaledInstance(width, height,  
                    Image.SCALE_SMOOTH);  
            BufferedImage tag = new BufferedImage(width, height,  
                    BufferedImage.TYPE_INT_RGB);  
            Graphics g = tag.getGraphics();  
            g.drawImage(image, 0, 0, null); // ������С���ͼ  
            g.dispose();  
            src = image;  
        }  
        // ����LOGO  
        Graphics2D graph = source.createGraphics();  
        int x = (CODE_IMG_SIZE - width) / 2;  
        int y = (CODE_IMG_SIZE - height) / 2;  
        graph.drawImage(src, x, y, width, height, null);  
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);  
        graph.setStroke(new BasicStroke(3f));  
        graph.draw(shape);  
        graph.dispose();  
    }  
      
    /** 
     * ������ά�루QRCode�� 
     * @param imgPath ͼƬ·�� 
     * @return 
     */  
    public String decoderQRCode(String imgPath) {  
        // QRCode ��ά��ͼƬ���ļ�  
        File imageFile = new File(imgPath);  
        BufferedImage bufImg = null;  
        String content = null;  
        try {  
            bufImg = ImageIO.read(imageFile);  
            QRCodeDecoder decoder = new QRCodeDecoder();  
            content = new String(decoder.decode(new TwoDimensionCodeImage(bufImg)), "utf-8");   
        } catch (IOException e) {  
            System.out.println("Error: " + e.getMessage());  
            e.printStackTrace();  
        } catch (DecodingFailedException dfe) {  
            System.out.println("Error: " + dfe.getMessage());  
            dfe.printStackTrace();  
        }  
        return content;  
    }  
      
    /** 
     * ������ά�루QRCode�� 
     * @param input ������ 
     * @return 
     */  
    public String decoderQRCode(InputStream input) {  
        BufferedImage bufImg = null;  
        String content = null;  
        try {  
            bufImg = ImageIO.read(input);  
            QRCodeDecoder decoder = new QRCodeDecoder();  
            content = new String(decoder.decode(new TwoDimensionCodeImage(bufImg)), "utf-8");   
        } catch (IOException e) {  
            System.out.println("Error: " + e.getMessage());  
            e.printStackTrace();  
        } catch (DecodingFailedException dfe) {  
            System.out.println("Error: " + dfe.getMessage());  
            dfe.printStackTrace();  
        }  
        return content;  
    }  
  
    public static void main(String[] args) {  
        String imgPath = "D:/apache-tomcat-8.0.33/testBinaryCode/QrCodeLoginPro/img/57145_1468064839.png";  
//        String encoderContent = "http://localhost:8080/QrCodeLoginPro/Login.html";  
        TwoDimensionCode handler = new TwoDimensionCode();  
//        handler.encoderQRCode(encoderContent, imgPath, "png");
        
        
        System.out.println("========encoder success");  
        String decoderContent = handler.decoderQRCode(imgPath);  
        System.out.println("����������£�");  
        System.out.println(decoderContent);  
        System.out.println("========decoder success!!!");  
    }  
}  
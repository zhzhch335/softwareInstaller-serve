package softwareInstaller;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * @describe 主控类，用于调用加密函数以及与数据类通信
 * @author Zhao Zhichen
 * @time 2017.08.10 下午1:07:58
 * @version softwareInstaller for serve.17.08.10
 * @see	
 */
public final class Main {

	// 软件版本默认值
	private static String softwareVersion = "1.2.364";

	// 功能开关默认值
	private static String funcationSwitch = "false";

	/*
	 * 文件读写相关
	 * 
	 */

	/**   
	 * @Title: createKey   
	 * @Description:  调用File类的方法创建密钥文件
	 * @param path 密钥文件路径
	 * @param info 要加密的系统信息
	 * @throws IOException 写入异常
	 * @throws NoSuchAlgorithmException 字典查找失败异常      
	 * @return: void      
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeyException 
	 * @throws InvalidKeySpecException 
	 */  
	public static void createKey(String path,String mKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		KeyCipher accessKey=dataEncode(mKey+" "+Main.softwareVersion+" "+Main.funcationSwitch);
		File.createKeyFile(path, accessKey);
	};

	/**   
	 * @Title: loadMachineKey  
	 * @Description:  加载机器码文件
	 * @param path 机器码文件路径
	 * @throws IOException 读取异常      
	 * @return: boolean      
	 */  
	public static String[] loadMachineKey(String path) throws IOException {
		String[] mKey = File.loadMachineFile(path);
		return mKey;
	};

	/*
	 * 文件读写相关 end
	 * 
	 */

	/*
	 * 信息获取相关
	 * 
	 */

	/**   
	 * @Title: getSoftwareVersion   
	 * @Description:查看版本号  
	 * @return: String      
	 */  
	public static String getSoftwareVersion() {
		return softwareVersion;
	}

	/**   
	 * @Title: getFuncationSwitch   
	 * @Description:  查看功能开关状态
	 * @return: String      
	 */  
	public static String getFuncationSwitch() {
		return funcationSwitch;
	}

	/*
	 * 信息获取相关end
	 * 
	 */
	
  
	private static KeyCipher dataEncode(String mKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeySpecException {
		KeyCipher key=new KeyCipher();
		KeyPairGenerator keyGene=KeyPairGenerator.getInstance("RSA");/*密钥生成器*/
		keyGene.initialize(2048);/*指定密钥大小*/
		KeyPair kp=keyGene.generateKeyPair();/*生成密钥*/
		PrivateKey priKey=kp.getPrivate();/*获取私钥*/
		PublicKey pubKey=kp.getPublic();/*获取公钥*/
		RSAPublicKey rsaPubKey=(RSAPublicKey)pubKey;/*转化为RSA私钥用于获取m和e*/
		RSAPrivateKey rsaPriKey=(RSAPrivateKey)priKey;/*转化为RSA私钥用于获取m和e*/
		BigInteger modulus=rsaPubKey.getModulus();/*获取模*/
		Cipher cp=Cipher.getInstance("RSA");/*加密器实例化*/
		cp.init(Cipher.ENCRYPT_MODE, pubKey);/*加密器配置*/
		byte[] mkey1=mKey.getBytes();
		key.result=cp.doFinal(mkey1);/*加密结果返回*/
		key.prikey=String.valueOf(rsaPriKey.getPrivateExponent());/*私钥返回*/
		key.pubkey=String.valueOf(rsaPubKey.getPublicExponent());/*公钥返回*/
		key.modulus=String.valueOf(modulus);/*模返回*/		
		return key;
	}
	

	/**   
	 * @Title: setSoftwareVersion   
	 * @Description:  设置版本号
	 * @param softwareVersion      
	 * @return: void      
	 */  
	public static void setSoftwareVersion(String softwareVersion) {
		Main.softwareVersion = softwareVersion;
	}

	/**   
	 * @Title: setFuncationSwitch   
	 * @Description:切换功能开关状态  
	 * @param funcationSwitch      参数为字符串“true”或是字符串“false”，分别代表功能开和功能关
	 * @return: void      
	 */  
	public static void setFuncationSwitch(String funcationSwitch) {
		Main.funcationSwitch = funcationSwitch;
	}
	

}
class KeyCipher{
	public String prikey;
	public String pubkey;
	public String modulus;
	public byte[] result;
}

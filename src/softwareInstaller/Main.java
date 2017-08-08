package softwareInstaller;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @describe 主控类，用于调用加密函数以及与数据类通信
 * @author Zhao Zhichen
 * @time 2017.08.07 下午2:26:52
 * @version softwareInstaller.17.08.07
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
	 */  
	public static void createKey(String path,String[] info) throws IOException, NoSuchAlgorithmException {
		String accessKey=dataEncode(info[0],info[1],info[2],info[3]);
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
		String[] info= {};
		String loadKey = File.loadMachineFile(path);
		Scanner sc=new Scanner(loadKey);
		info[0]=sc.next("++");
		info[1]=sc.next("++");
		info[2]=sc.next("++");
		info[3]=sc.next("++");
		sc.close();
		return info;
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
	
	/**   
	 * @Title: dataEncode   
	 * @Description:  SHA加密，使用哈希算法获取序列号
	 * @param cpuId
	 * @param diskId
	 * @param softwareVersion
	 * @param functionSwitch
	 * @throws NoSuchAlgorithmException 未找到字典异常      
	 * @return: String      返回值为32长度的字符串
	 */  
	private static String dataEncode(String cpuId, String diskId, String softwareVersion, String functionSwitch)
			throws NoSuchAlgorithmException {
		String key = "";
		String oriStr = cpuId + diskId + softwareVersion + functionSwitch;
		MessageDigest md = MessageDigest.getInstance("SHA");
		byte[] bkey = md.digest(oriStr.getBytes());
		for (byte i : bkey) {
			key = key + Integer.toHexString((i & 0x000000FF)|0xFFFFFF00).substring(6);
		}
		return key.toUpperCase();
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

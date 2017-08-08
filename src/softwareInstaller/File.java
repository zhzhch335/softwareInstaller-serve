package softwareInstaller;

import java.io.*;


/**
 * @describe 数据类，用于序列号的读写操作 
 * @author Zhao Zhichen
 * @time 2017.08.07 下午2:26:25
 * @version softwareInstaller.17.08.07
 * @see	
 */
public class File {


	/**   
	 * @Title: createKeyFile   
	 * @Description: 创建密钥文件   
	 * @param url 密钥文件地址
	 * @param key 密钥
	 * @throws IOException 写入异常      
	 * @return: void      
	 */  
	public static void createKeyFile(String url, String key) throws IOException {
		Writer wr = new FileWriter(url);
		wr.append(key);
		wr.close();
	}

	/**   
	 * @Title: loadMachineFile   
	 * @Description:  加载机器码文件
	 * @param url 机器码文件地址
	 * @throws IOException   读取异常   
	 * @return: String      
	 */   
	public static String loadMachineFile(String url) throws IOException {
		byte[] keybyte= {};
		FileInputStream fis=new FileInputStream(url);
		fis.read(keybyte);
		String key =new String(keybyte);
		fis.close();
		return key;
	}
}
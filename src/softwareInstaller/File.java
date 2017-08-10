package softwareInstaller;

import java.io.*;

/**
 * @describe 数据类，用于序列号的读写操作
 * @author Zhao Zhichen
 * @time 2017.08.10 下午1:03:36
 * @version softwareInstaller for serve.17.08.10
 * @see
 */
public class File {

	/**
	 * @Title: createKeyFile
	 * @Description: 创建密钥文件
	 * @param url
	 *            密钥文件地址
	 * @param key
	 *            密钥
	 * @throws IOException
	 *             写入异常
	 * @return: void
	 */
	public static void createKeyFile(String url, KeyCipher key) throws IOException {
		Writer wr = new FileWriter(url);
		wr.append(byteArrayToHexString(key.result) + '\n');
		wr.append(key.modulus + '\n');
		wr.append(key.prikey);
		wr.close();
	}

	/**
	 * @Title: loadMachineFile
	 * @Description: 加载机器码文件
	 * @param url
	 *            机器码文件地址
	 * @throws IOException
	 *             读取异常
	 * @return: String
	 */
	public static String[] loadMachineFile(String url) throws IOException {
		String[] key = new String[3];
		FileReader fr = new FileReader(url);
		BufferedReader br = new BufferedReader(fr);
		key[0] = br.readLine();/*加載機器碼*/
		key[1] = br.readLine();/*加載版本號*/
		key[2] = br.readLine();/*加載功能開關狀態*/
		br.close();
		return key;
	}

	/**   
	 * @Title: byteArrayToHexString   
	 * @Description:  将字节数组转化为十六进制数字字符串
	 * @param b 要转化的字节数组
	 * @return: String      
	 */  
	public static String byteArrayToHexString(byte[] b) {
		String result = "";
		String tempresult = "";
		for (Byte i : b) {
			tempresult = Integer.toHexString(i.intValue() + 128);
			if (tempresult.length() == 1) {
				tempresult = "0" + tempresult;
			}
			result = result + tempresult;
		}
		return result;

	}

}
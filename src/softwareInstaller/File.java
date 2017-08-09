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
	public static void createKeyFile(String url, KeyCipher key) throws IOException {
		Writer wr = new FileWriter(url);
		wr.append(byteArrayToHexString(key.result)+'\n');
		wr.append(key.modulus+'\n');
		wr.append(key.prikey);
		wr.close();
	}

	/**   
	 * @Title: loadMachineFile   
	 * @Description:  加载机器码文件
	 * @param url 机器码文件地址
	 * @throws IOException   读取异常   
	 * @return: String      
	 */   
	public static String[] loadMachineFile(String url) throws IOException {
		String[] key=new String[3];
		FileReader fr=new FileReader(url);
		BufferedReader br=new BufferedReader(fr);
		key[0]=br.readLine();
		key[1]=br.readLine();
		key[2]=br.readLine();
		br.close();
		return key;
	}
	
	public static String byteArrayToHexString(byte[] b) {
		String result="";
		String tempresult="";
		for(Byte i : b) {
			tempresult=Integer.toHexString(i.intValue()+128);
			if(tempresult.length()==1) {
				tempresult="0"+tempresult;
			}
			result=result+tempresult;
		}
		return result;
		
	}
	
	static byte[] hexStringToByteArray(String s) {
		String[] arr=s.split("");
		byte[] result=new byte[arr.length/2];
		int k=0;
		for(int i=0;i<=arr.length-2;i=i+2) {
			int tempint=Integer.parseInt(arr[i]+arr[i+1], 16)-128;
			result[k]=(byte)tempint;
			k++;
		}
		return result;
	}
}
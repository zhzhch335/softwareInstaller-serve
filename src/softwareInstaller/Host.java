package softwareInstaller;

import java.awt.EventQueue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.jvnet.substance.*;
import org.jvnet.substance.skin.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.Toolkit;

/**
 * @describe 服务端UI类，用于读取机器码生成注册文件
 * @author Zhao Zhichen
 * @time 2017.08.10 下午1:06:23
 * @version softwareInstaller for serve.17.08.10
 * @see
 */
public class Host {

	public static void main(String[] args) {

		/*
		 * 使用substance美化界面
		 */
		JFrame.setDefaultLookAndFeelDecorated(true);

		JDialog.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager.setLookAndFeel(new SubstanceLookAndFeel());
			SubstanceLookAndFeel.setSkin(new BusinessBlueSteelSkin());
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		/*
		 * 使用substance美化界面end
		 */

		/*
		 * 将UI绘制操作加入EDT中，加载窗体和组件 （线程安全）
		 */
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		/*
		 * 将UI绘制操作加入EDT中，加载窗体和组件 （线程安全）end
		 */
	}

	/**
	 * 窗体和组件初始化
	 * 
	 * @throws IOException
	 *             文件读写异常处理
	 * @throws NoSuchAlgorithmException
	 *             加密方法异常处理
	 */
	private static void initialize() throws IOException, NoSuchAlgorithmException {

		// 窗体
		JFrame frmHey = new JFrame();
		frmHey.setResizable(false);/* 禁止调整大小 */
		frmHey.setIconImage(
				Toolkit.getDefaultToolkit().getImage(Host.class.getResource("/resource/computer.png")));/* 设置左上角图标 */
		frmHey.getContentPane().setFont(new Font("方正兰亭超细黑简体", Font.PLAIN, 12));
		frmHey.setTitle("软件注册机-服务端");
		frmHey.setBounds(500, 200, 677, 427);
		frmHey.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);/* 设置窗体关闭时结束进程 */
		frmHey.getContentPane().setLayout(null);

		// 创建一个空对话框作为文件选择框和提示框的容器
		final JDialog dialog = new JDialog();
		dialog.setBounds(200, 200, 640, 480);

		// 生产按钮，提前实例化为了调用
		final JButton init = new JButton("生成");

		/*
		 * 上方信息显示区（也要放在登陆代码前面，原因同上）
		 * 
		 */

		// 机器码提示标签
		final JLabel mKeyHint = new JLabel("用户机器码：");
		mKeyHint.setBounds(274, 79, 79, 15);
		frmHey.getContentPane().add(mKeyHint);

		// 机器码显示标签
		final JLabel mKeyText = new JLabel("");
		mKeyText.setBounds(361, 79, 300, 15);
		frmHey.getContentPane().add(mKeyText);
		mKeyText.setText("未获取，请加载机器码文件后获取");

		// 版本提示标签
		final JLabel vHint = new JLabel("所需版本号：");
		vHint.setBounds(274, 128, 96, 15);
		frmHey.getContentPane().add(vHint);

		// 版本文本框
		final JTextField vText = new JTextField("未获取，请加载机器码文件后获取");
		vText.setEnabled(false);
		vText.addFocusListener(new FocusAdapter() {
			/**
			 * Title: focusLost Description: 焦点消失后进行修改版本号操作
			 * 
			 * @param e
			 *            焦点消失响应事件
			 * @see java.awt.event.FocusAdapter#focusLost(java.awt.event.FocusEvent)
			 */
			@Override
			public void focusLost(FocusEvent e) {
				if (!Main.getSoftwareVersion().contentEquals(vText.getText())) {/* 判断当前文本框中数据与原数据是否相同，相同则不必修改 */
					final JPasswordField pswinput = new JPasswordField();
					pswinput.setEchoChar('*');
					JLabel pswhint = new JLabel("【注意】请在与客户确认之后再修改软件版本号，否则会导致注册文件不匹配，要修改版本号，请输入密码：");
					Object[] obj = { pswhint, pswinput };
					JOptionPane jobj = new JOptionPane(obj, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
							null, null, null);
					JDialog jd = jobj.createDialog("密码");
					pswinput.requestFocus(true);
					new Thread(new Runnable() {/* 独立线程，休眠50毫秒待dialog绘制完毕后再进行焦点定位 */
						public void run() {
							try {
								Thread.sleep(50);
								pswinput.requestFocus();
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
						}
					}).start();
					jd.setVisible(true);
					StringBuffer psw = new StringBuffer("5858");
					String inputstr = String.valueOf(pswinput.getPassword());
					if (jobj.getValue() == null || !jobj.getValue().equals(0)) {/* 避免取消和关闭对话框的时候报错 */
						inputstr = null;
					}
					if (inputstr != null) {
						if (inputstr.contentEquals(psw)) {
							Main.setSoftwareVersion(vText.getText());
						} else {
							JOptionPane.showMessageDialog(dialog, "密码错误，修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
							vText.setText(Main.getSoftwareVersion());
						}
					} else {/* 不输入密码则将版本号复原 */
						vText.setText(Main.getSoftwareVersion());
					}
				}
			}
		});
		vText.setBounds(361, 125, 212, 21);
		vText.setColumns(10);
		frmHey.getContentPane().add(vText);

		// 功能输入标签
		final JLabel fHint = new JLabel("所需功能：");
		fHint.setBounds(274, 157, 79, 15);
		frmHey.getContentPane().add(fHint);

		// 功能切换按钮
		final JToggleButton fSwitch = new JToggleButton();
		if (Main.getFuncationSwitch().contentEquals("true")) {/* 获取初始状态 */
			fSwitch.setText("开");
			fSwitch.setSelected(true);
		} else {
			fSwitch.setText("关");
			fSwitch.setSelected(false);
		}

		fSwitch.addActionListener(new ActionListener() {

			/**
			 * Title: actionPerformed Description: 通用事件唤起功能切换修改操作
			 * 
			 * @param e
			 *            通用响应事件
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				final JPasswordField pswinput = new JPasswordField();
				pswinput.setEchoChar('*');
				JLabel pswhint = new JLabel("【注意】请在与客户确认之后再修改功能开关，否则会导致注册文件不匹配，要修改版本号，请输入密码：");
				Object[] obj = { pswhint, pswinput };
				JOptionPane jobj = new JOptionPane(obj, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
						null, null, null);
				JDialog jd = jobj.createDialog("密码");
				pswinput.requestFocus(true);
				new Thread(new Runnable() {/* 独立线程，休眠50毫秒待dialog绘制完毕后再进行焦点定位 */
					public void run() {
						try {
							Thread.sleep(50);
							pswinput.requestFocus();
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}

					}
				}).start();
				jd.setVisible(true);
				StringBuffer psw = new StringBuffer("5858");
				String inputstr = String.valueOf(pswinput.getPassword());
				if (jobj.getValue() == null || !jobj.getValue().equals(0)) {
					inputstr = null;
				}
				if (inputstr != null) {
					if (inputstr.contentEquals(psw)) {/* 修复了点击取消后会报错的bug */
						if (fSwitch.isSelected()) {
							fSwitch.setText("开");
							Main.setFuncationSwitch("true");
							SubstanceLookAndFeel.setSkin(new MangoSkin());
						} else {
							fSwitch.setText("关");
							Main.setFuncationSwitch("false");
							SubstanceLookAndFeel.setSkin(new BusinessBlueSteelSkin());
						}
					} else {
						JOptionPane.showMessageDialog(dialog, "密码错误，修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
						fSwitch.setSelected(!fSwitch.isSelected());
					}
				} else {
					fSwitch.setSelected(!fSwitch.isSelected());
				}
			}
		});
		fSwitch.setBounds(361, 153, 135, 23);
		fSwitch.setEnabled(false);
		frmHey.getContentPane().add(fSwitch);

		/*
		 * 上方信息显示区end
		 * 
		 */

		/**
		 * 下方操作区
		 */

		// 生成文件文本框
		final JTextField initText = new JTextField();
		initText.setBounds(209, 295, 287, 21);
		frmHey.getContentPane().add(initText);

		// 提示标签
		final JLabel initHint = new JLabel("请输入生成注册文件的路径：");
		initHint.setFont(new Font("微软雅黑", Font.BOLD, 12));
		initHint.setBounds(209, 265, 179, 15);
		frmHey.getContentPane().add(initHint);

		// 获取桌面路径
		final File fsv = FileSystemView.getFileSystemView().getHomeDirectory();
		final JFileChooser chooser = new JFileChooser(fsv);/* 将初始位置设定为桌面 */
		FileNameExtensionFilter filter = new FileNameExtensionFilter("密钥文件 .key", "key");
		chooser.setFileFilter(filter);/* 设置并加载文件过滤器 */

		// 浏览按钮，用于唤起文件选择对话框加载机器码文件
		final JButton loadMkeyFile = new JButton("从本地文件获取机器码");
		loadMkeyFile.addMouseListener(new MouseAdapter() {
			/**
			 * Title: mouseClicked Description: 鼠标点击唤起文件选择对话框
			 * 
			 * @param e
			 *            鼠标响应事件
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser(fsv);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("机器码文件 .mkey", "mkey");
				chooser.setFileFilter(filter);/* 设置并加载新的文件过滤器 */
				int returnVal = chooser.showOpenDialog(dialog);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						String[] mKey = Main.loadMachineKey(chooser.getSelectedFile().getPath());
						mKeyText.setText(mKey[0]);
						vText.setText(mKey[1]);
						if (mKey[2].contentEquals("true")) {
							fSwitch.setText("开");
							Main.setFuncationSwitch("true");
							SubstanceLookAndFeel.setSkin(new MangoSkin());
						} else {
							fSwitch.setText("关");
							Main.setFuncationSwitch("false");
							SubstanceLookAndFeel.setSkin(new BusinessBlueSteelSkin());
						}
						vText.setEnabled(true);
						fSwitch.setEnabled(true);
						init.setEnabled(true);
						init.addMouseListener(new MouseAdapter() {/* 机器码文件调用成功后才能进行生成 */
							/**
							 * Title: mouseClicked Description: 鼠标点击调用生成密钥方法
							 * 
							 * @param e
							 *            鼠标响应事件
							 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
							 */
							public void mouseClicked(MouseEvent e) {
								try {
									try {
										String mKey = mKeyText.getText();
										Main.createKey(initText.getText(), mKey);
									} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
											| IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException
											| NullPointerException e1) {
										e1.printStackTrace();
									}
									JOptionPane.showMessageDialog(dialog, "写入文件成功，写入路径为" + initText.getText(), "完成",
											JOptionPane.INFORMATION_MESSAGE);
								} catch (IOException e1) {
									JOptionPane.showMessageDialog(dialog, "路径错误或无权限写入文件，请稍后或更换路径再试", "错误",
											JOptionPane.ERROR_MESSAGE);
								}
							}
						});
					} catch (IOException | NullPointerException e1) {
						mKeyText.setText("获取失败");
						vText.setText("获取失败");
						JOptionPane.showMessageDialog(dialog, "读取机器码信息失败，请稍后或更换路径再试", "错误", JOptionPane.ERROR_MESSAGE);
					}

				}
			}
		});
		loadMkeyFile.setBounds(225, 216, 156, 23);
		frmHey.getContentPane().add(loadMkeyFile);

		// 生成按钮
		init.setEnabled(false);/* 未获取机器码时不可用 */
		init.setBounds(361, 346, 69, 23);
		frmHey.getContentPane().add(init);

		/**
		 * 下方操作区end
		 */

		/*
		 * 左侧提示区域
		 * 
		 */
		JTextPane txtpnkey = new JTextPane();
		txtpnkey.setText("\r\n\r\n\r\n\r\n    生成说明：\r\n     1、载入机器码文件获取机器码及软件配置信息\r\n     2、核对购买信息后生成注册文件");
		txtpnkey.setEditable(false);
		txtpnkey.setBounds(0, 0, 169, 399);
		frmHey.getContentPane().add(txtpnkey);

		JButton initFile = new JButton("浏览...");
		initFile.setBounds(529, 294, 79, 23);
		frmHey.getContentPane().add(initFile);
		initFile.addMouseListener(new MouseAdapter() {
			/**
			 * Title: mouseClicked Description: 鼠标点击唤起文件选择对话框
			 * 
			 * @param e
			 *            鼠标响应事件
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser(fsv);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("注册文件 .key", "key");
				chooser.setFileFilter(filter);/* 设置并加载新的文件过滤器 */
				int returnVal = chooser.showSaveDialog(dialog);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String url = chooser.getSelectedFile().getPath();
					if (!url.endsWith(".key")) {
						url = url + ".key";
					}
					initText.setText(url);
				}
			}
		});
		/*
		 * 左侧提示区域end
		 * 
		 */

		// 显示窗体
		frmHey.setVisible(true);

	}
}

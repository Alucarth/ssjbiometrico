package com.zkteco.biometric;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.codec.binary.Base64;



public class RegistroInnaj extends JFrame{

	JButton btnOpen = null;
	JButton btnEnroll = null;
	JButton btnVerify = null;
	JButton btnImg = null;
	JButton btnBuscar = null;
	JButton btnClose = null;
	JLabel lblInnaj;
	JLabel lblDetalleInnaj;
	JTextField textBuscar;
	private JTextArea textArea;
	private JTextArea textInnaj;
	
	private Innaj innaj;
	private Rest rest;
	
	//the width of fingerprint image
		int fpWidth = 0;
		//the height of fingerprint image
		int fpHeight = 0;
		//for verify test
		private byte[] lastRegTemp = new byte[2048];
		//the length of lastRegTemp
		private int cbRegTemp = 0;
		//pre-register template
		private byte[][] regtemparray = new byte[3][2048];
		//Register
		private boolean bRegister = false;
		//Identify
		private boolean bIdentify = true;
		//finger id
		private int iFid = 1;
		
		private int nFakeFunOn = 1;
		//must be 3
		static final int enroll_cnt = 3;
		//the index of pre-register function
		private int enroll_idx = 0;
		
		private byte[] imgbuf = null;
		private byte[] template = new byte[2048];
		private int[] templateLen = new int[1];
		
		
		private boolean mbStop = true;
		private long mhDevice = 0;
		private long mhDB = 0;
		private WorkThread workThread = null;
		
		private String imageString;
	
	public void IniciarPrograma()
	{
		JFrame.setDefaultLookAndFeelDecorated(true); 
		innaj = new Innaj();
		this.setLayout (null);
		int nRsize = 20;
		rest = new Rest();
		
		lblInnaj = new JLabel("Child Number");
		this.add(lblInnaj);
		lblInnaj.setBounds(30, 10 + nRsize, 140, 30);
		
		textBuscar = new JTextField();
		this.add(textBuscar);
		textBuscar.setBounds(30, 35 + nRsize, 75, 30);
		
		btnBuscar = new JButton("Buscar");
		this.add(btnBuscar);
		btnBuscar.setBounds(110, 35+ nRsize, 65, 30);
		
		lblDetalleInnaj = new JLabel("Informacion Innaj");
		this.add(lblDetalleInnaj);
		lblDetalleInnaj.setBounds(30, 65 + nRsize, 140, 30);
		
		textInnaj = new JTextArea();
		this.add(textInnaj);
		textInnaj.setBounds(30, 95 + nRsize, 140, 120);
		
		btnOpen = new JButton("Conectar");  
		this.add(btnOpen);  
		btnOpen.setBounds(30,230 + nRsize , 140,30);
		
		btnEnroll = new JButton("Registrar");  
		this.add(btnEnroll);  
		btnEnroll.setBounds(30, 260 + nRsize, 140, 30);
		
//		btnVerify = new JButton("Verificar");  
//		this.add(btnVerify);  
//		btnVerify.setBounds(30, 290 + nRsize, 140, 30);
//		
		btnClose = new JButton("Desconectar");  
		this.add(btnClose);  
		btnClose.setBounds(30, 320 + nRsize, 140, 30);
		
		 
		btnImg = new JButton();
		btnImg.setBounds(200, 5, 288, 375);
		btnImg.setDefaultCapable(false);
		this.add(btnImg); 
		
		textArea = new JTextArea();
		this.add(textArea);  
		textArea.setBounds(10, 420, 490, 150);
		textArea.setLineWrap(true);
		textArea.setSelectedTextColor(Color.RED);
		
		//evento click de los botones
		btnBuscar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
					System.out.println("Inciando Busqueda");
					String idInnaj ="";
					idInnaj =(String) textBuscar.getText();
					
						
						//consumiendo web service
						rest = new Rest();
						Thread t;
						t = new Thread() {
							public void run()
							{
								System.out.println("comenzando a consumir respuesta");
								System.out.println("respuesta: "+rest.getRespuesta());
								innaj = new Innaj();
								innaj.parseJSON(rest.getRespuesta());
								if(innaj.getName() != null)
								{
									String detalle = "Nombre: "+innaj.getName()+" \n";
									detalle += "Apodo:" +innaj.getApodo()+" \n";
									detalle += "CI:"+innaj.getCi()+" \n";
									detalle += "id:"+innaj.getId();
									textInnaj.setText(detalle);
								}else
								{
									textInnaj.setText("No se encontro datos del Innaj");
								}
							}
						};
						rest.EnviarGet(1, idInnaj,t );
						rest.start();
				   
					
//				
			   
				
			}
		});
		
		btnOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (0 != mhDevice)
				{
					//already inited
					textArea.setText("Please close device first!\n");
					return;
				}
				int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
				//Initialize
				cbRegTemp = 0;
				bRegister = false;
				bIdentify = false;
				iFid = 1;
				enroll_idx = 0;
				if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init())
				{
					textArea.setText("Inico Fallido!\n");
					return;
				}
				ret = FingerprintSensorEx.GetDeviceCount();
				if (ret < 0)
				{
					textArea.setText("No hay Dispositivos Conectados!\n");
					FreeSensor();
					return;
				}
				if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0)))
				{
					textArea.setText("Fallo al conectar con el dispositivo, ret = " + ret + "!\n");
					FreeSensor();
					return;
				}
				if (0 == (mhDB = FingerprintSensorEx.DBInit()))
				{
					textArea.setText("Init DB fail, ret = " + ret + "!\n");
					FreeSensor();
					return;
				}
				
				//For ISO/Ansi
				int nFmt = 0;	//Ansi
				/*if (radioISO.isSelected())
				{
					nFmt = 1;	//ISO
				}*/
				FingerprintSensorEx.DBSetParameter(mhDB,  5010, nFmt);				
				//For ISO/Ansi End
				
				//set fakefun off
				//FingerprintSensorEx.SetParameter(mhDevice, 2002, changeByte(nFakeFunOn), 4);
				
				byte[] paramValue = new byte[4];
				int[] size = new int[1];
				//GetFakeOn
				//size[0] = 4;
				//FingerprintSensorEx.GetParameters(mhDevice, 2002, paramValue, size);
				//nFakeFunOn = byteArrayToInt(paramValue);
				
				size[0] = 4;
				FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
				fpWidth = byteArrayToInt(paramValue);
				size[0] = 4;
				FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
				fpHeight = byteArrayToInt(paramValue);
								
				imgbuf = new byte[fpWidth*fpHeight];
				//btnImg.resize(fpWidth, fpHeight);
				mbStop = false;
				workThread = new WorkThread();
			    workThread.start();// ????????????
				textArea.setText("Open succ! Finger Image Width:" + fpWidth + ",Height:" + fpHeight +"\n");
			}
		});
		
		btnEnroll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("evento btnEnroll lanzado");
				if(0 == mhDevice)
				{
					textArea.setText("Por favor Conect el dispositivo primero!\n");
					return;
				}
				
				if(innaj.getName() != null)
				{
					if(imageString != "")
					{
						innaj.setFingerprint(imageString);
//						textArea.setText(innaj.getFingerprint());
						rest = new Rest();
						Thread t;
						t =  new Thread() {
							public void run ()
							{
								System.out.println("consumiendo respuesta");
								textArea.setText(rest.getRespuesta());
							}
						};
//						System.out.print(Innaj.toJson(innaj).toString());
						rest.EnviarPost(2,Innaj.toJson(innaj).toString() , t);
						rest.start();

						
					}else {
						textArea.setText("Coloque su huella en el disposito!\n");
					}
					
				}
			}
		});
		
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				FreeSensor();
				
				textArea.setText("Se Desconecto el Dispositivo!\n");
			}
		});
		this.setSize(520, 620);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setTitle("Registro Innaj");
		this.setResizable(false);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub
            	FreeSensor();
            }
		});
	}
	
	
	//liberar Sensor
	private void FreeSensor()
	{
		mbStop = true;
		try {		//wait for thread stopping
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (0 != mhDB)
		{
			FingerprintSensorEx.DBFree(mhDB);
			mhDB = 0;
		}
		if (0 != mhDevice)
		{
			FingerprintSensorEx.CloseDevice(mhDevice);
			mhDevice = 0;
		}
		FingerprintSensorEx.Terminate();
	}
	
	
	
	//clase adicional para el asincronismo del biometrico
	private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();
            int ret = 0;
            while (!mbStop) {
            	templateLen[0] = 2048;
            	if (0 == (ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen)))
            	{
            		if (nFakeFunOn == 1)
                	{
                		byte[] paramValue = new byte[4];
        				int[] size = new int[1];
        				size[0] = 4;
        				int nFakeStatus = 0;
        				//GetFakeStatus
        				ret = FingerprintSensorEx.GetParameters(mhDevice, 2004, paramValue, size);
        				nFakeStatus = byteArrayToInt(paramValue);
        				System.out.println("ret = "+ ret +",nFakeStatus=" + nFakeStatus);
        				if (0 == ret && (byte)(nFakeStatus & 31) != 31)
        				{
        					textArea.setText("Is a fake finger?\n");
        					return;
        				}
                	}
                	OnCatpureOK(imgbuf);
//                	OnExtractOK(template, templateLen[0]);
                	FileInputStream fis;
					try {
						System.out.println("iniciando metodo de conversion 64");
						String path =  Paths.get("fingerprint.bmp").toAbsolutePath().toString();
						File f = new File(path); //change path of image according to you
						fis = new FileInputStream(f);
						byte byteArray[] = new byte[(int)f.length()];
                    	fis.read(byteArray);
                    	
                    	imageString = Base64.encodeBase64String(byteArray);
                    	
//                    	if(innaj.getId()!= null)
//                    	{
//                    		innaj.setFingerprint(imageString);
//                    	}
//                    	textArea.setText(imageString);
//                    	
//                    	System.out.println(imageString);
                    	fis.close();
					} catch (FileNotFoundException e) {
						
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
	
	private void OnCatpureOK(byte[] imgBuf)
	{
		try {
			writeBitmap(imgBuf, fpWidth, fpHeight, "fingerprint.bmp");
			btnImg.setIcon(new ImageIcon(ImageIO.read(new File("fingerprint.bmp"))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void OnExtractOK(byte[] template, int len)
	{
		if(bRegister)
		{
			int[] fid = new int[1];
			int[] score = new int [1];
            int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
            if (ret == 0)
            {
                textArea.setText("the finger already enroll by " + fid[0] + ",cancel enroll\n");
                bRegister = false;
                enroll_idx = 0;
                return;
            }
            if (enroll_idx > 0 && FingerprintSensorEx.DBMatch(mhDB, regtemparray[enroll_idx-1], template) <= 0)
            {
            	textArea.setText("please press the same finger 3 times for the enrollment\n");
                return;
            }
            System.arraycopy(template, 0, regtemparray[enroll_idx], 0, 2048);
            enroll_idx++;
            if (enroll_idx == 3) {
            	int[] _retLen = new int[1];
                _retLen[0] = 2048;
                byte[] regTemp = new byte[_retLen[0]];
                
                if (0 == (ret = FingerprintSensorEx.DBMerge(mhDB, regtemparray[0], regtemparray[1], regtemparray[2], regTemp, _retLen)) &&
                		0 == (ret = FingerprintSensorEx.DBAdd(mhDB, iFid, regTemp))) {
                	iFid++;
                	cbRegTemp = _retLen[0];
                    System.arraycopy(regTemp, 0, lastRegTemp, 0, cbRegTemp);
                    //Base64 Template
                    textArea.setText("enroll succ:\n");
                } else {
                	textArea.setText("enroll fail, error code=" + ret + "\n");
                }
                bRegister = false;
            } else {
            	textArea.setText("You need to press the " + (3 - enroll_idx) + " times fingerprint\n");
            }
		}
		else
		{
			if (bIdentify)
			{
				int[] fid = new int[1];
				int[] score = new int [1];
				int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
                if (ret == 0)
                {
                	textArea.setText("Identify succ, fid=" + fid[0] + ",score=" + score[0] +"\n");
                }
                else
                {
                	textArea.setText("Identify fail, errcode=" + ret + "\n");
                }
                    
			}
			else
			{
				if(cbRegTemp <= 0)
				{
					textArea.setText("Please register first!\n");
				}
				else
				{
					int ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, template);
					if(ret > 0)
					{
						textArea.setText("Verify succ, score=" + ret + "\n");
					}
					else
					{
						textArea.setText("Verify fail, ret=" + ret + "\n");
					}
				}
			}
		}
	}
	
	public static void writeBitmap(byte[] imageBuf, int nWidth, int nHeight,
			String path) throws IOException {
		java.io.FileOutputStream fos = new java.io.FileOutputStream(path);
		java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

		int w = (((nWidth+3)/4)*4);
		int bfType = 0x424d; // ?????????????????????0???1?????????
		int bfSize = 54 + 1024 + w * nHeight;// bmp??????????????????2???5?????????
		int bfReserved1 = 0;// ?????????????????????????????????0???6-7?????????
		int bfReserved2 = 0;// ?????????????????????????????????0???8-9?????????
		int bfOffBits = 54 + 1024;// ??????????????????????????????????????????????????????????????????10-13?????????

		dos.writeShort(bfType); // ????????????????????????'BM'
		dos.write(changeByte(bfSize), 0, 4); // ????????????????????????
		dos.write(changeByte(bfReserved1), 0, 2);// ???????????????????????????
		dos.write(changeByte(bfReserved2), 0, 2);// ???????????????????????????
		dos.write(changeByte(bfOffBits), 0, 4);// ???????????????????????????

		int biSize = 40;// ??????????????????????????????14-17?????????
		int biWidth = nWidth;// ???????????????18-21?????????
		int biHeight = nHeight;// ???????????????22-25?????????
		int biPlanes = 1; // ?????????????????????????????????1???26-27?????????
		int biBitcount = 8;// ??????????????????????????????28-29?????????????????????1??????????????????4??????16?????????8??????256????????????24???????????????????????????
		int biCompression = 0;// ??????????????????????????????0??????????????????30-33????????????1???BI_RLEB??????????????????2???BI_RLE4????????????????????????
		int biSizeImage = w * nHeight;// ?????????????????????????????????????????????????????????????????????34-37?????????
		int biXPelsPerMeter = 0;// ??????????????????????????????????????????38-41????????????????????????????????????
		int biYPelsPerMeter = 0;// ??????????????????????????????????????????42-45????????????????????????????????????
		int biClrUsed = 0;// ????????????????????????????????????????????????46-49?????????????????????0??????????????????????????????
		int biClrImportant = 0;// ???????????????????????????????????????(50-53??????)????????????0???????????????????????????

		dos.write(changeByte(biSize), 0, 4);// ????????????????????????????????????
		dos.write(changeByte(biWidth), 0, 4);// ??????????????????
		dos.write(changeByte(biHeight), 0, 4);// ??????????????????
		dos.write(changeByte(biPlanes), 0, 2);// ?????????????????????????????????
		dos.write(changeByte(biBitcount), 0, 2);// ????????????????????????????????????
		dos.write(changeByte(biCompression), 0, 4);// ???????????????????????????
		dos.write(changeByte(biSizeImage), 0, 4);// ???????????????????????????
		dos.write(changeByte(biXPelsPerMeter), 0, 4);// ??????????????????????????????
		dos.write(changeByte(biYPelsPerMeter), 0, 4);// ??????????????????????????????
		dos.write(changeByte(biClrUsed), 0, 4);// ?????????????????????????????????
		dos.write(changeByte(biClrImportant), 0, 4);// ?????????????????????????????????????????????

		for (int i = 0; i < 256; i++) {
			dos.writeByte(i);
			dos.writeByte(i);
			dos.writeByte(i);
			dos.writeByte(0);
		}

		byte[] filter = null;
		if (w > nWidth)
		{
			filter = new byte[w-nWidth];
		}
		
		for(int i=0;i<nHeight;i++)
		{
			dos.write(imageBuf, (nHeight-1-i)*nWidth, nWidth);
			if (w > nWidth)
				dos.write(filter, 0, w-nWidth);
		}
		dos.flush();
		dos.close();
		fos.close();
	}

	public static byte[] changeByte(int data) {
		return intToByteArray(data);
	}
	
	public static byte[] intToByteArray (final int number) {
		byte[] abyte = new byte[4];  
	    // "&" ??????AND??????????????????????????????????????????????????????????????????????????????1?????????1?????????0???  
	    abyte[0] = (byte) (0xff & number);  
	    // ">>"????????????????????????????????????0???????????????????????????1  
	    abyte[1] = (byte) ((0xff00 & number) >> 8);  
	    abyte[2] = (byte) ((0xff0000 & number) >> 16);  
	    abyte[3] = (byte) ((0xff000000 & number) >> 24);  
	    return abyte; 
	}	 
		 
	public static int byteArrayToInt(byte[] bytes) {
		int number = bytes[0] & 0xFF;  
	    // "|="??????????????????  
	    number |= ((bytes[1] << 8) & 0xFF00);  
	    number |= ((bytes[2] << 16) & 0xFF0000);  
	    number |= ((bytes[3] << 24) & 0xFF000000);  
	    return number;  
	 }
	
	//end clase interna
	
	public static void main(String[] args) {
		
	    try {
            // Set System L&F
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    	
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
	    new RegistroInnaj().IniciarPrograma();
//		ZKFPDemo zk = new ZKFPDemo();
//		zk.launchFrame();
//		Rest rest = new Rest();
//		Innaj innaj = new Innaj();
//		innaj.parseJSON((String) rest.GetRestful(Rest.URL, Rest.INNAJ+'1'));
//		zk.textArea.setText("Nombre: "+innaj.name+  "\n");
	}
}

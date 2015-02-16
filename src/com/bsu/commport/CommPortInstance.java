package com.bsu.commport;

import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;

/**
 * 用来操作串口类
 * @author fengchong
 *
 */
public class CommPortInstance {
	private static CommPortInstance instance = null;
	public static CommPortInstance getInstance(){
		if(instance==null)
			instance = new CommPortInstance();
		return instance;
	}
	private CommPortInstance(){}
	
	private CommPortIdentifier portId;		//端口标识
	private Enumeration portList;			//端口列表
	
	private SerialPort serialPort;			//串口对象
	private SerialReader sreader;			//串口读取对象
	private SerialWriter swriter;			//串口写对象
	/**
	 * 初始化串口端口
	 * @param pname		端口名
	 */
	public void initCommPort(String pname){			
		portList = CommPortIdentifier.getPortIdentifiers();
		while(portList.hasMoreElements()){
			portId = (CommPortIdentifier) portList.nextElement();
			if(portId.getPortType() == CommPortIdentifier.PORT_SERIAL){
				if(portId.getName().equals(pname)){
					try {
						serialPort = (SerialPort) portId.open("SerialReader", 2000); 	//获得串口对象
						sreader = new SerialReader(serialPort);							//生成串口读取对象
						swriter = new SerialWriter(serialPort);							//生成串口写入对象
						System.out.println("======================init comm port success");
					} catch (PortInUseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				

					break;
				}
			}
		}
	}
	
	public SerialReader getSerialReader(){return sreader;}
	public SerialWriter getSerialWriter(){return swriter;}
	/**
	 * 关闭串口
	 */
	public void closeSerialPort(){
		serialPort.close();
	}
}

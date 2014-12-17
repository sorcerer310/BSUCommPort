package com.bsu.commport;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

/**
 * 串口读取类
 * @author fengchong
 *
 */
public class SerialReader{
	private SerialPort serialPort;
	private InputStream inputStream;
	private Thread readThread;
	private SerialReaderListener listener = null;										//监听对象
	
	public SerialReader(SerialPort sport){
		try{
			serialPort = sport; 
			inputStream = serialPort.getInputStream();									//从串口获得输入流
			serialPort.addEventListener(new SerialPortEventListener(){					//为串口增加事件监听
				@Override
				public void serialEvent(SerialPortEvent event) {
					switch(event.getEventType()) {
			        case SerialPortEvent.BI:
			        case SerialPortEvent.OE:
			        case SerialPortEvent.FE:
			        case SerialPortEvent.PE:
			        case SerialPortEvent.CD:
			        case SerialPortEvent.CTS:
			        case SerialPortEvent.DSR:
			        case SerialPortEvent.RI:
			        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			            break;
			        case SerialPortEvent.DATA_AVAILABLE:								//当有可用数据时
			            byte[] readBuffer = new byte[256];								//初始化256字节数组

			            try {
			                while (inputStream.available() > 0) {
			                    int numBytes = inputStream.read(readBuffer);
			                }
//			                System.out.println(new String(readBuffer));
			                listener.readCompleted(new String(readBuffer));				//通知外部命令读取完成		
			            } catch (IOException e) {
			            	e.printStackTrace();
			            }
			            break;
			        }
				}
			});
			
			serialPort.notifyOnDataAvailable(true);
			serialPort.setSerialPortParams(9600,
					SerialPort.DATABITS_7,
					SerialPort.STOPBITS_2,
					SerialPort.PARITY_EVEN);
			
			readThread = new Thread(new Runnable(){
				@Override
				public void run() {
					try{
						Thread.sleep(20000);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			});
			readThread.start();
		}catch(IOException e){
			e.printStackTrace();
		}catch(TooManyListenersException e){
			e.printStackTrace();
		}catch(UnsupportedCommOperationException e){
			e.printStackTrace();
		}
	}
	/**
	 * 设置串口读取的监听器
	 * @param l		从外部设置进来的监听器对象
	 */
	public void setSerialReaderListener(SerialReaderListener l){
		listener = l;
	}
	
	public static void main(String[] args){
		CommPortInstance cp = CommPortInstance.getInstance();
		cp.initCommPort("COM2");
		cp.getSerialReader().setSerialReaderListener(new SerialReaderListener(){
			@Override
			public void readCompleted(String command) {
				System.out.println("============read command:"+command);
			}});
	}
}

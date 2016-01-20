package com.browsesoft.workflow.perform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * 流程图管理器
 */
public class DiagramManager {

	static Logger log = Logger.getLogger(DiagramManager.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 373843474314241569L;

	private static DiagramManager instance = new DiagramManager();

	public static DiagramManager getInstance() {
		return instance;
	}

	/**
	 * 流程图名字与内容列表
	 */
	private Hashtable nameAndDiagrams = new Hashtable();

	/**
	 * 构造管理器
	 */
	public DiagramManager() {
		load();
	}

	/**
	 * 装载所有流程图, 在processes下
	 */
	public void load() {
		String path = this.getClass().getClassLoader()
				.getResource("/processes/").getPath();
		File[] files = new File(path).listFiles();
		for (File f : files) {
			// 得到文件名
			String fileName = f.getName();
			if (fileName == null) {
				continue;
			}
			try {
				// 生成流程图
				String digName = fileName.substring(0,fileName.indexOf("."));
				Diagram diagram = new Diagram();
				diagram.setName(digName);
				nameAndDiagrams.put(digName, diagram);
				// 根据文件路径得到流
				String result = "";
				String encoding = "GBK";
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(f), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					result += lineTxt;
				}
				bufferedReader.close();
				// 设置流程图内容
				diagram.setContext(result);
			} catch (Exception e) {
				log.error("流程" + fileName + ": " + e.getMessage());
			}
		}
	}

	/**
	 * 得到所有流程图
	 * 
	 * @return 流程图列表
	 * @throws RemoteException
	 */
	public Enumeration getDiagrams() throws RemoteException {
		return nameAndDiagrams.elements();
	}

	/**
	 * 取的所有流程图的名字
	 * 
	 * @return 所有流程图的名字
	 */
	public LinkedList getDiagramNames() throws RemoteException {
		return new LinkedList(nameAndDiagrams.keySet());
	}

	/**
	 * 根据流程图名字取的流程图
	 * 
	 * @param name
	 *            流程图名字
	 * @return 流程图
	 */
	public Diagram getDiagramByName(String name) throws RemoteException {
		return (Diagram) nameAndDiagrams.get(name);
	}

}
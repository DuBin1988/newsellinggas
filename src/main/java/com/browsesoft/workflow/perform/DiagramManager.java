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
 * ����ͼ������
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
	 * ����ͼ�����������б�
	 */
	private Hashtable nameAndDiagrams = new Hashtable();

	/**
	 * ���������
	 */
	public DiagramManager() {
		load();
	}

	/**
	 * װ����������ͼ, ��processes��
	 */
	public void load() {
		String path = this.getClass().getClassLoader()
				.getResource("/processes/").getPath();
		File[] files = new File(path).listFiles();
		for (File f : files) {
			// �õ��ļ���
			String fileName = f.getName();
			if (fileName == null) {
				continue;
			}
			try {
				// ��������ͼ
				String digName = fileName.substring(0,fileName.indexOf("."));
				Diagram diagram = new Diagram();
				diagram.setName(digName);
				nameAndDiagrams.put(digName, diagram);
				// �����ļ�·���õ���
				String result = "";
				String encoding = "GBK";
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(f), encoding);// ���ǵ������ʽ
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					result += lineTxt;
				}
				bufferedReader.close();
				// ��������ͼ����
				diagram.setContext(result);
			} catch (Exception e) {
				log.error("����" + fileName + ": " + e.getMessage());
			}
		}
	}

	/**
	 * �õ���������ͼ
	 * 
	 * @return ����ͼ�б�
	 * @throws RemoteException
	 */
	public Enumeration getDiagrams() throws RemoteException {
		return nameAndDiagrams.elements();
	}

	/**
	 * ȡ����������ͼ������
	 * 
	 * @return ��������ͼ������
	 */
	public LinkedList getDiagramNames() throws RemoteException {
		return new LinkedList(nameAndDiagrams.keySet());
	}

	/**
	 * ��������ͼ����ȡ������ͼ
	 * 
	 * @param name
	 *            ����ͼ����
	 * @return ����ͼ
	 */
	public Diagram getDiagramByName(String name) throws RemoteException {
		return (Diagram) nameAndDiagrams.get(name);
	}

}
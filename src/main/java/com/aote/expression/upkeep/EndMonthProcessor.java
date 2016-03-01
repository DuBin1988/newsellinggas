package com.aote.expression.upkeep;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.aote.expression.Param;
import com.aote.expression.paramprocessor.NoFitValueException;
import com.aote.expression.paramprocessor.ParamProcessor;
import com.aote.rs.util.DateTools;

/**
 * ά���ѽ�ֹ����,ȡ��ǰ�����뿪ʼ�����бȽϴ��
 */
public class EndMonthProcessor {
	public Calendar process(Map map) {
		// û�п�ʼ��,���õ�ǰ��
		String startmonth = (String) map.get("startmonth");
		try {
			Calendar start = DateTools.getDate(startmonth, "yyyy-MM-dd");
			Calendar end = this.getEnd(map);
			if (start.get(Calendar.YEAR) > end.get(Calendar.YEAR)
					|| (start.get(Calendar.YEAR) == end.get(Calendar.YEAR) && start
							.get(Calendar.MONTH) > end.get(Calendar.MONTH))) {
				return null;
			}
			// ȡ��ʼ���뵱ǰ���бȽϴ��
			if (end.getTimeInMillis() - start.getTimeInMillis() >= 0) {
				return end;
			} else {
				return start;
			}
		} catch (Exception e) {
			Calendar c = Calendar.getInstance();
			return c;
		}
	}

	/**
	 * �õ������·�
	 */
	private Calendar getEnd(Map map) {
		String type = (String) map.get("consumertype");
		String meter = (String) map.get("metertype");
		Calendar now = Calendar.getInstance();
		// ������ҵ�û�����ֹ����Ϊ�ϸ���
		if (type.equals("��ҵ") && meter.equals("����")) {
			now.add(Calendar.MONTH, -1);
			return now;
		}
		// �����û�����ҵ����ֹ����Ϊ��һ��
		if (type.equals("����") || type.equals("��ҵ") || type.equals("����վ")) {
			// ��ʼ���ڼ�һ���һ����
			String startmonth = (String) map.get("startmonth");
			Calendar start = DateTools.getDate(startmonth, "yyyy-MM-dd");
			// �����ʼʱ����ڵ�ǰʱ�䷵�ؿ�
			if (start.get(Calendar.YEAR) > now.get(Calendar.YEAR)
					|| (start.get(Calendar.YEAR) == now.get(Calendar.YEAR) && start
							.get(Calendar.MONTH) > now.get(Calendar.MONTH))) {
				now.add(Calendar.MONTH, -1);
				return now;
			}
			Calendar end = Calendar.getInstance();
			// ����δ������
			for (int i = 1; i < 10; i++) {
				end.setTime(start.getTime());
				end.add(Calendar.YEAR, i);
				end.add(Calendar.MONTH, -1);
				// �жϽ�ֹ�����Ƿ��ڵ�ǰ����֮��
				if (end.compareTo(now) > 0) {
					return end;
				}
			}
			return end;
		}
		return now;
	}
}

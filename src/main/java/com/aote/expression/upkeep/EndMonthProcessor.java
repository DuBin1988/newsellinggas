package com.aote.expression.upkeep;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.aote.expression.Param;
import com.aote.expression.paramprocessor.NoFitValueException;
import com.aote.expression.paramprocessor.ParamProcessor;
import com.aote.rs.util.DateTools;

/**
 * 维护费截止日期,取当前日期与开始日期中比较大的
 */
public class EndMonthProcessor {
	public Calendar process(Map map) {
		// 没有开始月,采用当前月
		String startmonth = (String) map.get("startmonth");
		try {
			Calendar start = DateTools.getDate(startmonth, "yyyy-MM-dd");
			Calendar end = this.getEnd(map);
			if (start.get(Calendar.YEAR) > end.get(Calendar.YEAR)
					|| (start.get(Calendar.YEAR) == end.get(Calendar.YEAR) && start
							.get(Calendar.MONTH) > end.get(Calendar.MONTH))) {
				return null;
			}
			// 取开始月与当前月中比较大的
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
	 * 得到结束月份
	 */
	private Calendar getEnd(Map map) {
		String type = (String) map.get("consumertype");
		String meter = (String) map.get("metertype");
		Calendar now = Calendar.getInstance();
		// 卡表商业用户，截止日期为上个月
		if (type.equals("商业") && meter.equals("卡表")) {
			now.add(Calendar.MONTH, -1);
			return now;
		}
		// 公福用户，工业，截止日期为下一年
		if (type.equals("公福") || type.equals("工业") || type.equals("加气站")) {
			// 开始日期加一年减一个月
			String startmonth = (String) map.get("startmonth");
			Calendar start = DateTools.getDate(startmonth, "yyyy-MM-dd");
			// 如果开始时间大于当前时间返回空
			if (start.get(Calendar.YEAR) > now.get(Calendar.YEAR)
					|| (start.get(Calendar.YEAR) == now.get(Calendar.YEAR) && start
							.get(Calendar.MONTH) > now.get(Calendar.MONTH))) {
				now.add(Calendar.MONTH, -1);
				return now;
			}
			Calendar end = Calendar.getInstance();
			// 多年未交处理
			for (int i = 1; i < 10; i++) {
				end.setTime(start.getTime());
				end.add(Calendar.YEAR, i);
				end.add(Calendar.MONTH, -1);
				// 判断截止日期是否在当前日期之后
				if (end.compareTo(now) > 0) {
					return end;
				}
			}
			return end;
		}
		return now;
	}
}

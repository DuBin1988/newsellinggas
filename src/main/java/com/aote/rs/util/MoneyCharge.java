package com.aote.rs.util;

import java.math.BigDecimal;

public class MoneyCharge {
	
	public static void main(String[] args) {
		double money;
		for(int i = 0;i<10;i=i+1){
			money = 1 + i ;
			double y=toYuan(money, 100.0, 2);
			System.out.println(y);
		}

		
	}
	public static int toFen(Object value)
	{
		if(value == null)
			return 0;
		BigDecimal fee = new BigDecimal(value.toString());
		fee = fee.multiply(new BigDecimal(100));
		return fee.intValue();
	}
	public static double toYuan(double d1,
			double d2,int len) {
			         BigDecimal b1 = new BigDecimal(d1); 
			         BigDecimal b2 = new BigDecimal(d2); 
			        return b1.divide(b2,len,BigDecimal.
			ROUND_HALF_UP).doubleValue(); 
			     } 

}

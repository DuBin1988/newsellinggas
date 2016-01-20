package com.browsesoft.note;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class SendNoteTimer extends TimerTask{

	
 	
	//待发送列表
	private static   LinkedList sendList = new LinkedList();
	
	public static SendNoteTimer self = null;
	 	
	public static SendNoteTimer getInstance() {
		if (self == null) {
			   Timer timer = new Timer();
			     GregorianCalendar gc = new GregorianCalendar();
			     TimerTask task = new SendNoteTimer();
			     timer.schedule(task,gc.getTime(),10000 );
			     self = new SendNoteTimer();
		}
		return self;
	}
	
	public void addNote(PhoneNote note)
	{
	   this.sendList.add(note);
	}
	
	
     

	@Override
	public void run() {
		LinkedList list  = new LinkedList();
		synchronized(this)
		{
			list.addAll(this.sendList);
			this.sendList.clear();
		}
		for(int i = 0 ; i < list.size() ;i++)
		{
			PhoneNote n = (PhoneNote)list.get(i);
			SMSSender sender = SMSSender.getInstance();
			sender.send_pdu_chi(n.phone, n.message);
			System.out.println("发送短信:"+n.phone+",内容:"+n.message);
		}
	}
	
	
}






 
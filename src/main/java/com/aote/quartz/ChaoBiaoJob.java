package com.aote.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class ChaoBiaoJob extends QuartzJobBean{
	
	private ChaoBiaoTask chaoBiaoTask;
	 
	public void setChaoBiaoTask(ChaoBiaoTask chaoBiaoTask) {
		this.chaoBiaoTask = chaoBiaoTask;
	}

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		if(chaoBiaoTask.finished)
			chaoBiaoTask.update();
	}
	
}

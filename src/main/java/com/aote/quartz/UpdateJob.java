package com.aote.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class UpdateJob extends QuartzJobBean{
	private UpdateTask updateTask;
	 
	public void setUpdateTask(UpdateTask updateTask) {
		this.updateTask = updateTask;
	}

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		if(updateTask.finished)
			updateTask.update();
	}
}
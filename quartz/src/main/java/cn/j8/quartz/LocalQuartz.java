package cn.j8.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 本地化的任务调度，直接使用quartz进行调度
 */
public class LocalQuartz {
	private Logger logger = LoggerFactory.getLogger(LocalQuartz.class);
	private Scheduler scheduler;

    public boolean initialize(Map<String, String> jobs){
        SchedulerFactory sf = new StdSchedulerFactory();
        try {
            scheduler = sf.getScheduler();
            for (Map.Entry<String, String> e : jobs.entrySet()) {
                Class cls = Class.forName(e.getKey());
                addJob(cls, e.getValue());
            }
            return true;
        }catch(Throwable e){
            return false;
        }
    }

	private void addJob(Class cls, String cron){
        logger.info("开始启动定时任务：" + cls.getName());
        JobDetail job = JobBuilder.newJob(cls).withIdentity(cls.getName(), "j8task").build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(cls +  "_trigger", "j8task")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
        try {
            scheduler.scheduleJob(job, trigger);
        }catch(SchedulerException e){
            logger.error("error", e);
        }
    }
	
	public boolean start() {
		if(scheduler == null)
			return false;
		try{
			scheduler.start();
			return true;
		}catch(Exception e){
			logger.error("error", e);
			return false;
		}
	}

	public boolean stop() {
		if(scheduler != null){
			try{scheduler.shutdown();}catch(Exception e){logger.error("error", e);}
		}
		return true;
	}
	
}

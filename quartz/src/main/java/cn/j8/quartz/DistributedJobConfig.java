package cn.j8.quartz;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.lite.api.strategy.JobShardingStrategy;

/**
 * 任务配置
 */
public class DistributedJobConfig {
    //-------------------------------
    //任务配置
    private Class<SimpleJob> jobClass;
    private String cron;

    //-------------------------------
    //分片配置
    private int shardingCount = 1;
    private String parameter;

    public Class<SimpleJob> getJobClass() {
        return jobClass;
    }

    public void setJobClass(Class<SimpleJob> jobClass) {
        this.jobClass = jobClass;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getShardingCount() {
        return shardingCount;
    }

    public void setShardingCount(int shardingCount) {
        this.shardingCount = shardingCount;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}

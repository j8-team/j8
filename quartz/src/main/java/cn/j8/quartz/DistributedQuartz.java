package cn.j8.quartz;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;

/**
 * 分布式任务调度，基于elasticjoblite进行
 */
public class DistributedQuartz {
    public static void schedule(ZkRegistry registry, DistributedJobConfig jobConfig) {
        // 定义作业核心配置
        JobCoreConfiguration coreConfig = JobCoreConfiguration
                .newBuilder(jobConfig.getJobClass().getCanonicalName(), jobConfig.getCron(), jobConfig.getShardingCount())
                .jobParameter(jobConfig.getParameter())
                .build();
        // 定义SIMPLE类型配置
        SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(coreConfig, jobConfig.getJobClass().getCanonicalName());
        // 定义Lite作业根配置
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfig)
                .jobShardingStrategyClass(SingleJobForSingleInstanceShardingStrategy.class.getCanonicalName())
                .overwrite(true)
                .build();

        new JobScheduler(registry.getRegCenter(), liteJobConfiguration).init();
    }
}

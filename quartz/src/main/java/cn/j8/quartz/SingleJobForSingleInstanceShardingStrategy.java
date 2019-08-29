package cn.j8.quartz;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dangdang.ddframe.job.lite.api.strategy.JobInstance;
import com.dangdang.ddframe.job.lite.api.strategy.JobShardingStrategy;

public class SingleJobForSingleInstanceShardingStrategy implements JobShardingStrategy{
	
	@Override
	public Map<JobInstance, List<Integer>> sharding(List<JobInstance> jobInstances, String jobName, int shardingTotalCount){
		Map<JobInstance, List<Integer>> map = new LinkedHashMap<>();
		for(int shardingItem=0; shardingItem<jobInstances.size(); ++shardingItem){
			if(shardingItem < shardingTotalCount){
				List<Integer> items = new ArrayList<>();
				items.add(shardingItem);
				map.put(jobInstances.get(shardingItem), items);
			}
			else{
				System.err.println("分片item=>" + shardingItem + "超过了任务实例数，不分配！");
			}
		}
		return map;
	}
}

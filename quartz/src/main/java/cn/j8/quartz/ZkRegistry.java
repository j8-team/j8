package cn.j8.quartz;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

public class ZkRegistry {
    private CoordinatorRegistryCenter regCenter;
    public ZkRegistry(String zkServer, String namespace){
        regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration(zkServer, namespace));
        regCenter.init();
    }

    CoordinatorRegistryCenter getRegCenter() {
        return regCenter;
    }

    public void close(){
        regCenter.close();
    }
}

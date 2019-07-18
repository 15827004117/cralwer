package cn.itcast.crawler.task;

import cn.itcast.crawler.pojo.JobInfo;
import cn.itcast.crawler.service.JobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Component
public class DataPipeline implements Pipeline {

    @Autowired
    private JobInfoService infoService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        // 拿到封装好的数据
        JobInfo jobInfo = resultItems.get("jobInfo");
        // 执行保存操作
        if(jobInfo != null){
            infoService.save(jobInfo);
        }
    }
}

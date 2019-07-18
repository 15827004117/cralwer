package cn.itcast.crawler.service.Impl;

import cn.itcast.crawler.dao.JobInfoDao;
import cn.itcast.crawler.pojo.JobInfo;
import cn.itcast.crawler.service.JobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JobInfoServiceImpl implements JobInfoService {

    @Autowired
    private JobInfoDao jobInfoDao;

    @Override
    public void save(JobInfo info) {
        // 根据url和时间查询数据
        JobInfo param = new JobInfo();
        param.setUrl(info.getUrl());
        param.setTime(info.getTime());
        List<JobInfo> list = this.findAll(param);
        // 判断数据库中是否有重复数据
        if(list.size() == 0){
            // 为空表示该信息不存在，需执行更新或新增操作
            jobInfoDao.saveAndFlush(info);
        }
    }

    @Override
    public List<JobInfo> findAll(JobInfo info) {
        // 设置查询条件
        Example example = Example.of(info);
        List list = jobInfoDao.findAll(example);
        return list;
    }
}

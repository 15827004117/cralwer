package cn.itcast.crawler.service;

import cn.itcast.crawler.pojo.JobInfo;

import java.util.List;

public interface JobInfoService {

    /**
     * 保存工作信息
     */
    void save(JobInfo info);

    /**
     * 查看工作信息列表
     */
    List<JobInfo> findAll(JobInfo info);
}

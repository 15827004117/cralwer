package cn.itcast.crawler.dao;

import cn.itcast.crawler.pojo.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobInfoDao extends JpaRepository<JobInfo, Long> {
}

package cn.itcast.crawler.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 招聘信息实体类
 */
@Entity
@Data
public class JobInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键id
    private String companyName; // 公司名称
    private String companyAddr; // 公司联系方式
    private String companyInfo; // 公司信息
    private String jobName;     // 职位名称
    private String jobAddr;     // 工作地点
    private String jobInfo;     // 职位信息
    private Integer salaryMin;  // 薪资范围，最小
    private Integer salaryMax;  // 薪资范围，最大
    private String url;         // 招聘信息详情页
    private String time;        // 职位最近发布时间



}

package cn.itcast.crawler.task;

import cn.itcast.crawler.pojo.JobInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Component
public class JobProcessor implements PageProcessor {

    @Autowired
    private DataPipeline pipeline;

    // 初始url
    private static final String URL = "https://search.51job.com/list/180200,000000,0000,00,9,99,java,2,1.html?" +
            "lang=c&stype=&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99" +
            "&providesalary=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0" +
            "&address=&line=&specialarea=00&from=&welfare=";


    @Override
    public void process(Page page) {
        // 解析页面，获取招聘信息详情的url地址
        List<Selectable> nodes = page.getHtml().css("div#resultList div.el").nodes();
        // 判断获取的集合是否为空
        if(nodes.size() == 0){
            // 为空，表示是招聘的详情页，解析详情页获取需要的数据保存
            this.saveJobInfo(page);
        }else {
            // 不为空，表示列表页,解析出详情页的url，放入任务队列中
            for (Selectable selectable : nodes) {
                String jobInfoUrl = selectable.links().toString();
                // 把获取到的url放到队列中
                page.addTargetRequest(jobInfoUrl);
            }
            // 获取下一页的url
            String bkUrl = page.getHtml().css("div.p_in li.bk").nodes().get(1).links().toString();
            // 放入队列
            page.addTargetRequest(bkUrl);
        }

        String html = page.getHtml().toString();
    }

    // 解析页面，获取需要的数据保存
    private void saveJobInfo(Page page) {
        //创建对象，解析数据，封装
        JobInfo info = new JobInfo();
        Html html = page.getHtml();
        info.setCompanyName(html.css("div.cn p.cname a", "text").toString());
        info.setCompanyAddr(Jsoup.parse(html.css("div.bmsg").nodes().get(1).toString()).text());
        info.setCompanyInfo(Jsoup.parse(html.css("div.tmsg").toString()).text());
        info.setJobName(html.css("div.cn h1","text").toString());
        info.setJobAddr(html.css("div.cn span.lname","text").toString());
        info.setJobInfo(Jsoup.parse(html.css("div.job_msg").toString()).text());
        info.setUrl(page.getUrl().toString());
        info.setSalaryMin(8000);
        info.setSalaryMax(10000);
        // 封装取出的时间信息
        String text = Jsoup.parse(html.css("div.cn p.msg").toString()).text();  //取出数据
        text = text.replace("|",",");  //特殊字符转换
        String[] split = text.trim().split(",");    //根据转后的,号切割字符串得到数组
        for (int i = 0; i < split.length; i++) {           //循环数组,如果以"发布"结尾就可以得到发布日期
            if(split[i].trim().endsWith("发布")){
                text = split[i].trim();
            }
        }
        info.setTime(text.substring(0, text.length() - 2)); //将"发布"两字去掉,值保留日期封装进实体类中

        page.putField("jobInfo", info);
    }

    private Site size= Site.me()
            .setCharset("gbk")     // 设置编码
            .setTimeOut(10*1000)    // 设置超时时间
            .setRetrySleepTime(3000)    // 设置重试时间
            .setRetryTimes(3);       //设置重试次数

    @Override
    public Site getSite() {
        return size;
    }

    /**
     * 定时任务
     * initialDelay： 启动后暂停1000毫秒开始任务
     * fixedDelay: 每间隔100*1000毫秒执行一次操作
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 100*1000)
    public void process() {
        Spider.create(new JobProcessor())   // 创建Spider
                .addUrl(URL)                // 设置url
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000))) //布隆去重
                .thread(10)                 // 开启10个线程
                .addPipeline(pipeline)      // 输出
                .run(); // 启动
    }
}

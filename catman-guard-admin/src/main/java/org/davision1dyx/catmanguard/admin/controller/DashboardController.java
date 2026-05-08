package org.davision1dyx.catmanguard.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.admin.vo.DashboardStateVO;
import org.davision1dyx.catmanguard.api.admin.vo.DashboardTrendVO;
import org.davision1dyx.catmanguard.api.admin.vo.HotQuestionVO;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 数据看板接口
 */
@Slf4j
@RestController
@RequestMapping("/processing/catman/admin/dashboard")
public class DashboardController {

    /**
     * 获取统计数据
     */
    @GetMapping("/state")
    public DashboardStateVO state(@RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate) {
        log.info("[GET] /processing/catman/admin/dashboard/state, startDate: {}, endDate: {}", startDate, endDate);
        
        List<DashboardStateVO.TicketStatusDistribution> ticketStatusDistribution = Arrays.asList(
                createDistribution("ASSIGNED", 45),
                createDistribution("IN_PROGRESS", 67),
                createDistribution("COMPLETED", 102),
                createDistribution("VECTOR_STORED", 20)
        );
        
        DashboardStateVO result = new DashboardStateVO();
        result.setTotalConversations(1250);
        result.setTotalTickets(234);
        result.setTotalDocuments(156);
        result.setOnlineStaff(8);
        result.setQaResolveRate(85);
        result.setTicketStatusDistribution(ticketStatusDistribution);
        
        return result;
    }

    private DashboardStateVO.TicketStatusDistribution createDistribution(String status, Integer count) {
        DashboardStateVO.TicketStatusDistribution distribution = new DashboardStateVO.TicketStatusDistribution();
        distribution.setStatus(status);
        distribution.setCount(count);
        return distribution;
    }

    /**
     * 获取趋势数据
     */
    @GetMapping("/trend")
    public DashboardTrendVO trend(@RequestParam String period) {
        log.info("[GET] /processing/catman/admin/dashboard/trend, period: {}", period);
        
        List<String> labels;
        List<Integer> conversations;
        List<Integer> tickets;
        
        switch (period.toLowerCase()) {
            case "week":
                labels = Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日");
                conversations = Arrays.asList(156, 189, 203, 178, 195, 120, 98);
                tickets = Arrays.asList(12, 18, 23, 15, 19, 8, 5);
                break;
            case "month":
                labels = Arrays.asList("第1周", "第2周", "第3周", "第4周");
                conversations = Arrays.asList(850, 920, 880, 950);
                tickets = Arrays.asList(65, 72, 68, 75);
                break;
            case "year":
                labels = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月");
                conversations = Arrays.asList(3200, 2800, 3100, 3500, 3800, 4200, 4100, 3900, 4300, 4500, 4800, 5200);
                tickets = Arrays.asList(280, 240, 290, 320, 350, 380, 360, 340, 370, 400, 420, 450);
                break;
            default:
                labels = Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日");
                conversations = Arrays.asList(156, 189, 203, 178, 195, 120, 98);
                tickets = Arrays.asList(12, 18, 23, 15, 19, 8, 5);
        }
        
        DashboardTrendVO result = new DashboardTrendVO();
        result.setLabels(labels);
        result.setConversations(conversations);
        result.setTickets(tickets);
        
        return result;
    }

    /**
     * 获取热门问题
     */
    @GetMapping("/hot-questions")
    public List<HotQuestionVO> hotQuestions(@RequestParam(required = false, defaultValue = "10") Integer limit) {
        log.info("[GET] /processing/catman/admin/dashboard/hot-questions, limit: {}", limit);
        
        List<HotQuestionVO> hotQuestions = Arrays.asList(
                createHotQuestion("如何配置API网关?", 156, 142, 91),
                createHotQuestion("数据库连接池配置", 132, 125, 95),
                createHotQuestion("微服务部署指南", 98, 90, 92),
                createHotQuestion("日志收集配置", 87, 78, 90),
                createHotQuestion("缓存策略设置", 76, 72, 95),
                createHotQuestion("服务熔断配置", 65, 60, 92),
                createHotQuestion("消息队列使用", 54, 48, 89),
                createHotQuestion("监控告警配置", 43, 40, 93),
                createHotQuestion("分布式事务处理", 32, 28, 88),
                createHotQuestion("API限流设置", 21, 19, 90)
        );
        
        if (limit < hotQuestions.size()) {
            return hotQuestions.subList(0, limit);
        }
        return hotQuestions;
    }

    private HotQuestionVO createHotQuestion(String question, Integer count, Integer resolved, Integer resolveRate) {
        HotQuestionVO vo = new HotQuestionVO();
        vo.setQuestion(question);
        vo.setCount(count);
        vo.setResolved(resolved);
        vo.setResolveRate(resolveRate);
        return vo;
    }
}

package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 数据看板统计VO
 */
@Data
public class DashboardStateVO {

    /**
     * 总对话数
     */
    private Integer totalConversations;

    /**
     * 总工单数
     */
    private Integer totalTickets;

    /**
     * 总文档数
     */
    private Integer totalDocuments;

    /**
     * 在线值班人员数
     */
    private Integer onlineStaff;

    /**
     * QA解决率
     */
    private Integer qaResolveRate;

    /**
     * 工单状态分布
     */
    private List<TicketStatusDistribution> ticketStatusDistribution;

    @Data
    public static class TicketStatusDistribution {
        private String status;
        private Integer count;
    }
}
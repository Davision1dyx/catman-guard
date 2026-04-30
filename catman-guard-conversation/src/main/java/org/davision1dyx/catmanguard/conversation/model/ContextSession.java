package org.davision1dyx.catmanguard.conversation.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.davision1dyx.catmanguard.datasource.entity.BaseModel;

/**
 * @author Davison
 * @date 2026-04-30
 * @description 会话
 */
@TableName("context_session")
public class ContextSession extends BaseModel {

    /**
     * 会话ID
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 智能体类型（react/file/ppt）
     */
    @TableField("agent_type")
    private String agentType;

    /**
     * 用户问题
     */
    @TableField("question")
    private String question;

    /**
     * AI回复
     */
    @TableField("answer")
    private String answer;

    /**
     * 涉及的执行工具名称（逗号分隔）
     */
    @TableField("tools")
    private String tools;

    /**
     * 参考链接JSON
     */
    @TableField("reference")
    private String reference;

    /**
     * 首次响应时间（毫秒）
     */
    @TableField("first_response_time")
    private Long firstResponseTime;

    /**
     * 整体回复时间（毫秒）
     */
    @TableField("total_response_time")
    private Long totalResponseTime;

    /**
     * 思考过程
     */
    @TableField("thinking")
    private String thinking;

    /**
     * 关联文件ID（用于关联ai_file_info或ai_ppt_inst）
     */
    @TableField("fileid")
    private String fileid;

    /**
     * 推荐问题JSON
     */
    @TableField("recommend")
    private String recommend;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTools() {
        return tools;
    }

    public void setTools(String tools) {
        this.tools = tools;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Long getFirstResponseTime() {
        return firstResponseTime;
    }

    public void setFirstResponseTime(Long firstResponseTime) {
        this.firstResponseTime = firstResponseTime;
    }

    public Long getTotalResponseTime() {
        return totalResponseTime;
    }

    public void setTotalResponseTime(Long totalResponseTime) {
        this.totalResponseTime = totalResponseTime;
    }

    public String getThinking() {
        return thinking;
    }

    public void setThinking(String thinking) {
        this.thinking = thinking;
    }

    public String getFileid() {
        return fileid;
    }

    public void setFileid(String fileid) {
        this.fileid = fileid;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }
}

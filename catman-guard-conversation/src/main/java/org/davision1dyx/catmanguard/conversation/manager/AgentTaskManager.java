package org.davision1dyx.catmanguard.conversation.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;
import org.davision1dyx.catmanguard.conversation.pojo.AgentResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@Slf4j
@Component
public class AgentTaskManager {

    /**
     * 会话ID -> 任务信息的映射
     */
    private final Map<String, TaskInfo> taskMap = new ConcurrentHashMap<>();


    public TaskInfo registerTask(String conversationId, Sinks.Many<String> sink, String agentType) {
        TaskInfo existing = taskMap.get(conversationId);
        if (existing != null) {
            log.warn("会话 {} 已有任务在执行，拒绝注册新任务", conversationId);
            return null;
        }
        TaskInfo taskInfo = new TaskInfo(sink, agentType);
        taskMap.put(conversationId, taskInfo);
        log.info("注册任务: conversationId={}, agentType={}", conversationId, agentType);
        return taskInfo;
    }

    /**
     * 检查会话是否有正在执行的任务
     *
     * @param conversationId 会话ID
     * @return 是否有正在执行的任务
     */
    public boolean hasRunningTask(String conversationId) {
        return taskMap.containsKey(conversationId);
    }

    /**
     * 设置任务的Disposable
     *
     * @param conversationId 会话ID
     * @param disposable     Disposable对象
     */
    public void setDisposable(String conversationId, Disposable disposable) {
        TaskInfo taskInfo = taskMap.get(conversationId);
        if (taskInfo != null) {
            taskInfo.setDisposable(disposable);
        }
    }


    private void removeTask(String conversationId) {
        TaskInfo removed = taskMap.remove(conversationId);
        if (removed != null) {
            log.info("移除任务: conversationId={}", conversationId);
        }
    }

    public boolean stopTask(String conversationId) {
        try {
            TaskInfo taskInfo = taskMap.get(conversationId);
            if (taskInfo != null) {
                Disposable disposable = taskInfo.getDisposable();
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                }

                Sinks.Many<String> sink = taskInfo.getSink();
                if (sink != null) {
                    sink.tryEmitNext(AgentResponse.text("⏹ 用户已停止生成\n"));
                    sink.tryEmitComplete();
                }
            }
            return true;
        } catch (Exception e) {
            log.error("停止任务异常, conversationId: {}", conversationId, e);
            return false;
        } finally {
            removeTask(conversationId);
        }
    }

    /**
     * 任务信息
     */
    public static class TaskInfo {
        private final Sinks.Many<String> sink;
        private Disposable disposable;
        private final long createTime;
        private String agentType;

        public TaskInfo(Sinks.Many<String> sink, String agentType) {
            this.sink = sink;
            this.agentType = agentType;
            this.createTime = System.currentTimeMillis();
        }

        public Sinks.Many<String> getSink() {
            return sink;
        }

        public Disposable getDisposable() {
            return disposable;
        }

        public void setDisposable(Disposable disposable) {
            this.disposable = disposable;
        }

        public long getCreateTime() {
            return createTime;
        }

        public String getAgentType() {
            return agentType;
        }
    }
}
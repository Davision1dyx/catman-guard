CREATE TABLE context_session (
     id BIGSERIAL PRIMARY KEY,
     session_id VARCHAR(255) NOT NULL,
     agent_type VARCHAR(255),
     question TEXT,
     answer TEXT,
     thinking TEXT,
     tools VARCHAR(1024),
     reference TEXT,
     recommend VARCHAR(1000),
     fileid VARCHAR(255),
     first_response_time BIGINT,
     total_response_time BIGINT,
     create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     lock_version INT DEFAULT 0,
     deleted INT DEFAULT 0
);

-- 索引
CREATE INDEX idx_session_id ON context_session(session_id);
CREATE INDEX idx_create_time ON context_session(create_time);

-- 字段注释
COMMENT ON TABLE context_session IS '存储智能体与用户的对话历史,支持会话隔离和记忆功能';
COMMENT ON COLUMN context_session.id IS '主键ID';
COMMENT ON COLUMN context_session.session_id IS '会话ID';
COMMENT ON COLUMN context_session.agent_type IS '智能体类型';
COMMENT ON COLUMN context_session.question IS '用户问题';
COMMENT ON COLUMN context_session.answer IS 'AI回复';
COMMENT ON COLUMN context_session.thinking IS '思考过程';
COMMENT ON COLUMN context_session.tools IS '涉及的执行工具名称(逗号分隔)';
COMMENT ON COLUMN context_session.reference IS '参考链接JSON';
COMMENT ON COLUMN context_session.recommend IS '推荐问题JSON';
COMMENT ON COLUMN context_session.fileid IS '关联文件ID';
COMMENT ON COLUMN context_session.first_response_time IS '首次响应时间(毫秒)';
COMMENT ON COLUMN context_session.total_response_time IS '整体回复时间(毫秒)';
COMMENT ON COLUMN context_session.create_time IS '创建时间';
COMMENT ON COLUMN context_session.update_time IS '更新时间';
COMMENT ON COLUMN context_session.lock_version IS '锁版本';
COMMENT ON COLUMN context_session.deleted IS '是否删除';

CREATE TABLE file_info (
   id BIGSERIAL PRIMARY KEY,
   file_id VARCHAR(255) NOT NULL,
   file_name VARCHAR(255) NOT NULL,
   file_title VARCHAR(500) NOT NULL,
   file_type VARCHAR(50),
   url VARCHAR(500),
   converted_url VARCHAR(500),
   storage_type VARCHAR(20) DEFAULT 'local',
   status VARCHAR(50) DEFAULT 'INIT',
   extension TEXT,
   description TEXT,
   upload_user VARCHAR(255),
   expire_time TIMESTAMP,
   knowledge_id VARCHAR(255),
   create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   lock_version INT DEFAULT 0,
   deleted INT DEFAULT 0
);

-- 索引
CREATE UNIQUE INDEX uk_file_id ON file_info(file_id);
CREATE INDEX idx_status ON file_info(status);
CREATE INDEX idx_knowledge_id ON file_info(knowledge_id);

-- 字段注释
COMMENT ON TABLE file_info IS '文件元数据表,存储文件基本信息和解析状态';
COMMENT ON COLUMN file_info.id IS '主键ID';
COMMENT ON COLUMN file_info.file_id IS '文件唯一标识(UUID)';
COMMENT ON COLUMN file_info.file_name IS '文件名';
COMMENT ON COLUMN file_info.file_title IS '文件标题';
COMMENT ON COLUMN file_info.file_type IS '文件类型(pdf/doc/docx/txt/md等)';
COMMENT ON COLUMN file_info.url IS '文件URL';
COMMENT ON COLUMN file_info.converted_url IS '转换后的文件URL';
COMMENT ON COLUMN file_info.storage_type IS '存储类型(local/minio)';
COMMENT ON COLUMN file_info.status IS '状态：INIT, UPLOADED, CONVERTING, CONVERTED, CHUNKED, VECTOR_STORED';
COMMENT ON COLUMN file_info.extension IS '文件扩展信息';
COMMENT ON COLUMN file_info.description IS '文件描述';
COMMENT ON COLUMN file_info.upload_user IS '上传用户';
COMMENT ON COLUMN file_info.expire_time IS '过期时间';
COMMENT ON COLUMN file_info.knowledge_id IS '所属知识库ID';
COMMENT ON COLUMN file_info.create_time IS '创建时间';
COMMENT ON COLUMN file_info.update_time IS '更新时间';
COMMENT ON COLUMN file_info.lock_version IS '锁版本';
COMMENT ON COLUMN file_info.deleted IS '是否删除';

CREATE TABLE file_chunk (
    id BIGSERIAL PRIMARY KEY,
    chunk_id VARCHAR(255) NOT NULL,
    file_id VARCHAR(255) NOT NULL,
    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    meta_data VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lock_version INT DEFAULT 0,
    deleted INT DEFAULT 0
);

-- 索引
CREATE UNIQUE INDEX uk_chunk_id ON file_chunk(chunk_id);
CREATE INDEX idx_file_id ON file_chunk(file_id);
CREATE UNIQUE INDEX uk_file_index ON file_chunk(file_id, chunk_index);

-- 字段注释
COMMENT ON TABLE file_chunk IS '文件分片表,存储文件切分后的片段';
COMMENT ON COLUMN file_chunk.id IS '主键ID';
COMMENT ON COLUMN file_chunk.chunk_id IS '分片唯一标识(UUID)';
COMMENT ON COLUMN file_chunk.file_id IS '文件ID';
COMMENT ON COLUMN file_chunk.chunk_index IS '分片序号';
COMMENT ON COLUMN file_chunk.content IS '分片内容';
COMMENT ON COLUMN file_chunk.meta_data IS '元数据';
COMMENT ON COLUMN file_chunk.create_time IS '创建时间';
COMMENT ON COLUMN file_chunk.update_time IS '更新时间';
COMMENT ON COLUMN file_chunk.lock_version IS '锁版本';
COMMENT ON COLUMN file_chunk.deleted IS '是否删除';

-- 工单表
CREATE TABLE issue (
    id BIGSERIAL PRIMARY KEY,
    issue_id VARCHAR(255) NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    type VARCHAR(50) DEFAULT 'INCIDENT',
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    status VARCHAR(50) DEFAULT 'ASSIGNED',
    submitter_id VARCHAR(255),
    submitter_name VARCHAR(255),
    submitter_email VARCHAR(255),
    assignee_id VARCHAR(255),
    assignee_name VARCHAR(255),
    assignee_email VARCHAR(255),
    knowledge_id VARCHAR(255),
    summary TEXT,
    root_cause TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lock_version INT DEFAULT 0,
    deleted INT DEFAULT 0
);

-- 索引
CREATE UNIQUE INDEX uk_issue_id ON issue(issue_id);
CREATE INDEX idx_issue_status ON issue(status);
CREATE INDEX idx_issue_priority ON issue(priority);
CREATE INDEX idx_issue_knowledge ON issue(knowledge_id);

-- 字段注释
COMMENT ON TABLE issue IS '工单表,存储工单信息';
COMMENT ON COLUMN issue.id IS '主键ID';
COMMENT ON COLUMN issue.issue_id IS '工单编号';
COMMENT ON COLUMN issue.title IS '工单标题';
COMMENT ON COLUMN issue.description IS '工单描述';
COMMENT ON COLUMN issue.type IS '工单类型：INCIDENT/REQUEST/PROBLEM/CHANGE';
COMMENT ON COLUMN issue.priority IS '优先级：HIGH/MEDIUM/LOW/CRITICAL';
COMMENT ON COLUMN issue.status IS '状态：ASSIGNED/IN_PROGRESS/COMPLETED/VECTOR_STORED';
COMMENT ON COLUMN issue.submitter_id IS '提交人ID';
COMMENT ON COLUMN issue.submitter_name IS '提交人姓名';
COMMENT ON COLUMN issue.submitter_email IS '提交人邮箱';
COMMENT ON COLUMN issue.assignee_id IS '负责人ID';
COMMENT ON COLUMN issue.assignee_name IS '负责人姓名';
COMMENT ON COLUMN issue.assignee_email IS '负责人邮箱';
COMMENT ON COLUMN issue.knowledge_id IS '关联知识库ID';
COMMENT ON COLUMN issue.summary IS '工单摘要（AI生成）';
COMMENT ON COLUMN issue.root_cause IS '问题归因（AI生成）';
COMMENT ON COLUMN issue.create_time IS '创建时间';
COMMENT ON COLUMN issue.update_time IS '更新时间';
COMMENT ON COLUMN issue.lock_version IS '锁版本';
COMMENT ON COLUMN issue.deleted IS '是否删除';

-- =============================================
-- 初始化工单知识库 (start)
-- =============================================

-- 创建默认工单知识库（运维文档类型）
INSERT INTO knowledge (knowledge_id, name, description, type, file_count, chunk_count, status)
VALUES ('kb-operation-tickets', '工单知识库', '存储已解决的运维工单，用于智能问答检索', 'operation', 0, 0, 'ACTIVE')
ON CONFLICT (knowledge_id) DO NOTHING;

-- =============================================
-- 初始化工单知识库 (end)
-- =============================================

-- 值班人员表
CREATE TABLE staff (
    id BIGSERIAL PRIMARY KEY,
    staff_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    group_id VARCHAR(255),
    group_name VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lock_version INT DEFAULT 0,
    deleted INT DEFAULT 0
);

-- 索引
CREATE UNIQUE INDEX uk_staff_id ON staff(staff_id);
CREATE INDEX idx_staff_group ON staff(group_id);
CREATE INDEX idx_staff_status ON staff(status);

-- 字段注释
COMMENT ON TABLE staff IS '值班人员表,存储值班人员信息';
COMMENT ON COLUMN staff.id IS '主键ID';
COMMENT ON COLUMN staff.staff_id IS '人员ID';
COMMENT ON COLUMN staff.name IS '姓名';
COMMENT ON COLUMN staff.email IS '邮箱';
COMMENT ON COLUMN staff.phone IS '电话';
COMMENT ON COLUMN staff.group_id IS '分组ID';
COMMENT ON COLUMN staff.group_name IS '分组名称';
COMMENT ON COLUMN staff.status IS '状态：ACTIVE/ON_LEAVE';
COMMENT ON COLUMN staff.create_time IS '创建时间';
COMMENT ON COLUMN staff.update_time IS '更新时间';
COMMENT ON COLUMN staff.lock_version IS '锁版本';
COMMENT ON COLUMN staff.deleted IS '是否删除';

-- 分组表
CREATE TABLE staff_group (
    id BIGSERIAL PRIMARY KEY,
    group_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lock_version INT DEFAULT 0,
    deleted INT DEFAULT 0
);

-- 索引
CREATE UNIQUE INDEX uk_group_id ON staff_group(group_id);

-- 字段注释
COMMENT ON TABLE staff_group IS '分组表,存储分组信息';
COMMENT ON COLUMN staff_group.id IS '主键ID';
COMMENT ON COLUMN staff_group.group_id IS '分组ID';
COMMENT ON COLUMN staff_group.name IS '分组名称';
COMMENT ON COLUMN staff_group.create_time IS '创建时间';
COMMENT ON COLUMN staff_group.update_time IS '更新时间';
COMMENT ON COLUMN staff_group.lock_version IS '锁版本';
COMMENT ON COLUMN staff_group.deleted IS '是否删除';

-- 排班表
CREATE TABLE schedule (
    id BIGSERIAL PRIMARY KEY,
    schedule_id VARCHAR(255) NOT NULL,
    group_id VARCHAR(255),
    group_name VARCHAR(255),
    start_date VARCHAR(20),
    end_date VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lock_version INT DEFAULT 0,
    deleted INT DEFAULT 0
);

-- 索引
CREATE UNIQUE INDEX uk_schedule_id ON schedule(schedule_id);
CREATE INDEX idx_schedule_group ON schedule(group_id);

-- 字段注释
COMMENT ON TABLE schedule IS '排班表,存储排班信息';
COMMENT ON COLUMN schedule.id IS '主键ID';
COMMENT ON COLUMN schedule.schedule_id IS '排班ID';
COMMENT ON COLUMN schedule.group_id IS '分组ID';
COMMENT ON COLUMN schedule.group_name IS '分组名称';
COMMENT ON COLUMN schedule.start_date IS '开始日期';
COMMENT ON COLUMN schedule.end_date IS '结束日期';
COMMENT ON COLUMN schedule.is_active IS '是否启用';
COMMENT ON COLUMN schedule.create_time IS '创建时间';
COMMENT ON COLUMN schedule.update_time IS '更新时间';
COMMENT ON COLUMN schedule.lock_version IS '锁版本';
COMMENT ON COLUMN schedule.deleted IS '是否删除';

-- 知识库表
CREATE TABLE knowledge (
    id BIGSERIAL PRIMARY KEY,
    knowledge_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) DEFAULT 'technical',
    file_count INTEGER DEFAULT 0,
    chunk_count INTEGER DEFAULT 0,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lock_version INT DEFAULT 0,
    deleted INT DEFAULT 0
);

-- 索引
CREATE UNIQUE INDEX uk_knowledge_id ON knowledge(knowledge_id);
CREATE INDEX idx_knowledge_type ON knowledge(type);
CREATE INDEX idx_knowledge_status ON knowledge(status);

-- 字段注释
COMMENT ON TABLE knowledge IS '知识库表,存储知识库信息';
COMMENT ON COLUMN knowledge.id IS '主键ID';
COMMENT ON COLUMN knowledge.knowledge_id IS '知识库唯一标识(UUID)';
COMMENT ON COLUMN knowledge.name IS '知识库名称';
COMMENT ON COLUMN knowledge.description IS '知识库描述';
COMMENT ON COLUMN knowledge.type IS '知识库类型：product-产品文档, technical-技术文档, operation-运维文档, training-培训资料';
COMMENT ON COLUMN knowledge.file_count IS '文件数量';
COMMENT ON COLUMN knowledge.chunk_count IS '分片数量';
COMMENT ON COLUMN knowledge.status IS '状态：ACTIVE-活跃, MAINTENANCE-维护中';
COMMENT ON COLUMN knowledge.create_time IS '创建时间';
COMMENT ON COLUMN knowledge.update_time IS '更新时间';
COMMENT ON COLUMN knowledge.lock_version IS '锁版本';
COMMENT ON COLUMN knowledge.deleted IS '是否删除';
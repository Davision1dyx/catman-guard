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
COMMENT ON COLUMN file_info.lock_version IS '锁版本';
COMMENT ON COLUMN file_info.deleted IS '是否删除';

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
   create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   lock_version INT DEFAULT 0,
   deleted INT DEFAULT 0
);

-- 索引
CREATE UNIQUE INDEX uk_file_id ON file_info(file_id);
CREATE INDEX idx_status ON file_info(status);
CREATE INDEX idx_create_time ON file_info(create_time);

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
COMMENT ON COLUMN file_info.create_time IS '创建时间';
COMMENT ON COLUMN file_info.update_time IS '更新时间';
COMMENT ON COLUMN file_info.lock_version IS '锁版本';
COMMENT ON COLUMN file_info.deleted IS '是否删除';

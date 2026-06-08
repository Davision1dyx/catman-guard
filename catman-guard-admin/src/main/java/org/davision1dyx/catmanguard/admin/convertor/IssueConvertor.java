package org.davision1dyx.catmanguard.admin.convertor;

import org.davision1dyx.catmanguard.admin.model.Issue;
import org.davision1dyx.catmanguard.api.admin.vo.IssueDetailVO;
import org.davision1dyx.catmanguard.api.admin.vo.IssueVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface IssueConvertor {

    IssueConvertor INSTANCE = Mappers.getMapper(IssueConvertor.class);

    @Mapping(source = "issueId", target = "id")
    @Mapping(source = "submitterId", target = "submitterId")
    @Mapping(source = "submitterName", target = "submitterName")
    @Mapping(source = "submitterEmail", target = "submitterEmail")
    @Mapping(source = "assigneeId", target = "assigneeId")
    @Mapping(source = "assigneeName", target = "assigneeName")
    @Mapping(source = "assigneeEmail", target = "assigneeEmail")
    @Mapping(source = "knowledgeId", target = "knowledgeId")
    @Mapping(source = "createTime", target = "createdTime")
    @Mapping(source = "updateTime", target = "updatedTime")
    IssueVO mapToVO(Issue issue);

    @Mapping(source = "issueId", target = "id")
    @Mapping(source = "submitterId", target = "submitterId")
    @Mapping(source = "submitterName", target = "submitterName")
    @Mapping(source = "submitterEmail", target = "submitterEmail")
    @Mapping(source = "assigneeId", target = "assigneeId")
    @Mapping(source = "assigneeName", target = "assigneeName")
    @Mapping(source = "assigneeEmail", target = "assigneeEmail")
    @Mapping(source = "knowledgeId", target = "knowledgeId")
    @Mapping(source = "createTime", target = "createdTime")
    @Mapping(source = "updateTime", target = "updatedTime")
    IssueDetailVO mapToDetailVO(Issue issue);

    @AfterMapping
    default void afterMapping(Issue source, IssueDetailVO target) {
        target.setAttachments(Collections.emptyList());
        target.setReplies(Collections.emptyList());
    }
}
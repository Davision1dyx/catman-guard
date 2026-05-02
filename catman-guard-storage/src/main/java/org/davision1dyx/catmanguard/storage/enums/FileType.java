package org.davision1dyx.catmanguard.storage.enums;

import java.util.Set;

/**
 * @author Davison
 * @date 2026-05-02
 * @description 文件类型
 */
public enum FileType {

    PDF("pdf"),
    TXT("txt", "text"),
    DOC("doc", "docx"),
    MARKDOWN("md"),
    EXCEL("xlsx", "xls", "csv"),
    ;

    public final Set<String> suffix;

    FileType(String... suffix) {
        this.suffix = Set.of(suffix);
    }

    public static FileType of(String suffix) {
        for (FileType multipartFile : values()) {
            if (multipartFile.suffix.contains(suffix.toLowerCase())) {
                return multipartFile;
            }
        }
        return null;
    }
}

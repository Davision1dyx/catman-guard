package org.davision1dyx.catmanguard.storage.handle.transform;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.*;
import java.util.stream.Collectors;

import static org.davision1dyx.catmanguard.base.constant.StorageConstant.*;

/**
 * @author Davison
 * @date 2026-05-05
 * @description
 */
public class MarkdownHeaderBrotherTextSplitHandler extends TextSplitter {

    private static final Map<String, String> DEFAULT_HEADERS_TO_SPLIT = new HashMap<>();

    /**
     * 需要分割的标题列表，按标题标记长度倒序排列
     */
    private final List<Map.Entry<String, String>> headersToSplitOn;

    /**
     * 是否按行返回结果
     */
    private final boolean returnEachLine;

    /**
     * 是否剥离标题行本身
     */
    private final boolean stripHeaders;

    /**
     * 是否启用父子分段模式
     */
    private final boolean parentChildModel;

    /**
     * 每个分片的最大字符数，0表示不限制
     */
    private final int chunkSize;

    /**
     * 相邻分片之间的重叠字符数
     */
    private final int overlap;

    static {
        DEFAULT_HEADERS_TO_SPLIT.put("#", "title");
        DEFAULT_HEADERS_TO_SPLIT.put("##", "twoTitle");
        DEFAULT_HEADERS_TO_SPLIT.put("###", "threeTitle");
        DEFAULT_HEADERS_TO_SPLIT.put("####", "fourTitle");
        DEFAULT_HEADERS_TO_SPLIT.put("#####", "fiveTitle");
        DEFAULT_HEADERS_TO_SPLIT.put("######", "sixTitle");
    }

    public MarkdownHeaderBrotherTextSplitHandler(int chunkSize, int overlap) {
        this(DEFAULT_HEADERS_TO_SPLIT, true, false, true, chunkSize, overlap);
    }

    public MarkdownHeaderBrotherTextSplitHandler(Map<String, String> headersToSplitOn, boolean returnEachLine, boolean stripHeaders, boolean parentChildModel) {
        this(headersToSplitOn, returnEachLine, stripHeaders, parentChildModel, 0, 0);
    }

    public MarkdownHeaderBrotherTextSplitHandler(Map<String, String> headersToSplitOn, boolean returnEachLine, boolean stripHeaders, boolean parentChildModel, int chunkSize, int overlap) {
        // 按标题标记长度倒序排列，确保优先匹配更长的标记（如"###"优先于"##"）
        this.headersToSplitOn = headersToSplitOn.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> -e.getKey().length()))
                .collect(Collectors.toList());
        this.returnEachLine = returnEachLine;
        this.stripHeaders = stripHeaders;
        this.parentChildModel = parentChildModel;
        this.chunkSize = chunkSize;
        this.overlap = overlap;
    }

    @Override
    protected List<String> splitText(String text) {
        // 移除文本中所有空行
        String filteredText = Arrays.stream(text.split("\n"))
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.joining("\n"));

        List<String> result = new ArrayList<>();
        List<Document> documents = splitWithMetadata(filteredText, new HashMap<>());
        return documents.stream().map(Document::getText).toList();
    }

    public List<Document> split(Document document) {
        // 移除文档中所有空行
        String text = Arrays.stream(document.getText().split("\n"))
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.joining("\n"));

        List<Document> result = splitWithMetadata(text, document.getMetadata());
        return result;
    }

    private List<Document> splitWithMetadata(String text, Map<String, Object> baseMetadata) {
        List<String> lines = Arrays.asList(text.split("\n"));
        List<Line> linesWithMetadata = new ArrayList<>();
        List<String> currentContent = new ArrayList<>();
        Map<String, Object> currentMetadata = new HashMap<>(baseMetadata);
        List<Header> headerStack = new ArrayList<>();  // 标题栈，用于追踪当前的标题层级结构
        Map<String, Object> initialMetadata = new HashMap<>(baseMetadata);
        // 初始化时就生成一个初始的 CHUNK_ID 和 HEADER_LEVEL，确保所有分片都有这些字段
        initialMetadata.put(CHUNK_ID, UUID.randomUUID().toString());
        initialMetadata.put(HEADER_LEVEL, 0);  // 0 表示无标题或根级别

        boolean inCodeBlock = false;  // 是否在代码块中
        String openingFence = "";     // 代码块的开始标记

        for (String line : lines) {
            String strippedLine = line.trim();

            // 处理代码块标记，代码块内的内容不作为标题处理
            if (!inCodeBlock) {
                if (strippedLine.startsWith("```")) {
                    inCodeBlock = !inCodeBlock;
                    openingFence = "```";
                } else if (strippedLine.startsWith("~~~")) {
                    inCodeBlock = !inCodeBlock;
                    openingFence = "~~~";
                }
            } else {
                if (strippedLine.startsWith(openingFence)) {
                    inCodeBlock = false;
                    openingFence = "";
                }
            }

            // 代码块内的内容直接添加，不做标题检测
            if (inCodeBlock) {
                currentContent.add(strippedLine);
                continue;
            }

            // 检测并处理标题行
            interrupted:
            {
                for (Map.Entry<String, String> header : headersToSplitOn) {
                    String sep = header.getKey();    // 标题标记，如"#"、"##"
                    String name = header.getValue(); // 元数据中的键名

                    // 判断是否为有效的标题行
                    if (strippedLine.startsWith(sep) && (strippedLine.length() == sep.length() || strippedLine.charAt(sep.length()) == ' ')) {
                        if (name != null) {
                            // 计算当前标题级别（统计#的个数）
                            int currentHeaderLevel = (int) sep.chars().filter(ch -> ch == '#').count();

                            // 维护标题栈：移除所有级别大于等于当前级别的标题
                            // 这样可以正确处理标题层级关系，如从### 回退到 ##
                            while (!headerStack.isEmpty() && headerStack.get(headerStack.size() - 1).getLevel() >= currentHeaderLevel) {
                                Header poppedHeader = headerStack.remove(headerStack.size() - 1);
                                initialMetadata.remove(poppedHeader.getName());
                            }

                            // 将当前标题加入栈，并更新元数据
                            Header headerType = new Header(currentHeaderLevel, name, strippedLine.substring(sep.length()).trim());
                            headerStack.add(headerType);
                            initialMetadata.put(name, headerType.getData());
                            initialMetadata.put(HEADER_LEVEL, currentHeaderLevel);
                            // 为每个分段生成唯一ID，用于后续建立父子关系
                            String currentChunkId = UUID.randomUUID().toString();
                            initialMetadata.put(CHUNK_ID, currentChunkId);
                        }

                        // 遇到新标题时，保存之前累积的内容
                        if (!currentContent.isEmpty()) {
                            linesWithMetadata.add(new Line(String.join("\n", currentContent), currentMetadata));
                            currentContent.clear();
                        }

                        // 根据stripHeaders配置决定是否保留标题行
                        if (!stripHeaders) {
                            currentContent.add(strippedLine);
                        }

                        break interrupted;
                    }
                }

                // 处理非标题行
                if (!strippedLine.isEmpty()) {
                    currentContent.add(strippedLine);
                } else if (!currentContent.isEmpty()) {
                    // 遇到空行时，保存当前累积的内容
                    linesWithMetadata.add(new Line(String.join("\n", currentContent), currentMetadata));
                    currentContent.clear();
                }
            }

            // 更新当前元数据为最新的标题信息
            currentMetadata = new HashMap<>(initialMetadata);
        }

        // 处理最后累积的内容
        if (!currentContent.isEmpty()) {
            linesWithMetadata.add(new Line(String.join("\n", currentContent), currentMetadata));
        }

        // 根据配置决定返回方式
        List<Document> segments;
        if (!returnEachLine) {
            // 聚合模式：将相同元数据的行合并
            segments = aggregateLinesToChunks(linesWithMetadata);
        } else {
            // 逐行模式：保持每行独立
            segments = linesWithMetadata.stream()// TODO metaData是空，所以报错了
                    .map(line -> new Document(line.getMetadata().get(CHUNK_ID).toString(), line.getContent(), line.getMetadata()))
                    .collect(Collectors.toList());
        }

        // 如果设置了 chunkSize，对超出大小的分片进行二次切割
        if (chunkSize > 0) {
            segments = splitByChunkSize(segments);
        }

        return segments;
    }

    /**
     * 聚合行为分块
     * 将具有相同元数据的行合并为一个分块，并处理父子关系
     *
     * @param lines 待聚合的行列表
     * @return 聚合后的文档片段列表
     */
    private List<Document> aggregateLinesToChunks(List<Line> lines) {
        List<Line> aggregatedChunks = new ArrayList<>();
        for (Line line : lines) {
            // 情况1：元数据相同，直接合并到上一个分块
            if (!aggregatedChunks.isEmpty() && aggregatedChunks.get(aggregatedChunks.size() - 1).getMetadata().equals(line.getMetadata())) {
                Line last = aggregatedChunks.get(aggregatedChunks.size() - 1);
                last.setContent(last.getContent() + "  \n" + line.getContent());
            }
            // 情况2：元数据不同但上一行以标题结尾且未剥离标题，则也合并
            // 这样可以将标题和其下的第一段内容合并在一起
            else if (!aggregatedChunks.isEmpty() && !aggregatedChunks.get(aggregatedChunks.size() - 1).getMetadata().equals(line.getMetadata())
                    && aggregatedChunks.get(aggregatedChunks.size() - 1).getMetadata().size() < line.getMetadata().size()
                    && aggregatedChunks.get(aggregatedChunks.size() - 1).getContent().split("\n")[aggregatedChunks.get(aggregatedChunks.size() - 1).getContent().split("\n").length - 1].startsWith("#") && !stripHeaders) {

                Line last = aggregatedChunks.get(aggregatedChunks.size() - 1);
                last.setContent(last.getContent() + "  \n" + line.getContent());
            }
            // 情况3：创建新分块
            else {
                aggregatedChunks.add(line);
            }
        }

        // 处理父子分段关系
        if (parentChildModel) {
            try {
                // 遍历所有分块，为非顶级标题建立父子关系
                for (int i = 0; i < aggregatedChunks.size(); i++) {
                    Map<String, Object> currentMetaData = aggregatedChunks.get(i).getMetadata();
                    Integer headerLevel = (Integer) currentMetaData.get(HEADER_LEVEL);
                    // 顶级标题（level=1）或无标题的分块跳过
                    if (headerLevel == null || headerLevel == 1) {
                        continue;
                    }

                    // 向前查找第一个级别更低的标题作为父节点
                    if (headerLevel > 1) {
                        for (int j = i - 1; j >= 0; j--) {
                            Map<String, Object> lastMetaData = aggregatedChunks.get(j).getMetadata();
                            Integer lastHeaderLevel = (Integer) lastMetaData.get(HEADER_LEVEL);
                            if (lastHeaderLevel != null && lastHeaderLevel < headerLevel) {
                                // 将父节点的chunkId设置为当前节点的parentChunkId
                                currentMetaData.put(PARENT_CHUNK_ID, lastMetaData.get(CHUNK_ID));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("父子模式转换失败，" + e.getMessage());
            }
        }

        return aggregatedChunks.stream()
                .map(chunk -> new Document(chunk.getMetadata().get(CHUNK_ID).toString(), chunk.getContent(), chunk.getMetadata()))
                .collect(Collectors.toList());
    }

    /**
     * 对超出 chunkSize 的分片进行二次切割
     * <p>
     * 切割规则：
     * - 未超出 chunkSize 的分片保持不变
     * - 超出 chunkSize 的分片按字符数切割，相邻分片之间保留 overlap 个字符的重叠
     * - 切割出的同组分片之间共享同一个 brotherChunkId，方便检索时拼接
     *
     * @param segments 原始分片列表
     * @return 切割后的分片列表
     */
    private List<Document> splitByChunkSize(List<Document> segments) {
        List<Document> result = new ArrayList<>();
        for (Document segment : segments) {
            String content = segment.getText();
            if (content.length() <= chunkSize) {
                // 未超出 chunkSize，保持原分片不变
                result.add(segment);
            } else {
                // 超出 chunkSize，需要二次切割
                // 生成共同的 brotherChunkId，赋予同组所有分片
                String brotherChunkId = UUID.randomUUID().toString();
                List<Document> subChunks = new ArrayList<>();

                int start = 0;
                while (start < content.length()) {
                    int end = Math.min(start + chunkSize, content.length());
                    String subContent = content.substring(start, end);

                    // 复制元数据并进行更新
                    Map<String, Object> subMetadata = new HashMap<>(segment.getMetadata());
                    String chunkId = UUID.randomUUID().toString();
                    subMetadata.put(CHUNK_ID, chunkId);
                    subMetadata.put(BROTHER_CHUNK_ID, brotherChunkId);

                    subChunks.add(new Document(chunkId, subContent, subMetadata));

                    if (end == content.length()) {
                        break;
                    }
                    // 下一片的起始位置 = 当前片的结束位置 - overlap
                    start = end - Math.min(overlap, end);
                }

                // 回填 brotherChunkIndex 和 brotherChunkTotal，方便后续按序拼接
                int total = subChunks.size();
                for (int i = 0; i < total; i++) {
                    subChunks.get(i).getMetadata().put(BROTHER_CHUNK_INDEX, i + 1);
                    subChunks.get(i).getMetadata().put(BROTHER_CHUNK_TOTAL, total);
                }

                result.addAll(subChunks);
            }
        }
        return result;
    }

    /**
     * 内部类：表示带有元数据的文本行
     */
    public static class Line {
        /**
         * 文本内容
         */
        private String content;
        /**
         * 元数据信息
         */
        private Map<String, Object> metadata;

        public Line(String content, Map<String, Object> metadata) {
            this.content = content;
            this.metadata = metadata;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }
    }

    /**
     * 内部类：表示Markdown标题
     */
    public static class Header {
        /**
         * 标题级别（1-6）
         */
        private int level;
        /**
         * 元数据中的键名
         */
        private String name;
        /**
         * 标题文本内容（不含#标记）
         */
        private String data;

        public Header(int level, String name, String data) {
            this.level = level;
            this.name = name;
            this.data = data;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}

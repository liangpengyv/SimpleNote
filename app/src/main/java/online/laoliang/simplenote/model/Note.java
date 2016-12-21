package online.laoliang.simplenote.model;

/**
 * 数据库表Note的实体类
 * Created by liang on 12/15.
 */
public class Note {

    private int id;
    private String text_content;
    private String grouping;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTextContent() {
        return text_content;
    }

    public void setTextContent(String text_content) {
        this.text_content = text_content;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

}


package me.u6k.narou_analyze.narou_crawler.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "t_novel_meta")
public class NovelMeta {

    @Id
    @Column(name = "ncode", length = 20)
    private String ncode;

    @Column(name = "title", length = 1000, nullable = false)
    private String title;

    @Column(name = "data", nullable = false)
    private byte[] data;

    @Column(name = "updated", nullable = false)
    private Timestamp updated;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getNcode() {
        return ncode;
    }

    public void setNcode(String ncode) {
        this.ncode = ncode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

}

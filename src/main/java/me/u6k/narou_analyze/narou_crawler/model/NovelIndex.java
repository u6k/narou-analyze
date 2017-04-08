
package me.u6k.narou_analyze.narou_crawler.model;

import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "t_novel_index")
public class NovelIndex {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "url", length = 2000, nullable = false)
    private URL url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

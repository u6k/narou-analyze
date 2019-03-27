
package me.u6k.narou_analyze.narou_crawler.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "t_novel_index")
public class NovelIndex {

    @Id
    @Column(name = "ncode", length = 20)
    private String ncode;

    @Column(name = "title", length = 1000, nullable = false)
    private String title;

    @Column(name = "search_date", nullable = false)
    private Date searchDate;

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

    public Date getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(Date searchDate) {
        this.searchDate = searchDate;
    }

}

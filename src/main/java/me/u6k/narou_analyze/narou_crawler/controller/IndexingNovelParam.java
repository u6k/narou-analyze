
package me.u6k.narou_analyze.narou_crawler.controller;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class IndexingNovelParam {

    private Date searchDate;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Date getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(Date searchDate) {
        this.searchDate = searchDate;
    }

}

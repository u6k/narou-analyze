
package me.u6k.narou_analyze.narou_crawler.controller;

import java.io.Serializable;
import java.net.URL;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class IndexingNovelParam implements Serializable {

    private URL searchPageUrl;

    private int limit;

    public URL getSearchPageUrl() {
        return searchPageUrl;
    }

    public void setSearchPageUrl(URL searchPageUrl) {
        this.searchPageUrl = searchPageUrl;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

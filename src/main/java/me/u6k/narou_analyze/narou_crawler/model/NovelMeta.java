
package me.u6k.narou_analyze.narou_crawler.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "writer", length = 100, nullable = false)
    private String writer;

    @Column(name = "story", length = 4000, nullable = true)
    private String story;

    @Column(name = "big_genre", nullable = true)
    private int bigGenre;

    @Column(name = "genre", nullable = true)
    private int genre;

    @Column(name = "keywords", nullable = true)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> keywords;

    @Column(name = "general_firstup", nullable = false)
    private Timestamp generalFirstup;

    @Column(name = "general_lastup", nullable = false)
    private Timestamp generalLastup;

    @Column(name = "novel_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private NovelType novelType;

    @Column(name = "is_end", nullable = false)
    private boolean isEnd;

    @Column(name = "general_all_no", nullable = false)
    private int generalAllNo;

    @Column(name = "length", nullable = false)
    private int length;

    @Column(name = "time", nullable = false)
    private int time;

    @Column(name = "is_stop", nullable = false)
    private boolean isStop;

    @Column(name = "is_r15", nullable = false)
    private boolean isR15;

    @Column(name = "is_bl", nullable = false)
    private boolean isBoysLove;

    @Column(name = "is_gl", nullable = false)
    private boolean isGirlsLove;

    @Column(name = "is_zankoku", nullable = false)
    private boolean isZankoku;

    @Column(name = "is_tensei", nullable = false)
    private boolean isTensei;

    @Column(name = "is_tenni", nullable = false)
    private boolean isTenni;

    @Column(name = "global_point", nullable = false)
    private int globalPoint;

    @Column(name = "favorite_novel_count", nullable = false)
    private int favoriteNovelCount;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "all_point", nullable = false)
    private int allPoint;

    @Column(name = "all_hyoka_count", nullable = false)
    private int allHyokaCount;

    @Column(name = "sasie_count", nullable = false)
    private int sasieCount;

    @Column(name = "kaiwa_ritu", nullable = false)
    private int kaiwaRitu;

    @Column(name = "novel_updated_at", nullable = false)
    private Timestamp novelUpdatedAt;

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public int getBigGenre() {
        return bigGenre;
    }

    public void setBigGenre(int bigGenre) {
        this.bigGenre = bigGenre;
    }

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Timestamp getGeneralFirstup() {
        return generalFirstup;
    }

    public void setGeneralFirstup(Timestamp generalFirstup) {
        this.generalFirstup = generalFirstup;
    }

    public Timestamp getGeneralLastup() {
        return generalLastup;
    }

    public void setGeneralLastup(Timestamp generalLastup) {
        this.generalLastup = generalLastup;
    }

    public NovelType getNovelType() {
        return novelType;
    }

    public void setNovelType(NovelType novelType) {
        this.novelType = novelType;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public int getGeneralAllNo() {
        return generalAllNo;
    }

    public void setGeneralAllNo(int generalAllNo) {
        this.generalAllNo = generalAllNo;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean isStop) {
        this.isStop = isStop;
    }

    public boolean isR15() {
        return isR15;
    }

    public void setR15(boolean isR15) {
        this.isR15 = isR15;
    }

    public boolean isBoysLove() {
        return isBoysLove;
    }

    public void setBoysLove(boolean isBoysLove) {
        this.isBoysLove = isBoysLove;
    }

    public boolean isGirlsLove() {
        return isGirlsLove;
    }

    public void setGirlsLove(boolean isGirlsLove) {
        this.isGirlsLove = isGirlsLove;
    }

    public boolean isZankoku() {
        return isZankoku;
    }

    public void setZankoku(boolean isZankoku) {
        this.isZankoku = isZankoku;
    }

    public boolean isTensei() {
        return isTensei;
    }

    public void setTensei(boolean isTensei) {
        this.isTensei = isTensei;
    }

    public boolean isTenni() {
        return isTenni;
    }

    public void setTenni(boolean isTenni) {
        this.isTenni = isTenni;
    }

    public int getGlobalPoint() {
        return globalPoint;
    }

    public void setGlobalPoint(int globalPoint) {
        this.globalPoint = globalPoint;
    }

    public int getFavoriteNovelCount() {
        return favoriteNovelCount;
    }

    public void setFavoriteNovelCount(int favoriteNovelCount) {
        this.favoriteNovelCount = favoriteNovelCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getAllPoint() {
        return allPoint;
    }

    public void setAllPoint(int allPoint) {
        this.allPoint = allPoint;
    }

    public int getAllHyokaCount() {
        return allHyokaCount;
    }

    public void setAllHyokaCount(int allHyokaCount) {
        this.allHyokaCount = allHyokaCount;
    }

    public int getSasieCount() {
        return sasieCount;
    }

    public void setSasieCount(int sasieCount) {
        this.sasieCount = sasieCount;
    }

    public int getKaiwaRitu() {
        return kaiwaRitu;
    }

    public void setKaiwaRitu(int kaiwaRitu) {
        this.kaiwaRitu = kaiwaRitu;
    }

    public Timestamp getNovelUpdatedAt() {
        return novelUpdatedAt;
    }

    public void setNovelUpdatedAt(Timestamp novelUpdatedAt) {
        this.novelUpdatedAt = novelUpdatedAt;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

}


package me.u6k.narou_analyze.narou_crawler.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NovelIndexRepository extends JpaRepository<NovelIndex, String> {

    @Query("select idx from NovelIndex idx where idx.searchDate = :searchDate")
    List<NovelIndex> findBySearchDate(@Param("searchDate") Date searchDate);

}

package com.nawasenahost.authservice.repository;

import com.nawasenahost.authservice.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("select a from AuditLog a " +
            "where (:hotelId is null or a.hotelId = :hotelId) " +
            "and (:entityType is null or a.entityType = :entityType) " +
            "and (:actor is null or a.actor = :actor) " +
            "order by a.createdAt desc")
    Page<AuditLog> search(@Param("hotelId") Integer hotelId,
                          @Param("entityType") String entityType,
                          @Param("actor") String actor,
                          Pageable pageable);

}

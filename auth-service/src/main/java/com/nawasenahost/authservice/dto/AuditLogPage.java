package com.nawasenahost.authservice.dto;

import com.nawasenahost.authservice.entity.AuditLog;

import java.util.List;

public class AuditLogPage {

    private List<AuditLog> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public AuditLogPage(List<AuditLog> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<AuditLog> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}

package com.automo.auth.dto;

import java.util.List;

public record PagedUserResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last,
    int numberOfElements,
    boolean empty
) {
    public static <T> PagedUserResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = page == 0;
        boolean last = page >= totalPages - 1;
        int numberOfElements = content.size();
        boolean empty = content.isEmpty();
        
        return new PagedUserResponse<>(
            content, page, size, totalElements, totalPages,
            first, last, numberOfElements, empty
        );
    }
}
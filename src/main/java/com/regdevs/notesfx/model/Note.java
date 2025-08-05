package com.regdevs.notesfx.model;

import java.time.Instant;
import java.util.Set;
import java.util.regex.*;
import java.util.*;

public record Note(
        long id,
        String title,
        String content,
        String tags,
        String createdAt,
        String updatedAt
) {
    private static final Pattern TAG_PATTERN = Pattern.compile("(?:^|\\s)#(\\w+)");

    public static String isoNow() {
        return Instant.now().toString();
    }

    public static Set<String> extractTags(String text) {
        Matcher m = TAG_PATTERN.matcher(text == null ? "" : text);
        Set<String> set = new HashSet<>();
        while (m.find()) set.add(m.group(1).toLowerCase());
        return set;
    }

    public static String normalizeTags(Set<String> tags) {
        return String.join(",", tags);
    }

    public Set<String> tagSet() {
        if (tags == null || tags.isBlank()) return Set.of();
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.toSet());
    }

}

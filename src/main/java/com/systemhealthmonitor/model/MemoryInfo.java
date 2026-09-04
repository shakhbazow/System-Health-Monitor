package com.systemhealthmonitor.model;

/**
 * Immutable record representing RAM/memory information at a point in time.
 *
 * @param totalBytes    Total installed RAM in bytes
 * @param usedBytes     Currently used RAM in bytes
 * @param freeBytes     Available RAM in bytes
 * @param usagePercent  Memory usage as percentage 0-100
 */
public record MemoryInfo(
        long totalBytes,
        long usedBytes,
        long freeBytes,
        double usagePercent
) {}


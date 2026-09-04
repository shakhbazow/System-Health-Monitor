package com.systemhealthmonitor.model;

/**
 * Immutable record representing CPU information at a point in time.
 *
 * A record automatically creates:
 * - A constructor that takes all fields
 * - Getter methods (model(), coreCount(), etc.)
 * - equals(), hashCode(), toString()
 *
 * @param model                  CPU model name (e.g., "Intel Core i7-12700K")
 * @param coreCount              Number of physical cores
 * @param logicalProcessorCount  Total logical processors (cores × threads)
 * @param usagePercent           Current CPU usage 0-100
 */
public record CpuInfo(
        String model,
        int coreCount,
        int logicalProcessorCount,
        double usagePercent
) {}
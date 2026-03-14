package io.github.viciousxerra.tsidgenerator.api;

/**
 * A value holder containing configuration information about the shard coordinate part of generated identifiers.
 * @param id Shard coordinate ID.
 * @param grantedBits Number of bits allocated for shard coordinate.
 */
public record ShardIdSettings(int id, int grantedBits) {
}

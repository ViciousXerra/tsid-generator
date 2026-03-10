package io.github.viciousxerra.tsidgenerator.api;

/**
 * A source of configuration of time sorted unique ID generator.
 */
public interface Configuration {
    /**
     * Get timestamp part of settings.
     *
     * @return a value holder containing configuration information about the timestamp part of generated identifiers.
     */
    TimestampSettings getTimestampSettings();

    /**
     * Get shard coordinate part of settings.
     *
     * @return an array of value holder containing configuration information about the shard coordinate part
     * of generated identifiers:
     * <p>
     * empty array - if no information is provided,
     * <p>
     * array with size equal to 1 - if information is provided for single shard ID,
     * <p>
     * array with size equal to 2 - if information is provided for data center ID and machine ID.
     */
    ShardIdSettings[] getShardIdSettings();

    /**
     * Get sequence part of settings.
     *
     * @return a value holder containing configuration information about the sequence part of generated identifiers.
     */
    SequenceSettings getSequenceSettings();
}

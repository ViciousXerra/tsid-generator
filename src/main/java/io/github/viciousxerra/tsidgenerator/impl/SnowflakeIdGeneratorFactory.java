package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.Configuration;
import io.github.viciousxerra.tsidgenerator.api.SequenceOverflowHandleStrategy;
import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueIdGenerator;
import java.time.OffsetDateTime;

/**
 * A factory class which produces default implementation of {@link TimeSortedUniqueIdGenerator} with
 * Snowflake ID preset.
 */
public final class SnowflakeIdGeneratorFactory extends DefaultTimeSortedUniqueIdGeneratorFactory {

    private static final int SNOWFLAKE_ID_TIMESTAMP_BITS = 41;
    private static final int SNOWFLAKE_ID_SHARD_BITS = 10;
    private static final int SNOWFLAKE_ID_SEQUENCE_BITS = 12;

    /**
     * Constructs {@link SnowflakeIdGeneratorFactory} factory with passed args.
     * <p>
     * This constructor delegates {@link Configuration} creation to {@link GeneratorConfiguration.Builder}.
     *
     * @param odt                            start point of timeline.
     * @param shardId                        shard ID.
     * @param sequenceOverflowHandleStrategy sequence overflow handle strategy related enumeration.
     */
    public SnowflakeIdGeneratorFactory(OffsetDateTime odt, int shardId,
                                       SequenceOverflowHandleStrategy sequenceOverflowHandleStrategy) {
        super(new GeneratorConfiguration.Builder()
                .withStartPoint(odt)
                .withTimestampBits(SNOWFLAKE_ID_TIMESTAMP_BITS)
                .withShardId(shardId)
                .withShardIdBits(SNOWFLAKE_ID_SHARD_BITS)
                .withSequenceBits(SNOWFLAKE_ID_SEQUENCE_BITS)
                .withSequenceOverflowStrategy(sequenceOverflowHandleStrategy)
                .build());
    }
}

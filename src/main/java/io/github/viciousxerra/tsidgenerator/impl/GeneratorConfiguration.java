package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.Configuration;
import io.github.viciousxerra.tsidgenerator.api.SequenceOverflowHandleStrategy;
import io.github.viciousxerra.tsidgenerator.api.SequenceSettings;
import io.github.viciousxerra.tsidgenerator.api.ShardIdSettings;
import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueIdGenerator;
import io.github.viciousxerra.tsidgenerator.api.TimestampSettings;
import io.github.viciousxerra.tsidgenerator.exception.TimelineBeforeStartPointException;
import org.apache.commons.lang3.Validate;

import java.time.OffsetDateTime;

import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.DATA_CENTER_ID_MUST_NOT_BE_GREATER_THAN_MESSAGE_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.GENERAL_BIT_SIZE_EXCEEDED_MESSAGE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.MACHINE_ID_MUST_NOT_BE_GREATER_THAN_MESSAGE_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.NUMBER_OF_SEQUENCE_GRANTED_BITS_MUST_BE_POSITIVE_MESSAGE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.SEQUENCE_OVERFLOW_HANDLER_STRATEGY_NULL_MESSAGE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.SHARD_ID_MUST_NOT_BE_GREATER_THAN_MESSAGE_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.START_POINT_NULL_MESSAGE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.TIMESTAMP_BITS_TEN_YEARS_WARRANTY_MESSAGE_TEMPLATE;
import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.UNSUPPORTED_CONFIGURATION_MESSAGE;

/**
 * A source of default configuration of time sorted unique ID generator.
 */
public final class GeneratorConfiguration implements Configuration {
    private final TimestampSettings timestampSettings;
    private final ShardIdSettings[] shardIdSettings;
    private final SequenceSettings sequenceSettings;

    private GeneratorConfiguration(TimestampSettings timestampSettings,
                                   ShardIdSettings[] shardIdSettings,
                                   SequenceSettings sequenceSettings) {
        this.timestampSettings = timestampSettings;
        this.shardIdSettings = shardIdSettings;
        this.sequenceSettings = sequenceSettings;
    }

    @Override
    public TimestampSettings getTimestampSettings() {
        return timestampSettings;
    }

    @Override
    public ShardIdSettings[] getShardIdSettings() {
        return shardIdSettings;
    }

    @Override
    public SequenceSettings getSequenceSettings() {
        return sequenceSettings;
    }

    /**
     * Builder class for {@link GeneratorConfiguration}.
     */
    public static class Builder {
        private static final int GUARANTEED_TEN_YEARS_BIT_SIZE = 39;
        private static final int MAX_BITS_LIMIT = 63;

        private OffsetDateTime startPoint;
        private int timestampBits;
        private int shardId;
        private int shardIdBits;
        private int dataCenterId;
        private int dataCenterIdBits;
        private int machineId;
        private int machineIdBits;
        private int sequenceBits;
        private SequenceOverflowHandleStrategy sequenceOverflowHandleStrategy;

        /**
         * Set timeline start point.
         *
         * @param startPoint start point.
         * @return this {@link Builder}.
         */
        public Builder withStartPoint(OffsetDateTime startPoint) {
            this.startPoint = startPoint;
            return this;
        }

        /**
         * Set timestamp bits.
         *
         * @param bits timestamp granted bits.
         * @return this {@link Builder}.
         */
        public Builder withTimestampBits(int bits) {
            this.timestampBits = bits;
            return this;
        }

        /**
         * Set shard ID.
         *
         * @param shardId shard ID.
         * @return this {@link Builder}.
         */
        public Builder withShardId(int shardId) {
            this.shardId = shardId;
            return this;
        }

        /**
         * Set shard ID bits.
         *
         * @param bits shard ID granted bits.
         * @return this {@link Builder}.
         */
        public Builder withShardIdBits(int bits) {
            this.shardIdBits = bits;
            return this;
        }

        /**
         * Set data center ID.
         *
         * @param dataCenterId data center ID.
         * @return this {@link Builder}.
         */
        public Builder withDataCenterId(int dataCenterId) {
            this.dataCenterId = dataCenterId;
            return this;
        }

        /**
         * Set data center ID bits.
         *
         * @param bits data center ID granted bits.
         * @return this {@link Builder}.
         */
        public Builder withDataCenterIdBits(int bits) {
            this.dataCenterIdBits = bits;
            return this;
        }

        /**
         * Set machine ID.
         *
         * @param machineId machine ID.
         * @return this {@link Builder}.
         */
        public Builder withMachineId(int machineId) {
            this.machineId = machineId;
            return this;
        }

        /**
         * Set machine ID bits.
         *
         * @param bits machine ID granted bits.
         * @return this {@link Builder}.
         */
        public Builder withMachineIdBits(int bits) {
            this.machineIdBits = bits;
            return this;
        }

        /**
         * Set sequence bits.
         *
         * @param bits sequence granted bits.
         * @return this {@link Builder}.
         */
        public Builder withSequenceBits(int bits) {
            this.sequenceBits = bits;
            return this;
        }

        /**
         * Set sequence overflow handle strategy related enumeration.
         *
         * @param sequenceOverflowStrategy related handle strategy related enumeration.
         * @return this {@link Builder}.
         */
        public Builder withSequenceOverflowStrategy(SequenceOverflowHandleStrategy sequenceOverflowStrategy) {
            this.sequenceOverflowHandleStrategy = sequenceOverflowStrategy;
            return this;
        }

        /**
         * Build {@link GeneratorConfiguration} with previously set parameters.
         *
         * @return {@link Configuration} for using in creating factory for creating default implementation of
         * {@link TimeSortedUniqueIdGenerator}
         * @throws NullPointerException              if passed {@link OffsetDateTime} startPoint or
         *                                           {@link SequenceOverflowHandleStrategy} sequenceOverflowStrategy is null.
         * @throws TimelineBeforeStartPointException if passed {@link OffsetDateTime} startPoint is ahead from current
         *                                           time.
         * @throws IllegalArgumentException          if passed timestamp bits less than 39 (this implementation guarantees
         *                                           validity for 10 years)
         *                                           or number of granted bits for sequence is not specified / not positive.
         * @throws IllegalArgumentException          if attempted to build unsupported configuration.
         *                                           <p>
         *                                           1) Use the builder without specifying shard coordinates and corresponding bit range completely if you don't
         *                                           need them.
         *                                           <p>
         *                                           2) Use the builder with specifying non-negative shard ID (optional, 0 if not specified) and corresponding
         *                                           bit range only.
         *                                           <p>
         *                                           3) Use the builder with specifying non-negative data center ID (optional, 0 if not specified) and
         *                                           non-negative machine ID (optional, 0 if not specified) and corresponding bit ranges only.
         * @throws IllegalArgumentException          if shard ID, data center ID or machine ID greater than maximum value for
         *                                           related number of granted bits (for example 5 bits stores maximum 31).
         */
        public GeneratorConfiguration build() {
            Validate.notNull(startPoint, START_POINT_NULL_MESSAGE);
            TimeUtils.checkTimelineBeforeStartPoint(OffsetDateTime.now(startPoint.getOffset()), startPoint);
            Validate.isTrue(timestampBits >= GUARANTEED_TEN_YEARS_BIT_SIZE,
                    TIMESTAMP_BITS_TEN_YEARS_WARRANTY_MESSAGE_TEMPLATE, GUARANTEED_TEN_YEARS_BIT_SIZE);
            Validate.isTrue(sequenceBits > 0, NUMBER_OF_SEQUENCE_GRANTED_BITS_MUST_BE_POSITIVE_MESSAGE);
            Validate.notNull(sequenceOverflowHandleStrategy, SEQUENCE_OVERFLOW_HANDLER_STRATEGY_NULL_MESSAGE);
            boolean shardIdNotSpecified = shardId == 0 && shardIdBits == 0;
            boolean dataCenterIdAndMachineIdNotSpecified =
                    dataCenterId == 0 && dataCenterIdBits == 0 &&
                            machineId == 0 && machineIdBits == 0;
            boolean withoutShardCoordinates = shardIdNotSpecified && dataCenterIdAndMachineIdNotSpecified;
            boolean withShardId = shardId >= 0 && shardIdBits > 0 && dataCenterIdAndMachineIdNotSpecified;
            boolean withCoupledDataCenterIdAndMachineId =
                    shardIdNotSpecified && dataCenterId >= 0 && dataCenterIdBits > 0 &&
                            machineId >= 0 && machineIdBits > 0;
            Validate.isTrue(withoutShardCoordinates || withShardId || withCoupledDataCenterIdAndMachineId,
                    UNSUPPORTED_CONFIGURATION_MESSAGE);
            int timestampAndSequenceGrantedBitsNumbersSum = Math.addExact(timestampBits, sequenceBits);
            int addShardIdBits = Math.addExact(timestampAndSequenceGrantedBitsNumbersSum, shardIdBits);
            int addDataCenterIdBits = Math.addExact(addShardIdBits, dataCenterIdBits);
            int addMachineIdBits = Math.addExact(addDataCenterIdBits, machineIdBits);
            Validate.isTrue(addMachineIdBits <= MAX_BITS_LIMIT, GENERAL_BIT_SIZE_EXCEEDED_MESSAGE);
            if (withShardId) {
                int shardIdMaxValue = BitUtils.getIntMaxValue(shardIdBits);
                Validate.isTrue(shardIdMaxValue >= shardId,
                        SHARD_ID_MUST_NOT_BE_GREATER_THAN_MESSAGE_TEMPLATE, shardIdMaxValue);
            }
            if (withCoupledDataCenterIdAndMachineId) {
                int dataCenterIdMaxValue = BitUtils.getIntMaxValue(dataCenterIdBits);
                Validate.isTrue(dataCenterIdMaxValue >= dataCenterId,
                        DATA_CENTER_ID_MUST_NOT_BE_GREATER_THAN_MESSAGE_TEMPLATE, dataCenterIdMaxValue);
                int machineIdMaxValue = BitUtils.getIntMaxValue(machineIdBits);
                Validate.isTrue(machineIdMaxValue >= machineId,
                        MACHINE_ID_MUST_NOT_BE_GREATER_THAN_MESSAGE_TEMPLATE, machineIdMaxValue);
            }
            TimestampSettings timestampSettings = new TimestampSettings(startPoint, BitUtils.getLongMaxValue(timestampBits), timestampBits);
            ShardIdSettings[] shardIdSettings =
                    withoutShardCoordinates ? new ShardIdSettings[0]
                            : withShardId ? new ShardIdSettings[]{new ShardIdSettings(shardId, shardIdBits)}
                            : new ShardIdSettings[]{new ShardIdSettings(dataCenterId, dataCenterIdBits), new ShardIdSettings(machineId, machineIdBits)};
            SequenceSettings sequenceSettings = new SequenceSettings(BitUtils.getIntMaxValue(sequenceBits), sequenceBits, sequenceOverflowHandleStrategy);
            return new GeneratorConfiguration(timestampSettings, shardIdSettings, sequenceSettings);
        }
    }

}

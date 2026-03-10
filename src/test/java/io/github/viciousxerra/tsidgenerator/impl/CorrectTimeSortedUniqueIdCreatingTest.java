package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.SequenceOverflowHandleStrategy;
import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueId;
import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueIdGenerator;
import io.github.viciousxerra.tsidgenerator.exception.SequenceOverflowException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static io.github.viciousxerra.tsidgenerator.impl.StringTemplates.SEQUENCE_OVERFLOW_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class CorrectTimeSortedUniqueIdCreatingTest {
    private static final OffsetDateTime OFFSET_DATE_TIME_NOW = OffsetDateTime.parse("2026-01-01T00:00:00+00:00");
    private static final long DELTA_SECONDS = 5L;
    private static final OffsetDateTime VALID_START_POINT = OFFSET_DATE_TIME_NOW.minusSeconds(DELTA_SECONDS);
    private static final ZoneOffset ZONE_OFFSET = OFFSET_DATE_TIME_NOW.getOffset();
    private static final int TIMESTAMP_BITS = 41;
    private static final int SHARD_ID = 5;
    private static final int SHARD_ID_BITS = 10;
    private static final int DATA_CENTER_ID = 4;
    private static final int DATA_CENTER_ID_BITS = 5;
    private static final int MACHINE_ID = 3;
    private static final int MACHINE_ID_BITS = DATA_CENTER_ID_BITS;
    private static final int SEQUENCE_BITS = 6;
    private static final int SEQUENCE_MAX_VALUE = (1 << SEQUENCE_BITS) - 1;

    @Test
    @DisplayName("Test correct creating without shard.")
    void testCorrectCreatingWithoutShard() {
        //Given
        List<TimeSortedUniqueId> ids = new ArrayList<>();
        Set<Integer> expectedSequenceToRemoveSet = new HashSet<>();
        try (MockedStatic<OffsetDateTime> offsetDateTimeMocked = Mockito.mockStatic(OffsetDateTime.class)) {
            offsetDateTimeMocked.when(() -> OffsetDateTime.now(ZONE_OFFSET)).thenReturn(OFFSET_DATE_TIME_NOW);
            GeneratorConfiguration configuration = new GeneratorConfiguration.Builder()
                    .withStartPoint(VALID_START_POINT)
                    .withTimestampBits(TIMESTAMP_BITS)
                    .withSequenceBits(SEQUENCE_BITS)
                    .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.THROW_EXCEPTION)
                    .build();
            DefaultTimeSortedUniqueIdGeneratorFactory factory = new DefaultTimeSortedUniqueIdGeneratorFactory(configuration);
            TimeSortedUniqueIdGenerator generator = factory.create();
            IntStream.rangeClosed(0, SEQUENCE_MAX_VALUE)
                    .boxed()
                    .forEach(expectedSequenceToRemoveSet::add);
            for (int i = 0; i <= SEQUENCE_MAX_VALUE; i++) {
                TimeSortedUniqueId id = generator.nextId();
                ids.add(id);
                expectedSequenceToRemoveSet.remove(id.getSequence());
            }
            assertThatThrownBy(generator::nextId)
                    .isExactlyInstanceOf(SequenceOverflowException.class)
                    .hasMessage(SEQUENCE_OVERFLOW_MESSAGE);
        }
        ids.forEach(id -> assertAll(
                () -> assertThat(id.getTimestamp()).isEqualTo(OFFSET_DATE_TIME_NOW),
                () -> assertThat(id.getShardCoordinates()).isEmpty(),
                () -> assertThat(id.getSequence()).isLessThanOrEqualTo(SEQUENCE_MAX_VALUE),
                () -> assertThat(expectedSequenceToRemoveSet).isEmpty()
        ));
    }

    @Test
    @DisplayName("Test correct creating with shard ID.")
    void testCorrectCreatingWithShardId() {
        //Given
        List<TimeSortedUniqueId> ids = new ArrayList<>();
        Set<Integer> expectedSequenceToRemoveSet = new HashSet<>();
        try (MockedStatic<OffsetDateTime> offsetDateTimeMocked = Mockito.mockStatic(OffsetDateTime.class)) {
            offsetDateTimeMocked.when(() -> OffsetDateTime.now(ZONE_OFFSET)).thenReturn(OFFSET_DATE_TIME_NOW);
            GeneratorConfiguration configuration = new GeneratorConfiguration.Builder()
                    .withStartPoint(VALID_START_POINT)
                    .withTimestampBits(TIMESTAMP_BITS)
                    .withShardId(SHARD_ID)
                    .withShardIdBits(SHARD_ID_BITS)
                    .withSequenceBits(SEQUENCE_BITS)
                    .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.THROW_EXCEPTION)
                    .build();
            DefaultTimeSortedUniqueIdGeneratorFactory factory = new DefaultTimeSortedUniqueIdGeneratorFactory(configuration);
            TimeSortedUniqueIdGenerator generator = factory.create();
            IntStream.rangeClosed(0, SEQUENCE_MAX_VALUE)
                    .boxed()
                    .forEach(expectedSequenceToRemoveSet::add);
            for (int i = 0; i <= SEQUENCE_MAX_VALUE; i++) {
                TimeSortedUniqueId id = generator.nextId();
                ids.add(id);
                expectedSequenceToRemoveSet.remove(id.getSequence());
            }
            assertThatThrownBy(generator::nextId)
                    .isExactlyInstanceOf(SequenceOverflowException.class)
                    .hasMessage(SEQUENCE_OVERFLOW_MESSAGE);
        }
        ids.forEach(id -> assertAll(
                () -> assertThat(id.getTimestamp()).isEqualTo(OFFSET_DATE_TIME_NOW),
                () -> assertThat(id.getShardCoordinates()).hasSize(1),
                () -> assertThat(id.getSequence()).isLessThanOrEqualTo(SEQUENCE_MAX_VALUE),
                () -> assertThat(expectedSequenceToRemoveSet).isEmpty()
        ));
    }

    @Test
    @DisplayName("Test correct creating with data center ID and machine ID.")
    void testCorrectCreatingWithDataCenterIdAndMachineId() {
        //Given
        List<TimeSortedUniqueId> ids = new ArrayList<>();
        Set<Integer> expectedSequenceToRemoveSet = new HashSet<>();
        try (MockedStatic<OffsetDateTime> offsetDateTimeMocked = Mockito.mockStatic(OffsetDateTime.class)) {
            offsetDateTimeMocked.when(() -> OffsetDateTime.now(ZONE_OFFSET)).thenReturn(OFFSET_DATE_TIME_NOW);
            GeneratorConfiguration configuration = new GeneratorConfiguration.Builder()
                    .withStartPoint(VALID_START_POINT)
                    .withTimestampBits(TIMESTAMP_BITS)
                    .withDataCenterId(DATA_CENTER_ID)
                    .withDataCenterIdBits(DATA_CENTER_ID_BITS)
                    .withMachineId(MACHINE_ID)
                    .withMachineIdBits(MACHINE_ID_BITS)
                    .withSequenceBits(SEQUENCE_BITS)
                    .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.THROW_EXCEPTION)
                    .build();
            DefaultTimeSortedUniqueIdGeneratorFactory factory = new DefaultTimeSortedUniqueIdGeneratorFactory(configuration);
            TimeSortedUniqueIdGenerator generator = factory.create();
            IntStream.rangeClosed(0, SEQUENCE_MAX_VALUE)
                    .boxed()
                    .forEach(expectedSequenceToRemoveSet::add);
            for (int i = 0; i <= SEQUENCE_MAX_VALUE; i++) {
                TimeSortedUniqueId id = generator.nextId();
                ids.add(id);
                expectedSequenceToRemoveSet.remove(id.getSequence());
            }
            assertThatThrownBy(generator::nextId)
                    .isExactlyInstanceOf(SequenceOverflowException.class)
                    .hasMessage(SEQUENCE_OVERFLOW_MESSAGE);
        }
        assertThat(expectedSequenceToRemoveSet).isEmpty();
        ids.forEach(id -> assertAll(
                () -> assertThat(id.getTimestamp()).isEqualTo(OFFSET_DATE_TIME_NOW),
                () -> assertThat(id.getShardCoordinates()).hasSize(2),
                () -> assertThat(id.getSequence()).isLessThanOrEqualTo(SEQUENCE_MAX_VALUE)
        ));
    }

    @RepeatedTest(10)
    @DisplayName("Test correct creating with data center ID and machine ID in multithreading environment.")
    void testCorrectCreatingWithDataCenterIdAndMachineIdWithMultithreading() {
        //Given
        List<TimeSortedUniqueId> ids = new CopyOnWriteArrayList<>();
        Set<Integer> expectedSequenceToRemoveSet = ConcurrentHashMap.newKeySet();
        GeneratorConfiguration configuration;
        try (MockedStatic<OffsetDateTime> offsetDateTimeMocked = Mockito.mockStatic(OffsetDateTime.class)) {
            offsetDateTimeMocked.when(() -> OffsetDateTime.now(ZONE_OFFSET)).thenReturn(OFFSET_DATE_TIME_NOW);
            configuration = new GeneratorConfiguration.Builder()
                    .withStartPoint(VALID_START_POINT)
                    .withTimestampBits(TIMESTAMP_BITS)
                    .withDataCenterId(DATA_CENTER_ID)
                    .withDataCenterIdBits(DATA_CENTER_ID_BITS)
                    .withMachineId(MACHINE_ID)
                    .withMachineIdBits(MACHINE_ID_BITS)
                    .withSequenceBits(SEQUENCE_BITS) //64 sequences per same millisecond
                    .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.THROW_EXCEPTION)
                    .build();
        }
        assertThat(configuration).isNotNull();
        DefaultTimeSortedUniqueIdGeneratorFactory factory = new DefaultTimeSortedUniqueIdGeneratorFactory(configuration);
        TimeSortedUniqueIdGenerator generator = factory.create();
        IntStream.rangeClosed(0, SEQUENCE_MAX_VALUE)
                .boxed()
                .forEach(expectedSequenceToRemoveSet::add);
        int threads = 4;
        int requestsPerThread = 16;
        CountDownLatch latch = new CountDownLatch(threads * requestsPerThread);
        Runnable r = () -> {
            try (MockedStatic<OffsetDateTime> localThreadOdtMocked = Mockito.mockStatic(OffsetDateTime.class)) {
                localThreadOdtMocked.when(() -> OffsetDateTime.now(ZONE_OFFSET)).thenReturn(OFFSET_DATE_TIME_NOW);
                for (int i = 0; i < requestsPerThread; i++) {
                    try {
                        TimeSortedUniqueId id = generator.nextId();
                        ids.add(id);
                        expectedSequenceToRemoveSet.remove(id.getSequence());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                }
            }
        };
        try (ExecutorService executorService = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                executorService.submit(r);
            }
            executorService.shutdown();
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try (MockedStatic<OffsetDateTime> odtMocked = Mockito.mockStatic(OffsetDateTime.class)) {
            odtMocked.when(() -> OffsetDateTime.now(ZONE_OFFSET)).thenReturn(OFFSET_DATE_TIME_NOW);
            assertThatThrownBy(generator::nextId)
                    .isExactlyInstanceOf(SequenceOverflowException.class)
                    .hasMessage(SEQUENCE_OVERFLOW_MESSAGE);
        }
        assertThat(latch.getCount()).isEqualTo(0);
        assertThat(expectedSequenceToRemoveSet).isEmpty();
        ids.forEach(id -> assertAll(
                () -> assertThat(id.getTimestamp()).isEqualTo(OFFSET_DATE_TIME_NOW),
                () -> assertThat(id.getShardCoordinates()).hasSize(2),
                () -> assertThat(id.getSequence()).isLessThanOrEqualTo(SEQUENCE_MAX_VALUE)
        ));
    }

    @ParameterizedTest
    @EnumSource(
            value = SequenceOverflowHandleStrategy.class,
            names = {"THREAD_FIXED_SLEEP", "THREAD_SLEEP_WITH_JITTER", "SPIN_ON_WAIT"}
    )
    @DisplayName("Test unique creating with data center ID and machine ID in multithreading environment")
    void testUniqueCreatingWithDataCenterIdAndMachineIdInMultithreadingEnvironment(SequenceOverflowHandleStrategy sequenceOverflowHandleStrategy) {
        Set<TimeSortedUniqueId> ids = ConcurrentHashMap.newKeySet();
        GeneratorConfiguration configuration = new GeneratorConfiguration.Builder()
                .withStartPoint(OffsetDateTime.now().minusSeconds(DELTA_SECONDS))
                .withTimestampBits(41)
                .withDataCenterId(13)
                .withDataCenterIdBits(4)
                .withMachineId(37)
                .withMachineIdBits(6)
                .withSequenceBits(12)
                .withSequenceOverflowStrategy(sequenceOverflowHandleStrategy)
                .build();
        DefaultTimeSortedUniqueIdGeneratorFactory factory = new DefaultTimeSortedUniqueIdGeneratorFactory(configuration);
        TimeSortedUniqueIdGenerator generator = factory.create();
        int threads = Runtime.getRuntime().availableProcessors() / 2;
        int idsPerThread = 100000;
        CountDownLatch latch = new CountDownLatch(threads * idsPerThread);
        Runnable idRequestRunnable = () -> {
            for (int i = 0; i < idsPerThread; i++) {
                try {
                    TimeSortedUniqueId id = generator.nextId();
                    ids.add(id);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }
        };
        try (ExecutorService threadPoolService = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                threadPoolService.submit(idRequestRunnable);
            }
            threadPoolService.shutdown();
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertThat(latch.getCount()).isEqualTo(0L);
        assertThat(ids).hasSize(threads * idsPerThread);
    }

}

[![Basic CI on pull request.](https://github.com/ViciousXerra/tsid-generator/actions/workflows/basic-pr-ci.yml/badge.svg)](https://github.com/ViciousXerra/tsid-generator/actions/workflows/basic-pr-ci.yml)  
[![Deploy snapshot.](https://github.com/ViciousXerra/tsid-generator/actions/workflows/development-deploy-snapshot.yml/badge.svg)](https://github.com/ViciousXerra/tsid-generator/actions/workflows/development-deploy-snapshot.yml)  
[![Deploy release.](https://github.com/ViciousXerra/tsid-generator/actions/workflows/deploy-release.yml/badge.svg)](https://github.com/ViciousXerra/tsid-generator/actions/workflows/deploy-release.yml)  
![Coverage](.github/badges/jacoco.svg)

# Time sorted unique ID generator

Default implementation of the thread-safe up to 63 bits time-sorted unique ID generator.

## Provided content

Contains factories for creating generator components, the generator itself, and a configuration class builder.  
Supports creating a generator only with a timestamp and sequence, with a shard ID, as well as with a data center ID and machine ID.  
Also included [Snowflake ID](https://en.wikipedia.org/wiki/Snowflake_ID) generator factory preset.

## Usage example

* Declare the dependency via your preferred build system:

  * Maven:
      ```xml
      <dependency>
          <groupId>io.github.viciousxerra</groupId>
          <artifactId>tsid-generator</artifactId>
          <version>1.0.0</version>
      </dependency>
      ```

  * Gradle (with Groovy DSL):
      ```groovy
      implementation 'io.github.viciousxerra:tsid-generator:1.0.0'
      ```

  * Gradle (with Kotlin DSL):
      ```kotlin
      implementation("io.github.viciousxerra:tsid-generator:1.0.0")
      ```

* Create configuration object:

    * Without shard coordinates:
      ```java
      import io.github.viciousxerra.tsidgenerator.api.SequenceOverflowHandleStrategy;
      import io.github.viciousxerra.tsidgenerator.impl.GeneratorConfiguration;

      OffsetDateTime startPoint = OffsetDateTime.parse("2026-01-01T00:00:00+00:00");
      GeneratorConfiguration configuration = new GeneratorConfiguration.Builder()
          .withStartPoint(startPoint)
      // supports at least 39 bits for timestamp
          .withTimestampBits(57)
          .withSequenceBits(6)
      // throw SequenceOverflowException on sequence overflow
          .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.THROW_EXCEPTION)
          .build();
      ```

  * With shard ID:
      ```java
      import io.github.viciousxerra.tsidgenerator.api.SequenceOverflowHandleStrategy;
      import io.github.viciousxerra.tsidgenerator.impl.GeneratorConfiguration;

      OffsetDateTime startPoint = OffsetDateTime.parse("2026-01-01T00:00:00+00:00");
      GeneratorConfiguration configuration = new GeneratorConfiguration.Builder()
          .withStartPoint(startPoint)
      // supports at least 39 bits for timestamp
          .withTimestampBits(41)
          .withShardIdBits(10)
      // optionally, by default eq 0
          .withShardId(1023)
          .withSequenceBits(12)
      // When the sequence overflows, the thread sleeps for a fixed time of 100 ms and then try again.
          .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.THREAD_FIXED_SLEEP)
          .build();
      ```

  * With data center ID and machine ID:
      ```java
      import io.github.viciousxerra.tsidgenerator.api.SequenceOverflowHandleStrategy;
      import io.github.viciousxerra.tsidgenerator.impl.GeneratorConfiguration;

      OffsetDateTime startPoint = OffsetDateTime.parse("2026-01-01T00:00:00+00:00");
      GeneratorConfiguration configuration = new GeneratorConfiguration.Builder()
          .withStartPoint(startPoint)
      // supports at least 39 bits for timestamp
          .withTimestampBits(41)
          .withDataCenterIdBits(5)
      // optionally, by default eq 0
          .withDataCenterId(31)
          .withMachineIdBits(5)
      // optionally, by default eq 0
          .withMachineId(31)
          .withSequenceBits(12)
      // When the sequence overflows, the thread spins in active loop while waiting next millisecond.
          .withSequenceOverflowStrategy(SequenceOverflowHandleStrategy.SPIN_ON_WAIT)
          .build();
      ```

* Pass this configuration object as argument to factory constructor parameter and create generator object:

    ```java
    import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueIdGenerator;
    import io.github.viciousxerra.tsidgenerator.impl.DefaultTimeSortedUniqueIdGeneratorFactory;
  
    DefaultTimeSortedUniqueIdGeneratorFactory factory = new DefaultTimeSortedUniqueIdGeneratorFactory(configuration);
    TimeSortedUniqueIdGenerator generator = factory.create();
    ```

* Or simply create generator from SnowflakeIdGeneratorFactory preset.

    ```java
    import io.github.viciousxerra.tsidgenerator.api.SequenceOverflowHandleStrategy;
    import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueIdGenerator;
    import io.github.viciousxerra.tsidgenerator.impl.DefaultTimeSortedUniqueIdGeneratorFactory;
    import io.github.viciousxerra.tsidgenerator.impl.SnowflakeIdGeneratorFactory;
  
    OffsetDateTime startPoint = OffsetDateTime.parse("2026-01-01T00:00:00+00:00");
    int shardId = 12;
    DefaultTimeSortedUniqueIdGeneratorFactory factory = new SnowflakeIdGeneratorFactory(
                            startPoint, shardId, SequenceOverflowHandleStrategy.THROW_EXCEPTION);
    TimeSortedUniqueIdGenerator generator = factory.create();
    ```

* Generate time sorted ID by calling nextId() method from non-daemon thread:

    ```java
    import io.github.viciousxerra.tsidgenerator.api.TimeSortedUniqueId;
  
    TimeSortedUniqueId id = generator.nextId();
    ```

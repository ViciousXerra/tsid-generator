package io.github.viciousxerra.tsidgenerator.api;

/**
 * A value holder containing configuration information about the sequence part of generated identifiers.
 * @param sequenceMaxValue The maximum sequence value for a generated identifier in a given millisecond.
 * @param grantedBits Number of bits allocated for sequence number.
 * @param overflowHandleStrategy Sequence overflow strategy related enumeration.
 */
public record SequenceSettings(int sequenceMaxValue, int grantedBits,
                               SequenceOverflowHandleStrategy overflowHandleStrategy) {
}

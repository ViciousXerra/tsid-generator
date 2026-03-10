package io.github.viciousxerra.tsidgenerator.impl;

final class StringTemplates {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String CURRENT_TIME_S = "Current time: %s";
    private static final String MUST_NOT_BE_NULL_TEMPLATE = "%s must not be null.";
    private static final String BIT_SIZE_EXCEEDED_TEMPLATE = "%s bit size has been exceeded.";

    static final String TIMELINE_BEFORE_START_POINT_TEMPLATE =
            "Current time is before startingPoint."
                    + LINE_SEPARATOR
                    + CURRENT_TIME_S
                    + LINE_SEPARATOR
                    + "Starting point: %s";
    static final String TIMESTAMP_OVERFLOW_TEMPLATE =
            "Timestamp limits have been exceeded."
                    + LINE_SEPARATOR
                    + CURRENT_TIME_S
                    + LINE_SEPARATOR
                    + "Limit: %s";
    static final String CLOCK_MOVE_BACKWARDS_MESSAGE = "Time has been turned back through start point. "
            + "Try again later.";
    static final String CLOCK_MOVE_BACKWARDS_MESSAGE_TEMPLATE =
            "Time has turned back. Unable to generate identifiers within %d milliseconds.";
    static final String SEQUENCE_OVERFLOW_MESSAGE = "Sequence overflow. Refused to generate.";
    static final String UNSUPPORTED_SEQUENCE_OVERFLOW_HANDLER_STRATEGY_MESSAGE =
            "Unsupported sequence overflow handle strategy.";
    static final String BIT_OVERFLOW_MESSAGE_TEMPLATE =
            "Bit overflow has been occurred."
                    + LINE_SEPARATOR
                    + "Current value: %d"
                    + LINE_SEPARATOR
                    + "Max value: %d";
    static final String MAX_LONG_BIT_SIZE_EXCEEDED = String.format(BIT_SIZE_EXCEEDED_TEMPLATE, "Long");
    static final String MAX_INT_BIT_SIZE_EXCEEDED = String.format(BIT_SIZE_EXCEEDED_TEMPLATE, "Integer");
    static final String START_POINT_NULL_MESSAGE = String.format(MUST_NOT_BE_NULL_TEMPLATE, "Start point");
    static final String TIMESTAMP_BITS_TEN_YEARS_WARRANTY_MESSAGE_TEMPLATE =
            "Timestamp granted bits must be equal or greater than %d (Guaranteed validity for 10 years).";
    static final String NUMBER_OF_SEQUENCE_GRANTED_BITS_MUST_BE_POSITIVE_MESSAGE =
            "Sequence granted bits must be positive.";
    static final String SEQUENCE_OVERFLOW_HANDLER_STRATEGY_NULL_MESSAGE =
            String.format(MUST_NOT_BE_NULL_TEMPLATE, "Sequence overflow strategy enumeration");
    static final String UNSUPPORTED_CONFIGURATION_MESSAGE =
            "Unsupported configuration."
                    + LINE_SEPARATOR
                    + "1) Use the builder without specifying shard coordinates and corresponding bit range completely "
                    + "if you don't need them."
                    + LINE_SEPARATOR
                    + "2) Use the builder with specifying non-negative shard ID (optional, 0 if not specified) and "
                    + "corresponding bit range only."
                    + LINE_SEPARATOR
                    + "3) Use the builder with specifying non-negative data center ID (optional, 0 if not specified) "
                    + "and non-negative machine ID (optional, 0 if not specified) and corresponding bit ranges only.";
    static final String GENERAL_BIT_SIZE_EXCEEDED_MESSAGE = "General bit size exceeded (maximum 63 bits)";
    static final String MUST_NOT_BE_GREATER_THAN_TEMPLATE = " must be not greater than %d";
    static final String SHARD_ID_MUST_NOT_BE_GREATER_THAN_MESSAGE_TEMPLATE = "Shard ID"
            + MUST_NOT_BE_GREATER_THAN_TEMPLATE;
    static final String DATA_CENTER_ID_MUST_NOT_BE_GREATER_THAN_MESSAGE_TEMPLATE = "Data center ID"
            + MUST_NOT_BE_GREATER_THAN_TEMPLATE;
    static final String MACHINE_ID_MUST_NOT_BE_GREATER_THAN_MESSAGE_TEMPLATE = "Machine ID"
            + MUST_NOT_BE_GREATER_THAN_TEMPLATE;

    private StringTemplates() {
    }
}

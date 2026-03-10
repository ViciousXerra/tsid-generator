package io.github.viciousxerra.tsidgenerator.impl;

import io.github.viciousxerra.tsidgenerator.api.Configuration;
import io.github.viciousxerra.tsidgenerator.api.SequenceProvider;
import io.github.viciousxerra.tsidgenerator.api.SequenceSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class SequenceProviderTest {
    private static final int SEQUENCE_MAX_VALUE = 5;
    private static final int SEQUENCE_START_VALUE = 0;

    @Mock
    private Configuration configuration;

    @Test
    @DisplayName("Test provided sequence on mocked GeneratorConfiguration")
    void testProvidedSequenceWithMock() {
        //Given
        Mockito.when(configuration.getSequenceSettings()).thenReturn(new SequenceSettings(
                SEQUENCE_MAX_VALUE,
                0,
                null
        ));
        //When
        SequenceProvider sequenceProvider = new SequenceProviderImpl(configuration);
        //Then
        for (int i = 0; i <= SEQUENCE_MAX_VALUE; i++) {
            int finalI = i;
            assertAll(
                    () -> assertThat(sequenceProvider.hasNext()).isTrue(),
                    () -> assertThat(sequenceProvider.nextSequence()).isEqualTo(finalI)
            );
        }
        assertThat(sequenceProvider.hasNext()).isFalse();
    }


    @Test
    @DisplayName("Test provided sequence reset on mocked GeneratorConfiguration")
    void testProvidedSequenceResetWithMock() {
        //Given
        Mockito.when(configuration.getSequenceSettings()).thenReturn(new SequenceSettings(
                SEQUENCE_MAX_VALUE,
                0,
                null
        ));
        //When
        SequenceProvider sequenceProvider = new SequenceProviderImpl(configuration);
        //Then
        for (int i = 0; i <= SEQUENCE_MAX_VALUE; i++) {
            sequenceProvider.nextSequence();
        }
        assertThat(sequenceProvider.hasNext()).isFalse();
        sequenceProvider.reset();
        assertAll(
                () -> assertThat(sequenceProvider.hasNext()).isTrue(),
                () -> assertThat(sequenceProvider.nextSequence()).isEqualTo(SEQUENCE_START_VALUE)
        );
    }

}

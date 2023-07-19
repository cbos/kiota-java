package com.microsoft.kiota.serialization;

import com.microsoft.kiota.PeriodAndDuration;

import java.io.Closeable;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.EnumSet;
import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Defines an interface for serialization of objects to a stream. */
public interface SerializationWriter extends Closeable {
    /**
     * Writes the specified string value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeStringValue(@Nullable final String key, @Nullable final String value);
    /**
     * Writes the specified Boolean value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeBooleanValue(@Nullable final String key, @Nullable final Boolean value);
    /**
     * Writes the specified Byte value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeByteValue(@Nullable final String key, @Nullable final Byte value);
    /**
     * Writes the specified Short value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeShortValue(@Nullable final String key, @Nullable final Short value);
    /**
     * Writes the specified BigDecimal value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeBigDecimalValue(@Nullable final String key, @Nullable final BigDecimal value);
    /**
     * Writes the specified Integer value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeIntegerValue(@Nullable final String key, @Nullable final Integer value);
    /**
     * Writes the specified Float value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeFloatValue(@Nullable final String key, @Nullable final Float value);
    /**
     * Writes the specified Double value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeDoubleValue(@Nullable final String key, @Nullable final Double value);
    /**
     * Writes the specified Long value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeLongValue(@Nullable final String key, @Nullable final Long value);
    /**
     * Writes the specified UUID value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeUUIDValue(@Nullable final String key, @Nullable final UUID value);
    /**
     * Writes the specified OffsetDateTime value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeOffsetDateTimeValue(@Nullable final String key, @Nullable final OffsetDateTime value);
    /**
     * Writes the specified LocalDate value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeLocalDateValue(@Nullable final String key, @Nullable final LocalDate value);
    /**
     * Writes the specified LocalTime value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeLocalTimeValue(@Nullable final String key, @Nullable final LocalTime value);
    /**
     * Writes the specified Period/Duration value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writePeriodAndDurationValue(@Nullable final String key, @Nullable final PeriodAndDuration value);
    /**
     * Writes the specified collection of primitive values to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param values the value to write to the stream.
     * @param <T> the type of the primitive.
     */
    <T> void writeCollectionOfPrimitiveValues(@Nullable final String key, @Nullable final Iterable<T> values);
    /**
     * Writes the specified collection of object values to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param values the value to write to the stream.
     * @param <T> the type of the object.
     */
    <T extends Parsable> void writeCollectionOfObjectValues(@Nullable final String key, @Nullable final Iterable<T> values);
    /**
     * Writes the specified collection of enum values to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param values the values to write to the stream.
     * @param <T> the type of the enum.
     */
    <T extends Enum<T>> void writeCollectionOfEnumValues(@Nullable final String key, @Nullable final Iterable<T> values);
    /**
     * Writes the specified model object value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     * @param additionalValuesToMerge the additional values to merge to the main value when serializing an intersection wrapper.
     * @param <T> the type of the model object.
     */
    <T extends Parsable> void writeObjectValue(@Nullable final String key, @Nullable final T value, @Nonnull final Parsable ...additionalValuesToMerge);
    /**
     * Gets the value of the serialized content.
     * @return the value of the serialized content.
     */
    @Nonnull
    InputStream getSerializedContent();
    /**
     * Writes the specified enum set value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param values the value to write to the stream.
     * @param <T> the type of the enum.
     */
    <T extends Enum<T>> void writeEnumSetValue(@Nullable final String key, @Nullable final EnumSet<T> values);
    /**
     * Writes the specified enum value to the stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     * @param <T> the type of the enum.
     */
    <T extends Enum<T>> void writeEnumValue(@Nullable final String key, @Nullable final T value);
    /**
     * Writes a null value for the specified key.
     * @param key the key to write the value with.
     */
    void writeNullValue(@Nullable final String key);
    /**
     * Writes the specified additional data values to the stream with an optional given key.
     * @param value the values to write to the stream.
     */
    void writeAdditionalData(@Nonnull final Map<String, Object> value);
    /**
     * Gets the callback called before the object gets serialized.
     * @return the callback called before the object gets serialized.
     */
    @Nullable
    Consumer<Parsable> getOnBeforeObjectSerialization();
    /**
     * Gets the callback called after the object gets serialized.
     * @return the callback called after the object gets serialized.
     */
    @Nullable
    Consumer<Parsable> getOnAfterObjectSerialization();
    /**
     * Gets the callback called right after the serialization process starts.
     * @return the callback called right after the serialization process starts.
     */
    @Nullable
    BiConsumer<Parsable, SerializationWriter> getOnStartObjectSerialization();
    /**
     * Sets the callback called before the objects gets serialized.
     * @param value the callback called before the objects gets serialized.
     */
    void setOnBeforeObjectSerialization(@Nullable final Consumer<Parsable> value);
    /**
     * Sets the callback called after the objects gets serialized.
     * @param value the callback called after the objects gets serialized.
     */
    void setOnAfterObjectSerialization(@Nullable final Consumer<Parsable> value);
    /**
     * Sets the callback called right after the serialization process starts.
     * @param value the callback called right after the serialization process starts.
     */
    void setOnStartObjectSerialization(@Nullable final BiConsumer<Parsable, SerializationWriter> value);
    /**
     * Writes the byte array value to the current stream with an optional given key.
     * @param key the key to write the value with.
     * @param value the value to write to the stream.
     */
    void writeByteArrayValue(@Nullable final String key, @Nonnull final byte[] value);
}
package org.gaga.oversoul.database.util;

import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;

public final class JooqConverters {

    private JooqConverters() {}

    public static long toLong(ULong value) {
        return value.longValue();
    }

    public static Long toNullableLong(ULong value) {
        return value == null ? null : value.longValue();
    }

    public static int toInt(UInteger value) {
        return value.intValue();
    }

    public static int toInt(UShort value) {
        return value.intValue();
    }

    public static Integer toNullableInt(UInteger value) {
        return value == null ? null : value.intValue();
    }

    public static Integer nullableInt(Number value) { return value == null ? null : value.intValue(); }
}
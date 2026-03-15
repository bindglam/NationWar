package io.github.bindglam.nationwar.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ObjectUtil {
    private ObjectUtil() {
    }

    public static <F, T> @Nullable T mapNullable(@Nullable F obj, Function<F, T> mapper) {
        return obj == null ? null : mapper.apply(obj);
    }

    public static <F, T> @NotNull T map(@NotNull F obj, Function<F, T> mapper) {
        return mapper.apply(obj);
    }
}

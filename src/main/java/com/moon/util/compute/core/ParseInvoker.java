package com.moon.util.compute.core;

import com.moon.lang.ref.IntAccessor;
import com.moon.lang.reflect.FieldUtil;
import com.moon.util.compute.RunnerSettings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import static com.moon.lang.SupportUtil.matchOne;
import static com.moon.lang.ThrowUtil.noInstanceError;
import static com.moon.lang.reflect.FieldUtil.getAccessibleField;
import static com.moon.lang.reflect.MethodUtil.getPublicStaticMethods;
import static com.moon.util.compute.core.Constants.*;
import static com.moon.util.compute.core.ParseUtil.nextVal;
import static java.util.Objects.requireNonNull;

/**
 * @author benshaoye
 */
final class ParseInvoker {
    private ParseInvoker() {
        noInstanceError();
    }

    private final static Predicate<Method> NONE_PARAM = m -> m.getParameterCount() == 0;

    final static AsRunner tryParseInvoker(
        char[] chars, IntAccessor indexer, int len, RunnerSettings settings, String name, AsValuer prevValuer
    ) {
        final int cache = indexer.get();
        final boolean isStatic = prevValuer instanceof DataLoader;
        if (nextVal(chars, indexer, len) == YUAN_L) {
            if (nextVal(chars, indexer, len) == YUAN_R) {
                // 无参方法调用
                return parseNoneParams(prevValuer, name, isStatic);
            } else {
                // 带有参数的方法调用
                return parseHasParams(chars, indexer.decrement(), len, settings, prevValuer, name, isStatic);
            }
        } else {
            // 静态字段检测
            indexer.set(cache);
            return tryParseStaticField(prevValuer, name, isStatic);
        }
    }

    /**
     * 带有参数的方法调用
     */
    private final static AsRunner parseHasParams(
        char[] chars, IntAccessor indexer, int len, RunnerSettings settings,
        AsValuer prev, String name, boolean isStatic
    ) {
        AsRunner[] params = ParseParams.parse(chars, indexer, len, settings);
        return params.length>1? parseMultiParamCaller(params, prev, name, isStatic)
            : parseOnlyParamCaller(params, prev, name, isStatic);
    }

    /**
     * 多参数调用的方法
     */
    private final static AsRunner parseMultiParamCaller(
        AsRunner[] params, AsValuer prev, String name, boolean isStatic
    ) {
        if (isStatic) {

        } else {

        }
        throw new UnsupportedOperationException();
    }

    /**
     * 带有一个参数的方法
     */
    private final static AsRunner parseOnlyParamCaller(
        AsRunner[] valuers, AsValuer prev, String name, boolean isStatic
    ) {
        if (isStatic) {
            // 静态方法
            Class sourceType = ((DataLoader) prev).getValue();
            return InvokeOneEnsure.of(valuers[0], sourceType, name);
        } else {
            // 成员方法
            return new InvokeOne(prev, valuers[0], name);
        }
    }

    /**
     * 无参方法调用
     */
    private final static AsRunner parseNoneParams(
        AsValuer prev, String name, boolean isStatic
    ) {
        if (isStatic) {
            // 静态方法
            return new InvokeNoneEnsure(
                matchOne(getPublicStaticMethods(
                    ((DataLoader) prev).getValue(), name), NONE_PARAM));
        } else {
            // 成员方法
            return new GetLink(prev, new InvokeNone(name));
        }
    }

    /**
     * 尝试解析静态字段，如果不是静态字段调用返回 null
     */
    private final static AsValuer tryParseStaticField(
        AsValuer prevValuer, String name, boolean isStatic
    ) {
        if (isStatic) {
            // 静态字段
            Class sourceType = ((DataLoader) prevValuer).getValue();
            Field field = requireNonNull(getAccessibleField(sourceType, name));
            return DataConst.get(FieldUtil.getValue(field, sourceType));
        }
        return null;
    }
}

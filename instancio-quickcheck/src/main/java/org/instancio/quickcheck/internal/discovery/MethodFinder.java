/*
 * Copyright 2022-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.quickcheck.internal.discovery;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.instancio.internal.util.Sonar;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;

final class MethodFinder {
    // Pattern: methodName(comma-separated list of parameter type names)
    @SuppressWarnings(Sonar.USING_SLOW_REGEX)
    private static final Pattern METHOD_PATTERN = Pattern.compile("(.+)\\((.*)\\)");

    private MethodFinder() {
    }

    static Optional<Method> findMethod(String methodSpecPart, Class<?> clazz) {
        Matcher matcher = METHOD_PATTERN.matcher(methodSpecPart);

        Preconditions.condition(matcher.matches(),
            () -> String.format("Method [%s] does not match pattern [%s]", methodSpecPart, METHOD_PATTERN));

        String methodName = matcher.group(1);
        String parameterTypeNames = matcher.group(2);

        return ReflectionUtils.findMethod(clazz, methodName, parameterTypeNames);
    }
}

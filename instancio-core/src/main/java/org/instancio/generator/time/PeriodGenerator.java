/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.generator.time;

import org.instancio.Generator;
import org.instancio.Random;
import org.instancio.generator.specs.PeriodGeneratorSpec;
import org.instancio.internal.ApiValidator;

import java.time.Period;

public class PeriodGenerator implements Generator<Period>, PeriodGeneratorSpec {

    private static final int DEFAULT_MIN_DAYS = 1;
    private static final int DEFAULT_MAX_DAYS = 365;

    private int minDays = DEFAULT_MIN_DAYS;
    private int maxDays = DEFAULT_MAX_DAYS;
    private int minMonths;
    private int maxMonths;
    private int minYears;
    private int maxYears;


    @Override
    public PeriodGeneratorSpec days(final int min, final int max) {
        ApiValidator.isTrue(min <= max, "Period days 'min' must be less than or equal 'max': days(%s, %s)", min, max);
        this.minDays = min;
        this.maxDays = max;
        return this;
    }

    @Override
    public PeriodGeneratorSpec months(final int min, final int max) {
        ApiValidator.isTrue(min <= max, "Period months 'min' must be less than or equal 'max': months(%s, %s)", min, max);
        this.minMonths = min;
        this.maxMonths = max;
        return this;
    }

    @Override
    public PeriodGeneratorSpec years(final int min, final int max) {
        ApiValidator.isTrue(min <= max, "Period years 'min' must be less than or equal 'max': years(%s, %s)", min, max);
        this.minYears = min;
        this.maxYears = max;
        return this;
    }

    @Override
    public Period generate(final Random random) {
        return Period.of(
                random.intRange(minYears, maxYears),
                random.intRange(minMonths, maxMonths),
                random.intRange(minDays, maxDays));
    }
}
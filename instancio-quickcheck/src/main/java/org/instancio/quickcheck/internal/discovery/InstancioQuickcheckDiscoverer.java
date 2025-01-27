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

import org.instancio.quickcheck.internal.discovery.predicates.IsTestClassWithProperties;
import org.instancio.quickcheck.internal.engine.InstancioQuickcheckEngineDescriptor;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver;

public class InstancioQuickcheckDiscoverer {
    private static final EngineDiscoveryRequestResolver<InstancioQuickcheckEngineDescriptor> RESOLVER = EngineDiscoveryRequestResolver.<InstancioQuickcheckEngineDescriptor>builder()
        .addClassContainerSelectorResolver(new IsTestClassWithProperties())
        .addSelectorResolver(context -> new ClassSelectorResolver(context.getClassNameFilter()))
        .addSelectorResolver(context -> new MethodSelectorResolver())
        .addTestDescriptorVisitor(context -> TestDescriptor::prune)
        .build();

    public void resolveSelectors(EngineDiscoveryRequest request, InstancioQuickcheckEngineDescriptor engineDescriptor) {
        RESOLVER.resolve(request, engineDescriptor);
    }
}

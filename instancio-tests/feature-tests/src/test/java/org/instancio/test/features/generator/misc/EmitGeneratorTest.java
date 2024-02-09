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
package org.instancio.test.features.generator.misc;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

@FeatureTag({Feature.GENERATOR, Feature.EMIT_GENERATOR})
@ExtendWith(InstancioExtension.class)
class EmitGeneratorTest {

    private static final int SIZE = 10;

    @WithSettings
    private final Settings settings = Settings.create()
            // ensure enough room for all items
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    @Test
    void multipleItemsInvocations() {
        final List<Integer> result = Instancio.ofList(Integer.class)
                .size(12)
                .generate(all(Integer.class), gen -> gen.emit()
                        .items(-1, -2, -3)
                        .items(-4)
                        .item(-5, 1)
                        .item(-6, 2)
                        .items(Arrays.asList(-7, -7, -7))
                        .items(Arrays.asList(-8, -9)))
                .create();

        assertThat(result).containsExactly(-1, -2, -3, -4, -5, -6, -6, -7, -7, -7, -8, -9);
    }

    @Test
    void emitSubtypes() {
        final List<Number> result = Instancio.ofList(Number.class)
                .size(5)
                .generate(all(Number.class), gen -> gen.emit()
                        .item((byte) 1, 1)
                        .items(2, 3L)
                        .items(Arrays.asList(4f, 5d)))
                .create();

        assertThat(result).containsExactly((byte) 1, 2, 3L, 4f, 5d);
    }

    @Test
    void ignoreUnusedItems() {
        final List<Integer> result = Instancio.ofList(Integer.class)
                .size(1)
                .generate(all(Integer.class), gen -> gen.emit().items(-1, -2, -3).ignoreUnused())
                .create();

        // left over values [-2, -3] ignored
        assertThat(result).containsOnly(-1);
    }

    @Test
    void emitNullCollectionElements() {
        final int size = 5;
        final List<Integer> result = Instancio.ofList(Integer.class)
                .size(size)
                .generate(all(Integer.class), gen -> gen.emit().item(null, size))
                .create();

        assertThat(result).hasSize(size).containsOnlyNulls();
    }

    @Test
    void emitNullArrayElements() {
        final int size = 5;
        final Integer[] result = Instancio.of(Integer[].class)
                .generate(root(), gen -> gen.array().length(size))
                .generate(all(Integer.class), gen -> gen.emit().item(null, size))
                .create();

        assertThat(result).hasSize(size).containsOnlyNulls();
    }

    @Test
    void whenEmptyEmitNullWithCollection() {
        final int size = 5;
        final List<Integer> result = Instancio.ofList(Integer.class)
                .size(size)
                .generate(all(Integer.class), gen -> gen.emit().items(1).whenEmptyEmitNull())
                .create();

        assertThat(result).containsOnly(1, null);
    }

    @Test
    void whenEmptyEmitNullWithArray() {
        final int size = 5;
        final Integer[] result = Instancio.of(Integer[].class)
                .generate(root(), gen -> gen.array().length(size))
                .generate(all(Integer.class), gen -> gen.emit().items(1).whenEmptyEmitNull())
                .create();

        assertThat(result).containsOnly(1, null);
    }

    @Test
    void emitNullMapValues() {
        final int size = 5;
        final Map<String, Integer> result = Instancio.ofMap(String.class, Integer.class)
                .size(size)
                .generate(all(Integer.class), gen -> gen.emit().item(null, size))
                .create();

        assertThat(result.values()).hasSize(size).containsOnlyNulls();
    }

    @Test
    @FeatureTag(Feature.UNSUPPORTED)
    @DisplayName("emit() null is not supported for map keys")
    void emitNullMapKey() {
        final int size = 5;
        final Map<Integer, String> result = Instancio.ofMap(Integer.class, String.class)
                .size(size)
                .generate(all(Integer.class), gen -> gen.emit().item(null, size))
                .create();

        assertThat(result.keySet()).hasSize(size).doesNotContainNull();
    }

    @Test
    void emitNull() {
        final int size = 5;
        final List<IntegerHolder> result = Instancio.ofList(IntegerHolder.class)
                .size(size)
                .generate(all(Integer.class), gen -> gen.emit().item(null, size))
                .create();

        assertThat(result).hasSize(size).allMatch(res -> res.getWrapper() == null);
    }

    @Test
    void shuffle() {
        final List<Integer> values = IntStream.rangeClosed(0, 100)
                .boxed().collect(Collectors.toList());

        final List<Integer> result = Instancio.ofList(Integer.class)
                .size(values.size())
                .generate(all(Integer.class), gen -> gen.emit().items(values).shuffle())
                .create();

        assertThat(result)
                .hasSameSizeAs(values)
                .isNotEqualTo(values);
    }

    /**
     * This test randomly fails,
     * It seems like that at the depth 2 there are actually 4 nodes but the Queue
     */
    @Test
    @Seed(3095387440446971337L) // test fails
//    @Seed(3486230662535349159L) //test passes
    void emit2ValuesForEachParentNode() {
        RootNode rootNode = Instancio.of(RootNode.class)
                .generate(field(RootNode::getDepth1Nodes), gen -> gen.collection().size(2))
                .generate(field(RootNode.Depth1Node::getIsDeleted), gen -> gen.emit().items(true, false))
                .generate(field(RootNode.Depth1Node::getDepth2Nodes), gen -> gen.collection().size(2))
                .generate(field(RootNode.Depth1Node.Depth2Node::getIsDeleted), gen -> gen.emit().items(true, false))
                .verbose()
                .create();
        assertThat(rootNode.depth1Nodes).extracting(RootNode.Depth1Node::getIsDeleted).containsExactly(true, false);
        assertThat(rootNode.depth1Nodes.stream().flatMap(d -> d.getDepth2Nodes().stream()))
                .extracting(RootNode.Depth1Node.Depth2Node::getIsDeleted).containsExactly(true, false, true, false);

    }

    @Data
    static class RootNode {

        List<Depth1Node> depth1Nodes;

        @Data
        static class Depth1Node {
            List<Depth2Node> depth2Nodes;
            Boolean isDeleted;

            @Data
            static class Depth2Node {
                Boolean isDeleted;
            }
        }
    }
}

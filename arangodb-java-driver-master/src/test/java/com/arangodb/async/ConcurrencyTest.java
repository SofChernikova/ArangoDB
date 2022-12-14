/*
 * DISCLAIMER
 *
 * Copyright 2016 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.arangodb.async;


import com.arangodb.async.internal.ArangoExecutorAsync;
import com.arangodb.entity.ArangoDBVersion;
import com.arangodb.mapping.ArangoJack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * @author Michele Rastelli
 */
class ConcurrencyTest {

    private ArangoDBAsync arangoDB;

    @BeforeEach
    void initialize() {
        arangoDB = new ArangoDBAsync.Builder().serializer(new ArangoJack()).build();
    }

    /**
     * FIXME:   make the user executor configurable in com.arangodb.internal.ArangoExecutorAsync::execute
     * (eg. this test passes using a CachedThreadPool)
     */
    @Disabled
    @Test
    @Timeout(2)
    void executorLimit() {
        List<CompletableFuture<ArangoDBVersion>> futures = IntStream.range(0, 20)
                .mapToObj(i -> arangoDB.getVersion()
                        .whenComplete((dbVersion, ex) -> {
                            System.out.println(Thread.currentThread().getName());
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }))
                .collect(Collectors.toList());

        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                fail();
            }
        });
    }


    /**
     * outgoing requests should be queued in the {@link ArangoExecutorAsync} outgoingExecutor
     */
    @Disabled
    @Test
    @Timeout(1)
    void outgoingRequestsParallelismTest() {
        for (int i = 0; i < 50_000; i++) {
            arangoDB.getVersion();
        }
    }
}

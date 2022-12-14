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

import com.arangodb.ArangoDBException;
import com.arangodb.entity.ArangoDBEngine;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.StreamTransactionEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.model.StreamTransactionOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * @author Michele Rastelli
 */
class StreamTransactionConflictsTest extends BaseTest {

    private static final String COLLECTION_NAME = "db_concurrent_stream_transactions_test";

    public StreamTransactionConflictsTest() throws ExecutionException, InterruptedException {
        if (db.collection(COLLECTION_NAME).exists().get())
            db.collection(COLLECTION_NAME).drop().get();

        db.createCollection(COLLECTION_NAME, null).get();
    }

    @AfterEach
    void teardown() throws ExecutionException, InterruptedException {
        if (db.collection(COLLECTION_NAME).exists().get())
            db.collection(COLLECTION_NAME).drop().get();
    }

    @Test
    void conflictOnInsertDocumentWithNotYetCommittedTx() throws ExecutionException, InterruptedException {
        assumeTrue(isAtLeastVersion(3, 5));
        assumeTrue(isStorageEngine(ArangoDBEngine.StorageEngineName.rocksdb));

        StreamTransactionEntity tx1 = db.beginStreamTransaction(
                new StreamTransactionOptions().readCollections(COLLECTION_NAME).writeCollections(COLLECTION_NAME)).get();

        StreamTransactionEntity tx2 = db.beginStreamTransaction(
                new StreamTransactionOptions().readCollections(COLLECTION_NAME).writeCollections(COLLECTION_NAME)).get();

        String key = UUID.randomUUID().toString();

        // insert a document from within tx1
        db.collection(COLLECTION_NAME)
                .insertDocument(new BaseDocument(key), new DocumentCreateOptions().streamTransactionId(tx1.getId())).get();

        try {
            // insert conflicting document from within tx2
            db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(key),
                    new DocumentCreateOptions().streamTransactionId(tx2.getId())).get();
            fail();
        } catch (ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(ArangoDBException.class);
        }

        db.abortStreamTransaction(tx1.getId()).get();
        db.abortStreamTransaction(tx2.getId()).get();
    }

    @Test
    void conflictOnInsertDocumentWithAlreadyCommittedTx() throws ExecutionException, InterruptedException {
        assumeTrue(isAtLeastVersion(3, 5));
        assumeTrue(isStorageEngine(ArangoDBEngine.StorageEngineName.rocksdb));

        StreamTransactionEntity tx1 = db.beginStreamTransaction(
                new StreamTransactionOptions().readCollections(COLLECTION_NAME).writeCollections(COLLECTION_NAME)).get();

        StreamTransactionEntity tx2 = db.beginStreamTransaction(
                new StreamTransactionOptions().readCollections(COLLECTION_NAME).writeCollections(COLLECTION_NAME)).get();

        String key = UUID.randomUUID().toString();

        // insert a document from within tx1
        db.collection(COLLECTION_NAME)
                .insertDocument(new BaseDocument(key), new DocumentCreateOptions().streamTransactionId(tx1.getId())).get();

        // commit tx1
        db.commitStreamTransaction(tx1.getId()).get();

        try {
            // insert conflicting document from within tx2
            db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(key),
                    new DocumentCreateOptions().streamTransactionId(tx2.getId())).get();
            fail();
        } catch (ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(ArangoDBException.class);
        }

        db.abortStreamTransaction(tx2.getId()).get();
    }
}

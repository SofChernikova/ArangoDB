/*
 * DISCLAIMER
 *
 * Copyright 2018 ArangoDB GmbH, Cologne, Germany
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
import com.arangodb.entity.BaseDocument;
import com.arangodb.internal.ArangoRequestParam;
import org.junit.jupiter.api.Test;


import java.util.concurrent.ExecutionException;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Mark Vollmary
 */
class ArangoRouteTest extends BaseTest {

  /*
	@Test
	void get() throws InterruptedException, ExecutionException {
		final Response res = db.route("/_api/version").get().get();
		assertThat(res.getBody().get("version").isString()).isEqualTo(true);
	}*/

  /*
	@Test
	void withHeader() throws InterruptedException, ExecutionException {
		final ArangoCollectionAsync collection = db.collection("route-test-col");
		try {
			collection.create();
			final BaseDocument doc = new BaseDocument();
			collection.insertDocument(doc).get();
			db.route("/_api/document", doc.getId()).withHeader(ArangoRequestParam.IF_NONE_MATCH, doc.getRevision())
					.get().get();
			fail();
		} catch (final ExecutionException e) {
			assertThat(e.getCause() instanceof ArangoDBException).isEqualTo(true);
		} finally {
			collection.drop();
		}
	}
  */

    @Test
    void withParentHeader() throws InterruptedException, ExecutionException {
        final ArangoCollectionAsync collection = db.collection("route-test-col");
        try {
            collection.create().get();
            final BaseDocument doc = new BaseDocument();
            collection.insertDocument(doc).get();
            db.route("/_api/document").withHeader(ArangoRequestParam.IF_NONE_MATCH, doc.getRevision())
                    .route(doc.getId()).get().get();
            fail();
        } catch (final ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(ArangoDBException.class);
        }
        collection.drop().get();
    }

}

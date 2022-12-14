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

package com.arangodb.entity;

import java.util.Collection;

/**
 * @author Mark Vollmary
 * @see <a href="https://www.arangodb.com/docs/stable/http/gharial-management.html#create-a-graph">API Documentation</a>
 */
public class GraphEntity implements Entity {

    private String name;
    /**
     * Special case where {@code _key} is used instead of {@code name}.
     */
    private String _key;
    private Collection<EdgeDefinition> edgeDefinitions;
    private Collection<String> orphanCollections;
    private Boolean isSmart;
    private Boolean isDisjoint;
    private Integer numberOfShards;
    private String smartGraphAttribute;
    private ReplicationFactor replicationFactor;
    private Integer minReplicationFactor;
    private Integer writeConcern;

    public String getName() {
        return name != null ? name : _key;
    }

    public Collection<EdgeDefinition> getEdgeDefinitions() {
        return edgeDefinitions;
    }

    public Collection<String> getOrphanCollections() {
        return orphanCollections;
    }

    public Boolean getIsSmart() {
        return isSmart;
    }

    public Boolean getIsDisjoint() {
        return isDisjoint;
    }

    public Integer getNumberOfShards() {
        return numberOfShards;
    }

    public Integer getReplicationFactor() {
        return replicationFactor.getReplicationFactor();
    }

    public Boolean getSatellite() {
        return this.replicationFactor.getSatellite();
    }

    /**
     * @deprecated use {@link #getWriteConcern()} instead
     */
    @Deprecated
    public Integer getMinReplicationFactor() {
        return minReplicationFactor;
    }

    public Integer getWriteConcern() {
        return writeConcern;
    }

    public String getSmartGraphAttribute() {
        return smartGraphAttribute;
    }

}

/*
 * Copyright (c) 2016 Uber Technologies, Inc. (hoodie-dev-group@uber.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uber.hoodie.config;

import com.google.common.base.Preconditions;
import com.uber.hoodie.index.HoodieIndex;

import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Indexing related config
 */
@Immutable
public class HoodieIndexConfig extends DefaultHoodieConfig {
    public static final String INDEX_TYPE_PROP = "hoodie.index.type";
    public static final String DEFAULT_INDEX_TYPE = HoodieIndex.IndexType.BLOOM.name();
    public static final String BLOOM_FILTER_NUM_ENTRIES = "hoodie.index.bloom.num_entries";
    public static final String DEFAULT_BLOOM_FILTER_NUM_ENTRIES = "60000";
    public static final String BLOOM_FILTER_FPP = "hoodie.index.bloom.fpp";
    public static final String DEFAULT_BLOOM_FILTER_FPP = "0.000000001";
    public final static String HBASE_ZKQUORUM_PROP = "hoodie.index.hbase.zkquorum";
    public final static String HBASE_ZKPORT_PROP = "hoodie.index.hbase.zkport";
    public final static String HBASE_TABLENAME_PROP = "hoodie.index.hbase.table";

    private HoodieIndexConfig(Properties props) {
        super(props);
    }

    public static HoodieIndexConfig.Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final Properties props = new Properties();

        public Builder fromFile(File propertiesFile) throws IOException {
            FileReader reader = new FileReader(propertiesFile);
            try {
                this.props.load(reader);
                return this;
            } finally {
                reader.close();
            }
        }

        public Builder withIndexType(HoodieIndex.IndexType indexType) {
            props.setProperty(INDEX_TYPE_PROP, indexType.name());
            return this;
        }

        public Builder bloomFilterNumEntries(int numEntries) {
            props.setProperty(BLOOM_FILTER_NUM_ENTRIES, String.valueOf(numEntries));
            return this;
        }

        public Builder bloomFilterFPP(double fpp) {
            props.setProperty(BLOOM_FILTER_FPP, String.valueOf(fpp));
            return this;
        }

        public Builder hbaseZkQuorum(String zkString) {
            props.setProperty(HBASE_ZKQUORUM_PROP, zkString);
            return this;
        }

        public Builder hbaseZkPort(int port) {
            props.setProperty(HBASE_ZKPORT_PROP, String.valueOf(port));
            return this;
        }

        public Builder hbaseTableName(String tableName) {
            props.setProperty(HBASE_TABLENAME_PROP, tableName);
            return this;
        }

        public HoodieIndexConfig build() {
            HoodieIndexConfig config = new HoodieIndexConfig(props);
            setDefaultOnCondition(props, !props.containsKey(INDEX_TYPE_PROP),
                INDEX_TYPE_PROP, DEFAULT_INDEX_TYPE);
            setDefaultOnCondition(props, !props.containsKey(BLOOM_FILTER_NUM_ENTRIES),
                BLOOM_FILTER_NUM_ENTRIES, DEFAULT_BLOOM_FILTER_NUM_ENTRIES);
            setDefaultOnCondition(props, !props.containsKey(BLOOM_FILTER_FPP),
                BLOOM_FILTER_FPP, DEFAULT_BLOOM_FILTER_FPP);
            // Throws IllegalArgumentException if the value set is not a known Hoodie Index Type
            HoodieIndex.IndexType.valueOf(props.getProperty(INDEX_TYPE_PROP));
            return config;
        }
    }
}

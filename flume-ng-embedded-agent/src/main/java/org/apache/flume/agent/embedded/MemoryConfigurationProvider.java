/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.agent.embedded;

import java.util.Map;

import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;
import org.apache.flume.conf.FlumeConfiguration;
import org.apache.flume.node.AbstractConfigurationProvider;

/**
 * MemoryConfigurationProvider is the simplest possible
 * AbstractConfigurationProvider simply turning a give properties file and
 * agent name into a FlumeConfiguration object.
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
class MemoryConfigurationProvider extends AbstractConfigurationProvider {
  private final Map<String, String> properties;

  MemoryConfigurationProvider(String name, Map<String, String> properties) {
    super(name);
    this.properties = properties;
  }

  @Override
  protected FlumeConfiguration getFlumeConfiguration() {
    return new FlumeConfiguration(properties);
  }

}

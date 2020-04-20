/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This class is about package attribute that captures
 * version info of Flume that was compiled.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface VersionAnnotation {

  /**
   * Get the Flume version
   * @return the version string "1.1"
   */
  String version();

  /**
   * Get the subversion revision.
   * @return the revision number as a string (eg. "100755")
   */
  String revision();

  /**
   * Get the branch from which this was compiled.
   * @return The branch name, e.g. "trunk"
   */
  String branch();

  /**
   * Get the username that compiled Flume.
   */
  String user();

  /**
   * Get the date when Flume was compiled.
   * @return the date in unix 'date' format
   */
  String date();

  /**
   * Get the url for the subversion repository.
   */
  String url();

  /**
   * Get a checksum of the source files from which
   * Flume was compiled.
   * @return a string that uniquely identifies the source
   **/
  String srcChecksum();

}

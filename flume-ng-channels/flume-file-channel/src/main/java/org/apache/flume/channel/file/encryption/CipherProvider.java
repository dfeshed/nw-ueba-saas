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
package org.apache.flume.channel.file.encryption;

import java.security.Key;

import com.google.common.base.Preconditions;

public abstract class CipherProvider {

  public abstract Encryptor.Builder<?> newEncryptorBuilder();
  public abstract Decryptor.Builder<?> newDecryptorBuilder();

  public abstract static class Encryptor {

    public abstract byte[] encrypt(byte[] clearText);
    public abstract byte[] getParameters();
    public abstract String getCodec();

    /** Builder implementations MUST have a no-arg constructor */
    public abstract static class Builder<T extends Encryptor> {
      protected Key key;

      public Builder<T> setKey(Key key) {
        this.key = Preconditions.checkNotNull(key, "key cannot be null");
        return this;
      }

      public abstract T build();
    }
  }

  public abstract static class Decryptor {
    public abstract byte[] decrypt(byte[] cipherText);
    public abstract String getCodec();

    /** Builder implementations MUST have a no-arg constructor */
    public abstract static class Builder<T extends Decryptor> {
      protected byte[] parameters;
      protected Key key;

      public Builder<T> setKey(Key key) {
        this.key = Preconditions.checkNotNull(key, "key cannot be null");
        return this;
      }

      public Builder<T> setParameters(byte[] parameters) {
        this.parameters = parameters;
        return this;
      }

      public abstract T build();
    }
  }
}

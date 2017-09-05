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

package org.apache.flume.channel.file;

import junit.framework.Assert;
import org.apache.flume.Event;

public class TestEventUtils {

    //@Test
    public void testPutEvent() {
        FlumeEvent event = new FlumeEvent(null, new byte[5]);
        Put put = new Put(1L, 1L, event);
        Event returnEvent = EventUtils.getEventFromTransactionEvent(put);
        Assert.assertNotNull(returnEvent);
        Assert.assertEquals(5, returnEvent.getBody().length);
    }

    //@Test
    public void testInvalidEvent() {
        Take take = new Take(1L, 1L);
        Event returnEvent = EventUtils.getEventFromTransactionEvent(take);
        Assert.assertNull(returnEvent);
    }

}

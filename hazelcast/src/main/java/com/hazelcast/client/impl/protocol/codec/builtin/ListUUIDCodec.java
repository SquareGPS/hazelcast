/*
 * Copyright (c) 2008-2017, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.client.impl.protocol.codec.builtin;

import com.hazelcast.client.impl.protocol.ClientMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.UUID;

public class ListUUIDCodec {

    public static void encode(ClientMessage clientMessage, Collection<UUID> collection) {
        int itemCount = collection.size();
        ClientMessage.Frame frame = new ClientMessage.Frame(new byte[itemCount * FixedSizeTypesCodec.UUID_SIZE_IN_BYTES]);
        Iterator<UUID> iterator = collection.iterator();
        for (int i = 0; i < itemCount; i++) {
            FixedSizeTypesCodec.encodeUUID(frame.content, i * FixedSizeTypesCodec.UUID_SIZE_IN_BYTES, iterator.next());
        }
        clientMessage.addFrame(frame);
    }

    public static List<UUID> decode(ListIterator<ClientMessage.Frame> iterator) {
        return decode(iterator.next());
    }

    public static List<UUID> decode(ClientMessage.Frame frame) {
        int itemCount = frame.content.length / FixedSizeTypesCodec.UUID_SIZE_IN_BYTES;
        List<UUID> result = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            FixedSizeTypesCodec.decodeUUID(frame.content, i * FixedSizeTypesCodec.UUID_SIZE_IN_BYTES);
        }
        return result;
    }
}

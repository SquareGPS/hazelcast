/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hazelcast.client.impl.protocol.ClientMessage.NULL_FRAME;

public class CodecUtil {

    public static void fastForwardToEndFrame(ListIterator<ClientMessage.Frame> iterator){
        for (ClientMessage.Frame frame = iterator.next(); !frame.isDataStructureEndFrame(); frame = iterator.next()){
            //ignore frame content
        }
    }

    public static <T> void encodeNullable(ClientMessage clientMessage, T value, BiConsumer<ClientMessage, T> encode) {
        if(value == null) {
            clientMessage.addFrame(NULL_FRAME);
        } else {
            encode.accept(clientMessage, value);
        }
    }

//    public static <T> T decodeNullable(ClientMessage.Frame frame, Function<ClientMessage.Frame, T> decode) {
//        return frame.isNullFrame() ? null: decode.apply(frame);
//    }

    public static <T> T decodeNullable(ListIterator<ClientMessage.Frame> iterator, Function<ListIterator<ClientMessage.Frame>, T> decode) {
        return nextFrameIsNullEndFrame(iterator) ? null: decode.apply(iterator);
    }

    public static boolean nextFrameIsDataStructureEndFrame(ListIterator<ClientMessage.Frame> iterator) {
        try {
            return iterator.next().isDataStructureEndFrame();
        } finally {
            iterator.previous();
        }
    }

    public static boolean nextFrameIsNullEndFrame(ListIterator<ClientMessage.Frame> iterator) {
        try {
            return iterator.next().isNullFrame();
        } finally {
            iterator.previous();
        }
    }

}

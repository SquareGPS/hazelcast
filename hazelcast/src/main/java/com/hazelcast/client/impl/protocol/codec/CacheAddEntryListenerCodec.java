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

package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.builtin.*;

import java.util.ListIterator;

import static com.hazelcast.client.impl.protocol.ClientMessage.*;
import static com.hazelcast.client.impl.protocol.codec.builtin.FixedSizeTypesCodec.*;
import com.hazelcast.logging.Logger;

/**
 * TODO DOC
 */
public class CacheAddEntryListenerCodec {

        private static final int REQUEST_LOCAL_ONLY_FIELD_OFFSET = CORRELATION_ID_FIELD_OFFSET + BOOLEAN_SIZE_IN_BYTES;
        private static final int REQUEST_INITIAL_FRAME_SIZE = REQUEST_LOCAL_ONLY_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
        public static final int REQUEST_MESSAGE_TYPE = 5377;//hex: 0x1501,
        private static final int RESPONSE_INITIAL_FRAME_SIZE = CORRELATION_ID_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
        public static final int RESPONSE_MESSAGE_TYPE = 104;//hex: 0x0068,
        private static final int EVENT_CACHE_TYPE_FIELD_OFFSET = CORRELATION_ID_FIELD_OFFSET + INT_SIZE_IN_BYTES;
        private static final int EVENT_CACHE_COMPLETION_ID_FIELD_OFFSET = EVENT_CACHE_TYPE_FIELD_OFFSET + INT_SIZE_IN_BYTES;
        private static final int EVENT_CACHE_INITIAL_FRAME_SIZE = EVENT_CACHE_COMPLETION_ID_FIELD_OFFSET + INT_SIZE_IN_BYTES;
        public static final int EVENT_CACHE_MESSAGE_TYPE = 104;//hex: 0x00D2,

    public static class RequestParameters {

        /**
         * Name of the cache.
         */
        public java.lang.String name;

        /**
         * if true fires events that originated from this node only, otherwise fires all events
         */
        public boolean localOnly;
    }

    public static ClientMessage encodeRequest(java.lang.String name, boolean localOnly) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.AddEntryListener");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        encodeBoolean(initialFrame.content, REQUEST_LOCAL_ONLY_FIELD_OFFSET, localOnly);
        clientMessage.addFrame(initialFrame);
        StringCodec.encode(clientMessage, name);
        return clientMessage;
    }

    public static CacheAddEntryListenerCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.iterator();
        RequestParameters request = new RequestParameters();
        ClientMessage.Frame initialFrame = iterator.next();
        request.localOnly = decodeBoolean(initialFrame.content, REQUEST_LOCAL_ONLY_FIELD_OFFSET);
        request.name = StringCodec.decode(iterator);
        return request;
    }

    public static class ResponseParameters {

        /**
         * TODO DOC
         */
        public java.lang.String response;
    }

    public static ClientMessage encodeResponse(java.lang.String response) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[RESPONSE_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, RESPONSE_MESSAGE_TYPE);
        clientMessage.addFrame(initialFrame);

        StringCodec.encode(clientMessage, response);
        return clientMessage;
    }

    public static CacheAddEntryListenerCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.iterator();
        ResponseParameters response = new ResponseParameters();
        iterator.next();//empty initial frame
        response.response = StringCodec.decode(iterator);
        return response;
    }

    public static ClientMessage encodeCacheEvent(int type, java.util.Collection<com.hazelcast.cache.impl.CacheEventData> keys, int completionId) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[EVENT_CACHE_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        initialFrame.flags |= ClientMessage.IS_EVENT;
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, EVENT_CACHE_MESSAGE_TYPE);
        encodeInt(initialFrame.content, EVENT_CACHE_TYPE_FIELD_OFFSET, type);
        encodeInt(initialFrame.content, EVENT_CACHE_COMPLETION_ID_FIELD_OFFSET, completionId);
        clientMessage.addFrame(initialFrame);
        ListMultiFrameCodec.encode(clientMessage, keys, CacheEventDataCodec::encode);
        return clientMessage;
    }

    public abstract static class AbstractEventHandler {

        public void handle(ClientMessage clientMessage) {
            int messageType = clientMessage.getMessageType();
            ListIterator<ClientMessage.Frame> iterator = clientMessage.iterator();
            ClientMessage.Frame frame;
            if (messageType == EVENT_CACHE_MESSAGE_TYPE) {
                frame = iterator.next();
                int type = decodeInt(frame.content, EVENT_CACHE_TYPE_FIELD_OFFSET);
                int completionId = decodeInt(frame.content, EVENT_CACHE_COMPLETION_ID_FIELD_OFFSET);
                java.util.List<com.hazelcast.cache.impl.CacheEventData> keys = ListMultiFrameCodec.decode(iterator, CacheEventDataCodec::decode);
                handleCacheEvent(type, keys, completionId);
                return;
            }
            Logger.getLogger(super.getClass()).finest("Unknown message type received on event handler :" + messageType);
        }
        public abstract void handleCacheEvent(int type, java.util.Collection<com.hazelcast.cache.impl.CacheEventData> keys, int completionId);
    }
}
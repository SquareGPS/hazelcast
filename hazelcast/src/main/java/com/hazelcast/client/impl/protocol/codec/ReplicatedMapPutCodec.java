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

/**
 * Associates a given value to the specified key and replicates it to the cluster. If there is an old value, it will
 * be replaced by the specified one and returned from the call. In addition, you have to specify a ttl and its TimeUnit
 * to define when the value is outdated and thus should be removed from the replicated map.
 */
public class ReplicatedMapPutCodec {

        private static final int REQUEST_TTL_FIELD_OFFSET = CORRELATION_ID_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
        private static final int REQUEST_INITIAL_FRAME_SIZE = REQUEST_TTL_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
        public static final int REQUEST_MESSAGE_TYPE = 3585;//hex: 0x0E01,
        private static final int RESPONSE_INITIAL_FRAME_SIZE = CORRELATION_ID_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
        public static final int RESPONSE_MESSAGE_TYPE = 105;//hex: 0x0069,

    public static class RequestParameters {

        /**
         * Name of the ReplicatedMap
         */
        public java.lang.String name;

        /**
         * Key with which the specified value is to be associated.
         */
        public com.hazelcast.nio.serialization.Data key;

        /**
         * Value to be associated with the specified key
         */
        public com.hazelcast.nio.serialization.Data value;

        /**
         * ttl in milliseconds to be associated with the specified key-value pair
         */
        public long ttl;
    }

    public static ClientMessage encodeRequest(java.lang.String name, com.hazelcast.nio.serialization.Data key, com.hazelcast.nio.serialization.Data value, long ttl) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ReplicatedMap.Put");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        encodeLong(initialFrame.content, REQUEST_TTL_FIELD_OFFSET, ttl);
        clientMessage.addFrame(initialFrame);
        StringCodec.encode(clientMessage, name);
        DataCodec.encode(clientMessage, key);
        DataCodec.encode(clientMessage, value);
        return clientMessage;
    }

    public static ReplicatedMapPutCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.iterator();
        RequestParameters request = new RequestParameters();
        ClientMessage.Frame initialFrame = iterator.next();
        request.ttl = decodeLong(initialFrame.content, REQUEST_TTL_FIELD_OFFSET);
        request.name = StringCodec.decode(iterator);
        request.key = DataCodec.decode(iterator);
        request.value = DataCodec.decode(iterator);
        return request;
    }

    public static class ResponseParameters {

        /**
         * TODO DOC
         */
        public com.hazelcast.nio.serialization.Data response;
    }

    public static ClientMessage encodeResponse(com.hazelcast.nio.serialization.Data response) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[RESPONSE_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, RESPONSE_MESSAGE_TYPE);
        clientMessage.addFrame(initialFrame);

        CodecUtil.encodeNullable(clientMessage, response, DataCodec::encode);
        return clientMessage;
    }

    public static ReplicatedMapPutCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.iterator();
        ResponseParameters response = new ResponseParameters();
        iterator.next();//empty initial frame
        response.response = CodecUtil.decodeNullable(iterator, DataCodec::decode);
        return response;
    }

}
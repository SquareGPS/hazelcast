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
 * Fetches invalidation metadata from partitions of map.
 */
public class CacheFetchNearCacheInvalidationMetadataCodec {

        private static final int REQUEST_INITIAL_FRAME_SIZE = CORRELATION_ID_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
        public static final int REQUEST_MESSAGE_TYPE = 5407;//hex: 0x151F,
        private static final int RESPONSE_INITIAL_FRAME_SIZE = CORRELATION_ID_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
        public static final int RESPONSE_MESSAGE_TYPE = 122;//hex: 0x007A,

    public static class RequestParameters {

        /**
         * names of the caches
         */
        public java.util.List<java.lang.String> names;

        /**
         * TODO DOC
         */
        public com.hazelcast.nio.Address address;
    }

    public static ClientMessage encodeRequest(java.util.Collection<java.lang.String> names, com.hazelcast.nio.Address address) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.FetchNearCacheInvalidationMetadata");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        clientMessage.addFrame(initialFrame);
        ListMultiFrameCodec.encode(clientMessage, names, StringCodec::encode);
        AddressCodec.encode(clientMessage, address);
        return clientMessage;
    }

    public static CacheFetchNearCacheInvalidationMetadataCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.iterator();
        RequestParameters request = new RequestParameters();
        iterator.next();//empty initial frame
        request.names = ListMultiFrameCodec.decode(iterator, StringCodec::decode);
        request.address = AddressCodec.decode(iterator);
        return request;
    }

    public static class ResponseParameters {

        /**
         * TODO DOC
         */
        public java.util.List<java.util.Map.Entry<java.lang.String, java.util.List<java.util.Map.Entry<java.lang.Integer, java.lang.Long>>>> namePartitionSequenceList;

        /**
         * TODO DOC
         */
        public java.util.List<java.util.Map.Entry<java.lang.Integer, java.util.UUID>> partitionUuidList;
    }

    public static ClientMessage encodeResponse(java.util.Collection<java.util.Map.Entry<java.lang.String,java.util.List<java.util.Map.Entry<java.lang.Integer,java.lang.Long>>>> namePartitionSequenceList, java.util.Collection<java.util.Map.Entry<java.lang.Integer,java.util.UUID>> partitionUuidList) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[RESPONSE_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, RESPONSE_MESSAGE_TYPE);
        clientMessage.addFrame(initialFrame);

        MapCodec.encode(clientMessage, namePartitionSequenceList, StringCodec::encode, MapIntegerLongCodec::encode);
        MapIntegerUUIDCodec.encode(clientMessage, partitionUuidList);
        return clientMessage;
    }

    public static CacheFetchNearCacheInvalidationMetadataCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.iterator();
        ResponseParameters response = new ResponseParameters();
        iterator.next();//empty initial frame
        response.namePartitionSequenceList = MapCodec.decode(iterator, StringCodec::decode, MapIntegerLongCodec::decode);
        response.partitionUuidList = MapIntegerUUIDCodec.decode(iterator);
        return response;
    }

}
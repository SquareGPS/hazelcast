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
 * Loads all keys into the store. This is a batch load operation so that an implementation can optimize the multiple loads.
 */
public class MapLoadAllCodec {

        private static final int REQUEST_REPLACE_EXISTING_VALUES_FIELD_OFFSET = CORRELATION_ID_FIELD_OFFSET + BOOLEAN_SIZE_IN_BYTES;
        private static final int REQUEST_INITIAL_FRAME_SIZE = REQUEST_REPLACE_EXISTING_VALUES_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
        public static final int REQUEST_MESSAGE_TYPE = 292;//hex: 0x0124,
        private static final int RESPONSE_INITIAL_FRAME_SIZE = CORRELATION_ID_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
        public static final int RESPONSE_MESSAGE_TYPE = 100;//hex: 0x0064,

    public static class RequestParameters {

        /**
         * name of map
         */
        public java.lang.String name;

        /**
         * when <code>true</code>, existing values in the Map will
         * be replaced by those loaded from the MapLoader
         */
        public boolean replaceExistingValues;
    }

    public static ClientMessage encodeRequest(java.lang.String name, boolean replaceExistingValues) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Map.LoadAll");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        encodeBoolean(initialFrame.content, REQUEST_REPLACE_EXISTING_VALUES_FIELD_OFFSET, replaceExistingValues);
        clientMessage.addFrame(initialFrame);
        StringCodec.encode(clientMessage, name);
        return clientMessage;
    }

    public static MapLoadAllCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.iterator();
        RequestParameters request = new RequestParameters();
        ClientMessage.Frame initialFrame = iterator.next();
        request.replaceExistingValues = decodeBoolean(initialFrame.content, REQUEST_REPLACE_EXISTING_VALUES_FIELD_OFFSET);
        request.name = StringCodec.decode(iterator);
        return request;
    }

    public static class ResponseParameters {
    }

    public static ClientMessage encodeResponse() {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[RESPONSE_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, RESPONSE_MESSAGE_TYPE);
        clientMessage.addFrame(initialFrame);

        return clientMessage;
    }

    public static MapLoadAllCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.iterator();
        ResponseParameters response = new ResponseParameters();
        iterator.next();//empty initial frame
        return response;
    }

}
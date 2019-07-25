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
 * TODO DOC
 */
public class ExecutorServiceCancelOnPartitionCodec {

        private static final int REQUEST_INTERRUPT_FIELD_OFFSET = CORRELATION_ID_FIELD_OFFSET + BOOLEAN_SIZE_IN_BYTES;
        private static final int REQUEST_INITIAL_FRAME_SIZE = REQUEST_INTERRUPT_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
        public static final int REQUEST_MESSAGE_TYPE = 2307;//hex: 0x0903,
        private static final int RESPONSE_RESPONSE_FIELD_OFFSET = CORRELATION_ID_FIELD_OFFSET + BOOLEAN_SIZE_IN_BYTES;
        private static final int RESPONSE_INITIAL_FRAME_SIZE = CORRELATION_ID_FIELD_OFFSET + LONG_SIZE_IN_BYTES;
        public static final int RESPONSE_MESSAGE_TYPE = 101;//hex: 0x0065,

    public static class RequestParameters {

        /**
         * Unique id for the execution.
         */
        public java.lang.String uuid;

        /**
         * If true, then the thread interrupt call can be used to cancel the thread, otherwise interrupt can not be used.
         */
        public boolean interrupt;
    }

    public static ClientMessage encodeRequest(java.lang.String uuid, boolean interrupt) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ExecutorService.CancelOnPartition");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        encodeBoolean(initialFrame.content, REQUEST_INTERRUPT_FIELD_OFFSET, interrupt);
        clientMessage.addFrame(initialFrame);
        StringCodec.encode(clientMessage, uuid);
        return clientMessage;
    }

    public static ExecutorServiceCancelOnPartitionCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.iterator();
        RequestParameters request = new RequestParameters();
        ClientMessage.Frame initialFrame = iterator.next();
        request.interrupt = decodeBoolean(initialFrame.content, REQUEST_INTERRUPT_FIELD_OFFSET);
        request.uuid = StringCodec.decode(iterator);
        return request;
    }

    public static class ResponseParameters {

        /**
         * TODO DOC
         */
        public boolean response;
    }

    public static ClientMessage encodeResponse(boolean response) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[RESPONSE_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, RESPONSE_MESSAGE_TYPE);
        clientMessage.addFrame(initialFrame);

        encodeBoolean(initialFrame.content, RESPONSE_RESPONSE_FIELD_OFFSET, response);
        return clientMessage;
    }

    public static ExecutorServiceCancelOnPartitionCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.iterator();
        ResponseParameters response = new ResponseParameters();
        ClientMessage.Frame initialFrame = iterator.next();
        response.response = decodeBoolean(initialFrame.content, RESPONSE_RESPONSE_FIELD_OFFSET);
        return response;
    }

}
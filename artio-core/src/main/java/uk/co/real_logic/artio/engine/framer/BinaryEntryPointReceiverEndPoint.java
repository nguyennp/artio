/*
 * Copyright 2021 Monotonic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.artio.engine.framer;

import org.agrona.ErrorHandler;
import org.agrona.concurrent.EpochNanoClock;
import uk.co.real_logic.artio.protocol.GatewayPublication;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

import static uk.co.real_logic.artio.fixp.SimpleOpenFramingHeader.BINARY_ENTRYPOINT_TYPE;

public class BinaryEntryPointReceiverEndPoint extends FixPReceiverEndPoint
{
    private boolean requiresAuthentication = true;

    BinaryEntryPointReceiverEndPoint(
        final long connectionId,
        final TcpChannel channel,
        final int bufferSize,
        final ErrorHandler errorHandler,
        final Framer framer,
        final GatewayPublication publication,
        final int libraryId,
        final EpochNanoClock epochNanoClock,
        final long correlationId)
    {
        super(
            connectionId,
            channel,
            bufferSize,
            errorHandler,
            framer,
            publication,
            libraryId,
            epochNanoClock,
            correlationId,
            BINARY_ENTRYPOINT_TYPE);
    }

    void checkMessage(final MutableAsciiBuffer buffer, final int offset, final int messageSize)
    {
        if (requiresAuthentication && pendingAcceptorLogon == null)
        {
            pendingAcceptorLogon = fixPGatewaySession.onLogon(buffer, offset, messageSize, channel, framer);
        }
    }

    boolean requiresAuthentication()
    {
        return requiresAuthentication;
    }

    void authenticated()
    {
        requiresAuthentication = false;
    }

    void trackDisconnect()
    {
    }
}

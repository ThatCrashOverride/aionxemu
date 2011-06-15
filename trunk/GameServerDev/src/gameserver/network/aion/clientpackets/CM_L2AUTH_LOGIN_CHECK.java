/**
 * This file is part of Aion X Emu <aionxemu.com>
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package gameserver.network.aion.clientpackets;

import gameserver.network.aion.AionClientPacket;
import gameserver.network.loginserver.LoginServer;

/**
 * In this packets aion client is authenticating himself by providing accountId and rest of sessionKey - we will check
 * if its valid at login server side.
 *
 * @author -Nemesiss-
 */
// TODO: L2AUTH? Really? :O
public class CM_L2AUTH_LOGIN_CHECK extends AionClientPacket {

    /**
     * playOk2 is part of session key - its used for security purposes we will check if this is the key what login
     * server sends.
     */
    private int playOk2;
    /**
     * playOk1 is part of session key - its used for security purposes we will check if this is the key what login
     * server sends.
     */
    private int playOk1;
    /**
     * accountId is part of session key - its used for authentication we will check if this accountId is matching any
     * waiting account login server side and check if rest of session key is ok.
     */
    private int accountId;
    /**
     * loginOk is part of session key - its used for security purposes we will check if this is the key what login
     * server sends.
     */
    private int loginOk;

    /**
     * Constructs new instance of <tt>CM_L2AUTH_LOGIN_CHECK </tt> packet
     *
     * @param opcode
     */
    public CM_L2AUTH_LOGIN_CHECK(int opcode) {
        super(opcode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        playOk2 = readD();
        playOk1 = readD();
        accountId = readD();
        loginOk = readD();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {
        LoginServer.getInstance().requestAuthenticationOfClient(accountId, getConnection(), loginOk, playOk1, playOk2);
    }
}

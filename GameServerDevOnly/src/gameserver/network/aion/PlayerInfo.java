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
package gameserver.network.aion;

import gameserver.model.account.PlayerAccountData;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.PlayerAppearance;
import gameserver.model.gameobjects.player.PlayerCommonData;
import gameserver.model.items.GodStone;
import gameserver.model.items.ItemSlot;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.utils.i18n.CustomMessageId;
import gameserver.utils.i18n.LanguageHandler;

import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author AEJTester
 * @author Nemesiss
 * @author Niato
 */
public abstract class PlayerInfo extends AionServerPacket {
    private static Logger log = Logger.getLogger(PlayerInfo.class);
    
    protected PlayerInfo() {

    }

    protected void writePlayerInfo(ByteBuffer buf, PlayerAccountData accPlData) {
        PlayerCommonData pbd = accPlData.getPlayerCommonData();
        final int raceId = pbd.getRace().getRaceId();
        final int genderId = pbd.getGender().getGenderId();
        final PlayerAppearance playerAppearance = accPlData.getAppereance();
        writeD(buf, pbd.getPlayerObjId());
        writeS(buf, pbd.getName());
        /**
         * Stupid NC...
         */
        int size = 44 - (pbd.getName().length() * 2 + 2);
        byte[] stupidNc = new byte[size];
        writeB(buf, stupidNc);
        writeD(buf, genderId);
        writeD(buf, raceId);
        writeD(buf, pbd.getPlayerClass().getClassId());
        writeD(buf, playerAppearance.getVoice());
        writeD(buf, playerAppearance.getSkinRGB());
        writeD(buf, playerAppearance.getHairRGB());
        writeD(buf, playerAppearance.getEyeRGB());
        writeD(buf, playerAppearance.getLipRGB());
        writeC(buf, playerAppearance.getFace());
        writeC(buf, playerAppearance.getHair());
        writeC(buf, playerAppearance.getDeco());
        writeC(buf, playerAppearance.getTattoo());
        writeC(buf, 5);// always 5
        writeC(buf, playerAppearance.getFaceShape());
        writeC(buf, playerAppearance.getForehead());
        writeC(buf, playerAppearance.getEyeHeight());
        writeC(buf, playerAppearance.getEyeSpace());
        writeC(buf, playerAppearance.getEyeWidth());
        writeC(buf, playerAppearance.getEyeSize());
        writeC(buf, playerAppearance.getEyeShape());
        writeC(buf, playerAppearance.getEyeAngle());
        writeC(buf, playerAppearance.getBrowHeight());
        writeC(buf, playerAppearance.getBrowAngle());
        writeC(buf, playerAppearance.getBrowShape());
        writeC(buf, playerAppearance.getNose());
        writeC(buf, playerAppearance.getNoseBridge());
        writeC(buf, playerAppearance.getNoseWidth());
        writeC(buf, playerAppearance.getNoseTip());
        writeC(buf, playerAppearance.getCheek());
        writeC(buf, playerAppearance.getLipHeight());
        writeC(buf, playerAppearance.getMouthSize());
        writeC(buf, playerAppearance.getLipSize());
        writeC(buf, playerAppearance.getSmile());
        writeC(buf, playerAppearance.getLipShape());
        writeC(buf, playerAppearance.getJawHeigh());
        writeC(buf, playerAppearance.getChinJut());
        writeC(buf, playerAppearance.getEarShape());
        writeC(buf, playerAppearance.getHeadSize());
        // 1.5.x 0x00, shoulderSize, armLength, legLength (BYTE) after HeadSize

        writeC(buf, playerAppearance.getNeck());
        writeC(buf, playerAppearance.getNeckLength());
        writeC(buf, playerAppearance.getShoulderSize()); // shoulderSize

        writeC(buf, playerAppearance.getTorso());
        writeC(buf, playerAppearance.getChest());
        writeC(buf, playerAppearance.getWaist());
        writeC(buf, playerAppearance.getHips());
        writeC(buf, playerAppearance.getArmThickness());
        writeC(buf, playerAppearance.getHandSize());
        writeC(buf, playerAppearance.getLegThicnkess());
        writeC(buf, playerAppearance.getFootSize());
        writeC(buf, playerAppearance.getFacialRate());
        writeC(buf, 0x00); // 0x00
        writeC(buf, playerAppearance.getArmLength()); // armLength
        writeC(buf, playerAppearance.getLegLength()); // legLength
        writeC(buf, playerAppearance.getShoulders());
        writeC(buf, 0x00); // always 0 may be acessLevel
        writeC(buf, 0x00); // always 0 - unk

        writeF(buf, playerAppearance.getHeight());
        int raceSex = 100000 + raceId * 2 + genderId;
        writeD(buf, raceSex);
        writeD(buf, pbd.getPosition().getMapId());//mapid for preloading map
        writeF(buf, pbd.getPosition().getX());
        writeF(buf, pbd.getPosition().getY());
        writeF(buf, pbd.getPosition().getZ());
        writeD(buf, pbd.getPosition().getHeading());
        writeD(buf, pbd.getLevel());// lvl confirmed
        writeD(buf, pbd.getTitleId());
        if(!accPlData.isLegionMember())
        {
            byte[] somebyte = new byte[76];
            writeB(buf, somebyte);
        }else{
            writeD(buf, accPlData.getLegion().getLegionId());
            writeS(buf, accPlData.getLegion().getLegionName());
            byte[] somebyte = new byte[70/*-2byte*/- (accPlData.getLegion().getLegionName().length() * 2)];
            writeB(buf, somebyte);
        }

        int itemsDataSize = 0;
        //TODO figure out this part when fully equipped
        List<Item> items = accPlData.getEquipment();

        for (Item item : items) {
            ItemTemplate itemTemplate = item.getItemTemplate();
            if (itemTemplate == null) {
                log.warn(LanguageHandler.translate(CustomMessageId.ERROR_ITEM_TEMPATE_MISSING,
                    pbd.getPlayerObjId(), item.getObjectId(), item.getItemId()));
                continue;
            }

            if (itemsDataSize < 208 &&
                itemTemplate.getItemSlot() <= ItemSlot.PANTS.getSlotIdMask())
            {
                writeC(buf, 1); // this flas is needed to show equipment on selection screen
                writeD(buf, item.getItemSkinTemplate().getTemplateId());
                GodStone godStone = item.getGodStone();
                writeD(buf, godStone != null ? godStone.getItemId() : 0);
                writeD(buf, item.getItemColor());

                itemsDataSize += 13;
            }
        }

        stupidNc = new byte[208 - itemsDataSize];
        writeB(buf, stupidNc);
        writeD(buf, accPlData.getDeletionTimeInSeconds());
        writeD(buf, 0x00);// unk
    }
}
package schr0.chastmob.util;

import schr0.chastmob.ChastMob;

public class ChastMobNBTs
{

	private static final String TAG = ChastMob.MOD_ID + ".";

	private static final String ENTITY_CHAST = TAG + "entity.chast" + ".";
	public static final String ENTITY_CHAST_INVENTORY = ENTITY_CHAST + "inventory";
	public static final String ENTITY_CHAST_EQUIPMENT = ENTITY_CHAST + "equipment";
	public static final String ENTITY_CHAST_ARM_COLOR = ENTITY_CHAST + "arm_color";
	public static final String ENTITY_CHAST_OWNER_UUID = ENTITY_CHAST + "owner_uuid";
	public static final String ENTITY_CHAST_OWNER_FOLLOW = ENTITY_CHAST + "owner_follow";
	public static final String ENTITY_CHAST_STATE_SIT = ENTITY_CHAST + "state_sit";

	private static final String ITEM_MODE_PATROL = TAG + "item.mode_patrol" + ".";
	public static final String ITEM_MODE_PATROL_POS_X = ITEM_MODE_PATROL + "pos_x";
	public static final String ITEM_MODE_PATROL_POS_Y = ITEM_MODE_PATROL + "pos_y";
	public static final String ITEM_MODE_PATROL_POS_Z = ITEM_MODE_PATROL + "pos_z";

	public static final String ITEM_FILTER = TAG + "item.filter" + ".";
	public static final String ITEM_FILTER_INVENTORY = ITEM_FILTER + "inventory";

}

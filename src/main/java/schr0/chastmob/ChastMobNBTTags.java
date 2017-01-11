package schr0.chastmob;

public class ChastMobNBTTags
{

	private static final String TAG = ChastMob.MOD_ID + ".";
	private static final String CATEGORY_ENTITY = TAG + "entity" + ".";
	public static final String ENTITY_CHAST = CATEGORY_ENTITY + "chast" + ".";

	public static final String CHAST_INVENTORY = ENTITY_CHAST + "inventory";
	public static final String CHAST_ARM_COLOR = ENTITY_CHAST + "arm_color";
	public static final String CHAST_OWNER_UUID = ENTITY_CHAST + "owner_uuid";
	public static final String CHAST_MODE_FOLLOW = ENTITY_CHAST + "mode_follow";
	public static final String CHAST_STATE_SIT = ENTITY_CHAST + "state_sit";

}

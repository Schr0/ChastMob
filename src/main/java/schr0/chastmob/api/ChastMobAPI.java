package schr0.chastmob.api;

import net.minecraftforge.common.util.EnumHelper;
import schr0.chastmob.api.ItemChastHelmet.ChastHelmetMaterial;

public class ChastMobAPI
{

	/**
	 * ModのID.
	 */
	public static final String MOD_ID = "schr0chastmob";

	public static ChastHelmetMaterial addChastHelmetMaterial(String name, int maxUses, int damageReduceAmount, int enchantability)
	{
		final Class<?>[] paramTypes =
		{
				int.class, int.class, int.class
		};

		return EnumHelper.addEnum(ChastHelmetMaterial.class, name, paramTypes, maxUses, damageReduceAmount, enchantability);
	}

}

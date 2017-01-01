package schr0.chastmob;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityChast extends EntityGolem
{

	private static final DataParameter<Byte> COVER_OPEN = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> ARM_COLOR = EntityDataManager.<Integer> createKey(EntityChast.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> SITTING = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);

	private InventoryChast inventoryChast;
	private EntityAIChastSit aiChastSit;
	private EntityPlayer tradePlayer;
	private float lidAngle;
	private float prevLidAngle;

	public EntityChast(World worldIn)
	{
		super(worldIn);
		this.setSize(0.9F, 1.5F);

		this.inventoryChast = new InventoryChast(this);
	}

	public static void func_189790_b(DataFixer p_189790_0_)
	{
		EntityLiving.func_189752_a(p_189790_0_, ChastMobEntitys.NAME_CHAST);
	}

	@Override
	protected void initEntityAI()
	{
		super.initEntityAI();

		EntityAIBase aiSwimming = new EntityAISwimming(this);
		EntityAIBase aiPanic = new EntityAIPanic(this, 2.5D);
		this.aiChastSit = new EntityAIChastSit(this);
		EntityAIBase aiChastTrading = new EntityAIChastTrading(this);
		EntityAIBase aiWander = new EntityAIWander(this, 1.25D);
		EntityAIBase aiWatchClosest = new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F);
		EntityAIBase aiLookIdle = new EntityAILookIdle(this);

		aiSwimming.setMutexBits(0);
		aiPanic.setMutexBits(1);
		this.aiChastSit.setMutexBits(1);
		aiChastTrading.setMutexBits(1);
		aiWander.setMutexBits(1);
		aiWatchClosest.setMutexBits(2);
		aiLookIdle.setMutexBits(3);

		this.tasks.addTask(0, aiSwimming);
		this.tasks.addTask(1, aiPanic);
		this.tasks.addTask(2, this.aiChastSit);
		this.tasks.addTask(3, aiChastTrading);
		this.tasks.addTask(4, aiWander);
		this.tasks.addTask(5, aiWatchClosest);
		this.tasks.addTask(6, aiLookIdle);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(COVER_OPEN, Byte.valueOf((byte) 0));
		this.getDataManager().register(ARM_COLOR, Integer.valueOf(EnumDyeColor.WHITE.getDyeDamage()));
		this.getDataManager().register(SITTING, Byte.valueOf((byte) 0));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setBoolean(ChastMobNBTTags.CHAST_COVER_OPEN, this.isCoverOpen());
		compound.setByte(ChastMobNBTTags.CHAST_ARM_COLOR, (byte) this.getArmColor().getDyeDamage());
		compound.setBoolean(ChastMobNBTTags.CHAST_SITTING, this.isSitting());

		compound.setTag(ChastMobNBTTags.CHAST_INVENTORY, this.getInventoryChast().writeInventoryToNBT());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		this.setCoverOpen(compound.getBoolean(ChastMobNBTTags.CHAST_COVER_OPEN));
		this.setArmColor(EnumDyeColor.byDyeDamage(compound.getByte(ChastMobNBTTags.CHAST_ARM_COLOR)));
		this.setSitting(compound.getBoolean(ChastMobNBTTags.CHAST_SITTING));
		this.aiChastSit.setSitting(compound.getBoolean(ChastMobNBTTags.CHAST_SITTING));

		this.getInventoryChast().readInventoryFromNBT(compound.getTagList(ChastMobNBTTags.CHAST_INVENTORY, 10));
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);

		World world = this.getEntityWorld();

		if (!world.isRemote)
		{
			InventoryHelper.dropInventoryItems(world, this, this.getInventoryChast());
		}
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack)
	{
		if (!hand.equals(EnumHand.MAIN_HAND) || (this.getEntityWorld().isRemote))
		{
			return false;
		}

		if (ChastMobVanillaHelper.isNotEmptyItemStack(stack))
		{
			if (stack.getItem().equals(Items.DYE))
			{
				EnumDyeColor enumDyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());

				if (enumDyeColor != this.getArmColor())
				{
					this.setArmColor(enumDyeColor);

					if (!player.capabilities.isCreativeMode)
					{
						--stack.stackSize;
					}

					player.swingArm(hand);

					this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

					return true;
				}
			}
		}

		if (player.isSneaking())
		{
			this.aiChastSit.setSitting(!this.isSitting());

			player.swingArm(hand);

			this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
		}
		else
		{
			player.displayGUIChest(this.getInventoryChast());

			player.swingArm(hand);
		}

		return true;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		this.onUpdateCoverOpen(this, this.isCoverOpen());
	}

	// TODO /* ======================================== MOD START =====================================*/

	@SideOnly(Side.CLIENT)
	public float getAngleCoverX(float partialTickTime)
	{
		return ((this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * partialTickTime) * 0.5F * (float) Math.PI);
	}

	public boolean isCoverOpen()
	{
		return ((((Byte) this.getDataManager().get(COVER_OPEN)).byteValue() & 16) != 0);
	}

	public void setCoverOpen(boolean isCoverOpen)
	{
		byte b0 = ((Byte) this.getDataManager().get(COVER_OPEN)).byteValue();

		if (isCoverOpen)
		{
			this.getDataManager().set(COVER_OPEN, Byte.valueOf((byte) (b0 | 16)));
		}
		else
		{
			this.getDataManager().set(COVER_OPEN, Byte.valueOf((byte) (b0 & -17)));
		}
	}

	public EnumDyeColor getArmColor()
	{
		return EnumDyeColor.byDyeDamage(((Integer) this.getDataManager().get(ARM_COLOR)).intValue() & 15);
	}

	public void setArmColor(EnumDyeColor enumDyeColor)
	{
		this.getDataManager().set(ARM_COLOR, Integer.valueOf(enumDyeColor.getDyeDamage()));
	}

	public boolean isSitting()
	{
		return ((((Byte) this.getDataManager().get(SITTING)).byteValue() & 16) != 0);
	}

	public void setSitting(boolean isSitting)
	{
		byte b0 = ((Byte) this.getDataManager().get(SITTING)).byteValue();

		if (isSitting)
		{
			this.getDataManager().set(SITTING, Byte.valueOf((byte) (b0 | 16)));
		}
		else
		{
			this.getDataManager().set(SITTING, Byte.valueOf((byte) (b0 & -17)));
		}
	}

	public InventoryChast getInventoryChast()
	{
		return this.inventoryChast;
	}

	@Nullable
	public EntityPlayer getTradePlayer()
	{
		return this.tradePlayer;
	}

	public void setTradePlayer(EntityPlayer player)
	{
		this.tradePlayer = player;
	}

	private void onUpdateCoverOpen(EntityChast entityChast, boolean isCoverOpen)
	{
		World world = this.getEntityWorld();

		this.prevLidAngle = this.lidAngle;

		if (isCoverOpen && (this.lidAngle == 0.0F))
		{
			entityChast.setCoverOpen(true);

			this.playSound(SoundEvents.BLOCK_CHEST_OPEN, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		}

		if ((!isCoverOpen && (0.0F < this.lidAngle)) || (isCoverOpen && (this.lidAngle < 1.0F)))
		{
			float angel1 = 0.1F;
			float angel2 = this.lidAngle;
			float angel3 = 0.5F;

			if (isCoverOpen)
			{
				this.lidAngle += angel1;
			}
			else
			{
				this.lidAngle -= angel1;
			}

			if (1.0F < this.lidAngle)
			{
				this.lidAngle = 1.0F;
			}

			if ((this.lidAngle < angel3) && (angel3 <= angel2))
			{
				entityChast.setCoverOpen(false);

				this.playSound(SoundEvents.BLOCK_CHEST_CLOSE, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F)
			{
				this.lidAngle = 0.0F;
			}
		}
	}

}

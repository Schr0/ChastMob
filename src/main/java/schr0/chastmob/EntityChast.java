package schr0.chastmob;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityChast extends EntityGolem
{

	private static final DataParameter<Integer> ARM_COLOR = EntityDataManager.<Integer> createKey(EntityChast.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> COVER_OPEN = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.<Optional<UUID>> createKey(EntityChast.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Byte> OWNER_TAME = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> OWNER_FOLLOW = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE_PANIC = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE_SIT = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE_TRADE = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);

	private InventoryChast inventoryChast;
	private EntityAIChastPanic aiChastPanic;
	private EntityAIChastSit aiChastSit;
	private EntityAIChastTrade aiChastTrade;
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

		double speed = 1.25D;
		int distance = 5;

		// Base
		EntityAIBase aiSwimming = new EntityAISwimming(this);
		this.aiChastPanic = new EntityAIChastPanic(this, (speed * 2), distance);
		this.aiChastSit = new EntityAIChastSit(this);
		this.aiChastTrade = new EntityAIChastTrade(this);
		// Follow || Freedom
		EntityAIBase aiChastCollectItem = new EntityAIChastCollectItem(this, speed, (double) distance);
		EntityAIBase aiChastFollowOwner = new EntityAIChastFollowOwner(this, speed, (double) distance);
		EntityAIBase aiChastGoHome = new EntityAIChastGoHome(this, speed, distance);
		EntityAIBase aiChastStoreChest = new EntityAIChastStoreChest(this, speed, distance);
		// Wander
		EntityAIBase aiChastWander = new EntityAIChastWander(this, speed, distance);
		EntityAIBase aiWatchClosestPlayer = new EntityAIWatchClosest(this, EntityPlayer.class, (float) distance);
		EntityAIBase aiWatchClosestGolem = new EntityAIWatchClosest(this, EntityGolem.class, (float) distance);
		EntityAIBase aiLookIdle = new EntityAILookIdle(this);

		aiSwimming.setMutexBits(0);
		this.aiChastPanic.setMutexBits(1);
		this.aiChastSit.setMutexBits(1);
		this.aiChastTrade.setMutexBits(1);
		aiChastCollectItem.setMutexBits(1);
		aiChastFollowOwner.setMutexBits(1);
		aiChastGoHome.setMutexBits(1);
		aiChastStoreChest.setMutexBits(1);
		aiChastWander.setMutexBits(1);
		aiWatchClosestPlayer.setMutexBits(2);
		aiWatchClosestGolem.setMutexBits(2);
		aiLookIdle.setMutexBits(2);

		this.tasks.addTask(0, aiSwimming);
		this.tasks.addTask(1, this.aiChastPanic);
		this.tasks.addTask(2, this.aiChastSit);
		this.tasks.addTask(3, this.aiChastTrade);
		this.tasks.addTask(4, aiChastCollectItem);
		this.tasks.addTask(5, aiChastFollowOwner);
		this.tasks.addTask(5, aiChastGoHome);
		this.tasks.addTask(6, aiChastStoreChest);
		this.tasks.addTask(7, aiChastWander);
		this.tasks.addTask(8, aiWatchClosestPlayer);
		this.tasks.addTask(8, aiWatchClosestGolem);
		this.tasks.addTask(9, aiLookIdle);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(ARM_COLOR, Integer.valueOf(EnumDyeColor.WHITE.getDyeDamage()));
		this.getDataManager().register(COVER_OPEN, Byte.valueOf((byte) 0));
		this.getDataManager().register(OWNER_UUID, Optional.<UUID> absent());
		this.getDataManager().register(OWNER_TAME, Byte.valueOf((byte) 0));
		this.getDataManager().register(OWNER_FOLLOW, Byte.valueOf((byte) 0));
		this.getDataManager().register(STATE_PANIC, Byte.valueOf((byte) 0));
		this.getDataManager().register(STATE_SIT, Byte.valueOf((byte) 0));
		this.getDataManager().register(STATE_TRADE, Byte.valueOf((byte) 0));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setTag(ChastMobNBTTags.CHAST_INVENTORY, this.getInventoryChast().writeInventoryToNBT());

		compound.setByte(ChastMobNBTTags.CHAST_ARM_COLOR, (byte) this.getArmColor().getDyeDamage());

		if (this.getOwnerUUID() == null)
		{
			compound.setString(ChastMobNBTTags.CHAST_OWNER_UUID, "");
		}
		else
		{
			compound.setString(ChastMobNBTTags.CHAST_OWNER_UUID, this.getOwnerUUID().toString());
		}

		compound.setBoolean(ChastMobNBTTags.CHAST_OWNER_FOLLOW, this.isOwnerFollow());

		compound.setBoolean(ChastMobNBTTags.CHAST_STATE_SIT, this.isStateSit());

		// TODO VANILLA BUG ?
		if (this.getRidingEntity() instanceof EntityPlayer)
		{
			this.dismountRidingEntity();
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		this.getInventoryChast().readInventoryFromNBT(compound.getTagList(ChastMobNBTTags.CHAST_INVENTORY, 10));

		this.setArmColor(EnumDyeColor.byDyeDamage(compound.getByte(ChastMobNBTTags.CHAST_ARM_COLOR)));

		this.setCoverOpen(false);

		String ownerUUID;

		if (compound.hasKey(ChastMobNBTTags.CHAST_OWNER_UUID, 8))
		{
			ownerUUID = compound.getString(ChastMobNBTTags.CHAST_OWNER_UUID);
		}
		else
		{
			ownerUUID = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), compound.getString("Owner"));
		}

		if (!ownerUUID.isEmpty())
		{
			try
			{
				this.setOwnerUUID(UUID.fromString(ownerUUID));
				this.setOwnerTame(true);
			}
			catch (Throwable var4)
			{
				this.setOwnerTame(false);
			}
		}

		this.setOwnerFollow(compound.getBoolean(ChastMobNBTTags.CHAST_OWNER_FOLLOW));

		if (this.aiChastPanic != null)
		{
			this.setAIPanicking(0);
		}

		this.setStatePanic(false);

		if (this.aiChastSit != null)
		{
			this.setAISitting(compound.getBoolean(ChastMobNBTTags.CHAST_OWNER_UUID));
		}

		this.setStateSit(compound.getBoolean(ChastMobNBTTags.CHAST_OWNER_UUID));

		if (this.aiChastTrade != null)
		{
			this.setAITrading(null);
		}

		this.setStateTrade(false);
	}

	@Override
	public double getYOffset()
	{
		if (this.getRidingEntity() != null)
		{
			return (super.getYOffset() - this.getRidingEntity().getYOffset());
		}

		return super.getYOffset();
	}

	@Override
	public double getMountedYOffset()
	{
		if (this.isStateSit())
		{
			return ((double) this.height * 0.45);
		}
		else
		{
			return ((double) this.height * 0.80);
		}
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player)
	{
		return (this.isOwnerTame() && this.isOwnerEntity(player));
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.getRidingEntity() instanceof EntityLivingBase)
		{
			return false;
		}

		boolean attackEntityFrom = super.attackEntityFrom(source, amount);

		if ((source.getSourceOfDamage() instanceof EntityLivingBase) && !this.getEntityWorld().isRemote)
		{
			this.setAIPanicking((int) amount);
		}

		return attackEntityFrom;
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
		if ((!hand.equals(EnumHand.MAIN_HAND)) || this.isStatePanic())
		{
			return false;
		}

		boolean isServerWorld = (!this.getEntityWorld().isRemote);

		if (this.isOwnerTame())
		{
			if (!this.isOwnerEntity(player))
			{
				return false;
			}

			if (ChastMobHelper.isNotEmptyItemStack(stack))
			{
				if (stack.getItem().equals(ChastMobItems.HOME_CHEST_MAP))
				{
					ItemStack stackMain = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

					if (!ChastMobHelper.isNotEmptyItemStack(stackMain))
					{
						this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);

						player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ChastMobHelper.getEmptyItemStack());

						this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

						player.swingArm(hand);

						return true;
					}
				}

				if (stack.getItem().equals(Items.DYE))
				{
					EnumDyeColor enumDyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());

					if (enumDyeColor != this.getArmColor())
					{
						if (isServerWorld)
						{
							this.setArmColor(enumDyeColor);

							if (!player.capabilities.isCreativeMode)
							{
								--stack.stackSize;
							}
						}

						this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

						player.swingArm(hand);

						return true;
					}
				}
			}

			if (player.isSneaking())
			{
				if (isServerWorld)
				{
					for (Entity entity : this.getPassengers())
					{
						if (entity instanceof EntityTameable)
						{
							EntityTameable entityTameable = (EntityTameable) entity;

							entityTameable.getAISit().setSitting(false);
							entityTameable.setSitting(false);
						}

						entity.dismountRidingEntity();
					}

					this.setAISitting(!this.isStateSit());

					if (this.isStateSit())
					{
						this.setOwnerFollow(!this.isOwnerFollow());
					}
					else
					{
						ItemStack stackMain = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

						if (ChastMobHelper.isNotEmptyItemStack(stackMain))
						{
							Block.spawnAsEntity(this.getEntityWorld(), this.getPosition(), stackMain);

							this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ChastMobHelper.getEmptyItemStack());
						}
					}
				}

				this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

				player.swingArm(hand);
			}
			else
			{
				if (this.getPassengers().isEmpty())
				{
					if (isServerWorld)
					{
						player.displayGUIChest(this.getInventoryChast());
					}

					player.swingArm(hand);
				}
			}
		}
		// TODO TEST
		else
		{
			this.onSpawnByPlayer(player);
		}

		return true;
	}

	@Override
	public void updateRidden()
	{
		super.updateRidden();

		if (this.isRiding())
		{
			Entity ridingEntity = this.getRidingEntity();

			this.prevRotationYaw = this.rotationYaw = ridingEntity.rotationYaw;

			if (ridingEntity.isSneaking())
			{
				this.dismountRidingEntity();
			}
		}
	}

	@Override
	public void updatePassenger(Entity passenger)
	{
		super.updatePassenger(passenger);

		if (this.isStatePanic())
		{
			if (!this.getEntityWorld().isRemote)
			{
				passenger.dismountRidingEntity();
			}

			return;
		}

		if (passenger.getClass().equals(EntityOcelot.class))
		{
			EntityOcelot entityOcelot = (EntityOcelot) passenger;

			if (!entityOcelot.isSitting())
			{
				entityOcelot.getAISit().setSitting(true);
				entityOcelot.setSitting(true);
			}
		}
		else
		{
			if (this.isStateSit())
			{
				this.setAISitting(false);
			}
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		this.onUpdateOpen(this, this.isCoverOpen());
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void onSpawnByPlayer(EntityPlayer player)
	{
		if (!player.getEntityWorld().isRemote)
		{
			this.setArmColor(EnumDyeColor.byDyeDamage(player.getRNG().nextInt(15)));
			this.setOwnerTame(true);
			this.setOwnerUUID(player.getUniqueID());
			this.setAISitting(true);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getAngleCoverX(float partialTickTime)
	{
		return ((this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * partialTickTime) * 0.5F * (float) Math.PI);
	}

	public EnumDyeColor getArmColor()
	{
		return EnumDyeColor.byDyeDamage(((Integer) this.getDataManager().get(ARM_COLOR)).intValue() & 15);
	}

	public void setArmColor(EnumDyeColor armColor)
	{
		this.getDataManager().set(ARM_COLOR, Integer.valueOf(armColor.getDyeDamage()));
	}

	public boolean isCoverOpen()
	{
		return ((((Byte) this.getDataManager().get(COVER_OPEN)).byteValue() & 1) != 0);
	}

	public void setCoverOpen(boolean isCoverOpen)
	{
		byte b0 = ((Byte) this.getDataManager().get(COVER_OPEN)).byteValue();

		if (isCoverOpen)
		{
			this.getDataManager().set(COVER_OPEN, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(COVER_OPEN, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	@Nullable
	public UUID getOwnerUUID()
	{
		return (UUID) ((Optional) this.getDataManager().get(OWNER_UUID)).orNull();
	}

	public void setOwnerUUID(@Nullable UUID ownerUUID)
	{
		this.getDataManager().set(OWNER_UUID, Optional.fromNullable(ownerUUID));
	}

	public boolean isOwnerTame()
	{
		return (((Byte) this.getDataManager().get(OWNER_TAME)).byteValue() & 1) != 0;
	}

	public void setOwnerTame(boolean isOwnerTame)
	{
		byte b0 = ((Byte) this.getDataManager().get(OWNER_TAME)).byteValue();

		if (isOwnerTame)
		{
			this.getDataManager().set(OWNER_TAME, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(OWNER_TAME, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isOwnerFollow()
	{
		return ((((Byte) this.getDataManager().get(OWNER_FOLLOW)).byteValue() & 1) != 0);
	}

	public void setOwnerFollow(boolean isModeFollow)
	{
		byte b0 = ((Byte) this.getDataManager().get(OWNER_FOLLOW)).byteValue();

		if (isModeFollow)
		{
			this.getDataManager().set(OWNER_FOLLOW, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(OWNER_FOLLOW, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isStatePanic()
	{
		return (((Byte) this.getDataManager().get(STATE_PANIC)).byteValue() & 1) != 0;
	}

	public void setStatePanic(boolean isStatePanic)
	{
		byte b0 = ((Byte) this.getDataManager().get(STATE_PANIC)).byteValue();

		if (isStatePanic)
		{
			this.getDataManager().set(STATE_PANIC, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(STATE_PANIC, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isStateSit()
	{
		return (((Byte) this.getDataManager().get(STATE_SIT)).byteValue() & 1) != 0;
	}

	public void setStateSit(boolean isStateSit)
	{
		byte b0 = ((Byte) this.getDataManager().get(STATE_SIT)).byteValue();

		if (isStateSit)
		{
			this.getDataManager().set(STATE_SIT, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(STATE_SIT, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isStateTrade()
	{
		return (((Byte) this.getDataManager().get(STATE_TRADE)).byteValue() & 1) != 0;
	}

	public void setStateTrade(boolean isStateTrade)
	{
		byte b0 = ((Byte) this.getDataManager().get(STATE_TRADE)).byteValue();

		if (isStateTrade)
		{
			this.getDataManager().set(STATE_TRADE, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(STATE_TRADE, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public InventoryChast getInventoryChast()
	{
		return this.inventoryChast;
	}

	public boolean isOwnerEntity(EntityLivingBase owner)
	{
		if (!(owner instanceof EntityPlayer))
		{
			return false;
		}

		EntityPlayer entityPlayer = (EntityPlayer) owner;
		UUID uuid = entityPlayer.getUniqueID();
		UUID uuidOwner = this.getOwnerUUID();

		if ((uuid != null && uuidOwner != null) && uuid.equals(uuidOwner))
		{
			return true;
		}

		return false;
	}

	@Nullable
	public EntityLivingBase getOwnerEntity()
	{
		try
		{
			UUID uuid = this.getOwnerUUID();

			return (uuid == null) ? null : this.getEntityWorld().getPlayerEntityByUUID(uuid);
		}
		catch (IllegalArgumentException var2)
		{
			return null;
		}
	}

	public void setAIPanicking(int panicTime)
	{
		this.aiChastPanic.setPanicking(panicTime);

		if (0 < panicTime)
		{
			this.aiChastSit.setSitting(false);
			this.aiChastTrade.setTrading(null);
		}
	}

	public void setAISitting(boolean isSitting)
	{
		this.aiChastSit.setSitting(isSitting);
	}

	public void setAITrading(@Nullable EntityPlayer tradePlayer)
	{
		this.aiChastTrade.setTrading(tradePlayer);
	}

	private void onUpdateOpen(EntityChast entityChast, boolean isCoverOpen)
	{
		this.prevLidAngle = this.lidAngle;

		if (isCoverOpen && (this.lidAngle == 0.0F))
		{
			entityChast.setCoverOpen(true);

			this.playSound(SoundEvents.BLOCK_CHEST_OPEN, 0.5F, entityChast.rand.nextFloat() * 0.1F + 0.9F);
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

				this.playSound(SoundEvents.BLOCK_CHEST_CLOSE, 0.5F, entityChast.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F)
			{
				this.lidAngle = 0.0F;
			}
		}
	}

}

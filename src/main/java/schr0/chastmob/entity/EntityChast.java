package schr0.chastmob.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
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
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.api.ItemChastHelmet;
import schr0.chastmob.entity.ai.EntityAIChastCollectItem;
import schr0.chastmob.entity.ai.EntityAIChastFollowOwner;
import schr0.chastmob.entity.ai.EntityAIChastStatePanic;
import schr0.chastmob.entity.ai.EntityAIChastStateSit;
import schr0.chastmob.entity.ai.EntityAIChastStateTrade;
import schr0.chastmob.entity.ai.EntityAIChastStoreChest;
import schr0.chastmob.entity.ai.EntityAIChastWander;
import schr0.chastmob.init.ChastMobGuis;
import schr0.chastmob.init.ChastMobPackets;
import schr0.chastmob.inventory.InventoryChastEquipments;
import schr0.chastmob.inventory.InventoryChastMain;
import schr0.chastmob.packet.MessageParticleEntity;

public class EntityChast extends EntityGolem
{

	private static final float SIZE_WIDTH = 0.9F;
	private static final float SIZE_HEIGHT = 1.5F;
	private static final double ENTITY_HEALTH = 20.0D;
	private static final double ENTITY_SPEED = 0.25D;

	private static final String TAG = ChastMob.MOD_ID + "." + "entity_chast" + ".";
	private static final String TAG_INVENTORY = TAG + "inventory";
	private static final String TAG_EQUIPMENTS = TAG + "equipments";
	private static final String TAG_ARM_COLOR = TAG + "arm_color";
	private static final String TAG_OWNER_UUID = TAG + "owner_uuid";
	private static final String TAG_FOLLOW = TAG + "follow";
	private static final String TAG_STATE_SIT = TAG + "state_sit";

	private static final DataParameter<Integer> ARM_COLOR = EntityDataManager.<Integer> createKey(EntityChast.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> COVER_OPEN = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.<Optional<UUID>> createKey(EntityChast.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Byte> TAMED = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> FOLLOW = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE_SIT = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE_PANIC = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE_TRADE = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);

	private InventoryChastMain inventoryMain;
	private InventoryChastEquipments inventoryEquipments;
	private EntityAIChastStatePanic aiStatePanic;
	private EntityAIChastStateSit aiStateSit;
	private EntityAIChastStateTrade aiStateTrade;
	private float lidAngle;
	private float prevLidAngle;

	public EntityChast(World worldIn)
	{
		super(worldIn);
		this.setSize(SIZE_WIDTH, SIZE_HEIGHT);
	}

	@Override
	protected void initEntityAI()
	{
		super.initEntityAI();

		EntityAIBase aiSwimming = new EntityAISwimming(this);
		this.aiStatePanic = new EntityAIChastStatePanic(this);
		this.aiStateSit = new EntityAIChastStateSit(this);
		this.aiStateTrade = new EntityAIChastStateTrade(this);
		EntityAIBase aiStoreChest = new EntityAIChastStoreChest(this);
		EntityAIBase aiCollectItem = new EntityAIChastCollectItem(this);
		EntityAIBase aiFollowOwner = new EntityAIChastFollowOwner(this);
		EntityAIBase aiWander = new EntityAIChastWander(this);
		EntityAIBase aiWatchClosestEntityPlayer = new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F);
		EntityAIBase aiWatchClosestEntityGolem = new EntityAIWatchClosest(this, EntityGolem.class, 6.0F);
		EntityAIBase aiLookIdle = new EntityAILookIdle(this);

		aiSwimming.setMutexBits(0);
		this.aiStatePanic.setMutexBits(1);
		this.aiStateSit.setMutexBits(1);
		this.aiStateTrade.setMutexBits(1);
		aiStoreChest.setMutexBits(1);
		aiCollectItem.setMutexBits(1);
		aiFollowOwner.setMutexBits(1);
		aiWander.setMutexBits(1);
		aiWatchClosestEntityPlayer.setMutexBits(2);
		aiWatchClosestEntityGolem.setMutexBits(3);
		aiLookIdle.setMutexBits(4);

		this.tasks.addTask(0, aiSwimming);
		this.tasks.addTask(1, this.aiStatePanic);
		this.tasks.addTask(2, this.aiStateSit);
		this.tasks.addTask(3, this.aiStateTrade);
		this.tasks.addTask(4, aiStoreChest);
		this.tasks.addTask(5, aiCollectItem);
		this.tasks.addTask(6, aiFollowOwner);
		this.tasks.addTask(7, aiWander);
		this.tasks.addTask(8, aiWatchClosestEntityPlayer);
		this.tasks.addTask(8, aiWatchClosestEntityGolem);
		this.tasks.addTask(9, aiLookIdle);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ENTITY_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ENTITY_SPEED);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(ARM_COLOR, Integer.valueOf(EnumDyeColor.WHITE.getDyeDamage()));
		this.getDataManager().register(COVER_OPEN, Byte.valueOf((byte) 0));
		this.getDataManager().register(OWNER_UUID, Optional.<UUID> absent());
		this.getDataManager().register(TAMED, Byte.valueOf((byte) 0));
		this.getDataManager().register(FOLLOW, Byte.valueOf((byte) 0));
		this.getDataManager().register(STATE_PANIC, Byte.valueOf((byte) 0));
		this.getDataManager().register(STATE_SIT, Byte.valueOf((byte) 0));
		this.getDataManager().register(STATE_TRADE, Byte.valueOf((byte) 0));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setTag(TAG_INVENTORY, this.getInventoryMain().writeInventoryToNBT());

		compound.setTag(TAG_EQUIPMENTS, this.getInventoryEquipments().writeInventoryToNBT());

		compound.setByte(TAG_ARM_COLOR, (byte) this.getArmColor().getDyeDamage());

		if (this.getOwnerUUID() == null)
		{
			compound.setString(TAG_OWNER_UUID, "");
		}
		else
		{
			compound.setString(TAG_OWNER_UUID, this.getOwnerUUID().toString());
		}

		compound.setBoolean(TAG_FOLLOW, this.isFollow());

		compound.setBoolean(TAG_STATE_SIT, this.isSit());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		this.getInventoryMain().readInventoryFromNBT(compound.getTagList(TAG_INVENTORY, 10));

		this.getInventoryEquipments().readInventoryFromNBT(compound.getTagList(TAG_EQUIPMENTS, 10));

		this.setArmColor(EnumDyeColor.byDyeDamage(compound.getByte(TAG_ARM_COLOR)));

		this.setCoverOpen(false);

		String ownerUUID;

		if (compound.hasKey(TAG_OWNER_UUID))
		{
			ownerUUID = compound.getString(TAG_OWNER_UUID);
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
				this.setTamed(true);
			}
			catch (Throwable throwable)
			{
				this.setTamed(false);
			}
		}

		this.setFollow(compound.getBoolean(TAG_FOLLOW));

		this.setSit(compound.getBoolean(TAG_STATE_SIT));

		this.setPanic(false);

		this.setTrade(false);

		this.setSitting(this.isSit());

		this.setPanicing(0);

		this.setTrading(null);
	}

	@Override
	public double getYOffset()
	{
		Entity ridingEntity = this.getRidingEntity();

		if (ridingEntity != null)
		{
			return (super.getYOffset() - ridingEntity.getYOffset());
		}

		return super.getYOffset();
	}

	@Override
	public double getMountedYOffset()
	{
		if (this.isSit())
		{
			return ((double) this.height * 0.45);
		}
		else
		{
			return ((double) this.height * 0.80);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damagesource)
	{
		return SoundEvents.BLOCK_WOOD_HIT;
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return SoundEvents.ENTITY_ITEM_BREAK;
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player)
	{
		return (this.isTamed() && this.isOwner(player));
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
	{
		ItemStack itemStack;

		switch (slotIn)
		{
			case HEAD :

				itemStack = this.getInventoryEquipments().getHeadItem();

				break;

			case MAINHAND :

				itemStack = this.getInventoryEquipments().getMainhandItem();

				break;

			case OFFHAND :

				itemStack = this.getInventoryEquipments().getOffhandItem();

				break;

			default :

				itemStack = ItemStack.EMPTY;

				break;
		}

		return itemStack;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
	{
		switch (slotIn)
		{
			case HEAD :

				this.getInventoryEquipments().setInventorySlotContents(0, stack);

				break;

			case MAINHAND :

				this.getInventoryEquipments().setInventorySlotContents(1, stack);

				break;

			case OFFHAND :

				this.getInventoryEquipments().setInventorySlotContents(2, stack);

				break;

			default :

				// none

				break;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.getRidingEntity() instanceof EntityLivingBase)
		{
			return false;
		}

		World world = this.getEntityWorld();
		boolean isServerWorld = !world.isRemote;

		if (this.isEquipHelmet())
		{
			ItemStack stackHelmet = this.getInventoryEquipments().getHeadItem();
			ItemChastHelmet itemChastHelmet = (ItemChastHelmet) stackHelmet.getItem();

			if (!itemChastHelmet.onDmageOwner(source, amount, stackHelmet, this))
			{
				return false;
			}
		}

		if (this.isPanic())
		{
			if (this.isEquipHelmet())
			{
				if (isServerWorld)
				{
					this.playSound(SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.5F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);

					ChastMobPackets.DISPATCHER.sendToAll(new MessageParticleEntity(this, 1));
				}

				return false;
			}
		}
		else
		{
			if ((source.getTrueSource() instanceof EntityLivingBase) && isServerWorld)
			{
				this.setPanicing((int) amount);
			}
		}

		return super.attackEntityFrom(source, amount);
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		World world = this.getEntityWorld();
		boolean isServerWorld = !world.isRemote;

		if (isServerWorld)
		{
			EntityLivingBase owner = this.getOwner();

			if (owner instanceof EntityPlayerMP)
			{
				((EntityPlayerMP) owner).sendMessage(new TextComponentTranslation("entity.chast.goodbye", new Object[]
				{
						TextFormatting.ITALIC.BOLD + this.getName(),
						TextFormatting.ITALIC.BOLD + owner.getName(),
				}));
			}

			Block.spawnAsEntity(world, this.getPosition(), new ItemStack(Blocks.CHEST));

			InventoryHelper.dropInventoryItems(world, this, this.getInventoryMain());

			InventoryHelper.dropInventoryItems(world, this, this.getInventoryEquipments());
		}

		super.onDeath(cause);
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		if (this.isPanic())
		{
			return false;
		}

		if (this.isTamed())
		{
			if (!this.isOwner(player) || (hand == EnumHand.OFF_HAND))
			{
				return false;
			}

			boolean isServerWorld = !this.getEntityWorld().isRemote;
			ItemStack stackHeldItem = player.getHeldItem(hand);

			for (Entity passenger : this.getPassengers())
			{
				if (passenger.isEntityAlive())
				{
					if (isServerWorld)
					{
						passenger.dismountRidingEntity();
					}

					return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_ITEM_PICKUP);
				}
			}

			if (!stackHeldItem.isEmpty())
			{
				if (stackHeldItem.getItem() == Items.DYE)
				{
					EnumDyeColor enumDyeColor = EnumDyeColor.byDyeDamage(stackHeldItem.getMetadata());

					if (enumDyeColor != this.getArmColor())
					{
						if (isServerWorld)
						{
							this.setArmColor(enumDyeColor);

							if (!player.capabilities.isCreativeMode)
							{
								stackHeldItem.shrink(1);
							}
						}

						return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_ITEM_PICKUP);
					}
				}

				if (stackHeldItem.interactWithEntity(player, this, hand))
				{
					return this.onSuccessProcessInteract(player, (SoundEvent) null);
				}
			}

			if (player.isSneaking())
			{
				if (isServerWorld)
				{
					this.setSitting(!this.isSit());
				}

				return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_ITEM_PICKUP);
			}
			else
			{
				if (isServerWorld)
				{
					player.openGui(ChastMob.instance, ChastMobGuis.ID_CHAST_INVENTORY, this.getEntityWorld(), this.getEntityId(), 0, 0);
				}

				return this.onSuccessProcessInteract(player, (SoundEvent) null);
			}
		}
		else
		{
			this.onSpawnByPlayer(player);

			return this.onSuccessProcessInteract(player, (SoundEvent) null);
		}
	}

	@Override
	public void updateRidden()
	{
		super.updateRidden();

		Entity ridingEntity = this.getRidingEntity();

		if (ridingEntity instanceof EntityLivingBase)
		{
			this.renderYawOffset = ((EntityLivingBase) ridingEntity).renderYawOffset;
		}
	}

	@Override
	public void updatePassenger(Entity passenger)
	{
		super.updatePassenger(passenger);

		if (this.isPanic() && !this.getEntityWorld().isRemote)
		{
			passenger.dismountRidingEntity();
		}

		if (!passenger.getClass().equals(EntityOcelot.class))
		{
			this.setSit(false);
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.ticksExisted < (20 * 5))
		{
			EntityLivingBase ownerEntity = this.getOwner();

			if ((ownerEntity != null) && (ownerEntity.getDistanceSq(this) < 16.0D))
			{
				this.getLookHelper().setLookPositionWithEntity(ownerEntity, this.getHorizontalFaceSpeed(), this.getVerticalFaceSpeed());
			}
		}

		boolean isCoverOpen = this.isCoverOpen();
		this.prevLidAngle = this.lidAngle;

		if (isCoverOpen && (this.lidAngle == 0.0F))
		{
			this.setCoverOpen(true);

			this.playSound(SoundEvents.BLOCK_CHEST_OPEN, 0.5F, this.rand.nextFloat() * 0.1F + 0.9F);
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
				this.setCoverOpen(false);

				this.playSound(SoundEvents.BLOCK_CHEST_CLOSE, 0.5F, this.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F)
			{
				this.lidAngle = 0.0F;
			}
		}

		if (this.isEquipHelmet())
		{
			ItemStack stackHelmet = this.getInventoryEquipments().getHeadItem();
			ItemChastHelmet itemChastHelmet = (ItemChastHelmet) stackHelmet.getItem();

			itemChastHelmet.onUpdateOwner(stackHelmet, this);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	@SideOnly(Side.CLIENT)
	public float getCoverRotateAngleX(float partialTickTime)
	{
		return ((this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * partialTickTime) * 0.5F * (float) Math.PI);
	}

	public EnumDyeColor getArmColor()
	{
		return EnumDyeColor.byDyeDamage(((Integer) this.getDataManager().get(ARM_COLOR)).intValue() & 15);
	}

	public void setArmColor(EnumDyeColor enumDyeColor)
	{
		this.getDataManager().set(ARM_COLOR, Integer.valueOf(enumDyeColor.getDyeDamage()));
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

	public boolean isOwner(Entity owner)
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
	public EntityLivingBase getOwner()
	{
		try
		{
			UUID uuid = this.getOwnerUUID();

			return (uuid == null) ? null : this.getEntityWorld().getPlayerEntityByUUID(uuid);
		}
		catch (IllegalArgumentException e)
		{
			return null;
		}
	}

	public void setOwnerUUID(@Nullable UUID ownerUUID)
	{
		this.getDataManager().set(OWNER_UUID, Optional.fromNullable(ownerUUID));
	}

	public boolean isTamed()
	{
		return (((Byte) this.getDataManager().get(TAMED)).byteValue() & 1) != 0;
	}

	public void setTamed(boolean isTamed)
	{
		byte b0 = ((Byte) this.getDataManager().get(TAMED)).byteValue();

		if (isTamed)
		{
			this.getDataManager().set(TAMED, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(TAMED, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isFollow()
	{
		return (((Byte) this.getDataManager().get(FOLLOW)).byteValue() & 1) != 0;
	}

	public void setFollow(boolean isFollow)
	{
		byte b0 = ((Byte) this.getDataManager().get(FOLLOW)).byteValue();

		if (isFollow)
		{
			this.getDataManager().set(FOLLOW, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(FOLLOW, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isPanic()
	{
		return (((Byte) this.getDataManager().get(STATE_PANIC)).byteValue() & 1) != 0;
	}

	public void setPanic(boolean isPanic)
	{
		byte b0 = ((Byte) this.getDataManager().get(STATE_PANIC)).byteValue();

		if (isPanic)
		{
			this.getDataManager().set(STATE_PANIC, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(STATE_PANIC, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isSit()
	{
		return (((Byte) this.getDataManager().get(STATE_SIT)).byteValue() & 1) != 0;
	}

	public void setSit(boolean isSit)
	{
		byte b0 = ((Byte) this.getDataManager().get(STATE_SIT)).byteValue();

		if (isSit)
		{
			this.getDataManager().set(STATE_SIT, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(STATE_SIT, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isTrade()
	{
		return (((Byte) this.getDataManager().get(STATE_TRADE)).byteValue() & 1) != 0;
	}

	public void setTrade(boolean isTrade)
	{
		byte b0 = ((Byte) this.getDataManager().get(STATE_TRADE)).byteValue();

		if (isTrade)
		{
			this.getDataManager().set(STATE_TRADE, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(STATE_TRADE, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public InventoryChastMain getInventoryMain()
	{
		if (this.inventoryMain == null)
		{
			this.inventoryMain = new InventoryChastMain(this);
		}

		return this.inventoryMain;
	}

	public InventoryChastEquipments getInventoryEquipments()
	{
		if (this.inventoryEquipments == null)
		{
			this.inventoryEquipments = new InventoryChastEquipments(this);
		}

		return this.inventoryEquipments;
	}

	public boolean isEquipHelmet()
	{
		return !this.getInventoryEquipments().getHeadItem().isEmpty();
	}

	public ChastMode getMode()
	{
		if (this.isFollow())
		{
			return ChastMode.FOLLOW;
		}
		else
		{
			return ChastMode.FREEDOM;
		}
	}

	public ChastCondition getCondition()
	{
		int health = (int) this.getHealth();
		int healthMax = (int) this.getMaxHealth();

		ChastCondition condition = ChastCondition.FINE;

		if (health < (healthMax / 2))
		{
			condition = ChastCondition.HURT;

			if (health < (healthMax / 4))
			{
				condition = ChastCondition.DYING;
			}
		}

		return condition;
	}

	public void setSitting(boolean isSit)
	{
		if (this.aiStateSit != null)
		{
			if (isSit)
			{
				this.aiStateSit.startTask();
			}
			else
			{
				this.aiStateSit.stopTask();
			}
		}
	}

	public void setPanicing(int timeCount)
	{
		if (this.aiStatePanic != null)
		{
			if (0 < timeCount)
			{
				this.aiStatePanic.startTask(timeCount);

				this.setSitting(false);
				this.setTrading(null);
			}
			else
			{
				this.aiStatePanic.stopTask();
			}
		}
	}

	public void setTrading(@Nullable Entity trader)
	{
		if (this.aiStateTrade != null)
		{
			if (trader != null)
			{
				this.aiStateTrade.startTask(trader);
			}
			else
			{
				this.aiStateTrade.stopTask();
			}
		}
	}

	public void onSpawnByPlayer(EntityPlayer player)
	{
		if (!player.getEntityWorld().isRemote)
		{
			this.setTamed(true);
			this.setOwnerUUID(player.getUniqueID());
			this.setFollow(true);
			this.setSit(false);

			player.sendMessage(new TextComponentTranslation("entity.chast.thanks", new Object[]
			{
					TextFormatting.ITALIC.BOLD + this.getName(),
					TextFormatting.ITALIC.BOLD + player.getName(),
			}));
		}

		ChastMobPackets.DISPATCHER.sendToAll(new MessageParticleEntity(this, 0));

		this.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
	}

	private boolean onSuccessProcessInteract(EntityPlayer player, @Nullable SoundEvent soundEvent)
	{
		player.swingArm(EnumHand.MAIN_HAND);

		if (soundEvent != null)
		{
			this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
		}

		return true;
	}

}

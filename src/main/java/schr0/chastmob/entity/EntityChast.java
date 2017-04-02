package schr0.chastmob.entity;

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
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.api.ItemChastHelmet;
import schr0.chastmob.entity.ai.ChastAIMode;
import schr0.chastmob.entity.ai.EntityAIChastCollectItem;
import schr0.chastmob.entity.ai.EntityAIChastFollowOwner;
import schr0.chastmob.entity.ai.EntityAIChastGoHome;
import schr0.chastmob.entity.ai.EntityAIChastKnockback;
import schr0.chastmob.entity.ai.EntityAIChastSit;
import schr0.chastmob.entity.ai.EntityAIChastStoreChest;
import schr0.chastmob.entity.ai.EntityAIChastTrade;
import schr0.chastmob.entity.ai.EntityAIChastWander;
import schr0.chastmob.init.ChastMobGui;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.init.ChastMobLang;
import schr0.chastmob.init.ChastMobNBTs;
import schr0.chastmob.init.ChastMobPacket;
import schr0.chastmob.inventory.InventoryChastEquipment;
import schr0.chastmob.inventory.InventoryChastMain;
import schr0.chastmob.item.ItemModePatrol;
import schr0.chastmob.packet.particleentity.MessageParticleEntity;

public class EntityChast extends EntityGolem
{

	public static void registerFixesChast(DataFixer p_189790_0_)
	{
		EntityLiving.registerFixesMob(p_189790_0_, EntityChast.class);
	}

	private static final DataParameter<Integer> ARM_COLOR = EntityDataManager.<Integer> createKey(EntityChast.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> COVER_OPEN = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.<Optional<UUID>> createKey(EntityChast.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Byte> OWNER_TAME = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> OWNER_FOLLOW = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE_KNOCKBACK = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE_SIT = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE_TRADE = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);

	private EntityAIChastKnockback aiChastKnockback;
	private EntityAIChastSit aiChastSit;
	private EntityAIChastTrade aiChastTrade;
	private InventoryChastMain inventoryChastMain;
	private InventoryChastEquipment inventoryChastEquipment;
	private float lidAngle;
	private float prevLidAngle;

	public EntityChast(World worldIn)
	{
		super(worldIn);
		this.setSize(0.9F, 1.5F);
	}

	@Override
	protected void initEntityAI()
	{
		super.initEntityAI();

		double speed = 1.25D;
		int distance = 5;

		// BASE
		EntityAIBase aiSwimming = new EntityAISwimming(this);
		this.aiChastKnockback = new EntityAIChastKnockback(this, (speed * 2), distance);
		this.aiChastSit = new EntityAIChastSit(this);
		this.aiChastTrade = new EntityAIChastTrade(this);
		// MODE
		EntityAIBase aiChastStoreChest = new EntityAIChastStoreChest(this, speed, distance);
		EntityAIBase aiChastCollectItem = new EntityAIChastCollectItem(this, speed, (double) distance);
		EntityAIBase aiChastGoHome = new EntityAIChastGoHome(this, speed, distance);
		EntityAIBase aiChastFollowOwner = new EntityAIChastFollowOwner(this, speed, (double) distance / 2.0D);
		// WONDER
		EntityAIBase aiChastWander = new EntityAIChastWander(this, speed, distance);
		EntityAIBase aiWatchClosestEntityPlayer = new EntityAIWatchClosest(this, EntityPlayer.class, (float) distance);
		EntityAIBase aiWatchClosestEntityGolem = new EntityAIWatchClosest(this, EntityGolem.class, (float) distance);
		EntityAIBase aiLookIdle = new EntityAILookIdle(this);

		aiSwimming.setMutexBits(0);
		this.aiChastKnockback.setMutexBits(1);
		this.aiChastSit.setMutexBits(1);
		this.aiChastTrade.setMutexBits(1);
		aiChastStoreChest.setMutexBits(1);
		aiChastCollectItem.setMutexBits(1);
		aiChastGoHome.setMutexBits(1);
		aiChastFollowOwner.setMutexBits(1);
		aiChastWander.setMutexBits(1);
		aiWatchClosestEntityPlayer.setMutexBits(2);
		aiWatchClosestEntityGolem.setMutexBits(3);
		aiLookIdle.setMutexBits(4);

		this.tasks.addTask(0, aiSwimming);
		this.tasks.addTask(1, this.aiChastKnockback);
		this.tasks.addTask(2, this.aiChastSit);
		this.tasks.addTask(3, this.aiChastTrade);
		this.tasks.addTask(4, aiChastStoreChest);
		this.tasks.addTask(5, aiChastCollectItem);
		this.tasks.addTask(6, aiChastGoHome);
		this.tasks.addTask(6, aiChastFollowOwner);
		this.tasks.addTask(7, aiChastWander);
		this.tasks.addTask(8, aiWatchClosestEntityPlayer);
		this.tasks.addTask(8, aiWatchClosestEntityGolem);
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
		this.getDataManager().register(STATE_KNOCKBACK, Byte.valueOf((byte) 0));
		this.getDataManager().register(STATE_SIT, Byte.valueOf((byte) 0));
		this.getDataManager().register(STATE_TRADE, Byte.valueOf((byte) 0));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setTag(ChastMobNBTs.ENTITY_CHAST_INVENTORY, this.getInventoryChastMain().writeInventoryToNBT());

		compound.setTag(ChastMobNBTs.ENTITY_CHAST_EQUIPMENT, this.getInventoryChastEquipment().writeInventoryToNBT());

		compound.setByte(ChastMobNBTs.ENTITY_CHAST_ARM_COLOR, (byte) this.getArmColor().getDyeDamage());

		if (this.getOwnerUUID() == null)
		{
			compound.setString(ChastMobNBTs.ENTITY_CHAST_OWNER_UUID, "");
		}
		else
		{
			compound.setString(ChastMobNBTs.ENTITY_CHAST_OWNER_UUID, this.getOwnerUUID().toString());
		}

		compound.setBoolean(ChastMobNBTs.ENTITY_CHAST_OWNER_FOLLOW, this.isOwnerFollow());

		compound.setBoolean(ChastMobNBTs.ENTITY_CHAST_STATE_SIT, this.isStateSit());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		this.getInventoryChastMain().readInventoryFromNBT(compound.getTagList(ChastMobNBTs.ENTITY_CHAST_INVENTORY, 10));

		this.getInventoryChastEquipment().readInventoryFromNBT(compound.getTagList(ChastMobNBTs.ENTITY_CHAST_EQUIPMENT, 10));

		this.setArmColor(EnumDyeColor.byDyeDamage(compound.getByte(ChastMobNBTs.ENTITY_CHAST_ARM_COLOR)));

		this.setCoverOpen(false);

		String ownerUUID;

		if (compound.hasKey(ChastMobNBTs.ENTITY_CHAST_OWNER_UUID))
		{
			ownerUUID = compound.getString(ChastMobNBTs.ENTITY_CHAST_OWNER_UUID);
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
			catch (Throwable throwable)
			{
				this.setOwnerTame(false);
			}
		}

		this.setOwnerFollow(compound.getBoolean(ChastMobNBTs.ENTITY_CHAST_OWNER_FOLLOW));

		this.setAIKnockbacking(0);

		this.setStateKnockback(false);

		this.setAISitting(compound.getBoolean(ChastMobNBTs.ENTITY_CHAST_STATE_SIT));

		this.setStateSit(compound.getBoolean(ChastMobNBTs.ENTITY_CHAST_STATE_SIT));

		this.setAITrading(null);

		this.setStateTrade(false);
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
	protected SoundEvent getHurtSound()
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
		return (this.isOwnerTame() && this.isOwnerEntity(player));
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
	{
		ItemStack itemStack;

		switch (slotIn)
		{
			case HEAD :

				itemStack = this.getInventoryChastEquipment().getHeadItem();
				break;

			case MAINHAND :

				itemStack = this.getInventoryChastEquipment().getMainhandItem();
				break;

			case OFFHAND :

				itemStack = this.getInventoryChastEquipment().getOffhandItem();
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

				this.getInventoryChastEquipment().setInventorySlotContents(0, stack);
				break;

			case MAINHAND :

				this.getInventoryChastEquipment().setInventorySlotContents(1, stack);
				break;

			case OFFHAND :

				this.getInventoryChastEquipment().setInventorySlotContents(2, stack);
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
			ItemStack stackHelmet = this.getInventoryChastEquipment().getHeadItem();

			if (!((ItemChastHelmet) stackHelmet.getItem()).onDmageOwner(source, amount, stackHelmet, this))
			{
				return false;
			}
		}

		if (this.isStateKnockback())
		{
			if (this.isEquipHelmet())
			{
				if (isServerWorld)
				{
					this.playSound(SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.5F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);

					ChastMobPacket.DISPATCHER.sendToAll(new MessageParticleEntity(this, 1));
				}

				return false;
			}
		}
		else
		{
			if ((source.getSourceOfDamage() instanceof EntityLivingBase) && isServerWorld)
			{
				this.setAIKnockbacking((int) amount);
			}
		}

		return super.attackEntityFrom(source, amount);
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		World world = this.getEntityWorld();

		if (!world.isRemote)
		{
			EntityLivingBase ownerEntity = this.getOwnerEntity();

			if (ownerEntity instanceof EntityPlayerMP)
			{
				((EntityPlayerMP) ownerEntity).sendMessage(new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_GOODBYE, new Object[]
				{
						TextFormatting.ITALIC.BOLD + this.getName(),
						TextFormatting.ITALIC.BOLD + ownerEntity.getName(),
				}));
			}

			Block.spawnAsEntity(world, this.getPosition(), new ItemStack(Blocks.CHEST));

			InventoryHelper.dropInventoryItems(world, this, this.getInventoryChastMain());

			InventoryHelper.dropInventoryItems(world, this, this.getInventoryChastEquipment());
		}

		super.onDeath(cause);
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		if (this.isStateKnockback())
		{
			return false;
		}

		if (this.isOwnerTame())
		{
			if (!this.isOwnerEntity(player) || (hand == EnumHand.OFF_HAND))
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

			if (ChastMobHelper.isNotEmptyItemStack(stackHeldItem))
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
					this.setAISitting(!this.isStateSit());
				}

				return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_ITEM_PICKUP);
			}
			else
			{
				if (isServerWorld)
				{
					player.openGui(ChastMob.instance, ChastMobGui.ID_CHAST_INVENTORY, this.getEntityWorld(), this.getEntityId(), 0, 0);
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

		if (this.isStateKnockback() && !this.getEntityWorld().isRemote)
		{
			passenger.dismountRidingEntity();
		}

		if (!passenger.getClass().equals(EntityOcelot.class))
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

		if (this.ticksExisted < (20 * 5))
		{
			EntityLivingBase ownerEntity = this.getOwnerEntity();

			if ((ownerEntity != null) && (ownerEntity.getDistanceToEntity(this) < 16.0D))
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
			ItemStack stackHelmet = this.getInventoryChastEquipment().getHeadItem();

			((ItemChastHelmet) stackHelmet.getItem()).onUpdateOwner(stackHelmet, this);
		}
	}

	// TODO /* ======================================== DATA_MANAGER START =====================================*/

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
		return (((Byte) this.getDataManager().get(OWNER_FOLLOW)).byteValue() & 1) != 0;
	}

	public void setOwnerFollow(boolean isOwnerFollow)
	{
		byte b0 = ((Byte) this.getDataManager().get(OWNER_FOLLOW)).byteValue();

		if (isOwnerFollow)
		{
			this.getDataManager().set(OWNER_FOLLOW, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(OWNER_FOLLOW, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isStateKnockback()
	{
		return (((Byte) this.getDataManager().get(STATE_KNOCKBACK)).byteValue() & 1) != 0;
	}

	public void setStateKnockback(boolean isStatePanic)
	{
		byte b0 = ((Byte) this.getDataManager().get(STATE_KNOCKBACK)).byteValue();

		if (isStatePanic)
		{
			this.getDataManager().set(STATE_KNOCKBACK, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(STATE_KNOCKBACK, Byte.valueOf((byte) (b0 & -2)));
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

	// TODO /* ======================================== MOD START =====================================*/

	public static enum Condition
	{

		FINE,
		HURT,
		DYING,

	}

	@SideOnly(Side.CLIENT)
	public float getCoverRotateAngleX(float partialTickTime)
	{
		return ((this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * partialTickTime) * 0.5F * (float) Math.PI);
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

	public EntityChast.Condition getCondition()
	{
		int health = (int) this.getHealth();
		int healthMax = (int) this.getMaxHealth();

		EntityChast.Condition condition = EntityChast.Condition.FINE;

		if (health < (healthMax / 2))
		{
			condition = EntityChast.Condition.HURT;

			if (health < (healthMax / 4))
			{
				condition = EntityChast.Condition.DYING;
			}
		}

		return condition;
	}

	public InventoryChastMain getInventoryChastMain()
	{
		if (this.inventoryChastMain == null)
		{
			this.inventoryChastMain = new InventoryChastMain(this);
		}

		return this.inventoryChastMain;
	}

	public InventoryChastEquipment getInventoryChastEquipment()
	{
		if (this.inventoryChastEquipment == null)
		{
			this.inventoryChastEquipment = new InventoryChastEquipment(this);
		}

		return this.inventoryChastEquipment;
	}

	public boolean isEquipHelmet()
	{
		return ChastMobHelper.isNotEmptyItemStack(this.getInventoryChastEquipment().getHeadItem());
	}

	public void onSpawnByPlayer(EntityPlayer player)
	{
		if (!player.getEntityWorld().isRemote)
		{
			this.setOwnerTame(true);
			this.setOwnerUUID(player.getUniqueID());
			this.setOwnerFollow(true);
			this.setAISitting(false);

			player.sendMessage(new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_THANKS, new Object[]
			{
					TextFormatting.ITALIC.BOLD + this.getName(),
					TextFormatting.ITALIC.BOLD + player.getName(),
			}));
		}

		ChastMobPacket.DISPATCHER.sendToAll(new MessageParticleEntity(this, 0));

		this.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
	}

	public ChastAIMode getAIMode()
	{
		ItemStack stackModeItem = this.getInventoryChastEquipment().getModeItem();

		if (this.isOwnerFollow())
		{
			return ChastAIMode.FOLLOW;
		}
		else
		{
			if (stackModeItem.getItem() == ChastMobItems.MODE_PATROL)
			{
				if (((ItemModePatrol) stackModeItem.getItem()).hasHomeChest(stackModeItem))
				{
					return ChastAIMode.PATROL;
				}
			}

			return ChastAIMode.FREEDOM;
		}
	}

	public void setAIKnockbacking(int timeCounter)
	{
		if (this.aiChastKnockback != null)
		{
			this.aiChastKnockback.setKnockbacking(timeCounter);

			if (0 < timeCounter)
			{
				this.aiChastSit.setSitting(false);
				this.aiChastTrade.setTrading(null);
			}
		}
	}

	public void setAISitting(boolean isSitting)
	{
		if (this.aiChastSit != null)
		{
			this.aiChastSit.setSitting(isSitting);
		}
	}

	public void setAITrading(@Nullable EntityPlayer tradePlayer)
	{
		if (this.aiChastTrade != null)
		{
			this.aiChastTrade.setTrading(tradePlayer);
		}
	}

	private boolean onSuccessProcessInteract(EntityPlayer player, @Nullable SoundEvent soundEvent)
	{
		player.swingArm(EnumHand.MAIN_HAND);

		if (soundEvent != null)
		{
			this.playSound(soundEvent, 1.0F, 1.0F);
		}

		return true;
	}

}

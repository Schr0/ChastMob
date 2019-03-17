package schr0.chastmob.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityGolem;
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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.api.ItemChastHelmet;
import schr0.chastmob.entity.ai.EntityAIChastCollectItem;
import schr0.chastmob.entity.ai.EntityAIChastFollowOwner;
import schr0.chastmob.entity.ai.EntityAIChastGoHome;
import schr0.chastmob.entity.ai.EntityAIChastPanic;
import schr0.chastmob.entity.ai.EntityAIChastSit;
import schr0.chastmob.entity.ai.EntityAIChastStoreChest;
import schr0.chastmob.entity.ai.EntityAIChastTrade;
import schr0.chastmob.entity.ai.EntityAIChastWander;
import schr0.chastmob.entity.ai.EntityAIChastWatchClosest;
import schr0.chastmob.init.ChastMobGuis;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.inventory.InventoryChastEquipments;
import schr0.chastmob.inventory.InventoryChastMain;
import schr0.chastmob.item.ItemHomeMap;
import schr0.chastmob.util.ChastMobParticles;

public class EntityChast extends EntityGolem
{

	private static final double MAX_HEALTH = 20.0D;
	private static final double MOVEMENT_SPEED = 0.25D;
	private static final float SIZE_WIDTH = 0.9F;
	private static final float SIZE_HEIGHT = 1.5F;
	private static final double SIT_MOUNTED_Y_OFFSET = 0.6D;

	private static final String TAG = ChastMob.MOD_ID + "." + "entity_chast" + ".";
	private static final String TAG_INVENTORY = TAG + "inventory";
	private static final String TAG_EQUIPMENTS = TAG + "equipments";
	private static final String TAG_HOME_CHEST_POS = TAG + "home_chest_pos";
	private static final String TAG_OWNER_UUID = TAG + "owner_uuid";
	private static final String TAG_ARM_COLOR = TAG + "arm_color";
	private static final String TAG_PANIC_TIME = TAG + "panic_time";
	private static final String TAG_FOLLOW = TAG + "follow";
	private static final String TAG_SIT = TAG + "sit";

	private static final DataParameter<BlockPos> HOME_CHEST_POS = EntityDataManager.<BlockPos> createKey(EntityChast.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.<Optional<UUID>> createKey(EntityChast.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Integer> ARM_COLOR = EntityDataManager.<Integer> createKey(EntityChast.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> COVER_OPEN = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> PANIC_TIME = EntityDataManager.<Integer> createKey(EntityChast.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> TAMED = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> FOLLOW = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> SIT = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> TRADE = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);

	private EntityAIChastSit aiSit;
	private EntityAIChastTrade aiTrade;
	private InventoryChastMain inventoryMain;
	private InventoryChastEquipments inventoryEquipments;
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
		EntityAIBase aiPanic = new EntityAIChastPanic(this);
		this.aiTrade = new EntityAIChastTrade(this);
		this.aiSit = new EntityAIChastSit(this);
		EntityAIBase aiGoHome = new EntityAIChastGoHome(this);
		EntityAIBase aiStoreChest = new EntityAIChastStoreChest(this);
		EntityAIBase aiFollowOwner = new EntityAIChastFollowOwner(this);
		EntityAIBase aiCollectItem = new EntityAIChastCollectItem(this);
		EntityAIBase aiWander = new EntityAIChastWander(this);
		EntityAIBase aiWatchClosest = new EntityAIChastWatchClosest(this);

		aiSwimming.setMutexBits(0);
		aiPanic.setMutexBits(1);
		this.aiTrade.setMutexBits(1);
		this.aiSit.setMutexBits(1);
		aiGoHome.setMutexBits(1);
		aiStoreChest.setMutexBits(1);
		aiFollowOwner.setMutexBits(1);
		aiCollectItem.setMutexBits(1);
		aiWander.setMutexBits(1);
		aiWatchClosest.setMutexBits(1);

		this.tasks.addTask(1, aiSwimming);
		this.tasks.addTask(2, aiPanic);
		this.tasks.addTask(3, this.aiTrade);
		this.tasks.addTask(4, this.aiSit);
		this.tasks.addTask(5, aiGoHome);
		this.tasks.addTask(6, aiStoreChest);
		this.tasks.addTask(7, aiCollectItem);
		this.tasks.addTask(8, aiFollowOwner);
		this.tasks.addTask(9, aiWander);
		this.tasks.addTask(10, aiWatchClosest);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(MOVEMENT_SPEED);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(HOME_CHEST_POS, BlockPos.ORIGIN);
		this.getDataManager().register(ARM_COLOR, Integer.valueOf(EnumDyeColor.WHITE.getDyeDamage()));
		this.getDataManager().register(COVER_OPEN, Byte.valueOf((byte) 0));
		this.getDataManager().register(PANIC_TIME, 0);
		this.getDataManager().register(OWNER_UUID, Optional.<UUID> absent());
		this.getDataManager().register(TAMED, Byte.valueOf((byte) 0));
		this.getDataManager().register(FOLLOW, Byte.valueOf((byte) 0));
		this.getDataManager().register(SIT, Byte.valueOf((byte) 0));
		this.getDataManager().register(TRADE, Byte.valueOf((byte) 0));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setTag(TAG_INVENTORY, this.getInventoryMain().writeInventoryToNBT());

		compound.setTag(TAG_EQUIPMENTS, this.getInventoryEquipments().writeInventoryToNBT());

		compound.setTag(TAG_HOME_CHEST_POS, this.newDoubleNBTList(new double[]
		{
				this.getHomeChestPosition().getX(), this.getHomeChestPosition().getY(), this.getHomeChestPosition().getZ()
		}));

		compound.setByte(TAG_ARM_COLOR, (byte) this.getArmColor().getDyeDamage());

		compound.setInteger(TAG_PANIC_TIME, this.getPanicTime());

		if (this.getOwnerUUID() == null)
		{
			compound.setString(TAG_OWNER_UUID, "");
		}
		else
		{
			compound.setString(TAG_OWNER_UUID, this.getOwnerUUID().toString());
		}

		compound.setBoolean(TAG_FOLLOW, this.isFollow());

		compound.setBoolean(TAG_SIT, this.isSit());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		this.getInventoryMain().readInventoryFromNBT(compound.getTagList(TAG_INVENTORY, 10));

		this.getInventoryEquipments().readInventoryFromNBT(compound.getTagList(TAG_EQUIPMENTS, 10));

		if (compound.hasKey(TAG_HOME_CHEST_POS))
		{
			NBTTagList nbttaglist = compound.getTagList(TAG_HOME_CHEST_POS, 6);

			if (nbttaglist.tagCount() == 3)
			{
				this.setHomeChestPosition(new BlockPos((int) nbttaglist.getDoubleAt(0), (int) nbttaglist.getDoubleAt(1), (int) nbttaglist.getDoubleAt(2)));
			}
		}

		this.setArmColor(EnumDyeColor.byDyeDamage(compound.getByte(TAG_ARM_COLOR)));

		this.setPanicTime(compound.getInteger(TAG_PANIC_TIME));

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

		this.setSitting(compound.getBoolean(TAG_SIT));

		this.setTrading((EntityPlayer) null);

		this.setCoverOpen(false);
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
			return ((double) this.height * SIT_MOUNTED_Y_OFFSET);
		}
		else
		{
			return super.getMountedYOffset();
		}
	}

	@Override
	public void updateRidden()
	{
		super.updateRidden();

		if (this.getRidingEntity() instanceof EntityLivingBase)
		{
			EntityLivingBase ridingEntity = (EntityLivingBase) this.getRidingEntity();

			this.renderYawOffset = ridingEntity.renderYawOffset;
			this.rotationYaw = ridingEntity.rotationYaw;
			this.prevRotationYaw = this.rotationYaw;
			this.rotationPitch = ridingEntity.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.renderYawOffset = this.rotationYaw;
			this.rotationYawHead = this.rotationYaw;
		}
	}

	@Override
	public void updatePassenger(Entity passenger)
	{
		super.updatePassenger(passenger);

		if (!this.getEntityWorld().isRemote && this.isPanic())
		{
			passenger.dismountRidingEntity();
		}
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

		if (this.isDefense())
		{
			if (isServerWorld)
			{
				ChastMobParticles.spawnParticleDefense(this);

				this.playSound(SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.5F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
			}

			return false;
		}
		else
		{
			if (isServerWorld && (source.getTrueSource() instanceof EntityLivingBase))
			{
				int panicTime = (int) amount;
				panicTime = Math.max(panicTime, 6);
				panicTime = Math.min(panicTime, 12);

				this.setPanicTime((panicTime * 20));
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
		if (this.isPanic() || (hand == EnumHand.OFF_HAND))
		{
			return false;
		}

		if (this.isTamed())
		{
			if (!this.isOwner(player))
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

					return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_ITEM_PICKUP, false);
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

						return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_ITEM_PICKUP, true);
					}
				}

				if (stackHeldItem.getItem() == ChastMobItems.HOME_MAP)
				{
					ItemHomeMap itemHomeMap = (ItemHomeMap) stackHeldItem.getItem();

					if (itemHomeMap.hasPosition(stackHeldItem))
					{
						BlockPos homeChestPosition = itemHomeMap.getPosition(stackHeldItem);

						if (this.getHomeChestPosition() == BlockPos.ORIGIN)
						{
							this.setHomeChestPosition(homeChestPosition);

							if (isServerWorld)
							{
								player.sendMessage(new TextComponentTranslation("entity.chast.home.register", new Object[]
								{
										TextFormatting.ITALIC.BOLD + this.getName(),
										homeChestPosition.getX(),
										homeChestPosition.getY(),
										homeChestPosition.getZ(),
								}));
							}

							return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_ITEM_PICKUP, true);
						}
					}
					else
					{
						if (this.getHomeChestPosition() != BlockPos.ORIGIN)
						{
							this.setHomeChestPosition(BlockPos.ORIGIN);

							if (isServerWorld)
							{
								player.sendMessage(new TextComponentTranslation("entity.chast.home.reset", new Object[]
								{
										TextFormatting.ITALIC.BOLD + this.getName()
								}));
							}

							return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_ITEM_PICKUP, true);
						}
					}
				}
			}

			if (player.isSneaking())
			{
				if (isServerWorld)
				{
					this.setSitting(!this.isSit());
				}

				return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, true);
			}
			else
			{
				if (isServerWorld)
				{
					player.openGui(ChastMob.instance, ChastMobGuis.ID_CHAST_INVENTORY, this.getEntityWorld(), this.getEntityId(), 0, 0);
				}

				return this.onSuccessProcessInteract(player, (SoundEvent) null, false);
			}
		}
		else
		{
			this.onSpawnByPlayer(player);

			return this.onSuccessProcessInteract(player, (SoundEvent) null, false);
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.ticksExisted < 20)
		{
			EntityLivingBase owner = this.getOwner();

			if (owner != null)
			{
				this.getLookHelper().setLookPositionWithEntity(owner, this.getHorizontalFaceSpeed(), this.getVerticalFaceSpeed());
			}
		}

		if (this.isEquipHelmet())
		{
			ItemStack stackHelmet = this.getInventoryEquipments().getHeadItem();
			ItemChastHelmet itemChastHelmet = (ItemChastHelmet) stackHelmet.getItem();

			itemChastHelmet.onUpdateOwner(stackHelmet, this);
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
	}

	// TODO /* ======================================== DATAMANAGER START =====================================*/

	public BlockPos getHomeChestPosition()
	{
		return (BlockPos) this.getDataManager().get(HOME_CHEST_POS);
	}

	public void setHomeChestPosition(BlockPos pos)
	{
		this.getDataManager().set(HOME_CHEST_POS, pos);
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

	public void setCoverOpen(boolean flag)
	{
		byte b0 = ((Byte) this.getDataManager().get(COVER_OPEN)).byteValue();

		if (flag)
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

	public boolean isTamed()
	{
		return (((Byte) this.getDataManager().get(TAMED)).byteValue() == 1);
	}

	public void setTamed(boolean flag)
	{
		byte b0 = ((Byte) this.getDataManager().get(TAMED)).byteValue();

		if (flag)
		{
			this.getDataManager().set(TAMED, Byte.valueOf((byte) 1));
		}
		else
		{
			this.getDataManager().set(TAMED, Byte.valueOf((byte) 0));
		}
	}

	public boolean isFollow()
	{
		return (((Byte) this.getDataManager().get(FOLLOW)).byteValue() == 1);
	}

	public void setFollow(boolean flag)
	{
		byte b0 = ((Byte) this.getDataManager().get(FOLLOW)).byteValue();

		if (flag)
		{
			this.getDataManager().set(FOLLOW, Byte.valueOf((byte) 1));
		}
		else
		{
			this.getDataManager().set(FOLLOW, Byte.valueOf((byte) 0));
		}
	}

	public int getPanicTime()
	{
		return ((Integer) this.getDataManager().get(PANIC_TIME)).intValue();
	}

	public void setPanicTime(int amount)
	{
		amount = Math.max(amount, 0);

		this.getDataManager().set(PANIC_TIME, amount);
	}

	public boolean isPanic()
	{
		return (0 < this.getPanicTime());
	}

	public boolean isDefense()
	{
		return (this.isPanic() && this.isEquipHelmet());
	}

	public void shrinkPanicTime(int amount)
	{
		this.setPanicTime(this.getPanicTime() - amount);
	}

	public boolean isSit()
	{
		return (((Byte) this.getDataManager().get(SIT)).byteValue() == 1);
	}

	public void setSit(boolean flag)
	{
		byte b0 = ((Byte) this.getDataManager().get(SIT)).byteValue();

		if (flag)
		{
			this.getDataManager().set(SIT, Byte.valueOf((byte) 1));
		}
		else
		{
			this.getDataManager().set(SIT, Byte.valueOf((byte) 0));
		}
	}

	public boolean isTrade()
	{
		return (((Byte) this.getDataManager().get(TRADE)).byteValue() == 1);
	}

	public void setTrade(boolean flag)
	{
		byte b0 = ((Byte) this.getDataManager().get(TRADE)).byteValue();

		if (flag)
		{
			this.getDataManager().set(TRADE, Byte.valueOf((byte) 1));
		}
		else
		{
			this.getDataManager().set(TRADE, Byte.valueOf((byte) 0));
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	@SideOnly(Side.CLIENT)
	public float getCoverRotateAngleX(float partialTickTime)
	{
		return ((this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * partialTickTime) * 0.5F * (float) Math.PI);
	}

	public void setTrading(@Nullable EntityPlayer trader)
	{
		if (this.aiTrade != null)
		{
			this.aiTrade.setTrading(trader);
		}
	}

	public void setSitting(boolean isSitting)
	{
		if (this.aiSit != null)
		{
			this.aiSit.setSitting(isSitting);
		}
	}

	public boolean canBlockBeSeen(BlockPos pos)
	{
		World world = this.getEntityWorld();
		IBlockState state = world.getBlockState(pos);

		if (state == Blocks.AIR.getDefaultState())
		{
			return false;
		}

		Vec3d entityVec3d = new Vec3d(this.posX, this.posY + this.getEyeHeight(), this.posZ);
		Vec3d targetVec3d = new Vec3d(((double) pos.getX() + 0.5D), ((double) pos.getY() + (state.getCollisionBoundingBox(world, pos).minY + state.getCollisionBoundingBox(world, pos).maxY) * 0.9D), ((double) pos.getZ() + 0.5D));
		RayTraceResult rayTraceResult = world.rayTraceBlocks(entityVec3d, targetVec3d);

		if ((rayTraceResult != null) && (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK))
		{
			if (rayTraceResult.getBlockPos().equals(pos))
			{
				return true;
			}
		}

		return false;
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

	public ChastMode getMode()
	{
		if (this.isFollow())
		{
			return ChastMode.FOLLOW;
		}
		else
		{
			if (this.getCanSeeHomeChest(false) != null)
			{
				return ChastMode.PATROL;
			}

			return ChastMode.FREEDOM;
		}
	}

	@Nullable
	public TileEntityChest getCanSeeHomeChest(boolean isOpenChest)
	{
		World world = this.getEntityWorld();
		BlockPos centerPos = this.getCenterPosition();
		TileEntityChest homeChest = (TileEntityChest) world.getTileEntity(centerPos);

		if (homeChest != null)
		{
			boolean isLockedChest = (((BlockChest) world.getBlockState(centerPos).getBlock()).getLockableContainer(world, centerPos) == null);

			if (isOpenChest && isLockedChest)
			{
				return (TileEntityChest) null;
			}

			return homeChest;
		}

		return (TileEntityChest) null;
	}

	public BlockPos getCenterPosition()
	{
		if (this.isFollow())
		{
			EntityLivingBase owner = this.getOwner();

			if (this.isTamed() && (owner != null))
			{
				return owner.getPosition();
			}
		}
		else
		{
			BlockPos homeChestPosition = this.getHomeChestPosition();

			if (this.canBlockBeSeen(homeChestPosition))
			{
				return homeChestPosition;
			}
		}

		return this.getPosition();
	}

	public void onSpawnByPlayer(EntityPlayer player)
	{
		if (!player.getEntityWorld().isRemote)
		{
			this.setTamed(true);
			this.setOwnerUUID(player.getUniqueID());
			this.setFollow(true);

			player.sendMessage(new TextComponentTranslation("entity.chast.thanks", new Object[]
			{
					TextFormatting.ITALIC.BOLD + this.getName(),
					TextFormatting.ITALIC.BOLD + player.getName(),
			}));
		}

		ChastMobParticles.spawnParticleHeart(this);

		this.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
	}

	private boolean onSuccessProcessInteract(EntityPlayer player, @Nullable SoundEvent soundEvent, boolean isParticle)
	{
		player.swingArm(EnumHand.MAIN_HAND);

		if (soundEvent != null)
		{
			this.playSound(soundEvent, 0.5F, 1.0F);
		}

		if (!player.getEntityWorld().isRemote && isParticle)
		{
			ChastMobParticles.spawnParticleNote(this);
		}

		return true;
	}

}

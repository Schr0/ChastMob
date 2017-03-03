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
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.ai.EntityAIChastCollectItem;
import schr0.chastmob.entity.ai.EntityAIChastFollowOwner;
import schr0.chastmob.entity.ai.EntityAIChastGoHome;
import schr0.chastmob.entity.ai.EntityAIChastPanic;
import schr0.chastmob.entity.ai.EntityAIChastSit;
import schr0.chastmob.entity.ai.EntityAIChastStoreChest;
import schr0.chastmob.entity.ai.EntityAIChastTrade;
import schr0.chastmob.entity.ai.EntityAIChastWander;
import schr0.chastmob.entity.ai.EnumAIMode;
import schr0.chastmob.init.ChastMobEntitys;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.init.ChastMobLang;
import schr0.chastmob.init.ChastMobNBTs;
import schr0.chastmob.init.ChastMobPacket;
import schr0.chastmob.packet.MessageParticleEntity;

public class EntityChast extends EntityGolem
{

	public static void func_189790_b(DataFixer p_189790_0_)
	{
		EntityLiving.func_189752_a(p_189790_0_, ChastMobEntitys.NAME_CHAST);
	}

	private static final DataParameter<Integer> ARM_COLOR = EntityDataManager.<Integer> createKey(EntityChast.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> COVER_OPEN = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.<Optional<UUID>> createKey(EntityChast.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Byte> OWNER_TAME = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> AI_MODE = EntityDataManager.<Integer> createKey(EntityChast.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> STATE_PANIC = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE_SIT = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> STATE_TRADE = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);

	private EntityAIChastPanic aiChastPanic;
	private EntityAIChastSit aiChastSit;
	private EntityAIChastTrade aiChastTrade;
	private InventoryChast inventoryChast;
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

		// Base
		EntityAIBase aiSwimming = new EntityAISwimming(this);
		this.aiChastPanic = new EntityAIChastPanic(this, (speed * 2), distance);
		this.aiChastSit = new EntityAIChastSit(this);
		this.aiChastTrade = new EntityAIChastTrade(this);
		// Freedom || Follow
		EntityAIBase aiChastStoreChest = new EntityAIChastStoreChest(this, speed, distance);
		EntityAIBase aiChastCollectItem = new EntityAIChastCollectItem(this, speed, (double) distance);
		EntityAIBase aiChastFollowOwner = new EntityAIChastFollowOwner(this, speed, (double) distance / 2.0D);
		EntityAIBase aiChastGoHome = new EntityAIChastGoHome(this, speed, distance);
		// Wander
		EntityAIBase aiChastWander = new EntityAIChastWander(this, speed, distance);
		EntityAIBase aiWatchClosestPlayer = new EntityAIWatchClosest(this, EntityPlayer.class, (float) distance);
		EntityAIBase aiWatchClosestGolem = new EntityAIWatchClosest(this, EntityGolem.class, (float) distance);
		EntityAIBase aiLookIdle = new EntityAILookIdle(this);

		// Base
		aiSwimming.setMutexBits(0);
		this.aiChastPanic.setMutexBits(1);
		this.aiChastSit.setMutexBits(1);
		this.aiChastTrade.setMutexBits(1);
		// Freedom || Follow
		aiChastStoreChest.setMutexBits(1);
		aiChastCollectItem.setMutexBits(1);
		aiChastFollowOwner.setMutexBits(1);
		aiChastGoHome.setMutexBits(1);
		// Wander
		aiChastWander.setMutexBits(1);
		aiWatchClosestPlayer.setMutexBits(2);
		aiWatchClosestGolem.setMutexBits(2);
		aiLookIdle.setMutexBits(2);

		// Base
		this.tasks.addTask(0, aiSwimming);
		this.tasks.addTask(1, this.aiChastPanic);
		this.tasks.addTask(2, this.aiChastSit);
		this.tasks.addTask(3, this.aiChastTrade);
		// Freedom || Follow
		this.tasks.addTask(4, aiChastStoreChest);
		this.tasks.addTask(5, aiChastCollectItem);
		this.tasks.addTask(6, aiChastFollowOwner);
		this.tasks.addTask(6, aiChastGoHome);
		// Wander
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
		this.getDataManager().register(AI_MODE, Integer.valueOf(EnumAIMode.FREEDOM.getNumber()));
		this.getDataManager().register(STATE_PANIC, Byte.valueOf((byte) 0));
		this.getDataManager().register(STATE_SIT, Byte.valueOf((byte) 0));
		this.getDataManager().register(STATE_TRADE, Byte.valueOf((byte) 0));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setTag(ChastMobNBTs.ENTITY_CHAST_INVENTORY, this.getInventoryChast().writeInventoryToNBT());

		compound.setByte(ChastMobNBTs.ENTITY_CHAST_ARM_COLOR, (byte) this.getArmColor().getDyeDamage());

		if (this.getOwnerUUID() == null)
		{
			compound.setString(ChastMobNBTs.ENTITY_CHAST_OWNER_UUID, "");
		}
		else
		{
			compound.setString(ChastMobNBTs.ENTITY_CHAST_OWNER_UUID, this.getOwnerUUID().toString());
		}

		compound.setByte(ChastMobNBTs.ENTITY_CHAST_AI_MODE, (byte) this.getAIMode().getNumber());

		compound.setBoolean(ChastMobNBTs.ENTITY_CHAST_STATE_SIT, this.isStateSit());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		this.getInventoryChast().readInventoryFromNBT(compound.getTagList(ChastMobNBTs.ENTITY_CHAST_INVENTORY, 10));

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

		this.setAIMode(EnumAIMode.byNumber(compound.getByte(ChastMobNBTs.ENTITY_CHAST_AI_MODE)));

		this.setAIPanicking(0);

		this.setStatePanic(false);

		this.setAISitting(compound.getBoolean(ChastMobNBTs.ENTITY_CHAST_STATE_SIT));

		this.setStateSit(compound.getBoolean(ChastMobNBTs.ENTITY_CHAST_STATE_SIT));

		this.setAITrading(null);

		this.setStateTrade(false);
	}

	@Override
	public void setCustomNameTag(String name)
	{
		super.setCustomNameTag(name);

		this.getInventoryChast().setCustomName(name);
	}

	@Override
	@Nullable
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

		if ((source.getSourceOfDamage() instanceof EntityLivingBase) && !this.getEntityWorld().isRemote)
		{
			this.setAIPanicking((int) amount);
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
				((EntityPlayerMP) ownerEntity).addChatMessage(new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_GOODBYE, new Object[]
				{
						TextFormatting.ITALIC.BOLD + this.getName(),
						TextFormatting.ITALIC.BOLD + ownerEntity.getName(),
				}));
			}

			Block.spawnAsEntity(world, this.getPosition(), new ItemStack(Blocks.CHEST));

			InventoryHelper.dropInventoryItems(world, this, this.getInventoryChast());

			for (EntityEquipmentSlot eqSlot : EntityEquipmentSlot.values())
			{
				ItemStack stackEq = this.getItemStackFromSlot(eqSlot);

				if (ChastMobHelper.isNotEmptyItemStack(stackEq))
				{
					Block.spawnAsEntity(world, this.getPosition(), stackEq);
				}

				this.setItemStackToSlot(eqSlot, ChastMobHelper.getEmptyItemStack());
			}
		}

		super.onDeath(cause);
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack)
	{
		if (this.isStatePanic())
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
			EntityEquipmentSlot eqSlotMainhand = EntityEquipmentSlot.MAINHAND;
			ItemStack stackMainhand = this.getItemStackFromSlot(eqSlotMainhand);

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

			if (ChastMobHelper.isNotEmptyItemStack(stack))
			{
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

						return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_ITEM_PICKUP);
					}
				}

				if (stack.getItem().equals(ChastMobItems.MAP_HOME_CHEST))
				{
					if (isServerWorld)
					{
						this.setAISitting(false);
						this.setAIMode(EnumAIMode.FREEDOM);

						if (ChastMobHelper.isNotEmptyItemStack(stackMainhand))
						{
							Block.spawnAsEntity(this.getEntityWorld(), this.getPosition(), stackMainhand);
						}

						this.setItemStackToSlot(eqSlotMainhand, stack);

						player.setItemStackToSlot(eqSlotMainhand, ChastMobHelper.getEmptyItemStack());
					}

					return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_ITEM_PICKUP);
				}

				if (stack.interactWithEntity(player, this, hand))
				{
					return this.onSuccessProcessInteract(player, (SoundEvent) null);
				}
			}

			if (player.isSneaking())
			{
				if (isServerWorld)
				{
					this.setAISitting(!this.isStateSit());
					this.setAIMode(EnumAIMode.FOLLOW);

					if (ChastMobHelper.isNotEmptyItemStack(stackMainhand))
					{
						Block.spawnAsEntity(this.getEntityWorld(), this.getPosition(), stackMainhand);
					}

					this.setItemStackToSlot(eqSlotMainhand, ChastMobHelper.getEmptyItemStack());
				}

				return this.onSuccessProcessInteract(player, SoundEvents.ENTITY_ITEM_PICKUP);
			}
			else
			{
				if (isServerWorld)
				{
					player.displayGUIChest(this.getInventoryChast());
				}

				return this.onSuccessProcessInteract(player, (SoundEvent) null);
			}
		}
		// TODO VANILLA FIX
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

		if (this.isStatePanic() && !this.getEntityWorld().isRemote)
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

		this.onUpdateCoverOpen();

		if (this.ticksExisted < (20 * 5))
		{
			EntityLivingBase ownerEntity = this.getOwnerEntity();

			if ((ownerEntity != null) && (ownerEntity.getDistanceToEntity(this) < 16.0D))
			{
				this.getLookHelper().setLookPositionWithEntity(ownerEntity, this.getHorizontalFaceSpeed(), this.getVerticalFaceSpeed());
			}
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	@SideOnly(Side.CLIENT)
	public float getAngleCoverX(float partialTickTime)
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

	public EnumAIMode getAIMode()
	{
		return EnumAIMode.byNumber(((Integer) this.getDataManager().get(AI_MODE)).intValue());
	}

	public void setAIMode(EnumAIMode enumAIMode)
	{
		this.getDataManager().set(AI_MODE, Integer.valueOf(enumAIMode.getNumber()));
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
		if (this.inventoryChast == null)
		{
			this.inventoryChast = new InventoryChast(this);
		}

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

	public void onSpawnByPlayer(EntityPlayer player)
	{
		if (!player.getEntityWorld().isRemote)
		{
			this.setOwnerTame(true);
			this.setOwnerUUID(player.getUniqueID());

			this.setAIMode(EnumAIMode.FOLLOW);
			this.setAISitting(false);

			player.addChatMessage(new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_THANKS, new Object[]
			{
					TextFormatting.ITALIC.BOLD + this.getName(),
					TextFormatting.ITALIC.BOLD + player.getName(),
			}));
		}

		ChastMobPacket.DISPATCHER.sendToAll(new MessageParticleEntity(this, 0));

		this.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
	}

	public void setAIPanicking(int panicTime)
	{
		if (this.aiChastPanic != null)
		{
			this.aiChastPanic.setPanicking(panicTime);

			if (0 < panicTime)
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

	private void onUpdateCoverOpen()
	{
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

}

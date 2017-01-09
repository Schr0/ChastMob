package schr0.chastmob;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityTameable;
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

	private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer> createKey(EntityChast.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> OPEN = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> PANIC = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> SIT = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> TRADE = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);

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

		double moveSpeed = 1.25D;
		int maxDistance = 5;

		EntityAIBase aiSwimming = new EntityAISwimming(this);
		this.aiChastPanic = new EntityAIChastPanic(this, (moveSpeed * 2), (maxDistance * 2));
		this.aiChastSit = new EntityAIChastSit(this);
		this.aiChastTrade = new EntityAIChastTrade(this);
		EntityAIChastStoreChest aiChastStoreChest = new EntityAIChastStoreChest(this, moveSpeed, maxDistance);
		EntityAIBase aiChastCollectItem = new EntityAIChastCollectItem(this, moveSpeed, (double) maxDistance);
		EntityAIWander aiWander = new EntityAIWander(this, moveSpeed);
		EntityAIBase aiWatchClosestGolem = new EntityAIWatchClosest(this, EntityGolem.class, (float) maxDistance);
		EntityAIBase aiWatchClosestPlayer = new EntityAIWatchClosest(this, EntityPlayer.class, (float) maxDistance);
		EntityAIBase aiLookIdle = new EntityAILookIdle(this);

		aiSwimming.setMutexBits(0);
		this.aiChastPanic.setMutexBits(1);
		this.aiChastSit.setMutexBits(1);
		this.aiChastTrade.setMutexBits(1);
		aiChastStoreChest.setMutexBits(1);
		aiChastCollectItem.setMutexBits(1);
		aiWander.setMutexBits(1);
		aiWatchClosestGolem.setMutexBits(2);
		aiWatchClosestPlayer.setMutexBits(2);
		aiLookIdle.setMutexBits(2);

		this.tasks.addTask(0, aiSwimming);
		this.tasks.addTask(1, this.aiChastPanic);
		this.tasks.addTask(2, this.aiChastSit);
		this.tasks.addTask(3, this.aiChastTrade);
		this.tasks.addTask(4, aiChastStoreChest);
		this.tasks.addTask(5, aiChastCollectItem);
		this.tasks.addTask(6, aiWander);
		this.tasks.addTask(7, aiWatchClosestGolem);
		this.tasks.addTask(8, aiWatchClosestPlayer);
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
		this.getDataManager().register(COLOR, Integer.valueOf(EnumDyeColor.WHITE.getDyeDamage()));
		this.getDataManager().register(OPEN, Byte.valueOf((byte) 0));
		this.getDataManager().register(PANIC, Byte.valueOf((byte) 0));
		this.getDataManager().register(SIT, Byte.valueOf((byte) 0));
		this.getDataManager().register(TRADE, Byte.valueOf((byte) 0));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setTag(ChastMobNBTTags.CHAST_INVENTORY, this.getInventoryChast().writeInventoryToNBT());

		compound.setByte(ChastMobNBTTags.CHAST_COLOR, (byte) this.getColor().getDyeDamage());

		compound.setBoolean(ChastMobNBTTags.CHAST_SIT, this.isSit());

		// TODO BUG FIX
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

		this.setColor(EnumDyeColor.byDyeDamage(compound.getByte(ChastMobNBTTags.CHAST_COLOR)));

		this.setOpen(false);

		if (this.aiChastPanic != null)
		{
			this.setPanicking(0);
		}

		this.setSit(compound.getBoolean(ChastMobNBTTags.CHAST_SIT));

		if (this.aiChastSit != null)
		{
			this.setSitting(this.isSit());
		}

		if (this.aiChastTrade != null)
		{
			this.setTrading(null);
		}
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
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.getRidingEntity() instanceof EntityLivingBase)
		{
			return false;
		}

		boolean attackEntityFrom = super.attackEntityFrom(source, amount);

		if ((source.getSourceOfDamage() instanceof EntityLivingBase) && !this.getEntityWorld().isRemote)
		{
			this.setPanicking(Math.max((4 * 20), (int) amount * 20));
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
		if ((!hand.equals(EnumHand.MAIN_HAND)) || this.isPanic())
		{
			return false;
		}

		boolean isServerWorld = (!this.getEntityWorld().isRemote);

		if (ChastMobHelper.isNotEmptyItemStack(stack))
		{
			if (stack.getItem().equals(Items.DYE))
			{
				EnumDyeColor enumDyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());

				if (enumDyeColor != this.getColor())
				{
					if (isServerWorld)
					{
						this.setColor(enumDyeColor);

						if (!player.capabilities.isCreativeMode)
						{
							--stack.stackSize;
						}

						this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
					}

					player.swingArm(hand);

					return true;
				}
			}

			/*
				if (!player.isBeingRidden())
				{
					this.startRiding(player);
				}
			// */
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

				this.setSitting(!this.isSit());

				this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
			}

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
			if (this.isSit())
			{
				this.setSitting(false);
			}
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		this.onUpdateOpen(this, this.isOpen());
	}

	// TODO /* ======================================== MOD START =====================================*/

	@SideOnly(Side.CLIENT)
	public float getAngleCoverX(float partialTickTime)
	{
		return ((this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * partialTickTime) * 0.5F * (float) Math.PI);
	}

	public EnumDyeColor getColor()
	{
		return EnumDyeColor.byDyeDamage(((Integer) this.getDataManager().get(COLOR)).intValue() & 15);
	}

	public void setColor(EnumDyeColor enumDyeColor)
	{
		this.getDataManager().set(COLOR, Integer.valueOf(enumDyeColor.getDyeDamage()));
	}

	public boolean isSit()
	{
		return (((Byte) this.getDataManager().get(SIT)).byteValue() & 1) != 0;
	}

	public void setSit(boolean isSit)
	{
		byte b0 = ((Byte) this.getDataManager().get(SIT)).byteValue();

		if (isSit)
		{
			this.getDataManager().set(SIT, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(SIT, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isOpen()
	{
		return ((((Byte) this.getDataManager().get(OPEN)).byteValue() & 1) != 0);
	}

	public void setOpen(boolean isOpen)
	{
		byte b0 = ((Byte) this.getDataManager().get(OPEN)).byteValue();

		if (isOpen)
		{
			this.getDataManager().set(OPEN, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(OPEN, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isTrade()
	{
		return (((Byte) this.getDataManager().get(TRADE)).byteValue() & 1) != 0;
	}

	public void setTrade(boolean isTradeg)
	{
		byte b0 = ((Byte) this.getDataManager().get(TRADE)).byteValue();

		if (isTradeg)
		{
			this.getDataManager().set(TRADE, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(TRADE, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isPanic()
	{
		return (((Byte) this.getDataManager().get(PANIC)).byteValue() & 1) != 0;
	}

	public void setPanic(boolean isPanic)
	{
		byte b0 = ((Byte) this.getDataManager().get(PANIC)).byteValue();

		if (isPanic)
		{
			this.getDataManager().set(PANIC, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(PANIC, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public InventoryChast getInventoryChast()
	{
		return this.inventoryChast;
	}

	public void setSitting(boolean isSitting)
	{
		this.aiChastSit.setSitting(isSitting);
	}

	public void setTrading(@Nullable EntityPlayer tradePlayer)
	{
		this.aiChastTrade.setTrading(tradePlayer);
	}

	public void setPanicking(int panicTime)
	{
		this.aiChastPanic.setPanicking(panicTime);

		if (0 < panicTime)
		{
			this.aiChastSit.setSitting(false);
			this.aiChastTrade.setTrading(null);
		}
	}

	private void onUpdateOpen(EntityChast entityChast, boolean isOpen)
	{
		this.prevLidAngle = this.lidAngle;

		if (isOpen && (this.lidAngle == 0.0F))
		{
			entityChast.setOpen(true);

			this.playSound(SoundEvents.BLOCK_CHEST_OPEN, 0.5F, entityChast.rand.nextFloat() * 0.1F + 0.9F);
		}

		if ((!isOpen && (0.0F < this.lidAngle)) || (isOpen && (this.lidAngle < 1.0F)))
		{
			float angel1 = 0.1F;
			float angel2 = this.lidAngle;
			float angel3 = 0.5F;

			if (isOpen)
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
				entityChast.setOpen(false);

				this.playSound(SoundEvents.BLOCK_CHEST_CLOSE, 0.5F, entityChast.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F)
			{
				this.lidAngle = 0.0F;
			}
		}
	}

}

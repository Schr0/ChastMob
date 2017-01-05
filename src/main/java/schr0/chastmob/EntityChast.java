package schr0.chastmob;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
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

	private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer> createKey(EntityChast.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> SITTING = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> OPEN = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> TRADE = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> PANIC = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);

	private static final int PANIC_TIME_MIN = (4 * 20);

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

		EntityAIBase aiSwimming = new EntityAISwimming(this);
		this.aiChastPanic = new EntityAIChastPanic(this);
		this.aiChastSit = new EntityAIChastSit(this);
		this.aiChastTrade = new EntityAIChastTrade(this);
		EntityAIBase aiChastCollectItem = new EntityAIChastCollectItem(this);
		EntityAIBase aiWander = new EntityAIWander(this, 1.25D);
		EntityAIBase aiWatchClosest = new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F);
		EntityAIBase aiLookIdle = new EntityAILookIdle(this);

		aiSwimming.setMutexBits(0);
		this.aiChastPanic.setMutexBits(1);
		this.aiChastSit.setMutexBits(1);
		this.aiChastTrade.setMutexBits(1);
		aiChastCollectItem.setMutexBits(1);
		aiWander.setMutexBits(1);
		aiWatchClosest.setMutexBits(2);
		aiLookIdle.setMutexBits(3);

		this.tasks.addTask(0, aiSwimming);
		this.tasks.addTask(1, this.aiChastPanic);
		this.tasks.addTask(2, this.aiChastSit);
		this.tasks.addTask(3, this.aiChastTrade);
		this.tasks.addTask(4, aiChastCollectItem);
		this.tasks.addTask(5, aiWander);
		this.tasks.addTask(6, aiWatchClosest);
		this.tasks.addTask(7, aiLookIdle);
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
		this.getDataManager().register(SITTING, Byte.valueOf((byte) 0));
		this.getDataManager().register(OPEN, Byte.valueOf((byte) 0));
		this.getDataManager().register(TRADE, Byte.valueOf((byte) 0));
		this.getDataManager().register(PANIC, Byte.valueOf((byte) 0));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setTag(ChastMobNBTTags.CHAST_INVENTORY, this.getInventoryChast().writeInventoryToNBT());
		compound.setByte(ChastMobNBTTags.CHAST_COLOR, (byte) this.getColor().getDyeDamage());
		compound.setBoolean(ChastMobNBTTags.CHAST_SITTING, this.isSitting());

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
		this.setSitting(compound.getBoolean(ChastMobNBTTags.CHAST_SITTING));

		this.setOpen(false);

		if (this.aiChastPanic != null)
		{
			this.setAIPanicFlag(0);
		}

		if (this.aiChastSit != null)
		{
			this.setAISitFlag(this.isSitting());
		}

		if (this.aiChastTrade != null)
		{
			this.setAITradeFlag(null);
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
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.getRidingEntity() instanceof EntityLivingBase)
		{
			return false;
		}

		boolean attackEntityFrom = super.attackEntityFrom(source, amount);

		if ((source.getSourceOfDamage() instanceof EntityLivingBase) && !this.getEntityWorld().isRemote)
		{
			this.setAIPanicFlag(Math.max(PANIC_TIME_MIN, (int) amount * 20));
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
		if (!hand.equals(EnumHand.MAIN_HAND))
		{
			return false;
		}

		boolean isServerWorld = (!this.getEntityWorld().isRemote);

		if (ChastMobVanillaHelper.isNotEmptyItemStack(stack))
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
		}

		if (player.isSneaking())
		{
			if (isServerWorld)
			{
				this.setAISitFlag(!this.isSitting());

				this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
			}

			player.swingArm(hand);
		}
		else
		{
			if (isServerWorld)
			{
				player.displayGUIChest(this.getInventoryChast());
			}

			player.swingArm(hand);

			/*
				if (!player.isBeingRidden())
				{
					this.startRiding(player);
				}
			// */
		}

		return true;
	}

	@Override
	public void updateRidden()
	{
		super.updateRidden();

		if (this.isRiding() && this.getRidingEntity().isSneaking())
		{
			this.dismountRidingEntity();
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

	public boolean isSitting()
	{
		return (((Byte) this.getDataManager().get(SITTING)).byteValue() & 1) != 0;
	}

	public void setSitting(boolean isSitting)
	{
		byte b0 = ((Byte) this.getDataManager().get(SITTING)).byteValue();

		if (isSitting)
		{
			this.getDataManager().set(SITTING, Byte.valueOf((byte) (b0 | 1)));
		}
		else
		{
			this.getDataManager().set(SITTING, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isOpen()
	{
		return ((((Byte) this.getDataManager().get(OPEN)).byteValue() & 1) != 0);
	}

	public void setOpen(boolean isCoverOpen)
	{
		byte b0 = ((Byte) this.getDataManager().get(OPEN)).byteValue();

		if (isCoverOpen)
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

	public void setTrade(boolean isTrading)
	{
		byte b0 = ((Byte) this.getDataManager().get(TRADE)).byteValue();

		if (isTrading)
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

	public void setAISitFlag(boolean flag)
	{
		this.aiChastSit.setSitting(flag);
	}

	public void setAITradeFlag(@Nullable EntityPlayer flag)
	{
		this.aiChastTrade.setTrading(flag);
	}

	public void setAIPanicFlag(int flag)
	{
		this.aiChastPanic.setPanicking(flag);

		if (0 < flag)
		{
			this.aiChastSit.setSitting(false);
			this.aiChastTrade.setTrading(null);
		}
	}

	private void onUpdateOpen(EntityChast entityChast, boolean isCoverOpen)
	{
		World world = this.getEntityWorld();

		this.prevLidAngle = this.lidAngle;

		if (isCoverOpen && (this.lidAngle == 0.0F))
		{
			entityChast.setOpen(true);

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
				entityChast.setOpen(false);

				this.playSound(SoundEvents.BLOCK_CHEST_CLOSE, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F)
			{
				this.lidAngle = 0.0F;
			}
		}
	}

}

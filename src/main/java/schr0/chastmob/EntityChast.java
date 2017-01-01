package schr0.chastmob;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityChast extends EntityGolem
{

	private static final DataParameter<Byte> COVER_OPEN = EntityDataManager.<Byte> createKey(EntityChast.class, DataSerializers.BYTE);

	private float lidAngle;
	private float prevLidAngle;

	public EntityChast(World worldIn)
	{
		super(worldIn);
		this.setSize(0.9F, 1.5F);
	}

	public static void func_189790_b(DataFixer p_189790_0_)
	{
		EntityLiving.func_189752_a(p_189790_0_, ChastMobEntitys.NAME_CHAST);
	}

	@Override
	protected void initEntityAI()
	{
		super.initEntityAI();
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 2.5D));
		this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(4, new EntityAILookIdle(this));
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
		this.getDataManager().register(COVER_OPEN, Byte.valueOf((byte) 0));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setBoolean(ChastMobNBTTags.CHAST_COVER_OPEN, this.isCoverOpen());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		this.setCoverOpen(compound.getBoolean(ChastMobNBTTags.CHAST_COVER_OPEN));
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		this.onUpdateCoverOpen(this, this.isCoverOpen());
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack)
	{
		if (!this.getEntityWorld().isRemote)
		{
			this.setCoverOpen(!this.isCoverOpen());
		}

		return true;
		// return super.processInteract(player, hand, stack);
	}

	// TODO /* ======================================== MOD START =====================================*/

	@SideOnly(Side.CLIENT)
	public float getCoverAngle(float partialTickTime)
	{
		return (this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * partialTickTime) * 0.5F * (float) Math.PI;
	}

	public boolean isCoverOpen()
	{
		return (((Byte) this.dataManager.get(COVER_OPEN)).byteValue() & 16) != 0;
	}

	public void setCoverOpen(boolean isCoverOpen)
	{
		byte b0 = ((Byte) this.dataManager.get(COVER_OPEN)).byteValue();

		if (isCoverOpen)
		{
			this.dataManager.set(COVER_OPEN, Byte.valueOf((byte) (b0 | 16)));
		}
		else
		{
			this.dataManager.set(COVER_OPEN, Byte.valueOf((byte) (b0 & -17)));
		}
	}

	public void onUpdateCoverOpen(EntityChast entityChast, boolean isOpen)
	{
		this.prevLidAngle = this.lidAngle;
		float angel = 0.2F;

		if (isOpen && this.lidAngle == 0.0F)
		{
			entityChast.setCoverOpen(true);

			this.playSound(SoundEvents.BLOCK_CHEST_OPEN, 0.5F, this.getEntityWorld().rand.nextFloat() * 0.1F + 0.9F);
		}

		if (!isOpen && this.lidAngle > 0.0F || isOpen && this.lidAngle < 1.0F)
		{
			float angel1 = this.lidAngle;

			if (isOpen)
			{
				this.lidAngle += angel;
			}
			else
			{
				this.lidAngle -= angel;
			}

			if (this.lidAngle > 1.0F)
			{
				this.lidAngle = 1.0F;
			}

			float angel2 = 0.5F;

			if (this.lidAngle < angel2 && angel1 >= angel2)
			{
				entityChast.setCoverOpen(false);

				this.playSound(SoundEvents.BLOCK_CHEST_CLOSE, 0.5F, this.getEntityWorld().rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F)
			{
				this.lidAngle = 0.0F;
			}
		}
	}

}

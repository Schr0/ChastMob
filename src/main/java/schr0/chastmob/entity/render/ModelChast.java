package schr0.chastmob.entity.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.entity.EntityChast;

@SideOnly(Side.CLIENT)
public class ModelChast extends ModelBase
{

	public static final int WIDTH = 128;
	public static final int HEIGHT = 64;

	public ModelRenderer body;
	public ModelRenderer core;
	public ModelRenderer coverMain;
	public ModelRenderer coverHandle;
	public ModelRenderer armRight;
	public ModelRenderer armLeft;
	public ModelRenderer legRight;
	public ModelRenderer legLeft;
	public ModelRenderer armourUpper;
	public ModelRenderer armourLower;

	public ModelChast()
	{
		this.textureWidth = WIDTH;
		this.textureHeight = HEIGHT;
		this.coverMain = new ModelRenderer(this, 0, 0);
		this.coverMain.addBox(-7.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
		this.armRight = new ModelRenderer(this, 0, 48);
		this.armRight.addBox(-1.0F, 0.0F, -1.0F, 2, 9, 2, 0.0F);
		this.armLeft = new ModelRenderer(this, 8, 48);
		this.armLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 9, 2, 0.0F);
		this.legLeft = new ModelRenderer(this, 24, 48);
		this.legLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 9, 2, 0.0F);
		this.core = new ModelRenderer(this, 32, 48);
		this.core.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 2, 0.0F);
		this.armourLower = new ModelRenderer(this, 64, 20);
		this.armourLower.addBox(-7.5F, -2.5F, -7.5F, 15, 9, 15, 0.0F);
		this.legRight = new ModelRenderer(this, 16, 48);
		this.legRight.addBox(-1.0F, 0.0F, -1.0F, 2, 9, 2, 0.0F);
		this.coverHandle = new ModelRenderer(this, 0, 0);
		this.coverHandle.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
		this.body = new ModelRenderer(this, 0, 19);
		this.body.addBox(-7.0F, -4.0F, -7.0F, 14, 10, 14, 0.0F);
		this.armourUpper = new ModelRenderer(this, 64, 0);
		this.armourUpper.addBox(-7.5F, -6.5F, -14.5F, 15, 5, 15, 0.0F);

		this.body.setRotationPoint(0F, 10F, 0F);

		// 親：this.body.setRotationPoint(0F, 10F, 0F);
		// 子：this.core.setRotationPoint(0F, 11F, -7F);
		this.core.setRotationPoint(0F, 1F, -7F);

		// 親：this.body.setRotationPoint(0F, 10F, 0F);
		// 子：this.coverMain.setRotationPoint(0F, 6F, 7F);
		this.coverMain.setRotationPoint(0F, -4F, 7F);

		// 親：this.coverMain.setRotationPoint(0F, 6F, 7F);
		// 子：this.coverHandle.setRotationPoint(0F, 6F, 7F);
		this.coverHandle.setRotationPoint(0F, 0F, 0F);

		// 親：this.coverMain.setRotationPoint(0F, 6F, 7F);
		// 子：this.armourUpper.setRotationPoint(0F, 6F, 7F);
		this.armourUpper.setRotationPoint(0F, 0F, 0F);

		// 親：this.body.setRotationPoint(0F, 10F, 0F);
		// 子：this.armourLower.setRotationPoint(0F, 10F, 0F);
		this.armourLower.setRotationPoint(0F, 0F, 0F);

		this.body.addChild(this.core);
		this.body.addChild(this.coverMain);
		this.body.addChild(this.armourLower);
		this.coverMain.addChild(this.coverHandle);
		this.coverMain.addChild(this.armourUpper);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

		if (this.isChild)
		{
			GL11.glPushMatrix();
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			GL11.glTranslatef(0.0F, (24.0F * scale), 0.0F);

			this.body.render(scale);
			this.armRight.render(scale);
			this.armLeft.render(scale);
			this.legRight.render(scale);
			this.legLeft.render(scale);

			GL11.glPopMatrix();
		}
		else
		{
			this.body.render(scale);
			this.armRight.render(scale);
			this.armLeft.render(scale);
			this.legRight.render(scale);
			this.legLeft.render(scale);
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		if (!(entityIn instanceof EntityChast))
		{
			return;
		}

		EntityChast entityChast = (EntityChast) entityIn;
		boolean isDefending = (entityChast.isPanic() && entityChast.isEquipHelmet());
		boolean isPanicking = (entityChast.isPanic() && !entityChast.isEquipHelmet());

		if (entityChast.isRiding() || !entityChast.getPassengers().isEmpty())
		{
			this.body.rotateAngleX = 0.0F;
		}
		else
		{
			this.body.rotateAngleX = (headPitch / (180F / (float) Math.PI));
		}

		this.body.rotateAngleY = (netHeadYaw / (180F / (float) Math.PI));
		this.core.rotateAngleZ = (ageInTicks * (entityChast.getHealth() / 50F));

		if (entityChast.isSit() || entityChast.isRiding())
		{
			float pointSitY = 7.0F;

			this.body.setRotationPoint(0F, (10F + pointSitY), 0F);
			this.armRight.setRotationPoint(-7F, (8F + pointSitY), 0F);
			this.armLeft.setRotationPoint(7F, (8F + pointSitY), 0F);
			this.legRight.setRotationPoint(-3F, (15F + pointSitY), 0F);
			this.legLeft.setRotationPoint(3F, (15F + pointSitY), 0F);

			float angleSitArmX = -0.95F;

			this.armRight.rotateAngleX = angleSitArmX;
			this.armLeft.rotateAngleX = angleSitArmX;

			float angleSitLegX = -1.55F;

			this.legRight.rotateAngleX = angleSitLegX;
			this.legLeft.rotateAngleX = angleSitLegX;
		}
		else
		{
			if (isDefending)
			{
				this.armRight.showModel = false;
				this.armLeft.showModel = false;
				this.legRight.showModel = false;
				this.legLeft.showModel = false;

				this.body.setRotationPoint(0F, (10F + 8.0F), 0F);

				return;
			}
			else
			{
				this.armRight.showModel = true;
				this.armLeft.showModel = true;
				this.legRight.showModel = true;
				this.legLeft.showModel = true;

				this.body.setRotationPoint(0F, 10F, 0F);
				this.armRight.setRotationPoint(-7F, 8F, 0F);
				this.armLeft.setRotationPoint(7F, 8F, 0F);
				this.legRight.setRotationPoint(-3F, 15F, 0F);
				this.legLeft.setRotationPoint(3F, 15F, 0F);
			}

			float angleSwingArmRightLegLeftX = (MathHelper.cos(limbSwing * 0.6662F) * 1.5F * limbSwingAmount);
			float angleSwingArmLeftLegRightX = (MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.5F * limbSwingAmount);

			if (isPanicking)
			{
				float anglePanicArmX = 3.0F;
				float anglePanicArmRightX = (MathHelper.cos(limbSwing * 0.6662F) * 0.5F * limbSwingAmount) - anglePanicArmX;
				float anglePanicArmLeftX = (MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.5F * limbSwingAmount) - anglePanicArmX;

				this.armRight.rotateAngleX = anglePanicArmRightX;
				this.armLeft.rotateAngleX = anglePanicArmLeftX;
			}
			else
			{
				this.armRight.rotateAngleX = angleSwingArmRightLegLeftX;
				this.armLeft.rotateAngleX = angleSwingArmLeftLegRightX;
			}

			this.legLeft.rotateAngleX = angleSwingArmRightLegLeftX;
			this.legRight.rotateAngleX = angleSwingArmLeftLegRightX;
		}

		float angleArmX = (MathHelper.sin(ageInTicks * 0.05F) * 0.05F);
		this.armRight.rotateAngleX += angleArmX;
		this.armLeft.rotateAngleX -= angleArmX;

		float angleArmZ = 0.35F;

		if (isPanicking)
		{
			this.armRight.rotateAngleZ = (MathHelper.sin(ageInTicks * 0.05F) * 0.05F) - angleArmZ;
			this.armLeft.rotateAngleZ = (MathHelper.sin(ageInTicks * 0.05F) * 0.05F) + angleArmZ;
		}
		else
		{
			this.armRight.rotateAngleZ = (MathHelper.sin(ageInTicks * 0.05F) * 0.05F) + angleArmZ;
			this.armLeft.rotateAngleZ = (MathHelper.sin(ageInTicks * 0.05F) * 0.05F) - angleArmZ;
		}

		this.body.rotateAngleY = this.armRight.rotateAngleY = this.armLeft.rotateAngleY = this.legRight.rotateAngleY = this.legLeft.rotateAngleY;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
	{
		if (!(entitylivingbaseIn instanceof EntityChast))
		{
			return;
		}

		EntityChast entityChast = (EntityChast) entitylivingbaseIn;

		this.coverMain.rotateAngleX = -(entityChast.getCoverRotateAngleX(partialTickTime));
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void postRenderArm(float scale, EnumHandSide side)
	{
		this.getArmForSide(side).postRender(scale);
	}

	private ModelRenderer getArmForSide(EnumHandSide side)
	{
		return (side == EnumHandSide.LEFT) ? this.armLeft : this.armRight;
	}

}

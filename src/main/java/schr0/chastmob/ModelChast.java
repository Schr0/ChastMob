package schr0.chastmob;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelChast extends ModelBase
{

	public static final int WIDTH = 64;
	public static final int HEIGHT = 64;

	public ModelRenderer body;
	public ModelRenderer core;
	public ModelRenderer coverMain;
	public ModelRenderer coverHandle;
	public ModelRenderer armRight;
	public ModelRenderer armLeft;
	public ModelRenderer legRight;
	public ModelRenderer legLeft;

	public ModelChast()
	{
		this.textureWidth = WIDTH;
		this.textureHeight = HEIGHT;

		this.body = new ModelRenderer(this, 0, 19).addBox(-7F, -4F, -7F, 14, 10, 14);
		this.body.setRotationPoint(0F, 10F, 0F);

		this.core = new ModelRenderer(this, 32, 48).addBox(-2F, -2F, -1F, 4, 4, 2);
		this.core.setRotationPoint(0F, 1F, -7F);
		// 親：this.body.setRotationPoint(0F, 10F, 0F);
		// 子：this.core.setRotationPoint(0F, 11F, -7F);

		this.coverMain = new ModelRenderer(this, 0, 0).addBox(-7F, -5F, -14F, 14, 5, 14);
		this.coverMain.setRotationPoint(0F, -4F, 7F);
		// 親：this.body.setRotationPoint(0F, 10F, 0F);
		// 子：this.coverMain.setRotationPoint(0F, 6F, 7F);

		this.coverHandle = new ModelRenderer(this, 0, 0).addBox(-1F, -2F, -15F, 2, 4, 1);
		this.coverHandle.setRotationPoint(0F, 0F, 0F);
		// 親：this.coverMain.setRotationPoint(0F, 6F, 7F);
		// 子：this.coverHandle.setRotationPoint(0F, 6F, 7F);

		this.armRight = new ModelRenderer(this, 0, 48).addBox(-1F, 0F, -1F, 2, 9, 2);
		this.armRight.setRotationPoint(-7F, 8F, 0F);

		this.armLeft = new ModelRenderer(this, 8, 48).addBox(-1F, 0F, -1F, 2, 9, 2);
		this.armLeft.setRotationPoint(7F, 8F, 0F);

		this.legRight = new ModelRenderer(this, 16, 48).addBox(-1F, 0F, -1F, 2, 9, 2);
		this.legRight.setRotationPoint(-3F, 15F, 0F);

		this.legLeft = new ModelRenderer(this, 24, 48).addBox(-1F, 0F, -1F, 2, 9, 2);
		this.legLeft.setRotationPoint(3F, 15F, 0F);

		this.coverMain.addChild(this.coverHandle);
		this.body.addChild(this.coverMain);
		this.body.addChild(this.core);

		this.body.mirror = true;
		this.core.mirror = true;
		this.coverMain.mirror = true;
		this.coverHandle.mirror = true;
		this.armRight.mirror = true;
		this.armLeft.mirror = true;
		this.legRight.mirror = true;
		this.legLeft.mirror = true;
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

		if (this.isChild)
		{
			GL11.glPushMatrix();

			float scaleHalf = (1.0F / 2.0F);
			GL11.glScalef(scaleHalf, scaleHalf, scaleHalf);
			GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);

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

		this.body.rotateAngleX = (headPitch / (180F / (float) Math.PI));
		this.body.rotateAngleY = (netHeadYaw / (180F / (float) Math.PI));
		this.core.rotateAngleZ = (ageInTicks * (entityChast.getHealth() / 50F));

		if (entityChast.isSitting() || entityChast.isRiding())
		{
			float pointSitY = 8.0F;
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
			this.body.setRotationPoint(0F, 10F, 0F);
			this.armRight.setRotationPoint(-7F, 8F, 0F);
			this.armLeft.setRotationPoint(7F, 8F, 0F);
			this.legRight.setRotationPoint(-3F, 15F, 0F);
			this.legLeft.setRotationPoint(3F, 15F, 0F);

			float angleSwingArmRightLegLeftX = (MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount);
			float angleSwingArmLeftLegRightX = (MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount);

			if (entityChast.isPanic())
			{
				this.armRight.rotateAngleX = 0.0F;
				this.armLeft.rotateAngleX = 0.0F;
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

		if (entityChast.isPanic())
		{
			float anglePanicArmZ = 2.5F;
			this.armRight.rotateAngleZ = (MathHelper.sin(ageInTicks * 0.05F) * 0.05F) + anglePanicArmZ;
			this.armLeft.rotateAngleZ = (MathHelper.sin(ageInTicks * 0.05F) * 0.05F) - anglePanicArmZ;
		}
		else
		{
			float angleArmZ = 0.35F;
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

		this.coverMain.rotateAngleX = -(entityChast.getAngleCoverX(partialTickTime));
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

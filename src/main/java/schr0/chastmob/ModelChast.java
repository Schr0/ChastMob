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
			float scaleHalf = 2.0F;
			GL11.glPushMatrix();
			GL11.glScalef(1.0F / scaleHalf, 1.0F / scaleHalf, 1.0F / scaleHalf);
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
		EntityChast entityChast = (EntityChast) entityIn;

		if (entityChast.isSitting())
		{
			float downRotationPointY = 8.0F;
			this.body.setRotationPoint(0F, (10F + downRotationPointY), 0F);
			this.armRight.setRotationPoint(-7F, (8F + downRotationPointY), 0F);
			this.armLeft.setRotationPoint(7F, (8F + downRotationPointY), 0F);
			this.legRight.setRotationPoint(-3F, (15F + downRotationPointY), 0F);
			this.legLeft.setRotationPoint(3F, (15F + downRotationPointY), 0F);

			float angleArmX = -0.95F;
			this.armRight.rotateAngleX = angleArmX;
			this.armLeft.rotateAngleX = angleArmX;

			float angleLegX = -1.55F;
			this.legRight.rotateAngleX = angleLegX;
			this.legLeft.rotateAngleX = angleLegX;
		}
		else
		{
			this.body.setRotationPoint(0F, 10F, 0F);
			this.armRight.setRotationPoint(-7F, 8F, 0F);
			this.armLeft.setRotationPoint(7F, 8F, 0F);
			this.legRight.setRotationPoint(-3F, 15F, 0F);
			this.legLeft.setRotationPoint(3F, 15F, 0F);

			this.armRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
			this.armLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
			this.legRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
			this.legLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		}

		if (!entityChast.isRiding())
		{
			this.body.rotateAngleX = (headPitch / (180F / (float) Math.PI));
		}

		this.body.rotateAngleY = (netHeadYaw / (180F / (float) Math.PI));
		this.body.rotateAngleY = this.armRight.rotateAngleY = this.armLeft.rotateAngleY = this.legRight.rotateAngleY = this.legLeft.rotateAngleY;

		float angelCoreZ = (entityChast.getHealth() / 50F);
		this.core.rotateAngleZ = (ageInTicks * angelCoreZ);

		this.armRight.rotateAngleX += (MathHelper.sin(ageInTicks * 0.05F) * 0.05F);
		this.armLeft.rotateAngleX -= (MathHelper.sin(ageInTicks * 0.05F) * 0.05F);

		float angleArmZ = 0.36F;
		this.armRight.rotateAngleZ = (MathHelper.sin(ageInTicks * 0.05F) * 0.05F) + angleArmZ;
		this.armLeft.rotateAngleZ = (MathHelper.sin(ageInTicks * 0.05F) * 0.05F) - angleArmZ;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
	{
		EntityChast entityChast = (EntityChast) entitylivingbaseIn;
		float angleCoverX = entityChast.getAngleCoverX(partialTickTime);

		this.coverMain.rotateAngleX = -angleCoverX;
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

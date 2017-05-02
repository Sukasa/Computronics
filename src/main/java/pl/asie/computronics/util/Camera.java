package pl.asie.computronics.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import pl.asie.computronics.reference.Config;

import javax.annotation.Nullable;

public class Camera {

	private World world;
	private float xDirection, yDirection, zDirection;
	private double oxPos, oyPos, ozPos, xPos, yPos, zPos;
	private Object hit;

	public Camera() {
		// not initialized
	}

	public boolean ray(World worldObj, double xCoord, double yCoord, double zCoord, @Nullable EnumFacing dir, float x, float y) {
		hit = null;
		if(x < -1.0F || x > 1.0F || y < -1.0F || y > 1.0F) {
			return false;
		}
		if(dir != null) {
			xDirection = 0.0F;
			yDirection = y;
			zDirection = 0.0F;


			double oxOffset = 0, oyOffset = 0, ozOffset = 0;

			switch(dir) {
				case EAST: {
					xDirection = 1.0F;
					zDirection = -x;
					xCoord += 0.6;
					oxOffset = -0.1;
				}
				break;
				case NORTH: {
					zDirection = -1.0F;
					xDirection = x;
					zCoord -= 0.6;
					ozOffset = 0.1;
				}
				break;
				case SOUTH: {
					zDirection = 1.0F;
					xDirection = -x;
					zCoord += 0.6;
					ozOffset = -0.1;
				}
				break;
				case WEST: {
					xDirection = -1.0F;
					zDirection = x;
					xCoord -= 0.6;
					oxOffset = 0.1;
				}
				break;
				case DOWN: {
					yDirection = -1.0F;
					xDirection = x;
					zDirection = y;
					yCoord -= 0.6;
					oyOffset = 0.1;
				}
				break;
				case UP: {
					yDirection = 1.0F;
					xDirection = x;
					zDirection = y;
					yCoord += 0.6;
					oyOffset = -0.1;
				}
				break;
				default:
					return false;
			}
			world = worldObj;
			xPos = xCoord;
			yPos = yCoord;
			zPos = zCoord;
			oxPos = xPos;
			oyPos = yPos;
			ozPos = zPos;
			// A little workaround for the way I do things (skipping the block right in front, that is)
			BlockPos pos = new BlockPos((int) Math.floor(xPos), (int) Math.floor(yPos), (int) Math.floor(zPos));
			if(!world.isAirBlock(pos)) {
				hit = world.getBlockState(pos).getBlock();
				return true;
			}

			// shoot ray
			float steps = Config.CAMERA_DISTANCE;
			Vec3d origin = new Vec3d(xPos, yPos, zPos);
			Vec3d target = new Vec3d(xPos + (xDirection * steps), yPos + (yDirection * steps), zPos + (zDirection * steps));
			RayTraceResult mop = world.rayTraceBlocks(origin, target);
			if(mop != null) {
				xPos = mop.hitVec.xCoord;
				yPos = mop.hitVec.yCoord;
				zPos = mop.hitVec.zCoord;
				oxPos += oxOffset;
				oyPos += oyOffset;
				ozPos += ozOffset;
				switch(mop.typeOfHit) {
					case ENTITY: {
						hit = mop.entityHit;
					}
					break;
					case BLOCK: {
						hit = world.getBlockState(mop.getBlockPos());
					}
					break;
					default:
						break;
				}
			}
			return true;
		}
		return false;
	}

	public double getDistance() {
		if(hit == null) {
			return -1.0;
		}
		double x = xPos - oxPos;
		double y = yPos - oyPos;
		double z = zPos - ozPos;
		return Math.sqrt(x * x + y * y + z * z);
	}
}

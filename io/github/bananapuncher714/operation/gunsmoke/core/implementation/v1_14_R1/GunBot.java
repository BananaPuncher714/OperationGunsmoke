package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;

import io.github.bananapuncher714.operation.gunsmoke.core.util.GunsmokeUtil;
import io.github.bananapuncher714.operation.gunsmoke.implementation.GunsmokeImplementation;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigGun;
import io.github.bananapuncher714.operation.gunsmoke.implementation.weapon.ConfigWeaponOptions;
import net.minecraft.server.v1_14_R1.Blocks;
import net.minecraft.server.v1_14_R1.DifficultyDamageScaler;
import net.minecraft.server.v1_14_R1.EntityArrow;
import net.minecraft.server.v1_14_R1.EntityCreeper;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntitySkeleton;
import net.minecraft.server.v1_14_R1.EntityTippedArrow;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EntityZombie;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.EnumMobSpawn;
import net.minecraft.server.v1_14_R1.GeneratorAccess;
import net.minecraft.server.v1_14_R1.GroupDataEntity;
import net.minecraft.server.v1_14_R1.IRangedEntity;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_14_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_14_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_14_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_14_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_14_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_14_R1.PathfinderGoalRandomStrollLand;
import net.minecraft.server.v1_14_R1.World;

public class GunBot extends EntityZombie implements IRangedEntity {
	ConfigGun gun;
	
	public GunBot( EntityTypes< EntityZombie > type, World world ) {
		super( type, world );
	}

	@Override
	protected void initPathfinder() {
	    // Adding our custom pathfinder selectors.
	    // Grants our zombie the ability to swim.
	    this.goalSelector.a(0, new PathfinderGoalFloat(this));
	    // This causes our zombie to shoot arrows.
	    // The parameters are: The ranged entity, movement speed, cooldown,
	    // maxDistance
	    // Or, with the second constructor: The ranged entity, movement speed,
	    // mincooldown, maxcooldown, maxDistance
	    this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0, 4, 20));
	    // Gets our zombie to attack creepers and skeletons!
	    this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityCreeper.class, true));
	    this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntitySkeleton.class, true));
	    this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityPlayer.class, true));
	    // Causes our zombie to walk towards it restriction.
	    this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0));
	    // Causes the zombie to walk around randomly.
	    this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0));
	    // Causes the zombie to look at players. Optional in our case. Last
	    // argument is range.
	    this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0f));
	    // Causes the zombie to randomly look around.
	    this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
	}
	
	@Override
	 public GroupDataEntity prepare( GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, GroupDataEntity groupdataentity, NBTTagCompound nbttagcompound) {
		groupdataentity = super.prepare( generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound );
	    // We'll set the main hand to a bow and head to a pumpkin now!
	    this.setSlot( EnumItemSlot.HEAD, new ItemStack( Blocks.PUMPKIN ) );
	    
	    ConfigWeaponOptions options = GunsmokeImplementation.getInstance().getGun( "remington870" );
	    gun = new ConfigGun( options );
	    
	    this.setSlot( EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy( gun.getItem() ) );
	    
	    gun.onEquip( ( LivingEntity ) this.getBukkitEntity(), GunsmokeUtil.getPlugin().getEntityManager().getEntity( this.getUniqueID() ), EquipmentSlot.HAND );
	    GunsmokeUtil.getPlugin().getItemManager().register( gun );
	    
	    return groupdataentity;
	}
	
	@Override
	public void a( final EntityLiving target, final float f ) {
		if ( gun.getBulletsRemaining() > 0 ) {
			gun.shoot();
		} else {
			gun.reload();
		}
		
//	    // Preparing the projectile
//	    final EntityArrow entityarrow = this.prepareProjectile(f);
//	    // Calculating the motion for the arrow to hit
//	    final double motX = target.locX - this.locX;
//	    final double motY = target.getBoundingBox().b + target.length / 3.0f - entityarrow.locY;
//	    final double motZ = target.locZ - this.locZ;
//	    final double horizontalMot = MathHelper.sqrt(motX * motX + motZ * motZ);
//	    // 'Shooting' the projectile (aka preparing it for being added to the
//	    // world.)
//	    entityarrow.shoot(motX, motY + horizontalMot * 0.2, motZ, 1.6f, 14 - world.getDifficulty().a() * 4);
//
//	    // OPTIONAL! Calls the event for shooting, that can be cancelled. I'd
//	    // keep it for other plugins that could cancel it.
//	    final EntityShootBowEvent event = CraftEventFactory.callEntityShootBowEvent(this, this.getItemInMainHand(), entityarrow, 0.8f);
//	    if (event.isCancelled()) {
//	        event.getProjectile().remove();
//	        return;
//	    }
//	    // Checking if the projectile has been changed thru the event..
//	    if (event.getProjectile() == entityarrow.getBukkitEntity()) {
//	        this.world.addEntity(entityarrow);
//	    }
//	    // And last, playing the shooting sound.
//	    this.a(SoundEffects.fV, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
	}

	protected EntityArrow prepareProjectile(final float unknown) {
	    // Creating the arrow instance. Now, you see, it's a Tipped Arrow. No
	    // idea why, but EntityArrow is abstract and we can't instantiate it
	    // without creating a custom class.
	    // This is why the arrows nowadays have the odd particle effect!
	    final EntityArrow arrow = new EntityTippedArrow(this.world, this);
	    // No idea what this does, copied from the sourcecode
	    arrow.a(this, unknown);
	    return arrow;
	}
}

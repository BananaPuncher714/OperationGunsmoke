package io.github.bananapuncher714.operation.gunsmoke.core.implementation.v1_14_R1;

import javax.annotation.Nonnull;

import io.github.bananapuncher714.operation.gunsmoke.api.nms.NBTCompound;
import net.minecraft.server.v1_14_R1.NBTTagCompound;

public class NBTCompoundNMS implements NBTCompound {
	protected NBTTagCompound compound;
	
	protected NBTCompoundNMS( @Nonnull NBTTagCompound compound ) {
		this.compound = compound;
	}

	@Override
	public String toString() {
		return compound.toString();
	}

	@Override
	public int hashCode() {
		return compound.hashCode();
	}

	@Override
	public boolean equals( Object obj ) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NBTCompoundNMS other = (NBTCompoundNMS) obj;
		if (compound == null) {
			if (other.compound != null)
				return false;
		} else if (!compound.equals(other.compound))
			return false;
		return true;
	}
}

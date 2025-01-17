package io.github.bananapuncher714.operation.gunsmoke.api.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class CollisionResultBlock extends CollisionResult {
	protected Block block;
	
	public CollisionResultBlock( Location location, BlockFace direction, Block block ) {
		super( location, direction, CollisionType.BLOCK );
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}
	
	public CollisionResultBlock copyOf() {
		return new CollisionResultBlock( location.clone(), direction, block );
	}
}

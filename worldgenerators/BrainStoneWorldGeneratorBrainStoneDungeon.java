package mods.brainstone.worldgenerators;

import java.util.Random;

import mods.brainstone.BrainStone;
import mods.brainstone.templates.BSP;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BrainStoneWorldGeneratorBrainStoneDungeon extends WorldGenerator {
	private int x, y, z;
	private World world;
	private Random random;
	private final int[] Options;

	public BrainStoneWorldGeneratorBrainStoneDungeon() {
		// height of the stairs

		Options = new int[] { 2 };
	}

	private boolean canPlaceSecretRoomHere() {
		int i, j, k;
		final int height = (Options[0] * 8) + 3;

		for (i = 3; i < 10; i++) {
			for (j = -1; j < 6; j++) {
				for (k = height + 1; k > (height - 8); k--)
					if (!this.isSolid(x + i, y - k, z + j))
						return false;
			}
		}

		return true;
	}

	private boolean canPlaceShackHere() {
		int i, j, k;

		for (i = 0; i < 6; i++) {
			for (j = 0; j < 5; j++)
				if (!this.isSolid(x + i, y - 1, z + j))
					return false;
		}

		for (i = 0; i < 4; i++)
			if (!this.isSolid(x + i, y - 1, z + 5))
				return false;

		for (i = 0; i < 6; i++) {
			for (j = 0; j < 5; j++) {
				for (k = 0; k < 4; k++)
					if (!this.isReplaceable(x + i, y + k, z + j))
						return false;
			}
		}

		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++)
				if (!this.isReplaceable(x + i, y + j, z + 5))
					return false;
		}

		if (!(this.isReplaceable(x + 4, y + 2, z + 5) && this.isReplaceable(
				x + 5, y + 2, z + 5)))
			return false;

		for (i = -1; i < 5; i++)
			if (!this.isReplaceable(x + i, y + 2, z + 6))
				return false;

		for (i = -1; i < 6; i++)
			if (!this.isReplaceable(x - 1, y + 2, z + i))
				return false;

		for (i = 0; i < 7; i++)
			if (!this.isReplaceable(x + i, y + 2, z - 1))
				return false;

		for (i = 0; i < 6; i++)
			if (!this.isReplaceable(x + 6, y + 2, z + i))
				return false;

		for (i = 1; i < 4; i++) {
			for (j = 1; j < 5; j++)
				if (!this.isReplaceable(x + j, y + 4, z + i))
					return false;
		}

		if (!(this.isReplaceable(x + 1, y + 4, z + 4) && this.isReplaceable(
				x + 2, y + 4, z + 4)))
			return false;

		if (!(this.isReplaceable(x + 2, y + 5, z + 2) && this.isReplaceable(
				x + 3, y + 5, z + 2)))
			return false;

		return true;
	}

	private boolean canPlaceStairsHere() {
		int i, j, k;

		final int height = (Options[0] * 8) + 5;

		for (i = -1; i < 6; i++) {
			for (j = -1; j < 6; j++) {
				for (k = 2; k < height; k++)
					if (!this.isSolid(x + i, y - k, z + j))
						return false;
			}
		}

		return true;
	}

	private boolean canPlaceStructHere(int structure, Object... options) {
		switch (structure) {
		case 0:
			return this.canPlaceShackHere();
		case 1:
			return this.canPlaceStairsHere() && this.canPlaceSecretRoomHere();
		}

		return false;
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		this.world = world;
		this.random = random;
		this.x = x;
		this.y = world.getHeightValue(x, z);
		this.z = z;

		BSP.finest("Trying at " + x + ", " + this.y + ", " + z + "!");

		int counter = 0;
		int direction = 0;
		int directionCounter = 0;
		int maxDirection = 1;

		while (true) {
			if (directionCounter >= maxDirection) {
				directionCounter = 0;
				maxDirection += direction % 2;
				direction = (direction + 1) % 4;
			}

			this.y = world.getHeightValue(this.x, this.z);

			if (this.canPlaceStructHere(0)) {
				Options[0] = 3;

				if (this.canPlaceStructHere(1)) {
					break;
				}

				Options[0] = 2;

				if (this.canPlaceStructHere(1)) {
					break;
				}

				Options[0] = 4;

				if (this.canPlaceStructHere(1)) {
					break;
				}
			}

			switch (direction) {
			case 0:
				this.x++;
				break;
			case 1:
				this.z++;
				break;
			case 2:
				this.x--;
				break;
			case 3:
				this.z--;
				break;
			}

			if (counter >= 10000) {
				BSP.finest("Failed");

				return false;
			}

			counter++;
			directionCounter++;
		}

		this.generateShack();

		this.generateStairs();

		this.generateSecretRoom();

		BSP.finest("Placed at " + x + ", " + this.y + ", " + z + "!");

		return true;
	}

	private void generateSecretRoom() {
		int i, j, k;
		int height = (Options[0] * 8) + 3;

		for (i = 4; i < 9; i++) {
			for (j = 0; j < 5; j++) {
				for (k = height; k > (height - 7); k--) {
					this.setBlock(
							x + i,
							y - k,
							z + j,
							((i == 4) || (i == 8) || (j == 0) || (j == 4)
									|| (k == height) || (k == (height - 6))) ? 4
									: 0);
				}
			}
		}

		this.setBlock(x + 6, (y - height) + 6, z + 2,
				BrainStone.brainStone().blockID);
		this.setBlock(x + 6, y - height, z + 2, BrainStone.brainStone().blockID);

		this.setBlock(x + 7, y - height - 2, z + 2,
				BrainStone.pulsatingBrainStone().blockID);

		height -= 1;

		// Chests

		this.setBlock(x + 6, y - height, z + 1, 54);
		TileEntityChest chest = (TileEntityChest) world.getBlockTileEntity(
				x + 6, y - height, z + 1);

		int rand1 = random.nextInt(9) + 2;

		for (i = 0; i < rand1; i++) {
			chest.setInventorySlotContents(
					random.nextInt(chest.getSizeInventory()), this.getLoot(1));
		}

		this.setBlock(x + 7, y - height, z + 1, 54);
		chest = (TileEntityChest) world.getBlockTileEntity(x + 7, y - height,
				z + 1);

		rand1 = random.nextInt(9) + 2;
		int rand2 = random.nextInt(3);

		for (i = 0; i < rand1; i++) {
			chest.setInventorySlotContents(
					random.nextInt(chest.getSizeInventory()),
					this.getLoot(rand2));
		}

		this.setBlock(x + 6, y - height, z + 3, 54);
		chest = (TileEntityChest) world.getBlockTileEntity(x + 6, y - height,
				z + 3);

		rand1 = random.nextInt(9) + 2;
		rand2 = random.nextInt(3);

		for (i = 0; i < rand1; i++) {
			chest.setInventorySlotContents(
					random.nextInt(chest.getSizeInventory()),
					this.getLoot(rand2));
		}

		this.setBlock(x + 7, y - height, z + 3, 54);
		chest = (TileEntityChest) world.getBlockTileEntity(x + 7, y - height,
				z + 3);

		rand1 = random.nextInt(9) + 2;

		for (i = 0; i < rand1; i++) {
			chest.setInventorySlotContents(
					random.nextInt(chest.getSizeInventory()), this.getLoot(1));
		}
	}

	private void generateShack() {
		int i, j, k;

		// Basement

		for (j = 0; j < 2; j++) {
			for (i = 0; i < 6; i++) {
				this.setBlock(x + i, y + j, z, 5);
			}

			for (i = 0; i < 6; i++) {
				this.setBlock(x, y + j, z + i, 5);
			}

			for (i = 0; i < 4; i++) {
				this.setBlock(x + i, y + j, z + 5, 5);
			}

			for (i = 0; i < 5; i++) {
				this.setBlock(x + 5, y + j, z + i, 5);
			}

			for (i = 3; i < 6; i++) {
				this.setBlock(x + i, y + j, z + 4, 5);
			}
		}

		// Door and Windows

		this.setBlock(x, y, z + 2, 0);
		this.setBlock(x, y + 1, z + 2, 0);
		this.setBlock(x + 2, y + 1, z, 0);
		this.setBlock(x + 5, y + 1, z + 2, 0);

		// Ceiling

		for (i = 0; i < 6; i++) {
			for (j = 0; j < 6; j++) {
				this.setBlock(x + i, y + 2, z + j, 5);
			}
		}

		// Roof - Layer 0

		this.setBlockAndMetadata(x + 4, y + 2, z + 5, 53, 1);
		this.setBlockAndMetadata(x + 5, y + 2, z + 5, 53, 3);

		for (i = -1; i < 5; i++) {
			this.setBlockAndMetadata(x + i, y + 2, z + 6, 53, 3);
		}

		for (i = -1; i < 6; i++) {
			this.setBlockAndMetadata(x - 1, y + 2, z + i, 53, 0);
		}

		for (i = 0; i < 7; i++) {
			this.setBlockAndMetadata(x + i, y + 2, z - 1, 53, 2);
		}

		for (i = 0; i < 6; i++) {
			this.setBlockAndMetadata(x + 6, y + 2, z + i, 53, 1);
		}

		// Roof - Layer 1

		for (i = 1; i < 5; i++) {
			for (j = 1; j < 5; j++) {
				this.setBlock(x + i, y + 3, z + j, 5);
			}
		}

		this.setBlockAndMetadata(x + 3, y + 3, z + 4, 53, 1);
		this.setBlockAndMetadata(x + 4, y + 3, z + 4, 53, 3);

		for (i = 0; i < 4; i++) {
			this.setBlockAndMetadata(x + i, y + 3, z + 5, 53, 3);
		}

		for (i = 0; i < 5; i++) {
			this.setBlockAndMetadata(x, y + 3, z + i, 53, 0);
		}

		for (i = 1; i < 6; i++) {
			this.setBlockAndMetadata(x + i, y + 3, z, 53, 2);
		}

		for (i = 1; i < 5; i++) {
			this.setBlockAndMetadata(x + 5, y + 3, z + i, 53, 1);
		}

		// Chest in Layer 1

		int rand = random.nextInt(14);
		final int chunkX = (new int[] { 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4,
				4 })[rand];
		final int chunkZ = (new int[] { 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 1, 2,
				3 })[rand];

		this.setBlock(x + chunkX, y + 3, z + chunkZ, 54);
		final TileEntityChest chest = (TileEntityChest) world
				.getBlockTileEntity(x + chunkX, y + 3, z + chunkZ);

		rand = random.nextInt(9) + 2;

		for (i = 0; i < rand; i++) {
			chest.setInventorySlotContents(
					random.nextInt(chest.getSizeInventory()), this.getLoot(0));
		}

		// Roof - Layer 2

		this.setBlock(x + 2, y + 4, z + 2, 5);
		this.setBlock(x + 3, y + 4, z + 2, 5);

		this.setBlockAndMetadata(x + 2, y + 4, z + 3, 53, 1);
		this.setBlockAndMetadata(x + 3, y + 4, z + 3, 53, 3);
		this.setBlockAndMetadata(x + 2, y + 4, z + 4, 53, 3);

		for (i = 2; i < 5; i++) {
			this.setBlockAndMetadata(x + 1, y + 4, z + i, 53, 0);
		}

		for (i = 1; i < 5; i++) {
			this.setBlockAndMetadata(x + i, y + 4, z + 1, 53, 2);
		}

		for (i = 2; i < 4; i++) {
			this.setBlockAndMetadata(x + 4, y + 4, z + i, 53, 1);
		}

		// Roof - Layer 3 (Top)

		this.setBlock(x + 2, y + 5, z + 2, 126);
		this.setBlock(x + 3, y + 5, z + 2, 126);

		// Inside

		for (i = 1; i < 5; i++) {
			for (j = 1; j < 4; j++) {
				for (k = 0; k < 2; k++) {
					this.setBlock(x + i, y + k, z + j, 0);
				}
			}
		}

		for (i = 1; i < 3; i++) {
			for (j = 0; j < 2; j++) {
				this.setBlock(x + i, y + j, z + 4, 0);
			}
		}
	}

	private void generateStairs() {
		int i, j, k, l;

		// Top Layer

		for (i = 0; i < 5; i++) {
			for (j = 0; j < 5; j++) {
				this.setBlock(x + i, y - 2, z + j, 4);
			}
		}

		for (i = 1; i < 4; i++) {
			this.setBlock(x + 3, y - 2, z + i, 0);
		}

		this.setBlock(x + 3, y - 2, z + 2, 50);
		this.setBlockAndMetadata(x + 2, y - 2, z + 1, 67, 1);

		// Stairs Down

		final int[] tmpX = new int[] { 2, 3, 3, 3, 2, 1, 1, 1 };
		final int[] tmpZ = new int[] { 1, 1, 2, 3, 3, 3, 2, 1 };
		int tmp, tmp2, height;

		for (l = 0; l < Options[0]; l++) {
			height = (l * 8) + 3;

			// Walls

			for (i = 0; i < 5; i++) {
				for (j = 0; j < 5; j++) {
					for (k = 0; k < 8; k++) {
						this.setBlock(
								x + i,
								y - height - k,
								z + j,
								(((i == 1) || (i == 3) || (j == 1) || (j == 3)) && !((i == 0)
										|| (i == 4) || (j == 0) || (j == 4))) ? 0
										: 4);
					}
				}
			}

			// Actual Stairs

			for (i = 0; i < 8; i++) {
				this.setBlock(x + tmpX[i], y - height - i, z + tmpZ[i], 4);

				tmp = (i + 1) % 8;
				tmp2 = i / 2;
				this.setBlockAndMetadata(x + tmpX[tmp], y - height - i, z
						+ tmpZ[tmp], 67, (tmp2 == 0) ? 3 : (tmp2 == 1) ? 0
						: (tmp2 == 2) ? 2 : 1);

				tmp = (i + 7) % 8;
				tmp2 = ((i + 1) / 2) % 4;
				this.setBlockAndMetadata(x + tmpX[tmp], y - height - i, z
						+ tmpZ[tmp], 67, (tmp2 == 0) ? 7 : (tmp2 == 1) ? 4
						: (tmp2 == 2) ? 6 : 5);

				if ((i % 2) == 1) {
					tmp = (i + 3) % 8;
					tmp2 = i / 2;
					this.setBlockAndMetadata(x + tmpX[tmp], y - height - i, z
							+ tmpZ[tmp], 50, (tmp2 == 0) ? 3 : (tmp2 == 1) ? 2
							: (tmp2 == 2) ? 4 : 1);
				}

			}
		}

		height = (Options[0] * 8) + 3;

		for (i = 0; i < 5; i++) {
			for (j = 0; j < 5; j++) {
				this.setBlock(x + i, y - height, z + j, 4);
			}
		}
	}

	private ItemStack getLoot(int lootId) {
		// Format: ItemStack, chance, min, rand1, rand2

		Object[][] loots = null;
		int size;

		switch (lootId) {
		case 0:
			loots = new Object[][] {
					{ new ItemStack(BrainStone.brainStone()), 0.1F, 1, 4, 5 },
					{ new ItemStack(BrainStone.dirtyBrainStone()), 1.0F, 1, 4,
							5 },
					{ new ItemStack(BrainStone.brainStoneDust()), 2.0F, 1, 6, 7 },
					{ new ItemStack(BrainStone.pulsatingBrainStone()), 0.05F,
							1, 1, 2 },
					{ new ItemStack(Item.diamond), 0.75F, 1, 4, 5 },
					{ new ItemStack(Item.emerald), 0.5F, 1, 4, 5 } };

			break;
		case 1:
			loots = new Object[][] {
					{ new ItemStack(BrainStone.brainStoneDust()), 0.1F, 1, 5, 6 },
					{ new ItemStack(Item.redstone), 1.0F, 3, 5, 7 },
					{ new ItemStack(Block.oreIron), 1.0F, 3, 2, 5 },
					{ new ItemStack(Item.dyePowder, 1, 4), 1.0F, 3, 5, 7 },
					{ new ItemStack(Block.oreGold), 1.0F, 1, 1, 4 },
					{ new ItemStack(Item.diamond), 0.2F, 1, 2, 2 },
					{ new ItemStack(Item.dyePowder, 1, 3), 1.0F, 3, 5, 7 },
					{ new ItemStack(Item.saddle), 0.2F, 1, 0, 0 },
					{ new ItemStack(Item.appleGold), 0.1F, 1, 0, 0 },
					{ new ItemStack(Item.appleGold, 1, 1), 0.01F, 1, 0, 0 } };

			break;
		case 2:
			loots = new Object[][] {
					{ new ItemStack(Block.cobblestoneMossy), 1.0F, 1, 19, 20 },
					{ new ItemStack(Block.glowStone), 1.0F, 1, 9, 10 },
					{ new ItemStack(Block.pumpkinLantern), 1.0F, 1, 9, 10 },
					{ new ItemStack(Block.ice), 1.0F, 1, 19, 20 },
					{ new ItemStack(Block.redstoneLampIdle), 1.0F, 1, 9, 10 },
					{ new ItemStack(Block.dragonEgg), 0.01F, 1, 0, 0 },
					{ new ItemStack(Item.netherStalkSeeds), 1.0F, 1, 9, 10 },
					{ new ItemStack(Item.slimeBall), 1.0F, 1, 9, 10 },
					{ new ItemStack(Item.book), 1.0F, 1, 9, 10 },
					{ new ItemStack(Item.blazeRod), 1.0F, 1, 2, 2 },
					{ new ItemStack(Item.enderPearl), 1.0F, 1, 2, 2 },

					{
							new ItemStack(BrainStone.brainStoneAxe(), 1,
									random.nextInt(5368)), 0.1F, 1, 0, 0 },
					{
							new ItemStack(BrainStone.brainStonePickaxe(), 1,
									random.nextInt(5368)), 0.1F, 1, 0, 0 },
					{
							new ItemStack(BrainStone.brainStoneShovel(), 1,
									random.nextInt(5368)), 0.1F, 1, 0, 0 },
					{
							new ItemStack(BrainStone.brainStoneHoe(), 1,
									random.nextInt(5368)), 0.1F, 1, 0, 0 },
					{
							new ItemStack(BrainStone.brainStoneSword(), 1,
									random.nextInt(5368)), 0.1F, 1, 0, 0 },
					{
							new ItemStack(BrainStone.brainStoneHelmet(), 1,
									random.nextInt(1824)), 0.1F, 1, 0, 0 },
					{
							new ItemStack(BrainStone.brainStonePlate(), 1,
									random.nextInt(1824)), 0.1F, 1, 0, 0 },
					{
							new ItemStack(BrainStone.brainStoneLeggings(), 1,
									random.nextInt(1824)), 0.1F, 1, 0, 0 },
					{
							new ItemStack(BrainStone.brainStoneBoots(), 1,
									random.nextInt(1824)), 0.1F, 1, 0, 0 },

					{
							EnchantmentHelper.addRandomEnchantment(random,
									new ItemStack(BrainStone.brainStoneAxe(),
											1, random.nextInt(5368)), 10),
							0.05F, 1, 0, 0 },
					{
							EnchantmentHelper.addRandomEnchantment(
									random,
									new ItemStack(BrainStone
											.brainStonePickaxe(), 1, random
											.nextInt(5368)), 10), 0.05F, 1, 0,
							0 },
					{
							EnchantmentHelper.addRandomEnchantment(random,
									new ItemStack(
											BrainStone.brainStoneShovel(), 1,
											random.nextInt(5368)), 10), 0.05F,
							1, 0, 0 },
					{
							EnchantmentHelper.addRandomEnchantment(random,
									new ItemStack(BrainStone.brainStoneSword(),
											1, random.nextInt(5368)), 10),
							0.05F, 1, 0, 0 },
					{
							EnchantmentHelper.addRandomEnchantment(random,
									new ItemStack(
											BrainStone.brainStoneHelmet(), 1,
											random.nextInt(1824)), 10), 0.05F,
							1, 0, 0 },
					{
							EnchantmentHelper.addRandomEnchantment(random,
									new ItemStack(BrainStone.brainStonePlate(),
											1, random.nextInt(1824)), 10),
							0.05F, 1, 0, 0 },
					{
							EnchantmentHelper.addRandomEnchantment(
									random,
									new ItemStack(BrainStone
											.brainStoneLeggings(), 1, random
											.nextInt(1824)), 10), 0.05F, 1, 0,
							0 },
					{
							EnchantmentHelper.addRandomEnchantment(random,
									new ItemStack(BrainStone.brainStoneBoots(),
											1, random.nextInt(1824)), 10),
							0.05F, 1, 0, 0 } };

			break;
		}

		if (loots == null)
			return null;

		float sum = 0.0F, rand, tmpChance;
		int i;
		ItemStack loot = null;
		Object[] tmpLoot;

		size = loots.length;

		for (i = 0; i < size; i++) {
			sum += (Float) loots[i][1];
		}

		rand = (float) MathHelper.getRandomDoubleInRange(random, 0.0, sum);

		for (i = 0; i < size; i++) {
			tmpLoot = loots[i];
			tmpChance = (Float) tmpLoot[1];

			if (rand < tmpChance) {
				loot = (ItemStack) tmpLoot[0];
				loot.stackSize = (Integer) tmpLoot[2]
						+ random.nextInt((Integer) tmpLoot[3] + 1)
						+ random.nextInt((Integer) tmpLoot[4] + 1);

				if (loot.itemID == Block.dragonEgg.blockID) {
					BSP.finest("Dragon Egg!!!!");
				}

				break;
			}

			rand -= tmpChance;
		}

		return loot;
	}

	private boolean isReplaceable(int x, int y, int z) {
		return world.getBlockMaterial(x, y, z).isReplaceable();
	}

	private boolean isSolid(int x, int y, int z) {
		return world.getBlockMaterial(x, y, z).isSolid();
	}

	/**
	 * Sets a Block at the specified Spot
	 */
	private void setBlock(int x, int y, int z, int blockId) {
		this.setBlockAndMetadata(x, y, z, blockId, 0);
	}

	/**
	 * Sets a Block with MetaData at the specified Spot
	 */
	private void setBlockAndMetadata(int x, int y, int z, int blockId,
			int metaData) {
		world.setBlock(x, y, z, blockId, metaData, 2);
	}
}
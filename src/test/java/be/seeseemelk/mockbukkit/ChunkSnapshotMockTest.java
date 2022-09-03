package be.seeseemelk.mockbukkit;

import be.seeseemelk.mockbukkit.block.data.AmethystClusterMock;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChunkSnapshotMockTest
{

	private World world;
	private Chunk chunk;

	@BeforeEach
	void setUp()
	{
		MockBukkit.mock();
		world = new WorldMock(Material.GRASS, 0, 319, 4);
		chunk = world.getChunkAt(1, 1);
	}

	@AfterEach
	void tearDown()
	{
		MockBukkit.unmock();
	}

	@Test
	void getX()
	{
		assertEquals(1, chunk.getChunkSnapshot().getX());
	}

	@Test
	void getZ()
	{
		assertEquals(1, chunk.getChunkSnapshot().getZ());
	}

	@Test
	void getWorldName()
	{
		assertEquals(world.getName(), chunk.getChunkSnapshot().getWorldName());
	}

	@Test
	void getBlockType()
	{
		assertEquals(Material.GRASS, chunk.getChunkSnapshot().getBlockType(0, 1, 0));
		assertEquals(Material.AIR, chunk.getChunkSnapshot().getBlockType(0, 10, 0));
	}

	@Test
	void getBlockData()
	{
		assertEquals(Material.GRASS, chunk.getChunkSnapshot().getBlockData(0, 1, 0).getMaterial());
		assertEquals(Material.AIR, chunk.getChunkSnapshot().getBlockData(0, 10, 0).getMaterial());
	}

	@Test
	void getBlockData_PreservesData()
	{
		AmethystClusterMock blockData = (AmethystClusterMock) Bukkit.createBlockData(Material.AMETHYST_CLUSTER);
		blockData.setWaterlogged(true);
		blockData.setFacing(BlockFace.SOUTH);
		chunk.getBlock(0, 1, 0).setBlockData(Bukkit.createBlockData(Material.AMETHYST_CLUSTER));

		AmethystClusterMock snapshotData = (AmethystClusterMock) chunk.getChunkSnapshot().getBlockData(0, 1, 0);

		assertEquals(Material.AMETHYST_CLUSTER, snapshotData.getMaterial());
		assertTrue(snapshotData.isWaterlogged());
		assertEquals(BlockFace.SOUTH, snapshotData.getFacing());
	}

	@Test
	void getFullTime()
	{
		long time = world.getFullTime();
		assertEquals(time, chunk.getChunkSnapshot().getCaptureFullTime());
	}

	@Test
	void isSectionEmpty()
	{
		assertFalse(chunk.getChunkSnapshot().isSectionEmpty(0));
		assertTrue(chunk.getChunkSnapshot().isSectionEmpty(1));
	}

	@Test
	void getBiome_DoesntIncludeBiome_ThrowsException()
	{
		assertThrowsExactly(IllegalStateException.class, () ->
		{
			chunk.getChunkSnapshot().getBiome(0, 0, 0);
		});
	}

	@Test
	void getBiome()
	{
		world.setBiome(0, 0, 0, Biome.BADLANDS);
		assertEquals(Biome.BADLANDS, chunk.getChunkSnapshot(false, true, false).getBiome(0, 0));
		assertEquals(Biome.BADLANDS, chunk.getChunkSnapshot(false, true, false).getBiome(0, 0, 0));
	}

}

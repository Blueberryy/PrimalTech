package primal_tech.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import primal_tech.ModBlocks;
import primal_tech.PrimalTech;
import primal_tech.blocks.BlockLeafBed;

public class ItemLeafBed extends Item {

	public ItemLeafBed() {
		setCreativeTab(PrimalTech.TAB);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flag) {
		list.add(TextFormatting.YELLOW + new TextComponentTranslation("tooltip.leaf_bed").getFormattedText());
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return EnumActionResult.SUCCESS;
		else if (facing != EnumFacing.UP)
			return EnumActionResult.FAIL;
		else {
			IBlockState iblockstate = world.getBlockState(pos);
			Block block = iblockstate.getBlock();
			boolean flag = block.isReplaceable(world, pos);

			if (!flag)
				pos = pos.up();

			int i = MathHelper.floor((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			EnumFacing enumfacing = EnumFacing.getHorizontal(i);
			BlockPos blockpos = pos.offset(enumfacing);
			ItemStack itemstack = player.getHeldItem(hand);

			if (player.canPlayerEdit(pos, facing, itemstack) && player.canPlayerEdit(blockpos, facing, itemstack)) {
				IBlockState iblockstate1 = world.getBlockState(blockpos);
				boolean flag1 = iblockstate1.getBlock().isReplaceable(world, blockpos);
				boolean flag2 = flag || world.isAirBlock(pos);
				boolean flag3 = flag1 || world.isAirBlock(blockpos);

				 if (flag2 && flag3 && world.getBlockState(pos.down()).isTopSolid() && world.getBlockState(blockpos.down()).isTopSolid()) {
					IBlockState iblockstate2 = ModBlocks.LEAF_BED.getDefaultState().withProperty(BlockLeafBed.OCCUPIED, Boolean.valueOf(false)).withProperty(BlockLeafBed.FACING, enumfacing).withProperty(BlockLeafBed.PART, BlockLeafBed.EnumPartType.FOOT);
					world.setBlockState(pos, iblockstate2, 10);
					world.setBlockState(blockpos, iblockstate2.withProperty(BlockLeafBed.PART, BlockLeafBed.EnumPartType.HEAD), 10);
					world.notifyNeighborsRespectDebug(pos, block, false);
					world.notifyNeighborsRespectDebug(blockpos, iblockstate1.getBlock(), false);
					SoundType soundtype = iblockstate2.getBlock().getSoundType(iblockstate2, world, pos, player);
					world.playSound((EntityPlayer) null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					itemstack.shrink(1);
					return EnumActionResult.SUCCESS;
				} else
					return EnumActionResult.FAIL;
			} else
				return EnumActionResult.FAIL;
		}
	}
}
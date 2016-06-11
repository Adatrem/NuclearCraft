package nc.tile.generator;

import nc.NuclearCraft;
import nc.block.NCBlocks;
import nc.block.generator.BlockFissionReactor;
import nc.handler.BombType;
import nc.handler.EntityBomb;
import nc.handler.NCExplosion;
import nc.item.NCItems;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class TileFissionReactor extends TileGenerator {
	
	private int tickCount = 0;
    public int complete;
    
    public int x0 = 0;
    public int y0 = 0;
    public int z0 = 0;
    public int x1 = 0;
    public int y1 = 0;
    public int z1 = 0;
    public int lx = 0;
    public int ly = 0;
    public int lz = 0;
    
    public int off = 0;
    
    public int E;
    public int H;
    public int EReal;
    public int HReal;
    public int FReal;
    public int energy;
    public int fueltime;
	public int fueltype;
	public int heat;
	public int efficiency;
	public int numberOfCells;
    private static double pMult = NuclearCraft.fissionRF;
    private static double hMult = NuclearCraft.fissionHeat;
    public String typeoffuel = StatCollector.translateToLocal("gui.noFuel");
    public int MBNumber;
    public String problem = StatCollector.translateToLocal("gui.casingIncomplete");
    private static final int[] slotsTop = new int[] {0, 1};
    private static final int[] slotsSides = new int[] {0, 1};

    public TileFissionReactor() {
		super("Fission Reactor", 25000000, 3);
	}

    public void updateEntity() {
    	super.updateEntity();
    	if(!this.worldObj.isRemote) {
    		checkStructure();
        	product();
    		fuel();
    		energy();
    		overheat();
    		addEnergy();
    	}
    	typeoffuelx();
    	if (flag != flag1) {
        	flag1 = flag;
        	BlockFissionReactor.updateBlockState(flag, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }
        markDirty();
        if (this.fueltime < 0) this.fueltime = 0;
    }
    
    public void overheat() {
    	if (this.heat >= 1000000) {
        	
    		if (NuclearCraft.nuclearMeltdowns) {
    			if (this.getBlockMetadata() == 4) NCExplosion.createExplosion(new EntityBomb(worldObj).setType(BombType.BOMB_STANDARD), worldObj, xCoord+lx/2, yCoord+ly/2, zCoord+lz/2, lx + ly + lz, lx + ly + lz, true);
            	else if (this.getBlockMetadata() == 2) NCExplosion.createExplosion(new EntityBomb(worldObj).setType(BombType.BOMB_STANDARD), worldObj, xCoord-lz/2, yCoord+ly/2, zCoord+lx/2, lx + ly + lz, lx + ly + lz, true);
            	else if (this.getBlockMetadata() == 5) NCExplosion.createExplosion(new EntityBomb(worldObj).setType(BombType.BOMB_STANDARD), worldObj, xCoord-lx/2, yCoord+ly/2, zCoord-lz/2, lx + ly + lz, lx + ly + lz, true);
            	else if (this.getBlockMetadata() == 3) NCExplosion.createExplosion(new EntityBomb(worldObj).setType(BombType.BOMB_STANDARD), worldObj, xCoord+lz/2, yCoord+ly/2, zCoord-lx/2, lx + ly + lz, lx + ly + lz, true);
    		} else this.heat = 1000000;
    	}
    }
    
    public void typeoffuelx() {
    	if (this.fueltype == 1) typeoffuel = StatCollector.translateToLocal("gui.LEU");
    	else if (this.fueltype == 2) typeoffuel = StatCollector.translateToLocal("gui.HEU");
    	else if (this.fueltype == 3) typeoffuel = StatCollector.translateToLocal("gui.LEP");
    	else if (this.fueltype == 4) typeoffuel = StatCollector.translateToLocal("gui.HEP");
    	else if (this.fueltype == 5) typeoffuel = StatCollector.translateToLocal("gui.MOX");
    	else if (this.fueltype == 6) typeoffuel = StatCollector.translateToLocal("gui.TBU");
    	else if (this.fueltype == 7) typeoffuel = StatCollector.translateToLocal("gui.LEU");
    	else if (this.fueltype == 8) typeoffuel = StatCollector.translateToLocal("gui.HEU");
    	else if (this.fueltype == 9) typeoffuel = StatCollector.translateToLocal("gui.LEP");
    	else if (this.fueltype == 10) typeoffuel = StatCollector.translateToLocal("gui.HEP");
    	else if (this.fueltype == 11) typeoffuel = StatCollector.translateToLocal("gui.MOX");
    	
    	else if (this.fueltype == 12) typeoffuel = StatCollector.translateToLocal("gui.LEU-Ox");
    	else if (this.fueltype == 13) typeoffuel = StatCollector.translateToLocal("gui.HEU-Ox");
    	else if (this.fueltype == 14) typeoffuel = StatCollector.translateToLocal("gui.LEP-Ox");
    	else if (this.fueltype == 15) typeoffuel = StatCollector.translateToLocal("gui.HEP-Ox");
    	else if (this.fueltype == 16) typeoffuel = StatCollector.translateToLocal("gui.LEU-Ox");
    	else if (this.fueltype == 17) typeoffuel = StatCollector.translateToLocal("gui.HEU-Ox");
    	else if (this.fueltype == 18) typeoffuel = StatCollector.translateToLocal("gui.LEP-Ox");
    	else if (this.fueltype == 19) typeoffuel = StatCollector.translateToLocal("gui.HEP-Ox");
    	
    	else if (this.fueltype == 0) typeoffuel = StatCollector.translateToLocal("gui.noFuel");
    	else typeoffuel = StatCollector.translateToLocal("gui.noFuel");
    }

    private void energy() {
    	double energyThisTick = 0;
    	double fuelThisTick = 0;
    	double heatThisTick = 0;
    	double fakeEnergyThisTick = 0;
    	double fakeHeatThisTick = 0;
    	double numberOfCells = 0;
    	double extraCells = 0;
    	double adj1 = 0;
    	double adj2 = 0;
    	double adj3 = 0;
    	double adj4 = 0;
    	double adj5 = 0;
    	double adj6 = 0;
    	double baseRF = 0;
    	double baseFuel = 0;
    	double baseHeat = 0;

    	if (tickCount >= NuclearCraft.fissionUpdateRate) {
	    	if (complete == 1) {
	        	for (int z = z0 + 1; z <= z1 - 1; z++) {
	        		for (int x = x0 + 1; x <= x1 - 1; x++) {
	        			for (int y = y0 + 1; y <= y1 - 1; y++) {
	        				if (find(NCBlocks.cellBlock, x, y, z)) {
	        					extraCells = 0;
	        					if (find(NCBlocks.cellBlock, x + 1, y, z)) extraCells += 1;
	        					if (find(NCBlocks.cellBlock, x - 1, y, z)) extraCells += 1;
	        					if (find(NCBlocks.cellBlock, x, y + 1, z)) extraCells += 1;
	        					if (find(NCBlocks.cellBlock, x, y - 1, z)) extraCells += 1;
	        					if (find(NCBlocks.cellBlock, x, y, z + 1)) extraCells += 1;
	        					if (find(NCBlocks.cellBlock, x, y, z - 1)) extraCells += 1;
	        					
	        					if (extraCells == 0) numberOfCells += 1;
	        					else if (extraCells == 1) adj1 += 1;
	        					else if (extraCells == 2) adj2 += 1;
	        					else if (extraCells == 3) adj3 += 1;
	        					else if (extraCells == 4) adj4 += 1;
	        					else if (extraCells == 5) adj5 += 1;
	        					else if (extraCells == 6) adj6 += 1;
	        				}
	        			}
	        		}
	        	}
	        }
	    	
	        if (this.getStackInSlot(1) == null && worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord) && this.fueltime > 0 && this.fueltype != 0 && complete == 1) {
	        	
	        	flag = true;
	        	off = 0;
	        	
	        	//LEU
	        	if (this.fueltype == 1 || this.fueltype == 7) {
	        		baseRF = NuclearCraft.baseRFLEU;
	            	baseFuel = NuclearCraft.baseFuelLEU;
	            	baseHeat = NuclearCraft.baseHeatLEU;
	        	}
	
	        	//HEU
	        	if (this.fueltype == 2 || this.fueltype == 8) {
	        		baseRF = NuclearCraft.baseRFHEU;
	            	baseFuel = NuclearCraft.baseFuelHEU;
	            	baseHeat = NuclearCraft.baseHeatHEU;
	        	}
	
	        	//LEP
	        	if (this.fueltype == 3 || this.fueltype == 9) {
	        		baseRF = NuclearCraft.baseRFLEP;
	            	baseFuel = NuclearCraft.baseFuelLEP;
	            	baseHeat = NuclearCraft.baseHeatLEP;
	        	}
	
	        	//HEP
	        	if (this.fueltype == 4 || this.fueltype == 10) {
	        		baseRF = NuclearCraft.baseRFHEP;
	            	baseFuel = NuclearCraft.baseFuelHEP;
	            	baseHeat = NuclearCraft.baseHeatHEP;
	        	}
	
	        	//MOX
	        	if (this.fueltype == 5 || this.fueltype == 11) {
	        		baseRF = NuclearCraft.baseRFMOX;
	            	baseFuel = NuclearCraft.baseFuelMOX;
	            	baseHeat = NuclearCraft.baseHeatMOX;
	        	}
	        	
	        	//TBU
	        	if (this.fueltype == 6) {
	        		baseRF = NuclearCraft.baseRFTBU;
	            	baseFuel = NuclearCraft.baseFuelTBU;
	            	baseHeat = NuclearCraft.baseHeatTBU;
	        	}
	        	
	        	//LEU-Ox
	        	if (this.fueltype == 12 || this.fueltype == 16) {
	        		baseRF = NuclearCraft.baseRFLEUOx;
	            	baseFuel = NuclearCraft.baseFuelLEUOx;
	            	baseHeat = NuclearCraft.baseHeatLEUOx;
	        	}
	
	        	//HEU-Ox
	        	if (this.fueltype == 13 || this.fueltype == 17) {
	        		baseRF = NuclearCraft.baseRFHEUOx;
	            	baseFuel = NuclearCraft.baseFuelHEUOx;
	            	baseHeat = NuclearCraft.baseHeatHEUOx;
	        	}
	
	        	//LEP-Ox
	        	if (this.fueltype == 14 || this.fueltype == 18) {
	        		baseRF = NuclearCraft.baseRFLEPOx;
	            	baseFuel = NuclearCraft.baseFuelLEPOx;
	            	baseHeat = NuclearCraft.baseHeatLEPOx;
	        	}
	
	        	//HEP-Ox
	        	if (this.fueltype == 15 || this.fueltype == 19) {
	        		baseRF = NuclearCraft.baseRFHEPOx;
	            	baseFuel = NuclearCraft.baseFuelHEPOx;
	            	baseHeat = NuclearCraft.baseHeatHEPOx;
	        	}
	        	
	        	energyThisTick += baseRF*(pMult/100 + this.heat/1000000)*(numberOfCells + 2*adj1 + 3*adj2 + 4*adj3 + 5*adj4 + 6*adj5 + 7*adj6)*Math.cbrt((lx - 2)*(ly - 2)*(lz - 2));
	        	heatThisTick += baseHeat*(numberOfCells + 3*adj1 + 6*adj2 + 10*adj3 + 15*adj4 + 21*adj5 + 28*adj6);
	        	fuelThisTick += (numberOfCells + adj1 + adj2 + adj3 + adj4 + adj5 + adj6)*baseFuel/NuclearCraft.fissionEfficiency;
	        	
	        	
	        	for (int z = z0 + 1; z <= z1 - 1; z++) {
	        		for (int x = x0 + 1; x <= x1 - 1; x++) {
	        			for (int y = y0 + 1; y <= y1 - 1; y++) {
	        				if(find(NCBlocks.graphiteBlock, x, y, z)) {
	        					energyThisTick += (pMult/100 + this.heat/1000000)*baseRF*(numberOfCells + adj1 + adj2 + adj3 + adj4 + adj5 + adj6)/10;
	        					heatThisTick += (hMult/100)*baseRF*(numberOfCells + adj1 + adj2 + adj3 + adj4 + adj5 + adj6)/5;
	        				}
	        				if(find(Blocks.water, x, y, z) && (numberOfCells + adj1 + adj2 + adj3 + adj4 + adj5 + adj6) > 0) {
	        					energyThisTick += 1; heatThisTick += 1;
	        				}
	        				if(find(NCBlocks.speedBlock, x, y, z) && (numberOfCells + adj1 + adj2 + adj3 + adj4 + adj5 + adj6) > 0) {
	        					if (lx - 2 + ly - 2 + lz - 2 > 0) fuelThisTick += 40000/(NuclearCraft.fissionEfficiency*(lx - 2 + ly - 2 + lz - 2));
	        				}
	        			}
	        		}
	        	}
	        
	        } else if(this.getStackInSlot(1) == null && !worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord) && this.fueltime > 0 && this.fueltype != 0 && complete == 1) {
	        	
	        	off = 1;
	        	
	        	//LEU
	        	if (this.fueltype == 1 || this.fueltype == 7) {
	        		baseRF = NuclearCraft.baseRFLEU;
	            	baseHeat = NuclearCraft.baseHeatLEU;
	        	}
	
	        	//HEU
	        	if (this.fueltype == 2 || this.fueltype == 8) {
	        		baseRF = NuclearCraft.baseRFHEU;
	            	baseHeat = NuclearCraft.baseHeatHEU;
	        	}
	
	        	//LEP
	        	if (this.fueltype == 3 || this.fueltype == 9) {
	        		baseRF = NuclearCraft.baseRFLEP;
	            	baseHeat = NuclearCraft.baseHeatLEP;
	        	}
	
	        	//HEP
	        	if (this.fueltype == 4 || this.fueltype == 10) {
	        		baseRF = NuclearCraft.baseRFHEP;
	            	baseHeat = NuclearCraft.baseHeatHEP;
	        	}
	
	        	//MOX
	        	if (this.fueltype == 5 || this.fueltype == 11) {
	        		baseRF = NuclearCraft.baseRFMOX;
	            	baseHeat = NuclearCraft.baseHeatMOX;
	        	}
	        	
	        	//TBU
	        	if (this.fueltype == 6) {
	        		baseRF = NuclearCraft.baseRFTBU;
	            	baseHeat = NuclearCraft.baseHeatTBU;
	        	}
	        	
	        	//LEU-Ox
	        	if (this.fueltype == 12 || this.fueltype == 16) {
	        		baseRF = NuclearCraft.baseRFLEUOx;
	            	baseHeat = NuclearCraft.baseHeatLEUOx;
	        	}
	
	        	//HEU-Ox
	        	if (this.fueltype == 13 || this.fueltype == 17) {
	        		baseRF = NuclearCraft.baseRFHEUOx;
	            	baseHeat = NuclearCraft.baseHeatHEUOx;
	        	}
	
	        	//LEP-Ox
	        	if (this.fueltype == 14 || this.fueltype == 18) {
	        		baseRF = NuclearCraft.baseRFLEPOx;
	            	baseHeat = NuclearCraft.baseHeatLEPOx;
	        	}
	
	        	//HEP-Ox
	        	if (this.fueltype == 15 || this.fueltype == 19) {
	        		baseRF = NuclearCraft.baseRFHEPOx;
	            	baseHeat = NuclearCraft.baseHeatHEPOx;
	        	}
	        	
	        	fakeEnergyThisTick += baseRF*(pMult/100 + this.heat/1000000)*(numberOfCells + 2*adj1 + 3*adj2 + 4*adj3 + 5*adj4 + 6*adj5 + 7*adj6)*Math.cbrt((lx - 2)*(ly - 2)*(lz - 2));
	    		fakeHeatThisTick += baseHeat*(hMult/100)*(numberOfCells + 3*adj1 + 6*adj2 + 10*adj3 + 15*adj4 + 21*adj5 + 28*adj6);
	        	
	    		for (int z = z0 + 1; z <= z1 - 1; z++) {
	        		for (int x = x0 + 1; x <= x1 - 1; x++) {
	        			for (int y = y0 + 1; y <= y1 - 1; y++) {
	        				if(find(NCBlocks.graphiteBlock, x, y, z)) {
	        					fakeEnergyThisTick += (pMult/100 + this.heat/1000000)*baseRF*(numberOfCells + adj1 + adj2 + adj3 + adj4 + adj5 + adj6)/10;
	        					fakeHeatThisTick += (hMult/100)*baseRF*(numberOfCells + adj1 + adj2 + adj3 + adj4 + adj5 + adj6)/5;
	        				}
	        				if(find(Blocks.water, x, y, z) && (numberOfCells + adj1 + adj2 + adj3 + adj4 + adj5 + adj6) > 0) {
	        					fakeEnergyThisTick += 1; fakeHeatThisTick += 1;
	        				}
	        			}
	        		}
	        	}
	        	flag = false;
	        } else {
	        	flag = false;
	        	off = 0;
	        }
	          	
	        if (complete == 1) {
	        	for (int z = z0 + 1; z <= z1 - 1; z++) {
	        		for (int x = x0 + 1; x <= x1 - 1; x++) {
	        			for (int y = y0 + 1; y <= y1 - 1; y++) {
	        				if(find(NCBlocks.coolerBlock, x, y, z)) {
	        					heatThisTick -= NuclearCraft.standardCool;
	        					if (surroundOr(NCBlocks.coolerBlock, x, y, z)) heatThisTick -= NuclearCraft.standardCool;
	        				}
	        				if(find(NCBlocks.waterCoolerBlock, x, y, z)) {
	        					heatThisTick -= NuclearCraft.waterCool;
	        					if (surroundOr(NCBlocks.reactorBlock, x, y, z)) heatThisTick -= NuclearCraft.waterCool;
	        				}
	        				if(find(NCBlocks.cryotheumCoolerBlock, x, y, z)) {
	        					heatThisTick -= NuclearCraft.cryotheumCool;
	        					if (surroundAnd(Blocks.air, x, y, z)) heatThisTick -= NuclearCraft.cryotheumCool;
	        				}
	        				if(find(NCBlocks.redstoneCoolerBlock, x, y, z)) {
	        					heatThisTick -= NuclearCraft.redstoneCool;
	        					if (surroundOr(NCBlocks.cellBlock, x, y, z)) heatThisTick -= NuclearCraft.redstoneCool;
	        				}
	        				if(find(NCBlocks.enderiumCoolerBlock, x, y, z)) {
	        					heatThisTick -= NuclearCraft.enderiumCool;
	        					if (surroundOr(NCBlocks.graphiteBlock, x, y, z)) heatThisTick -= NuclearCraft.enderiumCool;
	        				}
	        				if(find(NCBlocks.glowstoneCoolerBlock, x, y, z)) {
	        					heatThisTick -= NuclearCraft.glowstoneCool;
	        					if (surroundAnd(NCBlocks.graphiteBlock, x, y, z)) heatThisTick -= 3*NuclearCraft.glowstoneCool;
	        				}
	        				if(find(NCBlocks.heliumCoolerBlock, x, y, z)) {
	        					heatThisTick -= NuclearCraft.heliumCool;
	        				}
	        				if(find(NCBlocks.coolantCoolerBlock, x, y, z)) {
	        					heatThisTick -= NuclearCraft.coolantCool;
	        					if (surroundOr(Blocks.water, x, y, z)) heatThisTick -= NuclearCraft.coolantCool;
	        				}
	        				if(find(Blocks.water, x, y, z)) heatThisTick -= 1;
	        			}
	        		}
	        	}
	        	if (lx - 2 + ly - 2 + lz - 2 <= 1) heatThisTick -= NuclearCraft.baseHeatTBU;
	        }
	        E = (int) (energyThisTick + fakeEnergyThisTick);
	        EReal = (int) energyThisTick;
	        
	        FReal = (int) fuelThisTick;
	        
	        H = (int) (heatThisTick + fakeHeatThisTick);
	        HReal = (int) heatThisTick;
	        
	        if (complete == 1) efficiency = (int) (100*(energyThisTick + fakeEnergyThisTick)/(baseRF*(pMult/100)*(numberOfCells + adj1 + adj2 + adj3 + adj4 + adj5 + adj6)*Math.cbrt((lx - 2)*(ly - 2)*(lz - 2)))); else efficiency = 0;
	        
	        if (complete == 1) this.numberOfCells = (int) ((numberOfCells + adj1 + adj2 + adj3 + adj4 + adj5 + adj6)); else this.numberOfCells = 0;
	        
	        tickCount = 0;
		} else tickCount ++;
    	
    	if (EReal <= 0) flag = false;
        
        this.storage.receiveEnergy((int) EReal, false);
        this.fueltime -= FReal;
        if (this.heat + (int) HReal >= 0) this.heat += (int) HReal; else this.heat = 0;
        
        if (this.fueltime < 0) this.fueltime = 0;	
        if (this.fueltime == 0) E = 0;
	}
    
    public boolean findBasic(Block block, int x, int y, int z) {
    	return find(block, x, y, z);
    }
    
    public boolean surroundOr(Block block, int x, int y, int z) {
    	return (findBasic(block, x + 1, y, z) || findBasic(block, x - 1, y, z) || findBasic(block, x, y + 1, z) || findBasic(block, x, y - 1, z) || findBasic(block, x, y, z + 1) || findBasic(block, x, y, z - 1));
    }
    
    public boolean surroundAnd(Block block, int x, int y, int z) {
    	return (findBasic(block, x + 1, y, z) && findBasic(block, x - 1, y, z) && findBasic(block, x, y + 1, z) && findBasic(block, x, y - 1, z) && findBasic(block, x, y, z + 1) && findBasic(block, x, y, z - 1));
    }

	public boolean multiblockstring() {
    	if (complete == 1) {
    		return true;
    	} return false;
	}

	private void fuel() {
    	ItemStack stack = this.getStackInSlot(0);
    	ItemStack pstack = this.getStackInSlot(1);

        if (stack != null && pstack == null && isFuel(stack) && this.fueltime == 0 && this.fueltype == 0) {
            this.fueltime += fuelValue(stack);
            
            this.fueltype = TileFissionReactor.setfueltype(stack);
            
            --this.slots[0].stackSize;

            if (this.slots[0].stackSize <= 0) {
                this.slots[0] = null;
            }
            off = 1;
        }
    }
    
    private void product() {
        if (this.slots[1] == null && this.fueltime <= 0 && this.fueltype != 0) {    
        	if (this.fueltype == 1) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 22); this.fueltype = 0;
        	} else if (this.fueltype == 2) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 23); this.fueltype = 0;
        	} else if (this.fueltype == 3) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 24); this.fueltype = 0;
        	} else if (this.fueltype == 4) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 25); this.fueltype = 0;
        	} else if (this.fueltype == 5) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 26); this.fueltype = 0;
        	} else if (this.fueltype == 6) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 27); this.fueltype = 0;
        	} else if (this.fueltype == 7) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 28); this.fueltype = 0;
        	} else if (this.fueltype == 8) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 29); this.fueltype = 0;
        	} else if (this.fueltype == 9) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 30); this.fueltype = 0;
        	} else if (this.fueltype == 10) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 31); this.fueltype = 0;
        	} else if (this.fueltype == 11) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 32); this.fueltype = 0;
        	} else if (this.fueltype == 12) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 67); this.fueltype = 0;
        	} else if (this.fueltype == 13) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 68); this.fueltype = 0;
        	} else if (this.fueltype == 14) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 69); this.fueltype = 0;
        	} else if (this.fueltype == 15) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 70); this.fueltype = 0;
        	} else if (this.fueltype == 16) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 71); this.fueltype = 0;
        	} else if (this.fueltype == 17) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 72); this.fueltype = 0;
        	} else if (this.fueltype == 18) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 73); this.fueltype = 0;
        	} else if (this.fueltype == 19) {
        		this.slots[1] = new ItemStack(NCItems.fuel, 1, 74); this.fueltype = 0;
        	} else {
        	}
        	this.fueltime = 0;
        }
    }
    
    public static boolean isFuel(ItemStack stack) {
        return fuelValue(stack) > 0 && setfueltype(stack) != 0;
    }
    
    public static boolean isUpgrade(ItemStack stack) {
        return stack.getItem() == NCItems.upgrade;
    }
    
    public static int fuelValue(ItemStack stack) {
    	if (stack == null) return 0; else {
    		Item item = stack.getItem();
        	
        	if(item == new ItemStack (NCItems.fuel, 1, 11).getItem() && item.getDamage(stack) == 11) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 12).getItem() && item.getDamage(stack) == 12) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 13).getItem() && item.getDamage(stack) == 13) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 14).getItem() && item.getDamage(stack) == 14) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 15).getItem() && item.getDamage(stack) == 15) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 16).getItem() && item.getDamage(stack) == 16) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 17).getItem() && item.getDamage(stack) == 17) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 18).getItem() && item.getDamage(stack) == 18) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 19).getItem() && item.getDamage(stack) == 19) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 20).getItem() && item.getDamage(stack) == 20) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 21).getItem() && item.getDamage(stack) == 21) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 59).getItem() && item.getDamage(stack) == 59) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 60).getItem() && item.getDamage(stack) == 60) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 61).getItem() && item.getDamage(stack) == 61) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 62).getItem() && item.getDamage(stack) == 62) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 63).getItem() && item.getDamage(stack) == 63) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 64).getItem() && item.getDamage(stack) == 64) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 65).getItem() && item.getDamage(stack) == 65) return 10000000;
        	else if(item == new ItemStack (NCItems.fuel, 1, 66).getItem() && item.getDamage(stack) == 66) return 10000000;
        	return 0;
        }
	}
    
    public static int setfueltype(ItemStack stack) {
    	if (stack == null) return 0; else {
    		Item item = stack.getItem();
        	
    		if(item == new ItemStack (NCItems.fuel, 1, 11).getItem() && item.getDamage(stack) == 11) return 1;
    		else if(item == new ItemStack (NCItems.fuel, 1, 12).getItem() && item.getDamage(stack) == 12) return 2;
    		else if(item == new ItemStack (NCItems.fuel, 1, 13).getItem() && item.getDamage(stack) == 13) return 3;
    		else if(item == new ItemStack (NCItems.fuel, 1, 14).getItem() && item.getDamage(stack) == 14) return 4;
    		else if(item == new ItemStack (NCItems.fuel, 1, 15).getItem() && item.getDamage(stack) == 15) return 5;
    		else if(item == new ItemStack (NCItems.fuel, 1, 16).getItem() && item.getDamage(stack) == 16) return 6;
    		else if(item == new ItemStack (NCItems.fuel, 1, 17).getItem() && item.getDamage(stack) == 17) return 7;
     		else if(item == new ItemStack (NCItems.fuel, 1, 18).getItem() && item.getDamage(stack) == 18) return 8;
     		else if(item == new ItemStack (NCItems.fuel, 1, 19).getItem() && item.getDamage(stack) == 19) return 9;
     		else if(item == new ItemStack (NCItems.fuel, 1, 20).getItem() && item.getDamage(stack) == 20) return 10;
     		else if(item == new ItemStack (NCItems.fuel, 1, 21).getItem() && item.getDamage(stack) == 21) return 11;
     		else if(item == new ItemStack (NCItems.fuel, 1, 59).getItem() && item.getDamage(stack) == 59) return 12;
     		else if(item == new ItemStack (NCItems.fuel, 1, 60).getItem() && item.getDamage(stack) == 60) return 13;
     		else if(item == new ItemStack (NCItems.fuel, 1, 61).getItem() && item.getDamage(stack) == 61) return 14;
     		else if(item == new ItemStack (NCItems.fuel, 1, 62).getItem() && item.getDamage(stack) == 62) return 15;
     		else if(item == new ItemStack (NCItems.fuel, 1, 63).getItem() && item.getDamage(stack) == 63) return 16;
     		else if(item == new ItemStack (NCItems.fuel, 1, 64).getItem() && item.getDamage(stack) == 64) return 17;
     		else if(item == new ItemStack (NCItems.fuel, 1, 65).getItem() && item.getDamage(stack) == 65) return 18;
     		else if(item == new ItemStack (NCItems.fuel, 1, 66).getItem() && item.getDamage(stack) == 66) return 19;
    		return 0;
    	}
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("storage")) this.storage.readFromNBT(nbt.getCompoundTag("storage"));
        this.typeoffuel = nbt.getString("Typeoffuel");
        this.problem = nbt.getString("problem");
        this.fueltime = nbt.getInteger("Fueltime");
        this.fueltype = nbt.getInteger("Fueltype");
        
        this.x0 = nbt.getInteger("x0");
        this.y0 = nbt.getInteger("y0");
        this.z0 = nbt.getInteger("z0");
        this.x1 = nbt.getInteger("x1");
        this.y1 = nbt.getInteger("y1");
        this.z1 = nbt.getInteger("z1");
        this.lx = nbt.getInteger("lx");
        this.ly = nbt.getInteger("ly");
        this.lz = nbt.getInteger("lz");
        
        this.E = nbt.getInteger("E");
        this.H = nbt.getInteger("H");
        this.off = nbt.getInteger("off");
        this.MBNumber = nbt.getInteger("MBN");
        this.heat = nbt.getInteger("Heat");
        this.efficiency = nbt.getInteger("efficiency");
        this.numberOfCells = nbt.getInteger("numberOfCells");
        this.EReal = nbt.getInteger("EReal");
        this.HReal = nbt.getInteger("HReal");
        this.FReal = nbt.getInteger("FReal");
        this.complete = nbt.getInteger("complete");
    }

    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound energyTag = new NBTTagCompound();
		this.storage.writeToNBT(energyTag);
		nbt.setTag("storage", energyTag);
        nbt.setInteger("Fueltime", this.fueltime);
        nbt.setInteger("Fueltype", this.fueltype);
        
        nbt.setInteger("x0", this.x0);
        nbt.setInteger("y0", this.y0);
        nbt.setInteger("z0", this.z0);
        nbt.setInteger("x1", this.x1);
        nbt.setInteger("y1", this.y1);
        nbt.setInteger("z1", this.z1);
        nbt.setInteger("lx", this.lx);
        nbt.setInteger("ly", this.ly);
        nbt.setInteger("lz", this.lz);
        
        nbt.setInteger("E", this.E);
        nbt.setInteger("H", this.H);
        nbt.setInteger("off", this.off);
        nbt.setInteger("MBN", this.MBNumber);
        nbt.setInteger("Heat", this.heat);
        nbt.setInteger("efficiency", this.efficiency);
        nbt.setInteger("numberOfCells", this.numberOfCells);
        nbt.setString("Typeoffuel", this.typeoffuel);
        nbt.setString("problem", this.problem);
        nbt.setInteger("EReal", this.EReal);
        nbt.setInteger("HReal", this.HReal);
        nbt.setInteger("FReal", this.FReal);
        nbt.setInteger("complete", this.complete);
    }

    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == 0) return isFuel(stack);
        else return false;
    }

    public int[] getAccessibleSlotsFromSide(int slot) {
    	return slot == 0 ? slotsSides : slotsTop;
    }

    public boolean canExtractItem(int slot, ItemStack stack, int slots) {
        return slot == 1;
    }
    
    private boolean find(Block block, int x, int y, int z) {
    	int xc = xCoord;
    	int yc = yCoord + y;
    	int zc = zCoord;
    	
    	if (this.getBlockMetadata() == 4) return (worldObj.getBlock(xc+x, yc, zc+z) == block);
    	else if (this.getBlockMetadata() == 2) return (worldObj.getBlock(xc-z, yc, zc+x) == block);
    	else if (this.getBlockMetadata() == 5) return (worldObj.getBlock(xc-x, yc, zc-z) == block);
    	else if (this.getBlockMetadata() == 3) return (worldObj.getBlock(xc+z, yc, zc-x) == block);
    	else return false;
    }
    
    private boolean find(Block block, Block block2, int x, int y, int z) {
    	int xc = xCoord;
    	int yc = yCoord + y;
    	int zc = zCoord;
    	
    	if (this.getBlockMetadata() == 4) return (worldObj.getBlock(xc+x, yc, zc+z) == block || worldObj.getBlock(xc+x, yc, zc+z) == block2);
    	else if (this.getBlockMetadata() == 2) return (worldObj.getBlock(xc-z, yc, zc+x) == block || worldObj.getBlock(xc-z, yc, zc+x) == block2);
    	else if (this.getBlockMetadata() == 5) return (worldObj.getBlock(xc-x, yc, zc-z) == block || worldObj.getBlock(xc-x, yc, zc-z) == block2);
    	else if (this.getBlockMetadata() == 3) return (worldObj.getBlock(xc+z, yc, zc-x) == block || worldObj.getBlock(xc+z, yc, zc-x) == block2);
    	else return false;
    }
    
    private boolean checkStructure() {
    	if (tickCount >= NuclearCraft.fissionUpdateRate) {
	    	int l = NuclearCraft.fissionMaxLength + 2;
	    	Block b = NCBlocks.reactorBlock;
	    	Block r = NCBlocks.fissionReactorGraphiteIdle;
	    	Block rr = NCBlocks.fissionReactorGraphiteActive;
	    	boolean f = false;
	    	int rz = 0;
	    	int z0 = 0;
	    	int x0 = 0;
	    	int y0 = 0;
	    	int z1 = 0;
	    	int x1 = 0;
	    	int y1 = 0;
	    	for (int z = 0; z <= l; z++) {
	    		if (!find(b, 0, 0, -z) && !find(b, 0, 1, -z) && !find(b, 0, -1, -z) && (find(b, 0, 0, -z + 1) || find(b, 0, 1, -z + 1) || find(b, 0, -1, -z + 1))) {
	    			rz = l - z;
	    			z0 = -z;
	    			f = true;
	    			break;
	    		}
	    	}
	    	if (!f) {
	    		complete = 0; problem = StatCollector.translateToLocal("gui.casingIncomplete"); return false;
	    	}
	    	f = false;
	    	for (int y = 0; y <= l; y++) {
	    		if (!find(b, x0, -y, z0) && !find(b, x0, -y + 1, z0) && !find(b, x0 + 1, -y, z0) && !find(b, x0, -y, z0 + 1) && find(b, x0 + 1, -y, z0 + 1) && find(b, x0, -y + 1, z0 + 1) && find(b, x0 + 1, -y + 1, z0)) {
	    			y0 = -y;
	    			f = true;
	    			break;
	    		}
	    	}
	    	if (!f) {
	    		complete = 0; problem = StatCollector.translateToLocal("gui.casingIncomplete"); return false;
	    	}
	    	f = false;
	    	for (int z = 0; z <= rz; z++) {
	    		if (!find(b, x0, y0, z0 + z) && !find(b, x0, y0 + 1, z0 + z) && !find(b, x0 + 1, y0, z0 + z) && !find(b, x0, y0, z0 + z - 1) && find(b, x0 + 1, y0, z0 + z - 1) && find(b, x0, y0 + 1, z0 + z - 1) && find(b, x0 + 1, y0 + 1, z0 + z)) {
	    			z1 = z0 + z;
	    			f = true;
	    			break;
	    		}
	    	}
	    	if (!f) {
	    		complete = 0; problem = StatCollector.translateToLocal("gui.casingIncomplete"); return false;
	    	}
	    	f = false;
	    	for (int x = 0; x <= l; x++) {
	    		if (!find(b, x0 + x, y0, z0) && !find(b, x0 + x, y0 + 1, z0) && !find(b, x0 + x - 1, y0, z0) && !find(b, x0 + x, y0, z0 + 1) && find(b, x0 + x - 1, y0, z0 + 1) && find(b, x0 + x, y0 + 1, z0 + 1) && find(b, x0 + x - 1, y0 + 1, z0)) {
	    			x1 = x0 + x;
	    			f = true;
	    			break;
	    		}
	    	}
	    	if (!f) {
	    		complete = 0; problem = StatCollector.translateToLocal("gui.casingIncomplete"); return false;
	    	}
	    	f = false;
	    	for (int y = 0; y <= l; y++) {
	    		if (!find(b, x0, y0 + y, z0) && !find(b, x0, y0 + y - 1, z0) && !find(b, x0 + 1, y0 + y, z0) && !find(b, x0, y0 + y, z0 + 1) && find(b, x0 + 1, y0 + y, z0 + 1) && find(b, x0, y0 + y - 1, z0 + 1) && find(b, x0 + 1, y0 + y - 1, z0)) {
	    			y1 = y0 + y;
	    			f = true;
	    			break;
	    		}
	    	}
	    	if (!f) {
	    		complete = 0; problem = StatCollector.translateToLocal("gui.casingIncomplete"); return false;
	    	}
	    	f = false;
	    	if ((x0 > 0 || x1 < 0) || (y0 > 0 || y1 < 0) || (z0 > 0 || z1 < 0)) {
	    		problem = StatCollector.translateToLocal("gui.casingIncomplete");
	    		complete = 0;
				return false;
	    	}
	    	for (int z = z0; z <= z1; z++) {
	    		for (int x = x0; x <= x1; x++) {
	    			for (int y = y0; y <= y1; y++) {
	    				if(find(r, rr, x, y, z)) {
	    					if (x == 0 && y == 0 && z == 0) {} else {
	    						problem = StatCollector.translateToLocal("gui.multipleControllers");
	    						complete = 0;
	    						return false;
	    					}
	    				}
	    			}
	    		}
	    	}
	    	for (int z = z0 + 1; z <= z1 - 1; z++) {
	    		for (int x = x0 + 1; x <= x1 - 1; x++) {
	    			if(!find(b, x, y0, z) && !(x == 0 && y0 == 0 && z == 0)) {
	    				problem = StatCollector.translateToLocal("gui.casingIncomplete");
	    				complete = 0;
	    				return false;
	    			}
	    			if(!find(b, x, y1, z) && !(x == 0 && y1 == 0 && z == 0)) {
	    				problem = StatCollector.translateToLocal("gui.casingIncomplete");
	    				complete = 0;
	    				return false;
	    			}
	    		}
	    	}
	    	for (int y = y0 + 1; y <= y1 - 1; y++) {
	    		for (int x = x0 + 1; x <= x1 - 1; x++) {
	    			if(!find(b, x, y, z0) && !(x == 0 && y == 0 && z0 == 0)) {
	    				problem = StatCollector.translateToLocal("gui.casingIncomplete");
	    				complete = 0;
	    				return false;
	    			}
	    			if(!find(b, x, y, z1) && !(x == 0 && y == 0 && z1 == 0)) {
	    				problem = StatCollector.translateToLocal("gui.casingIncomplete");
	    				complete = 0;
	    				return false;
	    			}
	    		}
	    		for (int z = z0 + 1; z <= z1 - 1; z++) {
	    			if(!find(b, x0, y, z) && !(x0 == 0 && y == 0 && z == 0)) {
	    				problem = StatCollector.translateToLocal("gui.casingIncomplete");
	    				complete = 0;
	    				return false;
	    			}
	    			if(!find(b, x1, y, z) && !(x1 == 0 && y == 0 && z == 0)) {
	    				problem = StatCollector.translateToLocal("gui.casingIncomplete");
	    				complete = 0;
	    				return false;
	    			}
	    		}
	    	}
	    	for (int z = z0 + 1; z <= z1 - 1; z++) {
	    		for (int x = x0 + 1; x <= x1 - 1; x++) {
	    			for (int y = y0 + 1; y <= y1 - 1; y++) {
	    				if(find(b, x, y, z)) {
	    					problem = StatCollector.translateToLocal("gui.casingInInterior");
	    					complete = 0;
	    					return false;
	    				}
	    			}
	    		}
	    	}
	    	StatCollector.translateToLocal("gui.casingIncomplete");
	    	complete = 1;
	    	tickCount = 0;
	    	this.x0 = x0;
	    	this.y0 = y0;
	    	this.z0 = z0;
	    	this.x1 = x1;
	    	this.y1 = y1;
	    	this.z1 = z1;
	    	lx = x1 + 1 - x0;
	    	ly = y1 + 1 - y0;
	    	lz = z1 + 1 - z0;
	    	return true;
		} else {
			tickCount ++;
			return complete == 1;
		}
    }
}
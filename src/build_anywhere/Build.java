package build_anywhere;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import static com.fs.starfarer.api.impl.campaign.rulecmd.salvage.Objectives.SALVAGE_FRACTION;
import com.fs.starfarer.api.util.Misc;

public class Build
{
	//==== STATIC ==============================================================
	//==== PUBLIC ==============================================================
	public static String BA_FLAG = "$Built_Literaly_Anywhere";
	
	public static void build(SectorEntityToken builder, String objectType)
	{
		if(!canBuild(builder, objectType)) return;
		
		LocationAPI loc = builder.getContainingLocation();
		SectorEntityToken built = loc.addCustomEntity(null, null,objectType, builder.getFaction().getId());
		if (builder.getOrbit() != null)
			built.setOrbit(builder.getOrbit().makeCopy());
		
		built.setLocation(builder.getLocation().x, builder.getLocation().y);
		built.getMemoryWithoutUpdate().set(BA_FLAG, true);
		
		Global.getSoundPlayer().playUISound("ui_objective_constructed", 1f, 1f);
	}
	
	public static void printCost(TextPanelAPI text, SectorEntityToken builder, String type)
	{
		//printDescription(type);

		Misc.showCost(text, null, null, getConstructionResources(type), getConstructionQuantities(type));
		
		if (canBuild(builder, type)) {
			text.addPara("Proceed with construction?");
		} else {
			text.addPara("You do not have the necessary resources to build this structure.");
		}
	}
	
	public static String [] getConstructionResources(String type)
	{
		switch(type)
		{
			case Tags.COMM_RELAY+"_"+Tags.MAKESHIFT:
			case Tags.SENSOR_ARRAY+"_"+Tags.MAKESHIFT:
			case Tags.NAV_BUOY+"_"+Tags.MAKESHIFT:
				return new String[] {Commodities.HEAVY_MACHINERY, Commodities.METALS, Commodities.RARE_METALS};
			default:
				return new String[]{};
		}
	}
	public static int [] getConstructionQuantities(String type)
	{
		switch(type)
		{
			case Tags.COMM_RELAY+"_"+Tags.MAKESHIFT:
			case Tags.SENSOR_ARRAY+"_"+Tags.MAKESHIFT:
			case Tags.NAV_BUOY+"_"+Tags.MAKESHIFT:
				return new int[] {20, 100, 10};
			default:
				return new int[]{};
		}
	}
	public static int [] getSalvageQuantities(String type)
	{
		int [] constructQuantities = getConstructionQuantities(type);
		for (int i = 0; i < constructQuantities.length; i++)
			constructQuantities[i] *= SALVAGE_FRACTION;
		return constructQuantities;
	}
	
	public static boolean canBuild(SectorEntityToken builder, String type){return canBuild(builder, type, 1f);}
	public static boolean canBuild(SectorEntityToken builder, String type, Float percentage)
	{
		
		CargoAPI cargo = builder.getCargo();
		String [] res = getConstructionResources(type);
		int [] quantities = getConstructionQuantities(type);
		for (int i = 0; i < res.length; i++) {
			String commodityId = res[i];
			int quantity = calcAmount(quantities[i], percentage);
			if (quantity > cargo.getQuantity(CargoAPI.CargoItemType.RESOURCES, commodityId))
				return false;
		}
		return true;
	}
	
	public static void removeBuildCosts(SectorEntityToken buider, String objectType){removeBuildCosts(buider, objectType, 1f);}
	public static void removeBuildCosts(SectorEntityToken buider, String objectType, Float percentage)
	{
		CargoAPI cargo = buider.getCargo();
		String [] res = getConstructionResources(objectType);
		int [] quantities = getConstructionQuantities(objectType);
		for (int i = 0; i < res.length; i++) {
			String commodityId = res[i];
			int quantity = calcAmount(quantities[i], percentage);
			cargo.removeCommodity(commodityId, quantity);
		}
	}
	
	//==== PROTECTED ===========================================================
	protected static int calcAmount(int initial, Float percentage)
	{
		if(percentage < 0) percentage = 0f;
		if(percentage == null) percentage = 1f;
		
		int res = (int)(initial * percentage);
		return res>=0? res : 0;
	}
	
	public static void buildMakeshiftStation(SectorEntityToken builder)
	{
		builder.getContainingLocation().addCustomEntity(null, "Player station "+(int)(Math.random()*100), Entities.MAKESHIFT_STATION, builder.getFaction().getId())
			.setLocation(builder.getLocation().x, builder.getLocation().y);
	}
	
	public static void buildMiningStation(SectorEntityToken builder)
	{
		builder.getContainingLocation().addCustomEntity(null, "Player station "+(int)(Math.random()*100), Entities.STATION_MINING, builder.getFaction().getId())
			.setLocation(builder.getLocation().x, builder.getLocation().y);
	}
	
	public static void buildStationFromIndustry(SectorEntityToken builder)
	{
		builder.getContainingLocation().addCustomEntity(null, "Player station "+(int)(Math.random()*100), Entities.STATION_BUILT_FROM_INDUSTRY, builder.getFaction().getId())
			.setLocation(builder.getLocation().x, builder.getLocation().y);
	}
	
	public static void buildResearchStation(SectorEntityToken builder)
	{
		builder.getContainingLocation().addCustomEntity(null, "Player station "+(int)(Math.random()*100), Entities.STATION_RESEARCH, builder.getFaction().getId())
			.setLocation(builder.getLocation().x, builder.getLocation().y);
	}
	
	/*
	
	public static void buildMakeshiftStation(SectorEntityToken builder)
	{
		Global.getSector().getCampaignUI().getCurrentInteractionDialog().getTextPanel()
			.addPara(
				builder.getContainingLocation().addCustomEntity(null, "Player station "+(int)(Math.random()*100), Entities.MAKESHIFT_STATION, builder.getFaction().getId()).getFullName()
			);
	}
	
	public static void buildMiningStation(SectorEntityToken builder)
	{
		Global.getSector().getCampaignUI().getCurrentInteractionDialog().getTextPanel()
			.addPara(
				builder.getContainingLocation().addCustomEntity(null, "Player station "+(int)(Math.random()*100), Entities.STATION_MINING, builder.getFaction().getId()).getFullName()
			);
	}
	
	public static void buildStationFromIndustry(SectorEntityToken builder)
	{
		Global.getSector().getCampaignUI().getCurrentInteractionDialog().getTextPanel()
			.addPara(
				builder.getContainingLocation().addCustomEntity(null, "Player station "+(int)(Math.random()*100), Entities.STATION_BUILT_FROM_INDUSTRY, builder.getFaction().getId()).getFullName()
			);
	}
	
	public static void buildResearchStation(SectorEntityToken builder)
	{
		Global.getSector().getCampaignUI().getCurrentInteractionDialog().getTextPanel()
			.addPara(
				builder.getContainingLocation().addCustomEntity(null, "Player station "+(int)(Math.random()*100), Entities.STATION_RESEARCH, builder.getFaction().getId()).getFullName()
			);
	}
	
	 */
}

package build_anywhere.campaign.abilities;

import build_anywhere.Wait;
import build_anywhere.screens.BuildSomethingDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import java.util.HashMap;
import java.util.List;
import static build_anywhere.BAUtils.settingsGetFloatOr;

import build_anywhere.BAUtils;
public class Construct extends BaseDurationAbility {
	//==== STATIC ==============================================================
	public static final String
		FIGHTERS = FleetMemberType.FIGHTER_WING.name().toLowerCase(),
			
		HULL_MOD = "repair_gantry",
		HULL_MOD_BASE_VALUE = "ba_hull_mod_base_value",
		HULL_MOD_BUILT_IN_MULT = "ba_hull_mod_built_in_multiplier",
		HULL_MOD_FIGHTERS_MULT = "ba_hull_mod_"+FIGHTERS+"_multiplier",
		HULL_MOD_ON_FIGHTERS_AND_CARRIER_BONUS_MULT = "ba_hull_mod_on_fighters_and_carrier_bonus_multiplier",
		
		//TODO: Add sensor to the bargain sometime.
		CONSTRUCTION_DETECTABILITY_FLAT = "ba_construction_detectability_flat",
		CONSTRUCTION_DETECTABILITY_MULT = "ba_construction_detectability_multiplier";
		
	//Non final because someone might want to play with those values midgame with code ;)
	public static Float
		hullModBaseValue = settingsGetFloatOr(HULL_MOD_BASE_VALUE, null),
		hullModBuiltInMult = settingsGetFloatOr(HULL_MOD_BUILT_IN_MULT, 1f),
		hullModFightersMult = settingsGetFloatOr(HULL_MOD_FIGHTERS_MULT, 0f),
		hullModOnFightersAndCarrierBonusMult = settingsGetFloatOr(HULL_MOD_ON_FIGHTERS_AND_CARRIER_BONUS_MULT, 0f);
	
	public HashMap<ShipAPI.HullSize, Float> hullModShipClassMult = new HashMap(){{
		for(ShipAPI.HullSize size: ShipAPI.HullSize.values())
			put(size, settingsGetFloatOr("ba_hull_mod_"+size.toString().toLowerCase()+"_multiplier", 0f));
	}};
	
	
	//==== PUBLIC ==============================================================
	@Override protected String getActivationText()
	{
		return "Constructing";
	}

	@Override protected void activateImpl()
	{
		//if (entity.isPlayerFleet())
		//wait.wait(buildSomethingDialog.getDialog(), 0, id);
	}

	@Override public void advance(float amount)
	{
		super.advance(amount);
		
		if (constructionInterv != null && getFleet() != null && getFleet().isPlayerFleet()) {
			float days = Global.getSector().getClock().convertToDays(amount);
			constructionInterv.advance(days);
			if (constructionInterv.intervalElapsed()) {
//				WeightedRandomPicker<String> picker = new WeightedRandomPicker<String>();
//				
//				picker.add(Commodities.CREW, 1);
//				picker.add(Commodities.CREW, 1);
				
//				Color color = Misc.setAlpha(entity.getIndicatorColor(), 255);
//				
//				CargoAPI cargo = getFleet().getCargo();
//				float r = (float) Math.random();
//				if (r > 0.9f) {
//					cargo.addCommodity(Commodities.BETA_CORE, 1);
//					entity.addFloatingText("Found beta core", color, 0.5f);
//					Global.getSoundPlayer().playUISound("ui_cargo_metals_drop", 1f, 1f);
//				} else if (r > 0.5f) {
//					int qty = (int) (5f + 5f * (float) Math.random());
//					cargo.addCommodity(Commodities.METALS, qty);
//					entity.addFloatingText("Found " + qty + " metal", color, 0.5f);
//					Global.getSoundPlayer().playUISound("ui_cargo_metals_drop", 1f, 1f);
//				} else if (r > 0.25f) {
//					int qty = (int) (1f + 3f * (float) Math.random());
//					cargo.removeCommodity(Commodities.CREW, qty);
//					entity.addFloatingText("Lost " + qty + " crew", color, 0.5f);
//					Global.getSoundPlayer().playUISound("ui_cargo_crew_drop", 1f, 1f);
//				} else if (r >= 0) {
//					int qty = (int) (1f + 2f * (float) Math.random());
//					cargo.removeCommodity(Commodities.HEAVY_MACHINERY, qty);
//					entity.addFloatingText("Lost " + qty + " heavy machinery", color, 0.5f);
//					Global.getSoundPlayer().playUISound("ui_cargo_machinery_drop", 1f, 1f);
//				}
				
			}
		}
		
	}

	@Override protected void applyEffect(float amount, float level)
	{
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;
		
		
//		if (interval == null && level > 0) {
//			interval = new IntervalUtil(0.1f, 0.15f);
//		} else if (level <= 0) {
//			if (interval != null) {
//				entity.addFloatingText("Finished", Misc.setAlpha(entity.getIndicatorColor(), 255), 0.5f);
//			}
//			interval = null;
//		}
		
//		fleet.getStats().getSensorRangeMod().modifyFlat(getModId(), SENSOR_RANGE_BONUS * level, "Active sensor burst");
//		fleet.getStats().getDetectedRangeMod().modifyFlat(getModId(), DETECTABILITY_RANGE_BONUS * level, "Active sensor burst");
//		fleet.getStats().getFleetwideMaxBurnMod().modifyMult(getModId(), 0, "Active sensor burst");
//		fleet.getStats().getAccelerationMult().modifyMult(getModId(), 1f + (ACCELERATION_MULT - 1f) * level);
	}

	@Override protected void deactivateImpl()
	{
		cleanupImpl();
	}
	
	@Override protected void cleanupImpl()
	{
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;
	}
	
	@Override public boolean isUsable()
	{
		//So, where can someone NOT build? (Except "when", which is described by: "you need proper fleet").
		
		//Let's not waste SO many resources on every frame, shall we?...
		//return fleetConstructionEffectiveness(getFleet()) > 0;
		return true;
	}

	@Override public boolean hasTooltip(){return true;}
	@Override public void createTooltip(TooltipMakerAPI tooltip, boolean expanded)
	{
		tooltip.addTitle(spec.getName());
		
		float pad = 10f;
		tooltip.addPara("Build whatever is up your mind!", pad);
		
		if(expanded)
			tooltip.addPara("Each building requires some time and resources to build. "
				+"If waiting get's canceled after it's started, a proportion of "+
				"the full supplies needed will still be consumed.",
				0
			);
		
		if (!isUsable())
			tooltip.addPara("NOT usable. (Lol, thank you! :P)", Misc.getNegativeHighlightColor(), pad);
		
		addIncompatibleToTooltip(tooltip, expanded);
	}

	@Override public void pressButton()
	{
		BuildSomethingDialog bsd = new BuildSomethingDialog();
		Global.getSector().getCampaignUI().showInteractionDialog(
			bsd,
			Global.getSector().getPlayerFleet()
		);
		
		//Test expected behaviour... Does it wait to finish the dialog, or this function finished same time?
		Global.getSoundPlayer().playUISound("ui_objective_constructed", 1f, 1f);
		//Open up the selection menu.
		/* Through the choice in the menu it's defined:
			-Waiting time,
			-activation/not,
			-fleet effectivness,
			-can construct or not -for example: hostiles are tracking your movements.
		*/
		/*
		if(!getFleet().isPlayerFleet())
			super.pressButton();
		else
		{
			boolean allGood = true;
			if(allGood)
				super.pressButton();
		}
		*/
	}
	
	
	//==== PROTECTED ===========================================================
	protected final BuildSomethingDialog buildSomethingDialog = new BuildSomethingDialog();
	protected final Wait wait = new Wait();
	
	
	protected final IntervalUtil constructionInterv = new IntervalUtil(0, 0);
	
	protected float fleetConstructionEffectiveness(CampaignFleetAPI fleet)
	{
		//Check if empty/null
		if(fleet == null) return 0;
		List<FleetMemberAPI> ships = fleet.getFleetData().getMembersListCopy();
		if(ships == null || ships.isEmpty())
			return 0;
		
		//Loop through the ships
		int accumuator = 0;
		for(FleetMemberAPI ship: ships)
			accumuator += shipConstructionEffectiveness(ship);
		
		//Maybe being in an owned system playes a role?
		//Maybe being close to owned station playes a role?
		
		return accumuator;
	}
	
	protected float shipConstructionEffectiveness(FleetMemberAPI ship)
	{
		//HULL_MOD_BASE_VALUE not found in settings => construction disabled.
		if(ship == null || hullModBaseValue == null) return 0;
		
		ShipVariantAPI shipV = ship.getVariant();
		float shipEffectiveness = 0, fightersEffectiveness = 0, bonusEffectiveness = 0;
		
		//---- Ship ---------------------
		if(ship.getHullSpec().isBuiltInMod(HULL_MOD))
			shipEffectiveness += hullModBaseValue * hullModBuiltInMult;
		else if(shipV.hasHullMod(HULL_MOD))
			shipEffectiveness += hullModBaseValue;
		shipEffectiveness *= hullModShipClassMult.get(ship.getHullSpec().getHullSize());
		
		//Maybe combat rediness playes a role? Maybe not? Let's find out! ;P
		shipEffectiveness *= ship.getRepairTracker().getCR();
		
		//---- Fighters -----------------
		/* */
		for(int i=0; i<ship.getNumFightersInWing(); i++)
			if(shipV.getWing(i).getVariant().hasHullMod(HULL_MOD))
				fightersEffectiveness += hullModBaseValue * hullModFightersMult;
		/* * / //Or maybe: (to get the average of fighters effectiveness)
		float fightersEffectiveness = 0;
		for(int i=0; i<ship.getNumFightersInWing(); i++)
			if(shipV.getWing(i).getVariant().hasHullMod(HULL_MOD))
				fightersEffectiveness += hullModBaseValue * hullModFightersMult;
		fightersEffectiveness /= ship.getNumFightersInWing();
		effectiveness += fightersEffectiveness ;
		/* */
		
		//---- Bonus --------------------
		if(fightersEffectiveness>0 && shipEffectiveness>0)
			bonusEffectiveness += hullModOnFightersAndCarrierBonusMult * (shipEffectiveness + fightersEffectiveness);
		
		return shipEffectiveness + fightersEffectiveness + bonusEffectiveness;
	}
	protected boolean isConstructionCapable(FleetMemberAPI ship)
	{
		return shipConstructionEffectiveness(ship)>0;
	}
}
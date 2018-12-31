package build_anywhere.screens;

import build_anywhere.BAUtils;
import build_anywhere.Build;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.InteractionDialogImageVisual;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.input.Keyboard;
import static build_anywhere.BAUtils.settingsGetFloatOr;
import build_anywhere.Wait;
import build_anywhere.WaitAction;
import com.fs.starfarer.api.impl.campaign.ids.Tags;

public class BuildSomethingDialog implements InteractionDialogPlugin
{
	// NOTE: we use FleetInteractionDialogPluginImpl.inConversation to tell whether we're currently delegating stuff to the RuleBasedInteractionDialogPlugin
	
//==== STATIC ==============================================================
	public static final String
		//I would rather suffix, but would be either uglier or trickier to add the "ba_" preffix
		CONSTRUCTION_LEASH_WAIT_PREFIX = "ba_days_to_wait_in_leash_",
		STATION = Tags.STATION,
		COMM_RELAY_MAKESHIFT = Tags.COMM_RELAY+"_"+Tags.MAKESHIFT, //"comm_relay_makeshift",
		SENSOR_ARRAY_MAKESHIFT = Tags.SENSOR_ARRAY+"_"+Tags.MAKESHIFT,//"sensor_array_makeshift",
		NAV_BUOY_MAKESHIFT = Tags.NAV_BUOY+"_"+Tags.MAKESHIFT;//"nav_buoy_makeshift";
	
	//==== PUBLIC ==============================================================
	public InteractionDialogAPI getDialog(){return dialog;}

	@Override public void init(InteractionDialogAPI dialog)
	{
		FleetInteractionDialogPluginImpl.inConversation = false;
		this.dialog = dialog;
		this.options = dialog.getOptionPanel();
		this.text = dialog.getTextPanel();

		//dialog.setTextWidth(Display.getWidth() * .9f);

		dialog.getVisualPanel().showImageVisual(new InteractionDialogImageVisual("graphics/illustrations/fly_away.jpg", 640, 400));;
		initMenu();
	}

	void initMenu()
	{
		options.clearOptions();
		
		dialog.setPromptText("You decide to:");
		//options.addOption("Build a mining station", MenuOption.BUILD_MINING_STATION);
		//options.addOption("Build a research station", MenuOption.BUILD_RESEARCH_STATION);
		//options.addOption("Build a station from industry", MenuOption.BUILD_STATION_FROM_INDUSTRY);
		//options.addOption("Build a makeshift station", MenuOption.BUILD_MAAKESHIFT_STATION);
		options.addOption("Build a makeshift communication relay", MenuOption.BUILD_RELAY);
		options.addOption("Build a makeshift sensor array", MenuOption.BUILD_SENSOR);
		options.addOption("Build a makeshift nav buoy", MenuOption.BUILD_NAV_BOY);
		options.addOption("Close", MenuOption.EXIT);
		
		options.setShortcut(MenuOption.EXIT, Keyboard.KEY_ESCAPE, false, false, false, true);
	}
	
	@Override public void optionSelected(String optionText, Object optionData)
	{
		if (optionData == null) return;
		text.addParagraph(optionText, Global.getSettings().getColor("buttonText"));
		
		SectorEntityToken builder = getDialog().getInteractionTarget();
		String optionDataObjectType = relationships.get((MenuOption)optionData),
			toConfirmObjectType = toConfirm!=null? relationships.get((MenuOption)toConfirm) : null;
		
		switch((MenuOption)optionData)
		{
			case BUILD_RELAY:
			case BUILD_SENSOR:
			case BUILD_NAV_BOY:
				Build.printCost(text, builder, optionDataObjectType);
				
				options.clearOptions();
				dialog.setPromptText("And eventually you...");
				options.addOption("Confirm", MenuOption.CONFIRM_OPTION);
				options.addOption("Never mind", MenuOption.CANCEL_OPTION);
				
				toConfirm = (MenuOption)optionData;
				daysToWait = settingsGetFloatOr(CONSTRUCTION_LEASH_WAIT_PREFIX + optionDataObjectType, null);
			break;
			case BUILD_MAAKESHIFT_STATION:
				Build.buildMakeshiftStation(builder);
			break;
			case BUILD_RESEARCH_STATION:
				Build.buildResearchStation(builder);
			break;
			case BUILD_MINING_STATION:
				Build.buildMiningStation(builder);
			break;
			case BUILD_STATION_FROM_INDUSTRY:
				Build.buildStationFromIndustry(builder);
			break;
			case CONFIRM_OPTION:
				if(Build.canBuild(builder, toConfirmObjectType))
					waitPeriod.wait(getDialog(), daysToWait, "Constructing...", new AfterWaitBuild(builder, toConfirmObjectType));
					
			break;
			case CANCEL_OPTION:
				toConfirm = null;
				daysToWait = null;
				initMenu();
			break;
			case EXIT:
				dialog.dismiss();
			break;
		}
	}
	
	@Override public void optionMousedOver(String optionText, Object optionData){ }
	@Override public void advance(float amount){ }
	@Override public void backFromEngagement(EngagementResultAPI battleResult){ }
	@Override public Object getContext(){return null;}
	@Override public Map<String, MemoryAPI> getMemoryMap(){return null;}
	
	//==== PROTECTED ===========================================================
	protected MenuOption toConfirm = null;
	protected Float daysToWait = null;
	
	protected Wait waitPeriod = new Wait();
	
	protected InteractionDialogAPI dialog;
	protected TextPanelAPI text;
	protected OptionPanelAPI options;
	
	protected HashMap<String, MemoryAPI> memoryMap = new HashMap<>();
	
	protected HashMap<MenuOption, String> relationships = new HashMap(){{
		put(MenuOption.BUILD_RELAY, COMM_RELAY_MAKESHIFT);
		put(MenuOption.BUILD_SENSOR, SENSOR_ARRAY_MAKESHIFT);
		put(MenuOption.BUILD_NAV_BOY, NAV_BUOY_MAKESHIFT);
	}};
	
	protected enum MenuOption
	{
		BUILD_MAAKESHIFT_STATION,
		BUILD_MINING_STATION,
		BUILD_RESEARCH_STATION,
		BUILD_STATION_FROM_INDUSTRY,
		BUILD_RELAY,
		BUILD_SENSOR,
		BUILD_NAV_BOY,
		CONFIRM_OPTION,
		CANCEL_OPTION,
		EXIT
	}
	
	//==== CLASSES =============================================================
	protected static class AfterWaitBuild extends WaitAction
	{
		public AfterWaitBuild(SectorEntityToken builder, String objectType)
		{
			super(builder, objectType);
		}
		public void run(Float percentage)
		{
			if(percentage == 1f)
				Build.build(builder, objectType);
			Build.removeBuildCosts(builder, objectType, percentage);
		}
	}
}

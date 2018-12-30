package build_anywhere.plugins;

import build_anywhere.Build;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.listeners.ObjectiveEventListener;

public class BuildAnywhereModPlugin extends BaseModPlugin
{
    // call order: onNewGame -> onNewGameAfterProcGen -> onNewGameAfterEconomyLoad -> onEnabled -> onNewGameAfterTimePass -> onGameLoad
    
	//==== PUBLIC ==============================================================
	@Override public void onNewGame()
	{
        
    }
	
	@Override public void onNewGameAfterProcGen()
	{
		
    }
	
	@Override public void onNewGameAfterEconomyLoad()
	{
        
    }
	
	@Override public void onEnabled(boolean wasEnabledBefore)
	{
        
    }
    
	@Override public void onNewGameAfterTimePass()
	{
        
    }
	
	@Override public void onGameLoad(boolean newGame)
	{
		ensureHasConstructAbility();
		
		Global.getSector().getListenerManager().addListener(new ObjectiveEventListener(){
			@Override public void reportObjectiveChangedHands(SectorEntityToken objective, FactionAPI from, FactionAPI to){ }

			@Override public void reportObjectiveDestroyed(SectorEntityToken objective, SectorEntityToken stableLocation, FactionAPI enemy)
			{
				if(objective.getMemory().contains(Build.BA_FLAG))
					stableLocation.getContainingLocation().removeEntity(stableLocation);
			}
		}, true);
    }
    
	//--------------------------------------------------------------------------
    @Override public void onApplicationLoad() throws Exception
    {
        
    }
	
	@Override public void beforeGameSave()
    {
        
    }
	
	@Override public void afterGameSave()
	{
        
    }
	
	//==== PROTECTED ===========================================================
	protected void ensureHasConstructAbility()
	{
		CharacterDataAPI cda = Global.getSector().getCharacterData();
		if(!cda.getAbilities().contains("ba_construct"))
			cda.addAbility("ba_construct");
	}
}

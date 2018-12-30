package build_anywhere;


import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.BaseCampaignEventListenerAndScript;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CampaignProgressIndicatorAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.LeashScript;


public class Wait{
	
	//==== PUBLIC ==============================================================
	public EveryFrameScript getWaitScript() {return waitScript;}
	public LeashScript getLeashScript() {return leash;}
	public CampaignProgressIndicatorAPI getIndicator() {return indicator;}
	//public VarAndMemory getHandle() {return handle;}
	public boolean getFinished() {return finished;}
	public boolean getInProgress() {return inProgress;}
	public boolean getInterrupted() {return interrupted;}

	public boolean wait(InteractionDialogAPI dialog, float duration, String text, WaitAction stopped)
	{
		final float durationDays = duration;
		finished = false;
		interrupted = false;
		inProgress = true;
		this.stopped = stopped;
		if(text == null) text = "Waiting";
		
		Global.getSoundPlayer().playUISound("ui_wait_start", 1, 1);
		
		final SectorEntityToken target = dialog.getInteractionTarget();
		SectorEntityToken leashLoc = target.getContainingLocation().createToken(target.getLocation());
		
		//final CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
		//playerFleet.setInteractionTarget(null);
		//Vector2f offset = Vector2f.sub(playerFleet.getLocation(), target.getLocation(), new Vector2f());
//		float dir = Misc.getAngleInDegrees(offset);
//		offset = Misc.getUnitVectorAtDegreeAngle(dir)
		//float len = offset.length();
		//float radSum = playerFleet.getRadius() + target.getRadius() - 1f;
		//if (len > 0) {
			//offset.scale(radSum / len);
		//} else {
			//offset.set(radSum, 0);
		//}
		
		Vector2f offset = new Vector2f(0,0);
		
		indicator = Global.getFactory().createProgressIndicator(text, target, durationDays);
		target.getContainingLocation().addEntity(indicator);
		
		waitScript = new BaseCampaignEventListenerAndScript(durationDays + 0.1f) {
			private float elapsedDays = 0f;
			private boolean done = false;
			private boolean battleOccured = false;
			private boolean interactedWithSomethingElse = false;
			public boolean runWhilePaused() {return false;}
			public boolean isDone() {return done;}
			public void advance(float amount) {
				CampaignClockAPI clock = Global.getSector().getClock();
				Global.getSector().getCampaignUI().setDisallowPlayerInteractionsForOneFrame();
				
				float days = clock.convertToDays(amount);
				elapsedDays += days;
				inProgress = true;
				
//				float sinceLastBattle = clock.getElapsedDaysSince(Global.getSector().getLastPlayerBattleTimestamp());
//				if (sinceLastBattle <= elapsedDays) {
				if (battleOccured || interactedWithSomethingElse)
				{
					done = true;
					interrupted = true;
					
					Wait.this.stopped.run(indicator.getProgress()/indicator.getDurationDays());
							
					indicator.interrupt();
					Global.getSector().removeScript(leash);
					
					Global.getSoundPlayer().playUISound("ui_wait_interrupt", 1, 1);
				}
				else if (elapsedDays >= durationDays && !Global.getSector().getCampaignUI().isShowingDialog())
				{
					done = true;
					finished = true;
					inProgress = false;
					interrupted = false;
					
					Global.getSector().removeScript(leash);
					indicator.getContainingLocation().removeEntity(indicator);
					
					Wait.this.stopped.run(1f);
					//Global.getSector().getCampaignUI().showInteractionDialog(target);
					Global.getSoundPlayer().playUISound("ui_wait_finish", 1, 1);
				}
			}
			@Override
			public void reportBattleOccurred(CampaignFleetAPI primaryWinner, BattleAPI battle)
			{
				if (target instanceof CampaignFleetAPI && 
					(
						battle.getSnapshotSideFor((CampaignFleetAPI)target) != null || 
						target instanceof CampaignFleetAPI && battle.getSnapshotSideFor((CampaignFleetAPI)target) != null
					)
				)
					battleOccured = true;
			}
			@Override
			public void reportShownInteractionDialog(InteractionDialogAPI dialog) {
				interactedWithSomethingElse |= dialog.getInteractionTarget() != target;
			}
			@Override
			public void reportFleetDespawned(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
				if (fleet == target)
					battleOccured = true;
			}
			
		};
		
		if(CampaignFleetAPI.class.isInstance(target))
			leash = new LeashScript((CampaignFleetAPI)target, 50, leashLoc, offset, new Script() {
				public void run() {
					interrupted = true;
					Wait.this.stopped.run(indicator.getProgress()/indicator.getDurationDays());
					indicator.interrupt();
					Global.getSector().removeScript(waitScript);
					Global.getSoundPlayer().playUISound("ui_wait_interrupt", 1, 1);
				}
			});
		Global.getSector().addScript(leash);	
		Global.getSector().addScript(waitScript);
		
		Global.getSector().setPaused(false);
		dialog.dismiss();
		
		enabled = true;
		return true;
	}
	
	public boolean abort()
	{
		if(!enabled) return false;
		
		Global.getSector().removeScript(waitScript);
		Global.getSector().removeScript(leash);
		Wait.this.stopped.run(indicator.getProgress()/indicator.getDurationDays());
		indicator.getContainingLocation().removeEntity(indicator);

		finished = false;
		interrupted = false;
		inProgress = false;
		enabled = false;
		
		return true;
	}
	
	//==== PROTECTED ===========================================================
	protected EveryFrameScript waitScript;
	protected LeashScript leash;
	protected CampaignProgressIndicatorAPI indicator;
	protected WaitAction stopped;
	
	protected boolean
		enabled,
		finished,
		interrupted,
		inProgress;
}





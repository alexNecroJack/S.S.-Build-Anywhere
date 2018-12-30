package build_anywhere;

import com.fs.starfarer.api.campaign.SectorEntityToken;

public abstract class WaitAction
{
	//==== PUBLIC ==============================================================
	public WaitAction(SectorEntityToken builder, String objectType)
	{
		this.builder = builder;
		this.objectType = objectType;
		//percentageComplete = 0f;
	}
	
	abstract public void run(Float percentage);

	//==== PROTECTED ===========================================================
	protected SectorEntityToken builder;
	protected String objectType;
	//protected float percentageComplete;
}
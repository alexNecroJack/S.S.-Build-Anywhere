package build_anywhere;

import com.fs.starfarer.api.Global;

public class BAUtils
{
	/**
	 * 
	 * @param key
	 * @param defaultValue Can be null!
	 * @return 
	 */
	public static Float settingsGetFloatOr(String key, Float defaultValue)
	{
		try
		{
			Float toRet = Global.getSettings().getFloat(key);
			return toRet.isInfinite() || toRet.isNaN()? defaultValue : toRet;
		}
		catch(Exception exc){return defaultValue;}
	}
}

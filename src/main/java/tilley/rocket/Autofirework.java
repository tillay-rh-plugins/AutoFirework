package tilley.rocket;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

public class Autofirework extends Plugin {
	@Override
	public void onLoad() {
		RusherHackAPI.getModuleManager().registerFeature(new AutoFireworkModule());
		this.getLogger().info("loaded autofirework");
	}
	@Override
	public void onUnload() {
		this.getLogger().info("autofirework unloaded!");
	}
}

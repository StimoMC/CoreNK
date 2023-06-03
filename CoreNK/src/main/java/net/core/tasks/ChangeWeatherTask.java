package net.core.tasks;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.Task;

public class ChangeWeatherTask extends Task {
    @Override
    public void onRun(int i) {
        for (Level level : Server.getInstance().getLevels().values()) {
            if (level != null) {
                level.setRaining(false);
                level.setThundering(false);
            }
        }
    }
}

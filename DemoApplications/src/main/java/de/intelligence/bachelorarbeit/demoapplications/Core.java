package de.intelligence.bachelorarbeit.demoapplications;

import javafx.stage.StageStyle;

import de.intelligence.bachelorarbeit.simplifx.ClasspathScanPolicy;
import de.intelligence.bachelorarbeit.simplifx.SimpliFX;
import de.intelligence.bachelorarbeit.simplifx.annotation.ApplicationEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.annotation.StageConfig;

@StageConfig(title = "Test", style = StageStyle.DECORATED, alwaysTop = true, resizeable = false, iconPath = "/icon.png")
@ApplicationEntryPoint(Core.class)
public final class Core {

    public static void main(String[] args) {
        SimpliFX.setClasspathScanPolicy(ClasspathScanPolicy.LOCAL);
        SimpliFX.launch(Core.class);
    }

}

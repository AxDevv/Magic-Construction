package axdev.magicconstruction.basics;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigClient {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SHOW_LEDGE_PREVIEW;

    static {
        BUILDER.push("rendering");
        BUILDER.comment("Show preview for ledge placement when looking at air near blocks");
        SHOW_LEDGE_PREVIEW = BUILDER.define("ShowLedgePreview", true);
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}

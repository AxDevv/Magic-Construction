package axdev.magicconstruction.basics;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigClient {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue SHOW_LEDGE_PREVIEW;

    static {
        final var builder = new ForgeConfigSpec.Builder();

        builder.comment("Magic Construction - Client Configuration");

        builder.push("preview");
        SHOW_LEDGE_PREVIEW = builder.define("ShowLedgePreview", true);
        builder.pop();

        SPEC = builder.build();
    }
}

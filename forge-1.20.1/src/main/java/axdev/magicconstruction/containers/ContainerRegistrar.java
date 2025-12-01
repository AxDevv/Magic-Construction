package axdev.magicconstruction.containers;

import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.containers.handlers.HandlerBundle;
import axdev.magicconstruction.containers.handlers.HandlerShulkerbox;

public class ContainerRegistrar {
    public static void register() {
        MagicConstruction.instance.containerManager.register(new HandlerShulkerbox());
        MagicConstruction.instance.containerManager.register(new HandlerBundle());
    }
}

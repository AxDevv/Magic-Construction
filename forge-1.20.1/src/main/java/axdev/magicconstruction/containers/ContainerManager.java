package axdev.magicconstruction.containers;

import net.minecraft.world.item.ItemStack;
import axdev.magicconstruction.api.IContainerHandler;

import java.util.ArrayList;

public class ContainerManager {
    private final ArrayList<IContainerHandler> handlers;

    public ContainerManager() {
        handlers = new ArrayList<>();
    }

    public boolean register(IContainerHandler handler) {
        return handlers.add(handler);
    }

    public int countItems(ItemStack container, ItemStack item) {
        for (IContainerHandler handler : handlers) {
            int count = handler.countItems(container, item);
            if (count >= 0) return count;
        }
        return -1;
    }

    public int consumeItems(ItemStack container, ItemStack item, int count) {
        for (IContainerHandler handler : handlers) {
            int consumed = handler.consumeItems(container, item, count);
            if (consumed >= 0) return consumed;
        }
        return -1;
    }
}

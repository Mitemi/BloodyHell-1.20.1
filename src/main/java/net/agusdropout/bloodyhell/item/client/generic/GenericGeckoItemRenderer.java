package net.agusdropout.bloodyhell.item.client.generic;

import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoBlockItem;
import net.agusdropout.bloodyhell.item.custom.base.BaseGeckoItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericGeckoItemRenderer extends GeoItemRenderer<BaseGeckoItem> {
    public GenericGeckoItemRenderer() {
        super(new GenericGeckoItemModel());
    }

}

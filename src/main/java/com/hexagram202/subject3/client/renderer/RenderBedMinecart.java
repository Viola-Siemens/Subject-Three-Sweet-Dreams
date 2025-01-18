package com.hexagram202.subject3.client.renderer;

import com.hexagram202.subject3.client.models.ModelBedMinecart;
import com.hexagram202.subject3.common.entities.EntityBedMinecart;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;

public class RenderBedMinecart extends RenderMinecart<EntityBedMinecart> {
    public RenderBedMinecart(RenderManager p_i46155_1_) {
        super(p_i46155_1_);
        this.modelMinecart = new ModelBedMinecart();
    }
}

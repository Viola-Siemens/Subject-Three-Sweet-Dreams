package com.hexagram202.subject3.client.renderer;

import com.hexagram202.subject3.client.models.ModelBedBoat;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderManager;

public class RenderBedBoat extends RenderBoat {
    public RenderBedBoat(RenderManager p_i46190_1_) {
        super(p_i46190_1_);
        this.modelBoat = new ModelBedBoat();
    }
}

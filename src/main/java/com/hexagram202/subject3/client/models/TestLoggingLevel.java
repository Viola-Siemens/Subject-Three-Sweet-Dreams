package com.hexagram202.subject3.client.models;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;

public class TestLoggingLevel {
    public final String name;
    public TestLoggingLevel(String name) {
        this.name = name;
    }

    public float getValue(boolean log, int scale){
        if (log) {
            float val = MathHelper.cos(System.currentTimeMillis() / 31410f) * scale;
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(name + " : " + val));
            return val;
        } else return MathHelper.cos(System.currentTimeMillis() / 31410f) * scale;
    }
}

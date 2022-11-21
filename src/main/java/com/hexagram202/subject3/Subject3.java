package com.hexagram202.subject3;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "subject3")
public class Subject3 {
    public static final Logger LOGGER = LogManager.getLogger();

    @Mod.Instance
    public static Subject3 instance;
}

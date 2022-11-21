package com.hexagram202.subject3.common.utils;

import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nullable;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ChunkPoss {
    public static Stream<ChunkPos> rangeClosed(ChunkPos pCenter, int pRadius) {
        return rangeClosed(new ChunkPos(pCenter.x - pRadius, pCenter.z - pRadius), new ChunkPos(pCenter.x + pRadius, pCenter.z + pRadius));
    }

    public static Stream<ChunkPos> rangeClosed(final ChunkPos pStart, final ChunkPos pEnd) {
        int i = Math.abs(pStart.x - pEnd.x) + 1;
        int j = Math.abs(pStart.z - pEnd.z) + 1;
        final int k = pStart.x < pEnd.x ? 1 : -1;
        final int l = pStart.z < pEnd.z ? 1 : -1;
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<>((long) i * j, Spliterator.SIZED) {
            @Nullable
            private ChunkPos pos;

            public boolean tryAdvance(Consumer<? super ChunkPos> p_45630_) {
                if (this.pos == null) {
                    this.pos = pStart;
                } else {
                    int i1 = this.pos.x;
                    int j1 = this.pos.z;
                    if (i1 == pEnd.x) {
                        if (j1 == pEnd.z) {
                            return false;
                        }

                        this.pos = new ChunkPos(pStart.x, j1 + l);
                    } else {
                        this.pos = new ChunkPos(i1 + k, j1);
                    }
                }

                p_45630_.accept(this.pos);
                return true;
            }
        }, false);
    }


    public static ChunkPos chunkPosFromLog(long pPackedPos) {
        return new ChunkPos((int)pPackedPos, (int)(pPackedPos >> 32));
    }

    public static long chunkPosToLong(ChunkPos chunkPos) {
        return ChunkPos.asLong(chunkPos.x, chunkPos.z);
    }

    public static int getX(long pChunkAsLong) {
        return (int)(pChunkAsLong & 4294967295L);
    }

    public static int getZ(long pChunkAsLong) {
        return (int)(pChunkAsLong >>> 32 & 4294967295L);
    }
}

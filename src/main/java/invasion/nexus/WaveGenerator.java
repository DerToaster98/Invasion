package invasion.nexus;


import net.minecraft.entity.Entity;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public class WaveGenerator {

    @Nonnull
    public static Wave_ generate(int n, Random rand) {
        List<Pair<Integer, Consumer<Nexus>>> entries = new ArrayList<>();
        int duration = 0;
        switch (n) {
            case 0:
                break;
            case 1:

            default:
        }

        return new Wave_(duration, 0, entries);
    }


    private static class Entities {
        private final Function<Nexus, Entity>
                ZOMBIE_PIGMAN_T1 = null,
                ZOMBIE_PIGMAN_T2 = null,
                ZOMBIE_PIGMAN_T3 = null;
    }

    private static class Patterns {
        private final Function<Random, Function<Nexus, Entity>>
                ZOMBIE_T1 = null,
                ZOMBIE_T2 = null;

    }
}

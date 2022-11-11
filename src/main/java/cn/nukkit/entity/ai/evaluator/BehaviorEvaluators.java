package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.ai.memory.IMemory;
import cn.nukkit.entity.ai.memory.TimedMemory;
import org.jetbrains.annotations.NotNull;

/**
 * factory methods for all evaluators
 */
public class BehaviorEvaluators {

    public static AllMatchEvaluator all(@NotNull IBehaviorEvaluator... evaluators) {
        return new AllMatchEvaluator(evaluators);
    }

    public static AnyMatchEvaluator any(IBehaviorEvaluator... evaluators) {
        return new AnyMatchEvaluator(evaluators);
    }

    public static <T extends TimedMemory & IMemory<?>> PassByTimeEvaluator<T> passBy(Class<T> timedMemory, int minPassByTimeRange, int maxPassByTimeRange) {
        return passBy(timedMemory, minPassByTimeRange, maxPassByTimeRange, false);
    }

    public static <T extends TimedMemory & IMemory<?>> PassByTimeEvaluator<T> passBy(Class<T> timedMemory, int minPassByTimeRange, int maxPassByTimeRange, boolean allowEmpty) {
        return new PassByTimeEvaluator<>(timedMemory, minPassByTimeRange, maxPassByTimeRange, allowEmpty);
    }

    @NotNull
    public static ProbabilityEvaluator chance(int probability, int total) {
        return new ProbabilityEvaluator(probability, total);
    }
}

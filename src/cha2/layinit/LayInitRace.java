package cha2.layinit;

import anotation.NotThreadSafe;

public class LayInitRace {
    private static LayInitRace layInitRace = null;
    private LayInitRace() {}

    /**
     * 先检查后执行竞争
     * 当A线程进入后检查layInitRace对象为空，A基于该检查结果对layInitRace对象进行实例化。
     * 当B线程进入后，此时A线程还未完成实例化，则B线程检查到layInitRace为空，B基于该检查结果对layInitRace对象进行实例化。
     * 这种现象称为观察结果可能失效，但是我们基于观察结果执行了某些动作。
     *
     * 大多数竞态条件的实质：基于一种可能失效的观察结果来执行某些操作。
     */
    @NotThreadSafe
    public static LayInitRace getInstance() {
        if (layInitRace == null) {
            layInitRace = new LayInitRace();
        }
        return layInitRace;
    }
}

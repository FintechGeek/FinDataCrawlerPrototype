package org.charlestech.fin.prototype.seed;

/**
 * Created by Barry Zhu on 14-2-7.
 */
public class FinSeedNtesImpl extends FinSeedImpl {

    private static FinSeedNtesImpl finSeedNtes = null;

    private static synchronized void synInit() {
        if (null == finSeedNtes) {
            finSeedNtes = new FinSeedNtesImpl();
        }
    }

    public static FinSeedNtesImpl getFinSeedInstance() {
        if (null == finSeedNtes) {
            synInit();
        }
        return finSeedNtes;
    }

    private FinSeedNtesImpl() {
        super("fin_seed_ntes");
    }

    @Override
    public String getSource() {
        return "NTES";
    }
}

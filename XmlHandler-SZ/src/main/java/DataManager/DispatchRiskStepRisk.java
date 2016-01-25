package DataManager;

/**
 * Created by CaoYu on 2015/12/4.
 */
public class DispatchRiskStepRisk {
    private int myorder;
    private int stalosslevel;
    private int loadlosslevel;
    private int outputlosslevel;
    private int overloadlevel;
    private int lowvoltlevel;
    private int highvoltlevel;

    public void setHighvoltlevel(int highvoltlevel) {
        this.highvoltlevel = highvoltlevel;
    }

    public void setLoadlosslevel(int loadlosslevel) {
        this.loadlosslevel = loadlosslevel;
    }

    public void setLowvoltlevel(int lowvoltlevel) {
        this.lowvoltlevel = lowvoltlevel;
    }

    public void setMyorder(int myorder) {
        this.myorder = myorder;
    }

    public void setOutputlosslevel(int outputlosslevel) {
        this.outputlosslevel = outputlosslevel;
    }

    public void setOverloadlevel(int overloadlevel) {
        this.overloadlevel = overloadlevel;
    }

    public void setStalosslevel(int stalosslevel) {
        this.stalosslevel = stalosslevel;
    }

    public int getHighvoltlevel() {
        return highvoltlevel;
    }

    public int getLoadlosslevel() {
        return loadlosslevel;
    }

    public int getLowvoltlevel() {
        return lowvoltlevel;
    }

    public int getMyorder() {
        return myorder;
    }

    public int getOutputlosslevel() {
        return outputlosslevel;
    }

    public int getOverloadlevel() {
        return overloadlevel;
    }

    public int getStalosslevel() {
        return stalosslevel;
    }

}

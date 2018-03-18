package fortscale.web.beans;

/**
 * Created by shays on 17/08/2016.
 */
public class ValueCountBean {

    private String value;
    private int count;

    public ValueCountBean(String value, int count) {
        this.value = value;
        this.count = count;
    }

    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

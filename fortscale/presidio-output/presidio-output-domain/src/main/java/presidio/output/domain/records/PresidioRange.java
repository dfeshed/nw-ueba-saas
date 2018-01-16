package presidio.output.domain.records;

import org.springframework.util.Assert;

public class PresidioRange<T extends Comparable<T>> {
    private T lowerBound;
    private T upperBound;
    private boolean lowerInclusive;
    private boolean upperInclusive;

    public PresidioRange() {
    }

    public PresidioRange(T lowerBound, T upperBound) {
        this(lowerBound, upperBound, true, true);
    }

    public PresidioRange(T lowerBound, T upperBound, boolean lowerInclusive, boolean upperInclusive) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.lowerInclusive = lowerInclusive;
        this.upperInclusive = upperInclusive;
    }

    public boolean contains(T value) {
        Assert.notNull(value, "Reference value must not be null!");
        boolean greaterThanLowerBound = this.lowerBound == null ? true : (this.lowerInclusive ? this.lowerBound.compareTo(value) <= 0 : this.lowerBound.compareTo(value) < 0);
        boolean lessThanUpperBound = this.upperBound == null ? true : (this.upperInclusive ? this.upperBound.compareTo(value) >= 0 : this.upperBound.compareTo(value) > 0);
        return greaterThanLowerBound && lessThanUpperBound;
    }

    public T getLowerBound() {
        return this.lowerBound;
    }

    public T getUpperBound() {
        return this.upperBound;
    }


    public boolean isLowerInclusive() {
        return this.lowerInclusive;
    }

    public boolean isUpperInclusive() {
        return this.upperInclusive;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PresidioRange)) {
            return false;
        } else {
            PresidioRange other;
            label40:
            {
                other = (PresidioRange) o;
                Object this$lowerBound = this.getLowerBound();
                Object other$lowerBound = other.getLowerBound();
                if (this$lowerBound == null) {
                    if (other$lowerBound == null) {
                        break label40;
                    }
                } else if (this$lowerBound.equals(other$lowerBound)) {
                    break label40;
                }

                return false;
            }

            label33:
            {
                Object this$upperBound = this.getUpperBound();
                Object other$upperBound = other.getUpperBound();
                if (this$upperBound == null) {
                    if (other$upperBound == null) {
                        break label33;
                    }
                } else if (this$upperBound.equals(other$upperBound)) {
                    break label33;
                }

                return false;
            }

            if (this.isLowerInclusive() != other.isLowerInclusive()) {
                return false;
            } else {
                return this.isUpperInclusive() == other.isUpperInclusive();
            }
        }
    }

    public int hashCode() {
        int result = 1;
        Object $lowerBound = this.getLowerBound();
        result = result * 59 + ($lowerBound == null ? 43 : $lowerBound.hashCode());
        Object $upperBound = this.getUpperBound();
        result = result * 59 + ($upperBound == null ? 43 : $upperBound.hashCode());
        result = result * 59 + (this.isLowerInclusive() ? 79 : 97);
        result = result * 59 + (this.isUpperInclusive() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "Range(lowerBound=" + this.getLowerBound() + ", upperBound=" + this.getUpperBound() + ", lowerInclusive=" + this.isLowerInclusive() + ", upperInclusive=" + this.isUpperInclusive() + ")";
    }
}

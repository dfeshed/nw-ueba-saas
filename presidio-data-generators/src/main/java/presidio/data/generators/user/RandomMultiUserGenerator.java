package presidio.data.generators.user;

import org.springframework.util.Assert;
import presidio.data.domain.User;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RandomMultiUserGenerator implements IUserGenerator{
    private List<UserGeneratorProbability> userGeneratorProbabilityList;
    private Iterator<UserGeneratorProbability> userGeneratorProbabilityIterator;
    private Random random;

    public RandomMultiUserGenerator(List<UserGeneratorProbability> userGeneratorProbabilityList){
        double minProb = userGeneratorProbabilityList.stream().map(u -> u.probablility).min((p1, p2) -> Double.compare(p1,p2)).get();
        Assert.isTrue(minProb<=1 && minProb>=0, "The minimum probability should be in the range [0,1]");
        double maxProb = userGeneratorProbabilityList.stream().map(u -> u.probablility).max((p1, p2) -> Double.compare(p1,p2)).get();
        Assert.isTrue(maxProb<=1 && maxProb>0, "The maximum probability should be in the range (0,1]");
        double multiptyMaxProbToOne = 1/maxProb;
        userGeneratorProbabilityList.forEach(userGeneratorProbability -> userGeneratorProbability.setProbablility(userGeneratorProbability.getProbablility()*multiptyMaxProbToOne));
        this.userGeneratorProbabilityList = userGeneratorProbabilityList;
        resetEventGeneratorProbabilityIterator();
        random = new Random(0);
    }

    private void resetEventGeneratorProbabilityIterator(){
        userGeneratorProbabilityIterator = userGeneratorProbabilityList.iterator();
    }

    @Override
    public User getNext() {
        User ret = null;
        while (ret == null){
            UserGeneratorProbability userGeneratorProbability = userGeneratorProbabilityIterator.next();
            if(!userGeneratorProbabilityIterator.hasNext()){
                resetEventGeneratorProbabilityIterator();
            }
            if (random.nextDouble() <= userGeneratorProbability.getProbablility()) {
                ret = userGeneratorProbability.getUserGenerator().getNext();
            }
        }
        return ret;
    }


    public static class UserGeneratorProbability{
        private IUserGenerator userGenerator;
        private double probablility;

        public UserGeneratorProbability(IUserGenerator userGenerator, double probablility){
            this.userGenerator = userGenerator;
            this.probablility = probablility;
        }

        public IUserGenerator getUserGenerator() {
            return userGenerator;
        }

        public double getProbablility() {
            return probablility;
        }

        public void setProbablility(double probablility) {
            this.probablility = probablility;
        }
    }
}

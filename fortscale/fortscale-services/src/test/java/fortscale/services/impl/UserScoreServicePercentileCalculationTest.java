package fortscale.services.impl;


import fortscale.domain.core.Alert;
import fortscale.domain.core.AlertFeedback;
import fortscale.domain.core.Severity;
import fortscale.domain.core.UserSingleScorePercentile;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.AlertsService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by shays on 26/05/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserScoreServicePercentileCalculationTest {


    @InjectMocks
    public UserScoreServiceImpl userScoreService;


    @Test
    public void simplePercentileCalculationTest(){
        List<Pair<Double, Integer>> scoresToUsersCount= new ArrayList<>();
        scoresToUsersCount.add(new ImmutablePair<>(5.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(10.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(15.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(20.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(25.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(30.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(35.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(40.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(45.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(50.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(55.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(60.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(70.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(65.0, 100));


        scoresToUsersCount.add(new ImmutablePair<>(80.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(75.0, 100));


        scoresToUsersCount.add(new ImmutablePair<>(85.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(90.0, 100));


        scoresToUsersCount.add(new ImmutablePair<>(95.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(100.0, 100));

        //Expect that each decile will contain 200 users 0-10, 11-20, 21-30... 91..100
        List<UserSingleScorePercentile> m = userScoreService.getOrderdPercentiles(scoresToUsersCount, 10);
        Assert.assertEquals(10, m.size());

        for (int i=1; i<=10; i++){
            UserSingleScorePercentile u = m.get(i-1);
            Assert.assertEquals(i, u.getPercentile());
            Assert.assertEquals((i-1)*10, u.getMinScoreInPerecentile());
            Assert.assertEquals(i*10, u.getMaxScoreInPercentile());

        }



    }

    @Test
    public void advancedPercentileCalculationTest(){
        List<Pair<Double, Integer>> scoresToUsersCount= new ArrayList<>();
        //300 users between 0-50
        scoresToUsersCount.add(new ImmutablePair<>(0.0, 20));
        scoresToUsersCount.add(new ImmutablePair<>(10.0, 40));
        scoresToUsersCount.add(new ImmutablePair<>(32.0, 50));
        scoresToUsersCount.add(new ImmutablePair<>(38.0, 40));
        scoresToUsersCount.add(new ImmutablePair<>(40.0, 60));
        scoresToUsersCount.add(new ImmutablePair<>(50.0, 90));

        //300 users between 51-70
        scoresToUsersCount.add(new ImmutablePair<>(52.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(68.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(70.0, 100));

        //300 users between 71-80
        scoresToUsersCount.add(new ImmutablePair<>(72.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(80.0, 200));

        //300 users between 81-85
        scoresToUsersCount.add(new ImmutablePair<>(85-.0, 300));


        //300 users between 86-300
        scoresToUsersCount.add(new ImmutablePair<>(86.0, 20));
        scoresToUsersCount.add(new ImmutablePair<>(120.0, 180));
        scoresToUsersCount.add(new ImmutablePair<>(300.0, 100));



        //Expect that each HAMISHON will contain 300 users -- > 0-50, 51-70, 71-80, 81-85, 86-300
        List<UserSingleScorePercentile> m = userScoreService.getOrderdPercentiles(scoresToUsersCount, 5);
        Assert.assertEquals(5, m.size());

        //Hamishon 1

        Assert.assertEquals(1, m.get(0).getPercentile());
        Assert.assertEquals(0, m.get(0).getMinScoreInPerecentile());
        Assert.assertEquals(50, m.get(0).getMaxScoreInPercentile());

        //Hamishon 2
        Assert.assertEquals(2, m.get(1).getPercentile());
        Assert.assertEquals(50, m.get(1).getMinScoreInPerecentile());
        Assert.assertEquals(70, m.get(1).getMaxScoreInPercentile());

        //Hamishon 3
        Assert.assertEquals(3, m.get(2).getPercentile());
        Assert.assertEquals(70, m.get(2).getMinScoreInPerecentile());
        Assert.assertEquals(80, m.get(2).getMaxScoreInPercentile());

        //Hamishon 4
        Assert.assertEquals(4, m.get(3).getPercentile());
        Assert.assertEquals(80, m.get(3).getMinScoreInPerecentile());
        Assert.assertEquals(85, m.get(3).getMaxScoreInPercentile());

        //Hamishon 5
        Assert.assertEquals(5, m.get(4).getPercentile());
        Assert.assertEquals(85, m.get(4).getMinScoreInPerecentile());
        Assert.assertEquals(300, m.get(4).getMaxScoreInPercentile());


    }


}

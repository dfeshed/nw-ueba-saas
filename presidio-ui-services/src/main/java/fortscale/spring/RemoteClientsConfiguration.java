//package fortscale.spring;
//
//import fortscale.remote.RemoteAlertClientService;
//
//import fortscale.remote.RemoteUserClientService;
//import fortscale.remote.fake.FakeRemoteAlertClientService;
//import fortscale.remote.fake.FakeRemoteUserClientService;
//import fortscale.remote.fake.creators.FakeAlertsCreator;
//import fortscale.remote.fake.creators.FakeCreatorUtils;
//import fortscale.remote.fake.creators.FakeIndicatorCreators;
//import fortscale.remote.fake.creators.FakeUsersCreator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.context.annotation.Profile;
//
//@Configuration()
//@Import({RemoteClientsConfiguration.Production.class,RemoteClientsConfiguration.Fake.class})
///**
// * Presidio UI required the presidio-output to load data.
// * For integration tests and manual tests we need to use fake data.
// * This class load the fake or real data based on spring profile
// */
//public class RemoteClientsConfiguration {
//
//    @Profile("!fake")
//    public static class Production {
//        @Bean()
//        RemoteAlertClientService remoteAlertsClient() {
//
//            return new RemoteAlertClientService();
//        }
//
//        @Bean()
//        RemoteUserClientService remoteUsersClient() {
//
//            return new RemoteUserClientService();
//        }
//    }
//
//    @Profile("fake")
//    public static class Fake {
//
//        @Bean()
//        FakeAlertsCreator fakeAlertsCreator() {
//
//            return new FakeAlertsCreator(fakeCreatorUtils(),fakeIndicatorCreators());
//        }
//
//        @Bean()
//        FakeUsersCreator fakeUsersCreator() {
//
//            return new FakeUsersCreator(fakeCreatorUtils(),fakeAlertsCreator());
//        }
//
//        @Bean()
//        FakeIndicatorCreators fakeIndicatorCreators() {
//
//            return new FakeIndicatorCreators(fakeCreatorUtils());
//        }
//
//        @Bean()
//        FakeCreatorUtils fakeCreatorUtils() {
//
//            return new FakeCreatorUtils();
//        }
//
//
//        @Bean()
//        RemoteAlertClientService remoteAlertsClient() {
//
//            return new FakeRemoteAlertClientService(fakeAlertsCreator(),fakeIndicatorCreators());
//        }
//
//        @Bean()
//        RemoteUserClientService remoteUsersClient() {
//
//            return new FakeRemoteUserClientService(fakeAlertsCreator(),fakeUsersCreator());
//        }
//    }
//}

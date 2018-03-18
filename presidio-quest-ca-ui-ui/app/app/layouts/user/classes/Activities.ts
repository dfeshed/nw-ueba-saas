module Fortscale.layouts.user {

    import IActivityOrganizationCountry = Fortscale.shared.services.entityActivityUtils.IActivityOrganizationCountry;
    import IActivityUserCountry = Fortscale.shared.services.entityActivityUtils.IActivityUserCountry;
    import IActivityUserAuthentication = Fortscale.shared.services.entityActivityUtils.IActivityUserAuthentication;
    import IActivityUserWorkingHour = Fortscale.shared.services.entityActivityUtils.IActivityUserWorkingHour;
    import IActivityDevice = Fortscale.shared.services.entityActivityUtils.IActivityDevice;
    import IActivityUserDataUsage = Fortscale.shared.services.entityActivityUtils.IActivityUserDataUsage;
    import IActivityTopApplication = Fortscale.shared.services.entityActivityUtils.IActivityTopApplication;
    import IActivityTopDirectory = Fortscale.shared.services.entityActivityUtils.IActivityTopDirectory;
    import IActivityTopRecipientDomain = Fortscale.shared.services.entityActivityUtils.IActivityTopRecipientDomain;
    import IActivityUserClassificationExposure = Fortscale.shared.services.entityActivityUtils.IActivityUserClassificationExposure;

    export interface IActivities {
        organization: {
            topCountries: IActivityOrganizationCountry[]
        }
        user :{
            topCountries: IActivityUserCountry[],
            authentications: IActivityUserAuthentication,
            workingHours: IActivityUserWorkingHour[],
            sourceDevices: IActivityDevice[],
            targetDevices: IActivityDevice[],
            dataUsages: IActivityUserDataUsage[],
            topApplications: IActivityTopApplication[],
            topDirectories: IActivityTopDirectory[],
            topRecipientsDomains: IActivityTopRecipientDomain[],
            classificationExposure: IActivityUserClassificationExposure

        }
    }
    export class Activities implements IActivities{
        organization: {
            topCountries: IActivityOrganizationCountry[]
        };
        user :{
            topCountries: IActivityUserCountry[],
            authentications: IActivityUserAuthentication,
            workingHours: IActivityUserWorkingHour[],
            sourceDevices: IActivityDevice[],
            targetDevices: IActivityDevice[],
            dataUsages: IActivityUserDataUsage[],
            topApplications: IActivityTopApplication[],
            topDirectories: IActivityTopDirectory[],
            topRecipientsDomains: IActivityTopRecipientDomain[],
            classificationExposure: IActivityUserClassificationExposure
        };

        constructor () {

            this.organization = {
                topCountries: null
            };

            this.user = {
                topCountries: null,
                authentications: null,
                workingHours: null,
                sourceDevices: null,
                targetDevices: null,
                dataUsages: null,
                topApplications:null,
                topDirectories: null,
                topRecipientsDomains:null,
                classificationExposure:null
            };
        }
    }
}

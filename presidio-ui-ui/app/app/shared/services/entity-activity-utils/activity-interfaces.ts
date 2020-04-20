module Fortscale.shared.services.entityActivityUtils {

    import IDataBean = Fortscale.shared.interfaces.IDataBean;

    export interface IActivityCountry {
    }
    export interface IActivityAuthentication {
    }

    export interface IActivityWorkingHour {
    }

    export interface IActivityDevice {
    }

    export interface IActivityDataUsage {
    }

    export interface IActivityClassificationExposure{

    }

    export interface IActivityUserCountry extends IActivityCountry {
        country:string,
        count:number
    }

    export interface IActivityOrganizationCountry extends IActivityCountry {
        country:string
    }

    export interface IActivityUserAuthentication extends IActivityAuthentication {
        success:number,
        failed:number
    }

    export interface IActivityUserClassificationExposure extends IActivityClassificationExposure {
        classified:number,
        total:number
    }

    export interface IActivityUserWorkingHour extends IActivityWorkingHour {
        hour:number
    }

    export interface IActivityUserDevice extends IActivityDevice {
        deviceName: string,
        count: number,
        deviceType: string
    }

    export interface IActivityTopApplication extends IActivityDevice {
        name: string,
        count: number,
        type: string
    }

    export interface IActivityTopRecipientDomain extends IActivityDevice {
        name: string,
        count: number
    }

    export interface IActivityTopDirectory extends IActivityDevice {
        name: string,
        count: number

    }

    export interface IActivityUserDataUsage extends IActivityDataUsage {
        dataEntityId: string,
        value: number,
    }

    export interface IEntityActivity extends IDataBean {
    }

    export interface IEntityActivityLocationUser extends IEntityActivity {
        data:IActivityUserCountry[]
    }

    export interface IEntityActivityLocationOrganization extends IEntityActivity {
        data:IActivityOrganizationCountry[]
    }

    export interface IEntityActivityAuthenticationUser extends IEntityActivity {
        data:IActivityUserAuthentication[]
    }

    export interface IEntityActivityClassificationExposure extends IEntityActivity {
        data:IActivityUserClassificationExposure[]
    }

    export interface IEntityActivityWorkingHoursUser extends IEntityActivity {
        data:IActivityUserWorkingHour[]
    }

    export interface IEntityActivitySourceDevicesUser extends IEntityActivity {
        data:IActivityUserDevice[]
    }

    export interface IEntityActivityTargetDevicesUser extends IEntityActivity {
        data:IActivityUserDevice[]
    }

    export interface IEntityActivityDataUsagesUser extends IEntityActivity {
        data:IActivityUserDataUsage[]
    }
}

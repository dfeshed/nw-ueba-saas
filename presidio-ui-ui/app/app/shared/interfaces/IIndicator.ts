module Fortscale.shared.interfaces {

    export interface IIndicator {
        anomalyType:string;
        anomalyTypeFieldName:string;
        anomalyValue: any;
        dataEntitiesIds:string[];
        endDate:number;
        entityName:string;
        entityType:string;
        entityTypeFieldName:string;
        evidenceType:string;
        id:string;
        name:string;
        numOfEvents:number;
        retentionDate:number;
        score:number;
        severity:string;
        startDate:number;
        supportingInformation:string;
        timeframe:string;
    }

}

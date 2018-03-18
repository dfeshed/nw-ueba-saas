module Forstcale.shared.interfaces {
    
    import IIndicator = Fortscale.shared.interfaces.IIndicator;

    export interface IAlert {
        comments:any[];
        dataSourceAnomalyTypePair:{dataSource:string, anomalyType:string}[];
        endDate:number;
        entityId:string;
        entityName:string;
        entityType:string;
        evidenceSize:number;
        evidences:IIndicator[];
        feedback:string;
        id:string;
        name:string;
        score:number;
        severity:string;
        severityCode:number;
        startDate:number;
        status:string;
        timeframe:string;
        userScoreContribution:number;
        userScoreContributionFlag:boolean
    }
}

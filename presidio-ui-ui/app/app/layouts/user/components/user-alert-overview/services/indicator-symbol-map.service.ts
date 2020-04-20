module Fortscale.layouts.user {

    interface ISymnbolMapData {
        symbolName:string,
        anomalyType:string[]
    }

    let symbolsMap:ISymnbolMapData[] = [
        {
            symbolName: 'indicator-authentication-icon',
            anomalyType: [
                'auth_method',
                'Failure Code Anomaly',
                'High Number of Failed Authentications',
                'High Number of Successful Authentications',
            ]
        },
        {
            symbolName: 'indicator-data-usage-icon',
            anomalyType: [
                'Data Usage Anomaly',
                'Database Object Anomaly',
                'Database Server Anomaly',
                'Database Username Anomaly',
                'high Number of Accessed Database Objects',
                'High Number of Database Servers',
                'High Number of Database Users',
                'High Number of Printed Pages',
                'High Number of Successful Actions'
            ]
        },
        {
            symbolName: 'indicator-location-icon',
            anomalyType: [
                'Geolocation Anomaly',
                'Geolocation Sequence Anomaly',
                'High Number of Source Countries'
            ]
        },
        {
            symbolName: 'indicator-user-devices-icon',
            anomalyType: [
                'High Number of Source Devices',
                'Source Device Anomaly',
            ]
        },
        {
            symbolName: 'indicator-target-devices-icon',
            anomalyType: [
                'High Number of Target Devices',
                'Target Device Anomaly',
            ]
        },
        {
            symbolName: 'indicator-working-hours-icon',
            anomalyType: [
                'Activity Time Anomaly'
            ]
        },
        {
            symbolName: 'indicator-email-icon',
            anomalyType: [
                'Email Application Anomaly',
                'Email Sender Anomaly',
                'Activity Time Anomaly',
                'Email Recipient Domain Anomaly',
                'Email Source Device Anomaly',
                'Aggregated File Size Volume (Hourly, Daily)',
                'High Number of Emails Sent to External Single Recipients  (Hourly, Daily)',
                'Aggregated File Size Volume Sent to an External Single Recipient (Hourly, Daily)'

            ]
        },
        {
            symbolName: 'indicator-attachment-icon',
            anomalyType: [
                'Email Attachment File Size Anomaly',
                'Email Attachment Extension Anomaly',
                'High Number of Email Attachments',
                'High Number of Email Attachments to an External Single Recipient',
                'Aggregated File Size Volume'
            ]
        },{
            symbolName: 'indicator-file-icon',
            anomalyType: [
                'High Number of Source Network Folder Paths',
                'Aggregated File Size Volume Moved to Removable Device',
                'Aggregated File Size Volume Copied to Removable Device',
                'Aggregated File Size Volume Copied from Network Directory',
                'Aggregated File Size Volume Moved from Network Directory',
                'High Number of Files Moved to Removable Device',
                'High Number of Files Copied to Removable Device',
                'High Number of Files Moved from Network Directory',
                'High Number of Files Copied from Network Directory',
                'High Number of Files Recycled & Deleted',
                'High Number of Files Opened'
            ]
        }
        ,{
            symbolName: 'indicator-printer-icon',
            anomalyType: [
                'High Volume of Printed Data',
                'Printer Anomaly',
                'High Number of Printed Pages',


            ]
        }

    ];

    let DEFAULT_SYMBOL_NAME = 'indicator-general-icon';


    export interface IIndicatorSymbolMapService {
        getSymbolName (indicator:{anomalyType:string, (key:string):any}):string
    }

    class IndicatorSymbolMapService implements IIndicatorSymbolMapService {

        /**
         * Takes an indicator and tries to find on the SymbolMap if the anomalyType correlates to any of the definitions
         * If so the symbolName is returned from the map, otherwise the default symbol name is returned.
         * @param indicator
         * @returns {string}
         */
        getSymbolName (indicator:{anomalyType:string, (key:string):any}):string {

            let symbolName:string;

            _.some<ISymnbolMapData>(symbolsMap, (symbolType) => {
                if (symbolType.anomalyType.indexOf(indicator.anomalyType) !== -1) {
                    symbolName = symbolType.symbolName;
                    return true;
                }
            });


            return symbolName || DEFAULT_SYMBOL_NAME;
        }
    }

    angular.module('Fortscale.layouts.user')
        .service('indicatorSymbolMap', IndicatorSymbolMapService)
}

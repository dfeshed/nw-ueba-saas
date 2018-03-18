import IIndicator = Fortscale.shared.interfaces.IIndicator;
(function () {
    'use strict';

    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .factory('indicatorTypeMapper.lateralMovement', [
            '$filter',
            function ($filter:any) {

                return {
                    settings: {
                        params: {
                            context_type: '{{entityTypeFieldName}}',
                            context_value: '{{entityName}}',
                            feature: '{{anomalyTypeFieldName}}',
                            'function': 'vpnLateralMovement'
                        },
                        templates: {},
                        preProcessData: (response:{data:any[]}, indicator:IIndicator):{data:any[]} => {
                            let data:any[] = response.data;


                            // create user index list
                            let usersList:string[] = _.map(_.groupBy(data, 'value'), (userGroup:any, key:string) => {
                                return key;
                            });

                            // the session item would have two keys
                            let sessionItems:any[] = _.filter(data,
                                eventItem => eventItem.keys && eventItem.keys.length === 2);
                            if (sessionItems && sessionItems.length === 1) {
                                // pull out the the session item from the usersList and place it in the end of the list
                                let sessionItem = sessionItems[0];
                                let userIndex = usersList.indexOf(sessionItem.value);
                                usersList.splice(userIndex, 1);
                                usersList.push(sessionItem.value);


                                // Split session into two event items
                                _.remove(data, sessionItem);
                                let startDate = moment(parseInt(sessionItem.keys[0], 10)).utc()
                                    .format("MMM D, YYYY, h:mm:ss a");
                                let endDate = moment(parseInt(sessionItem.keys[1], 10)).utc()
                                    .format("MMM D, YYYY, h:mm:ss a");
                                let splitSessionItem1 = _.cloneDeep(sessionItem);
                                splitSessionItem1.sessionUserIndex = usersList.indexOf(sessionItem.value) + 1;
                                splitSessionItem1.keys.pop();
                                splitSessionItem1.additionalInformation.session_start_date = startDate;
                                splitSessionItem1.bullet = 'triangleRight';

                                data.push(splitSessionItem1);
                                let splitSessionItem2 = _.cloneDeep(sessionItem);
                                splitSessionItem2.keys.shift();
                                splitSessionItem2.additionalInformation.session_end_date = endDate;
                                splitSessionItem2.sessionUserIndex = usersList.indexOf(sessionItem.value) + 1;
                                splitSessionItem2.bullet = 'triangleLeft';

                                data.push(splitSessionItem2);
                            }

                            // Add user index to each of the items in the list
                            _.each(data, eventItem => {
                                if (!eventItem.sessionUserIndex) {
                                    eventItem.userIndex = usersList.indexOf(eventItem.value) + 1;
                                }
                            });
                            // Convert into list by date with the desired schema
                            response.data = _.map<any, any>(response.data, eventItem => {
                                let date = moment(parseInt(eventItem.keys[0], 10));
                                let dataItem:any = {
                                    date: new Date(parseInt(eventItem.keys[0], 10)),
                                    formatted_date: date.utc().format("MMM D, YYYY, h:mm:ss a"),
                                    username: eventItem.value,
                                    target_machine: eventItem.additionalInformation.target_machine,
                                    normalized_username: eventItem.additionalInformation.normalized_username,
                                    entity_id: eventItem.additionalInformation.entity_id,
                                    display_name: eventItem.additionalInformation.display_name ||
                                    eventItem.additionalInformation.normalized_username || eventItem.value,
                                    usersList: usersList,
                                    data_source_id: eventItem.additionalInformation.data_source,
                                    data_source: $filter('entityIdToName')(
                                        eventItem.additionalInformation.data_source),
                                    source_machine: eventItem.additionalInformation.source_machine,
                                    event_score: eventItem.additionalInformation.event_score,
                                    country_name: eventItem.additionalInformation.country_name,
                                    local_ip: eventItem.additionalInformation.local_ip,
                                    source_ip: eventItem.additionalInformation.source_ip,
                                    session_end_date: eventItem.additionalInformation.session_end_date,
                                    session_start_date: eventItem.additionalInformation.session_start_date,
                                    bullet: eventItem.bullet


                                };

                                if (eventItem.sessionUserIndex) {
                                    dataItem.sessionUserIndex = eventItem.sessionUserIndex;
                                }

                                if (eventItem.userIndex) {
                                    dataItem.userIndex = eventItem.userIndex;
                                }

                                return dataItem;
                            });

                            return response;
                        },
                        sortData: data => {
                            return _.orderBy(data, [(dataItem:any) => dataItem.date.valueOf()], ['asc']);
                        },
                        dataAdapter: (indicator, dataItem) => {
                            return dataItem;
                        },
                        chartSettings: {
                            "type": "serial",
                            "fontFamily": "'Open Sans', sans-serif",
                            "valueAxes": [{
                                "id": "v1",
                                "title": "Users",
                                "titleColor": "#989191",
                                "position": "left",
                                "precision": 0,
                                "tickLength": 1,
                                "labelFunction": function (value, stringValue, axisObject:any) {
                                    // return the username as label
                                    let username = axisObject.data[0].dataContext.usersList[parseInt(stringValue, 10) -
                                    1];

                                    let item:any = _.find(axisObject.data, (axisData:any) => {
                                        return axisData.dataContext.username === username;
                                    });

                                    return (item && item.dataContext && item.dataContext.display_name) || '';
                                },
                                "stackType": "regular",
                                "axisColor": "#BCB5B5",
                                "gridColor": "#989191",
                                "showFirstLabel": false
                            }],
                            "graphs": [{
                                "id": "g1",
                                "valueAxis": "v2",
                                "hideBulletsCount": 50,
                                "lineThickness": 40,
                                "lineColor": "#20acd4",
                                "type": "smoothedLine",
                                "valueField": "sessionUserIndex",
                                balloonFunction: (item:any) => {
                                    let value:string = moment(new Date(item.category)).utc()
                                        .format("MMM D, YYYY, h:mm:ss a");
                                    let context:any = item.dataContext;
                                    let balloonText = `(${context.data_source}) ${context.display_name} <br>logged on from <span style='color: red;'>${context.source_ip}</span><br>`;
                                    if (context.country_name) {
                                        balloonText += `originating from ${context.country_name}<br>`;
                                    }
                                    if (context.session_start_date) {
                                        balloonText += `Start Time: ${context.session_start_date}<br>`;
                                    }

                                    if (context.session_end_date) {
                                        balloonText += `End Time: ${context.session_end_date}<br>`;
                                    }
                                    balloonText += `Event Score: ${context.event_score}`;
                                    return balloonText;

                                },
                                "bulletField": "bullet",
                                "bulletColor": "#098cb4",
                                "bulletSize": 20,
                            },
                                {
                                    "id": "g2",
                                    "valueAxis": "v2",
                                    "bullet": "diamond",
                                    "bulletBorderAlpha": 1,
                                    "bulletColor": "#FF0000",
                                    "bulletSize": 20,
                                    "hideBulletsCount": 50,
                                    "lineThickness": 0,
                                    "lineColor": "#ff0000",
                                    "type": "smoothedLine",
                                    "useLineColorForBulletBorder": true,
                                    "valueField": "userIndex",
                                    "balloonText": "([[data_source]]) [[display_name]] <br>logged on from <span style='color: red;'>[[source_ip]]</span><br>using [[source_machine]]<br>to [[target_machine]]<br>Event Time: [[formatted_date]]<br>Event Score: [[event_score]]"
                                }],

                            "categoryField": "date",
                            "categoryAxis": {
                                "dashLength": 1,
                                "minorGridEnabled": true,
                                "gridPosition": "start",
                                "axisAlpha": 0,
                                "axisThickness": 0,
                                "gridColor": "#989191",
                                "gridThickness": 0,
                                "title": "",
                                "titleColor": "#989191",
                                "parseDates": false,
                                "equalSpacing": false,
                                "labelFunction": function (stringValue:string, dateObj:any) {
                                    return moment(dateObj.dataContext.date).utc().format("MMM D HH:mm");
                                }
                            },

                            "balloon": {
                                "borderThickness": 1,
                                "shadowAlpha": 0
                            },
                            "titles": [
                                {
                                    "color": "#989191",
                                    "id": "Title-1",
                                    "size": 15,
                                    "text": "Lateral Movement"
                                }
                            ]

                        }
                    },

                };
            }]
        );
}());


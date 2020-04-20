(function () {
    'use strict';

    angular.module("Fortscale").factory("geoHopping",
        ["securityFeed", "utils", "widgetViews", function (securityFeed, utils, widgetViews) {
            return {
                get graphSettings () {
                    return {
                        "value": "date_time",
                        "unique": "id",
                        "label": "name",
                        "eventLabelField": "country",
                        "scales": {
                            x: {
                                type: "time"
                            }
                        },
                        axes: {
                            x: {
                                type: "time",
                                label: "VPN Events Time"
                            }
                        },
                        options: {
                            margins: {
                                top: 0,
                                left: 0,
                                right: 5,
                                bottom: 0
                            }
                        },
                        eventTooltip: "{{city}}, {{country}} @ {{date_time:date}}"
                    };
                },
                get tableSettings () {
                    return {
                        fields: [
                            {
                                name: "Username",
                                value: "{{username}}",
                                link: "#/d/explore/vpn?filters=vpn.username%3D{{username}}",
                                externalLinks: [{
                                    url: "#/user/{{userId}}/user_overview",
                                    icon: "user",
                                    tooltip: "Go to {{username}}'s page"
                                }]
                            },
                            {
                                name: "Source",
                                value: "{{source_ip}}",
                                link: "#/d/explore/vpn?filters=vpn.source_ip%3D{{source_ip}}"
                            },
                            {
                                name: "Country",
                                value: "{{country}}",
                                link: "#/d/explore/vpn?filters=vpn.country%3D{{country}}"
                            },
                            {
                                name: "City",
                                value: "{{city}}",
                                link: "#/d/explore/vpn?filters=vpn.city%3D{{city}}"
                            },
                            {
                                name: "Time",
                                value: "{{date_time:date}}"
                            }
                        ]
                    };
                },
                getUserEvents: function (start, end, userId) {
                    return securityFeed.getNotifications({
                        after: start,
                        before: end,
                        userIds: userId,
                        types: "VpnGeoHoppingNotificationGenerator"
                    }).then(function (notifications) {
                        var userEventsIndex = {},
                            userEvents = [];

                        notifications.forEach(function (notification) {
                            var user = userEventsIndex[notification.userid];
                            if (!user) {
                                user = userEventsIndex[notification.userid] =
                                {name: notification.username, id: notification.userid, events: []};
                            }

                            user.events.push({
                                date_time: utils.date.getMoment(notification.event_time_utc).toDate(),

                                country: utils.strings.capitalize(notification.country),
                                city: utils.strings.capitalize(notification.city),
                                source_ip: notification.source_ip,
                                username: notification.username,
                                local_ip: notification.local_ip,
                                id: notification.id,
                                userId: notification.userid
                            });
                        });

                        for (var userId in userEventsIndex) {
                            if (userEventsIndex.hasOwnProperty(userId)) {
                                userEvents.push(userEventsIndex[userId]);
                            }
                        }

                        return userEvents;
                    });
                },
                getTableData: function(_view, data, params, rawData) {
                    data = _.uniq(data,'id');

                    return widgetViews.parseViewData(_view, data, params, rawData);
                }
            };

        }]);
}());

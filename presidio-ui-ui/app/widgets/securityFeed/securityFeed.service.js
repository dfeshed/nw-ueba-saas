(function () {
    'use strict';

    angular.module("SecurityFeed").factory("securityFeed",
        ["reports", "comments", "utils", function (reports, comments, utils) {
            return {
                addComment: function (notification, replyTo, message) {
                    return reports.runReport("notifications.addComment", {
                        notificationId: notification.id,
                        message: message,
                        replyTo: replyTo
                    }).then(function (results) {
                        return results.data[0].comments.pop();
                    });
                },
                saveFlag: function (notification, flag) {
                    return reports.runReport("notifications.flag_notification", {
                        notificationId: notification.id,
                        flag: flag
                    }).then(function (results) {
                        return results;
                    });
                },
                getNotifications: function (options) {
                    var reportParams = {};
                    if (options.after) {
                        reportParams.start = utils.date.toUnixTimestamp(options.after);
                    }

                    if (options.before) {
                        var beforeDate = utils.date.getMoment(options.before).toDate();
                        if (!beforeDate.getHours() && !beforeDate.getMinutes()) {
                            beforeDate.setDate(beforeDate.getDate() + 1);
                        }

                        reportParams.end = utils.date.toUnixTimestamp(beforeDate);
                    }

                    reportParams.notifications_events_dates =
                        (reportParams.start || "null") + "," + (reportParams.end || "null");

                    if (options.userIds) {
                        reportParams.userId =
                            angular.isArray(options.userIds) ? options.userIds.join(",") : options.userIds;
                    }

                    reportParams.showDismissed = options.showDismissed;

                    if (options.types) {
                        reportParams.type = angular.isArray(options.types) ? options.types.join(",") : options.types;
                    }

                    reportParams.size = options.maxResults ? options.maxResults : 9999;

                    if (options.page) {
                        reportParams.page = options.page;
                    }

                    return reports.runReportById("notifications", reportParams, true).then(function (results) {
                        return results.data;
                    });
                },
                dismiss: function (notification) {
                    return reports.runReport("notifications.dismiss_notification", {
                        notificationId: notification.id
                    });
                },
                undismiss: function (notification) {
                    return reports.runReport("notifications.undismiss_notification", {
                        notificationId: notification.id
                    });
                },
                get generatorTypes () {
                    return [
                        {name: "(Show all)", id: null},
                        {name: "Geo-hopping VPN sessions", "id": "VpnGeoHoppingNotificationGenerator"},
                        {name: "New VPN source country", "id": "NewCountryInVPNGenerator"},
                        {name: "User score shot up", "id": "ScoreShotUpGenerator"},
                        {name: "User status changed", "id": "UserCreatedEnabledDeletedGenerator"},
                        {name: "VPN brute force", "id": "VPNBruteForce"}
                    ];
                },
                userSearchSettings: function () {
                    return {
                        "reports": [
                            {
                                "dataSource": "api",
                                "endpoint": {
                                    "entity": "user",
                                    "method": "search"
                                },
                                "options": {
                                    "count": 10
                                },
                                "fields": {
                                    "name": {"type": "string"},
                                    "id": {"type": "string"}
                                },
                                "params": [
                                    {
                                        "field": "prefix",
                                        "type": "string",
                                        "dashboardParam": "term"
                                    }
                                ],
                                mock_data: "user_search"
                            }
                        ],
                        "search": {
                            "dataEntity": "users",
                            "dataEntityField": "id",
                            "labelField": "display_name"
                        },
                        "resultField": "name",
                        "value": "{{id}}",
                        "showValueOnSelect": true,
                        "placeholder": "Filter by user"
                    };
                },
                setNotificationComments: function (notification) {
                    return reports.runReport("notifications.notification",
                        {notificationId: notification.id}).then(function (result) {
                        var notificationData = result.data[0];
                        notification.comments = comments.listToTree(notificationData.comments);
                        notification.loadedComments = true;
                        return notification;
                    });
                }
            };

        }]);
}());

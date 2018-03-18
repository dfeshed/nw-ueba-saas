(function () {
    'use strict';

    angular.module("SecurityFeed").controller("SecurityFeedController",
        ["$scope", "securityFeed", "eventBus", "state", "utils",
            function ($scope, securityFeed, eventBus, state, utils) {

                function onParamsChange (e, params) {
                    var availableParams = ["start", "end", "userId", "userName", "showDismissed", "type"];
                    setParams(params);

                    for (var paramName in params) {
                        if (params.hasOwnProperty(paramName)) {
                            if (~availableParams.indexOf(paramName)) {
                                $scope.widget.refresh();
                                return;
                            }
                        }
                    }
                }

                function setParams (params) {
                    if (params.userId !== undefined && params.userName !== undefined) {
                        $scope.userFilter =
                            params.userId && params.userName ? {value: params.userId, label: params.userName} : null;
                    }

                    if (params.start !== undefined) {
                        $scope.startDate =
                            params.start ? utils.date.getMoment(params.start).format("YYYY-MM-DD") : null;
                    }

                    if (params.end !== undefined) {
                        $scope.endDate = params.end ? utils.date.getMoment(params.end).format("YYYY-MM-DD") : null;
                    }

                    if (params.showDismissed !== undefined) {
                        $scope.showDismissed = !!params.showDismissed;
                    }

                    if (params.type !== undefined) {
                        $scope.typeFilter = params.type;
                    }
                }

                function init () {
                    setParams(state.currentParams);
                }

                $scope.startDate = null;
                $scope.endDate = null;

                $scope.generatorTypes = securityFeed.generatorTypes;
                $scope.typeFilter = $scope.generatorTypes[0].id;

                $scope.showDismissed = false;

                // The optional names and their display names
                $scope.flagsName = {"FP": "Not Interesting", "TP": "Nailed it!"};

                // options for flags drop-down
                $scope.flagOptions = [
                    {
                        "value": "FP",
                        "label": $scope.flagsName.FP
                    },
                    {
                        "value": "TP",
                        "label": $scope.flagsName.TP
                    }
                ];

                // options for drop down, including remove-flag option
                $scope.flagOptionsWithRemove =
                    [{
                        "value": "",
                        "label": "Remove Flag"
                    }].concat($scope.flagOptions);

                $scope.$on("$destroy", function () {
                    eventBus.unsubscribe("dashboardParamsChange", onParamsChange);
                });

                $scope.showComments = function (item) {
                    if (!item.loadedComments) {
                        item.showComments = true;
                        securityFeed.setNotificationComments(item);
                    }
                    else {
                        item.showComments = !item.showComments;
                    }
                };

                // Show/Hide the flags drop down
                $scope.showFlags = function (item) {
                    item.showFlags = !item.showFlags;
                };

                $scope.saveNewComment = function (notification, replyTo) {
                    if (notification.newComment) {
                        securityFeed.addComment(notification, replyTo,
                            notification.newComment).then(function (comment) {
                            notification.newComment = null;
                            notification.comments.splice(0, 0, comment);
                            notification.commentsCount++;

                        });
                    }
                };

                $scope.saveComment = function (notification, message, comment) {
                    securityFeed.addComment(notification, comment.id, message).then(function (savedComment) {
                        if (!comment.children) {
                            comment.children = [];
                        }

                        comment.children.splice(0, 0, savedComment);
                        comment.reply = null;
                        comment.showReply = null;
                        notification.commentsCount++;
                    });
                };

                // Send request (using the service) to save the flag in the server
                $scope.saveFlag = function (notification) {
                    // send request to server
                    securityFeed.saveFlag(notification, notification.flag)
                        // If success
                        .then(function () {
                            // hide drop down
                            notification.showFlags = false;
                        });
                };

                $scope.dismiss = function (notification) {
                    if (confirm("Are you sure you wish to dismiss this notification?")) {
                        securityFeed.dismiss(notification).then(function () {
                            $scope.view.data.splice($scope.view.data.indexOf(notification), 1);
                            $scope.widget.totalResults--;
                        });
                    }
                };

                $scope.undismiss = function (notification) {
                    securityFeed.undismiss(notification).then(function () {
                        notification.dismissed = false;
                    });
                };

                $scope.onTypeSelect = function (type) {
                    $scope.setParams({type: type, page: null});
                };

                $scope.onStartDateChange = function (date) {
                    $scope.setParams({start: date ? Math.floor(date.valueOf() / 1000) : null, page: null});
                };
                $scope.clearStartDate = function () {
                    $scope.setParams({start: null});
                    $scope.startDate = null;
                };

                $scope.onEndDateChange = function (date) {
                    $scope.setParams({end: date ? Math.floor(date / 1000) : null, page: null});
                };
                $scope.clearEndDate = function () {
                    $scope.setParams({end: null});
                    $scope.endDate = null;
                };

                $scope.setUserFilter = function (userId, userDisplayName) {
                    $scope.userFilter = {value: userId, label: userDisplayName};
                    $scope.setParams({userId: userId, userName: userDisplayName, page: null});
                };

                $scope.clearUser = function () {
                    $scope.userFilter = null;
                    $scope.setParams({userId: null, userName: null});
                };

                $scope.changeShowDismissed = function () {
                    $scope.setParams({showDismissed: !$scope.showDismissed ? 1 : null, page: null});
                };

                $scope.userSearchSettings = securityFeed.userSearchSettings();

                eventBus.subscribe("dashboardParamsChange", onParamsChange);


                init();
            }]);
}());

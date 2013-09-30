angular.module("Fortscale").controller("UserComparisonController", ["$scope", "$http", "database", function ($scope, $http, database) {
    var otherUsers = [
        {"name": "Charles Kavanagh", "username": "CharlesK", "jobTitle": "Operations Specialist", "score": 65, "trend": "+30%", "location": "NYC"},
        {"name": "James Albert", "username": "JamesA", "jobTitle": "Operations Specialist", "score": 53, "trend": "+0.8%", "location": "NYC"},
        {"name": "Jim Knox", "username": "JimK", "jobTitle": "Operations Specialist", "score": 45, "trend": "+0.7%", "location": "NYC"},
        {"name": "Lucy Fulsom", "username": "LucyF", "jobTitle": "Operations Specialist", "score": 29, "trend": "+1.65%", "location": "NYC"}
    ];

    var selectTabs = {
        groups: function () {
            var conditionUsernames = [];
            angular.forEach($scope.users, function (user) {
                conditionUsernames.push(user.username);
            });

            database.query({
                entity: "user_groups",
                "conditions": [
                    {
                        "field": "username",
                        "operator": "included",
                        "value": conditionUsernames
                    }
                ]
            }).then(function (results) {
                    $scope.chartSettings.itemField = switchedFields ? "username" : "group";
                    $scope.chartSettings.childrenField = switchedFields ? "group" : "username";
                    $scope.chartData = results.data;
                });
        },
        machines: function () {
            var conditionUsernames = [];
            angular.forEach($scope.users, function (user) {
                conditionUsernames.push(user.username);
            });

            database.query({
                entity: "users_machines",
                "conditions": [
                    {
                        "field": "username",
                        "operator": "included",
                        "value": conditionUsernames
                    }
                ]
            }).then(function (results) {
                    $scope.chartSettings.itemField = switchedFields ? "username" : "hostname";
                    $scope.chartSettings.childrenField = switchedFields ? "hostname" : "username";
                    $scope.chartData = results.data;
                });
        },
        groupPolicy: function () {
            var conditionUsernames = [];
            angular.forEach($scope.users, function (user) {
                conditionUsernames.push(user.username);
            });

            database.query({
                entity: "users_group_policy",
                "conditions": [
                    {
                        "field": "username",
                        "operator": "included",
                        "value": conditionUsernames
                    }
                ]
            }).then(function (results) {
                    $scope.chartSettings.itemField = switchedFields ? "username" : "policy";
                    $scope.chartSettings.childrenField = switchedFields ? "policy" : "username";
                    $scope.chartData = results.data;
                });
        }
    };

    var currentTab = selectTabs.groups;
    var switchedFields = false;

    $scope.users = [
        {
            "name": "Andrea Smith",
            "username": "AndreaS",
            "jobTitle": "Operations Specialist",
            "location": "NYC",
            "score": 94,
            "trend": "+34%",
            "fixed": true
        }
    ];

    currentTab();

    $scope.searchUsers = function (search) {
        if (!search)
            return;

        $scope.users = [$scope.users[0]].concat(otherUsers);

        currentTab();
        $scope.lastSearch = search;
        $scope.usersSearch = "";
    };

    $scope.clearUsers = function () {
        $scope.users = [$scope.users[0]];
        currentTab();
    };

    $scope.removeUser = function (userIndex) {
        $scope.users.splice(userIndex, 1);
        currentTab();
    };

    $scope.highlight = function (user) {
        $scope.highlightedUser = user;
    };

    $scope.chartSettings = {
        "height": "700px",
        "itemField": "group",
        "childrenField": "username",
        "valueIsCount": true
    };

    $scope.switchChartFields = function () {
        var childrenField = $scope.chartSettings.childrenField;
        $scope.chartSettings.childrenField = $scope.chartSettings.itemField;
        $scope.chartSettings.itemField = childrenField;
        $scope.$broadcast("refresh");
        switchedFields = !switchedFields;
    };

    $scope.comparisonViews = [
        {
            data: [
                { display: "Groups", id: "groups" },
                { display: "Account Properties", id: "groupPolicy" },
                { display: "Machines", id: "machines" }
            ],
            settings: {
                "tab": {
                    "display": "{{name}}",
                    "id": "{{id}}",
                    "selected": "@currentFeature"
                },
                "label": {
                    "value": "{{score}}",
                    "style": "score",
                    "styleParams": {
                        "value": "score"
                    }
                },
                "onSelect": function (tab) {
                    currentTab = selectTabs[tab.id];
                    currentTab();
                }
            }
        }
    ];
}]);
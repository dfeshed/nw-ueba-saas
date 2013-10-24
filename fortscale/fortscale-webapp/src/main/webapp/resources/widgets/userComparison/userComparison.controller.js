angular.module("Fortscale").controller("UserComparisonController", ["$scope", "$http", "$location", "database", "bubblesChartWidgetData", "users", function ($scope, $http, $location, database, bubblesChartWidgetData, users) {
    var userIds,
        userIdsStorageKey = "userComparisonUserIds";

    var selectTabs = {
        groups: function () {
            var groupsData = [];
            angular.forEach($scope.users, function(user){
                angular.forEach(user.groups, function(group){
                    groupsData.push({
                        username: user.name,
                        group: group.name
                    })
                });
            });

            if ($scope.chartSettings){
                $scope.chartSettings.itemField = switchedFields ? "username" : "group";
                $scope.chartSettings.childrenField = switchedFields ? "group" : "username";
                $scope.chartData = bubblesChartWidgetData.getData({ settings: $scope.chartSettings }, groupsData);
            }
        },
        machines: function(){

        }
    };

    $scope.userComparisonSearchSettings = angular.extend(users.getSearchSettings(), { "placeholder": "Search for users to add" });
    $scope.users = [];

    $scope.addUsers = function(userId, rememberUsers){
        if (~userIds.indexOf(userId))
            return false;

        if (rememberUsers){
            userIds.push(userId);
            saveUsersInStorage();
        }

        users.getUsersDetails(userId).then(function(userDetails){
            $scope.users = $scope.users.concat(userDetails);
            currentTab();
        }, function(error){
            console.error("Can't get user details: ", error);
        })
    };

    var currentTab = selectTabs.groups;
    var switchedFields = false;

    currentTab();

    $scope.clearUsers = function () {
        $scope.users = [];
        currentTab();
        localStorage.removeItem(userIdsStorageKey);
        $location.search("users", null);
    };

    $scope.removeUser = function (userIndex) {
        $scope.users.splice(userIndex, 1);
        currentTab();
        saveUsersInStorage();
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
        switchedFields = !switchedFields;
        currentTab();
    };

    $scope.comparisonViews = [
        {
            data: [
                { display: "Groups", id: "groups" },
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

    function saveUsersInStorage(){
        localStorage.setItem(userIdsStorageKey, userIds.join(","));
    }

    function init(){
        userIds = localStorage.getItem(userIdsStorageKey);
        if (userIds)
            userIds = userIds.split(",");
        else
            userIds = [];

        if ($location.search().users){
            var queryUsers = $location.search().users.split(",");
            angular.forEach(queryUsers, function(userId){
                if (!~userIds.indexOf(userId))
                    userIds.push(userId);
            });
        }

        if (userIds.length)
            $scope.addUsers(userIds);
    }

    init();
}]);
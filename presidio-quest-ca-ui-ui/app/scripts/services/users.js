(function () {
    'use strict';

    angular.module("Fortscale").factory("users", [function () {
        return {
            getSearchSettings: function () {
                return {
                    "search": {
                        "dataEntity": "users",
                        "dataEntityField": ["id", "normalized_username"],
                        "labelField": "display_name"
                    },
                    "resultField": "displayname",
                    "value": "{{id}}",
                    "showValueOnSelect": false,
                    "placeholder": "All users",
                    "onSelect": function (e) {
                        if (e.$value) {
                            window.location.hash = "#/user/" + e.$value + "/user_overview";
                        }
                    }
                };
            }
        };
    }]);
}());

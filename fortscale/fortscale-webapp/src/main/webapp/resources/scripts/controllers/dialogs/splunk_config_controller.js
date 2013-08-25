angular.module("Fortscale").controller("SplunkConfigController", ["$scope", function($scope){
    $scope.fields = [
        {
            label: "Username",
            id: "username",
            tooltip: "Your Splunk username",
            value: getStorageValue("username")
        },
        {
            label: "Password",
            id: "password",
            type: "password",
            tooltip: "Your Splunk password",
            value: getStorageValue("password")
        },
        {
            label: "URL",
            id: "url",
            tooltip: "The location of your Splunk server's API. By default it's on the Splunk installation machine, on port 8089. For example: https://localhost:8089",
            value: getStorageValue("scheme") + "://" + getStorageValue("host") + ":" + getStorageValue("port"),
            width: 400,
            setFunction: function(value){
                var hostMatch = value.match(/^(https?):\/\/([^:]+):(\d+)/);
                if (!hostMatch)
                    return false;

                localStorage.splunk_config_scheme = hostMatch[1];
                localStorage.splunk_config_host = hostMatch[2];
                localStorage.splunk_config_port = hostMatch[3];

                return true;
            }
        },
        {
            label: "Splunk version",
            id: "version",
            tooltip: "The version of your Splunk installation",
            value: getStorageValue("version"),
            width: 50
        }
    ];

    $scope.submit = function(){
        var proceed = true;

        angular.forEach($scope.fields, function(field){
            if (!field.value){
                field.error = "Required field"
                proceed = false;
            }
            else{
                if (field.setFunction){
                    var success = field.setFunction(field.value);
                    if (!success){
                        field.error = "Invalid value";
                        proceed = false;
                    }
                }
                else
                    localStorage.setItem("splunk_config_" + field.id, field.value);
            }
        });

        if (proceed)
            location.reload();
    };

    function getStorageValue(key){
        return localStorage.getItem("splunk_config_" + key);
    }
}]);

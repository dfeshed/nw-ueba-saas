(function () {
    'use strict';
    angular.module("Loader",
        ["DataEntities", "Controls", 'Fortscale.shared.services.assert', "Fortscale.remoteAppConfig"])
        .run(["dataEntities", "controls", "remoteAppConfig", "$q",
            function (dataEntities, controls, remoteAppConfig, $q) {

                // If last login was more then an hour ago
                // var lastLoginTime = parseInt(localStorage.getItem('last-login-time'), 10);
                // if (lastLoginTime && lastLoginTime + (1000 * 60 * 60) < Date.now().valueOf()) {
                //     // If there is session login then do nothing. http-request-interceptor will do
                //     // what needs to be done. If no session login, go to login.
                //     var sessionLogin = sessionStorage.getItem('session-login');
                //     if (!sessionLogin) {
                //         window.location.href = 'signin.html?redirect=' +
                //             encodeURIComponent(document.location.hash);
                //         return;
                //     }
                // }

                var initPromises = [dataEntities.initEntities(), controls.initControls(),
                    remoteAppConfig.initRemoteAppConfig()];

                // After all pre loaded resources were loaded will start the Fortscale application
                $q.all(initPromises).then(function () {
                    angular.element(document).ready(function () {
                        angular.bootstrap(document, ["Fortscale"]);
                    });
                });
            }]);
}());

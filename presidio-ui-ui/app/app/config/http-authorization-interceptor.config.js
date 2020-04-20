(function () {
    'use strict';

    var writeLastLoginTimeoutId = null;

    /**
     * Writes the last successfull rest api time to localStorage
     */
    function writeLastLoginTime () {
        localStorage.setItem('last-login-time', new Date().valueOf() - 2000);
    }

    /**
     * Creates a 2 s throttle before writing to localStorage
     */
    function setLastLoginTimeout () {
        clearTimeout(writeLastLoginTimeoutId);
        writeLastLoginTimeoutId = setTimeout(writeLastLoginTime, 2000);
    }


    angular.module('Config')
        .factory('httpAuthorizationErrorInterceptor', [
            '$q',
            '$rootScope',
            'BASE_URL',
            function ($q, $rootScope, BASE_URL) {
                return {
                    response: function (response) {
                        // If a successfull attempt was made to the server api, the last login
                        // time is updated
                        var rgx = new RegExp('^' + BASE_URL);
                        if (rgx.test(response.config.url)) {
                            setLastLoginTimeout();
                        }
                        return response;
                    },
                    responseError: function (rejection) {

                        if (rejection.status === 401 && rejection.statusText === 'Unauthorized') {

                            // Get a session login. If it was not found, go to login without
                            // the modal.
                            var sessionLogin = sessionStorage.getItem('session-login');
                            if (!sessionLogin) {
                                // var href = 'signin.html?redirect=' +
                                //     encodeURIComponent(document.location.hash);
                                // window.location.href = href;
                                console.log("not empty session");
                            } else {
                                sessionStorage.removeItem('session-login');
                                localStorage.removeItem('last-login-time');
                                $rootScope.modal = {
                                    show: true,
                                    src: rejection.status === 403 ?
                                        "views/modals/password_expired.html" :
                                        "views/modals/session_expired.html"
                                };
                            }
                        }

                        return $q.reject(rejection);

                    }
                };
            }])
        .config(['$httpProvider', function ($httpProvider) {
            $httpProvider.interceptors.push('httpAuthorizationErrorInterceptor');
        }]);

}());















(function () {
    'use strict';
    angular.module('Fortscale.shared.filters', []);
}());

var Fortscale;
(function (Fortscale) {
    var SystemSetupAppLoader;
    (function (SystemSetupAppLoader) {
        angular.module('Fortscale.SystemSetupAppLoader', [
            'Fortscale.SystemSetupApp.config.values',
            'Fortscale.SystemSetupApp.shared.services.authUtils'
        ])
            .run([
            '$window',
            '$location',
            'authUtils',
            '$log',
            function ($window, $location, authUtils, $log) {
                authUtils.getCurrentUser()
                    .then(function (analyst) {
                    // Bootstrap system-setup-app
                    angular.bootstrap(document, ['Fortscale.SystemSetupApp']);
                })
                    .catch(function (err) {
                    if (err.status === 401) {
                        $window.location.href = "signin.html?absRedirect=" + encodeURIComponent($location.absUrl());
                    }
                    else {
                        $log.error(err);
                    }
                });
            }
        ]);
    })(SystemSetupAppLoader = Fortscale.SystemSetupAppLoader || (Fortscale.SystemSetupAppLoader = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        angular.module('Fortscale.SystemSetupApp', [
            'Fortscale.SystemSetupApp.config.values',
            'ngAnimate',
            'ui.router',
            'fsTemplates',
            'Fortscale.shared.services.assert',
            'Fortscale.shared.filters',
            'Fortscale.SystemSetupApp.shared.services',
            'Fortscale.SystemSetupApp.layouts',
            'Fortscale.SystemSetupApp.shared.components'
        ])
            .run([
            '$rootScope',
            function ($rootScope) {
                // Hide splash screen
                $rootScope.hideLoader = true;
                $rootScope.showMainView = true;
            }
        ]);
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var values;
            (function (values) {
                angular.module('Fortscale.SystemSetupApp.config.values', []);
            })(values = shared.values || (shared.values = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var layouts;
        (function (layouts) {
            angular.module('Fortscale.SystemSetupApp.layouts', []);
        })(layouts = SystemSetupApp.layouts || (SystemSetupApp.layouts = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var components;
            (function (components) {
                angular.module('Fortscale.SystemSetupApp.shared.components', []);
            })(components = shared.components || (shared.components = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var services;
            (function (services) {
                angular.module('Fortscale.SystemSetupApp.shared.services', [
                    'Fortscale.SystemSetupApp.shared.services.authUtils',
                    'Fortscale.SystemSetupApp.shared.services.navigationUtils',
                    'Fortscale.SystemSetupApp.shared.services.distinguishedNameUtils',
                    'Fortscale.shared.services.toastrService'
                ]);
            })(services = shared.services || (shared.services = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

(function () {
    'use strict';
    /**
     * Returns a filter
     *
     * @returns {function(any): number}
     */
    function fsPartialStrong($sce) {
        /**
         * This filter get text, and part of substring of this text,
         * it returns html which emphasis the the sub text (if such given).
         * Pay attention that angular process html only if you use it has ng-html-bind and not {{}}
         *
         *  prefixOnly - IF TRUE - mark only textToMakeStronger which is in the begining of the text
         *  condition-optional. If false, return the original value
         *
         */
        return function (val, textToMakeStronger, prefixOnly, allowBoldCondition) {
            if (allowBoldCondition === false) {
                return val;
            }
            //If no val or no textToMakeStronger return the value.
            if (!textToMakeStronger || !val) {
                return val;
            }
            var fullTextLower = val.toLowerCase();
            var strongTextLower = textToMakeStronger.toLowerCase();
            var startIndex = -1;
            if (prefixOnly) {
                startIndex = fullTextLower.startsWith(strongTextLower) ? 0 : -1;
            }
            else {
                //Extract the start and end indexes of the part which should be emphasis
                startIndex = fullTextLower.indexOf(strongTextLower);
            }
            if (startIndex === -1) {
                return val;
            }
            var endIndex = startIndex + strongTextLower.length;
            //Rebuild the string with the strong part
            var beforeStrongPart = val.substr(0, startIndex);
            var strongPart = val.substr(startIndex, strongTextLower.length);
            var afterStrongPart = val.substring(endIndex);
            var newHTMLUntrusted = beforeStrongPart + "<span style='color: #024d89;font-weight: 700;'>" + strongPart + "</span>" + afterStrongPart;
            return $sce.trustAsHtml(newHTMLUntrusted);
        };
    }
    angular.module('Fortscale.shared.filters')
        .filter('fsPartialStrong', ['$sce', fsPartialStrong]);
}());

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var values;
            (function (values) {
                angular.module('Fortscale.SystemSetupApp.config.values')
                    .constant('BASE_URL', '/fortscale-webapp/api')
                    .constant('BASE_WEBSOCKET_URL', '/fortscale-webapp');
            })(values = shared.values || (shared.values = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var values;
            (function (values) {
                angular.module('Fortscale.SystemSetupApp.config.values')
                    .constant('LOG_REPOSITORY_TYPES', [
                    {
                        value: 'SPLUNK',
                        name: 'Splunk'
                    },
                    {
                        value: 'QRADAR',
                        name: 'IBM QRadar'
                    }
                ]);
            })(values = shared.values || (shared.values = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var config;
        (function (config) {
            angular.module('Fortscale.SystemSetupApp')
                .config([
                '$stateProvider',
                '$urlRouterProvider',
                function ($stateProvider, $urlRouterProvider) {
                    //Configuration for UI-Router
                    $urlRouterProvider.otherwise('/active-directory-setup');
                    $stateProvider
                        .state('systemSetup', {
                        abstract: true,
                        templateUrl: 'system-setup-app/layouts/system-setup/system-setup.view.html',
                        controller: 'systemSetupController',
                        controllerAs: 'systemSetupCtrl',
                    })
                        .state('systemSetup.activeDirectorySetup', {
                        url: '/active-directory-setup',
                        templateUrl: 'system-setup-app/layouts/active-directory-setup/active-directory-setup.view.html',
                        controller: 'activeDirectorySetupController',
                        controllerAs: 'activeDirectoryCtrl',
                    })
                        .state('systemSetup.logRepositorySetup', {
                        url: '/log-repository-setup',
                        templateUrl: 'system-setup-app/layouts/log-repository-setup/log-repository-setup.view.html',
                        controller: 'logRepositorySetupController',
                        controllerAs: 'logRepositoryCtrl',
                    })
                        .state('systemSetup.tagsSetup', {
                        url: '/tags-setup',
                        templateUrl: 'system-setup-app/layouts/tags-setup/tags-setup.view.html',
                        controller: 'tagsSetupController',
                        controllerAs: 'tagsCtrl',
                    })
                        .state('systemSetup.setupSummary', {
                        url: '/setup-summary',
                        templateUrl: 'system-setup-app/layouts/setup-summary/setup-summary.view.html',
                        controller: 'setupSummaryController',
                        controllerAs: 'setupSummaryCtrl',
                    });
                }]);
        })(config = SystemSetupApp.config || (SystemSetupApp.config = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var config;
        (function (config) {
            angular.module('Fortscale.SystemSetupApp')
                .config([
                'navigationUtilsProvider',
                function (navigationUtilsProvider) {
                    navigationUtilsProvider
                        .registerNavItem({
                        state: 'systemSetup.activeDirectorySetup',
                        nextState: 'systemSetup.logRepositorySetup',
                        position: 0,
                        title: 'Active Directory'
                    })
                        .registerNavItem({
                        state: 'systemSetup.logRepositorySetup',
                        nextState: 'systemSetup.tagsSetup',
                        position: 1,
                        title: 'Log Repository'
                    })
                        .registerNavItem({
                        state: 'systemSetup.tagsSetup',
                        nextState: 'systemSetup.setupSummary',
                        position: 2,
                        title: '<span>Tags</span>'
                    });
                }]);
        })(config = SystemSetupApp.config || (SystemSetupApp.config = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var config;
        (function (config) {
            angular.module('Fortscale.SystemSetupApp')
                .factory('httpAuthInterceptor', [
                '$q', '$window', '$location',
                function ($q, $window, $location) {
                    return {
                        responseError: function (rejection) {
                            if (rejection.status === 401) {
                                $window.location.href = "signin.html?absRedirect=" + encodeURIComponent($location.absUrl());
                            }
                            return $q.reject(rejection);
                        }
                    };
                }
            ])
                .config([
                '$httpProvider',
                function (httpProvider) {
                    httpProvider.interceptors.push('httpAuthInterceptor');
                }
            ]);
        })(config = SystemSetupApp.config || (SystemSetupApp.config = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

(function () {
    'use strict';
    function assertFactory() {
        /**
         *
         * @param {boolean | *} condition
         * @param {string=} message
         * @param {function=} ErrorType The object should be
         */
        function assert(condition, message, ErrorType) {
            // Create an early return when condition is fulfilled to prevent execution of futile code.
            if (condition) {
                return;
            }
            // Set defaults
            message = message || '';
            var error;
            // Verify error type is valid. If it's not, then Error type should be error
            if (typeof ErrorType === 'function') {
                // Create new error from Error type
                error = new ErrorType(message);
            }
            // If ErrorType is not a function or (newly populated) error is not an instance of Error
            // Then error should be new Error
            if (!(error instanceof Error)) {
                error = new Error(message);
            }
            throw error;
        }
        /**
         * Validates that a variable is string and not an empty string
         *
         * @param {string} str
         * @param {string} strName
         * @param {string=} errMsg Defaults to empty string
         * @param {boolean=} isOptional Defaults to false
         * @param {boolean=} canBeEmpty Defaults to false
         */
        assert.isString = function (str, strName, errMsg, isOptional, canBeEmpty) {
            errMsg = errMsg || '';
            isOptional = !!isOptional;
            canBeEmpty = !!canBeEmpty;
            if (!(str === undefined && isOptional)) {
                assert(!_.isUndefined(str), errMsg + strName + ' must be provided.', ReferenceError);
                assert(_.isString(str), errMsg + strName + ' must be a string.', TypeError);
                assert(str !== '' || canBeEmpty, errMsg + strName + ' must not be an empty string.', RangeError);
            }
        };
        /**
         * Validates that a variable is string and not an empty string
         *
         * @param {number} num
         * @param {string} numName
         * @param {string=} errMsg Defaults to empty string
         * @param {boolean=} isOptional Defaults to false
         */
        assert.isNumber = function (num, numName, errMsg, isOptional) {
            errMsg = errMsg || '';
            isOptional = !!isOptional;
            if (!(num === undefined && isOptional)) {
                assert(!_.isUndefined(num), errMsg + numName + ' must be provided.', ReferenceError);
                assert(_.isNumber(num), errMsg + numName + ' must be a number.', TypeError);
            }
        };
        /**
         * Validates that a variable is an array
         *
         * @param {string} arr
         * @param {string} arrName
         * @param {string=} errMsg Defaults to empty string
         * @param {boolean=} isOptional Defaults to false
         */
        assert.isArray = function (arr, arrName, errMsg, isOptional) {
            errMsg = errMsg || '';
            isOptional = !!isOptional;
            if (!(arr === undefined && isOptional)) {
                assert(!_.isUndefined(arr), errMsg + arrName + ' must be provided.', ReferenceError);
                assert(_.isArray(arr), errMsg + arrName + ' must be an array.', TypeError);
            }
        };
        /**
         * Validates that a variable is a function
         *
         * @param {string} fn
         * @param {string} fnName
         * @param {string=} errMsg Defaults to empty string
         * @param {boolean=} isOptional Defaults to false
         */
        assert.isFunction = function (fn, fnName, errMsg, isOptional) {
            errMsg = errMsg || '';
            isOptional = !!isOptional;
            if (!(fn === undefined && isOptional)) {
                assert(!_.isUndefined(fn), errMsg + fnName + ' must be provided.', ReferenceError);
                assert(_.isFunction(fn), errMsg + fnName + ' must be a function.', TypeError);
            }
        };
        /**
         * Validates that a variable is a function
         *
         * @param {string} obj
         * @param {string} objName
         * @param {string=} errMsg Defaults to empty string
         * @param {boolean=} isOptional Defaults to false
         */
        assert.isObject = function (obj, objName, errMsg, isOptional) {
            errMsg = errMsg || '';
            isOptional = !!isOptional;
            if (!(obj === undefined && isOptional)) {
                assert(!_.isUndefined(obj), errMsg + objName + ' must be provided.', ReferenceError);
                assert(_.isObject(obj), errMsg + objName + ' must be an object.', TypeError);
            }
        };
        return assert;
    }
    angular.module('Fortscale.shared.services.assert', [])
        .factory('assert', assertFactory)
        .constant('assertConstant', assertFactory());
}());

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var services;
            (function (services) {
                var authUtils;
                (function (authUtils) {
                    var AuthUtilsService = (function () {
                        function AuthUtilsService($http, BASE_URL) {
                            this.$http = $http;
                            this.BASE_URL = BASE_URL;
                        }
                        AuthUtilsService.prototype.getCurrentUser = function () {
                            return this.$http.get(this.BASE_URL + "/analyst/me/details", { cache: true })
                                .then(function (res) {
                                return res.data.data[0];
                            });
                        };
                        AuthUtilsService.$inject = ['$http', 'BASE_URL'];
                        return AuthUtilsService;
                    }());
                    angular.module('Fortscale.SystemSetupApp.shared.services.authUtils', [])
                        .service('authUtils', AuthUtilsService);
                })(authUtils = services.authUtils || (services.authUtils = {}));
            })(services = shared.services || (shared.services = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var services;
            (function (services) {
                var navigationUtils;
                (function (navigationUtils) {
                    /**
                     * NavigationUtilsService class
                     */
                    var NavigationUtilsService = (function () {
                        function NavigationUtilsService(_navItems, assert) {
                            this._navItems = _navItems;
                            this.assert = assert;
                            this._ERR_MSG = 'NavigationUtilsService:';
                        }
                        /**
                         * Return the index of a nav item by state name.
                         * @param state
                         * @returns {number}
                         */
                        NavigationUtilsService.prototype.getIndexByStateName = function (state) {
                            // Validate arguments
                            this.assert.isString(state, 'state', this._ERR_MSG + " getCurrentIndex: ");
                            // Get sorted nav items
                            var navItems = this.getNavItems();
                            // Find index of current nav item (and validate that a nav item was found)
                            return _.findIndex(navItems, { state: state });
                        };
                        /**
                         * Return nav items length
                         * @returns {number}
                         */
                        NavigationUtilsService.prototype.getNumberOfItems = function () {
                            return this._navItems.length;
                        };
                        /**
                         * Returns (cloned) navItems list ordered by position (ascending).
                         * @returns {INavItem[]}
                         */
                        NavigationUtilsService.prototype.getNavItems = function () {
                            var navItem = _.cloneDeep(this._navItems);
                            return _.orderBy(navItem, ['position'], ['asc']);
                        };
                        /**
                         * Returns nav item by state
                         * @param state
                         * @returns {T}
                         */
                        NavigationUtilsService.prototype.getNavItemByState = function (state) {
                            return _.cloneDeep(_.find(this._navItems, { state: state }));
                        };
                        /**
                         *
                         * Returns the next state name from the nav item that has the desired state
                         * @param {string} state
                         * @returns {string}
                         */
                        NavigationUtilsService.prototype.getNextState = function (state) {
                            // validation
                            this.assert.isString(state, 'state', this._ERR_MSG + " getNextState: ");
                            var navItem = _.find(this._navItems, { state: state });
                            if (!navItem) {
                                return null;
                            }
                            return navItem.nextState;
                        };
                        /**
                         * Returns true if the next state is positioned after the current state.
                         *
                         * @param {string} currentState
                         * @param {string} nextState
                         * @returns {boolean}
                         */
                        NavigationUtilsService.prototype.isNavigationForward = function (currentState, nextState) {
                            // Validate arguments
                            this.assert.isString(currentState, 'currentState', this._ERR_MSG + " isNavigationForward: ");
                            this.assert.isString(nextState, 'nextState', this._ERR_MSG + " isNavigationForward: ");
                            // Get sorted nav items
                            var navItems = this.getNavItems();
                            // Find index of current nav item (and validate that a nav item was found)
                            var currentNavIndex = _.findIndex(navItems, { state: currentState });
                            this.assert(currentNavIndex !== -1, this._ERR_MSG + " isNavigationForward: currentState argument - does not correlate to any navigation item.", RangeError);
                            // Find index of next nav item (and validate that a nav item was found)
                            var nextNavIndex = _.findIndex(navItems, { state: nextState });
                            this.assert(nextNavIndex !== -1, this._ERR_MSG + " isNavigationForward: nextState argument - does not correlate to any navigation item.", RangeError);
                            // Return if next nav item is after the the current nav item
                            return (nextNavIndex > currentNavIndex);
                        };
                        return NavigationUtilsService;
                    }());
                    /**
                     * NavigationUtilsProvider class
                     */
                    var NavigationUtilsProvider = (function () {
                        function NavigationUtilsProvider(assert) {
                            var _this = this;
                            this.assert = assert;
                            this._ERR_MSG = 'NavigationUtilsProvider:';
                            this.$get = [
                                'assert',
                                function (assert) {
                                    return new NavigationUtilsService(_this._navItems, assert);
                                }
                            ];
                            this._navItems = [];
                        }
                        /**
                         * Validate a navItemConfig
                         * @param {INavItem} navItemConfig
                         * @private
                         */
                        NavigationUtilsProvider.prototype._validateNavItemConfig = function (navItemConfig) {
                            this.assert.isObject(navItemConfig, 'navItemConfig', this._ERR_MSG + " ");
                            this.assert.isString(navItemConfig.state, 'navItemConfig.state', this._ERR_MSG + " ");
                            this.assert(!(_.find(this._navItems, { state: navItemConfig.state })), this._ERR_MSG + " Trying to add a navigation item with a state that has already been registered. state property must be unique.", RangeError);
                            this.assert.isString(navItemConfig.nextState, 'navItemConfig.nextState', this._ERR_MSG + " ", true);
                            this.assert.isNumber(navItemConfig.position, 'navItemConfig.position', this._ERR_MSG + " ", true);
                            this.assert.isString(navItemConfig.title, 'navItemConfig.title', this._ERR_MSG + " ");
                        };
                        /**
                         * Registers a new navigation item
                         * @param navItemConfig
                         * @returns {Fortscale.SystemSetupApp.shared.services.navigationUtils.NavigationUtilsProvider}
                         */
                        NavigationUtilsProvider.prototype.registerNavItem = function (navItemConfig) {
                            this._validateNavItemConfig(navItemConfig);
                            var localNavItem = _.cloneDeep(navItemConfig);
                            // Automatically set position if one was not provided
                            if (_.isUndefined(localNavItem.position)) {
                                // The next position should be the highest current position plus one.
                                var maxNavItem = _.maxBy(this._navItems, 'position');
                                localNavItem.position = maxNavItem ? maxNavItem.position + 1 : 0;
                            }
                            // Set null as a default nextState
                            if (_.isUndefined(localNavItem.nextState)) {
                                localNavItem.nextState = null;
                            }
                            this._navItems.push(localNavItem);
                            return this;
                        };
                        NavigationUtilsProvider.$inject = ['assertConstant'];
                        return NavigationUtilsProvider;
                    }());
                    angular.module('Fortscale.SystemSetupApp.shared.services.navigationUtils', [])
                        .provider('navigationUtils', NavigationUtilsProvider);
                })(navigationUtils = services.navigationUtils || (services.navigationUtils = {}));
            })(services = shared.services || (shared.services = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var services;
            (function (services) {
                var distinguishedNameUtils;
                (function (distinguishedNameUtils) {
                    var DistinguishedNameUtilsService = (function () {
                        function DistinguishedNameUtilsService() {
                            this._DN_DELIMITER = ';';
                        }
                        /**
                         * Takes a string of delimited (with ';') dnStrings, splits them, trims them, and returns a list of dn strings.
                         * @param dnDelimited
                         * @returns {string|LoDashExplicitWrapper<string>[]|boolean[]}
                         * @private
                         */
                        DistinguishedNameUtilsService.prototype._getTrimmedDnStringsList = function (dnDelimited) {
                            // get a list of trimmed dn strings.
                            // return _.map(dnDelimited.split(this._DN_DELIMITER), dnStr => dnStr.trim());
                            var dns = _.map(dnDelimited.split(this._DN_DELIMITER), function (dnStr) { return dnStr.trim(); });
                            return _.map(dns, function (dn) {
                                var nodes = dn.split(',');
                                return _.map(nodes, function (node) { return node.trim(); }).join(',');
                            });
                        };
                        /**
                         * Validates a distinguished-name
                         * @param dnStr
                         * @returns {boolean}
                         */
                        DistinguishedNameUtilsService.prototype.validateDistinguishedName = function (dnStr) {
                            // Cross my fingers - this might work :)
                            // Its supposed to be one of the dn prefixes, then a '=', then things that are not a comma, and repeat
                            var nodesTest = /^(,?((cn|l|st|o|ou|c|street|dc|uid)=)[^,]+)+$/i;
                            return nodesTest.test(dnStr) && /^[^,]/.test(dnStr);
                        };
                        /**
                         * Validates Distinguished Name Organization
                         * @param dnStr
                         * @returns {boolean}
                         */
                        DistinguishedNameUtilsService.prototype.validateOrganization = function (dnStr) {
                            // Check that it starts with ou=
                            var primaryTest = /^ou=/i;
                            if (!primaryTest.test(dnStr)) {
                                return false;
                            }
                            // validate that it is a valid distinguished name
                            return this.validateDistinguishedName(dnStr);
                        };
                        /**
                         * Validates Distinguished Name Common Name
                         * @param dnStr
                         * @returns {boolean}
                         */
                        DistinguishedNameUtilsService.prototype.validateCommonName = function (dnStr) {
                            // Check that it starts with ou=
                            var primaryTest = /^cn=/i;
                            if (!primaryTest.test(dnStr)) {
                                return false;
                            }
                            // validate that it is a valid distinguished name
                            return this.validateDistinguishedName(dnStr);
                        };
                        /**
                         * Validates a ';' delimited list of Distinguished Names Organization
                         * @param dnDelimited
                         * @returns {boolean}
                         */
                        DistinguishedNameUtilsService.prototype.validateOrganizations = function (dnDelimited) {
                            var _this = this;
                            // get a list of trimmed dn strings.
                            var dnStrs = this._getTrimmedDnStringsList(dnDelimited);
                            // Check that every dn string validates
                            return _.every(dnStrs, function (dnStr) { return _this.validateOrganization(dnStr); });
                        };
                        /**
                         * Validates a ';' delimited list of Distinguished Names Common Names
                         * @param dnDelimited
                         * @returns {boolean}
                         */
                        DistinguishedNameUtilsService.prototype.validateCommonNames = function (dnDelimited) {
                            var _this = this;
                            // get a list of trimmed dn strings.
                            var dnStrs = this._getTrimmedDnStringsList(dnDelimited);
                            // Check that every dn string validates
                            return _.every(dnStrs, function (dnStr) { return _this.validateCommonName(dnStr); });
                        };
                        /**
                         * Trims all dns and dn nodes and return a string
                         * @param dnDelimited
                         * @returns {string[]|boolean[]}
                         */
                        DistinguishedNameUtilsService.prototype.trimDistinguishedNames = function (dnDelimited) {
                            return this._getTrimmedDnStringsList(dnDelimited).join(this._DN_DELIMITER);
                        };
                        return DistinguishedNameUtilsService;
                    }());
                    angular.module('Fortscale.SystemSetupApp.shared.services.distinguishedNameUtils', [])
                        .service('distinguishedNameUtils', DistinguishedNameUtilsService);
                })(distinguishedNameUtils = services.distinguishedNameUtils || (services.distinguishedNameUtils = {}));
            })(services = shared.services || (shared.services = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

/**
 * This is an angular-typescript wrapper on top of toastr library.
 */
var Fortscale;
(function (Fortscale) {
    var shared;
    (function (shared) {
        var services;
        (function (services) {
            var toastrService;
            (function (toastrService) {
                'use strict';
                var ToastrService = (function () {
                    function ToastrService() {
                        // Set timeOut and extendedTimeOut to 0 to make it sticky
                        toastr.options = _.merge({}, toastr.options, {
                            toastClass: 'fs-toast',
                            showMethod: 'fadeIn',
                            hideMethod: 'fadeOut',
                            hideDuration: 500,
                            positionClass: 'toast-top-center',
                            closeButton: true,
                            timeOut: 3000,
                            iconClasses: {
                                error: 'toast-error',
                                info: 'toast-info',
                                success: 'toast-success',
                                warning: 'toast-warning'
                            }
                        });
                    }
                    Object.defineProperty(ToastrService.prototype, "options", {
                        get: function () {
                            return toastr.options;
                        },
                        set: function (options) {
                            toastr.options = options;
                        },
                        enumerable: true,
                        configurable: true
                    });
                    Object.defineProperty(ToastrService.prototype, "version", {
                        get: function () {
                            return toastr.version;
                        },
                        enumerable: true,
                        configurable: true
                    });
                    ToastrService.prototype.subscribe = function (callback) {
                        toastr.subscribe(callback);
                    };
                    ToastrService.prototype.clear = function ($toastElement, clearOptions) {
                        toastr.clear($toastElement, clearOptions);
                    };
                    ToastrService.prototype.remove = function ($toastElement) {
                        toastr.remove($toastElement);
                    };
                    ToastrService.prototype.getContainer = function (options, create) {
                        return toastr.getContainer(options, create);
                    };
                    ToastrService.prototype.error = function (message, title, optionsOverride) {
                        return toastr.error(message, title, optionsOverride);
                    };
                    ToastrService.prototype.info = function (message, title, optionsOverride) {
                        return toastr.info(message, title, optionsOverride);
                    };
                    ToastrService.prototype.success = function (message, title, optionsOverride) {
                        return toastr.success(message, title, optionsOverride);
                    };
                    ToastrService.prototype.warning = function (message, title, optionsOverride) {
                        return toastr.warning(message, title, optionsOverride);
                    };
                    ToastrService.$inject = [];
                    return ToastrService;
                }());
                angular.module('Fortscale.shared.services.toastrService', [])
                    .service('toastrService', ToastrService);
            })(toastrService = services.toastrService || (services.toastrService = {}));
        })(services = shared.services || (shared.services = {}));
    })(shared = Fortscale.shared || (Fortscale.shared = {}));
})(Fortscale || (Fortscale = {}));
/**
 *
 * These are the options default values:
 *
{
    tapToDismiss: true,
        toastClass: 'toast',
    containerId: 'toast-container',
    debug: false,

    showMethod: 'fadeIn', //fadeIn, slideDown, and show are built into jQuery
    showDuration: 300,
    showEasing: 'swing', //swing and linear are built into jQuery
    onShown: undefined,
    hideMethod: 'fadeOut',
    hideDuration: 1000,
    hideEasing: 'swing',
    onHidden: undefined,
    closeMethod: false,
    closeDuration: false,
    closeEasing: false,

    extendedTimeOut: 1000,
    iconClasses: {
    error: 'toast-error',
        info: 'toast-info',
        success: 'toast-success',
        warning: 'toast-warning'
},
    iconClass: 'toast-info',
        positionClass: 'toast-top-right',
    timeOut: 5000, // Set timeOut and extendedTimeOut to 0 to make it sticky
    titleClass: 'toast-title',
    messageClass: 'toast-message',
    escapeHtml: false,
    target: 'body',
    closeHtml: '<button type="button">&times;</button>',
    newestOnTop: true,
    preventDuplicates: false,
    progressBar: false
}
 **/

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var components;
            (function (components) {
                var SystemSetupNavigationController = (function () {
                    function SystemSetupNavigationController($scope, $element, $window, navigationUtils) {
                        this.$scope = $scope;
                        this.$element = $element;
                        this.$window = $window;
                        this.navigationUtils = navigationUtils;
                        this._NAVIGATION_CONTAINER_SELECTOR = '.navigation-container';
                        this._CSS_VAR_ROW_DIM_NAME = '--row-dim';
                        this.staticNavItem = {
                            state: 'systemSetup.setupSummary',
                            nextState: null,
                            title: 'Setup Summary',
                            fontAwesome: 'fa-list-ul',
                            position: null
                        };
                    }
                    /**
                     * Sets the dim of the css variable to be used for the size of the icon.
                     * @private
                     */
                    SystemSetupNavigationController.prototype._setDims = function () {
                        var _this = this;
                        var navigationContainerEl = this.$element.find(this._NAVIGATION_CONTAINER_SELECTOR);
                        this.$scope.$applyAsync(function () {
                            var height = navigationContainerEl.height() / 8;
                            height = height > 100 ? 100 : height;
                            navigationContainerEl[0].style.setProperty(_this._CSS_VAR_ROW_DIM_NAME, height + "px");
                            navigationContainerEl[0].style.opacity = '1';
                        });
                    };
                    /**
                     * Makes sure the css variable is updated if the window is resized.
                     * @private
                     */
                    SystemSetupNavigationController.prototype._initResizeWatch = function () {
                        var ctrl = this;
                        var resizeHandler = function () {
                            ctrl._setDims();
                        };
                        // Add listener
                        ctrl.$window.addEventListener('resize', resizeHandler, false);
                        // Cleanup listener
                        ctrl.$scope.$on('$destroy', function () {
                            ctrl.$window.removeEventListener('resize', resizeHandler, false);
                        });
                    };
                    SystemSetupNavigationController.prototype._getNavItems = function () {
                        this.navItems = this.navigationUtils.getNavItems();
                    };
                    SystemSetupNavigationController.prototype.transitionHandler = function ($event, navItem) {
                        $event.preventDefault();
                        this.onTransition({ navItem: navItem });
                    };
                    SystemSetupNavigationController.prototype.$onInit = function () {
                        this._setDims();
                        this._initResizeWatch();
                        this._getNavItems();
                    };
                    SystemSetupNavigationController.$inject = ['$scope', '$element', '$window', 'navigationUtils'];
                    return SystemSetupNavigationController;
                }());
                var systemSetupNavigationComponent = {
                    controller: SystemSetupNavigationController,
                    templateUrl: 'system-setup-app/shared/components/system-setup-navigation/system-setup-navigation.component.html',
                    bindings: {
                        onTransition: '&'
                    }
                };
                angular.module('Fortscale.SystemSetupApp.shared.components')
                    .component('systemSetupNavigation', systemSetupNavigationComponent);
            })(components = shared.components || (shared.components = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var components;
            (function (components) {
                var NavigationItemController = (function () {
                    function NavigationItemController($scope, $element, $window, $sce, $timeout) {
                        this.$scope = $scope;
                        this.$element = $element;
                        this.$window = $window;
                        this.$sce = $sce;
                        this.$timeout = $timeout;
                    }
                    /**
                     * When _navItem is provided, it is cloned, stored, and unwatched.
                     * @private
                     */
                    NavigationItemController.prototype._initNavItemWatch = function () {
                        var _this = this;
                        var unWatchFn = this.$scope.$watch(function () { return _this._navItem; }, function (navItem) {
                            if (navItem) {
                                // Clone, store, and unwatch
                                _this.navItem = _.cloneDeep(navItem);
                                _this.title = _this.$sce.trustAsHtml(_this.navItem.title);
                                unWatchFn();
                            }
                        });
                    };
                    NavigationItemController.prototype.setHover = function (state) {
                        if (state) {
                            this.$element.addClass('hovered');
                        }
                        else {
                            this.$element.removeClass('hovered');
                        }
                    };
                    NavigationItemController.prototype._setElementIn = function () {
                        var _this = this;
                        this._navItemDelay = (this._navItemDelay || 200) + '';
                        this.$timeout(function () {
                            _this.$element.addClass('nav-item-enter');
                        }, parseInt(this._navItemDelay, 10));
                    };
                    NavigationItemController.prototype.$onInit = function () {
                        this._initNavItemWatch();
                        this._setElementIn();
                    };
                    NavigationItemController.$inject = ['$scope', '$element', '$window', '$sce', '$timeout'];
                    return NavigationItemController;
                }());
                var navigationItemComponent = {
                    controller: NavigationItemController,
                    templateUrl: 'system-setup-app/shared/components/navigation-item/navigation-item.component.html',
                    bindings: {
                        _navItem: '<navItem',
                        _navItemDelay: '@navItemDelay',
                    }
                };
                angular.module('Fortscale.SystemSetupApp.shared.components')
                    .component('navigationItem', navigationItemComponent);
            })(components = shared.components || (shared.components = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var components;
            (function (components) {
                var SystemSetupFormButtonsController = (function () {
                    function SystemSetupFormButtonsController($scope, $element) {
                        this.$scope = $scope;
                        this.$element = $element;
                    }
                    SystemSetupFormButtonsController.$inject = ['$scope', '$element'];
                    return SystemSetupFormButtonsController;
                }());
                var systemSetupFormButtonsComponent = {
                    controller: SystemSetupFormButtonsController,
                    templateUrl: 'system-setup-app/shared/components/system-setup-form-buttons/system-setup-form-buttons.component.html',
                    bindings: {
                        onSave: '&',
                        onSaveAndContinue: '&',
                        disableButtons: '<'
                    }
                };
                angular.module('Fortscale.SystemSetupApp.shared.components')
                    .component('systemSetupFormButtons', systemSetupFormButtonsComponent);
            })(components = shared.components || (shared.components = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var components;
            (function (components) {
                var SystemSetupFormHeaderController = (function () {
                    function SystemSetupFormHeaderController($scope, $element, $sce, $state, navigationUtils) {
                        this.$scope = $scope;
                        this.$element = $element;
                        this.$sce = $sce;
                        this.$state = $state;
                        this.navigationUtils = navigationUtils;
                    }
                    SystemSetupFormHeaderController.prototype.$onInit = function () {
                        if (this._description) {
                            this.description = this.$sce.trustAsHtml(this._description);
                        }
                        this.currentStepIndex = this.navigationUtils.getIndexByStateName(this.$state.current.name) + 1;
                        this.numberOfNavItems = this.navigationUtils.getNumberOfItems();
                    };
                    SystemSetupFormHeaderController.$inject = ['$scope', '$element', '$sce', '$state', 'navigationUtils'];
                    return SystemSetupFormHeaderController;
                }());
                var systemSetupFormHeaderComponent = {
                    controller: SystemSetupFormHeaderController,
                    templateUrl: 'system-setup-app/shared/components/system-setup-form-header/system-setup-form-header.component.html',
                    bindings: {
                        _description: '@description'
                    }
                };
                angular.module('Fortscale.SystemSetupApp.shared.components')
                    .component('systemSetupFormHeader', systemSetupFormHeaderComponent);
            })(components = shared.components || (shared.components = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var components;
            (function (components) {
                var SystemSetupLoaderController = (function () {
                    function SystemSetupLoaderController() {
                    }
                    SystemSetupLoaderController.$inject = [];
                    return SystemSetupLoaderController;
                }());
                var systemSetupLoaderComponent = {
                    controller: SystemSetupLoaderController,
                    templateUrl: 'system-setup-app/shared/components/system-setup-loader/system-setup-loader.component.html',
                    bindings: {
                        showLoader: '<'
                    }
                };
                angular.module('Fortscale.SystemSetupApp.shared.components')
                    .component('systemSetupLoader', systemSetupLoaderComponent);
            })(components = shared.components || (shared.components = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var components;
            (function (components) {
                var SystemSetupActiveDirectoryConnectorController = (function () {
                    function SystemSetupActiveDirectoryConnectorController($scope, $element, $http, BASE_URL) {
                        this.$scope = $scope;
                        this.$element = $element;
                        this.$http = $http;
                        this.BASE_URL = BASE_URL;
                        this._DCS_MODEL_NAME = 'models_dcs';
                        this._DCS_SINGLE_INPUT_MODEL_NAME = 'dcsSingleInput';
                        this.showPassword = false;
                        this.connectionTestResults = null;
                    }
                    /**
                     * Toggles 'Show Password'
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype.toggleShowPassword = function () {
                        this.showPassword = !this.showPassword;
                    };
                    /**
                     * Adds a connection string to dcs list
                     *
                     * @param {INgModelController} dcsModel
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype.addConnectionString = function (dcsModel) {
                        if (this.dcsSingleInput === '' || this.dcsSingleInput === null || this.dcsSingleInput === undefined) {
                            return;
                        }
                        dcsModel.$viewValue.push(this.dcsSingleInput);
                        dcsModel.$setViewValue(_.cloneDeep(dcsModel.$viewValue));
                        dcsModel.$setDirty();
                        dcsModel.$setTouched();
                        this.dcsSingleInput = '';
                    };
                    /**
                     * Removes connection string from dcs list
                     *
                     * @param {INgModelController} dcsModel
                     * @param {string} connectionString
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype.removeConnectionString = function (dcsModel, connectionString) {
                        var dcsList = _.filter(dcsModel.$viewValue, function (cs) { return cs !== connectionString; });
                        dcsModel.$setViewValue(dcsList);
                        dcsModel.$setDirty();
                        dcsModel.$setTouched();
                    };
                    /**
                     * Resets the connector to its initial state
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype.resetConnector = function () {
                        this.connector = _.cloneDeep(this._connector);
                        _.each(this.formCtrl, function (formCtrlProp, propName) {
                            if (/^models_/.test(propName)) {
                                formCtrlProp.$setPristine();
                                formCtrlProp.$setUntouched();
                            }
                        });
                        this.formCtrl.$setPristine();
                        this.formCtrl.$setUntouched();
                        this._setConnectionValidity();
                    };
                    SystemSetupActiveDirectoryConnectorController.prototype.isRemoveAllowed = function () {
                        return this.numberOfConnectors > 1;
                    };
                    /**
                     * Removes the current connector
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype.removeConnector = function () {
                        if (this.isRemoveAllowed()) {
                            this.formCtrl.$setDirty();
                            this.connector['removed'] = true;
                            this.onRemoveConnector();
                        }
                    };
                    /**
                     * Tests the connector. Sends params to the server and processes the response. If test fails, the form is invalid.
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype.testConnector = function ($event) {
                        var _this = this;
                        if ($event.currentTarget.attributes['disabled'] && $event.currentTarget.attributes['disabled'].value === 'disabled') {
                            return;
                        }
                        // Starts loader
                        this.setLoadingState({ state: true });
                        // Get connector value
                        var value = this.getValue();
                        // Sets encrypted_password param and remove from connector object
                        var params = {};
                        if (value.encryptedPassword) {
                            delete value.encryptedPassword;
                            params.encrypted_password = true;
                        }
                        else {
                            params.encrypted_password = false;
                        }
                        // Perform test and populate connectionTestResults
                        this.$http.post(this.BASE_URL + '/active_directory/test', value, {
                            params: params, transformResponse: function (message) { return message; }
                        })
                            .then(function (res) {
                            if (typeof res.data === 'string') {
                                res.data = JSON.parse(res.data);
                            }
                            if (res.data.authenticationTestResult.result) {
                                _this.connectionTestResults = {
                                    status: 'success',
                                    message: 'Connection test successful.'
                                };
                            }
                            else {
                                _this.connectionTestResults = {
                                    status: 'error',
                                    message: res.data.authenticationTestResult.reason
                                };
                            }
                        })
                            .catch(function (err) {
                            _this.connectionTestResults = {
                                status: 'error',
                                message: err.data
                            };
                        })
                            .finally(function () {
                            // Sets form validity (via connectionTest ngModel)
                            _this._setConnectionValidity();
                            // Turn off loader
                            _this.setLoadingState({ state: false });
                        });
                    };
                    /**
                     * Returns the value of the corrent connector
                     * @returns {IActiveDirectoryConnector}
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype.getValue = function () {
                        var connector = _.cloneDeep(this.connector);
                        // Raise flag encryptedPassword when password has not changed. If new-password and old-password are
                        // the same it would mean that the password was received from the server and therefor is encrypted
                        // (or its empty in which case the field would be invalid)
                        if (this.connector.domainPassword === this._connector.domainPassword) {
                            connector.encryptedPassword = true;
                        }
                        return connector;
                    };
                    /**
                     * Initiates connector watch. Once received connector is cloned and placed on 'connector'.
                     * @private
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype._initConnectorWatch = function () {
                        var _this = this;
                        var unwatch = this.$scope.$watch(function () { return _this._connector; }, function (connector) {
                            if (connector) {
                                _this.connector = _.cloneDeep(connector);
                                unwatch();
                                _this.$scope.$applyAsync(function () {
                                    // Find the form controller
                                    _this._setFormCtrl();
                                    // Remove unwanted controller
                                    _this._removeDcsSingleInputFromForm();
                                    // Add validation to
                                    _this._addDcsModelValidation();
                                    _this._AddConnectionTestValidation();
                                    // Register the 'getValue' function
                                    _this.registerConnector({ getValueFn: _this.getValue.bind(_this) });
                                });
                            }
                        });
                    };
                    /**
                     * Finds the form controller and places it on the instance
                     * @private
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype._setFormCtrl = function () {
                        this.formCtrl = this.$element.find('[ng-form]').controller('form');
                    };
                    /**
                     * This ngModel is used internally and therefor should not be registered in the form
                     * @private
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype._removeDcsSingleInputFromForm = function () {
                        this.formCtrl.$removeControl(this.formCtrl[this._DCS_SINGLE_INPUT_MODEL_NAME]);
                    };
                    /**
                     * Adds DCS validation (must be a list and have length)
                     * @private
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype._addDcsModelValidation = function () {
                        var _this = this;
                        var dcsNgModel = (this.formCtrl[this._DCS_MODEL_NAME]);
                        dcsNgModel.$validators['required'] = function (modelValue, viewValue) {
                            return _this._isDcsModelRequiredValid(viewValue);
                        };
                        dcsNgModel.$setValidity('required', this._isDcsModelRequiredValid(dcsNgModel.$viewValue));
                    };
                    SystemSetupActiveDirectoryConnectorController.prototype._isDcsModelRequiredValid = function (viewValue) {
                        return !!(viewValue && viewValue.length);
                        ;
                    };
                    /**
                     * Checks id the form is valid with relations to the test.
                     * Heuristics: If original connector and form connector are equal the its valid. If connectionTestResults is success then its valid.
                     * @returns {boolean}
                     * @private
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype._isFormTestedValid = function () {
                        var formConnectionTested = !!(this.connectionTestResults && this.connectionTestResults.status === 'success');
                        return this.connectorsEqual || formConnectionTested;
                    };
                    /**
                     * Sets connection validity to the connectionTest ngModel (which will affect form validity)
                     * @private
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype._setConnectionValidity = function () {
                        var connectionTestNgModel = (this.formCtrl['connectionTest']);
                        connectionTestNgModel.$setValidity('connectionTest', this._isFormTestedValid());
                    };
                    SystemSetupActiveDirectoryConnectorController.prototype._setConnectorsAreEqual = function () {
                        this.connectorsEqual = _.isEqual(this.connector, this._connector);
                    };
                    /**
                     * Adds watcher on connector parameters. When changed, _setConnectionValidity is invoked.
                     * @private
                     */
                    SystemSetupActiveDirectoryConnectorController.prototype._AddConnectionTestValidation = function () {
                        var _this = this;
                        this.$scope.$watchGroup([
                            function () { return _this.connector.dcs; },
                            function () { return _this.connector.domainBaseSearch; },
                            function () { return _this.connector.domainPassword; },
                            function () { return _this.connector.domainUser; },
                        ], function (newVal, oldVal) {
                            if (newVal !== oldVal) {
                                // Remove last test result if change has occurred
                                if (_this.connectionTestResults) {
                                    _this.connectionTestResults = null;
                                }
                            }
                            _this._setConnectorsAreEqual();
                            _this._setConnectionValidity();
                        });
                    };
                    SystemSetupActiveDirectoryConnectorController.prototype.$onInit = function () {
                        this._initConnectorWatch();
                    };
                    SystemSetupActiveDirectoryConnectorController.$inject = ['$scope', '$element', '$http', 'BASE_URL'];
                    return SystemSetupActiveDirectoryConnectorController;
                }());
                var systemSetupActiveDirectoryConnectorComponent = {
                    controller: SystemSetupActiveDirectoryConnectorController,
                    templateUrl: 'system-setup-app/shared/components/system-setup-active-directory-connector/system-setup-active-directory-connector.component.html',
                    bindings: {
                        _connector: '<connector',
                        connectorIndex: '<',
                        numberOfConnectors: '<',
                        onRemoveConnector: '&',
                        registerConnector: '&',
                        setLoadingState: '&'
                    },
                    require: {
                        parentFormCtrl: '^form'
                    }
                };
                angular.module('Fortscale.SystemSetupApp.shared.components')
                    .component('systemSetupActiveDirectoryConnector', systemSetupActiveDirectoryConnectorComponent);
            })(components = shared.components || (shared.components = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var components;
            (function (components) {
                var SystemSetupActiveDirectoryStepViewerController = (function () {
                    function SystemSetupActiveDirectoryStepViewerController($scope, $element, $http, $timeout) {
                        this.$scope = $scope;
                        this.$element = $element;
                        this.$http = $http;
                        this.$timeout = $timeout;
                    }
                    SystemSetupActiveDirectoryStepViewerController.prototype.$onInit = function () {
                        var _this = this;
                        this.fetchCompleted = false;
                        this.etlCompleted = false;
                        this.$scope.$watch(function () { return _this.stepData.etlCompletedPercentage; }, function () {
                            _this.etlCompleted = _this.stepData.etlCompletedPercentage > 0;
                            console.log("New count: " + _this.stepData.objectsCount);
                        });
                        this.$scope.$watch(function () { return _this.stepData.fetchCompletedPercentage; }, function () {
                            _this.fetchCompleted = _this.stepData.fetchCompletedPercentage > 0;
                        });
                    };
                    SystemSetupActiveDirectoryStepViewerController.prototype.isFetchInProgress = function () {
                        return this.stepData.isRunning && this.stepData.fetchCompletedPercentage < 100;
                    };
                    SystemSetupActiveDirectoryStepViewerController.prototype.isETLInProgress = function () {
                        return this.stepData.isRunning && this.stepData.fetchCompletedPercentage == 100 && this.stepData.etlCompletedPercentage < 100;
                    };
                    /**
                     * Formats an int representing number of seconds into a time string, hh:mm:ss
                     * @param diffSeconds
                     */
                    SystemSetupActiveDirectoryStepViewerController.prototype.prettyElapsedTime = function () {
                        if (_.isNil(this.elapsedTimeSeconds)) {
                            return "00:00:00";
                        }
                        else {
                            var duration = moment.duration(Number(this.elapsedTimeSeconds * 1000));
                            var days = duration.days();
                            //should never happened in our product - all our sessions are defined as less
                            // than 24 hours
                            if (days && days > 0) {
                                return days + "d";
                            }
                            else {
                                var hours = duration.hours(), minutes = duration.minutes(), seconds = duration.seconds();
                                if (hours || minutes || seconds) {
                                    return this.padLeft(hours, 2, "0") + ":" +
                                        this.padLeft(minutes, 2, "0") + ":" +
                                        this.padLeft(seconds, 2, "0");
                                }
                                else {
                                    //if session duration is less than 1 sec - will shown as 0 second
                                    return "00:00:00";
                                }
                            }
                        }
                    };
                    SystemSetupActiveDirectoryStepViewerController.prototype.padLeft = function (str, length, padCharacter) {
                        str = String(str);
                        var padLength = length - str.length;
                        if (padLength <= 0) {
                            return str;
                        }
                        var pad = [];
                        while (pad.length < padLength) {
                            pad.push(padCharacter);
                        }
                        return pad.join("") + str;
                    };
                    SystemSetupActiveDirectoryStepViewerController.$inject = ['$scope', '$element', '$http', '$timeout'];
                    return SystemSetupActiveDirectoryStepViewerController;
                }());
                var systemSetupActiveDirectoryStepViewerComponent = {
                    controller: SystemSetupActiveDirectoryStepViewerController,
                    templateUrl: 'system-setup-app/shared/components/system-setup-active-directory-step-viewer/system-setup-active-directory-step-viewer.component.html',
                    bindings: {
                        stepData: '<',
                        title: '@',
                        objectTitle: '@',
                        elapsedTimeSeconds: '<'
                    }
                };
                angular.module('Fortscale.SystemSetupApp.shared.components')
                    .component('systemSetupActiveDirectoryStepViewer', systemSetupActiveDirectoryStepViewerComponent);
            })(components = shared.components || (shared.components = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var components;
            (function (components) {
                var SystemSetupTagRuleController = (function () {
                    function SystemSetupTagRuleController() {
                    }
                    /**
                     * Invokes the delegate to delete a rule from tag
                     */
                    SystemSetupTagRuleController.prototype.deleteRule = function () {
                        this.onRemoveRule({ rule: this.rule, tag: this.tag });
                    };
                    SystemSetupTagRuleController.$inject = [];
                    return SystemSetupTagRuleController;
                }());
                var systemSetupTagRule = {
                    controller: SystemSetupTagRuleController,
                    templateUrl: 'system-setup-app/shared/components/system-setup-tag-rule/system-setup-tag-rule.component.html',
                    bindings: {
                        rule: '<',
                        tag: '<?',
                        onRemoveRule: '&'
                    }
                };
                angular.module('Fortscale.SystemSetupApp.shared.components')
                    .component('systemSetupTagRule', systemSetupTagRule);
            })(components = shared.components || (shared.components = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var layouts;
        (function (layouts) {
            var tagsSetup;
            (function (tagsSetup) {
                var SystemSetupTagsRulesPopupController = (function () {
                    function SystemSetupTagsRulesPopupController($element, $timeout, $scope) {
                        this.$element = $element;
                        this.$timeout = $timeout;
                        this.$scope = $scope;
                    }
                    /**
                     * Adds rules to tag
                     */
                    SystemSetupTagsRulesPopupController.prototype.addRules = function () {
                        var _this = this;
                        if (this.tagRulesForm.$invalid || this.tagRulesForm.$pristine) {
                            return;
                        }
                        this.rules = [];
                        _.each(this.models.commonNames, function (userRule) {
                            _this.rules.push(userRule);
                        });
                        if (this.models.regexp) {
                            this.rules = this.rules.concat(this.models.regexp.split(";"));
                        }
                        this.onAddRules({ rules: this.rules });
                    };
                    SystemSetupTagsRulesPopupController.prototype.removeRuleFromTag = function (rule) {
                        _.remove(this.models.commonNames, function (currentItemDn) {
                            return rule === currentItemDn;
                        });
                    };
                    SystemSetupTagsRulesPopupController.prototype.$onInit = function () {
                        var _this = this;
                        this.models = {
                            commonNames: [],
                            regexp: ""
                        };
                        this.$timeout(function () {
                            _this.$element.addClass('enter-active');
                        }, 50);
                    };
                    SystemSetupTagsRulesPopupController.$inject = ['$element', '$timeout', '$scope'];
                    return SystemSetupTagsRulesPopupController;
                }());
                /**
                 * Popup component. Used to enter tag rules (OU, CN, and Regex)
                 * @type {{controller: Fortscale.SystemSetupApp.layouts.tagsSetup.SystemSetupTagsRulesPopupController, templateUrl: string, bindings: {}}}
                 */
                var systemSetupTagsRulesPopup = {
                    controller: SystemSetupTagsRulesPopupController,
                    templateUrl: 'system-setup-app/layouts/tags-setup/components/system-setup-tags-rules-popup/system-setup-tags-rules-popup.component.html',
                    bindings: {
                        onAddRules: '&',
                        onCancel: '&'
                    }
                };
                /**
                 * This directive adds validation and formatting to the Organization field
                 * @type {string|function(IDistinguishedNameUtilsService): ng.IDirective[]}
                 */
                var adValidateOrganizationsDirective = [
                    'distinguishedNameUtils',
                    function (distinguishedNameUtils) {
                        var linkFn = function (scope, instanceElement, instanceAttributes, controller) {
                            controller[0].$validators['ad-validate-organizations'] =
                                function (modelValue, viewValue) {
                                    if (!modelValue && !viewValue) {
                                        return true;
                                    }
                                    return distinguishedNameUtils.validateOrganizations(modelValue);
                                };
                            controller[0].$formatters.push(function (value) {
                                if (!value) {
                                    return;
                                }
                                return distinguishedNameUtils.trimDistinguishedNames(value);
                            });
                        };
                        return {
                            require: ['ngModel'],
                            link: linkFn
                        };
                    }];
                /**
                 * This directive adds validation and formatting to the Organization field
                 * @type {string|function(IDistinguishedNameUtilsService): ng.IDirective[]}
                 */
                var adValidateCommonNamesDirective = [
                    'distinguishedNameUtils',
                    function (distinguishedNameUtils) {
                        var linkFn = function (scope, instanceElement, instanceAttributes, controller) {
                            controller[0].$validators['ad-validate-common-names'] =
                                function (modelValue, viewValue) {
                                    if (!modelValue && !viewValue) {
                                        return true;
                                    }
                                    return distinguishedNameUtils.validateCommonNames(modelValue);
                                };
                            controller[0].$formatters.push(function (value) {
                                if (!value) {
                                    return;
                                }
                                return distinguishedNameUtils.trimDistinguishedNames(value);
                            });
                        };
                        return {
                            require: ['ngModel'],
                            link: linkFn
                        };
                    }];
                angular.module('Fortscale.SystemSetupApp.layouts')
                    .component('systemSetupTagsRulesPopup', systemSetupTagsRulesPopup)
                    .directive('adValidateOrganizations', adValidateOrganizationsDirective)
                    .directive('adValidateCommonNames', adValidateCommonNamesDirective);
            })(tagsSetup = layouts.tagsSetup || (layouts.tagsSetup = {}));
        })(layouts = SystemSetupApp.layouts || (SystemSetupApp.layouts = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var layouts;
        (function (layouts) {
            var tagsSetup;
            (function (tagsSetup) {
                var KEY_ARROW_UP = 38;
                var KEY_ARROW_DOWN = 40;
                var NO_CURRENT_INDEX = -1;
                var RowItem = (function () {
                    function RowItem() {
                    }
                    return RowItem;
                }());
                var GroupsOuSearchComponentController = (function () {
                    function GroupsOuSearchComponentController($scope, $timeout, BASE_URL, $http) {
                        this.$scope = $scope;
                        this.$timeout = $timeout;
                        this.BASE_URL = BASE_URL;
                        this.$http = $http;
                        this._SEARCH_GROUPS_OU_URL = this.BASE_URL + '/tags/search';
                        this.attributeName = "searchValue";
                        this.ousAndGroups = [];
                        this.userRules = []; // contain the distinguish names
                        //If the we hover a user using mouse or arrows the index should be 0 or greater.
                        //If the user is on the search text input the index should be -1.
                        this.currentHoverIndex = NO_CURRENT_INDEX;
                    }
                    /**
                     * Set the current index for selected user
                     */
                    GroupsOuSearchComponentController.prototype.updateIndex = function (newIndex) {
                        this.currentHoverIndex = newIndex;
                    };
                    GroupsOuSearchComponentController.prototype.onSearchActive = function () {
                        var searchActive = this.searchText ? this.searchText.length > 0 : false;
                        if (this.timer) {
                            this.$timeout.cancel(this.timer);
                        }
                        if (searchActive) {
                            this.timer = this.$timeout(this._loadOuAndGroups.bind(this), 500);
                        }
                        else {
                            this._safeHide();
                        }
                    };
                    /**
                     * Get the ou/groups, clear the timer, and display / hide the list off users
                     * @private
                     */
                    GroupsOuSearchComponentController.prototype._loadOuAndGroups = function () {
                        var ctrl = this;
                        ctrl.timer = null;
                        var query = {
                            containedText: this.searchText
                        };
                        this.$http.get(this._SEARCH_GROUPS_OU_URL, { params: query })
                            .then(function (res) {
                            if (res.data) {
                                var ousList = res.data["ous"];
                                var groupsList = res.data["groups"];
                                ctrl.ousAndGroups = [];
                                _.each(ousList, function (ou) {
                                    ctrl.ousAndGroups.push({
                                        id: ou.id,
                                        name: ou.ou,
                                        distinguishedName: ou.distinguishedName,
                                        type: "OU"
                                    });
                                });
                                _.each(groupsList, function (group) {
                                    var newItem = {
                                        id: group.id,
                                        name: group.name,
                                        distinguishedName: group.distinguishedName,
                                        type: "Group"
                                    };
                                    ctrl.ousAndGroups.push(newItem);
                                });
                                ctrl._safeHide();
                            }
                        });
                    };
                    GroupsOuSearchComponentController.prototype.rowClickedEvent = function (item) {
                        if (this.isExistsItemSelected(item.distinguishedName)) {
                            //If exists - remove from selected list
                            _.remove(this.userRules, function (currentItemDn) {
                                return item.distinguishedName === currentItemDn;
                            });
                        }
                        else {
                            //If not exists - add to selected list
                            this.userRules.push(item.distinguishedName);
                        }
                    };
                    GroupsOuSearchComponentController.prototype.isExistsItemSelected = function (dn) {
                        var itemIndex = _.findIndex(this.userRules, function (currentItemDn) {
                            return dn === currentItemDn;
                        });
                        return itemIndex > -1;
                    };
                    /**
                     * Return true if the tooltip displayed
                     * @returns {boolean|string}
                     * @private
                     */
                    GroupsOuSearchComponentController.prototype._isVisible = function () {
                        true;
                        //return this._tooltip!=null && this._tooltip.visible;
                    };
                    GroupsOuSearchComponentController.prototype._safeHide = function () {
                        if (this._isVisible()) {
                        }
                        ;
                        this.currentHoverIndex = NO_CURRENT_INDEX;
                    };
                    GroupsOuSearchComponentController.prototype.deleteSearchText = function () {
                        this.searchText = null;
                        this.currentHoverIndex = NO_CURRENT_INDEX;
                        this.ousAndGroups = [];
                        this.applyFilter();
                    };
                    GroupsOuSearchComponentController.prototype.submitWithKeypress = function (e) {
                        if (e.keyCode === 13) {
                            //Key press happens before angular populate the value into the model
                            var newValue = e.target.value;
                            this.applyFilter(newValue);
                        }
                        else if ((e.keyCode === KEY_ARROW_UP || e.keyCode === KEY_ARROW_DOWN) && this.ousAndGroups) {
                            if (e.keyCode === KEY_ARROW_UP) {
                                this.currentHoverIndex > 0 ? this.currentHoverIndex-- : this.currentHoverIndex = NO_CURRENT_INDEX;
                            }
                            else if (e.keyCode === KEY_ARROW_DOWN) {
                                this.currentHoverIndex < this.ousAndGroups.length - 1 ? this.currentHoverIndex++ : this.currentHoverIndex = this.ousAndGroups.length - 1;
                            }
                            this.stopEvent(e);
                        }
                        else if (e.keyCode === 27) {
                            this._safeHide();
                        }
                    };
                    /**
                     * We need to prevent input curesur to move when arrow up or down. Submit with key press handle the actual event.
                     * stop event should prevent keypress
                     * @param e
                     */
                    GroupsOuSearchComponentController.prototype.stopEvent = function (e) {
                        if (e.keyCode === KEY_ARROW_UP) {
                            e.stopPropagation();
                            e.preventDefault();
                        }
                        else if (e.keyCode === KEY_ARROW_DOWN) {
                            e.stopPropagation();
                            e.preventDefault();
                        }
                    };
                    //Close the popup and reset the search text
                    GroupsOuSearchComponentController.prototype.closePopup = function () {
                        this.searchText = null;
                        this._safeHide();
                    };
                    GroupsOuSearchComponentController.prototype.$onInit = function () {
                    };
                    //Apply filter actually affect the state
                    GroupsOuSearchComponentController.prototype.applyFilter = function (differentSeachText) {
                        this._safeHide();
                        if (this.currentHoverIndex > NO_CURRENT_INDEX) {
                        }
                        else {
                        }
                    };
                    /**
                     * Watch action function . Set the value for the searchTaxt from outside
                     *
                     * @param {string|number} value
                     */
                    GroupsOuSearchComponentController.prototype._stateWatchActionFn = function (value) {
                        this._safeHide();
                        this.searchText = value;
                    };
                    GroupsOuSearchComponentController.$inject = ['$scope', '$timeout', 'BASE_URL', '$http'];
                    return GroupsOuSearchComponentController;
                }());
                var GroupsOuSearchComponent = {
                    controller: GroupsOuSearchComponentController,
                    controllerAs: '$ctrl',
                    templateUrl: 'system-setup-app/layouts/tags-setup/components/system-setup-tag-search/system-setup-tag-search.template.html',
                    bindings: {
                        userRules: '<'
                    }
                };
                angular.module('Fortscale.SystemSetupApp.layouts')
                    .component('groupsOuSearch', GroupsOuSearchComponent);
            })(tagsSetup = layouts.tagsSetup || (layouts.tagsSetup = {}));
        })(layouts = SystemSetupApp.layouts || (SystemSetupApp.layouts = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var shared;
        (function (shared) {
            var components;
            (function (components) {
                var SystemSetupLogRepositoryConnectorController = (function () {
                    function SystemSetupLogRepositoryConnectorController($scope, $element, $http, BASE_URL, LOG_REPOSITORY_TYPES) {
                        this.$scope = $scope;
                        this.$element = $element;
                        this.$http = $http;
                        this.BASE_URL = BASE_URL;
                        this.LOG_REPOSITORY_TYPES = LOG_REPOSITORY_TYPES;
                        this.showPassword = false;
                        this.connectionTestResults = null;
                        this.logRepositoryTypesState = (_a = {},
                            _a[this.LOG_REPOSITORY_TYPES[0].value] = {
                                name: this.LOG_REPOSITORY_TYPES[0].name,
                                fields: {
                                    id: {
                                        required: true
                                    },
                                    password: {
                                        name: 'Password',
                                        required: true
                                    },
                                    port: {
                                        defaultValue: 8089,
                                        required: true
                                    },
                                    user: {
                                        defaultValue: 'admin',
                                        hint: 'Enter user name e.g. user@company.com',
                                        required: true
                                    },
                                    host: {
                                        required: true
                                    }
                                }
                            },
                            _a[this.LOG_REPOSITORY_TYPES[1].value] = {
                                name: this.LOG_REPOSITORY_TYPES[1].name,
                                fields: {
                                    id: {
                                        required: true
                                    },
                                    host: {
                                        required: true
                                    },
                                    password: {
                                        name: 'Security Token',
                                        hint: 'Copy key from:  QRadar Admin > Authorized services > authentication token',
                                        required: true
                                    },
                                    port: {
                                        defaultValue: 3389,
                                        required: false
                                    },
                                    user: {
                                        defaultValue: null,
                                        required: true
                                    }
                                }
                            },
                            _a
                        );
                        var _a;
                    }
                    /**
                     * Toggles 'Show Password'
                     */
                    SystemSetupLogRepositoryConnectorController.prototype.toggleShowPassword = function () {
                        this.showPassword = !this.showPassword;
                    };
                    /**
                     * Resets the connector to its initial state
                     */
                    SystemSetupLogRepositoryConnectorController.prototype.resetConnector = function () {
                        this.connector = _.cloneDeep(this._connector);
                        _.each(this.formCtrl, function (formCtrlProp, propName) {
                            if (/^models_/.test(propName)) {
                                formCtrlProp.$setPristine();
                                formCtrlProp.$setUntouched();
                            }
                        });
                        this.formCtrl.$setPristine();
                        this.formCtrl.$setUntouched();
                        this._setConnectionValidity();
                    };
                    /**
                     * Removes the current connector
                     */
                    SystemSetupLogRepositoryConnectorController.prototype.removeConnector = function () {
                        this.formCtrl.$setDirty();
                        this.connector['removed'] = true;
                        this.onRemoveConnector();
                    };
                    SystemSetupLogRepositoryConnectorController.prototype.getConnectorDefinition = function () {
                        return this.logRepositoryTypesState[this.connector.fetchSourceType];
                    };
                    /**
                     * Tests the connector. Sends params to the server and processes the response. If test fails, the form is invalid.
                     */
                    SystemSetupLogRepositoryConnectorController.prototype.testConnector = function ($event) {
                        var _this = this;
                        if ($event.currentTarget.attributes['disabled'] &&
                            $event.currentTarget.attributes['disabled'].value === 'disabled') {
                            return;
                        }
                        // Starts loader
                        this.setLoadingState({ state: true });
                        // Get connector value
                        var value = this.getValue();
                        // Sets encrypted_password param and remove from connector object
                        var params = {};
                        if (value.encryptedPassword) {
                            delete value.encryptedPassword;
                            params.encrypted_password = true;
                        }
                        else {
                            params.encrypted_password = false;
                        }
                        // Perform test and populate connectionTestResults
                        this.$http.post(this.BASE_URL + '/log_repository/test', value, {
                            params: params, transformResponse: function (message) { return message; }
                        })
                            .then(function () {
                            _this.connectionTestResults = {
                                status: 'success',
                                message: 'Connection test successful.'
                            };
                        })
                            .catch(function (err) {
                            _this.connectionTestResults = {
                                status: 'error',
                                message: err.data
                            };
                        })
                            .finally(function () {
                            // Sets form validity (via connectionTest ngModel)
                            _this._setConnectionValidity();
                            // Turn off loader
                            _this.setLoadingState({ state: false });
                        });
                    };
                    /**
                     * Returns the value of the corrent connector
                     * @returns {ILogRepositoryConnector}
                     */
                    SystemSetupLogRepositoryConnectorController.prototype.getValue = function () {
                        var connector = _.cloneDeep(this.connector);
                        // Raise flag encryptedPassword when password has not changed. If new-password and old-password are
                        // the same it would mean that the password was received from the server and therefor is encrypted
                        // (or its empty in which case the field would be invalid)
                        if (this.connector.password === this._connector.password) {
                            connector.encryptedPassword = true;
                        }
                        return connector;
                    };
                    /**
                     * When switching between types, set defaults.
                     */
                    SystemSetupLogRepositoryConnectorController.prototype.changeRepositoryType = function () {
                        this._setExplicitDefaultValues(true);
                    };
                    /**
                     * Sets default values based on the type of repository
                     * @private
                     */
                    SystemSetupLogRepositoryConnectorController.prototype._setExplicitDefaultValues = function (enforceDefaults) {
                        var _this = this;
                        if (enforceDefaults === void 0) { enforceDefaults = false; }
                        var ctrl = this;
                        /**
                         * Set and explicit field value
                         * @param type
                         * @param fieldName
                         */
                        function setExplicitField(type, fieldName) {
                            // If no value, or is asked to enforce defaults
                            if (!ctrl.connector.port || enforceDefaults) {
                                // Set the value on the model
                                ctrl.connector[fieldName] = ctrl.logRepositoryTypesState[type].fields[fieldName].defaultValue;
                                // Determine if dirty or pristine
                                if (ctrl.connector[fieldName] !== ctrl._connector[fieldName]) {
                                    ctrl.formCtrl['models_' + fieldName].$setDirty();
                                }
                                else {
                                    ctrl.formCtrl['models_' + fieldName].$setPristine();
                                }
                            }
                        }
                        // Iterate through fields of repository type, and for each that has a 'defaultValue', invoke setExplicitField
                        _.each(this.logRepositoryTypesState[this.connector.fetchSourceType].fields, function (fieldObj, fieldName) {
                            if (fieldObj.hasOwnProperty('defaultValue')) {
                                setExplicitField(_this.connector.fetchSourceType, fieldName);
                            }
                        });
                    };
                    /**
                     * Initiates connector watch. Once received connector is cloned and placed on 'connector'.
                     * @private
                     */
                    SystemSetupLogRepositoryConnectorController.prototype._initConnectorWatch = function () {
                        var _this = this;
                        var unwatch = this.$scope.$watch(function () { return _this._connector; }, function (connector) {
                            if (connector) {
                                _this.connector = _.cloneDeep(connector);
                                unwatch();
                                _this.$scope.$applyAsync(function () {
                                    // Find the form controller
                                    _this._setFormCtrl();
                                    _this._AddConnectionTestValidation();
                                    // Register the 'getValue' function
                                    _this.registerConnector({ getValueFn: _this.getValue.bind(_this) });
                                    _this._setExplicitDefaultValues();
                                });
                            }
                        });
                    };
                    /**
                     * Finds the form controller and places it on the instance
                     * @private
                     */
                    SystemSetupLogRepositoryConnectorController.prototype._setFormCtrl = function () {
                        this.formCtrl = this.$element.find('[ng-form]').controller('form');
                    };
                    /**
                     * Checks id the form is valid with relations to the test.
                     * Heuristics: If original connector and form connector are equal the its valid. If connectionTestResults is success then its valid.
                     * @returns {boolean}
                     * @private
                     */
                    SystemSetupLogRepositoryConnectorController.prototype._isFormTestedValid = function () {
                        var formConnectionTested = !!(this.connectionTestResults &&
                            this.connectionTestResults.status === 'success');
                        return this.connectorsEqual || formConnectionTested;
                    };
                    /**
                     * Sets connection validity to the connectionTest ngModel (which will affect form validity)
                     * @private
                     */
                    SystemSetupLogRepositoryConnectorController.prototype._setConnectionValidity = function () {
                        var connectionTestNgModel = (this.formCtrl['connectionTest']);
                        connectionTestNgModel.$setValidity('connectionTest', this._isFormTestedValid());
                    };
                    SystemSetupLogRepositoryConnectorController.prototype._setConnectorsAreEqual = function () {
                        this.connectorsEqual = _.isEqual(this.connector, this._connector);
                    };
                    /**
                     * Adds watcher on connector parameters. When changed, _setConnectionValidity is invoked.
                     * @private
                     */
                    SystemSetupLogRepositoryConnectorController.prototype._AddConnectionTestValidation = function () {
                        var _this = this;
                        this.$scope.$watchGroup([
                            function () { return _this.connector.alias; },
                            function () { return _this.connector.host; },
                            function () { return _this.connector.password; },
                            function () { return _this.connector.port; },
                            function () { return _this.connector.fetchSourceType; },
                            function () { return _this.connector.user; },
                        ], function (newVal, oldVal) {
                            if (newVal !== oldVal) {
                                // Remove last test result if change has occurred
                                if (_this.connectionTestResults) {
                                    _this.connectionTestResults = null;
                                }
                            }
                            _this._setConnectorsAreEqual();
                            _this._setConnectionValidity();
                        });
                    };
                    SystemSetupLogRepositoryConnectorController.prototype.$onInit = function () {
                        this._initConnectorWatch();
                    };
                    SystemSetupLogRepositoryConnectorController.$inject = ['$scope', '$element', '$http', 'BASE_URL', 'LOG_REPOSITORY_TYPES'];
                    return SystemSetupLogRepositoryConnectorController;
                }());
                var systemSetupLogRepositoryConnectorComponent = {
                    controller: SystemSetupLogRepositoryConnectorController,
                    templateUrl: 'system-setup-app/shared/components/system-setup-log-repository-connector/system-setup-log-repository-connector.component.html',
                    bindings: {
                        _connector: '<connector',
                        connectorIndex: '<',
                        onRemoveConnector: '&',
                        registerConnector: '&',
                        setLoadingState: '&'
                    },
                    require: {
                        parentFormCtrl: '^form'
                    }
                };
                angular.module('Fortscale.SystemSetupApp.shared.components')
                    .component('systemSetupLogRepositoryConnector', systemSetupLogRepositoryConnectorComponent);
            })(components = shared.components || (shared.components = {}));
        })(shared = SystemSetupApp.shared || (SystemSetupApp.shared = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var layouts;
        (function (layouts) {
            var systemSetup;
            (function (systemSetup) {
                var SystemSetupController = (function () {
                    function SystemSetupController($scope, $element, $state, navigationUtils) {
                        this.$scope = $scope;
                        this.$element = $element;
                        this.$state = $state;
                        this.navigationUtils = navigationUtils;
                        this._MAIN_CONTENT_CONTAINER_ELEMENT_SELECTOR = '.main-content-container';
                        this._ANIMATE_BACK_CLASS_NAME = 'animate-back';
                        this._STATIC_NAV_ITEM_STATE_NAME = 'systemSetup.setupSummary';
                        this._formViewElement = this.$element.find(this._MAIN_CONTENT_CONTAINER_ELEMENT_SELECTOR);
                    }
                    SystemSetupController.prototype.continueToNexStep = function () {
                        var nextState = this.navigationUtils.getNextState(this.$state.current.name);
                        var nextNavItem = this.navigationUtils.getNavItemByState(nextState);
                        this.transition(nextNavItem ? nextNavItem : nextState);
                    };
                    /**
                     * Handles transition (animation and request) between forms
                     *
                     * @param navItem
                     */
                    SystemSetupController.prototype.transition = function (navItem) {
                        // If navItem is string, then always move forward, and go to the state.
                        // This is in case an explicit state movement is desired.
                        if (_.isString(navItem)) {
                            this._formViewElement.removeClass(this._ANIMATE_BACK_CLASS_NAME);
                            this.$state.go(navItem);
                            return;
                        }
                        // Do nothing if current and next states are equal
                        if (navItem.state === this.$state.current.name) {
                            return;
                        }
                        // Determine the direction of the animation.
                        // If nav item is the static nav item (summary) then animation is backwards
                        if (navItem.state === this._STATIC_NAV_ITEM_STATE_NAME) {
                            this._formViewElement.removeClass(this._ANIMATE_BACK_CLASS_NAME);
                        }
                        else if (this.$state.current.name === this._STATIC_NAV_ITEM_STATE_NAME) {
                            this._formViewElement.addClass(this._ANIMATE_BACK_CLASS_NAME);
                        }
                        else if (!this.navigationUtils.isNavigationForward(this.$state.current.name, navItem.state)) {
                            this._formViewElement.addClass(this._ANIMATE_BACK_CLASS_NAME);
                        }
                        else {
                            this._formViewElement.removeClass(this._ANIMATE_BACK_CLASS_NAME);
                        }
                        // Transition to requested state
                        this.$state.go(navItem.state);
                    };
                    SystemSetupController.$inject = ['$scope', '$element', '$state', 'navigationUtils'];
                    return SystemSetupController;
                }());
                angular.module('Fortscale.SystemSetupApp.layouts')
                    .controller('systemSetupController', SystemSetupController);
            })(systemSetup = layouts.systemSetup || (layouts.systemSetup = {}));
        })(layouts = SystemSetupApp.layouts || (SystemSetupApp.layouts = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var layouts;
        (function (layouts) {
            var activeDirectorySetup;
            (function (activeDirectorySetup) {
                //List of Datasources (For each data source we nned to do ETL+FETCH
                var USERS_STEP = "USER";
                var GROUPS_STEP = "GROUP";
                var OUS_STEP = "OU";
                var DEVICES_STEP = "COMPUTER";
                /*
                    Represent AdStep (data source)
                 */
                var IAdStep = (function () {
                    function IAdStep() {
                        this.etlCompletedPercentage = 0;
                        this.fetchCompletedPercentage = 0;
                        this.objectsCount = 0;
                        this.isRunning = false;
                        this.lastSuccessfullExecution = 0;
                        this.success = false;
                    }
                    return IAdStep;
                }());
                activeDirectorySetup.IAdStep = IAdStep;
                var ActiveDirectorySetupController = (function () {
                    function ActiveDirectorySetupController($scope, $element, $http, BASE_URL, $timeout, $log, toastrService, BASE_WEBSOCKET_URL, $interval) {
                        this.$scope = $scope;
                        this.$element = $element;
                        this.$http = $http;
                        this.BASE_URL = BASE_URL;
                        this.$timeout = $timeout;
                        this.$log = $log;
                        this.toastrService = toastrService;
                        this.BASE_WEBSOCKET_URL = BASE_WEBSOCKET_URL;
                        this.$interval = $interval;
                        this._END_POINT_PATH = '/active_directory'; // The web socket API
                        this.isLoading = false;
                        this.stompClient = null;
                        //End of attributes for last execution
                        //Old all the steps (data sources) and their status
                        this.adStepsMap = {};
                        this._getFormElementAndController();
                        this._initDataFetch();
                        this._featchEtlADStatus();
                        this._openWebSocketFetchEtlAD();
                        document.title = 'Active Directory Configuration';
                        this._initAdStepDataCounters();
                        this.$scope.$on('$destroy', this._clearElapsedTimeInterval);
                    }
                    /**
                     * Returns a list of derived IActiveDirectoryConnector by using registered getValueFns.
                     * @returns {IActiveDirectoryConnector[]}
                     * @private
                     */
                    ActiveDirectorySetupController.prototype._getValues = function () {
                        // Get a list of values by using registered getValueFns
                        var domainControllers = _.map(this.getValueFns, function (valueFn) {
                            var value = valueFn();
                            return {
                                dcs: value.dcs,
                                domainBaseSearch: value.domainBaseSearch,
                                domainUser: value.domainUser,
                                domainPassword: value.domainPassword,
                                removed: value.removed,
                                encryptedPassword: !!value.encryptedPassword
                            };
                        });
                        // Filter out all connectors that have been destroyed, and clean the remaining.
                        domainControllers = _.filter(domainControllers, function (domainController) { return !domainController.removed; });
                        _.each(domainControllers, function (domainController) { return delete domainController.removed; });
                        return domainControllers;
                    };
                    /**
                     * Submits available connectors.
                     * @returns {IPromise<TResult>}
                     * @private
                     */
                    ActiveDirectorySetupController.prototype._submitForm = function () {
                        var _this = this;
                        this.isLoading = true;
                        var domainControllers = this._getValues();
                        return this.$http.post(this.BASE_URL + this._END_POINT_PATH, domainControllers)
                            .then(function (res) {
                            _this.formElementController.$setPristine();
                            return res;
                        })
                            .catch(function (err) {
                            _this.$log.error(err);
                            _this.toastrService.error('There was an unknown server error. Please try again.', 'Active Directory Update Error');
                        })
                            .finally(function () {
                            _this.$timeout(function () {
                                _this.isLoading = false;
                            }, 500);
                        });
                    };
                    /**
                     * Transitions to next step
                     * @private
                     */
                    ActiveDirectorySetupController.prototype._continueToNextStep = function () {
                        this.$scope['systemSetupCtrl'].continueToNexStep();
                    };
                    /**
                     * Removes a connector from the list.
                     *
                     * @param connector
                     */
                    ActiveDirectorySetupController.prototype.removeConnector = function (connector) {
                        this.activeDirectoryConnectors =
                            _.filter(this.activeDirectoryConnectors, function (listConnector) {
                                return listConnector !== connector;
                            });
                    };
                    /**
                     * This method open web socket connection to endpoint "/active_directory/ad_fetch" and listen to server messages on
                     * '/wizard/ad-fetch-response' topic.\
                     *
                     * When component's scope (this this') is destroyed, the connection disconected, so we will not have
                     * to many open connections
                     *
                     */
                    ActiveDirectorySetupController.prototype._openWebSocketFetchEtlAD = function () {
                        //let socket:any = new SockJS('/fortscale-webapp/active_directory/ad_fetch',null, { debug: true , protocols_whitelist:['xhr-polling']});
                        var socket = new SockJS(this.BASE_WEBSOCKET_URL + '/active_directory/ad_fetch_etl');
                        this.stompClient = Stomp.over(socket);
                        var stompClientTemp = this.stompClient;
                        var ctrl = this;
                        this.stompClient.connect({}, function (frame) {
                            // setConnected(true);
                            console.log('Connected: ' + frame);
                            stompClientTemp.subscribe('/wizard/ad_fetch_etl_response', function (fetchETLResponse) {
                                ctrl._adFetchDelegate(fetchETLResponse.body);
                            });
                        });
                        this.$scope.$on('$destroy', function () {
                            stompClientTemp.disconnect(function () {
                                console.log("Socket have been disconnected due to navigate out action !");
                            });
                        });
                    };
                    /**
                     * This method get message from the websocket and update the UI accordingly
                     */
                    ActiveDirectorySetupController.prototype._adFetchDelegate = function (responseBody) {
                        var _this = this;
                        var status = JSON.parse(responseBody);
                        console.log("Task finish execution:" + status);
                        var percentage = status.success ? 100 : 0;
                        this.$timeout(function () {
                            if (status.taskType === "FETCH") {
                                _this.adStepsMap[status.dataSource].etlCompletedPercentage = 0;
                                //this.adStepsMap[status.dataSource].lastSuccessfullExecution= null;
                                if (status.success) {
                                    _this.adStepsMap[status.dataSource].fetchCompletedPercentage = percentage;
                                }
                                else {
                                    //Finish with failure. Stop execution
                                    _this.adStepsMap[status.dataSource].isRunning = false;
                                    _this.adStepsMap[status.dataSource].success = status.success;
                                    _this.adStepsMap[status.dataSource].fetchCompletedPercentage = 0;
                                }
                            }
                            else {
                                _this.adStepsMap[status.dataSource].etlCompletedPercentage = percentage;
                                _this.adStepsMap[status.dataSource].objectsCount = status.objectsCount;
                                _this.adStepsMap[status.dataSource].isRunning = false;
                                _this.adStepsMap[status.dataSource].lastSuccessfullExecution = status.lastExecutionTime;
                                _this.adStepsMap[status.dataSource].success = status.success;
                            }
                        });
                    };
                    /**
                     * Delegete to stop the execution process from server
                     */
                    ActiveDirectorySetupController.prototype.stopFeatchEtlADExecution = function () {
                        var _this = this;
                        this.$http.get(this.BASE_URL + this._END_POINT_PATH + "/stop_ad_fetch_etl").then(function (response) {
                            if (response.status < 300) {
                                _this.toastrService.success("FETCH/ETL execution stopped");
                                _this._featchEtlADStatus(); //Refresh the status
                                _this._clearElapsedTimeInterval();
                                _.each(_this.adStepsMap, function (step) { step.isRunning = true; });
                            }
                            else if (response.status === 406) {
                                _this.toastrService.error("Stop failed. There was nothing to stop");
                            }
                            else {
                                _this.toastrService.error('There was an unknown server error. Please try again.', 'Active Directory run error');
                            }
                        })
                            .catch(function (err) {
                            _this.$log.error(err);
                            _this.toastrService.error('There was an unknown server error. Please try again.', 'Active Directory run error');
                        });
                    };
                    /**
                     * Get Current Execution Status
                     */
                    ActiveDirectorySetupController.prototype._featchEtlADStatus = function () {
                        var _this = this;
                        this.$http.get(this.BASE_URL + this._END_POINT_PATH + "/ad_etl_fetch_status").then(function (response) {
                            if (response.data && response.data.fetchEtlExecutionStatus) {
                                _this.$timeout(function () {
                                    var currentExecutionFromUITime = response.data.fetchEtlExecutionStatus.lastAdFetchEtlExecutionTime;
                                    var atLeastOneRunningSrartExecutionTime = false;
                                    var tasksStatusArray = response.data.fetchEtlExecutionStatus.runningTasksStatuses;
                                    _.each(tasksStatusArray, function (dataSourceStatus) {
                                        if (_.isNil(dataSourceStatus.runningMode)) {
                                            //Currently not running
                                            _this.adStepsMap[dataSourceStatus.datasource].etlCompletedPercentage = 100;
                                            _this.adStepsMap[dataSourceStatus.datasource].fetchCompletedPercentage = 100;
                                            _this.adStepsMap[dataSourceStatus.datasource].objectsCount = dataSourceStatus.objectsCount;
                                            _this.adStepsMap[dataSourceStatus.datasource].lastSuccessfullExecution = dataSourceStatus.lastExecutionFinishTime;
                                            _this.adStepsMap[dataSourceStatus.datasource].isRunning = false;
                                        }
                                        else {
                                            atLeastOneRunningSrartExecutionTime = true;
                                            if (dataSourceStatus.runningMode.toLocaleLowerCase() === "fetch") {
                                                _this.adStepsMap[dataSourceStatus.datasource].fetchCompletedPercentage = 0;
                                            }
                                            else {
                                                _this.adStepsMap[dataSourceStatus.datasource].fetchCompletedPercentage = 100;
                                            }
                                            _this.adStepsMap[dataSourceStatus.datasource].etlCompletedPercentage = 0;
                                            _this.adStepsMap[dataSourceStatus.datasource].isRunning = true;
                                            _this.adStepsMap[dataSourceStatus.datasource].objectsCount = 0;
                                            _this.adStepsMap[dataSourceStatus.datasource].lastSuccessfullExecution = 0;
                                        }
                                        _this.adStepsMap[dataSourceStatus.datasource].success = true;
                                    });
                                    //If any of the tasks running - start the execution time
                                    if (atLeastOneRunningSrartExecutionTime && !_.isNil(currentExecutionFromUITime)) {
                                        _this._startElapseTimeCount(currentExecutionFromUITime);
                                    }
                                });
                            }
                        })
                            .catch(function (err) {
                            _this.$log.error(err);
                        });
                    };
                    /**
                     * Execute AD Fetch + ETL.
                     * Alert if already running.
                     * Doesn't wait for the running to finish
                     */
                    ActiveDirectorySetupController.prototype.featchEtlAD = function () {
                        var _this = this;
                        this.$http.get(this.BASE_URL + this._END_POINT_PATH + "/ad_fetch_etl").then(function (response) {
                            if (response.status < 300) {
                                _this.toastrService.success("Retrieve Active Directory Data");
                                _this._initAdStepDataCounters();
                                _this._startElapseTimeCount();
                                _.each(_this.adStepsMap, function (step) { step.isRunning = true; });
                            }
                            else if (response.status === 423) {
                                _this.toastrService.error("FETCH/ETL Already Running");
                            }
                            else {
                                //alert("return error: "+response.status);
                                _this.toastrService.error('There was an unknown server error. Please try again.', 'Active Directory run error');
                            }
                        })
                            .catch(function (err) {
                            _this.$log.error(err);
                            _this.toastrService.error('There was an unknown server error. Please try again.', 'Active Directory run error');
                        });
                    };
                    /*
                    Return true if the Group / Users / OU / Devices is still executing right now
                     */
                    ActiveDirectorySetupController.prototype.isAnyTaskRunning = function () {
                        var anyRunning = false;
                        _.each(this.adStepsMap, function (step) {
                            if (step.isRunning) {
                                anyRunning = true; //At least one is running
                            }
                        });
                        return anyRunning;
                    };
                    /**
                     * Sets loader on/off
                     * @param state
                     */
                    ActiveDirectorySetupController.prototype.setLoadingState = function (state) {
                        this.isLoading = !!state;
                    };
                    /**
                     * Gets the element's form element and constructor
                     * @private
                     */
                    ActiveDirectorySetupController.prototype._getFormElementAndController = function () {
                        var _this = this;
                        this.$scope.$applyAsync(function () {
                            _this.formElement = _this.$element.find('[ng-form]');
                            _this.formElementController = angular.element(_this.formElement).controller('form');
                        });
                    };
                    /**intellik
                     * Initiates the the data fetching process
                     * @private
                     */
                    ActiveDirectorySetupController.prototype._initDataFetch = function () {
                        var _this = this;
                        this.isLoading = true;
                        this.$http.get(this.BASE_URL + this._END_POINT_PATH)
                            .then(function (res) {
                            _this._activeDirectoryConnectors = res.data.adConnectionList;
                            _this.activeDirectoryConnectors = res.data.adConnectionList;
                            _this.getValueFns = [];
                            // Add new connector if list is empty
                            if (_this.activeDirectoryConnectors.length === 0) {
                                _this.activeDirectoryConnectors.push(_this._createNewConnector());
                            }
                        })
                            .catch(function (err) {
                            _this.$log.error(err);
                            _this.toastrService.error('There was an unknown server error. Please try again.', 'Active Directory Load Error');
                        })
                            .finally(function () {
                            _this.$timeout(function () {
                                _this.isLoading = false;
                            }, 500);
                        });
                    };
                    /**
                     * Tests if the data to be submited is the same as the existing data (used when saving a form to prevent
                     * unneeded calls to the server)
                     * @returns {boolean}
                     * @private
                     */
                    ActiveDirectorySetupController.prototype._testEqualConnectorLists = function () {
                        return _.isEqual(this._getValues(), this._activeDirectoryConnectors);
                    };
                    /**
                     * Returns a new empty IActiveDirectoryConnector
                     * @returns {{dcs: Array, domainBaseSearch: null, domainUser: null, domainPassword: null}}
                     * @private
                     */
                    ActiveDirectorySetupController.prototype._createNewConnector = function () {
                        return {
                            dcs: [],
                            domainBaseSearch: null,
                            domainUser: null,
                            domainPassword: null
                        };
                    };
                    /**
                     * Registers each added connector by being provided with a getValue function that will return the value of
                     * connector
                     * @param getValueFn
                     */
                    ActiveDirectorySetupController.prototype.registerConnector = function (getValueFn) {
                        this.getValueFns.push(getValueFn);
                    };
                    /**
                     * Adds a new connector. Creates a new connectors list (from the old one) and adds an empty connector.
                     */
                    ActiveDirectorySetupController.prototype.addNewConnector = function () {
                        // Create new list (for immutability)
                        this.activeDirectoryConnectors = this.activeDirectoryConnectors.slice(0);
                        // Add new empty connector
                        this.activeDirectoryConnectors.push(this._createNewConnector());
                    };
                    /**
                     * Submits the form to the server
                     */
                    ActiveDirectorySetupController.prototype.saveForm = function () {
                        var _this = this;
                        if (this.formElementController.$invalid || this.formElementController.$pristine) {
                            return;
                        }
                        if (this._testEqualConnectorLists()) {
                            return;
                        }
                        this.isLoading = true;
                        this._submitForm()
                            .then(function () {
                            _this._initDataFetch();
                        });
                    };
                    /**
                     * Method that run evey second more or less and update the time since that last execution
                     */
                    ActiveDirectorySetupController.prototype._calculateElapsedtime = function () {
                        console.log("calculateElapsedtime");
                        this.elapsedTimeSeconds = Math.floor((new Date().getTime() - this.startExecutionTime) / 1000);
                    };
                    /**
                     * Kill the time counter and restart the time count
                     *
                     * @private
                     */
                    ActiveDirectorySetupController.prototype._clearElapsedTimeInterval = function () {
                        if (this.elapsedTimeCalculationPromise != null) {
                            this.$interval.cancel(this.elapsedTimeCalculationPromise);
                            this.elapsedTimeCalculationPromise = null;
                        }
                        this.elapsedTimeSeconds = null;
                        this.startExecutionTime = null;
                    };
                    /**
                     * set the start execution time and sent interval to update it each second
                     * @param startTime
                     * @private
                     */
                    ActiveDirectorySetupController.prototype._startElapseTimeCount = function (startTime) {
                        var _this = this;
                        if (_.isNil(startTime)) {
                            this.startExecutionTime = new Date().getTime();
                        }
                        else {
                            this.startExecutionTime = startTime;
                        }
                        this.elapsedTimeCalculationPromise = this.$interval(function () {
                            _this._calculateElapsedtime();
                        }, 1000);
                    };
                    /**
                     * Submits the form to the server and continues to the next step in the wizard.
                     */
                    ActiveDirectorySetupController.prototype.saveFormAndContinue = function () {
                        var _this = this;
                        if (this.formElementController.$invalid || this.formElementController.$pristine) {
                            return;
                        }
                        if (this._testEqualConnectorLists()) {
                            this._continueToNextStep();
                            return;
                        }
                        this.isLoading = true;
                        this._submitForm()
                            .then(function () {
                            _this._continueToNextStep();
                        });
                    };
                    ActiveDirectorySetupController.prototype._initAdStepDataCounters = function () {
                        this.adStepsMap[USERS_STEP] = new IAdStep();
                        this.adStepsMap[GROUPS_STEP] = new IAdStep();
                        this.adStepsMap[OUS_STEP] = new IAdStep();
                        this.adStepsMap[DEVICES_STEP] = new IAdStep();
                    };
                    ActiveDirectorySetupController.$inject = ['$scope', '$element', '$http', 'BASE_URL', '$timeout', '$log', 'toastrService', 'BASE_WEBSOCKET_URL', '$interval'];
                    return ActiveDirectorySetupController;
                }());
                angular.module('Fortscale.SystemSetupApp.layouts')
                    .controller('activeDirectorySetupController', ActiveDirectorySetupController);
            })(activeDirectorySetup = layouts.activeDirectorySetup || (layouts.activeDirectorySetup = {}));
        })(layouts = SystemSetupApp.layouts || (SystemSetupApp.layouts = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var layouts;
        (function (layouts) {
            var setupSummary;
            (function (setupSummary) {
                var SetupSummaryController = (function () {
                    function SetupSummaryController($q, $http, $log, BASE_URL, toastrService, $timeout, LOG_REPOSITORY_TYPES) {
                        this.$q = $q;
                        this.$http = $http;
                        this.$log = $log;
                        this.BASE_URL = BASE_URL;
                        this.toastrService = toastrService;
                        this.$timeout = $timeout;
                        this.LOG_REPOSITORY_TYPES = LOG_REPOSITORY_TYPES;
                        this.isLoading = false;
                        this._ACTIVE_DIRECTORY_ENDPOINT = '/active_directory';
                        this._LOG_REPOSITORY_ENDPOINT = '/log_repository';
                        this._USER_TAGS_ENDPOINT = '/tags/user_tags';
                        this._initDataFetch();
                        this.logRepositoyTypes = _.keyBy(this.LOG_REPOSITORY_TYPES, 'value');
                    }
                    /**
                     * Get active directory data and store it
                     * @private
                     */
                    SetupSummaryController.prototype._initActiveDirectoryDataFetch = function () {
                        var _this = this;
                        return this.$http.get(this.BASE_URL + this._ACTIVE_DIRECTORY_ENDPOINT)
                            .then(function (res) {
                            _this.activeDirectoryConnectors = res.data.adConnectionList;
                        });
                    };
                    /**
                     * Get log repository data and store it
                     * @private
                     */
                    SetupSummaryController.prototype._initLogRepositoryDataFetch = function () {
                        var _this = this;
                        return this.$http.get(this.BASE_URL + this._LOG_REPOSITORY_ENDPOINT)
                            .then(function (res) {
                            _this.logRepositoryConnectors = res.data.logRepositoryList;
                        });
                    };
                    /**
                     * Get tags data and store it
                     * @returns {IPromise<TResult>}
                     * @private
                     */
                    SetupSummaryController.prototype._initTagsDataFetch = function () {
                        var _this = this;
                        return this.$http.get(this.BASE_URL + this._USER_TAGS_ENDPOINT)
                            .then(function (res) {
                            // Sort list by display name
                            _this.tagsList = _.orderBy(res.data.data, 'displayName');
                        });
                    };
                    /**
                     * Initates data fetch process.
                     * @private
                     */
                    SetupSummaryController.prototype._initDataFetch = function () {
                        var _this = this;
                        this.isLoading = true;
                        this.$q.all([
                            this._initActiveDirectoryDataFetch(),
                            this._initLogRepositoryDataFetch(),
                            this._initTagsDataFetch()
                        ])
                            .catch(function (err) {
                            _this.$log.error(err);
                            _this.toastrService.error('There was an unknown server error.', 'Setup Summary data load');
                        })
                            .finally(function () {
                            _this.$timeout(function () {
                                _this.isLoading = false;
                            }, 500);
                        });
                    };
                    SetupSummaryController.$inject = ['$q', '$http', '$log', 'BASE_URL', 'toastrService', '$timeout', 'LOG_REPOSITORY_TYPES'];
                    return SetupSummaryController;
                }());
                angular.module('Fortscale.SystemSetupApp.layouts')
                    .controller('setupSummaryController', SetupSummaryController);
            })(setupSummary = layouts.setupSummary || (layouts.setupSummary = {}));
        })(layouts = SystemSetupApp.layouts || (SystemSetupApp.layouts = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var layouts;
        (function (layouts) {
            var tagsSetup;
            (function (tagsSetup) {
                var MAX_RUILES_TO_PRESENT = 6;
                var AugmentedTagDefinition = (function () {
                    function AugmentedTagDefinition(name, displayName, rules, createsIndicator, active, isAssignable, error, changed, newTag, predefined) {
                        if (name === void 0) { name = null; }
                        if (displayName === void 0) { displayName = null; }
                        if (rules === void 0) { rules = []; }
                        if (createsIndicator === void 0) { createsIndicator = true; }
                        if (active === void 0) { active = true; }
                        if (isAssignable === void 0) { isAssignable = true; }
                        if (error === void 0) { error = null; }
                        if (changed === void 0) { changed = true; }
                        if (newTag === void 0) { newTag = true; }
                        if (predefined === void 0) { predefined = false; }
                        this.name = name;
                        this.displayName = displayName;
                        this.rules = rules;
                        this.createsIndicator = createsIndicator;
                        this.active = active;
                        this.isAssignable = isAssignable;
                        this.error = error;
                        this.changed = changed;
                        this.newTag = newTag;
                        this.predefined = predefined;
                    }
                    return AugmentedTagDefinition;
                }());
                var TagsSetupController = (function () {
                    function TagsSetupController($scope, $http, BASE_URL, $log, $timeout, toastrService) {
                        this.$scope = $scope;
                        this.$http = $http;
                        this.BASE_URL = BASE_URL;
                        this.$log = $log;
                        this.$timeout = $timeout;
                        this.toastrService = toastrService;
                        this.isLoading = false;
                        this._USER_TAGS_URL = this.BASE_URL + '/tags/user_tags';
                        this._RUNNING_TAGS_TASK_URL = this.BASE_URL + '/tags/run_tagging_task';
                        this.nameOfeditTagName = null;
                        this.editTagRulesName = null;
                        this.changed = false;
                        this.invalid = false;
                        //Tell for each tag if we like to show all rules, or just the first X
                        this.showAllRuleNumberForTag = [];
                        this._initNewTag();
                        this._initDataFetch();
                        document.title = 'Tags Configuration';
                    }
                    /**
                     * Sets the index of the currently edited tag name, and sets focus to the input field.
                     *
                     * @param tagIndex
                     * @param evt
                     */
                    TagsSetupController.prototype.setTagNameEditIndex = function (tag, evt) {
                        if (tag && (!tag.active || tag.predefined)) {
                            return;
                        }
                        this.updatedTagDisplayNameModel = tag.name;
                        this.nameOfeditTagName = tag.name;
                        this.$timeout(function () {
                            $(evt.currentTarget).find('input').focus();
                        }, 200);
                    };
                    /**
                     * On any tag name change, validate all tags, and determine if changed
                     *
                     * @param tag
                     * @param originalTag
                     */
                    TagsSetupController.prototype.changeTagName = function (tag, originalTag) {
                        var _this = this;
                        tag.changed = this._isTagChanged(tag, originalTag);
                        // As long as tag is considered new, the displayName affects the tag name.
                        if (tag.newTag) {
                            tag.name = tag.displayName;
                        }
                        // Validate all tags (because a change in one can affect another)
                        _.each(this.tagsList, function (tag) {
                            _this._validateTag(tag);
                        });
                        this._determineIfChanged();
                    };
                    /**
                     * On any tag name change, set tag.name and validate
                     * @param tag
                     */
                    TagsSetupController.prototype.changeNewTagName = function (tag) {
                        tag.name = tag.displayName;
                        this._validateTag(tag);
                        this._determineIfChanged();
                    };
                    /**
                     * Used to create blur (and force update by blur) when clicking on Enter
                     *
                     * @param evt
                     */
                    TagsSetupController.prototype.changeTagKeyDown = function (evt) {
                        switch (evt.keyCode) {
                            case 13:
                                $(evt.currentTarget).blur();
                                break;
                            case 27:
                                $(evt.currentTarget).blur();
                                break;
                        }
                    };
                    /**
                     * When input looses focus, this method is called. It reverts the value if there's an error in the name.
                     * It removes a new tag row if there's an error in the name (of a new tag). It determines if the form has
                     * changed or has errors. It set the nameOfeditTagName to null.
                     */
                    TagsSetupController.prototype.finishEditTagName = function (tag) {
                        tag.displayName = this.updatedTagDisplayNameModel;
                        this._determineIfChanged();
                        this._determineIfError();
                        this.nameOfeditTagName = null;
                    };
                    /**
                     * When finished editing a new tag display name, it will be considered as a tag and pushed into tags list.
                     */
                    TagsSetupController.prototype.finishEditNewTagName = function () {
                        if (this.newTag.error || !this.newTag.displayName) {
                            return;
                        }
                        // Set the tag name and put it in list
                        this.tagsList.push(this.newTag);
                        // Generate new tag and set 'changed' to form
                        this._initNewTag();
                        this.finishEditTagName(this.newTag);
                    };
                    /**
                     * Removes a new tag
                     * @param index
                     * @param event
                     */
                    TagsSetupController.prototype.deleteNewTag = function (tag, event) {
                        // Validate is a tag and a new tag
                        if (!(tag && tag.newTag)) {
                            return;
                        }
                        // Stop click event from bubbling to the row
                        event.stopPropagation();
                        // Remove tag
                        _.remove(this.tagsList, function (iteratedTag) { return iteratedTag.name === tag.name; });
                        this._determineIfChanged();
                    };
                    /**
                     * Toggles Creates-Indicator state.
                     * @param tag
                     * @param originalTag
                     */
                    TagsSetupController.prototype.toggleCreatesIndicator = function (tag, originalTag) {
                        if (!tag.active) {
                            return;
                        }
                        tag.createsIndicator = !tag.createsIndicator;
                        tag.changed = this._isTagChanged(tag, originalTag);
                        this._determineIfChanged();
                    };
                    /**
                     * Toggles Active state.
                     * @param tag
                     * @param originalTag
                     */
                    TagsSetupController.prototype.toggleActiveTag = function (tag, originalTag) {
                        tag.active = !tag.active;
                        tag.changed = this._isTagChanged(tag, originalTag);
                        this._determineIfChanged();
                    };
                    /**
                     * Removes rule from a tag
                     * @param rule
                     * @param tag
                     */
                    TagsSetupController.prototype.removeRuleFromTag = function (rule, tag) {
                        // Do nothing if tag is inactive
                        if (!tag.active) {
                            return;
                        }
                        tag.rules = _.filter(tag.rules, function (tagRule) { return tagRule !== rule; });
                        var originalTag = _.find(this.originalTagsList, { name: tag.name });
                        tag.changed = this._isTagChanged(tag, originalTag);
                        this._determineIfChanged();
                    };
                    TagsSetupController.prototype._getTagFromListByName = function (tagName) {
                        return _.find(this.tagsList, { "name": this.editTagRulesName });
                    };
                    /**
                     * Adds rules to edited tag
                     */
                    TagsSetupController.prototype.addRules = function (rules) {
                        var tag = this._getTagFromListByName(this.editTagRulesName);
                        tag.rules = (tag.rules || []).concat(rules);
                        this.editTagRulesName = null;
                        var originalTag = _.find(this.originalTagsList, { name: tag.name });
                        tag.changed = this._isTagChanged(tag, originalTag);
                        this._determineIfChanged();
                    };
                    /**
                     * Sets the index of the currently new rules tag popup
                     * @param tagIndex
                     */
                    TagsSetupController.prototype.openAddTagRulesPopup = function (tag) {
                        // Do nothing if tag is inactive
                        if (!tag.active) {
                            return;
                        }
                        this.editTagRulesName = tag.name;
                    };
                    /**
                     * Closes add rules popup without update
                     */
                    TagsSetupController.prototype.cancelAddRules = function () {
                        this.editTagRulesName = null;
                    };
                    /**
                     * Saves the edited tags
                     * @param disabled
                     */
                    TagsSetupController.prototype.saveTags = function (disabled) {
                        var _this = this;
                        if (disabled) {
                            return;
                        }
                        this._submitTags()
                            .then(function (res) {
                            return _this._initDataFetch();
                        })
                            .then(function () {
                            _this.changed = false;
                            _this.toastrService.success("Tagging configuration saved locally, to execute user tagging click Save & Continue");
                        });
                    };
                    /**
                     * Saves the edited tags and transitions to the nex step in the wizard
                     * @param disabled
                     */
                    TagsSetupController.prototype.saveTagsAndContinue = function (disabled) {
                        var _this = this;
                        if (disabled) {
                            return;
                        }
                        var ctrl = this;
                        this._submitAndExecuteTagingTask()
                            .then(function () {
                            ctrl.run_tagging_task;
                            _this._continueToNextStep();
                        });
                    };
                    /**
                     * Transitions to next step
                     * @private
                     */
                    TagsSetupController.prototype._continueToNextStep = function () {
                        this.$scope['systemSetupCtrl'].continueToNexStep();
                    };
                    /**
                     * Returns a list of ITagDefinition by taking all changed IAugmentedTagDefinition, and cleaning them.
                     * @returns {ITagDefinition[]}
                     * @private
                     */
                    TagsSetupController.prototype._getChangedCleanTags = function () {
                        var changedTags = _.filter(this.tagsList, 'changed');
                        return _.map(changedTags, function (tag) {
                            // Clone the tag
                            var cleanTag = _.cloneDeep(tag);
                            // Cleanup tag
                            delete cleanTag.error;
                            delete cleanTag.changed;
                            delete cleanTag.newTag;
                            return cleanTag;
                        });
                    };
                    /**
                     * Submits the edited tags
                     * @returns {IPromise<TResult>}
                     * @private
                     */
                    TagsSetupController.prototype._submitTags = function () {
                        var _this = this;
                        this.isLoading = true;
                        var tags = this._getChangedCleanTags();
                        return this.$http.post(this._USER_TAGS_URL, tags)
                            .catch(function (err) {
                            _this.$log.error(err);
                            _this.toastrService.error('There was an unknown server error. Please try again.', 'Tags Update Error');
                        })
                            .finally(function () {
                            _this.isLoading = false;
                        });
                    };
                    /**
                     * Submits the edited tags
                     * @returns {IPromise<TResult>}
                     * @private
                     */
                    TagsSetupController.prototype._submitAndExecuteTagingTask = function () {
                        var _this = this;
                        return this._submitTags().then(function () {
                            _this.$http.get(_this._RUNNING_TAGS_TASK_URL)
                                .catch(function (err) {
                                _this.$log.error(err);
                                _this.toastrService.error('There was an unknown server error while running tasks. Please try again.', 'Running Task TagsError');
                            })
                                .finally(function () {
                            });
                        });
                    };
                    /**
                     * Compares properties on current tag and the original tag. If they are different then true is returned.
                     * @param tag
                     * @param originalTag
                     * @returns {boolean}
                     * @private
                     */
                    TagsSetupController.prototype._isTagChanged = function (tag, originalTag) {
                        if (!originalTag) {
                            return true;
                        }
                        if (tag.displayName !== originalTag.displayName) {
                            return true;
                        }
                        if (tag.createsIndicator !== originalTag.createsIndicator) {
                            return true;
                        }
                        if (tag.active !== originalTag.active) {
                            return true;
                        }
                        if (!_.isEqual(tag.rules, originalTag.rules)) {
                            return true;
                        }
                        return false;
                    };
                    /**
                     * Removes a new tag (row)
                     * @param tagIndex
                     * @private
                     */
                    TagsSetupController.prototype._removeNewTag = function (tagIndex) {
                        this.tagsList.splice(tagIndex, 1);
                    };
                    /**
                     * Finds if any of the tags on tagsList has a 'changed' property set to true
                     * @private
                     */
                    TagsSetupController.prototype._determineIfChanged = function () {
                        this.changed = _.some(this.tagsList, function (tag) { return !!tag.changed; });
                    };
                    /**
                     * Finds if any of the tags on tagsList has an 'error' property
                     * @private
                     */
                    TagsSetupController.prototype._determineIfError = function () {
                        this.invalid = _.some(this.tagsList, function (tag) { return !!tag.error; });
                    };
                    /**
                     * Validates a tag. Find if a tag name is empty, or if the tag name is not unique.
                     * @param tag
                     * @param canBeEmptyName
                     * @private
                     */
                    TagsSetupController.prototype._validateTag = function (tag, canBeEmptyName) {
                        if (canBeEmptyName === void 0) { canBeEmptyName = false; }
                        // tag display name should not be an empty string
                        tag.error = {
                            msg: null
                        };
                        // Get a list of other tags
                        var otherTags = _.filter(this.tagsList, function (tagFromList) { return tagFromList !== tag; });
                        // tag display name can not be empty
                        if (!canBeEmptyName &&
                            (tag.displayName === '' || tag.displayName === null || tag.displayName === undefined)) {
                            tag.error.msg = 'Tag can not be empty.';
                        }
                        else if (_.some(otherTags, function (otherTag) { return otherTag.displayName.toLowerCase() === tag.displayName.toLowerCase(); })) {
                            tag.error.msg = 'Tag name must be unique.';
                        }
                        else if (tag.newTag && _.some(otherTags, function (otherTag) { return otherTag.name.toLowerCase() === tag.name.toLowerCase(); })) {
                            var dupTag = _.find(otherTags, { name: tag.name });
                            tag.error.msg =
                                "There is another tag with the same name id; name-id: " + dupTag.name + " | name: " + dupTag.displayName;
                        }
                        else {
                            delete tag.error;
                        }
                        this._determineIfError();
                    };
                    /**
                     * Loads tags data
                     * @private
                     */
                    TagsSetupController.prototype._initDataFetch = function () {
                        var _this = this;
                        this.isLoading = true;
                        return this.$http.get(this._USER_TAGS_URL)
                            .then(function (res) {
                            // Sort list by display name
                            var tagsList = _.orderBy(res.data.data, 'displayName');
                            // place original list (unedited)
                            _this.originalTagsList = tagsList;
                            // Clone to new list
                            _this.tagsList = _.cloneDeep(tagsList);
                        })
                            .catch(function (err) {
                            _this.$log.error(err);
                            _this.toastrService.error('There was an unknown server error. Please try again.', 'Tags Load Error');
                        })
                            .finally(function () {
                            _this.$timeout(function () {
                                _this.isLoading = false;
                            }, 500);
                        });
                    };
                    /**
                     * Tell the UI, if the state of this tag allow to present all the rules, or limit the presentation to the amount of the allowed MAX
                     * @param tag
                     * @returns {number}
                     */
                    TagsSetupController.prototype.getRulesLimit = function (tag) {
                        var showAll = this.showAllRuleNumberForTag[tag.name];
                        if (showAll) {
                            return tag.rules.length;
                        }
                        else {
                            return MAX_RUILES_TO_PRESENT;
                        }
                    };
                    /**
                     * Change the state to the tag rules display.
                     * If the tag state already present more then allowed this method set it to display just as the maximum allowed.
                     * If the tag state limit the presented rules to the max value, we change the state to display all
                     * @param tag
                     */
                    TagsSetupController.prototype.setRulesLimit = function (tag) {
                        this.showAllRuleNumberForTag[tag.name] = !this.showAllRuleNumberForTag[tag.name];
                    };
                    /**
                        The porpose of the moethod is to return if there are more tags to display or that we already presented
                        more tags then allowed.
            
                     * If we currently display less tags then the tags that the user have, this method return "more",
                     * if we currently display more tags then the defined max, the this methoer return "less"
                     * @param tag
                     * @returns {number}
                     */
                    TagsSetupController.prototype.displayMoreOrLessRulesText = function (tag) {
                        var ruleLimitForCurrentTag = this.getRulesLimit(tag);
                        if (tag.rules.length > ruleLimitForCurrentTag) {
                            return "more";
                        }
                        if (ruleLimitForCurrentTag > MAX_RUILES_TO_PRESENT) {
                            return "less";
                        }
                        return null;
                    };
                    /**
                     * Initiates a new tag object
                     *
                     * @private
                     */
                    TagsSetupController.prototype._initNewTag = function () {
                        this.newTag = new AugmentedTagDefinition();
                    };
                    TagsSetupController.$inject = ['$scope', '$http', 'BASE_URL', '$log', '$timeout', 'toastrService'];
                    return TagsSetupController;
                }());
                angular.module('Fortscale.SystemSetupApp.layouts')
                    .controller('tagsSetupController', TagsSetupController);
            })(tagsSetup = layouts.tagsSetup || (layouts.tagsSetup = {}));
        })(layouts = SystemSetupApp.layouts || (SystemSetupApp.layouts = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

var Fortscale;
(function (Fortscale) {
    var SystemSetupApp;
    (function (SystemSetupApp) {
        var layouts;
        (function (layouts) {
            var logRepositorySetup;
            (function (logRepositorySetup) {
                var LogRepositorySetupController = (function () {
                    function LogRepositorySetupController($scope, $element, $http, BASE_URL, $timeout, $log, toastrService, LOG_REPOSITORY_TYPES) {
                        this.$scope = $scope;
                        this.$element = $element;
                        this.$http = $http;
                        this.BASE_URL = BASE_URL;
                        this.$timeout = $timeout;
                        this.$log = $log;
                        this.toastrService = toastrService;
                        this.LOG_REPOSITORY_TYPES = LOG_REPOSITORY_TYPES;
                        this._END_POINT_PATH = '/log_repository';
                        this.isLoading = false;
                        this._initDataFetch();
                        document.title = 'Log Repository Configuration';
                    }
                    /**
                     * Returns a list of derived ILogRepositoryConnector by using registered getValueFns.
                     * @returns {ILogRepositoryConnector[]}
                     * @private
                     */
                    LogRepositorySetupController.prototype._getValues = function () {
                        // Get a list of values by using registered getValueFns
                        var logRepositories = _.map(this.getValueFns, function (valueFn) {
                            var value = valueFn();
                            return {
                                id: value.id,
                                alias: value.alias,
                                fetchSourceType: value.fetchSourceType,
                                host: value.host,
                                user: value.user,
                                password: value.password,
                                port: value.port,
                                removed: value.removed,
                                encryptedPassword: !!value.encryptedPassword
                            };
                        });
                        // Filter out all connectors that have been destroyed, and clean the remaining.
                        logRepositories = _.filter(logRepositories, function (logRepository) { return !logRepository.removed; });
                        _.each(logRepositories, function (logRepository) { return delete logRepository.removed; });
                        return logRepositories;
                    };
                    /**
                     * Submits available connectors.
                     * @returns {IPromise<TResult>}
                     * @private
                     */
                    LogRepositorySetupController.prototype._submitForm = function () {
                        var _this = this;
                        this.isLoading = true;
                        var domainControllers = this._getValues();
                        return this.$http.post(this.BASE_URL + this._END_POINT_PATH, domainControllers)
                            .then(function (res) {
                            _this.logRepositoryForm.$setPristine();
                            return res;
                        })
                            .catch(function (err) {
                            _this.$log.error(err);
                            _this.toastrService.error('There was an unknown server error. Please try again.', 'Log Repository Update Error');
                        })
                            .finally(function () {
                            _this.$timeout(function () {
                                _this.isLoading = false;
                            }, 500);
                        });
                    };
                    /**
                     * Transitions to next step
                     * @private
                     */
                    LogRepositorySetupController.prototype._continueToNextStep = function () {
                        this.$scope['systemSetupCtrl'].continueToNexStep();
                    };
                    /**
                     * Removes a connector from the list.
                     *
                     * @param connector
                     */
                    LogRepositorySetupController.prototype.removeConnector = function (connector) {
                        this.logRepositoryConnectors =
                            _.filter(this.logRepositoryConnectors, function (listConnector) {
                                return listConnector !== connector;
                            });
                        /*  this.$http.delete(this.BASE_URL + this._END_POINT_PATH+"/"+connector.id)
                              .then((res:ng.IHttpPromiseCallbackArg<{fetchSourceList:ILogRepositoryConnector}>) => {
                                  this._initDataFetch();
                              })
                              .catch((err) => {
                                  this.$log.error(err);
                                  this.toastrService.error('There was an unknown server error. Please try again.',
                                      'Log Repository Load Error');
                              })
                              .finally(() => {
                                  this.$timeout(() => {
                                      this.isLoading = false;
                                  }, 500);
              
                              })*/
                    };
                    /**
                     * Sets loader on/off
                     * @param state
                     */
                    LogRepositorySetupController.prototype.setLoadingState = function (state) {
                        this.isLoading = !!state;
                    };
                    /**
                     * Initiates the the data fetching process
                     * @private
                     */
                    LogRepositorySetupController.prototype._initDataFetch = function () {
                        var _this = this;
                        this.isLoading = true;
                        this.$http.get(this.BASE_URL + this._END_POINT_PATH)
                            .then(function (res) {
                            _this._logRepositoryConnectors = res.data.logRepositoryList;
                            _this.logRepositoryConnectors = res.data.logRepositoryList;
                            if (_this.logRepositoryConnectors.length == 0) {
                                var defaultConnector = {
                                    id: null,
                                    fetchSourceType: 'SPLUNK',
                                    alias: '',
                                    host: '',
                                    user: '',
                                    password: '',
                                    port: null,
                                    encryptedPassword: false
                                };
                                _this.logRepositoryConnectors.push(defaultConnector);
                            }
                            _this.getValueFns = [];
                        })
                            .catch(function (err) {
                            _this.$log.error(err);
                            _this.toastrService.error('There was an unknown server error. Please try again.', 'Log Repository Load Error');
                        })
                            .finally(function () {
                            _this.$timeout(function () {
                                _this.isLoading = false;
                            }, 500);
                        });
                    };
                    /**
                     * Tests if the data to be submited is the same as the existing data (used when saving a form to prevent
                     * unneeded calls to the server)
                     * @returns {boolean}
                     * @private
                     */
                    LogRepositorySetupController.prototype._testEqualConnectorLists = function () {
                        return _.isEqual(this._getValues(), this._logRepositoryConnectors);
                    };
                    /**
                     * Returns a new empty ILogRepositoryConnector
                     * @returns {{dcs: Array, domainBaseSearch: null, domainUser: null, domainPassword: null}}
                     * @private
                     */
                    LogRepositorySetupController.prototype._createNewConnector = function () {
                        return {
                            id: null,
                            alias: null,
                            fetchSourceType: this.LOG_REPOSITORY_TYPES[0].value,
                            host: null,
                            user: null,
                            password: null,
                            port: null,
                            encryptedPassword: false
                        };
                    };
                    /**
                     * Registers each added connector by being provided with a getValue function that will return the value of
                     * connector
                     * @param getValueFn
                     */
                    LogRepositorySetupController.prototype.registerConnector = function (getValueFn) {
                        this.getValueFns.push(getValueFn);
                    };
                    /**
                     * Adds a new connector. Creates a new connectors list (from the old one) and adds an empty connector.
                     */
                    LogRepositorySetupController.prototype.addNewConnector = function () {
                        // Create new list (for immutability)
                        if (this.logRepositoryConnectors) {
                            this.logRepositoryConnectors = this.logRepositoryConnectors.slice(0);
                        }
                        else {
                            this.logRepositoryConnectors = new Array();
                        }
                        // Add new empty connector
                        this.logRepositoryConnectors.push(this._createNewConnector());
                    };
                    /**
                     * Submits the form to the server
                     */
                    LogRepositorySetupController.prototype.saveForm = function () {
                        var _this = this;
                        if (this.logRepositoryForm.$invalid || this.logRepositoryForm.$pristine) {
                            return;
                        }
                        if (this._testEqualConnectorLists()) {
                            return;
                        }
                        this.isLoading = true;
                        this._submitForm()
                            .then(function () {
                            _this._initDataFetch();
                        });
                    };
                    /**
                     * Submits the form to the server and continues to the next step in the wizard.
                     */
                    LogRepositorySetupController.prototype.saveFormAndContinue = function () {
                        var _this = this;
                        if (this.logRepositoryForm.$invalid || this.logRepositoryForm.$pristine) {
                            return;
                        }
                        if (this._testEqualConnectorLists()) {
                            this._continueToNextStep();
                            return;
                        }
                        this.isLoading = true;
                        this._submitForm()
                            .then(function () {
                            _this._continueToNextStep();
                        });
                    };
                    LogRepositorySetupController.$inject = ['$scope', '$element', '$http', 'BASE_URL', '$timeout', '$log',
                        'toastrService', 'LOG_REPOSITORY_TYPES'];
                    return LogRepositorySetupController;
                }());
                angular.module('Fortscale.SystemSetupApp.layouts')
                    .controller('logRepositorySetupController', LogRepositorySetupController);
            })(logRepositorySetup = layouts.logRepositorySetup || (layouts.logRepositorySetup = {}));
        })(layouts = SystemSetupApp.layouts || (SystemSetupApp.layouts = {}));
    })(SystemSetupApp = Fortscale.SystemSetupApp || (Fortscale.SystemSetupApp = {}));
})(Fortscale || (Fortscale = {}));

//# sourceMappingURL=system-setup-main.3.0.0.js.map

(function () {
    'use strict';
    function FsResourceStore(fsResourceStoreService) {

        /**
         * Controller constructor
         *
         * @param $scope
         * @constructor
         */
        function FsResourceController($scope) {

            this.$scope = $scope;

            this.init();
        }

        _.merge(FsResourceController.prototype, {

            /**
             * resource watch function.
             *
             * @returns {*}
             *
             * @private
             */
            _resourceWatchFn: function () {
                return this._resource;
            },
            /**
             * resource watch action function. Invokes fsResourceStoreService.storeResource with
             * the resource.
             *
             * @param {*} resource
             * @private
             */
            _resourceWatchActionFn: function (resource) {
                if (resource !== undefined) {
                    fsResourceStoreService.storeResource(this._resourceName, resource,
                        !!this._purgeOnExpire);
                }
            },

            /**
             * Initiates resource watch
             *
             * @private
             */
            _initResourceWatch: function () {
                this.$scope.$watch(this._resourceWatchFn.bind(this),
                    this._resourceWatchActionFn.bind(this));
            },

            /**
             * Initiate all watches
             *
             * @private
             */
            _initWatches: function () {
                this._initResourceWatch();
            },

            /**
             * Controller init function.
             */
            init: function () {
                this._initWatches();
            }
        });

        FsResourceController.$inject = ['$scope'];

        return {
            restrict: 'E',
            scope: {},
            controller: FsResourceController,
            controllerAs: 'fsResource',
            bindToController: {
                _resourceName: '@resourceName',
                _resource: '=resource',
                _purgeOnExpire: '@purgeOnExpire'
            }
        };
    }

    FsResourceStore.$inject = ['fsResourceStore'];
    angular.module('Fortscale.shared.components.fsResourceStore')
        .directive('fsResourceStore', FsResourceStore);
}());

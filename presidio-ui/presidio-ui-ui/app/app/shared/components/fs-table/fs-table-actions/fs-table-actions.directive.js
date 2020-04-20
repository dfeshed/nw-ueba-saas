(function () {
    'use strict';

    function fsTableActionsDirective(assert, URLUtils, fsResourceStore, indicatorTypeMapper) {

         /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsTableActionsController() {
            this.init();
        }

        angular.extend(FsTableActionsController.prototype, {

            /**
             * Validations
             */

            _validate: function _validate() {
                var errStart = 'FsTableActionsController._validate: ';

                // Validate `exploreId`
                assert(angular.isString(this.exploreId),
                    errStart + '`exploreId` must be a String', TypeError);
                assert(this.exploreId.length,
                    errStart + '`exploreId` must not be empty', RangeError);

                // Validate `baseUrl`
                assert(angular.isString(this.baseUrl),
                    errStart + '`baseUrl` must be a String', TypeError);
                assert(this.baseUrl.length,
                    errStart + '`baseUrl` must not be empty', RangeError);
            },

            /**
             * PUBLIC METHODS
             */

            /**
             * Trigger a URL change to explore a specific Alert
             */
            exploreAlert: function () {

                var targetUrl = this.baseUrl + '/' + this.exploreId;

                var alert = fsResourceStore.fetchResourceItemById('alerts', this.exploreId);

                // Find an indicator that has an indicator type
                var indicator = null;
                var indicatorType = null;
                _.every(alert.evidences, function (iIndicator) {
                    var iIndicatorType = indicatorTypeMapper.getType(iIndicator);
                    if (iIndicatorType !== null) {
                        indicator = iIndicator;
                        indicatorType = iIndicatorType;
                    }
                });

                // Build the target url
                if (indicatorType !== null) {

                    targetUrl += '/' + indicator.id;

                    // Route to 'gen' for general indicators
                    if (indicatorType.indicatorClass === 'gen') {
                        targetUrl += '/gen/overview';

                    } else if (indicatorType.indicatorClass === 'tag') {
                        targetUrl += '/tag';
                    }

                }



                URLUtils.setUrl(targetUrl, true);
            },

            /**
             * Init
             */
            init: function () {
                this._validate();
            }

        });

        return {
            templateUrl: 'app/shared/components/fs-table/fs-table-actions/' +
                'fs-table-actions.view.html',
            restrict: 'E',
            controller: FsTableActionsController,
            controllerAs: 'actions',
            scope: {},
            bindToController: {
                exploreId: '=',
                baseUrl: '='
            }
        };
    }

    fsTableActionsDirective.$inject = ['assert', 'URLUtils', 'fsResourceStore',
        'indicatorTypeMapper'];

    angular.module('Fortscale.shared.components.fsTable')
        .directive('fsTableActions', fsTableActionsDirective);
}());

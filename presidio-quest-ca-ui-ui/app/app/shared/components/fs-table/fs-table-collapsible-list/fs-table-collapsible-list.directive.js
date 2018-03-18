(function () {
    'use strict';

    function fsTableCollapsibleList() {

         /**
         * The directive's controller function
         *
         * @constructor
         */

        function FsTableCollapsibleListController() {
             this.maxDisplayLength = 3;
             this.showFullList = false;
         }

        angular.extend(FsTableCollapsibleListController.prototype, {
            /**
             * This method controls if we see only the first 3 items in the list or all of them
             */
            showFullListFn : function showFullList(){
                var ctrl = this;
                if (ctrl.showFullList){
                    ctrl.showFullList = false;
                    this.maxDisplayLength = 3;
                } else {
                    ctrl.showFullList = true;
                    this.maxDisplayLength = ctrl.listCollection.length;

                }
            }
        });


        return {
            templateUrl: 'app/shared/components/fs-table/fs-table-collapsible-list/' +
            'fs-table-collapsible-list.view.html',
            restrict: 'E',
            controller: FsTableCollapsibleListController,
            controllerAs: 'entity',
            scope: {},
            bindToController: {
                titleId: '=',
                titleName: '=',
                listCollection: '=',
                listCollectionDisplayField: '='
            }

        };
    }

    fsTableCollapsibleList.$inject = [];


    angular.module('Fortscale.shared.components.fsTable')
        .directive('fsTableCollapsibleList', fsTableCollapsibleList);
}());

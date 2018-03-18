(function () {
    'use strict';

    function fsSimpleTabsStripDirective ($state) {
        var activeBullet;

        function FsSimpleTabsStripController () {
            var ctrl = this;
            _.each(this.bullets,function(bullet){
                if (bullet.id === ctrl.activeId){
                    ctrl.activeBullet = bullet;
                }
            });
        }

        FsSimpleTabsStripController.prototype.isTabActive  = function(tabId) {
            return this.activeBullet.id === tabId;
        };

        FsSimpleTabsStripController.prototype.setActive = function(bullet) {
            this.activeBullet = bullet;
        };


        return {
            restrict: 'E',
            scope: {},
            controller: FsSimpleTabsStripController,
            controllerAs: 'ctrl',
            bindToController: {
                bullets: '=',
                activeId: '='
            },
            templateUrl: 'app/shared/components/controls/fs-simple-tabs-strip/fs-simple-tabs-strip.html'
        };
    }

    fsSimpleTabsStripDirective.$inject = ['$state'];
    angular.module('Fortscale.shared.components.fsSimpleTabsStrip', [])
        .directive('fsSimpleTabsStrip', fsSimpleTabsStripDirective);
}());

(function () {
    'use strict';
    function ResourceFactory (Restangular) {

        var self = this;

        self.create = function (entity) {

            // validate setting

            return Restangular.all(entity);
        };

    }

    ResourceFactory.$inject = ['Restangular'];
    angular.module('Fortscale.shared.directives.fsStateContainer')
        .service('resourceFactory', ResourceFactory);
}());

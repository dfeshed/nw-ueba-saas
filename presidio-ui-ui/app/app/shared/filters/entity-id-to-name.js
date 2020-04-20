(function () {
    'use strict';

    function entityIdToName (dataEntities) {

        function entityIdToNameFilter(id) {
            var entityObject = dataEntities.getEntityById(id);
            return (entityObject && entityObject.name) || id ;
        }

        return entityIdToNameFilter;
    }

    entityIdToName.$inject = ['dataEntities'];

    angular.module('Fortscale.shared.filters')
        .filter('entityIdToName', entityIdToName);
}());

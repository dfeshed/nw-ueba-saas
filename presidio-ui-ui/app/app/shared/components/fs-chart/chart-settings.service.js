(function () {
    'use strict';

    function ChartSettings($injector) {
        this.$injector = $injector;
    }

    _.merge(ChartSettings.prototype, {
        getSettings: function (type, settings) {
            var generalSettings = this.$injector.get('fsChart.settings.general');
            var defaultSettings = this.$injector.get('fsChart.settings.' + type);
            settings = settings || {};
            return _.merge({}, generalSettings, defaultSettings, settings);
        }
    });

        ChartSettings.$inject = ['$injector'];

    angular.module('Fortscale.shared.components.fsChart')
        .service('chartSettings', ChartSettings);
}());

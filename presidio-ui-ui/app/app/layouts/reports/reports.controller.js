(function () {
    'use strict';

    //var GDS_BASE_ENTITY = 'scored_access_event';

    function ReportsController ($element, navBarSettings, dataEntities, dataEntitiesList, page, $uiViewScroll,
        $timeout, $state) {



        this.$element = $element;
        this.dataEntities = dataEntities;
        this.navBarSettingsMaster = _.merge({}, navBarSettings);
        this.dataEntitiesList = _.merge({}, dataEntitiesList);
        this.$uiViewScroll = $uiViewScroll;
        this.$timeout = $timeout;
        this.$state = $state;
        this.page = page;


        this.init();
    }

    _.merge(ReportsController.prototype, {

        /**
         * Populates Suspicious Users menu
         *
         * @private
         */
        _populateNavBarsSuspiciousUsers: function (suspiciousUsersNavItem) {
            // get leaf entities

            /** UPDATE: It was decided that this report will not be GDS. It will only have ssh, vpn, and kerberos.
             * For that resource, the following code is commented out, and there's a new code to replace it.
             * This commented code might be some day returned.
             */
                //var entities = this.dataEntities.getExtendingEntities('scored_access_event');

            var tags=[{
                "name":"Admin",
                "id": "admin"
             },
            {
                "name":"Executive",
                "id": "executive"
            },
            {
                "name":"Service",
                "id": "service"
            }];

            suspiciousUsersNavItem.items = _.map(tags, function (tag) {
                var navItem = {};
                navItem.html = tag.name + " Accounts";
                navItem.sref = 'reports.topRiskyTagged({tagName:"' + tag.id + '"})';
                navItem.srefActive = 'reports.suspiciousUsers';
                navItem.srefActiveParams = {tagName: tag.id};
                return navItem;
            });

        },

        /**
         * Initiates nav bar settings. Populates suspicious users section.
         * @private
         */
        _initNavBarSettings: function () {
            this.navBarSettings = _.merge({}, this.navBarSettingsMaster);
            this._populateNavBarsSuspiciousUsers(_.filter(this.navBarSettings, {'html': 'High Privileged Accounts'})[0]);
        },

        _scrollToElement: function () {
            this.$timeout(() => {

                // find active element
                var el = this.$element.find('li.active');
                // find container element
                var containerEl = this.$element.find('.nav-bar-container--groups-list-container');

                // if container element height is smalled than active element bottom (top+height) then scroll to it
                if (el[0] && containerEl[0] && (containerEl.height() <= el[0].offsetTop + el[0].offsetHeight)) {
                    this.$uiViewScroll(el);
                }
            }, 200);
        },

        isReportSelected: function () {
            return this.$state.is('reports');
        },

        init: function () {
            this._initNavBarSettings();
            this._scrollToElement();
            this.$timeout(()=>{this.page.setPageTitle('Reports');});

        }
    });

    ReportsController.$inject =
        ['$element', 'navBarSettings', 'dataEntities', 'dataEntitiesList', 'page', '$uiViewScroll',
            '$timeout', '$state'];
    angular.module('Fortscale.layouts.reports')
        .controller('ReportsController', ReportsController);
}());

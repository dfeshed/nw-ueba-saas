'use strict';

var _ = require('lodash');
var config = {};

function attachSrcDir (filePath) {
    return config.srcDir + '/' + filePath;
}

function attachSrcLibsDir (filePath) {
    return config.srcLibsDir + '/' + filePath;
}

config.ver = require('../version.json').number;
config.localServerPort = 3000;
config.srcDir = './app';
config.srcLibsDir = config.srcDir + '/libs';
config.srcImagesDir = config.srcDir + '/images';
config.srcSVGsDir = config.srcDir + '/svgs';
config.srcSVGsSpritesheetDir = config.srcDir + '/svgs-spritesheet';
config.srcStylesDir = config.srcDir + '/styles';
config.distDir = './dist';
config.distAssetsDir = config.distDir + '/assets';
config.distCssDir = config.distAssetsDir + '/css';
config.distScriptsDir = config.distAssetsDir + '/js';
config.distHTMLDir = config.distDir;
config.distSVGsDir = config.distAssetsDir + '/svgs';
config.distSVGsSpritesheetDir = config.distAssetsDir + '/svgs-spritesheet';
config.srcScriptsVendorDir = config.srcDir + '/libs';
config.messages = config.srcDir + '/messages';
/**
 * Index
 */
config.srcScssIndex = config.srcStylesDir + '/main.scss';
config.srcCssIndexVendors = _.map([
    'bootstrap/css/bootstrap.min.css',
    'bootstrap/bootstrap-overrides.css',
    'detail-wrap/css/layout.css',
    'detail-wrap/css/elements.css',
    'detail-wrap/css/icons.css',
    'detail-wrap/css/user-profile.css',
    'detail-wrap/css/personal-info.css',
    'ui-layout/ui-layout.css',
    'jquery-ui/fortscale/jquery-ui-1.11.0.custom.min.css',
    'jquery-ui-date-range-picker/css/ui.daterangepicker.css',
    'yoxigen/pagination/pagination.css',
    'angular-multi-select/angular-multi-select.css',
    'world-flags-sprite/stylesheets/flags32.css',
    'kendo-ui/style/kendo.common.min.css',
    'kendo-ui/style/kendo.default.min.css',
    'opentip/opentip.css',
    'toastr/toastr.css',
    'ammaps/ammap.css'
], attachSrcLibsDir);
config.srcIndexVendorScripts = [
    'jquery/jquery-2.2.1.js',
    'bootstrap/js/bootstrap.min.js',
    'highchart/highcharts.js',
    'highchart/highcharts-more.js',
    'highchart/map.js',
    'highchart/exporting.js',
    'highchart/data.js',
    'highchart/world.js',
    'amcharts/amcharts.js',
    'amcharts/serial.js',
    'amcharts/pie.js',
    'ammaps/ammap_amcharts_extension.js',
    'ammaps/maps/js/worldLow.js',
    'ammaps/maps/js/worldHigh.js',
    'jquery-ui/jquery-ui-1.11.0.custom.min.js',
    'd3/d3.v3.min.js',
    'd3/topojson.v1.min.js',
    'd3/resources/colorbrewer.js',
    'd3/resources/geometry.js',
    'detail-wrap/scripts/raphael-min.js',
    'detail-wrap/scripts/theme.js',
    'angular/angular.js',
    'angular-ui-router/angular-ui-router.js',
    'angular-resource/angular-resource.js',
    'angular-route/angular-route.js',
    'angular-bootstrap/ui-bootstrap-tpls-0.13.3.min.js',
    'angular-translate/angular-translate.js',
    'angular-translate/angular-translate-loader-url.js',
    'angular-animate/angular-animate.js',
    'angular-messages/angular-messages.js',
    'moment/moment.js',
    'jquery-ui-date-range-picker/js/daterangepicker.jQuery.js',
    'angular-multi-select/angular-multi-select.js',
    'angular-debounce/angular-debounce.min.js',
    'paging/paging.js',
    'ui-layout/ui-layout.js',
    'yoxigen/pagination/pagination.js',
    'bootbox/bootbox.min.js',
    'lodash/lodash.js',
    'restangular/restangular.js',
    'kendo-ui/js/kendo.web.js',
    'object-observe/object-observe-lite.js',
    'nanobar/nanobar.js',
    'opentip/opentip-jquery.js',
    'toastr/toastr.js',
    'sockjs/sockjs-0.3.4.js',
    'stomp/stomp.js'
];
config.srcIndexAppScripts = _.map([
    // Scripts
    'scripts/services/page.js',
    'scripts/modules/config.js',
    'app/config/http-authorization-interceptor.config.js',
    'scripts/directives/popup/popup.js',
    'scripts/directives/dropdown/dropdown.js',
    'scripts/modules/cache.js',
    'scripts/modules/EventBus.js',
    'scripts/modules/events.js',
    'scripts/modules/utils.js',
    'scripts/modules/format.js',
    'scripts/services/conditions.js',
    'scripts/modules/styles.js',
    'scripts/modules/transforms.js',
    'scripts/modules/icons.js',
    'scripts/modules/tags.js',
    'scripts/services/charts.data.service.js',
    'scripts/modules/colors.js',
    'scripts/directives/datePicker.js',
    'scripts/modules/state.js',
    'scripts/services/DAL.js',
    'scripts/services/api.js',
    'scripts/services/fsHighChartService.js',
    'scripts/modules/colors_constants.js',

    // Menus
    'scripts/modules/menus/menus_module.js',
    'scripts/modules/menus/classes/Menu.js',
    'scripts/modules/menus/services/menus.js',
    'scripts/modules/menus/services/dynamic_menus.js',

    // Controls
    'scripts/modules/controls/controls_module.js',
    'scripts/modules/controls/classes/Control.js',
    'scripts/modules/controls/classes/ControlList.js',
    'scripts/modules/controls/services/controls.js',

    // Data Entities
    'scripts/modules/data_entities/data_entities_module.js',
    'scripts/modules/data_entities/classes/DataEntity.js',
    'scripts/modules/data_entities/classes/DataEntityField.js',
    'scripts/modules/data_entities/classes/DataEntitySort.js',
    'scripts/modules/data_entities/services/dataEntities.js',
    'scripts/modules/data_entities/classes/QueryOperator.js',
    'scripts/modules/data_entities/services/queryOperators.js',
    'scripts/modules/data_entities/classes/DataEntityFieldType.js',
    'scripts/modules/data_entities/services/dataEntityFieldTypes.js',

    // Colors Themes
    'scripts/modules/colors_themes/color_themes_module.js',
    'scripts/modules/colors_themes/services/colorThemes.js',

    // Data Queries
    'scripts/modules/data_queries/data_queries_module.js',
    'scripts/modules/data_queries/classes/DataQuery.js',

    // Reports
    'scripts/modules/reports/reports_module.js',
    'scripts/modules/reports/classes/Report.js',
    'scripts/modules/reports/reports.js',
    'scripts/modules/reports/reports_process.js',

    'scripts/services/search.js',
    'scripts/services/popup-conditions.js',

    // Widgets
    'scripts/modules/widgets/widgets_module.js',
    'scripts/modules/widgets/classes/Widget.js',
    'scripts/modules/widgets/classes/WidgetView.js',
    'scripts/modules/widgets/classes/WidgetButton.js',
    'scripts/modules/widgets/dashboards.js',
    'scripts/modules/widgets/classes/Dashboard.js',
    'scripts/modules/widgets/classes/DashboardLayout.js',
    'scripts/modules/widgets/widget_views.js',
    'scripts/modules/widgets/widget.directive/widget.directive.js',

    // Widget Views
    'widgets/graph/Chart.js',
    'widgets/table/table.module.js',
    'widgets/table/table.controller.js',
    'widgets/table/table.directive.js',
    'widgets/table/classes/TableConfig.js',

    'widgets/bubbles/bubbles.module.js',
    'widgets/bubbles/bubbles.directive.js',

    'widgets/heatMap/heatMap.module.js',
    'widgets/heatMap/heatMap.directive.js',

    'widgets/barsChart/barsChart.module.js',
    'widgets/barsChart/barsChart.directive.js',

    'widgets/percentChart/percentChart.module.js',
    'widgets/percentChart/percentChart.directive.js',

    'widgets/properties/properties.module.js',

    'widgets/forceChart/forceChart.module.js',
    'widgets/forceChart/forceChart.directive.js',

    'widgets/scatterPlot/scatterPlot.module.js',
    'widgets/scatterPlot/scatterPlot.directive.js',
    'widgets/scatterPlot/scatterPlot.controller.js',

    'widgets/securityFeed/securityFeed.module.js',
    'widgets/securityFeed/securityFeed.controller.js',
    'widgets/securityFeed/securityFeed.service.js',

    'widgets/tags/tags.module.js',

    'widgets/timeline/timeline.module.js',
    'widgets/timeline/timeline.directive.js',
    'widgets/timelinePoints/timelinePoints.directive.js',
    'widgets/timeline/timeline.service.js',
    'widgets/timeline/timeline.controller.js',

    'widgets/map/map.module.js',
    'widgets/map/map.directive.js',

    'widgets/pie/pie.module.js',
    'widgets/pie/pie.directive.js',

    'widgets/bars/bars.module.js',
    'widgets/bars/bars.directive.js',

    'widgets/stackedBars/stackedBars.module.js',
    'widgets/stackedBars/stackedBars.directive.js',

    'widgets/multiLine/multiLine.module.js',
    'widgets/multiLine/multiLine.directive.js',

    'widgets/graphs/spanBars/spanBars.module.js',
    'widgets/graphs/spanBars/spanBars.directive.js',

    'widgets/links/links.module.js',
    'widgets/links/links.directive.js',

    'widgets/multiTimeline/multiTimeline.module.js',
    'widgets/multiTimeline/multiTimeline.directive.js',

    'widgets/tree/tree.module.js',
    'widgets/tree/tree.directive.js',

    'widgets/figures/figures.module.js',

    'scripts/directives/focusWhen.js',
    'scripts/directives/tooltip/tooltip.js',

    'app/app.module.js',
    'app/app.routes.js',

    // Services
    'scripts/services/version.js',
    'scripts/services/eventbus.js',
    'scripts/services/widgets.js',
    'scripts/services/widget_types.js',
    'scripts/services/packages.js',
    'scripts/modules/auth.js',
    'scripts/services/users.js',
    'scripts/services/comments.js',
    'scripts/modules/menus/services/menus.js',

    // Controllers
    'scripts/controllers/main_controller.js',
    'scripts/controllers/main_dashboard_controller.js',
    'scripts/controllers/dashboard_controller.js',
    'scripts/controllers/control_controller.js',
    'scripts/controllers/widgets/buttonsBar_controller.js',
    'scripts/controllers/widgets/buttonsBar_controller.js',
    'widgets/dashboardWidget/dashboard_widget_controller.js',
    'widgets/htmlWidget/htmlWidget.module.js',
    'scripts/controllers/items_list_controller.js',
    'widgets/monitoring/monitoring.controller.js',
    'widgets/repeater/repeater.controller.js',
    'widgets/package/package.controller.js',

    'scripts/controllers/popup_controller.js',
    'scripts/controllers/controls/simplePagination.js',
    'scripts/controllers/controls/button_controller.js',
    'scripts/controllers/account_settings_controller.js',
    'scripts/controllers/global_settings_controller.js',

    // Directives
    'scripts/directives/dateRange.js',
    'scripts/directives/searchBox.js',
    'scripts/directives/progressBar.js',
    'scripts/directives/modal.js',
    'scripts/directives/hide_on_error.js',
    'scripts/directives/nav/nav.js',
    'scripts/directives/tabs/tabs.js',
    'scripts/directives/dropdown_menu/dropdown_menu.js',
    'scripts/directives/dropdown_delegate.js',
    'scripts/directives/param_controls/param_controls.js',
    'scripts/directives/checklist/checklist.js',
    'scripts/directives/dropdown-noclose.js',
    'scripts/directives/button_select/button_select.js',
    'scripts/directives/numbers_only/numbers_only.js',
    'scripts/directives/duration_only/duration_only.js',
    'scripts/directives/number_range/number_range.js',
    'scripts/directives/in_operator/string-in.js',

    // Filters
    'scripts/filters/data_field_type_filter.js',

    // Packages
    'data/dashboards/packages/geo_hopping/geo_hopping.controller.js',
    'data/dashboards/packages/geo_hopping/geo_hopping.service.js',

    'widgets/tabs/tabs.module.js',
    'widgets/tabs/tabs.controller.js',

    'scripts/loader.js',

    // 2.0
    // Config
    // Constants
    'app/config/base-url.constant.js',
    'app/config/toggle-features.constant.js',
    'app/config/severities.constant.js',
    'app/config/version.constant.js',
    // config files
    'app/config/restangular.config.js',
    'app/config/angular-translate.config.js',
    'app/config/app-config/remote-app-config.provider.js',
    'app/config/app-config/configContainer.provider.ts',
    'app/config/app-config/configItem.provider.ts',
    'app/config/app-config/app-config.provider.ts',
    'app/config/app-config/app-config.formatters.ts',
    'app/config/app-config/app-config.validators.ts',
    'app/layouts/configuration/components/fs-config-type.directive.js',
    'app/config/app-config.settings.js',
    'app/config/app-config-messages.settings.js',
    'app/analytics/fs-analytics.configuration.js',
    'app/config/country-codes.constant.js',

    // Shared
    'app/shared/shared.module.js',

    // shared/iterfaces
    'app/shared/interfaces/IDataBean.ts',
    'app/shared/interfaces/IIndicator.ts',
    'app/shared/interfaces/IAlert.ts',

    // shared/services
    'app/shared/services/services.module.ts',
    'app/shared/services/assert/assert.factory.js',
    'app/shared/services/dependecy-mounter/dependecy-mounter.service.js',
    'app/shared/services/fs-download-file/fs-download-file.service.js',
    'app/shared/services/fs-indicator-graphs-handlers/fs-indicator-graphs-handler.provider.js',
    'app/shared/services/fs-indicator-graphs-handlers/fs-indicator-graphs-handler.config.js',
    'app/shared/services/assert/assert.factory.js',
    'app/shared/services/interpolation/interpolation.service.js',
    'app/shared/services/object-utils/object-utils.service.js',
    'app/shared/services/csv-converter/csv-converter.service.js',
    'app/shared/services/url-utils/url-utils.service.js',
    'app/shared/services/tags-utils/tags-utils.service.ts',
    'app/shared/services/table-settings-adapter/table-settings-util.service.js',
    'app/shared/services/json-loader/json-loader.service.js',
    'app/shared/services/dateRanges/dateRanges.service.js',
    'app/shared/components/alert-feedback/alert-feedback.module.js',
    'app/shared/components/alert-feedback/alert-feedback.service.js',
    'app/shared/components/controls/fs-min-score/fs-min-score.module.js',
    'app/shared/components/controls/fs-min-score/fs-min-score.directive.js',
    'app/shared/components/controls/fs-select/fs-select.module.js',
    'app/shared/components/controls/fs-select/fs-select.directive.js',
    'app/shared/components/controls/fs-autocomplete/fs-autocomplete.module.js',
    'app/shared/components/controls/fs-autocomplete/fs-autocomplete.directive.js',
    'app/shared/components/controls/fs-gen-input/fs-gen-input.module.js',
    'app/shared/components/controls/fs-gen-input/fs-gen-input.directive.js',
    'app/shared/services/country-codes-util/country-codes-util.service.js',
    'app/shared/services/fs-indicator-types/fs-indicator-types.js',
    // shared/services/indicator-type-mapper
    'app/shared/services/indicator-type-mapper/indicator-type-mapper.module.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-global.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-abstract-scatter-plot.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-activity-time-anomaly.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-aggregated-serial.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-aggregated-serial-data-rate.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-data-rate-scatter-plot.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-dual-column-chart.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-heatmap.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-single-column-chart.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-single-pie-chart.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-column-range.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-scatter-pie-chart.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-scatter-column-chart.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-shared-credentials.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-geo-location.ts',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-geo-sequence.ts',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper-settings/indicator-type-mapper-settings-lateral-movement.ts',

    'app/shared/services/device-utils-service/device-utils-service.ts',
    'app/shared/services/indicator-type-mapper/indicator-type-map.factory.js',
    'app/shared/services/indicator-type-mapper/indicator-type-mapper.service.js',
    'app/shared/services/string-utils/string-utils.service.js',
     'app/shared/services/fortscale-websocket-util/fortsclae-websocket.service.ts',


    // END OF shared/services/indicator-type-mapper
    // shared/services/model-utils
    'app/shared/services/model-utils/model-utils.module.js',
    'app/shared/services/model-utils/entity-utils.service.js',
    'app/shared/services/model-utils/user-utils.service.js',
    // END OF shared/services/model-utils

    // shared/services/modals
    'app/shared/services/fs-modals/fs-modals.service.js',
    // END OF shared/services/modals

    // shared/services/fs-nanobar
    'app/shared/services/fs-nanobar-automation/fs-nanobar-automation.ts',
    // END OF shared/services/fs-nanobar

    // shared/fs-indexedDB-service
    'app/shared/services/fs-indexedDB-service/fs-indexddb-service.js',
    // END OF hared/fs-indexedDB-service

    // shared/services/fs-indicator-error-codes
    'app/shared/services/fs-indicator-error-codes/fs-indicator-error-codes.service.ts',
    'app/shared/services/fs-indicator-error-codes/fs-indicator-error-codes-ntlm.ts',
    'app/shared/services/fs-indicator-error-codes/fs-indicator-error-codes-kerberos.ts',
    // END OF shared/services/fs-indicator-error-codes

    // shared/services/toastr-service
    'app/shared/services/toastr-service/toastr.service.ts',

    'app/shared/services/alert-updates-service/alert-updates.service.ts',
    // END OF shared/services/toastr-service

    // shared/services/entity-activity-utils
    'app/shared/services/entity-activity-utils/activity-interfaces.ts',
    'app/shared/services/entity-activity-utils/entity-activity-utils.service.ts',
    // END OF shared/services/entity-activity-utils

    // shared/services/state-management-service
    'app/shared/services/state-management-service/state-management-service.ts',

    // END OF shared/services/state-management-service


    // shared/services/am-maps
    'app/shared/services/am-maps/am-maps-country-lat-long.const.ts',
    'app/shared/services/am-maps/am-maps-urils.service.ts',
    // END OF shared/services/am-maps

    // END OF shared/services

    // shared/filters
    'app/shared/filters/filters.module.js',
    'app/shared/filters/page-to-offset.filter.js',
    'app/shared/filters/unix-to-time-stamp.filter.js',
    'app/shared/filters/entity-id-to-name.js',
    'app/shared/filters/duration-to-pretty-time.js',
    'app/shared/filters/pascal-case.filter.js',
    'app/shared/filters/ou-display.filter.js',
    'app/shared/filters/pretty-bytes.js',
    'app/shared/filters/pretty-ou.ts',
    'app/shared/filters/string-to-date.filter.ts',
    'app/shared/filters/pretty-messages.js',
    'app/shared/filters/pretty-alert-name.js',
    'app/shared/filters/round.js',
    'app/shared/filters/encode-url-component.js',
    'app/shared/filters/truncate-decimal.js',
    'app/shared/filters/orNA.filter.js',
    'app/shared/filters/orZero.filter.ts',
    'app/shared/filters/anomaly-type-formatter.filter.js',
    'app/shared/filters/fs-ordinal.filter.js',
    'app/shared/filters/fs-pretty-duration.filter.js',
    'app/shared/filters/fs-dt-humanize.ts',
    'app/shared/filters/fs-not-in.filter.ts',

     'app/shared/filters/fs-partial-strong.ts',
    'app/shared/filters/fs-remove-if-exist.ts',

    // shared/directives
    'app/shared/directives/directives.module.js',
    'app/shared/directives/fs-state-container/fs-state-container.directive.js',
    'app/shared/directives/fs-state-container/control-types.const.js',
    'app/shared/directives/fs-state-container/url-state-manager.service.js',
    'app/shared/directives/fs-state-container/fs-state-container.directive.js',
    'app/shared/directives/fs-state-container/resource-factory.service.js',

    // shared/components
    'app/shared/components/components.module.js',
    'app/shared/components/fs-controls/fs-controls.directive.js',
    'app/shared/components/fs-table/fs-table.module.js',
    'app/shared/components/fs-table/fs-table.directive.js',
    'app/shared/components/fs-table/fs-table-collapsible-list/fs-table-collapsible-list.directive.js',
    'app/shared/components/fs-table/fs-table-actions/fs-table-actions.directive.js',
    'app/shared/components/controls/fs-daterange/fs-daterange.directive.js',
    'app/shared/components/controls/fs-simple-tabs-strip/fs-simple-tabs-strip.directive.js',
    'app/shared/components/controls/fs-date-picker/fs-date-picker.module.js',
    'app/shared/components/controls/fs-date-picker/fs-date-picker.directive.js',
    'app/shared/components/fs-splitter/fs-splitter.directive.js',
    'app/shared/components/fs-multiselect/fs-multiselect.directive.js',
    'app/shared/components/fs-multiselect-autocomplete/fs-multiselect-autocomplete.directive.js',
    'app/shared/components/fs-severity-tag/fs-severity-tag.component.ts',
    'app/shared/components/fs-percentage-circle/fs-percentage-circle.component.ts',

    'app/shared/components/fs-nanobar/fs-nanobar.directive.ts',
    'app/shared/components/fs-score-icon/fs-score-icon.directive.js',
    'app/shared/components/fs-card-header/fs-card-header.component.ts',
    // shared/components/fs-chart
    'app/shared/components/fs-chart/fs-chart.module.js',
    'app/shared/components/fs-chart/settings/general-chart.settings.value.js',
    'app/shared/components/fs-chart/settings/pie-chart.settings.value.js',
    'app/shared/components/fs-chart/settings/heatmap-chart.settings.value.js',
    'app/shared/components/fs-chart/settings/column-chart.settings.value.js',
    'app/shared/components/fs-chart/settings/scatter-plot-chart.settings.value.js',
    'app/shared/components/fs-chart/settings/column-range-chart.settings.value.js',
    'app/shared/components/fs-chart/settings/line-chart.settings.value.js',
    'app/shared/components/fs-chart/chart-model-mapping.service.js',
    'app/shared/components/fs-chart/chart-settings.service.js',
    'app/shared/components/fs-chart/fs-chart.directive.js',
    // END of shared/components/fs-chart
    'app/shared/directives/fs-user-tag-details/fs-user-tag-details.directive.js',
    // shared/components/fs-resource-store
    'app/shared/directives/fs-resource-store/fs-resource-store.module.js',
    'app/shared/directives/fs-resource-store/fs-resource-store.directive.js',
    'app/shared/directives/fs-resource-store/fs-resource-store.provider.js',
    // END of shared/components/fs-resource-store

    // shared/components/fs-href-alert
    'app/shared/directives/fs-href-alert/fs-href-alert.directive.js',
    // END OF shared/components/fs-href-alert

    // shared/components/fs-flag
    'app/shared/components/fs-flag/fs-flag.module.js',
    'app/shared/components/fs-flag/fs-flag.directive.js',
    // END OF shared/components/fs-flag

    // shared/components/fs-header-bat
    'app/shared/components/fs-header-bar/fs-header-bar.component.ts',
    // END OF shared/components/fs-header-bat

    // shared/components/fs-loader
    'app/shared/components/fs-loader/fs-loader.component.ts',
    // END OF shared/components/fs-loader

    // shared/components/fs-svg-icon
    'app/shared/components/fs-svg-icon/fs-svg-icon.ts',
    // END OF shared/components/fs-svg-icon

    // shared/components/fs-alerts-tooltip
    'app/shared/components/fs-alerts-tooltip/fs-alerts-tooltip.directive.ts',
    // END OF fs-user-tooltip

    // shared/components/fs-user-devices-tooltip
    'app/shared/components/fs-user-devices-tooltip/fs-user-devices-tooltip.directive.ts',
    // END OF fs-user-tooltip

    // shared/components/fs-user-tooltip
    'app/shared/components/fs-user-tooltip/fs-user-tooltip.directive.ts',
    'app/shared/components/fs-text-tooltip/fs-text-tooltip.directive.ts',
    // END OF fs-user-tooltip

    // shared/components/fs-modal
    'app/shared/components/fs-modal/fs-modal.directive.ts',
    // shared/components/fs-indicators-tooltip
    'app/shared/components/fs-indicators-tooltip/fs-indicators-tooltip.directive.ts',
    // END OF fs-indicators-tooltip

    // shared/components/fs-tag
    'app/shared/components/fs-tag/fs-tag.component.ts',
    // END OF shared/components/fs-tag

    // shard/component/fs-table-scrollable
    'app/shared/components/fs-table-scrollable/fs-table-scrollable.component.ts',
    // END OF shard/component/fs-table-scrollable

    'app/shared/components/alert-feedback/alert-feedback-close-modal/alert-feedback-close-modal-controller.js',
    'app/shared/components/alert-feedback/alert-feedback-open-modal/alert-feedback-open-modal-controller.js',
    'app/shared/components/alert-feedback/alert-feedback-results-modal/alert-feedback-result-modal-controller.js',
    'app/shared/components/alert-feedback/alert-feedback-failure-modal/alert-feedback-failure-modal-controller.js',
    // shared/values
    'app/shared/values/indicator-severities.value.js',
    // END of shared/values

    // **gulp-scaffolds-inject** //DO NOT DELETE THIS LINE!

    // **END of gulp-scaffolds-inject** //DO NOT DELETE THIS LINE!
    // layouts

    'app/layouts/layouts.module.js',

    // layouts/alerts
    'app/layouts/alerts/alerts.controller.js',
    'app/layouts/alerts/alerts.resolve.js',
    'app/layouts/alerts/components/fs-alerts-record-menu/fs-alerts-record-menu.directive.js',
    // END OF layouts/alerts

    // layouts/user
    'app/layouts/user/user.module.ts',
    'app/layouts/user/user.route.ts',
    'app/layouts/user/classes/Activities.ts',
    'app/layouts/user/services/user-tags-utils.service.ts',
    'app/layouts/user/services/user-watch-util.service.ts',
    'app/layouts/user/services/user-indicators-utils.service.ts',
    'app/layouts/user/services/user-alerts-utils.service.ts',
    'app/layouts/user/services/indicator-chart-transition-util.ts',

    'app/layouts/user/components/user-alert-overview/services/indicator-symbol-map.service.ts',
    'app/layouts/user/components/user-upper-bar/user-upper-bar.component.ts',
    'app/layouts/user/components/user-profile/user-profile.component.ts',
    'app/layouts/user/components/user-tags/user-tags.component.ts',
    'app/layouts/user/components/user-activity-countries/user-activity-countries.component.ts',
    'app/layouts/user/components/user-activity-authentication/user-activity-authentication.component.ts',
    'app/layouts/user/components/user-activity-classification-exposure/user-activity-classification-exposure.component.ts',
    'app/layouts/user/components/user-activity-working-hours/user-activity-working-hours.component.ts',
    'app/layouts/user/components/user-activity-devices/user-activity-devices.component.ts',
    'app/layouts/user/components/user-activity-data-usage/user-activity-data-usage.component.ts',
    'app/layouts/user/components/user-activity-top-applications/user-activity-top-applications.component.ts',
    'app/layouts/user/components/user-activity-top-directories/user-activity-top-directories.component.ts',
    'app/layouts/user/components/user-activity-top-recipients-domains/user-activity-top-recipients-domains.component.ts',
    'app/layouts/user/components/user-risk-score/user-risk-score.component.ts',
    'app/layouts/user/components/user-indicator/components/user-indicator/user-indicator.component.ts',
    'app/layouts/user/components/user-indicator/components/user-indicator-header/user-indicator-header.controller.ts',
    'app/layouts/user/components/user-indicator/components/user-indicator-description/user-indicator-description.component.ts',
    'app/layouts/user/components/user-indicator/components/user-indicator-events/user-indicator-events.component.ts',
    'app/layouts/user/components/user-indicator/components/user-indicator-charts/user-indicator-charts.component.ts',
    'app/layouts/user/components/user-indicator/components/fs-indicator-am-chart/fs-indicator-am-chart.component.ts',
    'app/layouts/user/components/user-indicator/components/fs-indicator-am-geo-location/fs-indicator-am-geo-location.component.ts',
    'app/layouts/user/components/user-indicator/components/fs-indicator-activity-time-anomaly/fs-indicator-activity-time-anomaly.component.ts',
    'app/layouts/user/components/user-alert-overview/components/user-alert-overview-log-activities/user-alert-overview-log-activities.component.ts',
    'app/layouts/user/components/user-alert-overview/components/user-alert-single-comment/user-alert-single-comment.component.ts',
    'app/layouts/user/components/user-alert-overview/components/user-alert-feedback-log-activity/user-alert-feedback-log-activity.component.ts',
    'app/layouts/user/components/user-alert-overview/components/user-alert-overview-feedback/user-alert-overview-feedback.component.ts',
    'app/layouts/user/components/user-alert-overview/components/user-alert-overview-description/user-alert-overview-description.component.ts',
    'app/layouts/user/components/user-alert-overview/components/user-alert-overview-header/user-alert-overview-header.component.ts',
    'app/layouts/user/components/user-alert-overview/components/user-alert-flow/user-alert-flow.component.ts',
    'app/layouts/user/user.controller.ts',
    'app/layouts/user/components/user-attributes/user-attributes.controller.ts',
    'app/layouts/user/components/user-alert-overview/user-alert-overview.controller.ts',
    'app/layouts/user/components/user-indicator/user-indicator.controller.ts',
    // END OF layouts/user

    // layouts/users
    'app/layouts/users/users.module.ts',


    'app/layouts/users/services/users-state-types.ts',
    'app/layouts/users/services/convert-users-state-utils.service.ts',

    'app/layouts/users/services/users-utils.service.ts',
    'app/layouts/users/components/users-search-popup/users-search-popup.controller.ts',
    'app/layouts/users/components/users-severities-stacked-bar/users-severities-stacked-bar.component.ts',
    'app/layouts/users/components/users-action-bar/users-action-bar.component.ts',
    'app/layouts/users/components/users-tag-all-popup/users-tag-all-popup.controller.ts',
    'app/layouts/users/components/users-grid/users-grid.component.ts',
    'app/layouts/users/components/user-grid-row/user-grid-row.component.ts',
    'app/layouts/users/components/users-filters/users-filters.component.ts',
    'app/layouts/users/components/users-predefined-filter/users-predefined-filter.component.ts',
    'app/layouts/users/components/users-filter-in-use/users-filter-in-use.component.ts',
    'app/layouts/users/components/users-save-favorites-filter/users-save-favorites-filter.component.ts',
    'app/layouts/users/users.controller.ts',
    // END OF layouts/users

    // layouts/overview
    'app/layouts/overview/overview.module.ts',
    'app/layouts/overview/overview.route.ts',
    // layouts/overview/services
    'app/layouts/overview/services/high-risk-users-utils.service.ts',
    'app/layouts/overview/services/top-alerts-utils.service.ts',
    'app/layouts/overview/services/alert-stats-utils.ts',
    // END OF layouts/overview/services
    // layouts/overview/components
    'app/layouts/overview/components/high-risk-users/high-risk-users.component.ts',
    'app/layouts/overview/components/overview-alerts-severity-by-day/overview-alerts-severity-by-day.directive.ts',
    'app/layouts/overview/components/overview-alerts-status/overview-alert-status.directive.ts',
    'app/layouts/overview/components/overview-high-risk-user/overview-high-risk-user.component.ts',
    'app/layouts/overview/components/overview-top-alerts/overview-top-alerts.component.ts',
    'app/layouts/overview/components/overview-alert/overview-alert.component.ts',
    'app/layouts/overview/components/overview-users-tags-count/overview-users-tags-count.component.ts',
    // END OF layouts/overview/components
    'app/layouts/overview/overview.controller.ts',
    // END OF layouts/overview

    // layouts/reports
    'app/layouts/reports/reports.module.js',
    'app/layouts/reports/reports.route.js',
    'app/layouts/reports/reports.resolve.js',
    'app/layouts/reports/reports.controller.js',
    'app/layouts/reports/validators/fs-validator-ip.js',
    'app/layouts/reports/layouts/stale-accounts-monitoring/stale-accounts-monitoring.resolve.js',
    'app/layouts/reports/layouts/stale-accounts-monitoring/stale-accounts-monitoring.state.js',
    'app/layouts/reports/layouts/stale-accounts-monitoring/disabled-user-accounts.controller.js',
    'app/layouts/reports/layouts/stale-accounts-monitoring/inactive-user-accounts.controller.js',
    'app/layouts/reports/layouts/stale-accounts-monitoring/disabled-user-with-network.controller.js',
    'app/layouts/reports/layouts/stale-accounts-monitoring/terminated-user-with-network.controller.js',
    'app/layouts/reports/layouts/device-monitoring/device-monitoring.resolve.js',
    'app/layouts/reports/layouts/device-monitoring/device-monitoring.state.js',
    'app/layouts/reports/layouts/device-monitoring/ip-investigation.controller.js',
    'app/layouts/reports/layouts/device-monitoring/sensitive-resources-monitoring.controller.js',
    'app/layouts/reports/layouts/device-monitoring/suspicious-endpoint-access.controller.ts',
    'app/layouts/reports/layouts/suspicious-users/suspicious-users.controller.js',
    'app/layouts/reports/layouts/suspicious-users/suspicious-users.resolve.js',
    'app/layouts/reports/layouts/suspicious-users/suspicious-users.state.js',
    'app/layouts/reports/layouts/external-access-to-network/external-access-to-network.resolve.js',
    'app/layouts/reports/layouts/external-access-to-network/external-access-to-network.state.js',
    'app/layouts/reports/layouts/external-access-to-network/suspicious-vpn-data-amount.controller.js',
    'app/layouts/reports/layouts/external-access-to-network/vpn-geo-hopping.controller.js',
    'app/layouts/reports/components/fs-report-header.directive.js',
    // END of layouts/reports

    // layouts/configuration
    'app/layouts/configuration/configuration.module.ts',
    'app/layouts/configuration/services/configurationNavigation.service.ts',
    'app/layouts/configuration/services/configurationForm.service.ts',
    'app/layouts/configuration/services/configurationDecorator.service.ts',
    'app/layouts/configuration/configuration.controller.ts',
    'app/layouts/configuration/configuration-form.controller.ts',
    'app/layouts/configuration/components/fs-config-affected-items.directive.js',
    // layouts/configuration/decorators
    'app/layouts/configuration/decorators/log-email/log-email.decorator.ts',
    'app/layouts/configuration/decorators/system-email/system-email.decorator.ts',
    'app/layouts/configuration/decorators/system-siem/system-siem.decorator.ts',
    'app/layouts/configuration/decorators/system-alerts-email/system-alerts-email.decorator.ts',
    'app/layouts/configuration/decorators/system-syslog-forwarding/system-syslog-forwarding.decorator.ts',
    // layouts/configuration/renderers
    'app/layouts/configuration/renderers/ConfigurationRenderer.class.ts',
    'app/layouts/configuration/renderers/fs-config-renderer.directive.ts',
    'app/layouts/configuration/renderers/integer/integer.renderer.ts',
    'app/layouts/configuration/renderers/string/string.renderer.ts',
    'app/layouts/configuration/renderers/password/password.renderer.ts',
    'app/layouts/configuration/renderers/ip/ip.renderer.ts',
    'app/layouts/configuration/renderers/severity/severity.renderer.ts',
    'app/layouts/configuration/renderers/checkbox/checkbox.renderer.ts',
    'app/layouts/configuration/renderers/drop-down/drop-down.renderer.ts',
    'app/layouts/configuration/renderers/boolean/boolean.renderer.ts',
    'app/layouts/configuration/renderers/users-list/users-list.renderer.ts',
    'app/layouts/configuration/renderers/alerts-mail/fs-config-renderer-alerts-mail.directive.js',
    'app/layouts/configuration/renderers/forward-historical-alerts/fs-config-renderer-forward-historical-alerts.directive.js',
    'app/layouts/configuration/renderers/test-email/test-email.renderer.template.ts',
    'app/layouts/configuration/renderers/active-directory/fs-config-renderer-active-directory.directive.js',
    // layouts/configuration/layout-components
    'app/layouts/configuration/layout-components/batch-alert-forwading/batch-alert-forwarding.ts',
    // END OF layouts/configuration

    // layouts/pxgrid-configuration

    // END OF layouts/pxgrid-configuration
    'app/layouts/pxgrid-configuration/pxgrid-configuration.module.js',
    'app/layouts/pxgrid-configuration/pxgrid-configuration.controller.js',
    'app/layouts/pxgrid-configuration/pxgrid-configuration.route.js',
    // END OF layouts/pxgrid-configuration

    // layouts/analytics
    'app/analytics/fs-analytics.module.js',
    'app/analytics/services/fs-uuid.js',
    'app/analytics/services/fs-analytics.js',
    // END OF layouts/analytics

    // END OF layouts

], attachSrcDir);

/**
 * Signin
 */
config.srcCssSigninVendors = _.map([], attachSrcLibsDir);
config.srcScssSignin = config.srcStylesDir + '/signin.scss';
config.srcSigninVendorScripts = [
    'jquery/jquery-2.2.1.js',
    'angular/angular.js'
];
config.srcSigninAppScripts = _.map([
    'scripts/directives/focusWhen.js',
    'scripts/modules/auth.js',
    'scripts/modules/config.js',
    'scripts/modules/utils.js',
    'scripts/signin.js',
    'scripts/controllers/signin_controller.js'
], attachSrcDir);

/**
 * Admin
 */
config.srcCssAdminVendors = _.map([
    'bootstrap/css/bootstrap.min.css',
    'bootstrap/bootstrap-overrides.css',
    'detail-wrap/css/layout.css',
    'detail-wrap/css/elements.css',
    'detail-wrap/css/icons.css',
    'detail-wrap/css/signin.css',
    'font-awesome/css/font-awesome.min.css'
], attachSrcLibsDir);

config.srcScssAdmin = config.srcStylesDir + '/signin.scss';
config.srcAdminVendorScripts = [
    'jquery/jquery-2.2.1.js',
    '/bootstrap/js/bootstrap.min.js',
    '/detail-wrap/scripts/theme.js',
    '/angular/angular.js'
];
config.srcAdminAppScripts = _.map([
    'scripts/modules/auth.js',
    'scripts/modules/config.js',
    'scripts/modules/utils.js',
    'scripts/admin.js',
    'scripts/controllers/admin_controller.js'
], attachSrcDir);

/**
 * Change-Password
 */
config.srcCssChangePasswordVendors = _.map([], attachSrcLibsDir);
config.srcScssChangePassword = config.srcStylesDir + '/signin.scss';
config.srcChangePasswordVendorScripts = [
    'jquery/jquery-2.2.1.js',
    'angular/angular.js'
];
config.srcChangePasswordAppScripts = _.map([
    'scripts/directives/focusWhen.js',
    'scripts/modules/auth.js',
    'scripts/modules/config.js',
    'scripts/modules/utils.js',
    'scripts/signin.js',
    'scripts/controllers/signin_controller.js'
], attachSrcDir);


module.exports = config;

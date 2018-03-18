(function () {
    'use strict';

    function TableDataViewClass (DataView, Widget, utils, dataEntityFieldTypes, state, menus, dataEntities, Report) {
        // This next code is scary! We should definitely look into it when we have time, and kick the use of __proto__.
        // TODO!!
        /* jshint ignore:start */
        TableDataView.prototype.__proto__ = DataView;
        /* jshint ignore:end */


        var DEFAULT_PAGE_SIZE = 20;
        var pageSizes = [10, 20, 50, 100];
        var paramNames = {
            PAGE: "tableview_page",
            PAGE_SIZE: "tableview_pagesize",
            FIELDS: "tableview_fields",
            SORT_FIELD: "tableview_sort",
            SORT_FIELD_DIR: "tableview_sort_dir"
        };
        var formatMap = {
            "minutes": ":diffToPrettyTime:minutes",
            "hours": ":diffToPrettyTime:hours",
            "seconds": ":diffToPrettyTime:seconds",
            "sizeBytes": ":bytesCount",
            "sizeBytesForTime": ":bytesPerSecCount"
        };

        function TableDataView (explore) {
            this.pageSize = DEFAULT_PAGE_SIZE;
            this.page = 1;

            this.init(explore);

            if (!this.selectedFields) {
                this.selectedFields = getTableFields(this);
            }

            if (!this.sort && explore.dataEntity.defaultSort && explore.dataEntity.defaultSort.length) {
                this.sort = explore.dataEntity.defaultSort[0];
            }

            this.widget = createWidget(this);
        }

        TableDataView.prototype.update = function () {
            if (this.widget) {
                this.widget.clearEventListeners();
            }

            this.widget = createWidget(this);
        };

        /**
         * Gets the table field for displaying the specified DataEntityField
         * @param dataEntityField
         * @returns {{name: *, field: *}}
         */
        function getTableFieldConfig (dataEntityField) {
            /* jshint validthis: true */
            var tableDataView = this;

            var fieldConfig = {
                name: dataEntityField.name,
                field: dataEntityField.id,
                noValueDisplay: "N/A"
            };

            var scoreField = dataEntityField.scoreField;
            if (!scoreField && /_score$/.test(dataEntityField.id)) {
                scoreField = dataEntityField;
            }

            // For fields with score
            if (scoreField) {
                // Add icon
                fieldConfig.icon = {
                    "preset": "scoreBox",
                    "presetParams": {
                        "value": scoreField.id
                    },
                    "style": "score",
                    "styleParams": {
                        "value": scoreField.id
                    }
                };
                // add tooltip
                var suffix = "Score";
                var toolTipText = fieldConfig.name;
                //if field name does not end with the suffix "Score" - add it
                if (fieldConfig.name.indexOf(suffix,
                        fieldConfig.name.length - suffix.length) === -1) {
                    toolTipText += " " + suffix;
                }
                toolTipText += ": {{" + scoreField.id + ":toFixed:2}}";
                fieldConfig.valueTooltip = toolTipText;
            }

            // If field format is set, use it
            if (dataEntityField.format && formatMap[dataEntityField.format]) {
                fieldConfig.value = "{{" + dataEntityField.id + formatMap[dataEntityField.format] + "}}";
                delete fieldConfig.field;
            }
            // Otherwise, if a time field, parse it as UTC (no timezone offset)
            else if (dataEntityField.type === dataEntityFieldTypes.date_time ||
                dataEntityField.type === dataEntityFieldTypes.timestamp) {
                fieldConfig.value = "{{" + dataEntityField.id + ":date}}";
                delete fieldConfig.field;
            }

            fieldConfig.sortBy = tableDataView.getFieldTableDataViewId(dataEntityField);

            if (fieldConfig.sortBy === tableDataView.sort.field) {
                fieldConfig.sortDirection = tableDataView.sort.direction === "ASC" ? 1 : -1;
            }

            fieldConfig.menu = {
                items: [],
                "params": {
                    "value": "{{" + dataEntityField.id + "}}",
                    "displayValue": "{{" + dataEntityField.id + formatMap[dataEntityField.format] + "}}",
                    "fieldId": dataEntityField.id,
                    "entityId": dataEntityField.entity.id
                }
            };

            // If not view-only - add the filters to the menu
            if (!tableDataView.explore.viewOnly) {
                //when filter exists in default filters we do not add it to the dropdown filters
                if (tableDataView.explore.defaultFilterFieldIds === null ||
                    tableDataView.explore.defaultFilterFieldIds.indexOf(dataEntityField.id) < 0) {

                    //menu items should be treated the same as field items
                    var valueStr = 'displayValue || value';
                    if (dataEntityField.type === dataEntityFieldTypes.date_time ||
                        dataEntityField.type === dataEntityFieldTypes.timestamp) {
                        valueStr = valueStr + ":date";
                    }
                    fieldConfig.menu.items.push(
                        {
                            text: "Add filter: " + dataEntityField.name + " = '{{" + valueStr + "}}'",
                            "onSelect": {
                                "action": "setParams",
                                "actionOptions": {
                                    "addToParam": true,
                                    "updateUrl": true,
                                    "params": {
                                        "filters": "{{entityId}}.{{fieldId}}={{value}}"
                                    }
                                }
                            }
                        });
                    fieldConfig.menu.items.push(
                        {
                            text: "Clear all filters and create: " + dataEntityField.name + " = '{{" + valueStr + "}}'",
                            "onSelect": {
                                "action": "setParams",
                                "actionOptions": {
                                    "updateUrl": true,
                                    "params": {
                                        "filters": "{{entityId}}.{{fieldId}}={{value}}"
                                    }
                                }
                            }
                        }
                    );
                }
            }

            if (dataEntityField.attributes) {
                dataEntityField.attributes.forEach(function (attribute) {
                    menus.getMenuById(attribute).then(function (menu) {
                        fieldConfig.menu.items = fieldConfig.menu.items.concat(menu.items);
                    });
                });
            }
            if (dataEntityField.tags) {
                dataEntityField.tags.forEach(function (tag) {
                    fieldConfig.tags = {"name": tag};
                });
            }
            return fieldConfig;
        }

        function getTableSettings (tableDataView) {
            return {
                "sortParam": tableDataView.getParamName(paramNames.SORT_FIELD),
                "fields": tableDataView.selectedFields.map(getTableFieldConfig.bind(tableDataView))
            };
        }

        /*
         * Returns the default parameters for a Table Data View.
         */
        TableDataView.prototype.getDefaultParams = function () {
            var defaults = {};

            // Page number
            defaults[this.getParamName(paramNames.PAGE)] = 1;
            // Rows per page
            defaults[this.getParamName(paramNames.PAGE_SIZE)] = DEFAULT_PAGE_SIZE;
            // Table columns
            var tableFields = getTableFields(this);
            for (var i = 0; i < tableFields.length; i++) {
                tableFields[i] = this.getFieldTableDataViewId(tableFields[i]);
            }
            defaults[this.getParamName(paramNames.FIELDS)] = tableFields.join(",");

            return defaults;
        };

        TableDataView.prototype.setParams = function (params, updateOnChange) {
            function filterFieldIds (field) {
                /* jshint validthis:true */
                return ~fieldIds.indexOf(this.getFieldTableDataViewId(field));
            }

            var changed = false;

            var pageSize = params[this.getParamName(paramNames.PAGE_SIZE)],
                page = params[this.getParamName(paramNames.PAGE)],
                fieldIds = params[this.getParamName(paramNames.FIELDS)],
                sortField = params[this.getParamName(paramNames.SORT_FIELD)],
                sortFieldDir = params[this.getParamName(paramNames.SORT_FIELD_DIR)];

            if (pageSize !== undefined) {
                pageSize = Number(pageSize);
                if (pageSize !== this.pageSize) {
                    this.pageSize = pageSize;
                    if (isNaN(this.pageSize)) {
                        this.pageSize = DEFAULT_PAGE_SIZE;
                    }

                    this.resetPaging();
                    changed = true;
                }
            }

            if (page !== undefined) {
                page = Number(page);
                if (this.page !== page) {
                    this.page = page;
                    if (isNaN(this.page)) {
                        this.resetPaging();
                    }

                    changed = true;
                }
            }

            if (sortField || sortFieldDir) {
                if (!this.sort) {
                    this.sort = {};
                }

                if (sortField) {
                    this.sort.field = sortField;
                }

                if (sortFieldDir) {
                    this.sort.direction = sortFieldDir === -1 || sortFieldDir === "-1" ? "DESC" : "ASC";
                }

                changed = true;
            }

            if (fieldIds !== undefined) {
                if (fieldIds === null) {
                    this.selectedFields = null;
                }

                fieldIds = fieldIds ? fieldIds.split(",") : [];
                this.selectedFields = this.explore.dataEntity.fieldsArray.filter(function (field) {
                    return ~fieldIds.indexOf(this.getFieldTableDataViewId(field));
                }.bind(this));


                if (this.selectedFields.length < fieldIds.length && this.explore.dataEntity.linkedEntities) {
                    for (var linkedEntity, i = 0; i < this.explore.dataEntity.linkedEntities.length &&
                    this.selectedFields.length < fieldIds.length; i++) {
                        linkedEntity = dataEntities.getEntityById(this.explore.dataEntity.linkedEntities[i].entity);
                        this.selectedFields =
                            this.selectedFields.concat(linkedEntity.fieldsArray.filter(filterFieldIds.bind(this)));
                    }
                }

                if (!this.selectedFields.length) {
                    this.selectedFields = getTableFields(this);
                }

                if (this.widget) {
                    this.widget.views[0].settings = getTableSettings(this);
                    this.widget.update();
                }
            }

            if (changed && updateOnChange !== false) {
                this.update();
            }

            return changed;
        };

        /**
         * Gets the ID of a field to use inside the tableDataView, in selected field IDs
         * If the field is not from the current explore's dataEntity, the ID is the full path (entityId.fieldId),
         * otherwise the fieldID only is used, for simplicity.
         * @param dataEntityField
         */
        TableDataView.prototype.getFieldTableDataViewId = function (dataEntityField) {
            if (dataEntityField.entity === this.explore.dataEntity) {
                return dataEntityField.id;
            }

            return dataEntityField.entity.id + "." + dataEntityField.id;
        };

        TableDataView.prototype.resetPaging = function () {
            this.page = 1;
            var pageParam = {};
            pageParam[this.getParamName(paramNames.PAGE)] = 1;
            state.setParams(pageParam);
        };

        function getControlsTitle (tableDataView) {
            var firstResultIndex = (tableDataView.page - 1) * tableDataView.pageSize + 1;
            return "Displaying " + firstResultIndex + " - {{total:min:" +
                (tableDataView.page * tableDataView.pageSize) + "}} of {{total}} results";
        }

        function createWidget (tableDataView) {

            function onStateChange (e, data) {
                tableDataView.setParams(data.params, true);
            }

            function runExport () {
                var reportConfig = createReport(tableDataView, "exportEvents");
                var report = new Report(reportConfig);
                report.openInIframe = true;
                report.options = {};

                //in case of using time zone shifting (in the feature we want to able to support UTC and local time )
                //report.options.timezoneOffsetMins=utils.date.timezone * 60;

                //for 1.2.0 we don't want to support time zone shifting
                report.options.timezoneOffsetMins = 0;

                // In case the user selected specific fields, we want to send them to the server in order to export
                // only them
                if (state && state.currentParams && state.currentParams.tableview_fields) {
                    report.options.returnFields = state.currentParams.tableview_fields;
                }
                report.run({});
            }

            var widgetConfig = {
                report: createReport(tableDataView),
                flags: utils.objects.extend(tableDataView.getWidgetFlags(), {
                    stretchVertically: true
                }),
                title: getControlsTitle(tableDataView),
                noDataTitle: "No results found",
                loadingTitle: "Loading Results...",
                "controls": [
                    {
                        "type": "multiSelect",
                        "label": "Fields",
                        "param": tableDataView.getParamName(paramNames.FIELDS),
                        "value": tableDataView.selectedFields.map(function (field) {
                            return tableDataView.getFieldTableDataViewId(field);
                        }),
                        "autoUpdate": true,
                        "settings": {
                            options: getFieldSelectionOptions(tableDataView),
                            maxLabels: 3,
                            buttonText: "Select fields <span class='caret'></span>"
                        }
                    },
                    {
                        "type": "select",
                        "label": "Rows per page",
                        "param": tableDataView.getParamName(paramNames.PAGE_SIZE),
                        "value": tableDataView.pageSize ? tableDataView.pageSize.toString() :
                        state.currentParams[tableDataView.getParamName(paramNames.PAGE_SIZE)] ||
                        DEFAULT_PAGE_SIZE.toString(),
                        "settings": {
                            "options": pageSizes
                        },
                        "autoUpdate": true,
                        "isRequired": true
                    },
                    {
                        "type": "simplePagination",
                        "value": tableDataView.page,
                        "settings": {
                            "pageSize": tableDataView.pageSize
                        },
                        "autoUpdate": true,
                        "param": tableDataView.getParamName(paramNames.PAGE)
                    }
                ],
                views: [
                    {
                        "type": "table",
                        "settings": getTableSettings(tableDataView)
                    }
                ]
            };

            if (tableDataView.explore.includeExport) {
                widgetConfig.buttons = [
                    {
                        "text": "Export",
                        "icon": "#download-icon",
                        "onClick": runExport
                    }
                ];
            }

            var widget = new Widget(widgetConfig, tableDataView.explore);
            widget.onStateChange.subscribe(onStateChange);
            widget.clearEventListeners = function () {
                widget.onStateChange.unsubscribe(onStateChange);
            };

            return widget;


            // clicking on the "export" button will send a report to the exportEvent API and download a file into the
            // iframe
        }

        function getFieldSelectionOptions (tableDataView) {
            var fieldsSelection = tableDataView.explore.dataEntity.fieldsArray
                .filter(function (field) {
                    // filter out internal fields (according to their attributes)
                    return !(~field.attributes.indexOf("internal"));
                })
                .map(getFieldSelection.bind(tableDataView));

            if (tableDataView.explore.dataEntity.linkedEntities) {
                tableDataView.explore.dataEntity.linkedEntities.forEach(function (linkedEntity) {
                    var entity = dataEntities.getEntityById(linkedEntity.entity);
                    fieldsSelection = fieldsSelection.concat(
                        entity.fieldsArray
                            .filter(function (field) {
                                // filter out internal fields (according to their attributes) or if thie mark to be
                                // oonly shown for linked entity and also field that specific to not the main entity
                                return !((~field.attributes.indexOf("internal")) ||
                                ( field.shownForSpecificEntity !== undefined &&
                                field.entity.id === field.shownForSpecificEntity) );
                            })
                            .map(getFieldSelection.bind(tableDataView))
                    );
                });
            }

            return fieldsSelection;
        }

        function getFieldSelection (field) {
            /* jshint validthis: true */
            return {
                value: this.getFieldTableDataViewId(field),
                label: "<strong>" + field.entity.name + " <i class='icon-angle-right'></i></strong> " + field.name
            };
        }

        function createReport (tableDataView, api) {
            var dataQueryConfig = utils.objects.extend({
                fields: getReportFields(tableDataView),
                entity: tableDataView.explore.dataEntity.id,
                limit: tableDataView.pageSize,
                offset: tableDataView.pageSize * (tableDataView.page - 1),
                sort: tableDataView.sort || tableDataView.explore.dataEntity.defaultSort
            }, tableDataView.explore.getFiltersDataQuery());

            // If a JOIN is required, need to get fields from the joined entities as well:
            if (!!(dataQueryConfig.entitiesJoin = tableDataView.getDataQueryJoin(dataQueryConfig))) {
                dataQueryConfig.fields = [
                    {
                        entity: tableDataView.explore.dataEntity.id,
                        allFields: true
                    }
                ].concat(dataQueryConfig.entitiesJoin.map(function (entityJoin) {
                        return {
                            entity: entityJoin.entity,
                            allFields: true
                        };
                    }));
            }

            var report = tableDataView.getDataQueryReport(dataQueryConfig, null, api);

            if (!tableDataView.widget || !tableDataView.report || tableDataView.widget.total === undefined) {
                report.endpoint.requestTotal = "true";
            }

            return report;
        }

        function getReportFields (tableDataView) {
            var reportEntitiesIndex = {},
                fields = [];

            tableDataView.selectedFields.forEach(function (field) {
                reportEntitiesIndex[field.entity.id] = true;
            });

            for (var entity in reportEntitiesIndex) {
                if (reportEntitiesIndex.hasOwnProperty(entity)) {
                    fields.push({
                        entity: entity,
                        allFields: true
                    });
                }
            }

            return fields;
        }

        function getTableFields (tableDataView) {
            return tableDataView.explore.dataEntity.fieldsArray.filter(function (field) {
                return field.isDefaultEnabled;
            });
        }

        return TableDataView;
    }

    TableDataViewClass.$inject =
        ["DataView", "Widget", "utils", "dataEntityFieldTypes", "state", "menus", "dataEntities", "Report"];

    angular.module("Explore.DataViews").factory("TableDataView", TableDataViewClass);
})();

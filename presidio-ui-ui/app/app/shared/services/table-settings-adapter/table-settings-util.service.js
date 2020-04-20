(function () {
    'use strict';

    function TableSettingsUtil () {}

    angular.extend(TableSettingsUtil.prototype, {

        /**
         * The default adapter object that holds general definitions.
         *
         * @private
         */
        _adapter: {
            types: {
                DATE_TIME: {
                    template: '{{ dataItem.date_time | date:\"MM/dd/yyyy HH\\:mm\\:ss\":\"UTC\"}}'
                }
            },
            ids: {
                status: {
                    'template': "{{dataItem.status }}",
                    'attributes': {
                        'class': 'capitalizeText'
                    }
                },
                write_bytes: {
                    template: '{{dataItem.write_bytes|prettyBytes}}'
                },
                read_bytes: {
                    template: '{{dataItem.read_bytes|prettyBytes}}'
                },
                session_score: {
                    template: '<fs-score-icon score="dataItem.session_score"></fs-score-icon>' +
                    '{{dataItem.session_score}}'
                },
                event_score: {
                    template: '<fs-score-icon score="dataItem.event_score"></fs-score-icon>' +
                    '{{dataItem.event_score}}'
                },
                country: {
                    template: '{{ dataItem.country|orNA}}'
                },
                city: {
                    template: '{{ dataItem.city|orNA}}'
                },
                source_machine: {
                    template: '{{ dataItem.source_machine|orNA}}'
                }
            }
        },

        /**
         * Takes (optionally) an adapter and returns an adapter that is the default adapter
         * merged with the provided adapter.
         *
         * @param {object=} adapter
         * @returns {object}
         * @private
         */
        _getAdapterObject: function (adapter) {
            return adapter ? _.merge({types: {}, ids: {}}, adapter, this._adapter) :
                this._adapter;
        },
        /**
         * Returns a specific adapter property based on the adapterType, the property and the
         * adapterObject
         *
         * @param {string} adapterType Should generally be 'types' or 'ids'
         * @param {string} prop The property name correlating to the desired adapter
         * @param {object} adapter The adapter object
         * @returns {object|null} Returns null if the property does not exists on the adapterType,
         * or the adapterType does not exist on the adapter.
         * @private
         */
        _getAdapter: function (adapterType, prop, adapter) {
            return (adapter[adapterType] && adapter[adapterType][prop]) || null;
        },

        /**
         * Takes a title and makes sure that every two words there's a line break.
         * @param title
         * @returns {*}
         * @private
         */
        _processTitle: function (title) {

            if (!_.isString(title) || title === '') {
                return title;
            }

            // if title is string and not empty
            // split by space
            var titleNodes = title.split(' ');

            // remove empty strings
            titleNodes = _.filter(titleNodes, titleNode => titleNode !== '');

            // iterate through strings and for each even concat with space, for each odd concat with <br>
            var processedTitle = '';
            _.each(titleNodes, (titleNode, index) => {
                if (index === titleNodes.length - 1) {
                    processedTitle += titleNode;
                } else if (index % 2 === 0) {
                    processedTitle += titleNode + ' ';
                } else {
                    processedTitle += titleNode + '<br>';
                }
            });

            // return string
            return processedTitle;
        },


        /**
         * Takes an entity field and an adapter and returns a single column definition
         *
         * @param {{name: string, id: string, type: {id: string}}} entityField
         * @param {object} adapter An adapter object
         * @returns {object} returns a column definition object
         * @private
         */
        _getColumnByEntityField: function (entityField, adapter) {

            // Define title and field
            var title = entityField.name;
            var field = entityField.id;

            // Create a basic column def object
            var columnDef = {
                title: title,
                field: field
            };

            // Get adapters (this is contingent on the existence of adapters on the type or id)
            var adapterByType = this.getAdapterByType(entityField.type.id, adapter);
            var adapterById = this.getAdapterById(entityField.id, adapter);

            // Merge type adapter if exists into the columnDef
            if (adapterByType) {
                _.merge(columnDef, adapterByType);
            }

            // Merge id adapter if exists into the columnDef
            if (adapterById) {
                _.merge(columnDef, adapterById);
            }


            // Add score icon
            if (entityField.scoreField) {

                var template = '<fs-score-icon score="::dataItem.' +
                    entityField.scoreField.id + '"></fs-score-icon>';

                if (columnDef.template) {
                    columnDef.template = template + columnDef.template;
                } else {
                    columnDef.template = template + '{{::dataItem.' + columnDef.field + '}}';
                }
            }

            columnDef.title = this._processTitle(columnDef.title);

            if (_.isNil(adapter.sortable)){
                columnDef.sortable = false;
            }
            return columnDef;
        },

        /**
         * Takes a type value and returns adapter.types[type] or null
         *
         * @param {string} type The property name on adapter.types
         * @param {object} adapter The adapter object
         * @returns {Object|null}
         */
        getAdapterByType: function (type, adapter) {

            // All types should be upper cased.
            type = type && type.toUpperCase();

            return this._getAdapter('types', type, adapter);
        },

        /**
         *
         * @param {string} id the property name on adapter.ids or null
         * @param {object} adapter
         * @returns {Object|null}
         */
        getAdapterById: function (id, adapter) {
            return this._getAdapter('ids', id, adapter);
        },

        /**
         * Returns a columns definition object. It takes an entityFields array,
         * and uses it to build the columns definition.
         *
         * @param {Array<{name: string, id: string, type: {id: string}}>} entityFields
         * @param {object=} adapter An optional adapter object that is different from the default
         * service adapter.
         * If adapter object is provided, it will be merged (override) into the default adapter.
         */
        getColumnsByEntityFields: function (entityFields, adapter) {
            var self = this;

            // Get adapter object (merge if provided with default adapter)
            adapter = self._getAdapterObject(adapter);

            // Return a columns settings map
            return _.map(entityFields, function (entityField) {
                return self._getColumnByEntityField(entityField, adapter);
            });
        }
    });

    TableSettingsUtil.$inject = [];

    angular.module('Fortscale.shared.services.tableSettingsUtil', [])
        .service('tableSettingsUtil', TableSettingsUtil);
}());

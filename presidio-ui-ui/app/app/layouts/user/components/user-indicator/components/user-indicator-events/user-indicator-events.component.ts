module Fortscale.layouts.user {

    import INanobarAutomationService = Fortscale.shared.services.fsNanobarAutomation.INanobarAutomationService;
    import IHttpPromiseCallbackArg = angular.IHttpPromiseCallbackArg;

    interface IEventsTableState {
        page:number,
        pageSize:number,
        sortDirection:string,
        sortBy:string,
    }

    class UserIndicatorEventsController {

        indicator:any;
        events:any[];
        state:{
            eventsTable: IEventsTableState
        };
        tableSettings:any;
        tableAdapter:{ ids:{[anomalyType:string]:{field:string, template:string, sortable:boolean}}};
        tableModel:any;
        fetchTableState: Function;
        updateTableState: Function;
        NANOBAR_ID = 'user-page';


        /**
         * This method fetch the score field of the events dataEntity
         * The score field if configured on the dataEntity metadata
         * under the "performanceField" details. (Shay Schwartz)
         * @param entityId
         * @returns {*}The id of the score field.
         * @private
         */
        _getDefaultScoreField (entityId):string {
            let dataEntityId = this.indicator.dataEntitiesIds[0];
            if (!dataEntityId) {
                return null;
            }

            let entityMetadata = this.dataEntities.getEntityById(dataEntityId);

            if (!entityMetadata) {
                return null;
            }

            var performanceField = entityMetadata.performanceField;
            if (!performanceField) {
                return null;
            }

            return performanceField.field.id || null;
        }

        /**
         * Load events and digest response
         * @returns {IPromise<TResult>}
         * @private
         */
        _loadEvents ():ng.IPromise<void> {
            let httpPromise;
            httpPromise = this.$http.get(`${this.BASE_URL}/evidences/${this.indicator.id}/events`, {
                params: {
                    page: this.state.eventsTable.page || 1,
                    size: this.state.eventsTable.pageSize || 100,
                    sort_direction: this.state.eventsTable.sortDirection || 'DESC',
                    sort_field: this.state.eventsTable.sortBy
                }
            })
                .then((res:IHttpPromiseCallbackArg<any>) => {
                    this.events = res.data.data || [];
                    this.tableModel = this.events;
                    this.tableModel._meta = {
                        offset: res.data.offset || 0,
                        total: res.data.total || 0
                    };
                })
                .catch((err) => {
                    console.error('There was a problem loading events.', err);
                    this.events = [];
                    this.tableModel = this.events;
                    this.tableModel._meta = {
                        offset: 0,
                        total: 0
                    };
                });

            this.nanobarAutomationService.addPromise(this.NANOBAR_ID, httpPromise);
            return httpPromise;
        }

        /**
         * Returns the relevant entity fields for the data entity.
         * @param indicator
         * @returns {T[]}
         * @private
         */
        _getEntityFields (indicator):any[] {
            var dataEntityId = indicator.dataEntitiesIds[0];

            var fieldsArray = this.dataEntities.getEntityById(dataEntityId).fieldsArray;

            return _.filter(fieldsArray, {isDefaultEnabled: true});
        }

        /**
         * Initiates indicator watch. Once received it gets the 'sortBy' filed, the columns defs and loads the events.
         * @private
         */
        _initIndicatorWatch ():void {

            // deregister once an indicator is received. There should be only one indicator in the lifetime of this component.
            let deregister;
            deregister = this.$scope.$watch(
                () => this.indicator,
                () => {
                    if (this.indicator) {
                        // get default sort
                        this.state.eventsTable.sortBy =
                            this._getDefaultScoreField(this.indicator.dataEntitiesIds[0]);
                        this.state.eventsTable = _.clone(this.state.eventsTable);

                        // get columns definition
                        this.tableSettings.columns = this.tableSettingsUtil
                            .getColumnsByEntityFields(this._getEntityFields(this.indicator), this.tableAdapter);

                        // get events
                        this._loadEvents();

                        deregister();
                    }
                }
            );
        }



        /**
         * Initiates the controller's state
         * @private
         */
        _initStateObject () {
            this.state = {
                eventsTable: {
                    page: 1,
                    pageSize: 100,
                    sortBy: null,
                    sortDirection: 'DESC'
                }
            };
        }

        /**
         * Initiates a table settings
         * @private
         */
        _initTableSettings () {
            this.tableSettings = {
                scrollable: false,
                groupable: false,
                sortable: {
                    mode: 'single'
                },
                'alwaysPageable': true
            }
        }

        /**
         * Adds a table adapter
         * @private
         */
        _initTableAdapter () {
            this.tableAdapter = {
                ids: {
                    event_time: {
                        field: 'event_time',
                        sortable: false,
                        template: '{{ dataItem.event_time | date:\"MM/dd/yyyy HH\\:mm\\:ss\":\"UTC\"}}'

                    },
                    time_detected: {
                        field: 'time_detected',
                        template: '{{ dataItem.time_detected | date:\"MM/dd/yyyy HH\\:mm\\:ss\":\"UTC\"}}',
                        sortable: false

                    },
                    date: {
                        field: 'date',
                        template: '{{ dataItem.date | date:\"MM/dd/yyyy HH\\:mm\\:ss\":\"UTC\"}}',
                        sortable: false
                    },
                    start_time: {
                        field: 'start_time',
                        template: '{{ dataItem.start_time | date:\"MM/dd/yyyy HH\\:mm\\:ss\":\"UTC\"}}'
                        ,
                        sortable: false
                    },
                    end_time: {
                        field: 'end_time',
                        template: '{{ dtaItem.end_time | date:\"MM/dd/yyyy HH\\:mm\\:ss\":\"UTC\"}}',
                        sortable: false
                    },
                    duration: {
                        field: 'duration',
                        template: '{{ dataItem.duration | durationToPrettyTime}}',
                        sortable: false
                    },
                    data_bucket: {
                        field: 'data_bucket',
                        template: '{{dataItem.data_bucket | prettyBytes}}/s',
                        sortable: false

                    },
                    file_size: {
                        field: 'file_size',
                        template: '{{dataItem.file_size | prettyBytes}}/s',
                        sortable: false

                    },
                    attachment_file_size: {
                        field: 'attachment_file_size',
                        template: '{{dataItem.attachment_file_size | prettyBytes}}/s',
                        sortable: false
                    },
                    username:{
                        field: 'username',
                        template: '<fs-grid-link text="{{dataItem.username}}" url="{{dataItem.username_link}}"></fs-grid-link>',
                        sortable: false
                    },
                    full_name: {
                        field: 'full_name',
                        template: '{{dataItem.username}}',
                        sortable: false
                    },
                    user_sid: {
                        field: 'user_sid',

                        template: '<fs-grid-link text="{{dataItem.user_sid}}" url="{{dataItem.user_sid_link}}"></fs-grid-link>',
                        sortable: false
                    },
                    process_directory_groups: {
                        field: 'process_directory_groups',
                        template: '<span ng-repeat="processDirectoryGroup in dataItem.processDirectoryGroups">{{processDirectoryGroup}} </span>',
                        sortable: false
                    },
                    process_categories: {
                        field: 'process_categories',
                        template: '<span ng-repeat="processCategory in dataItem.processCategories">{{processCategory}} </span>',
                        sortable: false
                    },
                    computer: {
                        field: 'computer',
                        template: '{{dataItem.srcMachineId}}',
                        sortable: false
                    },
                    src_device_id: {
                        field: 'src_device_id',
                        template: '<fs-grid-link text="{{dataItem.srcMachineNameRegexCluster}}" url="{{dataItem.src_device_id_link}}"></fs-grid-link>',
                        sortable: false
                    },
                    user: {
                        field: 'user',

                        template: '<fs-grid-link text="{{dataItem.user}}" url="{{dataItem.user_link}}"></fs-grid-link>',
                        sortable: false
                    },
                    source_folder_path: {
                        field: 'source_folder_path',

                        template: '<fs-grid-link text="{{dataItem.source_folder_path}}" url="{{dataItem.source_folder_path_link}}"></fs-grid-link>',
                        sortable: false
                    },
                    file_name: {
                        field: 'file_name',

                        template: '<fs-grid-link text="{{dataItem.file_name}}" url="{{dataItem.file_name_link}}"></fs-grid-link>',
                        sortable: false
                    },
                    object_dn: {
                        field: 'object_dn',

                        template: '<fs-grid-link text="{{dataItem.object_dn}}" url="{{dataItem.object_dn_link}}"></fs-grid-link>',
                        sortable: false
                    },
                    machine_name: {
                        field: 'machine_name',

                        template: '<fs-grid-link text="{{dataItem.machine_name}}" url="{{dataItem.machine_name_link}}"></fs-grid-link>',
                        sortable: false
                    },
                    src_process: {
                        field: 'src_process',

                        template: '<fs-grid-link text="{{dataItem.src_process}}" url="{{dataItem.src_process_link}}"></fs-grid-link>',
                        sortable: false
                    },
                    dst_process: {
                        field: 'dst_process',

                        template: '<fs-grid-link text="{{dataItem.dst_process}}" url="{{dataItem.dst_process_link}}"></fs-grid-link>',
                        sortable: false
                    }
                }
            };
        }

        $onInit () {
            this._initIndicatorWatch();
        }




        static $inject = ['$scope','$location', 'fsNanobarAutomationService', 'dataEntities', '$http', 'BASE_URL',
            'tableSettingsUtil','utils'];

        constructor (public $scope:ng.IScope, public $location:ng.ILocationService ,public nanobarAutomationService:INanobarAutomationService,
            public dataEntities:any, public $http:ng.IHttpService, public BASE_URL:string,
            public tableSettingsUtil:any, public utils:any) {

            // The next two functions are on the constructor to lock the this.state
            var ctrl = this;

            /**
             * Returns the state of the table
             * @returns {IEventsTableState}
             */
            ctrl.fetchTableState = function ():IEventsTableState {
                return ctrl.state.eventsTable;
            };

            /**
             * Updates the state of the table and reloads events
             * @param state
             */
            ctrl.updateTableState = function (state: {
                id: string,
                    type: string,
                    value: IEventsTableState,
                    immediate: boolean}) {
                ctrl.state.eventsTable = state.value;
                ctrl._loadEvents();
            };

            this._initStateObject();
            this._initTableSettings();
            this._initTableAdapter();
        }
    }

    let UserIndicatorEventsComponent:ng.IComponentOptions = {
        controller: UserIndicatorEventsController,
        templateUrl: 'app/layouts/user/components/user-indicator/components/user-indicator-events/user-indicator-events.component.html',
        bindings: {
            indicator: '<',
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userIndicatorEvents', UserIndicatorEventsComponent);
}



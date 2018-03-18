module Fortscale.shared.components.fsActivityTimeAnomalyComponent {

    import INanobarAutomationService = Fortscale.shared.services.fsNanobarAutomation.INanobarAutomationService;
    declare var ActivityTimeAnomalys:any;

    interface ITimeAnomalyResponse {
        anomaly:boolean;
        value:number;
        keys:string[]
    }

    interface ITimeAnomaliesResponse {
        data:ITimeAnomalyResponse[]
        info:string;
        offset:number;
        total:number;
        warning:string;
    }

    interface IGridModel {
        [day: string]: {
            [hour: string]: {
                value: number,
                anomaly: boolean
            }
        }
    }

    class ActivityTimeAnomalyController {

        settings:any;
        indicator:any;
        containerElement:ng.IAugmentedJQuery;

        weekDaysUS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
        HoursInDay = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23];

        _response: ITimeAnomaliesResponse;

        _INDICATORS_DATA_PATH_NAME:string = 'evidences';
        _HISTORICAL_DATA_PATH_NAME:string = 'historical-data';
        _NANOBAR_ID:string = 'user-page';
        _ERR_MSG:string = 'ActivityTimeAnomalyComponent: ';
        _renderReady:boolean = false;
        gridModel: IGridModel;


        /**
         * Returns a promise that resolves on fetched data from the server
         * @returns {IHttpPromise<T>}
         * @private
         */
        _fetchData ():ng.IPromise<ITimeAnomaliesResponse> {
            let params = this.interpolation.interpolate(this.settings.params, this.indicator);
            return this.$http.get(
                `${this.BASE_URL}/${this._INDICATORS_DATA_PATH_NAME}/${this.indicator.id}/${this._HISTORICAL_DATA_PATH_NAME}`,
                {params: params})
                .then((res:ng.IHttpPromiseCallbackArg<ITimeAnomaliesResponse>):ITimeAnomaliesResponse => {
                    return res.data;
                });
        }

        _digestData (data: ITimeAnomalyResponse[]) {

            // sort by day of week and hour
            let sortedData = _.orderBy(data, [
                (timeAnomaly: ITimeAnomalyResponse) => {
                    // return index of day
                    return this.weekDaysUS.indexOf(timeAnomaly.keys[0]);
                },
                (timeAnomaly: ITimeAnomalyResponse) => {
                    // return hour as number
                    return parseInt(timeAnomaly.keys[1],10);
                }
            ], ['asc', 'asc']);

            _.each(sortedData, (timeAnomalyResponse: ITimeAnomalyResponse, index: number) => {
                if (!this.gridModel[timeAnomalyResponse.keys[0]]) {
                    this.gridModel[timeAnomalyResponse.keys[0]] = {};
                }

                // Create a cascading effect of painting the active hours.
                this.$timeout(() => {
                    this.gridModel[timeAnomalyResponse.keys[0]][timeAnomalyResponse.keys[1]] = {
                        value: timeAnomalyResponse.value,
                        anomaly: timeAnomalyResponse.anomaly
                    };
                }, index*40);



            });
        }


        /**
         * Renders a chart
         * @private
         */
        _renderGrid (): void {
            if (this._renderReady) {
                this._digestData(this._response.data);
            }
        }

        /**
         * Sets watch on settings. When settings received, data is fetched and processed, and chart is rendered.
         * @private
         */
        _initSettingsWatch () {
            this.$scope.$watch(
                () => this.settings,
                () => {
                    if (this.settings) {
                        this._renderReady = false;
                        let promise = this._fetchData()
                            .then((response:ITimeAnomaliesResponse) => {
                                this._renderReady = true;
                                this._response = response;
                                this._renderGrid();
                            })
                            .catch((err) => {
                                console.error(this._ERR_MSG + 'There was a problem loading data', err);
                            });

                        this.fsNanobarAutomationService.addPromise(this._NANOBAR_ID, promise);

                    }
                }
            );
        }


        $onInit () {
            this._initSettingsWatch();
        }


        static $inject = ['$scope', '$element', '$http', 'BASE_URL', 'interpolation', '$interpolate',
            'fsNanobarAutomationService', '$timeout'];

        constructor (public $scope:ng.IScope, public $element:ng.IAugmentedJQuery,
            public $http:ng.IHttpService, public BASE_URL:string, public interpolation:any,
            public $interpolate:ng.IInterpolateService, public fsNanobarAutomationService:INanobarAutomationService, public $timeout: ng.ITimeoutService) {

            this.gridModel = {};
        }
    }

    let ActivityTimeAnomalyComponent:ng.IComponentOptions = {
        controller: ActivityTimeAnomalyController,
        bindings: {
            settings: '<',
            indicator: '<'
        },
        templateUrl: 'app/layouts/user/components/user-indicator/components/fs-indicator-activity-time-anomaly/fs-indicator-activity-time-anomaly.component.html'
    };

    angular.module('Fortscale.shared.components')
        .component('fsIndicatorActivityTimeAnomaly', ActivityTimeAnomalyComponent);
}

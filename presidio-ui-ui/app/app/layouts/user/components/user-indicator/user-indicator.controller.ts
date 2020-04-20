module Fortscale.layouts.user {

    class userIndicatorViewController {

        alerts:any = null;
        alert:any = null;
        indicator:any = null;
        indicatorType:any = null;
        user: any;

        _getActiveAlert ():void {
            if (this.$stateParams.alertId) {
                this.alert = _.cloneDeep(_.find(this.alerts, {id: this.$stateParams.alertId}));
            }
        }

        _getActiveIndicator ():void {
            if (this.$stateParams.indicatorId && this.alert) {
                this.indicator = _.cloneDeep(_.find(this.alert.evidences, {id: this.$stateParams.indicatorId}));
            }
        }

        _getIndicatorType ():void {
            if (this.indicator) {
                this.indicatorType = this.indicatorTypeMapper.getType(this.indicator);
            }
        }

        _initAlertsWatch ():void {
            this.$scope.$watch(
                () => this.$scope.userCtrl.alerts,
                (alerts) => {
                    if (alerts) {
                        this.alerts = _.cloneDeep(alerts);
                        this._getActiveAlert();
                        this._getActiveIndicator();
                        this._getIndicatorType();
                    }
                }
            );
        }

        _initUserWatch ():void {
            this.$scope.$watch(
                () => this.$scope.userCtrl.user,
                (user) => {
                    if (user) {
                        this.user = _.cloneDeep(user);
                    }
                }
            );
        }


        _init () {
            this._initAlertsWatch();
            this._initUserWatch();

        }

        static $inject = ['$scope', '$stateParams', 'indicatorTypeMapper'];

        constructor (public $scope:any, public $stateParams:{alertId:string, indicatorId:string},
            public indicatorTypeMapper:any) {
            this._init();
        }
    }

    angular.module('Fortscale.layouts.user')
        .controller('userIndicatorController', userIndicatorViewController);
}

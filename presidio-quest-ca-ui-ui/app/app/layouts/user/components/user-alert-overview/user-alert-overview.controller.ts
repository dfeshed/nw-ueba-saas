module Fortscale.layouts.user {

    import IStateService = angular.ui.IStateService;
    import IToastrService = Fortscale.shared.services.toastrService.IToastrService;

    class userAlertOverviewViewController {



        alertId:String = null;
        userAlerts:any = null;
        userCtrl:any = null;

        updateFeedback:any;
        updateComment:any;
        deleteComment:any;
        addComment:any;
        analystMap:any = {};//Map the analyst name as retrived from the API to analyst object

        alerts:any = null;
        alert:any;

        /**
         * Extract the active alert from the state params, clone it, and store it
         * on the conrollter
         * @private
         */
        _getActiveAlert ():void {

            if (this._getCurrentAlertId()) {
                this.alert = _.cloneDeep(_.find(this.userAlerts, {id: this._getCurrentAlertId()}));
            }
        }

        /**
         * Extract all the alerts from user controller, clone it, and
         * save the cloned object on this controller
         * @private
         */
        _initAlertsWatch ():void {
            this.$scope.$watch(
                () => this.$scope.userCtrl.sortedAlerts,
                (alerts) => {
                    if (alerts && alerts.length) {
                        this.userAlerts = _.cloneDeep(alerts);
                        this._getActiveAlert();
                        this.page.setPageTitle(`${this.userAlerts[0].entityName} - Alert Overview`);
                    }
                }
            );
        }

        /**
         * When component loaded, we load a map from each analyst names as apear on the comment, to full display name.
         * @private
         */
        _initAnalystFullNames () {
            let ctrl:any = this;

            this.authService.getAllUsers().then((result)=> {
                _.forEach(result, function (value, key) {
                    ctrl.analystMap[value.emailAddress] = value.fullName;
                });
            })
                .catch((err) => {
                    this.toastrService.warning(
                        `Can't load analyst full names `);
                });

        }

        /**
         * Extract delegated methods from user controller and save it on this controller
         * @private
         */
        _initPopulatedFunctions (){
            this.updateComment = this.userCtrl.updateComment.bind(this.userCtrl);
            this.addComment = this.userCtrl.addComment.bind(this.userCtrl);
            this.deleteComment = this.userCtrl.deleteComment.bind(this.userCtrl);
            this.updateFeedback = this.userCtrl.updateFeedback.bind(this.userCtrl);
        }

        _getCurrentAlertId ():String{
            this.alertId = this.$stateParams.alertId

            if (_.isNil(this.alertId) && this.userAlerts.length>0){
                this.alertId = this.userAlerts[0].alertId;
            }

            let ctrl:userAlertOverviewViewController = this;
            if (!_.some(this.userAlerts, { 'id': this.alertId })){
                this.alertId = this.userAlerts[0].id;
            }

            if (this.alertId){
                this.$state.go('user.alert-overview', {userId: this.userCtrl.user.id, alertId:this.alertId});
            }
            return this.alertId;

        }

        _init () {
         //   this._initCurrentAlert();
            this._initAlertsWatch();
            this._initAnalystFullNames();
            this._initPopulatedFunctions();
        }

        static $inject = ['$scope', '$stateParams','auth', 'toastrService', 'page', '$state' ];

        constructor (public $scope:any, public $stateParams:{alertId:String},
                     public authService:any,  public toastrService:IToastrService, public page: any,public $state:IStateService) {
            this.alertId = $stateParams.alertId;
            this.userCtrl = $scope.userCtrl;
            this._init();
        }
    }

    angular.module('Fortscale.layouts.user')
        .controller('userAlertOverviewController', userAlertOverviewViewController);
}

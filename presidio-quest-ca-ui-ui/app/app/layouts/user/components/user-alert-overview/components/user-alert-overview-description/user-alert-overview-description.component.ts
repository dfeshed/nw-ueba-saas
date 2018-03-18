module Fortscale.layouts.user {

    import IUserAlertsUtilsService = Fortscale.layouts.user.IUserAlertsUtilsService;
    import IIndicator = Fortscale.shared.interfaces.IIndicator;

    class AlertFeedbackController {
        tagsPrettyNames:String;
        alert : any;

        /**
         * Extract the list of data sources from any of current alert's indicator
         * Return data sources names as string with separators
         * If the alert is still not loaded, return empty string
         * @returns {String}
         */
        getDataSources () : String{
            if (typeof this.alert==="undefined"){
                return "";
            }
            let evidencesList:IIndicator[] = this.alert.evidences;
            return this.userAlertsUtils.getDataSources(evidencesList);

        }

        /**
         * Extract the list of tags from any of current alert's indicator
         * Return tag names as string with separators
         * If the alert is still not loaded, return empty string
         * @returns {String}
         */
        getTags () : void{
            if (typeof this.alert==="undefined"){
                return;
            }
            //Because evidences list filtered to contain only the untags evidences,
            //The tags evidences stored in tagEvidences
            let tagEvidencesList:IIndicator[] = this.alert.tagEvidences;

            this.userAlertsUtils.getTags(tagEvidencesList).then((tagNames:string)=>{
                this.tagsPrettyNames = tagNames;
            });

        }

        /**
         * Read the description of the alert from messages.
         * @returns {any}
         */
        getAlertDescription(){
            if (typeof this.alert==="undefined"){
                return "";
            }
            // let showMore:boolean=false;
            // let alertDescription:String="";
            // let fullAlertDescription:String= this.userAlertsUtils.getAlertDescription(this.alert);
            // if (fullAlertDescription.length>550){
            //     alertDescription= fullAlertDescription.substring(0,1350);
            //     alertDescription+="...";
            //     showMore=true;
            // } else {
            //     alertDescription=fullAlertDescription;
            // }
            // return {
            //     showMore: showMore,
            //     shortDescription:alertDescription,
            //     fullDescription: fullAlertDescription
            //
            // };
            return this.userAlertsUtils.getAlertDescription(this.alert);
        }

        /**
         * Read the description of the alert from messages.
         * @returns {any}
         */
        getAlertHelp(){
            if (typeof this.alert==="undefined"){
                return "";
            }
            // let showMore:boolean=false;
            // let alertDescription:String="";
            // let fullAlertDescription:String= this.userAlertsUtils.getAlertDescription(this.alert);
            // if (fullAlertDescription.length>550){
            //     alertDescription= fullAlertDescription.substring(0,1350);
            //     alertDescription+="...";
            //     showMore=true;
            // } else {
            //     alertDescription=fullAlertDescription;
            // }
            // return {
            //     showMore: showMore,
            //     shortDescription:alertDescription,
            //     fullDescription: fullAlertDescription
            //
            // };
            return this.userAlertsUtils.getAlertRelatedText(this.alert);
        }

        /**
         * Extract all the alerts from user controller, clone it, and
         * save the cloned object on this controller
         * @private
         */
        _initAlertWatch ():void {
            this.$scope.$watch(
                () => this.alert,
                (alert) => {
                    if (alert && alert.tagEvidences) {
                        this.getTags();

                    }
                }
            );
        }

        $onInit () {
            this._initAlertWatch();
        }


        static $inject = ['$scope','userAlertsUtils','TAGS_FEATURE_ENABLED'];

        constructor (public $scope:ng.IScope, public userAlertsUtils:IUserAlertsUtilsService,public TAGS_FEATURE_ENABLED:string) {

        }
    }

    let alertFeedbackController:ng.IComponentOptions = {
        controller: AlertFeedbackController,
        templateUrl: 'app/layouts/user/components/user-alert-overview/components/user-alert-overview-description/user-alert-overview-description.component.html',
        bindings: {
            alert: '<alertModel'

        }
    };
    angular.module('Fortscale.layouts.user')
        .component('alertDescription', alertFeedbackController);
}

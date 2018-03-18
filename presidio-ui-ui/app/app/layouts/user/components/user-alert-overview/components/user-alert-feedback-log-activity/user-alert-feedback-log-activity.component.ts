module Fortscale.layouts.user {


    import IFeeadback = Fortscale.shared.services.alertUpdatesService.IFeeadback;
    import IAlertUpdatesService = Fortscale.shared.services.alertUpdatesService.IAlertUpdatesService;
    import IToastrService = Fortscale.shared.services.toastrService.IToastrService;

    //This copmonent display the history feedback changes
    class AlertSingleFeedbackController {

        analystMap:any = {}; //Analysts maps
        feedback:IFeeadback; //The choosen feedback to display
        description:string; //The localized description of the feedback name

        /**
         * Get the score number and return it without sign
         * @returns {number}
         */
        getAbsoluteScoreChange():number{
            return Math.abs(this.feedback.scoreDelta);
        }


        $onInit(){
            this._localizeFeedbackDescription();
        }

        /**
         * Get the localized description of the feedback
         * @private
         */
        _localizeFeedbackDescription():void{

            this.$translate('enum.alert_feedback.'+this.feedback.alertFeedback).then( (text)=> {
                this.description = text;
            }, (translationId)=> {
                this.description = translationId;
            });
        }

        static $inject = ['$translate'];

        constructor (public $translate:Function) {
        }
    }

    let alertSingleFeedbackController:ng.IComponentOptions = {
        controller: AlertSingleFeedbackController,
        templateUrl: 'app/layouts/user/components/user-alert-overview/components/user-alert-feedback-log-activity/user-alert-feedback-log-activity.component.html',
        bindings: {
            feedback: '<',
            analystMap: '<analystMap',//Map the analyst name as retrived from the API to analyst object
            last:'<' //Indicate if this is the last row

        }
    };
    angular.module('Fortscale.layouts.user')
        .component('alertFeedbackLogActivity', alertSingleFeedbackController);
}

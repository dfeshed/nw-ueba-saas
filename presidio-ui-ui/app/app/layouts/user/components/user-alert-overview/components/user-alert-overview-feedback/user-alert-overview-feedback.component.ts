module Fortscale.layouts.user {


    //Import API declaration for IComment, IAlertComments and IToastrService
    import IToastrService = Fortscale.shared.services.toastrService.IToastrService;
    import AlertFeedback = Fortscale.shared.services.alertUpdatesService.AlertFeedback;
    import AlertStatus = Fortscale.shared.services.alertUpdatesService.AlertStatus;
    import IAlertUpdatesService = Fortscale.shared.services.alertUpdatesService.IAlertUpdatesService;
    import FeedbackObject = Fortscale.shared.services.alertUpdatesService.FeedbackObject;

    enum ShrinkExpand {
        Shrink,
        Expand
    }


    class AlertFeedbackController {

        AlertFeedback=AlertFeedback;
        alert:any;
        updateFeedbackDelegate:any;

        //Constants for animation
        ACTUAL_RISK_UNCHECKED_SELECTOR:string = '.actual-risk-button-unchecked';
        ACTUAL_RISK_CHECKED_SELECTOR:string =   '.actual-risk-button-checked';

        NOT_RISK_UNCHECKED_SELECTOR:string =    '.not-a-risk-button-unchecked';
        NOT_RISK_CHECKED_SELECTOR:string =      '.not-a-risk-button-checked';
        ANIMATION_TIME:number = 500;


        /**
         * Method the change the feedback and status of the alert.
         * If the want'ed feedback is the same as current alert's feedback the method return the alert to "unresolved" feedback
         * and "open" status. If the feedback different the current alert's feedback- we set the new feedback and "closed" status.
         * @param clickedFeedbackStatus
         */
        setFeedback(clickedFeedbackStatus:AlertFeedback){

            let currentFeedback: any = AlertFeedback[this.alert.feedback];
            //Type script is not understand that AlertFeedback[this.alert.feedback] return AlertFeedback
            let newFeedback:AlertFeedback;
            let newStatus:AlertStatus;
            //If the users click again on already exists status, the alert will be changed to unresolved / open
            if(currentFeedback === clickedFeedbackStatus){
                newFeedback = AlertFeedback.None;
                newStatus = AlertStatus.Open;
            }  else {
                //The user selected different status then existing one,
                //we change the status to closed and send the new feedback
                newFeedback = clickedFeedbackStatus;
                newStatus = AlertStatus.Closed;
            }

            let feedbackObject:FeedbackObject = {
                "feedback" : newFeedback,
                "status" : newStatus
            };

            this.authService.getCurrentUser().then((result)=> {
                //Create the comment object
                let analystName = result.userName;
                this.alertUpdatesService.updateFeedback(this.alert.id, feedbackObject, analystName).then((response: any) => {
                    if (response.status === 200) { //Success
                        this.updateFeedbackDelegate({"alertId": this.alert.id, "feedbackObject": feedbackObject});
                    } else {
                        this.toastrService.error(
                            `There was an error trying to update alert feedback. `);
                    }
                })
                    .catch((err) => {
                        this.toastrService.error(
                            `There was an error trying to update alert feedback. `);
                    })
            });
        }


        /**
         * Animation methods
         * @type {string[]}
         */
        _shrinkElement (element) {
            element.css('transform', 'scaleY(0)');
        }

        _expandElement (element) {
            element.css('transform', 'scaleY(1)');
        }

        _setTransitions (shrinkElement, expandElement) {
            shrinkElement[0].style['WebkitTransition'] = `transform ${this.ANIMATION_TIME / 2 / 1000}s cubic-bezier(0, 0, 0.84, 0.15) 0s`;
            expandElement[0].style['WebkitTransition'] = `transform ${this.ANIMATION_TIME / 2 / 1000}s cubic-bezier(0, 0, 0.5, 1) ${this.ANIMATION_TIME / 2 / 1000}s`;
        }

        _applyShrinkExpand(condition:boolean, expandSelectorIfTrue:String, expandSelectorIfFalse:String, expandOnly:boolean){

            let expandSelector = (condition ? expandSelectorIfTrue : expandSelectorIfFalse);
            let expandElement = this.$element.find(expandSelector);
            this._expandElement(expandElement);

            if (!expandOnly){
                let shrinkSelector = (condition ? expandSelectorIfFalse :expandSelectorIfTrue);
                let shrinkElement = this.$element.find(shrinkSelector);
                this._shrinkElement(shrinkElement);
                this._setTransitions(shrinkElement, expandElement);
                this.$scope.$applyAsync(() => {
                    this._shrinkElement(shrinkElement);
                    this._expandElement(expandElement);
                });
            }
        }

        _initialRiskFeedbackButtonsFlip () {
            if (typeof this.alert === "undefined"){
                //Do nothing
                return;
            }

            let currentFeedback: any = AlertFeedback[this.alert.feedback];

            //Calculate "risk button" presentation
            this._applyShrinkExpand(AlertFeedback.Approved ===  currentFeedback,
                this.ACTUAL_RISK_CHECKED_SELECTOR,
                this.ACTUAL_RISK_UNCHECKED_SELECTOR,
                true);


            //Calculate "not a risk button" presentation
            this._applyShrinkExpand(AlertFeedback.Rejected ===  currentFeedback,
                this.NOT_RISK_CHECKED_SELECTOR,
                this.NOT_RISK_UNCHECKED_SELECTOR,
                true);
        }

        _watchRiskFeedbackFlip () {
            if (typeof this.alert === "undefined"){
                //Do nothing
                return;
            }

            let currentFeedback: any = AlertFeedback[this.alert.feedback];

            //Calculate "risk button" presentation
            this._applyShrinkExpand(AlertFeedback.Approved ===  currentFeedback,
                this.ACTUAL_RISK_CHECKED_SELECTOR,
                this.ACTUAL_RISK_UNCHECKED_SELECTOR,
                false);


            //Calculate "not a risk button" presentation
            this._applyShrinkExpand(AlertFeedback.Rejected ===  currentFeedback,
                this.NOT_RISK_CHECKED_SELECTOR,
                this.NOT_RISK_UNCHECKED_SELECTOR,
                false);

        }

        $onInit () {
            this.$scope.$watch(() => this.alert && this.alert.feedback, (feedback:AlertFeedback) => {


                    if (this.alert && this.alert.feedback !== undefined) {
                        this._watchRiskFeedbackFlip();
                    } else {

                        this._initialRiskFeedbackButtonsFlip();
                    }

            });
        }

        static $inject = ['$scope', '$element', 'alertUpdatesService', 'toastrService','auth'];

        constructor (public $scope:ng.IScope,
                     public $element:ng.IAugmentedJQuery,
                     public alertUpdatesService:IAlertUpdatesService,
                     public toastrService:IToastrService,
                     public authService:any) {
        }
    }

    let alertFeedbackController:ng.IComponentOptions = {
        controller: AlertFeedbackController,
        templateUrl: 'app/layouts/user/components/user-alert-overview/components/user-alert-overview-feedback/user-alert-overview-feedback.component.html',
        bindings: {
            alert: '<alertModel',
            updateFeedbackDelegate: '&'
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('alertFeedback', alertFeedbackController);
}

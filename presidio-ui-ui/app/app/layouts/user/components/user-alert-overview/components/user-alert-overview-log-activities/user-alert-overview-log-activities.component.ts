module Fortscale.layouts.user {


    import IUserActivity = Fortscale.shared.services.alertUpdatesService.IUserActivity;
    import IComment = Fortscale.shared.services.alertUpdatesService.IComment;
    import IFeeadback = Fortscale.shared.services.alertUpdatesService.IFeeadback;
    import AlertFeedback = Fortscale.shared.services.alertUpdatesService.AlertFeedback;

    import IAlertUpdatesService = Fortscale.shared.services.alertUpdatesService.IAlertUpdatesService;
    import IToastrService = Fortscale.shared.services.toastrService.IToastrService;


    class AlertLogActivitiesController {


        //This string bound to the text area of new comments in the UI
        newCommentText:String;

        //This object bound to comment which currently edited. Undefined or Null if no comment currently edited.
        //      currentUpdatedComment:IComment = null;
        alert:any;
        analystMap:any = {};
        //Reference to method which have been called after add / update / delete finish
        addAlertComment:any;
        deleteAlertComment:any;
        updateAlertComment:any;

        deleteAlert(alertId:string, userActivityId:string):void{
            this.deleteAlertComment({"alertId": alertId, "commentId": userActivityId});
        }

        updateAlert(alertId:string, userActivity:IUserActivity):void{
            this.updateAlertComment({"alertId": alertId, "comment": userActivity});
        }
        /**
         * Sending the comment from to the backend using alertUpdatesService,
         * and when done, trigger addAlertComment to update the whole state.
         */
        addComment () {
            if (!this.newCommentText) {
                return;
            }
            //Get current user from auth service before update the alert
            this.authService.getCurrentUser().then((result)=> {
                //Create the comment object
                let analystName = result.userName;
                let newComment:IComment = {
                    type: 'AnalystCommentFeedback',
                    commentText: this.newCommentText,
                    analystUserName: analystName,
                    modifiedAt: new Date(),
                    id: ""
                };
                this.alertUpdatesService.addComment(this.alert.id, newComment).then((response:any) => {
                    //When server update success, clear the current text, and
                    this.newCommentText = "";
                    if (response.status === 201) { //Success
                        this.addAlertComment({"alertId": this.alert.id, "comment": response.data});

                    } else {
                        //alert("return error: "+response.status);

                    }
                })
                    .catch((err) => {
                        this.toastrService.error(
                            `There was an error trying to add comment. `);
                    })
            });
        }


        /**
         * Listen to key press event on new comment.
         * If the key is enter - trigger addComment
         * @param keyEvent - the key event
         */
        newCommentKeyPressHandler (keyEvent) {
            if (keyEvent.which === 13) {
                this.addComment();
            }
        }




        static $inject = ['$scope', 'auth', 'alertUpdatesService', 'toastrService'];

        constructor (public $scope:ng.IScope, public authService:any, public alertUpdatesService:IAlertUpdatesService,
                     public toastrService:IToastrService) {
        }
    }

    let alertLogActivitiesController:ng.IComponentOptions = {
        controller: AlertLogActivitiesController,
        templateUrl: 'app/layouts/user/components/user-alert-overview/components/user-alert-overview-log-activities/user-alert-overview-log-activities.component.html',
        bindings: {
            alert: '<alertModel',
            analystMap: '<analystMap',//Map the analyst name as retrived from the API to analyst object
            updateAlertComment: '&',
            addAlertComment: '&',
            deleteAlertComment: '&',
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('alertLogActivities', alertLogActivitiesController);
}

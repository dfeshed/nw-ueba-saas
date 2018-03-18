module Fortscale.layouts.user {


    //Import API declaration for IComment, IAlertComments and IToastrService
    import IComment = Fortscale.shared.services.alertUpdatesService.IComment;
    import IAlertUpdatesService = Fortscale.shared.services.alertUpdatesService.IAlertUpdatesService;
    import IToastrService = Fortscale.shared.services.toastrService.IToastrService;

    /**
     * This component display one comment and allow edit and delete
     */
    class AlertSingleCommentController {


        //This object bound to comment which currently edited. Undefined or Null if no comment currently edited.
        currentUpdatedComment:IComment = null;
        alertId:string;
        analystMap:any = {};
        //Reference to the delete & update methods
        deleteAlertComment:any;
        updateAlertComment:any;

        COMMENT_EDIT_AREA_SELECTOR_PREFIX:string = '.comment-edit-area-';


        /**
         * Sending the comment delete request to backend using alertUpdatesService,
         * and when done, trigger deleteAlertComment to update the whole state.
         */
        deleteComment (comment:IComment) {
            this.alertUpdatesService.deleteComment(this.alertId, comment).then((response:any) => {

                if (response.status === 200) {//Success
                    this.deleteAlertComment({"alertId": this.alertId, "commentId": comment.id});

                } else {
                    alert("return error: " + response.status);
                }
            })
                .catch((err) => {
                    this.toastrService.error(
                        `There was an error trying to delete comment. `);
                })
        }

        /**
         * Sending the comment update request to backend using alertUpdatesService,
         * and when done, trigger updateAlertComment to update the whole state.
         */
        updateComment () {
            this.authService.getCurrentUser().then((result)=> {

                this.alertUpdatesService.updateComment(this.alertId, this.currentUpdatedComment)
                    .then((response:any) => {

                        if (response.status === 200) {
                            this.currentUpdatedComment = null;
                            this.updateAlertComment({"alertId": this.alertId, "comment": response.data});

                        } else {
                            alert("return error: " + response.status);
                        }
                    })
                    .catch((err) => {
                        this.toastrService.error(
                            `There was an error trying to update comment. `);
                    })
            });
        }

        /**
         * Listen to key press event on edited comment.
         * If the key is enter - trigger updateComment
         * @param keyEvent - the key event
         */
        commentEditingKeyPressHandler (keyEvent) {
            if (keyEvent.which === 13) {
                this.updateComment();
            }
        }

        /**
         * When clicking "update" on any comment, update the state with the clicked comment details
         * @param comment
         */
        setUpdateMode (comment:IComment) {
            this.currentUpdatedComment = _.clone(comment);
            this.currentUpdatedComment.modifiedAt = null;

            // Its hard to say exactly when angular will expose the element (that has ng-show), so to get best result,
            // while planning for a case where it might take longer, there are 3 focus requests, on 50, 100, and 200 ms
            _.each([50, 100, 200], (time) => {
                this.$timeout(() => {
                    this.$element.find(this.COMMENT_EDIT_AREA_SELECTOR_PREFIX + comment.id).focus();
                }, time);
            });

        }

        static $inject = ['$scope', 'auth', 'alertUpdatesService', 'toastrService', '$element', '$timeout'];

        constructor (public $scope:ng.IScope, public authService:any, public alertUpdatesService:IAlertUpdatesService,
                     public toastrService:IToastrService, public $element:ng.IAugmentedJQuery,
                     public $timeout:ng.ITimeoutService) {
        }
    }

    let alertSingleCommentController:ng.IComponentOptions = {
        controller: AlertSingleCommentController,
        templateUrl: 'app/layouts/user/components/user-alert-overview/components/user-alert-single-comment/user-alert-single-comment.component.html',
        bindings: {
            alertId: '<',
            comment: '<',
            analystMap: '<analystMap',//Map the analyst name as retrived from the API to analyst object
            updateAlertComment: '&',
            deleteAlertComment: '&',
            last:'<' //Indicate if this is the last row
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('alertSingleComment', alertSingleCommentController);
}

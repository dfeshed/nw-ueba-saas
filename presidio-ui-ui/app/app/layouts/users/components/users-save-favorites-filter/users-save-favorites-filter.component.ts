module Fortscale.Users.layouts.usersFavortiePopup {

    const FILTER_NAME_FIELD_SELECTOR:string = "#filterName";

    class UsersAddFavoritesPopupController {

        private models: {
            filterName: string
        };
        private favoriteNameForm: ng.IFormController;

        //Button delegate
        private onSave: (locals: {filterName:string}) => ng.IPromise<void>;
        private onCancel: () => void;

        //Name exists - boolean for error message.
        nameExists:boolean = false;

        visible:boolean;


        /**
         * Try to save filter. Listen to respone from delegate.
         * If name exists- display the error, else - clear the error
         */
        saveFilter(){
            let ctrl:any = this;
            this.onSave({filterName:this.models.filterName}).then((res:any)=>{
                if (res.status === 409){//Duplicate name
                    ctrl.nameExists = true;
                } else {
                    ctrl.nameExists = false;
                }

            });
        }

        /**
         * On cancel always clear the nameExists error flag
         */
        cancelFilterSaving(){
            this.nameExists = false;
            this.onCancel();
        }

        $onInit () {
            this.$timeout(() => {
                this.$element.addClass('enter-active');

            }, 50);

            this.$scope.$watch(() => this.visible, (visible:boolean) => {

                //When chenged to visible, the focus should take place
                if (this.visible) {
                    this._focusDefaultField();
                }

            });
        }

        _focusDefaultField():any{
            let ctrl:any=this;

            this.$timeout(function () {

                let inputField: JQuery = ctrl.$element.find(FILTER_NAME_FIELD_SELECTOR);
                ctrl.$timeout(function () {

                   // inputField.focus();
                    inputField[0].focus();
                    inputField.select();
                });
            },400);
        }

        submitWithKeypress(e:any){
                if (e.keyCode===13) {
                    this.saveFilter();

                }
        }

        static $inject = ['$scope','$element', '$timeout'];

        constructor (public $scope:ng.IScope,public $element: any, public $timeout: ng.ITimeoutService) {
        }
    }



    let usersAddFavoritesPopup:ng.IComponentOptions = {
        controller: UsersAddFavoritesPopupController,
        templateUrl: 'app/layouts/users/components/users-save-favorites-filter/users-save-favorites-filter.component.html',
        bindings: {
            onSave: '&',
            onCancel: '&',
            visible: '<'

        }
    };

    angular.module('Fortscale.layouts.users')
        .component('usersAddFavoritesPopup', usersAddFavoritesPopup);

}

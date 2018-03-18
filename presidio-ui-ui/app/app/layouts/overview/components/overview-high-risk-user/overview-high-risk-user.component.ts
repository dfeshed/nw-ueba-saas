module Fortscale.layouts.overview.components.highRiskUsers {

    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;
    class HighRiskUserController {

        user:any;
        tags:ITagDefinition[];
        userTags:ITagDefinition[];

        static $inject = ['userUtils', '$scope'];

        constructor (public userUtils:any, public $scope:ng.IScope) {
        }

        _setUserTags () {
            this.userTags = [];
            _.each<string>(this.user.tags, (tag: string) => {
                let tagObj = _.find(this.tags, {name: tag});
                if (tagObj) {
                    this.userTags.push(tagObj);
                }
            });
        }


        _initUserWatch () {
            let deregister = this.$scope.$watch(
                () => this.user,
                (user:any) => {
                    if (user) {
                        this.userUtils.setFallBackDisplayNames([user]);
                        deregister();
                    }
                }
            );
        }

        _initTagsWatch () {
            let deregister = this.$scope.$watch(
                () => this.tags,
                () => {
                    if (this.user && this.tags) {
                        this._setUserTags();
                        deregister();
                    }
                }
            );        }

        _initWatches () {
            this._initUserWatch();
            this._initTagsWatch();
        }

        $onInit () {
            this._initWatches();
        }
    }

    let HighRiskUserComponent:ng.IComponentOptions = {
        controller: HighRiskUserController,
        bindings: {
            user: '<',
            tags: '<'
        },
        templateUrl: 'app/layouts/overview/components/overview-high-risk-user/overview-high-risk-user.component.html'
    };

    angular.module('Fortscale.layouts.overview')
        .component('overviewHighRiskUser', HighRiskUserComponent);
}

module Fortscale.layouts.user {

    import IActivityTopRecipientDomain = Fortscale.shared.services.entityActivityUtils.IActivityTopRecipientDomain;
    import IDeviceUtilsService = Fortscale.shared.services.deviceUtilsService.IDeviceUtilsService;
    import IUserActivityExtend = Fortscale.shared.services.deviceUtilsService.IUserActivityExtend;
    import IUserTopRecipientDomain = Fortscale.shared.services.deviceUtilsService.IUserTopRecipientDomain;
    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;

    class DomainFamilySettings{
        icon:string;
        prefixes:string[];
    }

    class ActivityTopRecipientsDomainsController {

        _recipientsDomains: IActivityTopRecipientDomain[] = [];
        recipientsDomains: IUserTopRecipientDomain[] = null;

        domainsSettings: DomainFamilySettings[];


        //Remove all recipientDomain with no count, if any returned from server.
        _removeZeroCount (recipientDomain: IUserTopRecipientDomain[]) {
            //return new array of IUserTopRecipientsDomains with recipientDomain
            return _.filter(recipientDomain, (app) => app.count > 0);
        }

        /**
         * Takes received source recipientDomain, sorts, repositions 'other', and adds percent to each, then stores on recipientDomain
         * @private
         */
        _digestDomains ():void  {

            let recipientDomain: IUserTopRecipientDomain[];

            // sort _recipientDomain
            recipientDomain = _.orderBy<IUserTopRecipientDomain>(_.cloneDeep(this._recipientsDomains), 'count', 'desc');

            // // pluck "other" and push to the end
            //this.deviceUtilsService.repositionOthers(recipientDomain);

            let recipientsDomains:IUserActivityExtend[] = recipientDomain;
            // remove all items with zero count
            recipientsDomains = this.deviceUtilsService.removeZeroCount(recipientsDomains);
            recipientsDomains = this.deviceUtilsService.updatePercentageOnDevice(recipientsDomains);


            this.recipientsDomains = <IUserTopRecipientDomain[]>recipientsDomains;
        }

        _sortRecipientsDomains (): void {
            this.recipientsDomains = _.orderBy(this.recipientsDomains, [
                (userApplication:IUserTopRecipientDomain) => userApplication.name === 'Others',
                'count'
            ], [
                'asc',
                'desc'
            ]);

        }

        /**
         * Activates the bars
         * @private
         */
        _activateBars () {
            _.each(this.recipientsDomains, (recipientDomain: IUserTopRecipientDomain, index:number) => {
                this.$timeout(() => {
                    recipientDomain.active = true;
                }, ((this.recipientsDomains.length-1)-index)*400 + 200);
            });
        }

        /**
         * Initiates watch on received source recipientDomain
         * @private
         */
        _initRecipientsDomainsWatch () {
            this.$scope.$watch(
                () => this._recipientsDomains,
                (recipientDomain) => {
                    if (recipientDomain && recipientDomain.length) {
                        this._digestDomains();
                        this._sortRecipientsDomains();
                        this._activateBars();
                    } else if (recipientDomain) {
                        this.recipientsDomains = [];
                    }
                }
            );
        }

        /**
         * Read from configuration to build map of the recipientDomain to display namd and icon
         * @private
         */
        _buildIconsAndDisplayNames(){

            this.domainsSettings=[];
           _.each(this.appConfig.getConfigNodesTree().nodes.messages.nodes.en.nodes.recipient_domain.nodes,(node:any)=>{
               //For each recipientDomain:
                let appKey=node.id; //Get partial key of the recipientDomain's attributes

                let domainsSettings = this.appConfig.getConfigItemsByContainer(appKey); //Get sub values for the recipientDomain
                let currentApp: DomainFamilySettings = new DomainFamilySettings();

                _.each(domainsSettings,(setting)=>{
                    if (setting.displayName === 'prefix') {
                        currentApp.prefixes = setting.value.split(",");
                    } else {

                    } if (setting.displayName === 'icon') {
                        currentApp[setting.displayName] = setting.value;
                    }
                });

                this.domainsSettings.push(currentApp);

            });
        }

        /**
         * Read the settings for recipientDomain by name, return default values if not found
         * @param appName
         * @returns {DomainFamilySettings}
         */
        getSettingsForApp(domain:string):DomainFamilySettings{
            let app:DomainFamilySettings = null;
            _.each(this.domainsSettings,(domainFamilySetting:DomainFamilySettings)=>{
               _.each(domainFamilySetting.prefixes,(prefix:string)=>{
                   if (domain.startsWith(prefix)){
                       app = domainFamilySetting;
                   }
                });
            });
            if (_.isNil(app)){
                //No configurations use defaults
                app = new DomainFamilySettings;
                app.icon = "at.png";
            }
            return app;

        }

        /**
         * Build style object for image
         * @param appName
         * @returns {{background-image: string, border: string}}
         */
        getImageStyle(appName:string):any{
            let iconPath:string = this.getSettingsForApp(appName).icon;
            iconPath="assets/images/icons/top_recipients_domain/"+iconPath;
            return { 'background-image': 'url('+iconPath+')', border: 'none'}
        }


        $onInit () {
            this._buildIconsAndDisplayNames();
            this._initRecipientsDomainsWatch();

        }

        static $inject = ['$scope', '$timeout','deviceUtilsService','appConfig'];

        constructor (public $scope:ng.IScope, public $timeout:ng.ITimeoutService,
                     public deviceUtilsService:IDeviceUtilsService,public appConfig:IAppConfigService) {
        }
    }

    let activityTopRecipientsDomainsComponent:ng.IComponentOptions = {
        controller: ActivityTopRecipientsDomainsController,
        templateUrl: 'app/layouts/user/components/user-activity-top-recipients-domains/user-activity-top-recipients-domains.component.html',
        bindings: {
            _recipientsDomains: '<recipientsDomains',
            description: '@'
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userActivityTopRecipientsDomains', activityTopRecipientsDomainsComponent);
}

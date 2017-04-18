import { A } from 'ember-array/utils';
import computed, { alias } from 'ember-computed';
import { isNone } from 'ember-utils';
import Service from 'ember-service';
import service from 'ember-service/inject';

export default Service.extend({

  accessControl: service(),
  request: service(),

  hasAdminAccess: alias('accessControl.hasAdminAccess'),
  hasConfigAccess: alias('accessControl.hasConfigAccess'),
  hasMonitorAccess: alias('accessControl.hasMonitorAccess'),
  hasInvestigateAccess: alias('accessControl.hasInvestigateAccess'),
  hasRespondAccess: alias('accessControl.hasRespondAccess'),

  selected: null,

  options: computed('hasAdminAccess', 'hasConfigAccess', 'hasMonitorAccess', 'hasInvestigateAccess', 'hasRespondAccess', function() {
    const { hasAdminAccess, hasConfigAccess, hasMonitorAccess, hasInvestigateAccess, hasRespondAccess } =
      this.getProperties('hasAdminAccess', 'hasConfigAccess', 'hasMonitorAccess', 'hasInvestigateAccess', 'hasRespondAccess');

    const options = A([]);

    if (hasRespondAccess) {
      options.pushObject({
        key: '/respond',
        label: 'userPreferences.defaultLandingPage.respond'
      });
    }

    if (hasInvestigateAccess) {
      // TODO: add this option back when we want to enable the new investigate to clients

      // options.pushObject({
      //   key: '/investigate',
      //   label: 'userPreferences.defaultLandingPage.investigate'
      // });

      options.pushObject({
        key: '/investigation',
        label: 'userPreferences.defaultLandingPage.investigateClassic'
      });
    }

    if (hasMonitorAccess) {
      options.pushObject({
        key: '/unified',
        label: 'userPreferences.defaultLandingPage.dashboard'
      });
    }

    if (hasConfigAccess) {
      options.pushObject({
        key: this.get('accessControl.configUrl'),
        label: 'userPreferences.defaultLandingPage.live'
      });
    }

    if (hasAdminAccess) {
      options.pushObject({
        key: this.get('accessControl.adminUrl'),
        label: 'userPreferences.defaultLandingPage.admin'
      });
    }

    return options;
  }),

  persist(value) {
    this.get('request').promiseRequest({
      method: 'setPreference',
      modelName: 'preferences',
      query: {
        data: {
          defaultComponentUrl: value
        }
      }
    }).catch(() => {
      this.get('flashMessages').error(this.get('i18n').t('userPreferences.landingPageError'));
    });
  },

  /**
   * Sets and persists the default landing page the user selects in the preferences menu
   * @param value The selected landing page
   * @public
   */
  setDefaultLandingPage(value) {
    if (value && value.key) {
      if (!isNone(this.get('selected'))) {
        this.persist(value.key);
      }
      this.set('selected', value);
    } else {
      if (!isNone(this.get('selected'))) {
        this.persist(value);
      }
      this.set('selected', this.get('options').findBy('key', value));
    }
  }
});

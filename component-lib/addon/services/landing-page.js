import { A } from 'ember-array/utils';
import computed, { alias } from 'ember-computed';
import { isNone } from 'ember-utils';
import Service from 'ember-service';
import service from 'ember-service/inject';
import config from 'ember-get-config';

export default Service.extend({

  accessControl: service(),
  request: service(),

  hasAdminAccess: alias('accessControl.hasAdminAccess'),
  hasConfigAccess: alias('accessControl.hasConfigAccess'),
  hasMonitorAccess: alias('accessControl.hasMonitorAccess'),
  hasInvestigateAccess: alias('accessControl.hasInvestigateAccess'),
  hasRespondAccess: alias('accessControl.hasRespondAccess'),

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
      options.pushObjects([
        {
          key: '/investigate',
          label: 'userPreferences.defaultLandingPage.investigate'
        }, {
          key: '/investigation',
          label: 'userPreferences.defaultLandingPage.investigateClassic'
        }
      ]);
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
      this.get('flashMessages').error(this.get('i18n').t('userPreferences.landingPageError'), {
        iconName: 'delete-1',
        iconStyle: 'filled'
      });
    });
  },

  selected: computed({
    get() {
      return this.get('_selected') || this.get('options').findBy('key', config.landingPageDefault);
    },

    set(key, value) {
      if (value.key) {
        if (!isNone(this.get('_selected'))) {
          this.persist(value.key);
        }
        this.set('_selected', value);
        return value;
      } else {
        if (!isNone(this.get('_selected'))) {
          this.persist(value);
        }
        this.set('_selected', this.get('options').findBy('key', value));
        return this.get('_selected');
      }
    }
  })

});

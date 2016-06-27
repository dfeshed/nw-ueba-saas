import { moduleForComponent, test } from 'ember-qunit';
import { incidentStatusIds, incidentPriorityIds } from 'sa/incident/constants';
import config from 'sa/config/environment';

// It' required to load a component/route/app in order to run translations.
moduleForComponent('rsa-incident-tile', 'Integration | Constants', {
  integration: true,

  beforeEach() {
    this.set('i18n', this.container.lookup('service:i18n'));
  }
});

test('Status are defined for all languages', function(assert) {
  let i18n = this.get('i18n');

  config.moment.includeLocales.forEach((locale) => {
    i18n.set('locale', locale);
    incidentStatusIds.forEach((status) => {
      assert.equal(i18n.t(`incident.status.${ status }`).toString().indexOf('Missing translation'), -1, `Found ${ locale } translation for status id: ${ status }`);
    });
  });
});

test('Priorities are defined for all languages', function(assert) {
  let i18n = this.get('i18n');

  config.moment.includeLocales.forEach((locale) => {
    i18n.set('locale', locale);
    incidentPriorityIds.forEach((priority) => {
      assert.equal(i18n.t(`incident.priority.${ priority }`).toString().indexOf('Missing translation'), -1, `Found ${ locale } translation for priority id: ${ priority }`);
    });
  });
});

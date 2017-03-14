import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../../helpers/data-helper';

moduleForComponent('rsa-incident-overview', 'Integration | Component | Incident Overview', {
  integration: true,
  resolver: engineResolverFor('respond'),
  setup() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  this.render(hbs`{{rsa-incident/overview}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-incident-overview');
    assert.equal($el.length, 1, 'Expected to find overview root element in DOM.');

    [ '.created', '.by', '.sealed', '.sources', '.catalyst-count', '.assignee', '.priority', '.status' ].forEach((selector) => {
      const $field = $el.find(`${selector} span`);
      assert.ok($field.text().trim(), `Expected to find non-empty field element in DOM for: ${selector}`);
    });
  });
});
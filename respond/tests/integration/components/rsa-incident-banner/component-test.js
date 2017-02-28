import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../helpers/data-helper';

moduleForComponent('rsa-incident-banner', 'Integration | Component | Incident Banner', {
  integration: true,
  resolver: engineResolverFor('respond'),
  setup() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  this.render(hbs`{{rsa-incident-banner incidentId="INC-X"}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-incident-banner');
    assert.equal($el.length, 1, 'Expected to find root element in DOM.');
    [ '.id', '.risk-score', '.alert-count', '.created', '.priority', '.status', '.assignee' ].forEach((className) => {
      const $field = $el.find(className);
      assert.ok($field.text().trim(), `Expected to find non-empty ${className} element in DOM.`);
    });
  });
});
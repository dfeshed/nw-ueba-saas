import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import { incidentDetails } from '../../../../data/data';

moduleForComponent('rsa-incident-overview', 'Integration | Component | Incident Overview', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('it renders', function(assert) {
  this.set('incidentDetails', incidentDetails);
  this.render(hbs`{{rsa-incident/overview info=incidentDetails}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-incident-overview');
    assert.equal($el.length, 1, 'Expected to find overview root element in DOM.');

    [ '.created', '.by', '.sources', '.catalyst-count' ].forEach((selector) => {
      const $field = $el.find(`${selector} span`);
      assert.ok($field.text().trim(), `Expected to find non-empty field element in DOM for: ${selector}`);
    });

    ['.assignee', '.priority', '.status'].forEach((selector) => {
      const $field = $el.find(`${selector} div.edit-button .rsa-form-button`);
      assert.ok($field.text().trim(), `Expected to find non-empty button text in DOM for : ${selector}`);
    });
  });
});
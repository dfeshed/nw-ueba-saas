import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

const ALERT_TABS = [
  {
    name: 'CRITICAL',
    selected: true
  },
  {
    name: 'HIGH'
  },
  {
    name: 'MEDIUM'
  },
  {
    name: 'LOW'
  }
];

module('Integration | Component | endpoint/risk-properties/alert-tab', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    this.set('tabs', ALERT_TABS);

    await render(hbs`
      {{#endpoint/risk-properties/alert-tab tabs=tabs as |tab|}}
        {{tab.name}}
      {{/endpoint/risk-properties/alert-tab}}
    `);
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'CRITICAL', 'CRITICAL tab is selected');
  });
});

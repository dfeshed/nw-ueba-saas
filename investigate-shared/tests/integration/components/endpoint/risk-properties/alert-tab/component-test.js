import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

const ALERT_TABS = [
  {
    name: 'critical',
    selected: true
  },
  {
    name: 'high'
  },
  {
    name: 'medium'
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
    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'critical', 'critical tab is selected');
  });

  test('severity tab with 0 alerts should not be not clickable', async function(assert) {
    const ALERT_TABS = [
      {
        name: 'critical',
        count: 1,
        selected: true
      },
      {
        name: 'high',
        count: 0
      },
      {
        name: 'medium',
        count: 1
      }
    ];

    this.set('tabs', ALERT_TABS);

    this.set('changeTab', () => {
      this.set('tabs', [{
        name: 'medium',
        count: 1,
        selected: true
      }]);
    });

    await render(hbs`
      {{#endpoint/risk-properties/alert-tab defaultAction=(action changeTab) tabs=tabs as |tab|}}
        {{tab.name}}
      {{/endpoint/risk-properties/alert-tab}}
    `);

    // Clicking on high severity tab with 0 alerts
    await click(document.querySelectorAll('.rsa-nav-tab')[1]);

    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'critical', 'critical tab is selected');

    // Clicking on medium severity tab with 1 alerts
    await click(document.querySelectorAll('.rsa-nav-tab')[2]);

    assert.equal(find('.rsa-nav-tab.is-active').textContent.trim(), 'medium', 'medium tab is selected');

  });
});

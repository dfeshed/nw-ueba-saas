import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/risk-properties', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    this.set('activeTab', 'CRITICAL');
    await render(hbs`{{endpoint/risk-properties activeAlertTab=activeTab}}`);

    assert.equal(findAll('.risk-properties').length, 1, 'risk properties is rendered');
    assert.equal(findAll('.risk-properties .rsa-nav-tab').length, 3, '3 tabs are present');

  });

  test('alert count is rendered', async function(assert) {
    const alertsData = {
      'hash': 'ccc8538dd62f20999717e2bbab58a18973b938968d699154df9233698a899efa',
      'alertCount': {
        'critical': 1,
        'high': 10,
        'medium': 20
      },
      'categorizedAlerts': {
        'Some Randon Endpoint Alert': {
          'alertCount': 2,
          'alertCategory': 'CRITICAL'
        }
      }
    };
    this.set('alertsData', alertsData);
    this.set('activeTab', 'CRITICAL');
    await render(hbs`{{endpoint/risk-properties activeAlertTab=activeTab alertsData=alertsData}}`);
    assert.equal(findAll('.risk-properties').length, 1, 'risk properties is rendered');
    assert.equal(findAll('.risk-properties .alert-count')[0].innerText, 1, 'Critical alert count is 1');
    assert.equal(findAll('.risk-properties .alert-count')[1].innerText, 10, 'High alert count is 10');
    assert.equal(findAll('.risk-properties .alert-count')[2].innerText, 20, 'Medium alert count is 20');
  });
});

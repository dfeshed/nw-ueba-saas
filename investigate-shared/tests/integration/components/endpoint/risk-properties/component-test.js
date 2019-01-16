import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/risk-properties', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    this.set('riskState', { activeRiskSeverityTab: 'critical' });
    await render(hbs`{{endpoint/risk-properties riskState=riskState}}`);

    assert.equal(findAll('.risk-properties').length, 1, 'risk properties is rendered');
    assert.equal(findAll('.risk-properties .rsa-loader').length, 1, 'loading icon is present');

  });

  test('Risk Score related severity and context are rendered', async function(assert) {
    const riskScoreContext = {
      'id': 'ccc8538dd62f20999717e2bbab58a18973b938968d699154df9233698a899efa',
      'distinctAlertCount': {
        'critical': 1,
        'high': 2,
        'medium': 3,
        'low': 0
      },
      'categorizedAlerts': {
        'Critical': {
          'test alert': {
            'alertCount': 10,
            'eventCount': 10
          }
        }
      }
    };
    this.set('riskState', { activeRiskSeverityTab: 'critical', riskScoreContext });
    await render(hbs`{{endpoint/risk-properties riskState=riskState riskType='FILE'}}`);

    assert.equal(findAll('.risk-properties').length, 1, 'risk properties is rendered');
    assert.equal(findAll('.risk-properties .rsa-nav-tab').length, 4, '4 tabs are present');
    assert.equal(findAll('.risk-properties .alert-count')[0].innerText, 1, 'Critical alert count is 1');
    assert.equal(findAll('.risk-properties .alert-count')[1].innerText, 2, 'High alert count is 2');
    assert.equal(findAll('.risk-properties .alert-count')[2].innerText, 3, 'Medium alert count is 3');
    assert.equal(findAll('.risk-properties .alert-count')[3].innerText, 6, 'All alert count is 6');

    assert.equal(
      find('.alert-context__name').textContent.trim(),
      'test alert (10)',
      'Display alert name and alert count for alert context'
    );
    assert.equal(find('.alert-context__event').textContent.trim(), '10 event(s)', 'Display 10 events for alert context');
  });

  test('change landing severity tab if currentActiveTab has 0 alerts', async function(assert) {
    assert.expect(2);
    const riskScoreContext = {
      'id': 'test-hash',
      'distinctAlertCount': {
        'critical': 0,
        'high': 1,
        'medium': 3,
        'low': 0
      },
      'categorizedAlerts': {
        'High': {
          'test alert': {
            'alertCount': 2,
            'eventCount': 2
          }
        }
      }
    };
    this.set('riskState', { activeRiskSeverityTab: 'critical', riskScoreContext });
    this.set('setSelectedAlert', (context) => {
      assert.equal(context.eventCount, 2, '2 events are present');
    });

    this.set('activate', () => {
      this.set('riskState', { activeRiskSeverityTab: 'high', riskScoreContext });
    });

    await render(hbs`{{endpoint/risk-properties
      riskState=riskState
      setSelectedAlert=(action setSelectedAlert)
      defaultAction=(action activate)}}`);

    assert.equal(find('.rsa-nav-tab.is-active .label').textContent.trim(), 'HIGH', 'high tab is selected');
  });

  test('Display alert count for host', async function(assert) {
    const riskScoreContext = {
      'id': 'C593263F-E2AB-9168-EFA4-C683E066A035',
      'distinctAlertCount': {
        'critical': 1,
        'high': 2,
        'medium': 3,
        'low': 1
      },
      'categorizedAlerts': {
        'Critical': {
          'test alert': {
            'alertCount': 10,
            'eventCount': 10
          }
        }
      }
    };
    this.set('riskState', { activeRiskSeverityTab: 'critical', riskScoreContext });
    this.set('riskType', 'HOST');
    await render(hbs`{{endpoint/risk-properties riskState=riskState riskType=riskType}}`);

    assert.equal(findAll('.risk-properties').length, 1, 'risk properties is rendered');
    assert.equal(findAll('.risk-properties .rsa-nav-tab').length, 4, '4 tabs are present');
    assert.equal(findAll('.risk-properties .alert-count')[0].innerText, 1, 'Critical alert count is 1');
    assert.equal(findAll('.risk-properties .alert-count')[1].innerText, 2, 'High alert count is 2');
    assert.equal(findAll('.risk-properties .alert-count')[2].innerText, 3, 'Medium alert count is 3');
    assert.equal(findAll('.risk-properties .alert-count')[3].innerText, 7, 'All alert count is 7');

    assert.equal(find('.alert-context__name').textContent.trim(), 'test alert (10)',
      'Display alert name and alert count for alert context');

    assert.equal(findAll('.rsa-panel-message').length, 0,
      'No Error Message related to Context Empty/Server Error/Insight Agent should be displayed for valid Risk Score of an agent.');
  });

  test('relevant error message is displayed when respond server mongo is down', async function(assert) {
    this.set('state', {
      riskScoreContextError: {
        error: 'mongo.connection.failed'
      },
      isRespondServerOffline: false
    });

    await render(hbs`{{endpoint/risk-properties
      riskState=state}}`);

    assert.equal(find('.rsa-panel-message').textContent.trim(), 'Database is not reachable. Retry after sometime.');
  });

  test('relevant error message is displayed when respond server is down', async function(assert) {
    this.set('state', {
      isRespondServerOffline: true
    });

    await render(hbs`{{endpoint/risk-properties
      riskState=state}}`);

    assert.equal(findAll('.error-page').length, 1, 'Respond Server is offline');
    assert.equal(find('.error-page .title').textContent.trim(), 'Respond Server is offline');
  });

  test('relevant error message is displayed when empty risk score context is returned', async function(assert) {

    this.set('state', {
      activeRiskSeverityTab: 'critical',
      riskScoreContextError: null,
      riskScoreContext: {
        distinctAlertCount: {
          critical: 0,
          high: 0,
          medium: 0,
          low: 0
        }
      }
    });

    await render(hbs`{{endpoint/risk-properties
      riskState=state}}`);

    assert.equal(findAll('.rsa-panel-message').length, 1, 'Error Message for No alerts available exists.');
  });


  test('show loading icon when alerts name data is not received', async function(assert) {
    const riskScoreContext = {
      'id': 'C593263F-E2AB-9168-EFA4-C683E066A035',
      'distinctAlertCount': {
        'critical': 1,
        'high': 2,
        'medium': 3,
        'low': 1
      },
      'categorizedAlerts': {
        'Critical': {
          'test alert': {
            'alertCount': 10,
            'eventContexts': 10
          }
        }
      }
    };
    this.set('riskState', { activeRiskSeverityTab: 'critical', alertsLoadingStatus: 'loading', riskScoreContext });
    this.set('riskType', 'FILES');
    await render(hbs`{{endpoint/risk-properties riskState=riskState riskType=riskType}}`);
    assert.equal(findAll('.risk-properties').length, 1, 'risk properties is rendered');
    assert.equal(findAll('.alerts-loading').length, 1, 'loading icon is present');
  });

  test('relevant error message is displayed when risk score context for insight agent is requested', async function(assert) {

    this.set('state', {
      activeRiskSeverityTab: 'critical'
    });

    await render(hbs`{{endpoint/risk-properties
      isInsightsAgent=true
      riskState=state}}`);

    assert.equal(findAll('.rsa-panel-message').length, 1, 'Error Message for Risk Score feature not available for insight agent.');
  });
});

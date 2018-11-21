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
      'hash': 'ccc8538dd62f20999717e2bbab58a18973b938968d699154df9233698a899efa',
      'distinctAlertCount': {
        'critical': 1,
        'high': 2,
        'medium': 3
      },
      'categorizedAlerts': {
        'Critical': {
          'test alert': {
            'alertCount': 10,
            'eventContexts': [{
              'id': 'decoder-id1',
              'sourceId': '1'
            },
            {
              'id': 'decoder-id2',
              'sourceId': '2'
            }]
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
    assert.equal(find('.alert-context__event').textContent.trim(), '2 event(s)', 'Display 2 events for alert context');
  });

  test('change landing severity tab if currentActiveTab has 0 alerts', async function(assert) {
    assert.expect(2);
    const riskScoreContext = {
      'hash': 'test-hash',
      'distinctAlertCount': {
        'critical': 0,
        'high': 1,
        'medium': 3
      },
      'categorizedAlerts': {
        'High': {
          'test alert': {
            'alertCount': 2,
            'eventContexts': [{ 'id': 1, 'sourceId': 'decoder-id1' }, { 'id': 2, 'sourceId': 'decoder-id1' }]
          }
        }
      }
    };
    this.set('riskState', { activeRiskSeverityTab: 'critical', riskScoreContext });
    this.set('setSelectedAlert', (context) => {
      assert.equal(context.context.length, 2, '2 events are present');
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

  test('check files count', async function(assert) {
    const riskScoreContext = {
      'id': 'C593263F-E2AB-9168-EFA4-C683E066A035',
      'distinctAlertCount': {
        'critical': 1,
        'high': 2,
        'medium': 3
      },
      'categorizedAlerts': {
        'Critical': {
          'test alert': {
            'alertCount': 10,
            'eventContexts': [{
              'id': 'decoder-id1',
              'sourceId': '1'
            },
            {
              'id': 'decoder-id2',
              'sourceId': '2'
            }]
          }
        }
      }
    };
    this.set('riskState', { activeRiskSeverityTab: 'critical', riskScoreContext });
    await render(hbs`{{endpoint/risk-properties riskState=riskState}}`);
    assert.equal(find('.alert-context__files').textContent.trim(), '0 file(s)', 'Display 10 events for alert context');
  });


  test('Display alert count for host', async function(assert) {
    const riskScoreContext = {
      'id': 'C593263F-E2AB-9168-EFA4-C683E066A035',
      'distinctAlertCount': {
        'critical': 1,
        'high': 2,
        'medium': 3
      },
      'categorizedAlerts': {
        'Critical': {
          'test alert': {
            'alertCount': 10,
            'eventContexts': [{
              'id': 'decoder-id1',
              'sourceId': '1'
            },
            {
              'id': 'decoder-id2',
              'sourceId': '2'
            }]
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
    assert.equal(findAll('.risk-properties .alert-count')[3].innerText, 6, 'All alert count is 6');

    assert.equal(find('.alert-context__name').textContent.trim(), 'test alert (10)',
      'Display alert name and alert count for alert context');
    assert.equal(find('.alert-context__files').textContent.trim(), '0 file(s)', 'Display 10 events for alert context');
  });

  test('relevant error message is displayed when risk scoring server mongo is down', async function(assert) {
    this.set('isRiskScoringServerNotConfigured', false);
    this.set('state', {
      riskScoreContextError: {
        error: 'mongo.connection.failed'
      },
      isRiskScoringServerOffline: false
    });

    await render(hbs`{{endpoint/risk-properties
      riskState=state
      isRiskScoringServerNotConfigured=isRiskScoringServerNotConfigured}}`);

    assert.equal(find('.rsa-panel-message').textContent.trim(), 'Database is not reachable. Retry after sometime.');
  });

  test('relevant error message is displayed when risk scoring server is down', async function(assert) {
    this.set('isRiskScoringServerNotConfigured', false);
    this.set('state', {
      isRiskScoringServerOffline: true
    });

    await render(hbs`{{endpoint/risk-properties
      riskState=state
      isRiskScoringServerNotConfigured=isRiskScoringServerNotConfigured}}`);

    assert.equal(findAll('.error-page').length, 1, 'Risk Scoring Server is offline');
    assert.equal(find('.error-page .title').textContent.trim(), 'Risk Scoring Server is offline');
  });

  test('relevant error message is displayed when risk scoring server is configured', async function(assert) {
    this.set('isRiskScoringServerNotConfigured', true);

    this.set('state', { activeRiskSeverityTab: 'critical' });

    await render(hbs`{{endpoint/risk-properties
      riskState=state
      isRiskScoringServerNotConfigured=isRiskScoringServerNotConfigured}}`);

    assert.equal(findAll('.error-page').length, 1, 'Risk Scoring Server is not configured');
    assert.equal(find('.error-page .title').textContent.trim(), 'Risk Scoring Server is not configured');
  });

  test('relevant error message is displayed when empty risk score context is returned', async function(assert) {

    this.set('state', {
      activeRiskSeverityTab: 'critical',
      riskScoreContextError: null,
      riskScoreContext: {
        distinctAlertCount: {
          critical: 0,
          high: 0,
          medium: 0
        }
      }
    });

    await render(hbs`{{endpoint/risk-properties
      riskState=state
      isRiskScoringServerNotConfigured=isRiskScoringServerNotConfigured}}`);

    assert.equal(findAll('.rsa-panel-message').length, 1, 'Error Message for No alerts available exists.');
  });
});

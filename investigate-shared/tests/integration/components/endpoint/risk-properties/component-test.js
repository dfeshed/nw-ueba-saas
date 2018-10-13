import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/risk-properties', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    this.set('activeRiskSeverityTab', 'critical');
    await render(hbs`{{endpoint/risk-properties activeRiskSeverityTab=activeRiskSeverityTab}}`);

    assert.equal(findAll('.risk-properties').length, 1, 'risk properties is rendered');
    assert.equal(findAll('.risk-properties .rsa-loader').length, 1, 'loading icon is present');

  });

  test('Risk Score related severity and context are rendered', async function(assert) {
    const riskScoreContext = {
      'hash': 'ccc8538dd62f20999717e2bbab58a18973b938968d699154df9233698a899efa',
      'alertCount': {
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
    this.set('riskScoreContext', riskScoreContext);
    this.set('activeRiskSeverityTab', 'critical');
    await render(hbs`{{endpoint/risk-properties activeRiskSeverityTab=activeRiskSeverityTab riskScoreContext=riskScoreContext}}`);

    assert.equal(findAll('.risk-properties').length, 1, 'risk properties is rendered');
    assert.equal(findAll('.risk-properties .rsa-nav-tab').length, 4, '4 tabs are present');
    assert.equal(findAll('.risk-properties .alert-count')[0].innerText, 1, 'Critical alert count is 1');
    assert.equal(findAll('.risk-properties .alert-count')[1].innerText, 2, 'High alert count is 2');
    assert.equal(findAll('.risk-properties .alert-count')[2].innerText, 3, 'Medium alert count is 3');
    assert.equal(findAll('.risk-properties .alert-count')[3].innerText, 6, 'All alert count is 6');

    assert.equal(find('.alert-context__name').textContent.trim(), 'test alert (10)',
                          'Display alert name and alert count for alert context');
    assert.equal(find('.alert-context__event').textContent.trim(), '2 events', 'Display 10 events for alert context');
  });

  test('change landing severity tab if currentActiveTab has 0 alerts', async function(assert) {
    assert.expect(2);
    const riskScoreContext = {
      'hash': 'test-hash',
      'alertCount': {
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
    this.set('riskScoreContext', riskScoreContext);
    this.set('activeRiskSeverityTab', 'critical');
    this.set('setSelectedAlert', (context) => {
      assert.equal(context.context['decoder-id1'].length, 2, '2 events are grouped in decoder-id1');
    });

    this.set('activate', () => {
      this.set('activeRiskSeverityTab', 'high');
    });

    await render(hbs`{{endpoint/risk-properties 
      activeRiskSeverityTab=activeRiskSeverityTab 
      setSelectedAlert=(action setSelectedAlert)
      riskScoreContext=riskScoreContext defaultAction=(action activate)}}`);

    assert.equal(find('.rsa-nav-tab.is-active .label').textContent.trim(), 'High', 'high tab is selected');
  });
});

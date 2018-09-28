import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/risk-properties/alert-context', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {

    await render(hbs`{{endpoint/risk-properties/alert-context}}`);

    assert.equal(findAll('.alert-context').length, 1, 'alert context related to risk is rendered');

  });

  test('show alert context for selected risk severity', async function(assert) {
    const contexts = [{
      alertName: 'test-alert1',
      alertCount: 1,
      eventCount: 10
    },
    {
      alertName: 'test-alert2',
      alertCount: 2,
      eventCount: 20
    }];

    this.set('contexts', contexts);

    await render(hbs`
      {{#endpoint/risk-properties/alert-context contexts=contexts as |context|}}
        <div class="alert-context__name">{{context.alertName}} ({{context.alertCount}})</div>
        <div class="alert-context__event">{{context.eventCount}} events</div>
      {{/endpoint/risk-properties/alert-context}}
    `);

    assert.equal(findAll('.alert-context__container').length, 2, 'Number of alert context containers should be 2.');

    assert.equal(findAll('.alert-context__name')[0].textContent.trim(), 'test-alert1 (1)',
                          'Display alert name and alert count for first alert context');
    assert.equal(findAll('.alert-context__event')[0].textContent.trim(), '10 events', 'Display 10 events for first alert context');

    assert.equal(findAll('.alert-context__name')[1].textContent.trim(), 'test-alert2 (2)',
                          'Display alert name and alert count for second alert context');
    assert.equal(findAll('.alert-context__event')[1].textContent.trim(), '20 events', 'Display 20 events for second alert context');

  });

});

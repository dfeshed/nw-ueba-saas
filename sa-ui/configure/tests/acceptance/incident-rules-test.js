import { test, module } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { findAll, click, visit, currentURL, settled, waitUntil } from '@ember/test-helpers';

const timeout = 20000;

module('Acceptance | Component | Respond Incident Rules', function(hooks) {
  setupApplicationTest(hooks);

  test('clicking link will load the detail for incident rule', async function(assert) {
    assert.expect(2);
    const done = waitForSockets();
    await visit('/configure/respond/incident-rules');
    assert.equal(currentURL(), '/configure/respond/incident-rules');
    const incidentRuleSelector = '[test-id=incidentRuleLink]';
    await waitUntil(() => findAll(incidentRuleSelector).length > 0, { timeout });
    const [ firstRow ] = findAll(incidentRuleSelector);
    await click(firstRow);
    await waitUntil(() => currentURL() === '/configure/respond/incident-rule/59b92bbf4cb0f0092b6b6a8a', { timeout });
    assert.equal(currentURL(), '/configure/respond/incident-rule/59b92bbf4cb0f0092b6b6a8a');
    await settled().then(() => done());
  });
});

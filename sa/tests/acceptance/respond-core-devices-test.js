import { test, module } from 'qunit';
import { setupApplicationTest } from 'ember-qunit';
import { setupLoginTest, login } from '../helpers/setup-login';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { findAll, click, visit, currentURL, settled, waitUntil } from '@ember/test-helpers';

const timeout = 20000;

module('Acceptance | Respond | core devices lookup', function(hooks) {
  setupApplicationTest(hooks);
  setupLoginTest(hooks);

  test('incident detail route should fetch core devices for recon integration', async function(assert) {
    assert.expect(2);

    const done = waitForSockets();

    await visit('/');
    await waitUntil(() => currentURL() === '/login', { timeout });
    await login();

    await waitUntil(() => currentURL() === '/respond/incidents', { timeout });

    await visit('/respond/incident/INC-102');

    await waitUntil(() => currentURL() === '/respond/incident/INC-102', { timeout });

    assert.equal(currentURL(), '/respond/incident/INC-102');

    const incidentsSelector = '[test-id=incidentInspectorIndicators]';
    await click(incidentsSelector);

    const alertsSelector = '[test-id=alertsTableSection]';
    const eventAnalysisSelector = '[test-id=alertsTableReconVisualCue]';
    const toggleEventsSelector = '[test-id=alertsTableToggleEvents]';
    const completeEventAnalysisSelector = `${alertsSelector}:nth-of-type(2) ${toggleEventsSelector} > ${eventAnalysisSelector}`;

    await waitUntil(() => findAll(completeEventAnalysisSelector).length === 1, { timeout });
    assert.equal(findAll(completeEventAnalysisSelector).length, 1);

    await settled().then(() => done());
  });
});

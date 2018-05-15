import { test } from 'qunit';
import { run } from '@ember/runloop';
import wait from 'ember-test-helpers/wait';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import teardownSockets from '../helpers/teardown-sockets';
import { waitForSockets } from '../helpers/wait-for-sockets';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';

moduleForAcceptance('Acceptance | storyline', {
  resolver: engineResolverFor('respond'),
  afterEach() {
    teardownSockets.apply(this);
  }
});

test('incident details storyline events open event analysis on click', function(assert) {
  assert.expect(8);

  const done = waitForSockets();

  const indicatorsSelector = '[test-id=incidentInspectorIndicators]';
  const toggleEventsSelector = '[test-id=alertsTableToggleEvents]';
  const reconLinkSelector = '[test-id=respondReconLink]';
  const reconWrapperSelector = '[test-id=reconRespondWrapper]';
  const closeReconButton = '[test-id=reconRespondCloseBtn]';

  visit('/respond/incident/INC-123');

  andThen(function() {
    assert.equal(currentURL(), '/respond/incident/INC-123', 'The route starts at incident details');
    assert.equal(find(indicatorsSelector).length, 1);
  });

  click(indicatorsSelector);

  andThen(function() {
    return waitFor(() => find(toggleEventsSelector).length > 0);
  });

  andThen(function() {
    assert.ok(find(toggleEventsSelector).length > 0);
  });

  click(toggleEventsSelector);

  andThen(function() {
    assert.equal(currentURL(), '/respond/incident/INC-123', 'The route remains incident details');
    assert.ok(find(reconLinkSelector).length > 0);
  });

  click(`${reconLinkSelector} .recon-link-to:eq(0)`);

  andThen(function() {
    return wait().then(() => {
      assert.equal(find(reconWrapperSelector).length, 1);
      assert.equal(currentURL(), '/respond/incident/INC-123/recon?endpointId=555d9a6fe4b0d37c827d402d&eventId=150', 'The route has changed to recon and includes queryParams for eventId and endpointId');

      run(() => {
        click(closeReconButton);
        return waitFor(() => currentURL && currentURL() === '/respond/incident/INC-123').then(() => {
          assert.ok(true, 'The route has dropped the queryParams because recon was closed');
          return wait().then(() => done());
        });
      });
    });
  });
});

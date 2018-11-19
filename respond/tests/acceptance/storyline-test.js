import { test } from 'qunit';
import { run } from '@ember/runloop';
import wait from 'ember-test-helpers/wait';
import { waitUntil } from '@ember/test-helpers';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import teardownSockets from '../helpers/teardown-sockets';
import { waitForSockets } from '../helpers/wait-for-sockets';

const indicatorsSelector = '[test-id=incidentInspectorIndicators]';
const toggleEventsSelector = '[test-id=alertsTableToggleEvents]';
const reconLinkSelector = '[test-id=respondReconLink]';
const reconWrapperSelector = '[test-id=reconRespondWrapper]';
const closeReconButton = '[test-id=reconRespondCloseBtn]';

moduleForAcceptance('Acceptance | storyline', {
  resolver: engineResolverFor('respond'),
  afterEach() {
    teardownSockets.apply(this);
  }
});

test('incident details storyline events open event analysis on click', function(assert) {
  assert.expect(8);

  const done = waitForSockets();

  visit('/respond/incident/INC-123');

  andThen(function() {
    assert.equal(currentURL(), '/respond/incident/INC-123', 'The route starts at incident details');
    assert.equal(find(indicatorsSelector).length, 1);
  });

  click(indicatorsSelector);

  andThen(function() {
    return waitUntil(() => find(toggleEventsSelector).length > 0);
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

      const queryParamsRegex = new RegExp(/respond\/incident\/INC-123\/recon\?endpointId=555d9a6fe4b0d37c827d402d&eventId=150&selection=(.*)/);
      const theCurrentUrl = currentURL();
      const urlHadQueryParams = theCurrentUrl.match(queryParamsRegex);
      assert.ok(!!urlHadQueryParams, 'The route has changed to recon and includes queryParams for eventId, endpointId and selection');

      run(() => {
        click(closeReconButton);
        return waitUntil(() => currentURL && currentURL() === '/respond/incident/INC-123').then(() => {
          assert.ok(true, 'The route has dropped the queryParams because recon was closed');
          return wait().then(() => done());
        });
      });
    });
  });
});

test('incident details will rehydrate event analysis when starting from recon url with queryParams', function(assert) {
  assert.expect(3);

  const done = waitForSockets();

  visit('/respond/incident/INC-123/recon?endpointId=555d9a6fe4b0d37c827d402d&eventId=150&selection=586ecf95ecd25950034e1312%3A0');

  andThen(() => {
    assert.equal(currentURL(), '/respond/incident/INC-123/recon?endpointId=555d9a6fe4b0d37c827d402d&eventId=150&selection=586ecf95ecd25950034e1312%3A0', 'The route starts at incident details with given queryParams');
  });

  andThen(function() {
    return wait().then(() => {
      assert.equal(find(reconWrapperSelector).length, 1);

      run(() => {
        click(closeReconButton);
        return waitUntil(() => currentURL && currentURL() === '/respond/incident/INC-123').then(() => {
          assert.ok(true, 'The route has dropped the queryParams because recon was closed');
          return wait().then(() => done());
        });
      });
    });
  });
});

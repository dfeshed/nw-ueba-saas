import { skip, test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import teardownSockets from 'sa/tests/helpers/teardown-sockets';

moduleForAcceptance('Acceptance | investigate', {
  afterEach() {
    teardownSockets.apply(this, arguments);
  }
});

test('investigate will open recon panel when eventId and endpointId queryParams present', function(assert) {
  visit('/investigate/recon?eventId=5&endpointId=555d9a6fe4b0d37c827d402e');
  andThen(function() {
    assert.equal(currentURL(), '/investigate/recon?eventId=5&endpointId=555d9a6fe4b0d37c827d402e');
    assert.equal(find('[test-id=reconInvestigateWrapper]').length, 1);
  });
});

// deeply nested URLs do not redirect back
skip('investigate\'s query results page does not redirect back to investigate-events.index', function(assert) {
  const url = '/investigate/events/query/0/0/0';
  visit(url);
  andThen(function() {
    assert.equal(currentURL(), url, 'correct path remains.');
  });
});

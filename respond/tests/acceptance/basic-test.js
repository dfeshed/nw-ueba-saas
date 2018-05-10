import { test } from 'qunit';
import wait from 'ember-test-helpers/wait';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import teardownSockets from '../helpers/teardown-sockets';
import { waitForSockets } from '../helpers/wait-for-sockets';

moduleForAcceptance('Acceptance | basic', {
  resolver: engineResolverFor('respond'),
  afterEach() {
    teardownSockets.apply(this);
  }
});

test('visiting an unknown route redirects to /respond/incidents', function(assert) {
  assert.expect(1);

  const done = waitForSockets();

  visit('/respond/blahblah');

  andThen(function() {
    assert.equal(currentURL(), '/respond/incidents', 'The base /respond endpoint should redirect to respond/incidents');
  });

  andThen(function() {
    return wait().then(() => done());
  });
});

test('visiting /respond redirects to /respond/incidents', function(assert) {
  assert.expect(1);

  const done = waitForSockets();

  visit('/respond');

  andThen(function() {
    assert.equal(currentURL(), '/respond/incidents', 'The base /respond endpoint should redirect to respond/incidents');
  });

  andThen(function() {
    return wait().then(() => done());
  });
});

test('visiting /respond/incident/INC-123', function(assert) {
  assert.expect(1);

  const done = waitForSockets();

  visit('/respond/incident/INC-123');

  andThen(function() {
    assert.equal(currentURL(), '/respond/incident/INC-123', 'The route loads and we are not redirected');
  });

  andThen(function() {
    return wait().then(() => done());
  });
});

test('visiting /respond/alerts', function(assert) {
  assert.expect(1);

  const done = waitForSockets();

  visit('/respond/alerts');

  andThen(function() {
    assert.equal(currentURL(), '/respond/alerts', 'The route loads and we are not redirected');
  });

  andThen(function() {
    return wait().then(() => done());
  });
});

test('visiting /respond/alert/12345', function(assert) {
  assert.expect(1);

  const done = waitForSockets();

  visit('/respond/alert/12345');

  andThen(function() {
    assert.equal(currentURL(), '/respond/alert/12345', 'The route loads and we are not redirected');
  });

  andThen(function() {
    return wait().then(() => done());
  });
});

test('visiting /respond/tasks', function(assert) {
  assert.expect(1);

  const done = waitForSockets();

  visit('/respond/tasks');

  andThen(function() {
    assert.equal(currentURL(), '/respond/tasks', 'The route loads and we are not redirected');
  });

  andThen(function() {
    return wait().then(() => done());
  });
});

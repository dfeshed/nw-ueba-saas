import { test } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import engineResolverFor from '../helpers/engine-resolver';
import teardownSockets from '../helpers/teardown-sockets';

moduleForAcceptance('Acceptance | basic', {
  resolver: engineResolverFor('respond'),
  afterEach: teardownSockets
});

test('visiting an unknown route redirects to /respond/incidents', function(assert) {
  visit('/respond/blahblah');

  andThen(function() {
    assert.equal(currentURL(), '/respond/incidents', 'The base /respond endpoint should redirect to respond/incidents');
  });
});

test('visiting /respond redirects to /respond/incidents', function(assert) {
  visit('/respond');

  andThen(function() {
    assert.equal(currentURL(), '/respond/incidents', 'The base /respond endpoint should redirect to respond/incidents');
  });
});

test('visiting /respond/incident/INC-123', function(assert) {
  visit('/respond/incident/INC-123');

  andThen(function() {
    assert.equal(currentURL(), '/respond/incident/INC-123', 'The route loads and we are not redirected');
  });
});

test('visiting /respond/alerts', function(assert) {
  visit('/respond/alerts');

  andThen(function() {
    assert.equal(currentURL(), '/respond/alerts', 'The route loads and we are not redirected');
  });
});

test('visiting /respond/alert/12345', function(assert) {
  visit('/respond/alert/12345');

  andThen(function() {
    assert.equal(currentURL(), '/respond/alert/12345', 'The route loads and we are not redirected');
  });
});

test('visiting /respond/tasks', function(assert) {
  visit('/respond/tasks');

  andThen(function() {
    assert.equal(currentURL(), '/respond/tasks', 'The route loads and we are not redirected');
  });
});
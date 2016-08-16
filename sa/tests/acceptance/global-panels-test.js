import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | global panels', {
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('toggling notifications panel', function(assert) {
  assert.expect(2);
  visit('/do/monitor');
  click('.notifications-panel-trigger');

  andThen(function() {
    assert.equal(find('.rsa-application-notifications-panel.is-expanded').length, 1);
    click('.notifications-panel-trigger');

    andThen(function() {
      assert.equal(find('.rsa-application-notifications-panel.is-expanded').length, 0);
    });
  });
});

test('toggling incident queue panel', function(assert) {
  assert.expect(2);
  visit('/do/monitor');
  click('.incident-queue-trigger');

  andThen(function() {
    assert.equal(find('.rsa-application-incident-queue-panel.is-expanded').length, 1);
    click('.incident-queue-trigger');

    andThen(function() {
      assert.equal(find('.rsa-application-incident-queue-panel.is-expanded').length, 0);
    });
  });
});

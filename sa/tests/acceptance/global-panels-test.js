import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | global panels', {
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

// test('toggling notifications panel', function(assert) {
//   assert.expect(2);
//   visit('/do/monitor');
//   click('.notifications-panel-trigger');
//
//   andThen(function() {
//     assert.equal(find('.rsa-application-notifications-panel.is-expanded').length, 1);
//     click('.notifications-panel-trigger');
//
//     andThen(function() {
//       assert.equal(find('.rsa-application-notifications-panel.is-expanded').length, 0);
//     });
//   });
// });

test('toggling incident queue panel', function(assert) {
  assert.expect(10);
  visit('/do/monitor');
  click('.incident-queue-trigger');

  andThen(function() {
    assert.equal(find('.rsa-application-incident-queue-panel.is-expanded').length, 1, 'Testing to see if Incident queue panel is expanded');
    assert.equal(find('.rsa-application-incident-queue-panel__tab').length, 1, 'Testing to see incident queue has tabs');
    assert.equal(find('.js-my-incidents.is-active').length, 1, 'Check if my incidents is active');
    assert.equal(find('.js-all-incidents').length, 1, 'Check all incidents tab is present');
    assert.equal(find('.js-all-incidents.is-active').length, 0, 'Check all incidents tab is not active');
    assert.ok(find('.rsa-content-card').length > 0, 'Incidents queue is populated');

    click('.js-all-incidents');
    andThen(()=>{
      assert.equal(find('.js-all-incidents.is-active').length, 1, 'when all incidents is clicked, check all incidents is active');
      assert.equal(find('.js-my-incidents.is-active').length, 0, 'check my incidents is not active');
      assert.ok(find('.rsa-content-card').length > 0, 'Incidents queue is populated');
    });

    click('.incident-queue-trigger');
    andThen(function() {
      assert.equal(find('.rsa-application-incident-queue-panel.is-expanded').length, 0, 'check side panel closes when clicked again');
    });
  });
});

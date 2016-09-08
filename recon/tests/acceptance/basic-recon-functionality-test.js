import { test } from 'qunit';
import moduleForAcceptance from '../../tests/helpers/module-for-acceptance';
import Ember from 'ember';
const { run } = Ember;

moduleForAcceptance('Acceptance | basic recon functionality');

test('show/hide header items', function(assert) {
  visit('/');

  andThen(function() {
    run.later(this, function() {
      assert.ok(find('.recon-event-header .header-item').length > 0);
    }, 100);
  });

  click('.recon-event-titlebar .action-buttons .toggle-header');

  andThen(function() {
    run.later(this, function() {
      assert.ok(find('.recon-event-header .header-item').length === 0);
    }, 100);
  });
});

test('show/hide meta', function(assert) {
  visit('/');

  andThen(function() {
    run.later(this, function() {
      assert.ok(find('.recon-meta-content').length === 0, 'Meta is hidden');
    }, 100);
  });

  click('.recon-event-titlebar .action-buttons .toggle-meta');

  andThen(function() {
    run.later(this, function() {
      assert.ok(find('.recon-meta-content').length === 1, 'Meta is shown');
    }, 100);
  });
});

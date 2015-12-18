import Ember from 'ember';
import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import config from 'sa/config/environment';

moduleForAcceptance('Acceptance | theme');

test('visit default protected path, set theme and check DOM', function(assert) {
  assert.expect(3);

  visit('/');

  andThen(function() {

    // Find the theme button.
    const btn = find('.js-test-nav-theme-btn');
    assert.equal(btn.length, 1, 'Could not find the Theme button DOM.');

    // Click it to open the theme popover.
    btn.trigger('click');

    // Find the Dark theme option in the theme popover. Since it is in the popover DOM, make sure to look
    // throughout the entire document.body, not just in the local DOM.
    const el = find('.js-test-menlo-park-theme-li', document.body);
    assert.equal(el.length, 1, 'Could not find the Menlo Park Theme option DOM.');

    // Click it to set the theme to Dark.
    el.trigger('click');

    // Confirm that the DOM has changed to Dark theme.
    const rootSelector = config.APP.rootElement || 'body',
        $root = Ember.$(rootSelector);
    assert.ok(
      $root.hasClass('rsa-menlo-park'),
      'Chose the Menlo Park theme, but could not find the corresponding CSS class applied to DOM.'
    );

  });

});

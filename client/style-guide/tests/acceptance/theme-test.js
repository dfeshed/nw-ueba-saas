import { test } from 'qunit';
import moduleForAcceptance from 'style-guide/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | theme');

test('available themes', function(assert) {
  visit('/');
  click('button.rsa-application-select-theme');

  andThen(function() {
    let themes = find('.rsa-application-themes li');
    assert.equal(themes.length, 2, 'wrong number of themes');
  });
});

test('toggling themes', function(assert) {
  visit('/');
  click('button.rsa-application-select-theme');
  click('.rsa-application-themes li:last-child');

  andThen(function() {
    assert.equal(find('.rsa-light', document).length, 1, 'was not light');
  });
});

test('toggling themes', function(assert) {
  visit('/');
  click('button.rsa-application-select-theme');
  click('.rsa-application-themes li:last-child');
  click('button.rsa-application-select-theme');
  click('.rsa-application-themes li:first-child');

  andThen(function() {
    assert.equal(find('.rsa-dark', document).length, 1, 'was not dark');
  });
});

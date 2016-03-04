import { test } from 'qunit';
import moduleForAcceptance from 'style-guide/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | i18n');

test('available locales', function(assert) {
  visit('/');
  click('button.rsa-application-select-locale');

  andThen(function() {
    let locales = find('.rsa-application-locales li');
    assert.equal(locales.length, 2, 'wrong number of locales');
  });
});

test('toggling themes', function(assert) {
  visit('/');
  click('button.rsa-application-select-locale');
  click('.rsa-application-locales li:last-child');

  andThen(function() {
    let en = find('.rsa-application-locales li:first-child').text();
    assert.equal(en, 'jp_English', 'was not Japanese');
  });
});

test('toggling themes', function(assert) {
  visit('/');
  click('button.rsa-application-select-locale');
  click('.rsa-application-locales li:last-child');
  click('button.rsa-application-select-locale');
  click('.rsa-application-locales li:first-child');

  andThen(function() {
    let en = find('.rsa-application-locales li:first-child').text();
    assert.equal(en, 'English', 'was not English');
  });
});

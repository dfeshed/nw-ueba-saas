import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | preference panel', {
  // After each test, destroy the MockServer instances we've created (if any), so that the next test will not
  // throw an error when it tries to re-create them.
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('Iteration: verify all options are available in components', function(assert) {
  assert.expect(6);
  visit('/do/monitor');
  click('.user-preferences-trigger');
  click('.js-test-user-preferences-modal');

  andThen(function() {
    // iterate language select options
    assert.deepEqual(find('#modalDestination .js-test-language-select select option')
        .map(function() {
          return $(this).text().trim();
        }).get(), ['English', 'Japanese'], 'Language');

    // time zone has too many options. Skip it.

    // iterate date format options.
    assert.deepEqual(find('#modalDestination .js-test-date-format-select select option')
        .map(function() {
          return $(this).text().trim();
        }).get(), ['MM/DD/YYYY', 'DD/MM/YYYY', 'YYYY/MM/DD'], 'Date Format');

    // iterate time format options.
    assert.deepEqual(find('#modalDestination .time-format-radio-group .rsa-form-radio')
        .map(function() {
          return $(this).text().trim();
        }).get(), ['12hr', '24hr'], 'Time Format');

    // iterate default landing page options.
    assert.deepEqual(find('#modalDestination .js-test-default-landing-page-select select option')
        .map(function() {
          return $(this).text().trim();
        }).get(), ['Respond', 'Monitor', 'Admin', 'Investigate'], 'Default Landing Page');

    // iterate theme options.
    assert.deepEqual(find('#modalDestination .theme-radio-group .rsa-form-radio')
        .map(function() {
          return $(this).text().trim();
        }).get(), ['Light', 'Dark'], 'Theme');

    // iterate spacing options.
    assert.deepEqual(find('#modalDestination .spacing-radio-group .rsa-form-radio')
        .map(function() {
          return $(this).text().trim();
        }).get(), ['Tight', 'Loose'], 'Spacing');
  });
});

test('User can set preferences and the values are stored in local storage', function(assert) {
  assert.expect(9);

  visit('/do/monitor');
  click('.user-preferences-trigger');
  click('.js-test-user-preferences-modal');

  // set language to Japanese
  fillIn('#modalDestination .js-test-language-select select', 'ja');
  // set time zone to UTC
  fillIn('#modalDestination .js-test-time-zone-select select', 'UTC');
  // set date format to YYYY/MM/DD
  fillIn('#modalDestination .js-test-date-format-select select', 'YYYY/MM/DD');
  // set time format to 12hr
  click('#modalDestination .time-format-radio-group .rsa-form-radio:first-of-type input');
  // set default landing page to Admin
  fillIn('#modalDestination .js-test-default-landing-page-select select', 'protected.admin');
  // set theme to light
  click('#modalDestination .theme-radio-group .rsa-form-radio:first-of-type input');
  // set spacing to tight
  click('#modalDestination .spacing-radio-group .rsa-form-radio:first-of-type input');
  // set notifications to uncheck
  click('#modalDestination .js-test-notifications-checkbox input');
  // set context menus to uncheck
  click('#modalDestination .js-test-context-menus-checkbox input');
  // click Apply button
  click('#modalDestination .js-test-apply button');

  andThen(function() {
    assert.equal(localStorage['rsa-i18n-default-locale'], 'ja', 'Language');
    assert.equal(localStorage['rsa::securityAnalytics::timeZonePreference'],
     'UTC', 'Time Zone');
    assert.equal(localStorage['rsa::securityAnalytics::dateFormatPreference'],
     'YYYY/MM/DD', 'Date Format');
    assert.equal(localStorage['rsa::securityAnalytics::timeFormatPreference'],
     '12hr', 'Time Format');
    assert.equal(localStorage['rsa::securityAnalytics::landingPagePreference'],
     'protected.admin', 'Default Landing Page');
    assert.equal(localStorage['rsa::securityAnalytics::themePreference'],
     'light', 'Theme');
    assert.equal(localStorage['rsa::securityAnalytics::spacingPreference'],
     'tight', 'Spacing');
    assert.equal(localStorage['rsa::securityAnalytics::notificationsPreference'],
     'false', 'Notifications');
    assert.equal(localStorage['rsa::securityAnalytics::contextMenuPreference'],
     'false', 'Context Menus');
  });
});

test('Preference panel can load preference values from local storage.', function(assert) {
  assert.expect(9);

  localStorage.setItem('rsa-i18n-default-locale', 'ja');
  localStorage.setItem('rsa::securityAnalytics::timeZonePreference', 'UTC');
  localStorage.setItem('rsa::securityAnalytics::dateFormatPreference', 'YYYY/MM/DD');
  localStorage.setItem('rsa::securityAnalytics::timeFormatPreference', '12hr');
  localStorage.setItem('rsa::securityAnalytics::landingPagePreference', 'protected.admin');
  localStorage.setItem('rsa::securityAnalytics::themePreference', 'light');
  localStorage.setItem('rsa::securityAnalytics::spacingPreference', 'tight');
  localStorage.setItem('rsa::securityAnalytics::notificationsPreference', 'false');
  localStorage.setItem('rsa::securityAnalytics::contextMenuPreference', 'false');

  visit('/do/monitor');
  click('.user-preferences-trigger');
  click('.js-test-user-preferences-modal');
  andThen(function() {
    // verify language is Japanese
    assert.equal(find('#modalDestination .js-test-language-select select' +
        ' option:selected').text(), 'ja_Japanese', 'Language');
    // verify time zone is UTC
    assert.equal(find('#modalDestination .js-test-time-zone-select select' +
        ' option:selected').text(), 'UTC', 'Time Zone');
    // verify date format is YYYY/MM/DD
    assert.equal(find('#modalDestination .js-test-date-format-select select' +
        ' option:selected').text(), 'ja_YYYY/MM/DD', 'Date Format');
    // verify time format to be 12hr
    assert.equal(find('#modalDestination .time-format-radio-group' +
        ' .rsa-form-radio.is-selected').text().trim(), 'ja_12hr', 'Time Format');
    // verify default landing page is Admin
    assert.equal(find('#modalDestination .js-test-default-landing-page-select select' +
        ' option:selected').text(), 'ja_Admin', 'Default Landing Page');
    // verify theme is Light
    assert.equal(find('#modalDestination .theme-radio-group' +
        ' .rsa-form-radio.is-selected').text().trim(), 'ja_Light', 'Theme');
    // verify spacing is Tight
    assert.equal(find('#modalDestination .spacing-radio-group' +
        ' .rsa-form-radio.is-selected').text().trim(), 'ja_Tight', 'Spacing');
    // verify notification is unchecked.
    assert.equal(find('#modalDestination .js-test-notifications-checkbox' +
        ' input').attr('value'), 'false', 'Notification');
    // verify context menus is unchecked.
    assert.equal(find('#modalDestination .js-test-context-menus-checkbox' +
        ' input').attr('value'), 'false', 'Context Menus');
  });
});

test('User can reset to undo current changes in the panel.', function(assert) {
  assert.expect(2);
  visit('/do/monitor');
  click('.user-preferences-trigger');
  click('.js-test-user-preferences-modal');

  // set language to Japanese
  fillIn('#modalDestination .js-test-language-select select', 'ja');
  // set theme to light
  click('#modalDestination .theme-radio-group .rsa-form-radio:first-of-type input');
  // click Reset button
  click('#modalDestination .js-test-revert span');

  // reopen the preference panel
  click('.user-preferences-trigger');
  click('.js-test-user-preferences-modal');

  andThen(function() {
    // verify language is English
    assert.equal(find('#modalDestination .js-test-language-select select' +
        ' option:selected').text(), 'English', 'Language');
    // verify theme is Dark
    assert.equal(find('#modalDestination .theme-radio-group' +
        ' .rsa-form-radio.is-selected').text().trim(), 'Dark', 'Theme');
  });
});

test('Error has correct message when password and confirmation do not match', function(assert) {
  assert.expect(1);
  visit('/do/monitor');
  click('.user-preferences-trigger');
  click('.js-test-user-preferences-modal');

  // enter password
  fillIn('#modalDestination .js-test-new-password input', 'asdf');
  // enter a different confirmation password
  fillIn('#modalDestination .js-test-confirm-password input', 'fdsa');

  andThen(function() {
    // verify Error has the correct message
    assert.equal(find('#modalDestination .js-test-new-password i').attr('title'),
     'Password and confirmation do not match', 'Password and confirmation do not match');
  });
});
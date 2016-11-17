import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import teardownSockets from 'sa/tests/helpers/teardown-sockets';

const trimText = function() {
  return window.$(this).text().trim();
};

moduleForAcceptance('Acceptance | preference panel', {
  afterEach: teardownSockets
});

test('Iteration: verify all options are available in components', function(assert) {
  assert.expect(4);
  visit('/do/monitor');
  click('.user-preferences-trigger');
  click('.js-test-user-preferences-modal');

  andThen(() => {
    // iterate language select options
    click('#modalDestination .js-test-language-select .ember-power-select-trigger');
    andThen(() => {
      assert.deepEqual(find('.ember-power-select-dropdown .ember-power-select-option')
          .map(trimText).get(),
          ['English', 'Japanese'], 'Language');
    });

    // // time zone has too many options. Skip it.

    // // iterate date format options.
    click('#modalDestination .js-test-date-format-select .ember-power-select-trigger');
    andThen(() => {
      assert.deepEqual(find('.ember-power-select-dropdown .ember-power-select-option')
          .map(trimText).get(),
          ['MM/DD/YYYY', 'DD/MM/YYYY', 'YYYY/MM/DD'], 'Date Format');
    });


    // // iterate default landing page options.
    click('#modalDestination .js-test-default-landing-page-select .ember-power-select-trigger');
    andThen(() => {
      assert.deepEqual(find('.ember-power-select-dropdown .ember-power-select-option')
          .map(trimText).get(),
          ['Respond', 'Monitor', 'Admin', 'Investigate'], 'Default Landing Page');
    });

    // iterate time format options.
    assert.deepEqual(find('#modalDestination .time-format-radio-group .rsa-form-radio')
        .map(trimText).get(),
        ['12hr', '24hr'], 'Time Format');

  });
});

test('User can set preferences and the values are stored in local storage', function(assert) {
  assert.expect(7);
  visit('/do/monitor');
  click('.user-preferences-trigger');
  click('.js-test-user-preferences-modal');

  // set language to Japanese
  click('#modalDestination .js-test-language-select .ember-power-select-trigger');
  andThen(() => {
    click('.ember-power-select-dropdown .ember-power-select-option:nth-child(2)');
  });

  // set time zone to UTC
  click('#modalDestination .js-test-time-zone-select .ember-power-select-trigger');
  andThen(() => {
    click('.ember-power-select-dropdown .ember-power-select-option:nth-child(2)');
  });

  // set date format to YYYY/MM/DD
  click('#modalDestination .js-test-date-format-select .ember-power-select-trigger');
  andThen(() => {
    click('.ember-power-select-dropdown .ember-power-select-option:nth-child(2)');
  });

  // set default landing page to Admin
  click('#modalDestination .js-test-default-landing-page-select .ember-power-select-trigger');
  andThen(() => {
    click('.ember-power-select-dropdown .ember-power-select-option:nth-child(2)');
  });

  // set time format to 12hr
  click('#modalDestination .time-format-radio-group .rsa-form-radio:first-of-type input');
  // set notifications to uncheck
  click('#modalDestination .js-test-notifications-checkbox input');
  // set context menus to uncheck
  click('#modalDestination .js-test-context-menus-checkbox input');
  // click Apply button
  click('#modalDestination .js-test-apply button');

  andThen(() => {
    assert.equal(localStorage['rsa-i18n-default-locale'], 'ja', 'Language');

    assert.equal(localStorage['rsa::securityAnalytics::timeZonePreference'],
     'Africa/Accra', 'Time Zone');
    assert.equal(localStorage['rsa::securityAnalytics::dateFormatPreference'],
     'DD/MM/YYYY', 'Date Format');
    assert.equal(localStorage['rsa::securityAnalytics::timeFormatPreference'],
     '12hr', 'Time Format');
    assert.equal(localStorage['rsa::securityAnalytics::landingPagePreference'],
     'protected.monitor', 'Default Landing Page');
    assert.equal(localStorage['rsa::securityAnalytics::notificationsPreference'],
     'false', 'Notifications');
    assert.equal(localStorage['rsa::securityAnalytics::contextMenuPreference'],
     'false', 'Context Menus');
  });
});

test('Preference panel can load preference values from local storage.', function(assert) {
  assert.expect(7);

  localStorage.setItem('rsa-i18n-default-locale', 'ja');
  localStorage.setItem('rsa::securityAnalytics::timeZonePreference', 'UTC');
  localStorage.setItem('rsa::securityAnalytics::dateFormatPreference', 'YYYY/MM/DD');
  localStorage.setItem('rsa::securityAnalytics::timeFormatPreference', '12hr');
  localStorage.setItem('rsa::securityAnalytics::landingPagePreference', 'protected.admin');
  localStorage.setItem('rsa::securityAnalytics::notificationsPreference', 'false');
  localStorage.setItem('rsa::securityAnalytics::contextMenuPreference', 'false');

  visit('/do/monitor');
  click('.user-preferences-trigger');
  click('.js-test-user-preferences-modal');
  andThen(() => {
    // verify language is Japanese
    assert.equal(
      find('#modalDestination .js-test-language-select .ember-power-select-trigger').text().trim(),
      'ja_Japanese'
    );
    // verify time zone is UTC
    assert.equal(
      find('#modalDestination .js-test-time-zone-select .ember-power-select-trigger').text().trim(),
       'UTC'
     );
    // verify date format is YYYY/MM/DD
    assert.equal(
      find('#modalDestination .js-test-date-format-select .ember-power-select-trigger').text().trim(),
      'ja_YYYY/MM/DD'
    );
    // verify default landing page is Admin
    assert.equal(
      find('#modalDestination .js-test-default-landing-page-select .ember-power-select-trigger').text().trim(),
      'ja_Admin'
    );

    // verify notification is unchecked.
    assert.equal(find('#modalDestination .js-test-notifications-checkbox' +
        ' input').attr('value'), 'false', 'Notification');
    // verify context menus is unchecked.
    assert.equal(find('#modalDestination .js-test-context-menus-checkbox' +
        ' input').attr('value'), 'false', 'Context Menus');
    // verify time format to be 12hr
    assert.equal(find('#modalDestination .time-format-radio-group' +
        ' .rsa-form-radio.is-selected').text().trim(), 'ja_12hr', 'Time Format');
  });
});

test('User can reset to undo current changes in the panel.', function(assert) {
  assert.expect(1);
  visit('/do/monitor');
  click('.user-preferences-trigger');
  click('.js-test-user-preferences-modal');

  // set language to Japanese
  click('#modalDestination .js-test-language-select .ember-power-select-trigger');
  andThen(() => {
    click('.ember-power-select-dropdown .ember-power-select-option:last-of-type');
  });
  // click Reset button
  click('#modalDestination .js-test-revert .rsa-form-button');

  // reopen the preference panel
  click('.user-preferences-trigger');
  click('.js-test-user-preferences-modal');

  andThen(() => {
    // verify language is English
    assert.equal(find('#modalDestination .js-test-language-select .ember-power-select-trigger').text().trim(), 'English', 'Language');
  });
});

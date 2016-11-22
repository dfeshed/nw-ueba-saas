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
          ['Dashboard', 'Respond', 'Investigate', 'Investigate Classic', 'Live', 'Admin'], 'Default Landing Page');
    });

    // iterate time format options.
    assert.deepEqual(find('#modalDestination .time-format-radio-group .rsa-form-radio')
        .map(trimText).get(),
        ['12hr', '24hr'], 'Time Format');

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

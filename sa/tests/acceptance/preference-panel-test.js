import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';

const trimText = function() {
  return window.$(this).text().trim();
};

moduleForAcceptance('Acceptance | preference panel', {});

test('Iteration: verify all options are available in components', function(assert) {
  assert.expect(6);
  visit('/respond/incidents');
  click('.user-preferences-trigger');

  andThen(() => {
    // for 11.1 we only support english so no dropdown options are present for language
    // iterate language select options
    // click('.rsa-application-user-preferences-panel .js-test-language-select .ember-power-select-trigger');
    // andThen(() => {
    //   assert.deepEqual(find('.ember-power-select-dropdown .ember-power-select-option')
    //       .map(trimText).get(),
    //       ['English'], 'Language');
    // });

    assert.equal(find('.js-test-time-zone-select .ember-power-select-selected-item').text().trim(), 'Los Angeles (GMT-07:00)');

    // // iterate date format options.
    click('.rsa-application-user-preferences-panel .js-test-date-format-select .ember-power-select-trigger');
    andThen(() => {
      assert.deepEqual(find('.ember-power-select-dropdown .ember-power-select-option')
          .map(trimText).get(),
          ['MM/DD/YYYY', 'DD/MM/YYYY', 'YYYY/MM/DD'], 'Date Format');
    });


    // // iterate default landing page options.
    click('.rsa-application-user-preferences-panel .js-test-default-landing-page-select .ember-power-select-trigger');
    andThen(() => {
      assert.deepEqual(find('.ember-power-select-dropdown .ember-power-select-option')
          .map(trimText).get(),
          ['Respond', 'Investigate', 'Monitor', 'Configure', 'Admin'], 'Default Landing Page');
    });

    // // iterate default investigate page landing options.
    click('.rsa-application-user-preferences-panel .js-test-default-investigate-page-select .ember-power-select-trigger');
    andThen(() => {
      assert.deepEqual(find('.ember-power-select-dropdown .ember-power-select-option')
          .map(trimText).get(),
          ['Navigate', 'Events', 'Event Analysis', 'Hosts', 'Files', 'Users', 'Malware Analysis'], 'Default Investigation Landing Page');
    });

    assert.equal(find('.rsa-application-user-preferences-panel .time-format-radio-group .rsa-form-radio-label.HR24').length, 1);
    assert.equal(find('.rsa-application-user-preferences-panel .time-format-radio-group .rsa-form-radio-label.HR12').length, 1);
  });
});

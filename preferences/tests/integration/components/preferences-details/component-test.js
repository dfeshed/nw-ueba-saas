import { moduleForComponent, skip, test } from 'ember-qunit';
import $ from 'jquery';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { clickTrigger, selectChoose } from '../../../helpers/ember-power-select';
import preferencesConfig from '../../../data/config';
import wait from 'ember-test-helpers/wait';

const assertForPreferencesPanelSelectedOptions = (waitFor, assert, child, index, selectedOption) => {
  $('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.rsa-preferences-field-content', { count: 5 }).then(() => {
    clickTrigger(`.rsa-preferences-field-content:nth-child(${child})`);
    return waitFor('.ember-power-select-options').then(function() {
      selectChoose(`.rsa-preferences-field-content:nth-child(${child})`, selectedOption);
      const done = assert.async();
      return wait().then(() => {
        const str = $('.ember-power-select-selected-item')[index].innerText.trim();
        assert.equal(str, selectedOption, 'Value needs to default to the user selected option');
        done();
      });
    });
  });

};

moduleForComponent('preferences-panel', 'Integration | Component | Preferences Details', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.set('preferencesConfig', preferencesConfig);
    initialize(this);
    this.render(hbs `
      {{#rsa-application-content}}
        <grid responsive>
          <box class="col-xs-3">
            <aside>
              {{preferences-panel-trigger preferencesConfig=preferencesConfig}}
              <div class='testDiv'>
                Panel Content
              </div>
            </aside>
          </box>
          <box class="col-xs-9">
            <page>
              {{preferences-panel}}
            </page>
          </box>
        </grid>
      {{/rsa-application-content}}`);
  }
});

test('Preferences panel opens correctly with all user selected preferences', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.ember-power-select-selected-item', { count: 3 }).then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
    assert.equal(this.$('.rsa-preferences-field-content').length, 5);
    let str = this.$('.ember-power-select-selected-item')[0].innerText.trim();
    assert.equal(str, 'Packet Analysis');
    str = this.$('.ember-power-select-selected-item')[1].innerText.trim();
    assert.equal(str, 'Download Log');
    str = this.$('.ember-power-select-selected-item')[2].innerText.trim();
    assert.equal(str, 'Download PCAP');
    assert.equal(this.$('.rsa-form-radio-label.DB.checked').length, 1);
    assert.equal(this.$('.rsa-form-checkbox-label.checked').length, 1);
  });
});

test('Preferences panel should show Time format Settings', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.rsa-form-radio-group-label').then(() => {
    assert.equal(this.$('.rsa-form-radio-label.DB.checked').length, 1);
  });
});

test('Preferences panel comes with valid options for Analysis', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.rsa-preferences-field-content', { count: 5 }).then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
    clickTrigger('.rsa-preferences-field-content:nth-child(1)');
    return waitFor('.ember-power-select-options').then(function() {
      const $options = $('.ember-power-select-option');
      assert.equal($options.length, 3);
      assert.equal($options.text().trim().replace(/\s+/g, ''), 'TextAnalysisPacketAnalysisFileAnalysis');
    });
  });
});

test('Preferences panel comes with valid options for packet format', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.rsa-preferences-field-content', { count: 5 }).then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
    clickTrigger('.rsa-preferences-field-content:nth-child(3)');
    return waitFor('.ember-power-select-options').then(function() {
      const $options = $('.ember-power-select-option');
      assert.equal($options.length, 4);
      assert.equal($options.text().trim().replace(/\s+/g, ''), 'DownloadPCAPDownloadAllPayloadsDownloadRequestPayloadDownloadResponsePayload');
    });
  });
});

test('Preferences panel comes with valid options for log format', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.rsa-preferences-field-content', { count: 5 }).then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
    clickTrigger('.rsa-preferences-field-content:nth-child(2)');
    return waitFor('.ember-power-select-options').then(function() {
      const $options = $('.ember-power-select-option');
      assert.equal($options.length, 4);
      assert.equal($options.text().trim().replace(/\s+/g, ''), 'DownloadLogDownloadCSVDownloadXMLDownloadJSON');
    });
  });
});

skip('Preferences panel saves new Analysis on change', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.ember-power-select-selected-item', { count: 3 }).then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
    clickTrigger();
    return waitFor('.ember-power-select-options').then(function() {
      $('li:contains("Events")').trigger('click');
      $('li:contains("Events")').click();
      click($('li:contains("Events")'));
      assert.ok($('.ember-power-select-selected-item').text().indexOf('Events') > 0);
    });
  });
});

test('Preferences panel defaults the Analysis View to the user selected value', function(assert) {
  return assertForPreferencesPanelSelectedOptions(waitFor, assert, 1, 0, 'File Analysis');
});

test('Preferences panel defaults the dowloadLogFormat to the user selected value', function(assert) {
  return assertForPreferencesPanelSelectedOptions(waitFor, assert, 2, 1, 'Download XML');
});

test('Preferences panel defaults the dowloadPacketFormat to the user selected value', function(assert) {
  return assertForPreferencesPanelSelectedOptions(waitFor, assert, 3, 2, 'Download Request Payload');
});

test('Preferences panel should change Time format Settings on click', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.rsa-form-radio-group-label').then(() => {
    assert.equal(this.$('.rsa-form-radio-label.DB.checked').length, 1);
    assert.equal(this.$('.rsa-form-radio-label.WALL.checked').length, 0);
    $('.rsa-form-radio-label.WALL').trigger('click');
    return waitFor('.rsa-form-radio-label.WALL.checked').then(() => {
      assert.equal(this.$('.rsa-form-radio-label.WALL.checked').length, 1);
    });
  });
});

test('Preferences panel should uncheck the Download automatically checkbox on click', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.rsa-form-checkbox-label').then(() => {
    assert.equal(this.$('.rsa-form-checkbox-label.checked').length, 1);
    $('.rsa-form-checkbox-label').trigger('click');
    return waitFor(() => {
      return this.$('.rsa-form-checkbox-label.checked').length === 0;
    }).then(function() {
      assert.equal($('.rsa-form-checkbox-label.checked').length, 0);
    });
  });
});
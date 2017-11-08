import { moduleForComponent, test, skip } from 'ember-qunit';
import $ from 'jquery';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { clickTrigger } from '../../../helpers/ember-power-select';

moduleForComponent('preferences-panel', 'Integration | Component | Preferences Details', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
    initialize(this);
    this.render(hbs `
      {{#rsa-application-content}}
        <grid responsive>
          <box class="col-xs-3">
            <aside>
              {{preferences-panel-trigger launchFor="investigate-events" additionalFilters="ServiceID"}}
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

skip('Preferences panel opens correctly with all user selected preferences', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.rsa-preferences-field-content', { count: 3 }).then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
    assert.equal(this.$('.rsa-preferences-field-content').length, 3);
    let str = this.$('.ember-power-select-selected-item')[0].innerText.trim();
    assert.equal(str, 'Packet Analysis');
    str = this.$('.ember-power-select-selected-item')[1].innerText.trim();
    assert.equal(str, 'Download XML');
    str = this.$('.ember-power-select-selected-item')[2].innerText.trim();
    assert.equal(str, 'Download Payload1');
  });
});

test('Preferences panel comes with valid options for Analysis', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.rsa-preferences-field-content', { count: 3 }).then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
    clickTrigger();
    return waitFor('.ember-power-select-options').then(function() {
      const $options = $('.ember-power-select-option');
      assert.equal($options.length, 3);
      assert.equal($options.text().trim().replace(/\s+/g, ''), 'TextAnalysisPacketAnalysisFileAnalysis');
    });
  });
});

test('Preferences panel comes with valid options for packet format', function(assert) {
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return waitFor('.rsa-preferences-field-content', { count: 3 }).then(() => {
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
  return waitFor('.rsa-preferences-field-content', { count: 3 }).then(() => {
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
  return waitFor('.rsa-preferences-field-content', { count: 3 }).then(() => {
    assert.equal(this.$('.is-expanded').length, 1, 'Preference Panel opened.');
    clickTrigger();
    return waitFor('.ember-power-select-options').then(function() {
      $('li:contains("Text Analysis")').trigger('click');
      $('li:contains("Text Analysis")').click();
      click($('li:contains("Text Analysis")'));
      assert.ok($('.ember-power-select-trigger').text().indexOf('Packet Analysis') > 0);
    });
  });
});

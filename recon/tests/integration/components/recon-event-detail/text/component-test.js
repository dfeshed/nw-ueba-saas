import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import DataHelper from '../../../../helpers/data-helper';

moduleForComponent('recon-event-detail-text', 'Integration | Component | recon event detail text', {
  integration: true,
  setup() {
    this.inject.service('redux');
  }
});

test('text view renders encoded text', function(assert) {
  new DataHelper(this.get('redux')).populateTexts();
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'requestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1$Host:www.saavn.comresponseHTTP/1.1200OKCache-control:no-store,no-cache,must-revalidate,');
  });
});

test('text view renders decoded text', function(assert) {
  new DataHelper(this.get('redux')).populateTexts(true);
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'requestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1Host:www.saavn.comConnection:keep-aliveAccept:*/*X-Requested-With:XMLHttpRequestUser-Age');
  });
});

// TODO - I don't know if this is a valid test anymore. What output would we see
// in the Text view for log data?
test('text view renders log text', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData()
    .setEventTypeToLog()
    .setViewToText();
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'Error:Notextdatawasgeneratedduringcontentreconstruction.Thiscouldmeanthattheeventdatawascorruptorinvalid.Checktheotherreconstructionviews.');
  });
});

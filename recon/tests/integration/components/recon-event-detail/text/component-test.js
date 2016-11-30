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

test('text view renders packet text', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData()
    .setViewToText();

  this.render(hbs`{{recon-event-detail/text}}`);

  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'requestGET/images/hp/thirds__sml_ad_3.jpgHTTP/1.1Host:www.bladehq.comAccept:*/*Connection:keep-aliveCookie:__atuvc=8%7C49;__atuvs=56679ca029696108007;_isuid=50728CB8-C07A-4F02-9CEC-35F63080AA0C;cat_id');
  });
});

test('text view renders log text', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData()
    .setEventTypeToLog()
    .setViewToText();

  this.render(hbs`{{recon-event-detail/text}}`);

  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'GET/images/hp/thirds__sml_ad_3.jpgHTTP/1.1Host:www.bladehq.comAccept:*/*Connection:keep-aliveCookie:__atuvc=8%7C49;__atuvs=56679ca029696108007;_isuid=50728CB8-C07A-4F02-9CEC-35F63080AA0C;cat_id=1;curr');
  });
});

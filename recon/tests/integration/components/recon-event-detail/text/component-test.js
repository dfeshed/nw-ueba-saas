import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import DataActions from 'recon/actions/data-creators';
import * as ACTION_TYPES from 'recon/actions/types';
import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import { determineEventType } from 'recon/utils/event-types';

moduleForComponent('recon-event-detail-text', 'Integration | Component | recon event detail text', {
  integration: true,
  setup() {
    this.inject.service('redux');
  }
});

test('text view renders packet text', function(assert) {
  assert.expect(1);

  const done = assert.async();
  this.get('redux').dispatch(DataActions.setNewReconView(RECON_VIEW_TYPES_BY_NAME.TEXT));

  this.render(hbs`{{recon-event-detail/text}}`);

  setTimeout(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'requestGET/images/hp/thirds__sml_ad_3.jpgHTTP/1.1Host:www.bladehq.comAccept:*/*Connection:keep-aliveCookie:__atuvc=8%7C49;__atuvs=56679ca029696108007;_isuid=50728CB8-C07A-4F02-9CEC-35F63080AA0C;cat_id');
    done();
  }, 400);
});

test('text view renders log text', function(assert) {
  assert.expect(1);

  const done = assert.async();

  // Set medium to 32, to force to log type
  this.get('redux').dispatch({
    type: ACTION_TYPES.SET_EVENT_TYPE,
    payload: determineEventType([['medium', 32]])
  });

  this.get('redux').dispatch(DataActions.setNewReconView(RECON_VIEW_TYPES_BY_NAME.TEXT));

  this.render(hbs`{{recon-event-detail/text}}`);

  setTimeout(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'GET/images/hp/thirds__sml_ad_3.jpgHTTP/1.1Host:www.bladehq.comAccept:*/*Connection:keep-aliveCookie:__atuvc=8%7C49;__atuvs=56679ca029696108007;_isuid=50728CB8-C07A-4F02-9CEC-35F63080AA0C;cat_id=1;curr');
    done();
  }, 400);
});

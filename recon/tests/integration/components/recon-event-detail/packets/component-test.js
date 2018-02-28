import wait from 'ember-test-helpers/wait';

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import { scheduleOnce } from '@ember/runloop';

import VisualActions from 'recon/actions/visual-creators';
import DataHelper from '../../../../helpers/data-helper';

moduleForComponent('recon-event-detail-packets', 'Integration | Component | recon event detail packets', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
  }
});

test('renders packets if data present', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToPacket()
    .initializeData()
    .renderPackets();
  this.render(hbs`{{recon-event-detail/packets}}`);
  return wait().then(() => {
    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, 'responsepacket1un11fin11ID4804965123248SEQ1878393573PAYLOAD0bytes000000000016000032000048000064a44c11ef6201f0f755ed59bf0800450000343c1140007e06846c8945834a36fbf8bbd7a900506ff602e50000000080022000c8140');
  });
});

test('single packet component is runloop safe', function(assert) {
  assert.expect(2);

  new DataHelper(this.get('redux'))
    .setViewToPacket()
    .initializeData()
    .renderPackets();
  this.render(hbs`{{recon-event-detail/packets}}`);

  return wait().then(() => {
    let $el = this.$('.rsa-packet');
    assert.ok($el.length > 1, 'Expected to find rsa-packet element in DOM.');

    // will destory the component so we can confirm the spaniel watcher callback is safe
    scheduleOnce('render', this, function() {
      this.clearRender();
    });

    return wait().then(() => {
      $el = this.$('.rsa-packet');
      assert.equal($el.length, 0, 'Should not blow up because spaniel watcher callback was prevented from running while destroyed');
    });
  });
});

test('renders spinner when data present but in the process of being rendered', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToPacket()
    .initializeData();
  this.render(hbs`{{recon-event-detail/packets}}`);
  return wait().then(() => {
    const loader = this.$('.recon-loader').length;
    assert.equal(loader, 1);
  });
});

test('renders error when no data present', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToPacket()
    .noPackets();
  this.render(hbs`{{recon-event-detail/packets}}`);
  return wait().then(() => {
    const str = this.$('.rsa-panel-message').text().trim().replace(/\s/g, '');
    assert.equal(str, 'NoHEXdatawasgeneratedduringcontentreconstruction.');
  });
});

test('renders nothing when data present, but hidden by request/response', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToPacket()
    .initializeData();

  this.get('redux').dispatch(VisualActions.toggleRequestData());
  this.get('redux').dispatch(VisualActions.toggleResponseData());

  this.render(hbs`{{recon-event-detail/packets}}`);
  return wait().then(() => {
    // remove the pager so its text doesn't confuse test
    this.$('.recon-pager').remove();

    const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
    assert.equal(str, '');
  });
});

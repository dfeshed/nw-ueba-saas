import wait from 'ember-test-helpers/wait';
import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { scheduleOnce } from '@ember/runloop';
import { find, findAll, render } from '@ember/test-helpers';

import VisualActions from 'recon/actions/visual-creators';
import DataHelper from '../../../../helpers/data-helper';


let redux;
module('Integration | Component | recon event detail packets', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    redux = this.owner.lookup('service:redux');
  });

  test('renders packets if data present', async function(assert) {

    new DataHelper(redux)
      .setViewToPacket()
      .initializeData()
      .renderPackets();
    await render(hbs`
      {{#recon-event-detail/packets}}
        {{recon-event-detail/single-packet shouldRenderBytes=true}}
      {{/recon-event-detail/packets}}
    `);

    return wait().then(() => {
      const str = find('.rsa-packet').textContent.trim().replace(/\s/g, '').substring(0, 200);
      assert.equal(str, 'responsepacket1un11fin11ID4804965123248SEQ1878393573PAYLOAD0bytes000000000016000032000048000064a44c11ef6201f0f755ed59bf0800450000343c1140007e06846c8945834a36fbf8bbd7a900506ff602e50000000080022000c8140');
    });
  });

  test('single packet component is runloop safe', async function(assert) {
    assert.expect(2);

    new DataHelper(redux)
      .setViewToPacket()
      .initializeData()
      .renderPackets();
    await render(hbs`{{recon-event-detail/packets}}`);

    return wait().then(() => {
      assert.ok(findAll('.rsa-packet').length > 1, 'Expected to find rsa-packet element in DOM.');

      // will destory the component so we can confirm the spaniel watcher callback is safe
      scheduleOnce('render', this, function() {
        this.clearRender();
      });

      return wait().then(() => {
        assert.equal(findAll('.rsa-packet').length, 0, 'Should not blow up because spaniel watcher callback was prevented from running while destroyed');
      });
    });
  });

  test('renders spinner when data present but in the process of being rendered', async function(assert) {

    new DataHelper(redux)
      .setViewToPacket()
      .initializeData();
    await render(hbs`{{recon-event-detail/packets}}`);
    return wait().then(() => {
      const loader = findAll('.recon-loader').length;
      assert.equal(loader, 1);
    });
  });

  test('renders error when no data present', async function(assert) {
    new DataHelper(redux)
      .setViewToPacket()
      .noPackets();
    await render(hbs`{{recon-event-detail/packets}}`);
    return wait().then(() => {
      const str = find('.rsa-panel-message').textContent.trim().replace(/\s/g, '');
      assert.equal(str, 'NoHEXdatawasgeneratedduringcontentreconstruction.');
    });
  });

  test('renders nothing when data present, but hidden by request/response', async function(assert) {
    new DataHelper(redux)
      .setViewToPacket()
      .initializeData();

    redux.dispatch(VisualActions.toggleRequestData());
    redux.dispatch(VisualActions.toggleResponseData());

    await render(hbs`{{recon-event-detail/packets}}`);
    return wait().then(() => {
      // remove the pager so its text doesn't confuse test
      find('.recon-pager').remove();

      const str = find('.recon-event-detail-packets').textContent.trim().replace(/\s/g, '').substring(0, 200);
      assert.equal(str, '');
    });
  });

  test('renders message when data present, but hidden by payload only', async function(assert) {
    new DataHelper(redux)
      .setViewToPacket()
      .initializeDataWithoutPayloads()
      .togglePayloadOnly()
      .renderPackets();

    await render(hbs`{{recon-event-detail/packets}}`);

    return wait().then(() => {
      const str = find('.message').textContent.trim().replace(/\s/g, '');
      assert.equal(str, 'TheeventhasnopayloadandtheDisplayPayloadsOnlyoptionisselected.ToviewthePacketAnalysisfortheevent,disabletheDisplayPayloadsOnlyoption.');
    });
  });

});
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import { TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

moduleForComponent('recon-event-titlebar', 'Integration | Component | recon event titlebar', {
  integration: true
});

test('no index or total shows just label for recon type', function(assert) {
  this.set('reconstructionType', TYPES_BY_NAME.PACKET);
  this.render(hbs`{{recon-event-titlebar reconstructionType=reconstructionType}}`);
  assert.equal(this.$('.prompt').text().trim(), TYPES_BY_NAME.PACKET.label);
});

test('title renders', function(assert) {
  this.set('reconstructionType', TYPES_BY_NAME.PACKET);
  this.set('total', 555);
  this.set('index', 25);
  this.render(hbs`
    {{recon-event-titlebar
      index=index
      reconstructionType=reconstructionType
      total=total}}
    `);
  assert.equal(this.$('.prompt').text().trim(), `${TYPES_BY_NAME.PACKET.label} (26 of 555)`);
});

test('clicking close executes action', function(assert) {
  assert.expect(1);
  const done = assert.async();
  this.set('closeRecon', function() {
    assert.ok(true);
    done();
  });
  this.set('reconstructionType', TYPES_BY_NAME.PACKET);
  this.render(hbs`
    {{recon-event-titlebar
      closeRecon=closeRecon
      reconstructionType=reconstructionType}}
    `);
  this.$().find('.rsa-icon-close').click();
});

test('clicking expand executes action', function(assert) {
  assert.expect(1);
  const done = assert.async();
  this.set('expandRecon', function() {
    assert.ok(true);
    done();
  });
  this.set('reconstructionType', TYPES_BY_NAME.PACKET);
  this.render(hbs`
    {{recon-event-titlebar
      expandRecon=expandRecon
      reconstructionType=reconstructionType}}
    `);
  this.$().find('.rsa-icon-arrow-left-9').click();
});

test('clicking shrink executes multiple actions', function(assert) {
  assert.expect(1);
  const done = assert.async();
  let doneTimes = 0;
  const doneCb = function() {
    if (++doneTimes == 2) {
      assert.ok(true);
      done();
    }
  };
  this.set('shrinkRecon', doneCb);
  this.set('toggleMetaDetails', doneCb);
  this.set('reconstructionType', TYPES_BY_NAME.PACKET);
  this.render(hbs`
    {{recon-event-titlebar
      isExpanded=true
      reconstructionType=reconstructionType
      shrinkRecon=shrinkRecon
      toggleMetaDetails=toggleMetaDetails}}
    `);
  this.$().find('.rsa-icon-arrow-right-9').click();
});

test('calls action when reconstruction view is changed', function(assert) {
  const done = assert.async();
  this.set('updateReconstructionView', (val) => {
    assert.equal(val.code, '2');
    done();
  });
  this.set('reconstructionType', TYPES_BY_NAME.PACKET);
  this.render(hbs`
    {{recon-event-titlebar
      reconstructionType=reconstructionType
      updateReconstructionView=updateReconstructionView}}
    `);
  this.$().find('.prompt').click();
  this.$().find('select').val('2').trigger('change');
});

test('clicking header toggle executes action', function(assert) {
  assert.expect(1);
  const done = assert.async();
  this.set('toggleHeaderData', function() {
    assert.ok(true);
    done();
  });
  this.set('reconstructionType', TYPES_BY_NAME.PACKET);
  this.render(hbs`
    {{recon-event-titlebar
      reconstructionType=reconstructionType
      toggleHeaderData=toggleHeaderData}}
    `);
  this.$().find('.rsa-icon-layout-6').click();
});

test('clicking meta toggle executes actions', function(assert) {
  assert.expect(1);
  const done = assert.async();
  let doneTimes = 0;
  const doneCb = function() {
    if (++doneTimes == 2) {
      assert.ok(true);
      done();
    }
  };
  this.set('toggleMetaDetails', doneCb);
  this.set('expandRecon', doneCb);
  this.set('reconstructionType', TYPES_BY_NAME.PACKET);
  this.render(hbs`
    {{recon-event-titlebar
      expandRecon=expandRecon
      reconstructionType=reconstructionType
      toggleMetaDetails=toggleMetaDetails}}
    `);
  this.$().find('.rsa-icon-layout-2').click();
});

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import { selectChoose, clickTrigger } from 'ember-power-select/test-support/helpers';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import $ from 'jquery';
import wait from 'ember-test-helpers/wait';

let setState;

const setupState = (packetCountValue) => {
  new ReduxDataHelper(setState)
    .isPacketView()
    .packetTotal(packetCountValue)
    .build();
};

moduleForComponent('data-pagination', 'Integration | Component | Packet Pagination', {
  integration: true,
  beforeEach() {
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('testing packet pagination basic controls', function(assert) {
  assert.expect(6);
  setupState(410);
  this.render(hbs`{{recon-pager/data-pagination}}`);

  assert.equal(this.$('.data-pagination').length, 1, 'Pagination controls expected');
  assert.equal(this.$('.last-page').text(), 5, 'Last Page Number as expected');
  assert.equal(this.$('.page-first-button').hasClass('is-disabled'), true, 'Page first should be disabled');
  assert.equal(this.$('.page-previous-button').hasClass('is-disabled'), true, 'Page previous should be disabled');
  assert.equal(this.$('.page-next-button').hasClass('is-disabled'), false, 'Page next should not be disabled');
  assert.equal(this.$('.page-last-button').hasClass('is-disabled'), false, 'Page last should not be disabled');
});

test('testing change in number of packets per page', function(assert) {
  assert.expect(2);
  setupState(410);
  this.render(hbs`{{recon-pager/data-pagination}}`);

  assert.equal(this.$('.ember-power-select-trigger').text().trim(), '100');
  clickTrigger('.power-select-dropdown');
  selectChoose('.power-select-dropdown', '300');
  assert.equal(this.$('.last-page').text(), 2, 'Last Page Number should be changed');
});

test('testing jump to specific page', function(assert) {
  assert.expect(9);
  setupState(210);
  this.render(hbs`{{recon-pager/data-pagination}}`);

  assert.equal(this.$('.page-first-button').hasClass('is-disabled'), true, 'Page first should be disabled');
  assert.equal(this.$('.page-previous-button').hasClass('is-disabled'), true, 'Page previous should be disabled');
  assert.equal(this.$('.page-next-button').hasClass('is-disabled'), false, 'Page next should not be disabled');
  assert.equal(this.$('.page-last-button').hasClass('is-disabled'), false, 'Page last should not be disabled');

  const e = new $.Event('keypress');
  e.keyCode = 13;
  this.$('.input-page-number').val(2).trigger(e);
  assert.equal(this.$('.input-page-number').val(), 2, ' Page Number as expected');
  assert.equal(this.$('.page-first-button').hasClass('is-disabled'), false, 'Page first now should not be disabled');
  assert.equal(this.$('.page-previous-button').hasClass('is-disabled'), false, 'Page previous now should not be disabled');
  assert.equal(this.$('.page-next-button').hasClass('is-disabled'), false, 'Page next should not be disabled');
  assert.equal(this.$('.page-last-button').hasClass('is-disabled'), false, 'Page last should not be disabled');

});

test('testing packet pagination navigation buttons', function(assert) {
  assert.expect(8);
  setupState(410);

  this.render(hbs`{{recon-pager/data-pagination}}`);

  assert.equal(this.$('.data-pagination').length, 1, 'Pagination controls expected');
  assert.equal(this.$('.input-page-number').val(), 1, 'Currently on 1st page');
  assert.equal(this.$('.last-page').text(), 5, 'Last Page Number as expected');

  this.$('.page-next-button .rsa-form-button').click();

  assert.equal(this.$('.input-page-number').val(), 2, 'Moves to 2nd page');
  assert.equal(this.$('.page-first-button').hasClass('is-disabled'), false, 'Page first should not be disabled');
  assert.equal(this.$('.page-previous-button').hasClass('is-disabled'), false, 'Page previous should not be disabled');
  assert.equal(this.$('.page-next-button').hasClass('is-disabled'), false, 'Page next should not be disabled');
  assert.equal(this.$('.page-last-button').hasClass('is-disabled'), false, 'Page last should not be disabled');
});

test('Recon should pick default Page Size set by the user', function(assert) {
  assert.expect(2);
  new ReduxDataHelper(setState)
    .isPacketView()
    .packetTotal(410)
    .packetPageSize(300)
    .build();
  this.render(hbs`{{recon-pager/data-pagination}}`);
  return wait().then(() => {
    assert.equal(this.$('.ember-power-select-trigger').text().trim(), '300');
    assert.equal(this.$('.last-page').text(), 2, 'Last Page Number should be changed');
  });
});
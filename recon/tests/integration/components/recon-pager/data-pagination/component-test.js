import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { selectChoose, clickTrigger } from '../../../../helpers/ember-power-select';
import $ from 'jquery';

const packets = {
  pageNumber: 1,
  packetsPageSize: 100
};

const data = {
  eventId: 5
};

const header = {
  headerItems: [{}], headerLoading: false
};

const visuals = {
  currentReconView: {
    code: 1,
    id: 'packet',
    name: 'PACKET',
    component: 'recon-event-detail/packets',
    dataKey: 'packets.packets'
  }
};

let setState;

moduleForComponent('data-pagination', 'Integration | Component | Packet Pagination', {
  integration: true,
  beforeEach() {
    initialize(this);
    setState = (state) => {
      const fullState = { recon: { ...state } };
      applyPatch(Immutable.from(fullState));
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('testing packet pagination basic controls', function(assert) {
  assert.expect(6);
  setState({
    packets: {
      ...packets,
      packetsPageSize: 100
    },
    header: {
      ...header,
      headerItems: [{ name: 'packetCount', key: '', type: 'UInt64', value: 410, id: 'packetCount' }]
    },
    data
  });

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
  setState({
    packets: {
      ...packets,
      packetsPageSize: 100
    },
    header: {
      ...header,
      headerItems: [{ name: 'packetCount', key: '', type: 'UInt64', value: 410, id: 'packetCount' }]
    },
    data,
    visuals
  });

  this.render(hbs`{{recon-pager/data-pagination}}`);

  assert.equal(this.$('.ember-power-select-trigger').text().trim(), '100');
  clickTrigger('.power-select-dropdown');
  selectChoose('.power-select-dropdown', '300');
  assert.equal(this.$('.last-page').text(), 2, 'Last Page Number should be changed');

});

test('testing jump to specific page', function(assert) {

  assert.expect(9);
  setState({
    packets: {
      ...packets,
      packetsPageSize: 100
    },
    header: {
      ...header,
      headerItems: [{ name: 'packetCount', key: '', type: 'UInt64', value: 210, id: 'packetCount' }]
    },
    data,
    visuals
  });


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
  setState({
    packets: {
      ...packets,
      packetsPageSize: 100
    },
    header: {
      ...header,
      headerItems: [{ name: 'packetCount', key: '', type: 'UInt64', value: 410, id: 'packetCount' }]
    },
    data,
    visuals
  });

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
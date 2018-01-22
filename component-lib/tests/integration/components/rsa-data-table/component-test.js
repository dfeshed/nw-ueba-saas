import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import $ from 'jquery';
import wait from 'ember-test-helpers/wait';

const items = [ {
  service: 'notes',
  orig_ip: '8.202.108.50'
}];

const renderDifferentColumns = function(_this, width = null) {

  const columnsConfig = [
    {
      field: 'service',
      title: 'Service Type',
      width
    }, {
      field: 'orig_ip',
      title: 'Originating IP Address',
      width
    }, {
      field: 'ip.src',
      title: 'Source IP Address'
    }, {
      field: 'ip.dst',
      title: 'Destination IP Address'
    }, {
      field: 'tcp.dstport',
      title: 'TCP Destination Port'
    }
  ];
  _this.set('items', items);
  _this.set('columnsConfig', columnsConfig);
  _this.render(hbs`
    {{#rsa-data-table items=items columnsConfig=columnsConfig}}
      {{#rsa-data-table/header as |column|}}
        {{column.title}}
      {{/rsa-data-table/header}}
      {{#rsa-data-table/body  showNoResultMessage=false as |item index column|}}
        {{#rsa-data-table/body-cell column=column}}
          aa
        {{/rsa-data-table/body-cell}}
      {{/rsa-data-table/body}}
    {{/rsa-data-table}}
  `);
};

moduleForComponent('rsa-data-table', 'Integration | Component | rsa data table', {
  integration: true
});

test('Since no of columns are less and no width is given so adjusting the cell width according to the viewport should be more than 100', function(assert) {
  renderDifferentColumns(this);
  // To compare the width value, retrieving it without the units
  const match = $('.rsa-data-table-body-cell').attr('style').match(/([\d\.]+)([^\d]*)/);
  const columnWidth = match && Number(match[1]);
  assert.ok(columnWidth > 100, true);
});

test('since width is in px, hence still computes it', function(assert) {
  renderDifferentColumns(this, '120px');
  const match = $('.rsa-data-table-body-cell').attr('style').match(/([\d\.]+)([^\d]*)/);
  const columnWidth = match && Number(match[1]);
  assert.ok(columnWidth > 100, true);
});

test('since width is in some other units than px, so will not compute it', function(assert) {
  renderDifferentColumns(this, '90vw');
  assert.equal($('.rsa-data-table-body-cell').attr('style'), 'width: 90vw;');
});

test('since combined cell width is more than viewport, so will not adjust the width', function(assert) {
  renderDifferentColumns(this, '600px');
  assert.equal($('.rsa-data-table-body-cell').attr('style'), 'width: 600px;');
});

test('when the width of ViewPort of data-table changes, it needs to recalculate the width for columns', function(assert) {
  renderDifferentColumns(this);
  $('.rsa-data-table').width(1000);
  const match = $('.rsa-data-table-body-cell').attr('style').match(/([\d\.]+)([^\d]*)/);
  const columnWidth = match && Number(match[1]);
  $('.rsa-data-table').width(3000);
  const done = assert.async();
  return wait().then(() => {
    const newMatch = $('.rsa-data-table-body-cell').attr('style').match(/([\d\.]+)([^\d]*)/);
    const newColumnWidth = newMatch && Number(newMatch[1]);
    assert.ok(columnWidth != newColumnWidth, true);
    done();
  });
});
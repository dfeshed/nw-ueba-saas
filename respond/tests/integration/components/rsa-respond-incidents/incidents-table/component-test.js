import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import columns from 'respond/components/rsa-respond-incidents/incidents-table/columns';
import testIncidents from '../../../../server/subscriptions/incidents/query/index';

const dataTableSelector = '.rsa-respond-incidents-table .rsa-data-table';

// The columns defined as being visible to the user
const visibleColumns = columns.filter((column) => {
  return column.visible;
});

moduleForComponent('rsa-respond-incidents/incidents-table', 'Integration | Component | Respond Incidents Table', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('The Incidents table renders to the DOM', function(assert) {
  assert.expect(1);
  this.render(hbs`{{rsa-respond-incidents/incidents-table}}`);
  assert.equal(this.$(dataTableSelector).length, 1, 'The Incidents table should be found in the DOM');
});


test('The Incidents table shows appropriate message when there are no incidents', function(assert) {
  assert.expect(1);
  const selector = `${dataTableSelector} .rsa-panel-message`;
  this.render(hbs`{{rsa-respond-incidents/incidents-table}}`);

  assert.equal(this.$(selector).text().trim(), 'No matching Incidents were found', 'The Incidents table should show a custom no-results message');
});

test('The Incidents has the expected number of columns represented as cells in the header row', function(assert) {
  assert.expect(2);
  const headerCellSelector = `${dataTableSelector} .rsa-data-table-header .rsa-data-table-header-cell`;
  const bodyRowSelector = `${dataTableSelector} .rsa-data-table-body-row`;
  this.render(hbs`{{rsa-respond-incidents/incidents-table}}`);

  assert.equal(this.$(headerCellSelector)
      .length, visibleColumns.length, 'The Incidents should have the proper number of columns (defined in incident-tables/columns.js)');
  assert.equal(this.$(bodyRowSelector).length, 0, 'There are no rows in the table body');
});

// Skipping for now since the link-to in the incidents table breaks the test in the ember engine until engines v0.5
test('When rendered with an incidents list, the appropriate number of rows are represented in the DOM', function(assert) {
  assert.expect(1);
  const incidents = testIncidents.message().data;
  const selector = `${dataTableSelector} .rsa-data-table-body-row`;

  this.set('incidents', incidents);
  this.render(hbs`
    {{rsa-respond-incidents/incidents-table 
      useLazyRendering=false
      incidents=incidents
    }}`);

  assert.equal(this.$(selector).length, incidents.length, 'All data objects appears as rows in the table');
});

// Skipping for now since the link-to in the incidents table breaks the test in the ember engine until engines v0.5
test('When rendered with an incidents list, the risk score inline badge appears in every row', function(assert) {
  assert.expect(1);
  const incidents = testIncidents.message().data;
  const selector = `${dataTableSelector} .rsa-data-table-body-row .rsa-content-label`;

  this.set('incidents', incidents);
  this.render(hbs`
    {{rsa-respond-incidents/incidents-table 
      useLazyRendering=false
      incidents=incidents
    }}`);

  assert.equal(this.$(selector).length, incidents.length, 'Every row has an inline risk score badge');
});
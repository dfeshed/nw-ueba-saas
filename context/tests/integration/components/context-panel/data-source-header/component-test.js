import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import dataSourceDetails from 'context/config/machines';

moduleForComponent('context-panel/data-source-header', 'Integration | Component | context panel/data-source-header', {
  integration: true
});

test('it renders', function(assert) {
  const contextData = {};


  this.set('contextData', contextData);
  this.set('dataSourceDetails', dataSourceDetails);
  this.render(hbs`{{context-panel/data-source-header contextData=contextData dataSourceDetails=dataSourceDetails}}`);

  assert.equal(this.$('.rsa-loader').length, 1);
});

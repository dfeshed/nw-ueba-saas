import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';

import * as RequestApi from 'investigate-events/actions/query-validation-creators';
import { pressEnter, testSetupConfig } from './util';

moduleForComponent(
  'query-filters/query-filter-fragment',
  'Integration | Component | query-filter-fragment validation-server-side',
  testSetupConfig
);

test('it renders with proper class when a single query is invalid', function(assert) {
  this.render(hbs`{{query-filters/query-filter-fragment queryFragmentInvalid=true}}`);
  const $el = this.$('.rsa-query-fragment.query-fragment-invalid');
  assert.equal($el.length, 1, 'Expected invalid class name binding.');
});

sinon.stub(RequestApi, 'validateIndividualQuery');

test('submitting a query fragment and checking if the api wrapper function is called', function(assert) {

  RequestApi.validateIndividualQuery.reset();

  this.set('list', []);
  this.set('metaOptions', [{ displayName: 'Medium', flags: -2147483309, format: 'UInt8', metaName: 'medium' }]);

  this.render(hbs`{{query-filters/query-filter-fragment validateWithServer=true filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('medium = 32');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
  assert.ok(RequestApi.validateIndividualQuery.calledOnce, 'Validate query action creator called once');
  assert.equal(RequestApi.validateIndividualQuery.args[0][0], 'medium = 32', 'correct params are being passed');
  RequestApi.validateIndividualQuery.restore();
});

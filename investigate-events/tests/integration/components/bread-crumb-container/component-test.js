import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

moduleForComponent('bread-crumb-container', 'Integration | Component | bread-crumb-container', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    initialize(this);
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it disables the submit button when required values are missing', function(assert) {
  new ReduxDataHelper(setState)
    .hasRequiredValuesToQuery(false)
    .build();
  this.render(hbs`{{bread-crumb-container}}`);
  assert.ok(this.$('.execute-query-button').hasClass('is-disabled'), 'Expected is-disabled CSS class on the submit button.');
});

test('it enables the submit button when required values are present', function(assert) {
  new ReduxDataHelper(setState)
    .hasRequiredValuesToQuery(true)
    .build();
  this.render(hbs`{{bread-crumb-container}}`);
  assert.notOk(this.$('.execute-query-button').hasClass('is-disabled'), 'Expected is-disabled CSS class on the submit button.');
});
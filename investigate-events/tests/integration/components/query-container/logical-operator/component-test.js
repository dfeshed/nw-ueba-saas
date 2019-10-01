import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { find, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import PILL_SELECTORS from '../pill-selectors';
import { OPERATOR_AND, OPERATOR_OR } from 'investigate-events/constants/pill';
import { createOperator } from 'investigate-events/util/query-parsing';

module('Integration | Component | Logical Operator', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it renders as an AND by default', async function(assert) {
    await render(hbs`
      {{query-container/logical-operator}}
    `);
    assert.ok(find(PILL_SELECTORS.logicalOperatorAND), 'renders as AND by default');
  });

  test('it renders as an AND', async function(assert) {
    this.set('pillData', createOperator(OPERATOR_AND));
    await render(hbs`
      {{query-container/logical-operator
        pillData=pillData
      }}
    `);
    assert.ok(find(PILL_SELECTORS.logicalOperatorAND), 'renders as AND');
  });

  test('it renders as an OR', async function(assert) {
    this.set('pillData', createOperator(OPERATOR_OR));
    await render(hbs`
      {{query-container/logical-operator
        pillData=pillData
      }}
    `);
    assert.ok(find(PILL_SELECTORS.logicalOperatorOR), 'renders as OR');
  });
});
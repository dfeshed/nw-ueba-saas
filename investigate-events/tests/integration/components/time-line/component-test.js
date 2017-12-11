import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

let setState;
moduleForComponent('time-line', 'Integration | Component | time-line', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    initialize(this);
    setState = (state) => {
      const fullState = { investigate: { ...state } };
      applyPatch(Immutable.from(fullState));
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it renders', function(assert) {
  this.render(hbs`{{query-container/time-line}}`);

  assert.equal(this.$('.rsa-investigate-timeline').length, 1, 'Expected to find root DOM node');
  assert.equal(this.$('.rsa-chart').length, 1, 'Expected to find child chart\'s root DOM node');
});

test('it renders a wait indicator when status is \'wait\'', function(assert) {
  setState({
    eventTimeline: {
      status: 'wait'
    }
  });

  this.render(hbs`{{query-container/time-line}}`);

  assert.equal(this.$('.rsa-investigate-timeline .js-test-wait').length, 1, 'Expected to find wait DOM node');
});

test('it renders an error indicator when status is \'rejected\'', function(assert) {
  setState({
    eventTimeline: {
      status: 'rejected'
    }
  });

  assert.expect(2);

  this.set('retryAction', function() {
    assert.ok(true, 'retryAction was invoked when Retry DOM was clicked');
  });

  this.render(hbs`{{query-container/time-line retryAction=retryAction}}`);

  assert.equal(this.$('.rsa-investigate-timeline .js-test-rejected').length, 1, 'Expected to find error DOM node');

  const retryButton = this.$('.rsa-investigate-timeline .js-test-retry');
  assert.equal(retryButton.length, 1, 'Expected to find Retry DOM node');

});

test('it renders an empty message only when the status is \'resolved\' and no data is given', function(assert) {
  setState({
    eventTimeline: {
      status: 'rejected',
      data: {
        value: 1,
        count: 1
      }
    }
  });

  this.render(hbs`{{query-container/time-line}}`);

  assert.equal(this.$('.rsa-investigate-timeline .js-test-empty').length, 0, 'Expected empty message to be missing');

  this.set('status', 'rejected');
  assert.equal(this.$('.rsa-investigate-timeline .js-test-empty').length, 0, 'Expected empty message to be missing');

  this.set('status', 'resolved');
  assert.equal(this.$('.rsa-investigate-timeline .js-test-empty').length, 0, 'Expected empty message to be missing');

});

test('it renders an empty message only when the status is \'resolved\' and no data is given', function(assert) {
  setState({
    eventTimeline: {
      status: 'resolved',
      data: undefined
    }
  });

  this.render(hbs`{{query-container/time-line}}`);
  assert.equal(this.$('.rsa-investigate-timeline .js-test-empty').length, 1, 'Expected empty message in DOM');
});

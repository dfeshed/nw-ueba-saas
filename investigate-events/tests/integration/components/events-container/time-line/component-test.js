import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { find, render } from '@ember/test-helpers';

let setState;
module('Integration | Component | Time Line', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      const fullState = { investigate: { ...state } };
      patchReducer(this, Immutable.from(fullState));
    };
    initialize(this.owner);
  });

  test('it renders', async function(assert) {
    await render(hbs`{{events-container/time-line}}`);
    assert.ok(find('.rsa-investigate-timeline'), 'Expected to find root DOM node');
    assert.ok(find('.rsa-chart'), 'Expected to find child chart\'s root DOM node');
  });

  test('it renders a wait indicator when status is \'wait\'', async function(assert) {
    setState({
      eventTimeline: {
        status: 'wait'
      }
    });
    await render(hbs`{{events-container/time-line}}`);
    assert.ok(find('.rsa-investigate-timeline .js-test-wait'), 'Expected to find wait DOM node');
  });

  test('it renders an error indicator when status is \'rejected\'', async function(assert) {
    setState({
      eventTimeline: {
        status: 'rejected'
      }
    });
    await render(hbs`{{events-container/time-line}}`);
    assert.ok(find('.rsa-investigate-timeline .js-test-rejected'), 'Expected to find error DOM node');
    assert.ok(find('.rsa-investigate-timeline .js-test-retry'), 'Expected to find Retry DOM node');
  });

  test('it renders an empty message only when the status is \'resolved\' and no data is given', async function(assert) {
    setState({
      eventTimeline: {
        status: 'rejected',
        data: {
          value: 1,
          count: 1
        }
      }
    });
    await render(hbs`{{events-container/time-line}}`);
    assert.notOk(find('.rsa-investigate-timeline .js-test-empty'), 'Expected empty message to be missing');
    this.set('status', 'rejected');
    assert.notOk(find('.rsa-investigate-timeline .js-test-empty'), 'Expected empty message to be missing');
    this.set('status', 'resolved');
    assert.notOk(find('.rsa-investigate-timeline .js-test-empty'), 'Expected empty message to be missing');

  });

  test('it renders an empty message only when the status is \'resolved\' and no data is given', async function(assert) {
    setState({
      eventTimeline: {
        status: 'resolved',
        data: undefined
      }
    });
    await render(hbs`{{events-container/time-line}}`);
    assert.ok(find('.rsa-investigate-timeline .js-test-empty'), 'Expected empty message in DOM');
  });
});
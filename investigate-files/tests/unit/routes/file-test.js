import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import FileRoute from 'investigate-files/routes/file';
import { computed } from '@ember/object';
import { patchReducer } from '../../helpers/vnext-patch';
import sinon from 'sinon';
import Immutable from 'seamless-immutable';
import FileCreators from 'investigate-files/actions/data-creators';

let transition, redux;


module('Unit | Route | file', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    transition = null;
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'investigate-files'
    }));
    redux = this.owner.lookup('service:redux');
    const PatchedRoute = FileRoute.extend({
      redux: computed(function() {
        return redux;
      }),
      transitionTo(routeName) {
        transition = routeName;
      },
      replaceWith(routeName) {
        transition = routeName;
      }
    });
    return PatchedRoute.create();
  };

  test('model hook should call initializerForFileDetailsAndAnalysis', async function(assert) {
    assert.expect(1);

    const mock = sinon.stub(FileCreators, 'initializerForFileDetailsAndAnalysis');

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model({ sid: '123' });

    await settled();

    assert.ok(mock.callCount === 1, 'initializerForFileDetailsAndAnalysis method is called');
    mock.restore();
  });

  test('model hook should not call initializerForFileDetailsAndAnalysis when sid is not present', async function(assert) {
    assert.expect(1);

    const mock = sinon.stub(FileCreators, 'initializerForFileDetailsAndAnalysis');

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model({ });

    await settled();

    assert.ok(mock.callCount === 0, 'initializerForFileDetailsAndAnalysis method is not called');
    mock.restore();
  });

  test('switchToSelectedFileDetailsTab action executed correctly', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await settled();

    await route.send('switchToSelectedFileDetailsTab', 'abc', 'text');

    assert.ok(transition, 'abc');
  });
});

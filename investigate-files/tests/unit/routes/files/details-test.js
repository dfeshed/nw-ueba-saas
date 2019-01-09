import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import DetailsRoute from 'investigate-files/routes/files/details';
import { computed } from '@ember/object';
import FileCreators from 'investigate-files/actions/data-creators';
import sinon from 'sinon';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import VisualCreators from 'investigate-files/actions/visual-creators';

let transition, redux;


module('Unit | Route | files.index', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'investigate-files'
    }));

    redux = this.owner.lookup('service:redux');

    const PatchedRoute = DetailsRoute.extend({
      redux: computed(function() {
        return redux;
      }),
      transitionTo(routeName) {
        transition = routeName;
      }
    });
    return PatchedRoute.create();
  };

  test('model hook should call initializerForFileDetailsAndAnalysis if param has sid', async function(assert) {
    assert.expect(1);

    const mock = sinon.stub(FileCreators, 'initializerForFileDetailsAndAnalysis');

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model({ sid: '123' });

    await settled();

    assert.ok(mock.callCount === 1, 'bootstrapInvestigateFiles method is called');
  });

  test('switchToSelectedFileDetailsTab action executed correctly', async function(assert) {
    assert.expect(2);

    const mock = sinon.stub(VisualCreators, 'setNewFileTab');

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await settled();
    await route.send('switchToSelectedFileDetailsTab', 'details', 'text');
    assert.ok(mock.callCount === 1, 'bootstrapInvestigateFiles method is called');
    assert.ok(transition, 'details');
  });

});

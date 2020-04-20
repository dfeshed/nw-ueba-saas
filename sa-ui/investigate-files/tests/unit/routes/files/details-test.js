import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import DetailsRoute from 'investigate-files/routes/files/details';
import { computed } from '@ember/object';
import { patchReducer } from '../../../helpers/vnext-patch';
import sinon from 'sinon';
import Immutable from 'seamless-immutable';
import FileCreators from 'investigate-files/actions/data-creators';

let transition, redux;


module('Unit | Route | files.details', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    transition = null;
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'investigate-files'
    }));
    const contextualHelp = this.owner.lookup('service:contextualHelp');
    redux = this.owner.lookup('service:redux');

    const PatchedRoute = DetailsRoute.extend({
      redux: computed(function() {
        return redux;
      }),
      contextualHelp: computed(function() {
        return contextualHelp;
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
    mock.restore();
  });

  test('model hook does not call initializerForFileDetailsAndAnalysis if sid is not present', async function(assert) {
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
    await route.send('switchToSelectedFileDetailsTab', 'details', 'text');
    assert.ok(transition, 'details');
  });

  test('the contextual-help "topic" is set to invFiles on deactivation of the route', async function(assert) {
    assert.expect(1);
    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);
    route.activate();
    route.deactivate();
    assert.equal(route.get('contextualHelp.topic'), route.get('contextualHelp.invFiles'), 'The contextual-help topic is set to invFiles');
  });

  test('switchToSelectedFileDetailsTab action executed correctly for ANALYSIS tab', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await settled();
    await route.send('switchToSelectedFileDetailsTab', 'ANALYSIS', 'text');
    assert.ok(transition, 'ANALYSIS');
  });

});

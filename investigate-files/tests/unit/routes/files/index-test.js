import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import IndexRoute from 'investigate-files/routes/files/index';
import { computed } from '@ember/object';
import FileCreators from 'investigate-files/actions/data-creators';
import sinon from 'sinon';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let redux;


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

    const PatchedRoute = IndexRoute.extend({
      redux: computed(function() {
        return redux;
      })
    });
    return PatchedRoute.create();
  };

  test('model hook should call bootstrapInvestigateFiles', async function(assert) {
    assert.expect(1);

    const mock = sinon.stub(FileCreators, 'bootstrapInvestigateFiles');

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model({});

    await settled();

    assert.ok(mock.callCount === 1, 'bootstrapInvestigateFiles method is called');
  });


});

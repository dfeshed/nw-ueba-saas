import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import FileanAlysisRoute from 'investigate-hosts/routes/hosts/details/tab/fileanalysis';
import { computed } from '@ember/object';
import FileAnalysis from 'investigate-shared/actions/data-creators/file-analysis-creators';
import sinon from 'sinon';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let redux;


module('Unit | Route | Hosts | Details | Tab | fileanalysis', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'hosts.details.tab'
    }));
    redux = this.owner.lookup('service:redux');
    const PatchedRoute = FileanAlysisRoute.extend({
      redux: computed(function() {
        return redux;
      }),

      modelFor() {
        return { sid: 1 };
      }
    });
    return PatchedRoute.create();
  };

  test('model hook should call getFileAnalysisData', async function(assert) {
    assert.expect(1);

    const mock = sinon.stub(FileAnalysis, 'getFileAnalysisData');

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model({ fileHash: 'fileHash', fileFormat: 'string', fileSid: 'testSid' });

    await settled();

    assert.ok(mock.callCount === 1, 'getFileAnalysisData method is called');
  });


});

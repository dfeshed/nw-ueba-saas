import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';
import sinon from 'sinon';
import VisualCreators from 'investigate-files/actions/visual-creators';

let setNewFileTabSpy;
const spys = [];

const selectors = {
  fileActions: '.file-details-tabs  .rsa-nav-tab'
};


spys.push(
  setNewFileTabSpy = sinon.stub(VisualCreators, 'setNewFileTab')
);

module('Integration | Component | file-details/header/file-details-tabs', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.afterEach(function() {
    spys.forEach((s) => {
      s.restore();
    });
  });

  test('file-details-tabs renders', async function(assert) {
    assert.expect(4);
    const state = {
      files: {
        fileList: {
          selectedDetailFile: {
            firstFileName: 'dtf.exe'
          },
          hostNameList: [{
            value: 'Machine1'
          },
          {
            value: 'Machine2'
          },
          {
            value: 'Machine3'
          },
          {
            value: 'Machine4'
          }]
        }

      }
    };
    this.set('switchToSelectedFileDetailsTab', (tabName, format) => {
      assert.equal(tabName, 'ANALYSIS', 'Second tab analysis is clicked');
      assert.equal(format, 'text', 'format is properly set');
    });
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{file-details/header/file-details-tabs switchToSelectedFileDetailsTab=switchToSelectedFileDetailsTab}}`);
    assert.equal(findAll(selectors.fileActions).length, 2, '2 tabs are present');
    await click(findAll(selectors.fileActions)[1]);
    assert.equal(setNewFileTabSpy.callCount, 1, 'setNewFileTab is called once');
  });
});

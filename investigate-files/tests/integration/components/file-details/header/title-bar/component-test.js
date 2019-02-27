import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';
import sinon from 'sinon';
import VisualCreators from 'investigate-files/actions/visual-creators';

let setNewFileTabSpy;
const spys = [];

const selectors = {
  filename: '.title-bar__file-name',
  fileTabs: '.title-bar .rsa-nav-tab',
  closeButton: '.title-bar__close',
  closeLink: '.title-bar__close a',
  filePropertyPanel: '.title-bar__fileProperties',
  fileDetailTitlebar: '.title-bar .rsa-nav-tab'
};


spys.push(
  setNewFileTabSpy = sinon.stub(VisualCreators, 'setNewFileTab')
);

module('Integration | Component | file-details/header/title-bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.afterEach(function() {
    spys.forEach((s) => {
      s.restore();
    });
  });

  test('it renders', async function(assert) {
    assert.expect(7);
    const state = {
      files: {
        fileList: {
          selectedDetailFile: {
            firstFileName: 'dtf.exe'
          }
        }
      }
    };
    this.set('switchToSelectedFileDetailsTab', (tabName, format) => {
      assert.equal(tabName, 'ANALYSIS', 'Second tab analysis is clicked');
      assert.equal(format, 'text', 'format is properly set');
    });
    patchReducer(this, Immutable.from(state));

    await render(hbs`{{file-details/header/title-bar switchToSelectedFileDetailsTab=switchToSelectedFileDetailsTab}}`);

    assert.equal(find(selectors.filename).textContent, 'dtf.exe', 'Filename is rendered');
    assert.equal(findAll(selectors.fileTabs).length, 2, 'Two tabs are rendered');
    assert.equal(findAll(selectors.closeButton).length, 1, 'Close button is present');
    assert.equal(findAll(selectors.filePropertyPanel).length, 1, 'Show/Hide file Property panel button is present');
    await click(findAll(selectors.fileDetailTitlebar)[1]);
    assert.equal(setNewFileTabSpy.callCount, 1, 'setNewFileTab is called once');
  });
});

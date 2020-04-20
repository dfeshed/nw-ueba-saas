import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';
const spys = [];

const selectors = {
  filename: '.title-bar__file-name',
  fileTabs: '.title-bar .rsa-nav-tab',
  closeButton: '.title-bar__close',
  closeLink: '.title-bar__close a',
  filePropertyPanel: '.title-bar__fileProperties',
  fileDetailTitlebar: '.title-bar .rsa-nav-tab',
  rsaRiskScore: '.title-bar svg text',
  osType: '.file-title-os-wrapper .osType',
  fileActions: '.file-header-actions .file-actionbar'
};

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
    assert.expect(6);
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
    patchReducer(this, Immutable.from(state));

    await render(hbs`{{file-details/header/title-bar}}`);
    assert.equal(find(selectors.rsaRiskScore).textContent, '0', 'Risk score is rendered.');
    assert.equal(find(selectors.filename).textContent, 'dtf.exe', 'Filename is rendered');
    assert.equal(findAll(selectors.osType).length, 1, 'OS Type is present');
    assert.equal(findAll(selectors.fileActions).length, 1, 'File Action bar is present');
    assert.equal(findAll(selectors.closeButton).length, 1, 'Close button is present');
    assert.equal(findAll(selectors.filePropertyPanel).length, 1, 'Show/Hide file Property panel button is present');
  });
});

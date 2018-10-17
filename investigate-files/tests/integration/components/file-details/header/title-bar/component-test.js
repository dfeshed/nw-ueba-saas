import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';

const selectors = {
  filename: '.title-bar__file-name',
  fileTabs: '.title-bar .rsa-nav-tab'
};

module('Integration | Component | file-details/header/title-bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  test('it renders', async function(assert) {
    const state = {
      files: {
        fileList: {
          selectedDetailFile: {
            firstFileName: 'dtf.exe'
          }
        }
      }
    };
    patchReducer(this, Immutable.from(state));

    await render(hbs`{{file-details/header/title-bar}}`);

    assert.equal(find(selectors.filename).textContent, 'dtf.exe', 'Filename is rendered');
    assert.equal(findAll(selectors.fileTabs).length, 2, 'Two tabs are rendered');
  });
});

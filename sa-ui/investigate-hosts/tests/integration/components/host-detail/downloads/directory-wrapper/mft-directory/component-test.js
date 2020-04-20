import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { hostDownloads } from '../../../../../components/state/downloads';

module('Integration | Component | mft-directory', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  test('mft-directory has loaded', async function(assert) {
    this.set('item', hostDownloads.mft.mftDirectory.subDirectories[0]);
    await render(hbs`{{host-detail/downloads/directory-wrapper/mft-directory data=item value=item.name}}`);
    assert.equal(findAll('.mft-directory').length, 12, 'mft-directory has loaded');
    assert.equal(findAll('.mft-directory_arrow').length, 11, 'sub directories present');
    assert.equal(findAll('.no-sub-folders').length, 1, 'Folder without subdirectories present');
    assert.equal(findAll('.mft-folder-icon.main-drive').length, 1, 'Main Drive icon is present');
  });
});

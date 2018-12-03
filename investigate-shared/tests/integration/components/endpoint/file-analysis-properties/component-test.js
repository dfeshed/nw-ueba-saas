import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/file-analysis-properties', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('file-analysis-properties component renders file format specific', async function(assert) {
    const filePropertiesData = {
      format: 'elf',
      fileName: '1394ohci.sys',
      size: 229888,
      checksumMd5: 'a87d604aea360176311474c87a63bb88',
      checksumSha1: '38dbe54a022b6c73edbdb8bf5cba32a882d2df2a',
      checksumSha256: 'b1507868c382cd5d2dbc0d62114fcfbf7a780904a2e3ca7c7c1dd0844ada9a8f',
      entropy: 6.347836129820286,
      downloadedPath: '/endpoint/downloads/files/b1507',
      downloadedFileName: 'b1507868c382cd5d2dbc0d62114fcfbf7a780904a2e3ca7c7c1dd0844ada9a8f',
      elf: {
        architecture: 'CPU_TYPE_x86_64',
        entryPoint: '0X000012CE',
        entryPointValid: true,
        sectionNames: [
          '.interp01',
          '.interp02'
        ],
        fileType: 'Executable',
        neededLibraries: [
          'library01',
          'library02'
        ],
        packerSectionFound: false,
        uncommonSectionFound: false
      }
    };
    this.set('filePropertiesData', filePropertiesData);
    await render(hbs`{{endpoint/file-analysis-properties filePropertiesData=filePropertiesData}}`);
    assert.equal(findAll('.file-analysis-properties').length, 1, 'file-analysis-properties component has rendered.');
    assert.equal(find('.content-section__property:nth-child(2) .tooltip-text').textContent.trim(), 'elf', 'elf config has been rendered.');
  });

  test('file-analysis-properties component renders default config', async function(assert) {
    const filePropertiesData = {
      format: 'script',
      fileName: 'adp94xx.sys',
      size: 491088,
      checksumMd5: '2f6b34b83843f0c5118b63ac634f5bf4',
      checksumSha1: '2f1e5cc89b811aab5983bfe20235d44450fe8361',
      checksumSha256: '43e3f5fbfb5d33981ac503dee476868ec029815d459e7c36c4abc2d2f75b5735',
      entropy: 6.548615161814601,
      downloadedPath: '/endpoint/downloads/files/43e3f',
      downloadedFileName: '43e3f5fbfb5d33981ac503dee476868ec029815d459e7c36c4abc2d2f75b5735'
    };
    this.set('filePropertiesData', filePropertiesData);
    await render(hbs`{{endpoint/file-analysis-properties filePropertiesData=filePropertiesData}}`);
    assert.equal(findAll('.file-analysis-properties').length, 1, 'file-analysis-properties component has rendered.');
    assert.equal(find('.content-section__property:nth-child(2) .tooltip-text').textContent.trim(), 'script', 'default config has been rendered.');
  });

});

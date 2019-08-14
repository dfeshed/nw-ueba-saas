import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, fillIn, triggerEvent } from '@ember/test-helpers';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { encodingOptions } from 'admin-source-management/components/usm-policies/policy-wizard/define-policy-sources-step/body-cell/settings';

let setState;

const sources = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: 'apache-server-1' } ];
const column = {
  field: 'fileEncoding',
  title: 'adminUsm.policyWizard.filePolicy.fileEncoding',
  width: '15vw',
  displayType: 'fileEncoding'
};
const item = {
  fileType: 'apache',
  fileEncoding: 'Local Encoding',
  enabled: true,
  startOfEvents: false,
  sourceName: 'apache-server-1',
  exclusionFilters: ['filter-1', 'filter-2'],
  paths: ['path1', 'path2']
};

module('Integration | Component | usm-policies/policy-wizard/define-policy-sources-step/body-cell', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('it renders define-policy-sources-step/body-cell component', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSources(sources)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/define-policy-sources-step/body-cell}}`);
    assert.equal(findAll('.child-source-container .body-cell').length, 1, 'Expected to define-policy-sources-step/body-cell element in DOM.');
  });

  test('there should be 16 dropdown options available for file encoding', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    this.setProperties({
      column,
      encodingOptions,
      item
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        encodingOptions=encodingOptions
        item=item
      }}
    `);
    await clickTrigger('.file-encoding');
    assert.equal(findAll('.ember-power-select-option').length, 16, 'Dropdown is rendered with correct number of items');
  });

  test('select Local Encoding from the file encoding dropdown', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    this.setProperties({
      column,
      encodingOptions,
      item
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        encodingOptions=encodingOptions
        item=item
      }}
    `);
    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), 'Local Encoding');
  });

  test('changing the file encoding triggers sourceUpdated method', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    this.setProperties({
      column,
      encodingOptions,
      item
    });

    this.set('sourceUpdated', () => {
      assert.ok(true, 'sourceUpdated should be called');
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        encodingOptions=encodingOptions
        item=item
        sourceUpdated=sourceUpdated
      }}
    `);

    await selectChoose('.file-encoding', 'Local Encoding');
    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), 'Local Encoding', 'selected item matches Local Encoding');
  });

  test('source name is displayed in the container', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    const column = {
      field: 'sourceName',
      title: 'adminUsm.policyWizard.filePolicy.sourceName',
      width: '15vw',
      displayType: 'sourceNameInput'
    };

    this.setProperties({
      column,
      encodingOptions,
      item
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        encodingOptions=encodingOptions
        item=item
      }}
    `);
    assert.equal(findAll('.source-name').length, 1);
  });

  test('directory paths is displayed in the container', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    const column = {
      field: 'paths',
      title: 'adminUsm.policyWizard.filePolicy.logFilePath',
      width: '100%',
      displayType: 'paths'
    };

    this.setProperties({
      column,
      encodingOptions,
      item
    });

    this.set('sourceUpdated', () => {
      assert.ok(true, 'sourceUpdated should be called');
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        encodingOptions=encodingOptions
        item=item
        sourceUpdated=sourceUpdated
      }}
    `);
    assert.equal(findAll('.paths').length, 1, 'Directory paths are rendered');
    const value1 = findAll('.paths .directory-path input')[0].value;
    const value2 = findAll('.paths .directory-path input')[1].value;
    const value = 'path1';
    const [eventIdEl] = findAll('.paths input');
    await fillIn(eventIdEl, value);
    await triggerEvent(eventIdEl, 'blur');
    assert.equal(value1, 'path1', 'Default value for the first directory path is rendered correctly');
    assert.equal(value2, 'path2', 'Default value for the second directory path is rendered correctly');
  });

  test('exclusion filter is displayed in the text area container', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    const column = {
      field: 'exclusionFilters',
      title: 'adminUsm.policyWizard.filePolicy.exclusionFilters',
      width: '30vw',
      displayType: 'exclusionFilters'
    };

    this.setProperties({
      column,
      item
    });

    this.set('sourceUpdated', () => {
      assert.ok(true, 'sourceUpdated should be called');
    });

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        sourceUpdated=sourceUpdated
        item=item
      }}
    `);
    const value = 'filter-3\nfilter-4';
    const [eventIdEl] = findAll('.exclusion-filters textarea');
    await fillIn(eventIdEl, value);
    await triggerEvent(eventIdEl, 'blur');

    assert.equal(findAll('.exclusion-filters').length, 1);
  });

  test('file type is displayed in the container', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .policyWizFileSources(sources)
      .build();

    const column = {
      field: 'fileType',
      title: 'adminUsm.policyWizard.filePolicy.logFileType',
      width: '30vw',
      displayType: 'fileTypeInput'
    };

    this.setProperties({
      column,
      encodingOptions,
      item
    });

    this.set('delete', () => {});

    await render(hbs`
      {{usm-policies/policy-wizard/define-policy-sources-step/body-cell
        column=column
        encodingOptions=encodingOptions
        item=item
        delete=delete
      }}
    `);
    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), 'apache', 'selected item matches apache');
    assert.equal(findAll('.file-type .ember-power-select-trigger[aria-disabled=true]').length, 1, 'File type power-select control appears in the DOM and is disabled');
  });
});

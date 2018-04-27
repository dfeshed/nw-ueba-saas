import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, triggerEvent, click } from '@ember/test-helpers';
import engineResolver from '../../../../helpers/engine-resolver';

const sampleConfig = [
  {
    sectionName: 'File.General',
    fieldPrefix: 'fileProperties',
    fields: [
      {
        field: 'firstFileName'
      },
      {
        field: 'entropy'
      },
      {
        field: 'size',
        format: 'SIZE'
      },
      {
        field: 'format'
      }
    ]
  },
  {
    sectionName: 'File.Signature',
    fieldPrefix: 'fileProperties',
    fields: [
      {
        field: 'signature.features',
        format: 'SIGNATURE'
      },
      {
        field: 'signature.timeStamp',
        format: 'DATE'
      },
      {
        field: 'signature.thumbprint'
      },
      {
        field: 'signature.signer'
      }
    ]
  }
];

const sampleData = {
  fileProperties: {
    firstFileName: 'XXX Test',
    entropy: 1,
    size: 1024,
    format: 'PE',
    signature: {
      features: 'XXX unsigned',
      thumbprint: '',
      signer: ''
    }
  }
};

module('Integration | Component | host-detail/base-property-panel', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders the property panel', async function(assert) {
    assert.expect(1);
    await render(hbs`{{host-detail/base-property-panel}}`);
    assert.equal(findAll('.host-property-panel').length, 1, 'Expected to render the property panel');
  });

  test('it renders the title for property panel', async function(assert) {
    assert.expect(1);
    this.set('title', 'Test Panel');
    await render(hbs`{{host-detail/base-property-panel title=title}}`);
    assert.equal(find('.header-section .header-section__title h2').textContent.trim(), 'Test Panel', 'title should match to "Test Panel"');
  });
  test('it should render the properties based on config and data', async function(assert) {
    assert.expect(2);
    this.set('title', 'Test Panel');
    this.set('config', sampleConfig);
    this.set('data', sampleData);
    await render(hbs`{{host-detail/base-property-panel config=config data=data title=title}}`);
    assert.equal(findAll('.content-section .content-section__section-name').length, 2, 'total number of section should be 2');
    assert.equal(find('.content-section .content-section__section-name:first-child').textContent.trim(), 'File.General', 'First Section name should match');
  });

  test('it filter the properties by property name', async function(assert) {
    assert.expect(2);
    this.set('title', 'Test Panel');
    this.set('config', sampleConfig);
    this.set('data', sampleData);
    await render(hbs`{{host-detail/base-property-panel config=config data=data title=title}}`);
    const input = find('.header-section__search-box input');
    input.value = 'XXX';
    await triggerEvent('.header-section__search-box input', 'change');
    assert.equal(findAll('.content-section__property').length, 2, 'Two matching properties');

    input.value = 'ZZZ';
    await triggerEvent('.header-section__search-box input', 'change');
    assert.equal(find('.content-section .message').textContent.trim(), 'No matching results', 'Should display empty message');
  });

  test('it should hide the property with empty value', async function(assert) {
    assert.expect(3);
    this.set('title', 'Test Panel');
    this.set('config', sampleConfig);
    this.set('data', sampleData);
    await render(hbs`{{host-detail/base-property-panel config=config data=data title=title}}`);
    assert.equal(findAll('.content-section__property').length, 8, 'All the properties');

    await click('.header-section__check-box input');
    assert.equal(findAll('.content-section__property').length, 5, 'Properties with non empty values');

    await click('.header-section__check-box input');
    assert.equal(findAll('.content-section__property').length, 8, 'All the properties');
  });
});
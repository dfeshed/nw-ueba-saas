import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

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

moduleForComponent('host-detail/base-property-panel', 'Integration | Component | host details base-property-panel', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('redux');
  }
});

test('it renders the property panel', function(assert) {
  this.render(hbs`{{host-detail/base-property-panel}}`);
  assert.equal(this.$('.host-property-panel').length, 1, 'Expected to render the property panel');
});

test('it renders the title for property panel', function(assert) {
  this.set('title', 'Test Panel');
  this.render(hbs`{{host-detail/base-property-panel title=title}}`);
  assert.equal(this.$('.header-section .header-section__title h2').text().trim(), 'Test Panel', 'title should match to "Test Panel"');
});
test('it should render the properties based on config and data', function(assert) {
  this.set('title', 'Test Panel');
  this.set('config', sampleConfig);
  this.set('data', sampleData);
  this.render(hbs`{{host-detail/base-property-panel config=config data=data title=title}}`);
  assert.equal(this.$('.content-section .content-section__section-name').length, 2, 'total number of section should be 2');
  assert.equal(this.$('.content-section .content-section__section-name:eq(0)').text().trim(), 'File.General', 'First Section name should match');
});

test('it filter the properties by property name', function(assert) {
  this.set('title', 'Test Panel');
  this.set('config', sampleConfig);
  this.set('data', sampleData);
  this.render(hbs`{{host-detail/base-property-panel config=config data=data title=title}}`);
  const $input = this.$('.header-section__search-box input');
  $input.val('XXX');
  $input.change();
  assert.equal(this.$('.content-section__property').length, 2, 'Two matching properties');

  $input.val('ZZZ');
  $input.change();
  assert.equal(this.$('.content-section .message').text().trim(), 'No matching results', 'Should display empty message');
});

test('it should hide the property with empty value', function(assert) {
  this.set('title', 'Test Panel');
  this.set('config', sampleConfig);
  this.set('data', sampleData);
  this.render(hbs`{{host-detail/base-property-panel config=config data=data title=title}}`);
  assert.equal(this.$('.content-section__property').length, 8, 'All the properties');

  this.$('.header-section__check-box input').trigger('click');
  assert.equal(this.$('.content-section__property').length, 5, 'Properties with non empty values');

  this.$('.header-section__check-box input').trigger('click');
  assert.equal(this.$('.content-section__property').length, 8, 'All the properties');
});

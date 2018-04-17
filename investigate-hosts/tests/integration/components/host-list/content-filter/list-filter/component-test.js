import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import $ from 'jquery';
import { patchSocket } from '../../../../../helpers/patch-socket';

const defaultConfig = {
  selected: [],
  propertyName: 'signature',
  panelId: 'list-filter-test',
  label: 'investigateHosts.hosts.column.groups',
  options: ['singned', 'unsigned', 'other'],
  expression: {
  }
};
moduleForComponent('host-list/content-filter/list-filter', 'Integration | Component | endpoint host-list/content-filter list-filter', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it should render the list filter button', function(assert) {
  this.set('config', defaultConfig);
  this.render(hbs`{{host-list/content-filter/list-filter config=config}}`);
  assert.equal(this.$('.filter-trigger-button').length, 1, 'List filter button exists');
  assert.equal(this.$('.filter-trigger-button span').text().trim(), 'Agent Group: All', 'Should display correct filter label');
});

test('should show filter options on clicking the trigger button', function(assert) {
  this.set('config', defaultConfig);
  this.render(hbs`{{host-list/content-filter/list-filter config=config}}`);
  assert.equal(this.$('.filter-trigger-button').length, 1, 'List filter button exists');
  assert.equal(this.$('.filter-trigger-button span').text().trim(), 'Agent Group: All', 'Should display correct filter label');
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    assert.equal($('.list-filter__content').length, 1);
    assert.equal($('.list-filter__content li').length, 3);
  });

});

test('it parse the given expression correctly for display', function(assert) {
  const expression = {
    propertyName: 'signature',
    propertyValues: [{ value: 'singned' }],
    restrictionType: 'IN'
  };
  this.set('config', { ...defaultConfig, expression });
  this.render(hbs`{{host-list/content-filter/list-filter config=config}}`);
  assert.equal(this.$('.filter-trigger-button span').text().trim(), 'Agent Group: singned', 'Should display correct filter label');
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    assert.equal($('.list-filter__content').length, 1);
    assert.equal($('.list-filter__content .rsa-form-checkbox:checked').length, 1);
  });
});


test('it should send correct expression for filtering on update', function(assert) {
  assert.expect(6);
  const expression = {
    propertyName: 'signature',
    propertyValues: [{ value: 'singned' }],
    restrictionType: 'IN'
  };
  this.set('config', { ...defaultConfig, expression });

  patchSocket((method, model, query) => {
    assert.equal(method, 'machines');
    assert.deepEqual(query.data.criteria.expressionList, [{
      propertyName: 'signature',
      propertyValues: [{ value: 'singned' }, { value: 'unsigned' }],
      restrictionType: 'IN'
    }]);
  });
  this.render(hbs`{{host-list/content-filter/list-filter config=config}}`);
  assert.equal(this.$('.filter-trigger-button span').text().trim(), 'Agent Group: singned', 'Should display correct filter label');
  this.$('.filter-trigger-button').trigger('click');
  return wait().then(() => {
    assert.equal($('.list-filter__content').length, 1);
    assert.equal($('.list-filter__content .rsa-form-checkbox:checked').length, 1);
    $('.list-filter__content input:eq(1)').change();
    $('.footer button').trigger('click');
    return wait().then(() => {
      assert.equal(this.$('.filter-trigger-button span').text().trim(), 'Agent Group: singned', 'Should display correct filter label');
    });
  });
});

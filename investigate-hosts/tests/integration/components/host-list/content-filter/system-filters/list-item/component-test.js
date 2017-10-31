import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../../helpers/engine-resolver';
const filter = {
  'createdBy': 'admin',
  'createdOn': 1495517131585,
  'lastModifiedBy': 'admin',
  'lastModifiedOn': 1495517131585,
  'id': '5923c7cbd8d4ae128db98c98',
  'name': 'JAZZ_NWE_5_AGENTS',
  'filterType': 'MACHINE',
  'criteria': {
    'criteriaList': [],
    'expressionList': [
      {
        'propertyName': 'machine.agentVersion',
        'restrictionType': 'IN',
        'propertyValues': [
          {
            'value': '5.0.0.0'
          }
        ]
      }
    ],
    'predicateType': 'AND'
  },
  'systemFilter': false
};
moduleForComponent('host-list/content-filter/system-filters/list-item', 'Integration | Component | List Item', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  },
  afterEach() {
  }
});

test('it renders the filter name', function(assert) {
  this.set('applyFilter', function(id) {
    assert.equal(id, 11);
  });
  this.set('filter', filter);
  this.render(hbs`{{host-list/content-filter/system-filters/list-item filter=filter}}`);
  assert.equal(this.$('.filter-list__item-label').text().trim(), 'JAZZ_NWE_5_AGENTS');

});

test('it shows the delete button on mouse hover', function(assert) {
  this.set('filter', filter);
  this.set('applyFilter', function(id) {
    assert.equal(id, 11);
  });
  this.render(hbs`{{host-list/content-filter/system-filters/list-item applyFilter=(action applyFilter 11) filter=filter}}`);
  assert.equal(this.$('.filter-list__item-label').text().trim(), 'JAZZ_NWE_5_AGENTS');
  assert.equal(this.$('.delete-filter:visible').length, 0);
  this.$('.filter-list__item').trigger('mouseover');
  assert.equal(this.$('.filter-list__item').hasClass('is-hovering'), true);
  this.$('.filter-list__item').trigger('mouseout');
  assert.equal(this.$('.filter-list__item').hasClass('is-hovering'), false);
});

test('should send delete action with selected id', function(assert) {
  this.set('filter', filter);
  this.set('deleteFilter', function(id) {
    assert.equal(id, 1);
  });
  this.set('applyFilter', function(id) {
    assert.equal(id, 11);
  });
  this.render(hbs`{{host-list/content-filter/system-filters/list-item applyFilter=(action applyFilter 11) filter=filter deleteFilter=(action deleteFilter 1)}}`);
  assert.equal(this.$('.delete-filter:visible').length, 0);
  this.$('.filter-list__item').trigger('mouseover');
  this.$('.delete-filter').trigger('click');
});

test('should send apply filter action on clicking the filter name', function(assert) {
  this.set('filter', filter);
  this.set('applyFilter', function(id) {
    assert.equal(id, 11);
  });
  this.render(hbs`{{host-list/content-filter/system-filters/list-item filter=filter applyFilter=(action applyFilter 11)}}`);
  assert.equal(this.$('.delete-filter:visible').length, 0);
  this.$('.filter-list__item').trigger('click');
});

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import wait from 'ember-test-helpers/wait';
import IncidentsCube from 'sa/utils/cube/incidents';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-respond/landing-page/respond-index/list-view', 'Integration | Component | rsa respond/landing page/respond index/list view', {
  integration: true,

  beforeEach() {
    let allCube = IncidentsCube.create({
      array: []
    });

    allCube.get('records').pushObjects([EmberObject.create({
      riskScore: 1,
      id: 'INC-491',
      name: 'Suspected command and control communication with www.mozilla.com',
      createdBy: 'User X',
      created: '2015-10-10',
      lastUpdated: '2015-10-10',
      statusSort: 0,
      prioritySort: 0,
      alertCount: 10,
      eventCount: 2,
      sources: ['Event Stream Analysis'],
      assignee: {
        id: '1'
      },
      categories: []
    }),
    EmberObject.create({
      riskScore: 1,
      id: 'INC-492',
      name: 'Suspected command and control communication with www.mozilla.com',
      createdBy: 'User X',
      created: '2015-10-10',
      lastUpdated: '2015-10-10',
      statusSort: 1,
      prioritySort: 1,
      alertCount: 10,
      eventCount: 2,
      sources: ['Event Stream Analysis'],
      assignee: {
        id: '2'
      },
      categories: [{
        name: 'childCategory1',
        parent: 'parentCategory1',
        id: '1'
      }, {
        name: 'childCategory2',
        parent: 'parentCategory1',
        id: '2'
      }]
    })]);

    let users = [
      EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', friendlyName: 'user2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', friendlyName: 'user3', email: 'user3@rsa.com' })
    ];

    let categoryTags = [
      EmberObject.create({
        id: '1',
        parent: 'parentCategory1',
        name: 'childCategory1'
      }),
      EmberObject.create({
        id: '2',
        parent: 'parentCategory1',
        name: 'childCategory2'
      })
    ];

    this.setProperties({
      allIncidents: allCube,
      users,
      categoryTags
    });
  }
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-respond-list__filter-panel').length, 1, 'Filter panel is present');
  assert.equal(this.$('.rsa-data-table').length, 1, 'Data table is present');

  let that = this;
  return wait().then(function() {
    assert.equal(that.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'All incidents are displayed');
  });
});


test('filter panel renders', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  assert.ok(this.$('.rsa-respond-list__filter-panel__priority'), 'Priority filter is present');
  assert.ok(this.$('.rsa-respond-list__filter-panel__priority .rsa-form-checkbox').length > 0, 'Priority options are present');

  assert.ok(this.$('.rsa-respond-list__filter-panel__status'), 'Status filter is present');
  assert.ok(this.$('.rsa-respond-list__filter-panel__status .rsa-form-checkbox').length > 0, 'Status options are present');

  assert.ok(this.$('.rsa-respond-list__filter-panel__assignee-selector'), 'Assignee filter is present');

  assert.ok(this.$('.rsa-respond-list__filter-panel__category'), 'Category filter is present');
  assert.ok(this.$('.rsa-respond-list__filter-panel__category .rsa-form-tag-manager'), 'Category options are present');

  assert.ok(this.$('.rsa-respond-list__filter-panel__reset-button'), 'Filter button is present');
});

test('Filters affect the number of incidents on screen', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view allIncidents=allIncidents users=users categoryTags=categoryTags}}`);

  let resetButton = this.$().find('.rsa-respond-list__filter-panel__reset-button .expand');

  let that = this;
  return wait().then(function() {
    let totalIncidentCount = that.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length;
    assert.equal(totalIncidentCount, 2, 'Initial list has 2 incidents');

    that.$('.rsa-respond-list__filter-panel__priority .priority-1 input:first').prop('checked', true).trigger('change');

    return wait().then(function() {
      assert.equal(that.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Priority filter works');

      resetButton.click();

      return wait().then(function() {
        assert.equal(that.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, totalIncidentCount, 'Reset-button resets selected priority filters');

        that.$('.rsa-respond-list__filter-panel__status .status-1 input:first').prop('checked', true).trigger('change');
        return wait().then(function() {
          assert.equal(that.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Status filter works');

          resetButton.click();

          return wait().then(function() {
            assert.equal(that.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, totalIncidentCount, 'Reset-button resets selected Status filters');
            that.$('.rsa-respond-list__filter-panel__assignee-selector .prompt').click();
            that.$('.rsa-respond-list__filter-panel__assignee-selector select').val('1').trigger('change');

            return wait().then(function() {
              assert.equal(that.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Assignee filter works');

              resetButton.click();
              return wait().then(function() {
                assert.equal(that.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, totalIncidentCount, 'Reset-button resets selected Assignee filters');

                that.$('.rsa-respond-list__filter-panel__category .rsa-form-tag-manager .rsa-form-tag-manager__tree-toggle__icon').click();
                that.$('.rsa-respond-list__filter-panel__category .rsa-form-tag-manager .rsa-content-tree .rsa-content-accordion:first-child').click();
                that.$('.rsa-respond-list__filter-panel__category .rsa-form-tag-manager .rsa-content-tree .rsa-content-accordion:first-child .content .rsa-content-tree__child-label:first-child').click();

                return wait().then(function() {
                  assert.equal(that.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Category filter works');

                  resetButton.click();
                  return wait().then(function() {
                    assert.equal(that.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, totalIncidentCount, 'Reset-button resets selected Category filters');
                  });
                });
              });
            });
          });
        });
      });
    });
  });
});
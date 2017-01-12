import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import resourceTypes from './data/resource-types';
import metaValues from './data/meta-values';
import metaKeys from './data/meta-keys';
import categories from './data/categories';
import Ember from 'ember';
import { clickTrigger } from '../../../../helpers/ember-power-select';
import wait from 'ember-test-helpers/wait';


const media = ['log', 'packet', 'log and packet'];

const { $, run } = Ember;

moduleForComponent('rsa-live/search-criteria', 'Integration | Component | rsa live/search criteria', {
  integration: true
});

function triggerMouseEvent(node, eventType) {
  const clickEvent = document.createEvent('MouseEvents');
  clickEvent.initEvent(eventType, true, true);
  node.dispatchEvent(clickEvent);
}

test('The Search Criteria component renders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });
  this.set('resourceTypes', resourceTypes);
  this.render(hbs`{{rsa-live/search-criteria}}`);
  assert.equal(this.$('.rsa-live-search-criteria').length, 1,
      'The element with the class name .rsa-live-search-criteria is found in the DOM');
});

test('The advanced search fields are shown when the advanced search toggle is clicked', function(assert) {
  this.render(hbs`{{rsa-live/search-criteria}}`);
  assert.equal(this.$('.rsa-live-search-criteria.is-showing-advanced').length, 0,
      'The Advanced Search section is invisible by default');

  this.$('.advanced-toggle input[type="checkbox"]').click();

  assert.equal(this.$('.rsa-live-search-criteria.is-showing-advanced').length, 1,
      'The Advanced Search section is visible after clicking toggle switch');

  this.$('.advanced-toggle input[type="checkbox"]').click();

  assert.equal(this.$('.rsa-live-search-criteria.is-showing-advanced').length, 0,
      'The Advanced Search section is invisible again after re-clicking toggle');
});


test('The Resource Type dropdown is populated with options', function(assert) {
  this.set('resourceTypes', resourceTypes);
  this.render(hbs`{{rsa-live/search-criteria
              resourceTypes=(readonly resourceTypes)
              onchange="search"
      }}`);
  clickTrigger('.live-search-form-resource-type');
  assert.equal($('.ember-power-select-option').length, 35, 'The Power Select dropdown opens with 35 items when clicked');
});

test('The Medium dropdown is populated with options', function(assert) {
  this.set('media', media);
  this.render(hbs`{{rsa-live/search-criteria              
              media=(readonly media)
              onchange="search"
      }}`);
  clickTrigger('.live-search-form-medium');
  assert.equal($('.ember-power-select-option').length, 3, 'The Power Select dropdown opens with 3 items when clicked');
});

test('The Meta Keys dropdown is populated with options', function(assert) {
  this.set('metaKeys', metaKeys);
  this.render(hbs`{{rsa-live/search-criteria              
              metaKeys=(readonly metaKeys)
              onchange="search"
      }}`);
  clickTrigger('.live-search-form-meta-keys');
  assert.equal($('.ember-power-select-option').length, 59, 'The Power Select dropdown opens with 59 items when clicked');
});

test('The Meta Values dropdown is populated with options', function(assert) {
  this.set('metaValues', metaValues);
  this.render(hbs`{{rsa-live/search-criteria              
              metaValues=(readonly metaValues)
              onchange="search"
      }}`);
  clickTrigger('.live-search-form-meta-values');
  assert.equal($('.ember-power-select-option').length, 341, 'The Power Select dropdown opens with 341 items when clicked');
});

test('The Meta Values dropdown is populated with options', function(assert) {
  this.set('metaValues', metaValues);
  this.render(hbs`{{rsa-live/search-criteria              
              metaValues=(readonly metaValues)
              onchange="search"
      }}`);
  clickTrigger('.live-search-form-meta-values');
  assert.equal($('.ember-power-select-option').length, 341, 'The Power Select dropdown opens with 341 items when clicked');
});

test('Search action called with correct value when user types in keywords field', function(assert) {
  let searchPayload;

  assert.expect(2);

  this.set('searchCriteria', { textSearch: 'test' });
  this.on('search', function(searchCriteria) {
    searchPayload = searchCriteria;
  });
  this.render(hbs`{{rsa-live/search-criteria                            
              searchCriteria=searchCriteria
              onchange="search"
      }}`);
  const searchBox = $('input.search-box');
  assert.equal(searchBox.val(), 'test', 'The starting value for keywords input is the searchCriteria.textSearch value');
  searchBox.val('symantec').trigger('keyup');

  return wait().then(() => {
    assert.equal(searchPayload.textSearch, 'symantec', 'Search information updated on typing in keywords input');
  });
});

test('Search action called with correct value when user types in keywords field', function(assert) {
  let searchPayload;

  assert.expect(1);

  this.set('searchCriteria', {});
  this.set('categories', categories);
  this.on('search', function(searchCriteria) {
    searchPayload = searchCriteria;
  });
  this.render(hbs`{{rsa-live/search-criteria                            
              categories=(readonly categories)
              searchCriteria=searchCriteria
              onchange="search"
      }}`);

  const firstTreeNode = $('.rsa-tree > .rsa-tree-node').first().find('> .node-name');

  firstTreeNode.click();
  return wait().then(() => {
    assert.equal(searchPayload.tags, 'threat', 'Clicking on a node in the categories tree signals a search event with the correct payload');
  });
});

test('Selecting an option from the power-select dropdown signals a search event with the expected payload', function(assert) {
  let searchPayload;

  assert.expect(1);

  this.set('searchCriteria', {});
  this.set('metaValues', metaValues);
  this.on('search', function(searchCriteria) {
    searchPayload = searchCriteria;
  });

  this.render(hbs`{{rsa-live/search-criteria  
              searchCriteria=(readonly searchCriteria)
              metaValues=(readonly metaValues)
              onchange="search" }}`);

  run(() => {
    clickTrigger('.live-search-form-meta-values');
    const [ option ] = $('.ember-power-select-option').first();
    triggerMouseEvent(option, 'mouseover');
    triggerMouseEvent(option, 'mousedown');
    triggerMouseEvent(option, 'mouseup');
    triggerMouseEvent(option, 'click');

    assert.equal(searchPayload.metaValues[0], 'tor-exit-node-ip', 'The dropdown / power select option value is added to the search payload');
  });
});

import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import searchResults from '../../../../data/subscriptions/live-search/findAll/data';
import { FETCH_SEARCH_RESULTS_SUCCESS, RESOURCE_TOGGLE_SELECT } from 'live-content/actions/live-content/types';
import engineResolverFor from '../../../../helpers/engine-resolver';
import Ember from 'ember';

const { $, copy } = Ember;

moduleForComponent('rsa-live/search-results', 'Integration | Component | rsa live/search results', {
  integration: true,
  resolver: engineResolverFor('live-content'),
  beforeEach() {
    this.inject.service('redux');
    this.get('redux').dispatch({
      type: FETCH_SEARCH_RESULTS_SUCCESS,
      payload: searchResults
    });
    $('body').append($('<style>#ember-testing-container { background-color: #212121; } #ember-testing > div {height: 100%;}</style>'));
  }
});

test('The Search Result component appears in the DOM', function(assert) {
  this.on('showDetailsPanel', function() { });

  this.render(hbs`{{rsa-live/search-results showDetailsPanel=(action 'showDetailsPanel')}}`);

  assert.equal(this.$('.rsa-live-search-results').length, 1, 'The Search Results component\'s class name appears in the DOM');
  assert.equal(this.$('.rsa-data-table-body-row').length, 20, 'The correct number of data table rows are found based on the search result data');
  assert.equal(this.$('.rsa-live-search-results .rsa-data-table-header-cell').length, 6, 'There are six columns (header cells) displayed');
  assert.equal(this.$('.rsa-form-checkbox.checked').length, 0, 'There are no row selection checkboxes checked since there a no selections');
});

test('Presence of selections appears as checked resources in the results', function(assert) {

  searchResults.items.forEach((result, index) => {
    if (index % 2 === 0) { // select every other result
      this.get('redux').dispatch({
        type: RESOURCE_TOGGLE_SELECT,
        payload: result
      });
    }
  });

  this.on('showDetailsPanel', function() { });
  this.render(hbs`{{rsa-live/search-results showDetailsPanel=(action 'showDetailsPanel')}}`);

  assert.equal(this.$('.rsa-form-checkbox.checked').length, 10, '10 rows are selected based on the selection object');
});

test('Pagination control shows correctly enabled/disabled buttons and message for the first page of results', function(assert) {

  this.on('showDetailsPanel', function() { });
  this.render(hbs`{{rsa-live/search-results showDetailsPanel=(action 'showDetailsPanel')}}`);

  assert.equal(this.$('.live-pagination-showing-message').text().trim(), 'Showing results 1 - 20 of 254', 'The result summary text is correctly displayed');
  assert.equal(this.$('.live-search-pagination .go-to-beginning.is-disabled').length, 1, 'The "Go to first page" button is disabled');
  assert.equal(this.$('.live-search-pagination .go-to-previous.is-disabled').length, 1, 'The "Previous" button is disabled');
  assert.equal(this.$('.live-search-pagination .go-to-end:not(.is-disabled)').length, 1, 'The "Go to end" button is not disabled');
  assert.equal(this.$('.live-search-pagination .go-to-next:not(.is-disabled)').length, 1, 'The "Next" button is not disabled');
});

test('Pagination control shows correctly enabled/disabled buttons and message for the last page of results', function(assert) {
  const lastPageOfResults = copy(searchResults, true);

  lastPageOfResults.pageNumber = lastPageOfResults.totalPages - 1; // zero based index
  lastPageOfResults.hasNext = false;
  lastPageOfResults.hasPrevious = true;

  this.get('redux').dispatch({
    type: FETCH_SEARCH_RESULTS_SUCCESS,
    payload: lastPageOfResults
  });

  this.on('showDetailsPanel', function() { });
  this.render(hbs`{{rsa-live/search-results showDetailsPanel=(action 'showDetailsPanel')}}`);

  assert.equal(this.$('.live-pagination-showing-message').text().trim(), 'Showing results 241 - 254 of 254', 'The result summary text is correctly displayed');
  assert.equal(this.$('.live-search-pagination .go-to-end.is-disabled').length, 1, 'The "Go to end" button is disabled');
  assert.equal(this.$('.live-search-pagination .go-to-next.is-disabled').length, 1, 'The "Next" button is disabled');
  assert.equal(this.$('.live-search-pagination .go-to-beginning:not(.is-disabled)').length, 1, 'The "Go to beginning" button is not disabled');
  assert.equal(this.$('.live-search-pagination .go-to-previous:not(.is-disabled)').length, 1, 'The "Previous" button is not disabled');

});

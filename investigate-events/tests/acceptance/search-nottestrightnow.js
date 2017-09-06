import { test } from 'qunit';
import moduleForAcceptance from 'dummy/tests/helpers/module-for-acceptance';

const resultBodyRows = '.rsa-investigate-events-table__body .rsa-investigate-events-table-row';

moduleForAcceptance('Acceptance | investigate | search', {
});

const submitBlankSearch = () => {
  click('.rsa-investigate-query-bar__service .ember-power-select-trigger');
  click('.ember-power-select-options li:first-of-type');
  click('.rsa-investigate-query-bar__time-range .ember-power-select-trigger');
  click('.ember-power-select-options li:first-of-type');
  click('.rsa-investigate-query-bar__submit button');
};

test('can run a search and get a list of results', function(assert) {
  visit('/investigate');
  waitFor('.rsa-investigate-query-bar__service');

  andThen(() => {
    submitBlankSearch();
  });

  waitFor('.rsa-data-table-load-more.complete');

  andThen(() => {
    const rows = find(resultBodyRows);
    assert.ok(rows.length > 0);
  });
});



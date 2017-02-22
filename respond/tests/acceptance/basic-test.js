import { skip } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';
import engineResolverFor from '../helpers/engine-resolver';

moduleForAcceptance('Acceptance | basic', {
  resolver: engineResolverFor('respond')
});

skip('visiting /respond redirects to /respond/incidents', function(assert) {
  visit('/respond');

  andThen(function() {
    assert.equal(currentURL(), '/respond/incidents', 'The base /respond endpoint should redirect to respond/incidents');
  });
});

// Note: Because of data table lazy rendering and phantomjs, the complete set of returned incidents do not
// render to the DOM in the phantomjs test. Currently it's only 32 items rendered. For now, we will only
// test that over 20 results are returned
skip('visiting /respond/incidents shows 20 or more results', function(assert) {
  visitAndWaitForReduxStateChange('/respond/incidents', 'respond.incidents.incidents');
  andThen(function() {
    assert.ok(find('.rsa-data-table-body-row').length >= 20, 'There are 20 or more rows in the incidents data table');
  });
});

skip('clicking on a row redirects to the incident details route', function(assert) {
  visitAndWaitForReduxStateChange('/respond/incidents', 'respond.incidents.incidents');
  andThen(function() {
    click('.rsa-data-table-body-row:first');
    andThen(function() {
      assert.equal(currentURL(), '/respond/incident/INC-102', 'The first row click navigates the user to INC-102 details page');
    });
  });
});

skip('Clicking on the filter button in the toolbar opens the filter panel', function(assert) {
  visitAndWaitForReduxStateChange('/respond/incidents', 'respond.incidents.incidents');
  andThen(function() {
    assert.equal(find('.rsa-respond-incidents.show-more-filters').length, 0, 'The class show-more-filters does not appear on the incidents wrapper');
    click('.more-filters-button .rsa-icon');
    andThen(function() {
      assert.equal(find('.rsa-respond-incidents.show-more-filters').length, 1, 'The class show-more-filters appears on the incidents wrapper');
    });
  });
});
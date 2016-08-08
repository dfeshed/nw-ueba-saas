import Ember from 'ember';
import BaseSelectors from 'component-lib/selectors/defaults';

const { $: Ember$ } = Ember;

export default Ember$.extend({}, BaseSelectors, {
  'nav': {
    'monitorLink': '.js-test-nav-monitor-link',
    'respondLink': '.js-test-nav-respond-link',
    'investigateLink': '.js-test-nav-investigate-link',
    'adminLink': '.js-test-nav-admin-link',
    'changeThemeButton': '.js-test-nav-theme-btn',
    'changeLocaleButton': '.js-test-nav-locale-btn',
    'enLocaleListItem': '.js-test-nav-locale-en-li',
    'jpLocaleListItem': '.js-test-nav-locale-jp-li',
    'logoutButton': '.js-test-nav-logout-btn'
  },
  'pages': {
    'monitor': {
      'root': '.js-test-monitor-root'
    },
    'explore': {
      'url': '/do/explore',
      'path': 'protected.explore',
      'root': '.js-test-explore-root'
    },
    'admin': {
      'root': '.js-test-admin-root'
    },
    'login': {
      'url': '/login',
      'root': '.js-test-login-root',
      'usernameInput': 'label[class$=rsa-form-input] input',
      'passwordInput': 'label[class$=rsa-form-input] input',
      'loginButton': 'div[class$=rsa-application-content] button[class*=rsa-form-button]',
      'errorMessage': 'rsa-login-error-message'
    },
    'respond': {

      'root': '.js-test-respond-root',
      'url': '/do/respond',
      'path': 'protected.respond.index',
      'card': {
        'allIncElm': 'header[class^=rsa-respond-index__toolbar] a[class=ember-view]',
        'incSection': 'section[class$=rsa-d3list]',
        'incTile': {
          'editButton': 'div[class^=rsa-edit-tool]',
          'assigneeSelect': 'div[class^=rsa-incident-tile-assignee]',
          'assigneeLabel': 'div[class^=rsa-incident-tile-assignee]',
          'statusSelect': '.rsa-incident-tile-status-selector select',
          'statusLabel': '.rsa-incident-tile-status-selector .prompt',
          'prioritySelect': '.rsa-incident-tile-priority-selector select',
          'priorityLabel': '.rsa-incident-tile-priority-selector .prompt'
        }
      },
      'list': {
        'table': 'div[class=rsa-respond-list]',
        'columns': '.rsa-data-table-header-cell'
      },
      'toggleViewHeader': 'header[class$=rsa-respond-index-header]',
      'listViewBtn': 'div[class^=rsa-respond-index-header__list-btn]',
      'cardViewBtn': 'div[class^=rsa-respond-index-header__card-btn]',
      'incidentsPanel': {
        'myQueueButton': '.js-test-respond-incs-my-queue-btn',
        'allIncidentsButton': '.js-test-respond-incs-all-inc-btn',
        'filterButton': '.js-test-respond-incs-filter-btn',
        'sortButton': '.js-test-respond-incs-sort-btn',

        'filterPopover': {
          'hourButton': '.js-test-respond-incs-filter-hour-btn',
          'dayButton': '.js-test-respond-incs-filter-day-btn',
          'weekButton': '.js-test-respond-incs-filter-week-btn',
          'monthButton': '.js-test-respond-incs-filter-month-btn',

          'priorityAllListItem': '.js-test-respond-incs-filter-li[data-field=\'priority\'][data-group=\'null\']',
          'priorityLowListItem': '.js-test-respond-incs-filter-li[data-field=\'priority\'][data-group=\'0\']',
          'priorityMediumListItem': '.js-test-respond-incs-filter-li[data-field=\'priority\'][data-group=\'1\']',
          'priorityHighListItem': '.js-test-respond-incs-filter-li[data-field=\'priority\'][data-group=\'2\']',
          'priorityCriticalListItem': '.js-test-respond-incs-filter-li[data-field=\'priority\'][data-group=\'3\']',

          'statusAllListItem': '.js-test-respond-incs-filter-li[data-field=\'status\'][data-group=\'null\']',
          'statusNewListItem': '.js-test-respond-incs-filter-li[data-field=\'status\'][data-group=\'0\']',
          'statusAssignedListItem': '.js-test-respond-incs-filter-li[data-field=\'status\'][data-group=\'1\']',
          'statusInProgressListItem': '.js-test-respond-incs-filter-li[data-field=\'status\'][data-group=\'2\']',
          'statusRemediationRequestedListItem': '.js-test-respond-incs-filter-li[data-field=\'status\'][data-group=\'3\']',
          'statusRemediationCompleteListItem': '.js-test-respond-incs-filter-li[data-field=\'status\'][data-group=\'4\']',
          'statusClosedListItem': '.js-test-respond-incs-filter-li[data-field=\'status\'][data-group=\'5\']',
          'statusFalsePositiveListItem': '.js-test-respond-incs-filter-li[data-field=\'status\'][data-group=\'6\']'
        },

        'sortPopover': {
          'idListItem': '.js-test-respond-incs-sort-id-li',
          'titleListItem': '.js-test-respond-incs-sort-title-li',
          'createdListItem': '.js-test-respond-incs-sort-created-li',
          'priorityListItem': '.js-test-respond-incs-sort-priority-li',
          'statusListItem': '.js-test-respond-incs-sort-status-li',
          'ascendingListItem': '.js-test-respond-incs-sort-ascending-li',
          'descendingListItem': '.js-test-respond-incs-sort-descending-li'
        },

        'incidentUnorderedList': '.js-test-respond-incs-ul',
        'footer': '.js-test-respond-incs-footer'
      }
    }
  }
});

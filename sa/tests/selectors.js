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
      'carouselItems': 'div[class=rsa-carousel__visible-items] div[class^=rsa-incident-content-card]',
      'incidentTile': 'div[class=rsa-carousel__visible-items] span[class=rsa-incident-tile-created-date]',
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
          'priorityLabel': '.rsa-incident-tile-priority-selector .prompt',
          'scoreElm': 'div[class=score]',
          'incLbl': 'label[class=rsa-incident-tile-id]',
          'incTitleName': 'li[class=rsa-incident-tile-name]',
          'priorityValueElm': 'div[class^=rsa-incident-tile-priority-selector] div[class=prompt] div',
          'assigneeValueElm': 'div[class^=rsa-incident-tile-assignee-selector] div[class=prompt] div',
          'alertCountLbl': 'label[class=rsa-incident-tile-alert-count]',
          'eventCountLbl': 'label[class=rsa-incident-tile-event-count]',
          'incidentTileSourcesElm': 'div[class=rsa-incident-tile-sources] div',
          'createdDateTimeElm': 'span[class=rsa-incident-tile-created-date] span[class=datetime]',
          'createdDateTimeAgoElm': 'span[class=rsa-incident-tile-created-date] span[class=time-ago]'
        }
      },
      'list': {
        'table': 'div[class=rsa-respond-list]',
        'columns': '.rsa-data-table-header-cell'
      },
      'details': {
        'overview': {
          'accordion': '.rsa-respond-detail-overview__accordion',
          'textarea': '.rsa-respond-detail-overview__accordion textarea'
        }
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
    },
    'incident': {
      'closeButton': 'div[class^=rsa-incident-detail-header__buttons__close-incident]',
      'storylineElm': 'vbox[class=rsa-response-storyline]',
      'storylineTimeTxt': 'vbox[class^=rsa-response-storyline__times] span[class=datetime]',
      'storylineMainRow': 'hbox[class=rsa-response-storyline__main-row]',
      'journalEntryNote': 'div[class$=rsa-journal-entry] label[class^=rsa-journal-entry__edit-note]',
      'journalEntryWrapper': 'div[class=rsa-journal-wrapper__journal-viewport]',
      'myNotesRdoBtn': 'label[class^=rsa-journal-wrapper__journal-sort__my-notes]  span[class=inner-wrapper]',
      'allNotesRdoBtn': 'label[class^=rsa-journal-wrapper__journal-sort__all-notes] span input',
      'createdByElm': 'div[class=rsa-journal-entry__created-by]',
      'journalEntryNoteTxtElm': 'div[class=rsa-journal-entry__note]',
      'investigationMilestonesTxtElm': 'div[class=rsa-journal-entry__milestones] div[class^=rsa-journal-entry__milestone]',
      'dateTimeElm': 'span[class^=rsa-journal-entry__created_time-ago] span',
      'journalEditBtn': 'div[class^=rsa-journal-entry__edit-journal] i[class*=rsa-icon-pencil]',
      'journalDeleteBtn': 'div[class^=rsa-journal-entry__delete-journal] i[class*=rsa-icon-bin-1]',
      'overviewTxtArea': 'label[class^=js-respond-detail-overview-textArea]',
      'journalShowBtn': 'span[class=journalAction]',
      'storylineIndicatorTypeElm': 'span[class=indicator_type]',
      'scoreElm': 'div[class=score]',
      'incLbl': 'label[class=rsa-incident-detail-header__id]',
      'incTitleName': 'label[class^=rsa-incident-detail-header__name] input',
      'priorityValueElm': 'div[class^=rsa-incident-detail-header__priority] div[class=prompt] div',
      'assigneeValueElm': 'div[class^=rsa-incident-detail-header__assignee] div[class=prompt] div',
      'alertCountLbl': 'div[class^=rsa-incident-detail-header__alerts] label',
      'eventCountLbl': 'div[class^=rsa-incident-detail-header__events] label',
      'incidentTileSourcesElm': 'div[class^= rsa-incident-tile-source]',
      'createdDateTimeElm': 'span[class^=rsa-incident-detail-header__created] span[class=datetime]',
      'createdDateTimeAgoElm': 'span[class^=rsa-incident-detail-header__created_time-ago] span[class=time-ago]'
    }
  }
});

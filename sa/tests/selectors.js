import Ember from 'ember';
import BaseSelectors from 'component-lib/selectors/defaults';

const { $: Ember$ } = Ember;

export default Ember$.extend({}, BaseSelectors, {
  'nav': {
    'monitorLink': '.js-test-nav-monitor-link',
    'respondLink': '.js-test-nav-respond-link',
    'investigateLink': '.js-test-nav-investigate-link',
    'configLink': '.js-test-nav-configure-link',
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
      'updateIndLbl': '.rsa-update-indicator__label',
      'card': {
        'allIncElm': 'header[class^=rsa-respond-index__toolbar] a[class=ember-view]',
        'incSection': 'section[class$=rsa-d3list]',
        'incTile': {
          'incidentTile': '.rsa-incident-tile',
          'saveIncidentButton': '.rsa-edit-tool .rsa-icon-check-1',
          'editButton': 'div[class^=rsa-edit-tool]',
          'assigneeDropDown': '.rsa-incident-tile-assignee-selector .rsa-icon-arrow-down-12',
          'statusDropDown': '.rsa-incident-tile-status-selector .rsa-icon-arrow-down-12',
          'priorityDropDown': '.rsa-incident-tile-priority-selector .rsa-icon-arrow-down-12',
          'assigneeSelect': 'div[class^=rsa-incident-tile-assignee]',
          'assigneeLabel': 'div[class^=rsa-incident-tile-assignee]',
          'statusSelect': '.rsa-incident-tile-status-selector select',
          'statusLabel': '.rsa-incident-tile-status-selector .prompt',
          'statusValue': '.rsa-incident-tile-status-selector .prompt div',
          'prioritySelect': '.rsa-incident-tile-priority-selector select',
          'priorityLabel': '.rsa-incident-tile-priority-selector .prompt',
          'scoreElm': 'div[class=score]',
          'incLbl': 'label[class=rsa-incident-tile-id]',
          'incTitleName': 'li[class=rsa-incident-tile-name]',
          'priorityValueElm': 'div[class^=rsa-incident-tile-priority-selector] div[class=prompt] div',
          'assigneeValueElm': 'div[class^=rsa-incident-tile-assignee-selector] div[class=prompt] div',
          'alertsCountLbl': '.rsa-incident-tile-alert-count',
          'eventCountLbl': 'label[class=rsa-incident-tile-event-count]',
          'incidentTileSourcesElm': 'div[class=rsa-incident-tile-sources] div',
          'createdDateTimeElm': 'span[class=rsa-incident-tile-created-date] span[class=datetime]',
          'createdDateTimeAgoElm': 'span[class=rsa-incident-tile-created-date] span[class=time-ago]',
          'assigneeSelectOption': '.rsa-incident-tile-assignee-selector select option',
          'prioritySelectOpt': '.rsa-incident-tile-priority-selector select option',
          'statusSelectOption': '.rsa-incident-tile-status-selector select option'
        }
      },
      'listView': {
        'listViewIcon': '.rsa-respond-index-header__list-btn .rsa-icon-view-headline',
        'gridViewIcon': '.rsa-respond-index-header__tile-btn .rsa-icon-view-module-1',
        'incidentCountLbl': '.rsa-respond-index-header__label',
        'riskScoreLbl': '.js-header-cell-rsa-respond-list-riskscore',
        'incidentIdLbl': '.js-header-cell-incident-id',
        'incidentNameLbl': '.js-header-cell-rsa-respond-list-name',
        'dateCreatedLbl': '.js-header-cell-rsa-respond-list-created',
        'assigneeLbl': '.js-header-cell-rsa-respond-list-assignee',
        'statusLbl': '.js-header-cell-rsa-respond-list-status',
        'alertCountLbl': '.js-header-cell-rsa-respond-list-alertCount',
        'sourcesLbl': '.js-header-cell-rsa-respond-list-sources',
        'eventsLbl': '.js-header-cell-rsa-respond-list-events',
        'criticalChkBox': '.priority-3 .inner-wrapper',
        'highChkBox': '.priority-2 .inner-wrapper',
        'mediumChkBox': '.priority-1 .inner-wrapper',
        'lowChkBox': '.priority-0 .inner-wrapper',
        'newChkBox': '.status-0 .inner-wrapper',
        'assignedChkBox': '.status-1 .inner-wrapper',
        'inProgressChkBox': '.status-2 .inner-wrapper',
        'remReqChkBox': '.status-3 .inner-wrapper',
        'remCompChkBox': '.status-4 .inner-wrapper',
        'closedChkBox': '.status-5 .inner-wrapper',
        'falsePositiveChkBox': '.status-6 .inner-wrapper',
        'resetFiltersBtn': '.rsa-respond-list__filter-panel__reset-button .rsa-form-button',
        'riskScoreCol': '.rsa-respond-list-riskscore .score',
        'incIdCol': '.incident-id',
        'incNameCol': '.rsa-respond-list-name',
        'dateCreatedCol': '.datetime',
        'assigneeCol': '.rsa-respond-list-assignee',
        'statusCol': '.rsa-respond-list-status',
        'sourcesCol': '.rsa-respond-list-sources .ember-view',
        'eventsCountCol': '.rsa-respond-list-events',
        'alertsCountCol': '.rsa-respond-list-alertCount',
        'assigneeNameOpt': '.rsa-respond-list__filter-panel__assignee-selector option',
        'assigneeDropDownSel': '.rsa-respond-list__filter-panel__assignee-selector .rsa-icon-arrow-down-12',
        'categoryTagPlusIcon': '.rsa-icon-add-circle-1',
        'globalCatIcon': '.rsa-icon-arrow-right-12',
        'globalCatNameElm': '.rsa-content-accordion',
        'catNameElm': '.rsa-content-tree__child-label',
        'catTreeElm': '.rsa-content-tree__tree-container'
      },
      'list': {
        'table': 'div[class=rsa-respond-list]',
        'columns': '.rsa-data-table-header-cell'
      },
      'details': {
        'header': {
          'detailHeader': '.rsa-incident-detail-header',
          'prioritySelectOption': '.rsa-incident-detail-header__priority select option'
        },
        'overview': {
          'accordion': '.rsa-respond-detail-overview__accordion',
          'textarea': '.rsa-respond-detail-overview textarea'
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
      'storylineElm': '.rsa-respond-storyline-wrapper',
      'storylineTimeTxt': '.rsa-respond-storyline-wrapper .rsa-content-datetime',
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
      'journalShowBtn': '.rsa-icon-notepad-edit',
      'scoreElm': '.score',
      'incLbl': '.rsa-incident-detail-header__id',
      'incTitleName': '.rsa-incident-detail-header__name input',
      'priorityValueElm': 'div[class^=rsa-incident-detail-header__priority] div[class=prompt] div',
      'assigneeValueElm': 'div[class^=rsa-incident-detail-header__assignee] div[class=prompt] div',
      'alertCountLbl': '.rsa-incident-detail-header__alerts label',
      'eventCountLbl': '.rsa-incident-detail-header__events label',
      'incidentTileSourcesElm': '.rsa-incident-tile-source',
      'createdDateTimeElm': '.rsa-incident-detail-header__created .datetime',
      'createdDateTimeAgoElm': '.rsa-incident-detail-header__created_time-ago .time-ago',
      'storylineIndicatorTypeElm': '.indicator_type',
      'categoryTagsDropdownIcon': '.rsa-content-accordion .rsa-icon-arrow-down-12',
      'addIcon': '.rsa-icon-add-circle-1',
      'categoryTagDropDownIcon': '.rsa-category-tags-panel .rsa-icon-arrow-right-12',
      'categoryListdropdownElm': '.rsa-content-tree',
      'categoryTagsElm': '.rsa-content-tree__tree-container h3',
      'categoryTagsElmDropdownIcon': '.rsa-content-tree__tree-container .rsa-icon',
      'categoryTagsElmDropdownNestedElmStartElm': '.rsa-content-tree__tree-container .content',
      'categoryTagsElmDropdownNestedElms': '.rsa-content-tree__child-label',
      'taggedElm': '.rsa-form-tag-manager .rsa-content-label',
      'taggedElmDeleteIcon': '.rsa-form-tag-manager .rsa-icon-close',
      'statusValue': '.rsa-incident-detail-header__status .prompt div',
      'assigneeDropDown': '.rsa-incident-detail-header__assignee .rsa-icon',
      'assigneeSelectOption': '.rsa-incident-detail-header__assignee select option',
      'statusDropDown': '.rsa-incident-detail-header__status .rsa-icon',
      'statusSelectOption': '.rsa-incident-detail-header__status select option',
      'priorityDropDown': '.rsa-incident-detail-header__priority .rsa-icon',
      'prtySelOpt': '.rsa-incident-detail-header__priority select option',
      'alertsGridIcon': '.main .rsa-icon-arrow-right-12',
      'riskScoreElm': '.rsa-riskscore .score',
      'dateTime': '.rsa-respond-detail-grid .rsa-createddate .datetime',
      'eventsElm': '.rsa-alerts-events',
      'domainElm': '.rsa-domain',
      'alrtsHostElm': '.rsa-alerts-host',
      'alrtSourceElm': '.rsa-alert-source .rsa-content-label',
      'journalTxtArea': '.rsa-journal-entry .ember-text-area',
      'journalEditDropDownBtn': '.rsa-journal-entry__edit-milestone .rsa-icon-arrow-down-12',
      'journalMilestoneOptions': '.rsa-journal-entry__edit-milestone select option',
      'journalSaveBtn': '.rsa-journal-entry__actions__save .rsa-form-button',
      'journalEntryTag': '.rsa-journal-entry__milestones .rsa-journal-entry__milestone',
      'journalEditButton': '.rsa-icon-pencil-1',
      'journalDelBtn': '.rsa-icon-bin-1',
      'journalDeleteConfirmBtn': '.rsa-journal-entry__delete-dialog__actions__confirm',
      'journalNte': '.rsa-journal-entry__note'
    }
  },
  'investigate': {
    'root': '.js-test-investigate-root',
    'navigateLink': 'js-test-investigate-navigate-classic',
    'eventsLink': 'js-test-investigate-events-classic',
    'malwareLink': 'js-test-investigate-malware-classic',
    'url': '/do/investigate',
    'path': 'protected.investigate.index'
  }
});

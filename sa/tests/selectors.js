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
    'preferenceButton': '.js-test-user-preferences-modal .rsa-form-button',
    'logoutButton': '.global>.rsa-form-button-wrapper .rsa-form-button'
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
    'userPref': {
      'friendlyUsernameInput': '#modalDestination .js-test-friendly-username input',
      'defaultLandingPageTrigger': '#modalDestination .js-test-default-landing-page-select .ember-power-select-trigger',
      'languageTrigger': '#modalDestination .js-test-language-select .ember-power-select-trigger',
      'timeZoneTrigger': '#modalDestination .js-test-time-zone-select .ember-power-select-trigger',
      'dateFormatTrigger': '#modalDestination .js-test-date-format-select .ember-power-select-trigger',
      'timeFormatRadioGroupInput': '#modalDestination .time-format-radio-group .rsa-form-radio input',
      'resetButton': '#modalDestination .js-test-revert button',
      'applyButton': '#modalDestination .js-test-apply button',
      'userPrefDropDownListItem': '#ember-basic-dropdown-wormhole ul li'
    },
    'respond': {

      'root': '.js-test-respond-root',
      'carouselItems': 'div[class=rsa-carousel__visible-items] div[class^=rsa-incident-content-card]',
      'incidentTile': 'div[class=rsa-carousel__visible-items] span[class=rsa-incident-tile-created-date]',
      'url': '/do/respond',
      'path': 'protected.respond.index',
      'updateIndLbl': '.rsa-update-indicator__label',
      'rsaLoader': '.rsa-loader',
      'rsaActiveElmCheck': 'is-active',
      'rsaIconClose': '.rsa-icon-close',
      'rsaTableCell': '.rsa-data-table-body-cell',
      'card': {
        'rsaContentCard': '.rsa-content-card',
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
          'statusSelect': '.rsa-incident-tile-status-selector .ember-power-select-trigger',
          'statusLabel': '.rsa-incident-tile-status-selector .ember-power-select-selected-item',
          'statusValue': '.rsa-incident-tile-status-selector',
          'sortSelect': '.sort-options .ember-power-select-trigger',
          'sortLabel': '.sort-options .ember-power-select-selected-item',
          'sortValue': '.sort-options .ember-power-select-selected-item',
          'sortSelectOption': '.ember-power-select-option',
          'sortOptDir': '.sort-options__direction',
          'sortOptDirUp': '.sort-options__direction .rsa-icon-arrow-up-7',
          'sortOptDirDown': '.sort-options__direction .rsa-icon-arrow-down-7',
          'prioritySelect': '.rsa-incident-tile-priority-selector .ember-power-select-trigger',
          'priorityLabel': '.rsa-incident-tile-priority-selector .ember-power-select-selected-item',
          'scoreElm': 'div[class=score]',
          'incLbl': 'label[class=rsa-incident-tile-id]',
          'incTitleName': 'li[class=rsa-incident-tile-name]',
          'priorityValueElm': '.rsa-incident-tile-priority-selector',
          'assigneeValueElm': '.rsa-incident-tile-assignee-selector',
          'alertsCountLbl': '.rsa-incident-tile-alert-count',
          'eventCountLbl': 'label[class=rsa-incident-tile-event-count]',
          'incidentTileSourcesElm': 'div[class=rsa-incident-tile-sources] div',
          'createdDateTimeElm': 'span[class=rsa-incident-tile-created-date] span[class=datetime]',
          'createdDateTimeAgoElm': 'span[class=rsa-incident-tile-created-date] span[class=time-ago]',
          'assigneeSelectOption': '.ember-power-select-option',
          'prioritySelectOpt': '.ember-power-select-option',
          'statusSelectOption': '.ember-power-select-option',
          'fromIpElm': '.from-ip .ip',
          'toIpElm': '.to-ip .ip',
          'multipleFromIpElm': '.from-ip .ip-count',
          'multipleToIpElm': '.to-ip .ip-count'
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
        'riskScoreSort': '.js-header-cell-sort-rsa-respond-list-riskscore',
        'incidentIdSort': '.js-header-cell-sort-rsa-respond-list-incident-id',
        'incidentNameSort': '.js-header-cell-sort-rsa-respond-list-name',
        'dateCreatedSort': '.js-header-cell-sort-rsa-respond-list-created',
        'assigneeSort': '.js-header-cell-sort-rsa-respond-list-assignee',
        'statusSort': '.js-header-cell-sort-rsa-respond-list-status',
        'alertCountSort': '.js-header-cell-sort-rsa-respond-list-alertCount',
        'sourcesSort': '.js-header-cell-sort-rsa-respond-list-sources',
        'eventsSort': '.js-header-cell-sort-rsa-respond-list-events',
        'prioritySort': 'js-header-cell-sort-rsa-respond-list-priority',
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
        'incIdCol': '.rsa-respond-list-incident-id',
        'incNameCol': '.rsa-respond-list-name',
        'dateCreatedCol': '.datetime',
        'assigneeCol': '.rsa-respond-list-assignee',
        'statusCol': '.rsa-respond-list-status',
        'sourcesCol': '.rsa-respond-list-sources .ember-view',
        'eventsCountCol': '.rsa-respond-list-events',
        'alertsCountCol': '.rsa-respond-list-alertCount',
        'assigneeNameOpt': '.ember-power-select-option',
        'assigneeDropDownSel': '.rsa-respond-list__filter-panel__assignee-selector .ember-power-select-trigger',
        'sourceNameOpt': '.ember-power-select-option',
        'sourceDropDownSel': '.rsa-respond-list__filter-panel__source-selector .ember-power-select-trigger',
        'sourceNameOptionTag': '.rsa-respond-list__filter-panel__source-selector .ember-power-select-multiple-option',
        'categoryTagPlusIcon': '.rsa-icon-add-circle-1',
        'globalCatIcon': '.rsa-icon-arrow-right-12',
        'globalCatNameElm': '.rsa-content-accordion',
        'catNameElm': '.rsa-content-tree__child-label',
        'catTreeElm': '.rsa-content-tree__tree-container',
        'addRemoveColIcon': '.rsa-data-table-header__column-selector .rsa-icon',
        'riskScoreColChkBox': '.column-selection-riskScore .inner-wrapper',
        'idColChkBox': '.column-selection-id .inner-wrapper',
        'nameColChkBox': '.column-selection-name .inner-wrapper',
        'priorityColChkBox': '.column-selection-prioritySort .inner-wrapper',
        'dateColChkBox': '.column-selection-created .inner-wrapper',
        'assigneeColChkBox': '.column-selection-assigneeName .inner-wrapper',
        'statusColChkBox': '.column-selection-statusSort .inner-wrapper',
        'alertsColChkBox': '.column-selection-alertCount .inner-wrapper',
        'srcColChkBox': '.column-selection-sources .inner-wrapper',
        'eventsColChkBox': '.column-selection-eventCount .inner-wrapper',
        'addRemoveColSection': '.ember-tether-element section',
        'riskScoreColInput': '.column-selection-riskScore input',
        'idColInput': '.column-selection-id input',
        'nameColInput': '.column-selection-name input',
        'dateColInput': '.column-selection-created input',
        'assigneeColInput': '.column-selection-assigneeName input',
        'statusColInput': '.column-selection-statusSort input',
        'alertsColInput': '.column-selection-alertCount input',
        'srcColInput': '.column-selection-sources input',
        'eventsColInput': '.column-selection-eventCount input',
        'priorityColInput': '.column-selection-prioritySort input',
        'allColumnsElms': '.panel-B .rsa-data-table-header-row .rsa-data-table-header-cell'
      },
      'list': {
        'table': 'div[class=rsa-respond-list]',
        'columns': '.rsa-data-table-header-cell'
      },
      'details': {
        'header': {
          'detailHeader': '.rsa-incident-detail-header',
          'prioritySelectOption': '.ember-power-select-option'
        },
        'overview': {
          'accordion': '.rsa-respond-detail-overview__accordion',
          'textarea': '.rsa-respond-detail-overview textarea'
        }
      },
      'toggleViewHeader': 'header[class^=rsa-respond-index-header]',
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
      'storylineTimeTxt': '.datetime',
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
      'priorityValueElm': 'div[class^=rsa-incident-detail-header__priority] .ember-power-select-selected-item',
      'assigneeValueElm': 'div[class^=rsa-incident-detail-header__assignee] .ember-power-select-selected-item',
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
      'statusValue': '.rsa-incident-detail-header__status .ember-power-select-selected-item',
      'assigneeDropDown': '.rsa-incident-detail-header__assignee .ember-power-select-trigger',
      'assigneeSelectOption': '.ember-power-select-option',
      'statusDropDown': '.rsa-incident-detail-header__status .ember-power-select-trigger',
      'statusSelectOption': '.ember-power-select-option',
      'priorityDropDown': '.rsa-incident-detail-header__priority .ember-power-select-trigger',
      'prtySelOpt': '.ember-power-select-option',
      'alertsGridIcon': '.main .rsa-icon-arrow-right-12',
      'alertsGridContentElm': '.main_row',
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
      'journalNte': '.rsa-journal-entry__note',
      'incidentQueuePanelBtn': '.incident-queue-trigger .rsa-icon',
      'myIncidentsBtn': '.js-my-incidents',
      'allIncidentsInQueueElm': '.rsa-application-incident-queue-panel a',
      'allIncidentsBtn': '.js-all-incidents',
      'incQueueIncScore': '.rsa-incident-tile-score .score',
      'incQueueIncId': '.rsa-incident-tile-id-header',
      'incQueueIncName': '.rsa-incident-tile-name',
      'incQueueIncStatus': '.rsa-incident-tile-status',
      'incQueueIncPriority': '.rsa-incident-tile-priority',
      'incQueueIncAssignee': '.rsa-incident-tile-assignee',
      'loaderWheelElm': '.rsa-loader__wheel',
      'srcIpValueElm': '.rsa-incident-detail-header__source-ip label',
      'destIpValueElm': '.rsa-incident-detail-header__destination-ip label',
      'eventOverViewTitleElm': '.event-overview__title',
      'eventPanelCloseBtn': '.rsa-icon-close',
      'eventOverViewIndicatorsGrpElm': '.event-overview__summary__indicators__group',
      'indicatorScoreValue': '.event-overview__summary__indicators__group .score',
      'indicatorName': '.event-overview__summary__indicators__group__description',
      'eventOverViewHeaderElmLabel': '.event-overview__header .label',
      'eventOverViewHeaderValue': '.event-overview__header .value',
      'eventSrcIpValue': '.event-overview__source__header__ip-address',
      'eventDestIpValue': '.event-overview__destination__header__ip-address',
      'srcDevicePortValue': '.event-overview__source__port .value',
      'srcDeviceMacAddressValue': '.event-overview__source__mac-address .value',
      'srcUserValue': '.event-overview__source__user .value',
      'destDevicePortValue': '.event-overview__destination__port .value',
      'destDeviceMacAddressValue': '.event-overview__destination__mac-address .value',
      'destUserValue': '.event-overview__destination__user .value',
      'domainValue': '.event-overview__domain__header__domain-value',
      'domainRegistrarValue': '.event-overview__domain__registrar .value',
      'domainRegistrantValue': '.event-overview__domain__registrant .value',
      'metaInfoKey': '.event-overview__meta__info__key-value__key',
      'metaInfoValue': '.event-overview__meta__info__key-value__value',
      'eventMetaDisplayIcon': '.rsa-icon-arrow-right-8'
    },
    'investigate': {
      'breadcrumbMoreButton': '.rsa-investigate-breadcrumb__more .rsa-form-button',
      'breadcrumbQueryButton': '.rsa-investigate-breadcrumb__submit .rsa-form-button',
      'breadcrumbQueryInput': '.rsa-investigate-breadcrumb__query-input input',
      'closedMetaViewPanel': '.rsa-investigate-query__body.recon-is-closed.meta-panel-size-min',
      'closeMetaViewIcon': '.rsa-investigate-meta__tools .min',
      'compressedTimeline': '.master.ember-view.rsa-chart',
      'compressMetaViewIcon': '.rsa-investigate-meta__tools .default',
      'compresstimelineAnimationIcon': '.ember-view.rsa-icon.is-filled.rsa-icon-shrink-horizontal-2.is-large',
      'defaultMetaViewPanel': '.rsa-investigate-query__body.recon-is-closed.meta-panel-size-default',
      'defaultMetaViewWithKeys': '.rsa-investigate-meta__content',
      'eventPanel': '.ember-view.rsa-investigate-events-table',
      'eventRows': '.rsa-investigate-events-table-row',
      'eventsLink': '.js-test-investigate-events-classic',
      'expandedMetaViewPanel': '.rsa-investigate-query__body.recon-is-closed.meta-panel-size-max',
      'expandedTimeline': '.detail.ember-view.rsa-chart',
      'expandMetaViewIcon': '.rsa-investigate-meta__tools .max',
      'expandtimelineAnimationIcon': '.ember-view.rsa-icon.is-filled.rsa-icon-expand-vertical-4.is-large',
      'malwareLink': '.js-test-investigate-malware-classic',
      'metaQueryInput': '.rsa-investigate-query-bar__query-input input',
      'metaValue': '.rsa-investigate-meta-key-values__value',
      'metaViewPanel': '.rsa-investigate-meta__content',
      'navigateLink': '.js-test-investigate-navigate-classic',
      'path': 'protected.investigate.index',
      'queryButton': '.rsa-investigate-query-bar__submit',
      'queryDropDownListItem': '#ember-basic-dropdown-wormhole ul li',
      'recon': {
        'recon': '.recon-container',
        'reconCloseRecon': '.recon-event-header .rsa-icon-close',
        'reconDownloadFiles': '.recon-container .export-files-button button',
        'reconHidePacket': '.recon-event-detail-packets .rsa-icon-arrow-down-12',
        'reconHideShowHeader': '.recon-event-header .rsa-icon-layout-6',
        'reconHideShowRequest': '.recon-event-header .rsa-icon-arrow-circle-right-2',
        'reconHideShowResponse': '.recon-event-header .rsa-icon-arrow-circle-left-2',
        'reconSelectAFile': '.recon-event-detail-files .rsa-data-table-body .rsa-form-checkbox',
        'reconSelectAllFiles': '.recon-event-detail-files .rsa-data-table-header .rsa-form-checkbox',
        'reconSelectorItems': '.ember-power-select-options',
        'reconShowPacket': '.recon-event-detail-packets .rsa-icon-arrow-right-12',
        'reconViewSelector': '.recon-view-selector'
      },
      'root': '.js-test-investigate-root',
      'selectedService': '.rsa-investigate-query-bar__service .ember-power-select-selected-item',
      'selectedServiceLbl': '.js-test-service',
      'selectedTimeRange': '.rsa-investigate-query-bar__time-range .ember-power-select-selected-item',
      'serviceDropDown': '.rsa-investigate-query-bar__service .ember-power-select-trigger',
      'serviceOptions': '.rsa-investigate-query-bar__service option',
      'serviceSelectOptions': '.rsa-investigate-query-bar__service ul li',
      'timelineContent': '.rsa-line-series',
      'timelinePanel': '.rsa-chart-background',
      'timelineXAxis': '.rsa-x-axis',
      'timelineYAxis': '.rsa-y-axis',
      'timeRangeDropDown': '.rsa-investigate-query-bar__time-range .ember-power-select-trigger',
      'timeRangeLbl': '.datetime',
      'timeRangeSelectOptions': '.rsa-investigate-query-bar__time-range select option',
      'url': '/do/investigate'
    },
    'contextpanel': {
      'liveconnect': {
        'riskIndicators': {
          'container': '.rsa-context-panel__liveconnect__risk-indicators',
          'categoriesPanel': '.rsa-context-panel__liveconnect__risk-indicators__category-panel',
          'category': '.rsa-context-panel__liveconnect__risk-indicators__category-panel__cat-header',
          'tagHighlighted': '.js-test-risk-indicator-tag .highlighted',
          'tagDisabled': '.js-test-risk-indicator-tag .disabled'
        }
      }
    }
  }
});

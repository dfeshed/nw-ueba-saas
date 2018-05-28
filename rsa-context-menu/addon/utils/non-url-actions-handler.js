import copyToClipboard from 'component-lib/utils/copy-to-clipboard';
import { openUrl, changeUrl, buildInvestigateUrl, buildEventAnalysisUrl } from 'rsa-context-menu/utils/build-url';

/**
 * Non Url based OOTB actions List.
 * Action List:  nonUrlBasedActionsArray = [
    'drillDownNewTabEquals',
    'drillDownNewTabNotEquals',
    'drillDownNotEquals',
    'viewListNewTab',
    'change-meta-view-ACTION_OPEN',
    'change-meta-view-ACTION_CLOSE',
    'change-meta-view-ACTION_AUTO',
    'change-meta-view-ACTION_HIDDEN',
    'rootDrill',
    'InvestigationEventDrillDownEquals',
    'malwareScanAction',
    'contextServiceDefaultAction',
    'reconstructionAction',
    'addToList',
    'copyMetaAction',
    'reconAnalysisAction',
    'InvestigationEventDrillDownNotEquals',
    'InvestigationEventDrillDownContains',
    'InvestigationEventRefocusEquals',
    'InvestigationEventRefocusNotEquals',
    'InvestigationEventDrillDownContainsNewTab',
    'InvestigationEventDrillDownNotEqualsNewTab',
    'InvestigationEventRefocusContains',
    'InvestigationEventRefocusSplitSessions',
    'InvestigationEventRefocusNewTabEquals',
    'InvestigationEventRefocusNewTabNotEqualsNewTab',
    'InvestigationEventRefocusNewTabContainsNewTab',
    'InvestigationEventRefocusNewTabSplitSessionsNewTab'
  ]
 *
 * Currently these actions will be captured here. Later these action definition will move in DB.
 * @public
 */
export const nonUrlBasedActions = {
  drillDownNewTabEquals: ([selection], contextDetails) => {
    openUrl(buildInvestigateUrl(selection, '=', contextDetails));
  },
  drillDownNewTabNotEquals: ([selection], contextDetails) => {
    openUrl(buildInvestigateUrl(selection, '!=', contextDetails));
  },
  drillDownNotEquals: ([selection], contextDetails) => {
    // Need to revisit this action.
    openUrl(buildInvestigateUrl(selection, '!=', contextDetails));
  },
  copyMetaAction: ([selection]) => {
    copyToClipboard(selection.metaValue);
  },
  rootDrill: ([selection], contextDetails) => {
    // Need to revisit this action.
    openUrl(buildInvestigateUrl(selection, '!=', contextDetails));
  },
  InvestigationEventDrillDownEquals: ([selection], contextDetails) => {
    openUrl(buildEventAnalysisUrl(selection, '=', contextDetails));
  },
  InvestigationEventDrillDownNotEquals: ([selection], contextDetails) => {
    changeUrl(buildEventAnalysisUrl(selection, '!=', contextDetails));
  },
  InvestigationEventDrillDownNotEqualsNewTab: ([selection], contextDetails) => {
    openUrl(buildEventAnalysisUrl(selection, '!=', contextDetails));
  },
  InvestigationEventDrillDownContainsNewTab: ([selection], contextDetails) => {
    openUrl(buildEventAnalysisUrl(selection, 'contains', contextDetails));
  },
  InvestigationEventDrillDownContains: ([selection], contextDetails) => {
    changeUrl(buildEventAnalysisUrl(selection, 'contains', contextDetails));
  },
  InvestigationEventRefocusEquals: ([selection], contextDetails) => {
    changeUrl(buildEventAnalysisUrl(selection, '=', contextDetails, true));
  },
  InvestigationEventRefocusNotEquals: ([selection], contextDetails) => {
    changeUrl(buildEventAnalysisUrl(selection, '!=', contextDetails, true));
  },
  InvestigationEventRefocusContains: ([selection], contextDetails) => {
    changeUrl(buildEventAnalysisUrl([selection], 'contains', contextDetails, true));
  },
  InvestigationEventRefocusNewTabEquals: ([selection], contextDetails) => {
    openUrl(buildEventAnalysisUrl(selection, '=', contextDetails, true));
  },
  InvestigationEventRefocusNewTabNotEqualsNewTab: ([selection], contextDetails) => {
    openUrl(buildEventAnalysisUrl(selection, '!=', contextDetails, true));
  },
  InvestigationEventRefocusNewTabContainsNewTab: ([selection], contextDetails) => {
    openUrl(buildEventAnalysisUrl(selection, 'contains', contextDetails, true));
  }
};

/**
 * Following actions are not supported in context menu action. This should be removed in future.
 * @public
 */
export const nonSupportedActionList = [
  'contextServiceDefaultAction',
  'InvestigationEventRefocusSplitSessions',
  'InvestigationEventRefocusNewTabSplitSessionsNewTab',
  'addToList',
  'malwareScanAction',
  'change-meta-view-ACTION_OPEN',
  'change-meta-view-ACTION_CLOSE',
  'change-meta-view-ACTION_AUTO',
  'change-meta-view-ACTION_HIDDEN',
  'reconstructionAction',
  'reconAnalysisAction'
];
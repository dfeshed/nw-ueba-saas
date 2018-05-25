import copyToClipboard from 'component-lib/utils/copy-to-clipboard';
import { openUrl, buildInvestigateUrl, buildEventAnalysisUrl } from 'rsa-context-menu/utils/build-url';

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
  InvestigationEventDrillDownNotEquals: ([selection], contextDetails) => {
    window.location.href = buildEventAnalysisUrl(selection, '!=', contextDetails);
  },
  InvestigationEventDrillDownContains: ([selection], contextDetails) => {
    window.location.href = buildEventAnalysisUrl(selection, 'contains', contextDetails);
  },
  InvestigationEventRefocusNotEquals: ([selection], contextDetails) => {
    window.location.href = buildEventAnalysisUrl(selection, '!=', contextDetails, true);
  },
  InvestigationEventRefocusContains: ([selection], contextDetails) => {
    window.location.href = buildEventAnalysisUrl([selection], 'contains', contextDetails, true);
  },
  InvestigationEventRefocusNewTabNotEqualsNewTab: ([selection], contextDetails) => {
    openUrl(buildEventAnalysisUrl(selection, '!=', contextDetails, true));
  },
  InvestigationEventRefocusNewTabContainsNewTab: ([selection], contextDetails) => {
    openUrl(buildEventAnalysisUrl(selection, 'contains', contextDetails, true));
  },
  InvestigationEventRefocusSplitSessions: ([selection], contextDetails) => {
    // Need to revisit this action.
    window.location.href = buildEventAnalysisUrl(selection, '!=', contextDetails, true);
  },
  InvestigationEventRefocusNewTabSplitSessionsNewTab: ([selection], contextDetails) => {
    // Need to revisit this action.
    openUrl(buildEventAnalysisUrl(selection, '!=', contextDetails, true));
  }
};

/**
 * Following actions are not supported in context menu action. This should be removed in future.
 * @public
 */
export const nonSupportedActionList = [
  'contextServiceDefaultAction',
  'addToList',
  'malwareScanAction',
  'change-meta-view-ACTION_OPEN',
  'change-meta-view-ACTION_CLOSE',
  'change-meta-view-ACTION_AUTO',
  'change-meta-view-ACTION_HIDDEN',
  'reconstructionAction',
  'reconAnalysisAction'
];
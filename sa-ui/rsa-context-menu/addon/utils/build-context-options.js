import _ from 'lodash';
import { log } from 'ember-debug';
import {
  nonUrlBasedActions,
  nonSupportedActionList
} from 'rsa-context-menu/utils/non-url-actions-handler';
import { windowProxy } from 'component-lib/utils/window-proxy';

/**
 * This function will remove all disabled functions.
 * @private
*/
const _filteredActionList = (contextActions) => {
  return contextActions.filter((action) => action.disabled !== 'true' &&
    action.disabled !== true &&
    !nonSupportedActionList.includes(action.id));
};

/**
 * This function will add menuAction similar to ember-context-menu action.
 * Input: [{
    urlFormat: 'http://www.google.com/search?q={0}',
    displayName: 'applyRefocusSessionSplitsInNewTabLabel',
    cssClasses: [
      'ip.src',
      'ip.dst'
    ],
    groupName: 'refocusNewTabGroup',
    moduleClasses: [
      'UAP.investigation.analysis.view.EventAnalysisPanel'
    ]
  }]
 * Output: [{
    ...otherProperties,
    menuAction: {
      label: applyRefocusSessionSplitsInNewTabLabel,
      action(selection, contextDetails) {
        windowProxy.openInNewTab(action.urlFormat.replace('{0}', selection[0].metaValue));
      }
    }
  }]
 * @private
*/
const _getModifiedActions = (contextActions) => {

  return contextActions.map((action) => {
    action.menuAction = {
      prefix: 'contextmenu.actions.',
      label: action.displayName,
      action(selection, contextDetails) {
        if (action.urlFormat) {
          // encoding required to prevent special chars
          // from blowing up the app
          const encodedMetaValue = encodeURIComponent(selection[0].metaValue);
          const pivoteUrl = action.urlFormat.replace('{0}', encodedMetaValue).replace('{1}', selection[0].metaName);
          if (action.openInNewTab === true || action.openInNewTab === 'true') {
            windowProxy.openInNewTab(pivoteUrl);
          } else {
            windowProxy.openInCurrentTab(pivoteUrl);
          }
        } else if (nonUrlBasedActions[action.id]) {
          nonUrlBasedActions[action.id](selection, contextDetails);
        } else {
          log(`${action.displayName} currently not supported.`);
        }
      }
    };
    return action;
  });
};

/**
 * This function will flatten action array for based on cssClassName and scope.
 * Input: output from _getModifiedActions(contextActions)
 * Output: Single object will be converted as multiple object.
  [{
    ...otherProperties,
    moduleName: moduleClasses[0],
    scope: cssClasses[0],
  }, {
    ...otherProperties,
    moduleName: moduleClasses[0],
    scope: cssClasses[1],
  }, {
    ...otherProperties,
    moduleName: moduleClasses[1],
    scope: cssClasses[0],
  }, {
    ...otherProperties,
    moduleName: moduleClasses[1],
    scope: cssClasses[1],
  }]
 * @private
 */
const _getFlattenAction = (contextActions) => {
  return _.flatMap(contextActions, (action) => {
    const flattenMap = [];
    _.forEach(action.moduleClasses, (module) => {
      _.forEach(action.cssClasses, (scope) => {
        flattenMap.push({ ...action, module: module.split('.')[module.split('.').length - 1], scope });
      });
    });
    return flattenMap;
  });
};

/**
 * This function will group first based on module and second based on scope. Adds actions array based on
 * ember-context-menu items format. For url based function value will be populated automatically for other
 * OOTB menu items hard coded actions will be used (from context-menu utility).
 * Input: return of _getFlattenAction(contextActions)
 * Output: {
    EventAnalysisPanel: {
      'ip.src': [
        {
          label: applyRefocusSessionSplitsInNewTabLabel,
          action
        }, {
          label: 'groupName',
          subactions: [{
          label: applyRefocusSessionSplitsInNewTabLabel,
          action
        }]
      }],
      'ip.dst': SimilarArray
    }
  }
 * @private
 */
const _getModuleBasedAction = (flattenAction) => {
  const moduleBasedAction = {};
  const groupByModuleActions = _.groupBy(flattenAction, 'module');
  _.forEach(groupByModuleActions, (module, key) => {
    moduleBasedAction[key] = {};
    const nestedGroupByScope = _.groupBy(module, 'scope');
    _.forEach(nestedGroupByScope, (scopeObj, scope) => {
      moduleBasedAction[key][scope] = [];
      const nestedGroupByGroupName = _.groupBy(scopeObj, 'groupName');
      _.forEach(nestedGroupByGroupName, (group, groupkey) => {
        const sortedGroup = _.sortBy(group, 'order');
        const actions = _.map(sortedGroup, (action) => {
          return action.menuAction;
        });
        if (groupkey !== 'undefined') {
          moduleBasedAction[key][scope].push({ prefix: 'contextmenu.groups.', label: groupkey, subActions: actions });
        } else {
          moduleBasedAction[key][scope] = moduleBasedAction[key][scope].concat(actions);
        }
      });
    });
  });
  return moduleBasedAction;
};

/**
 * This function is to convert classic SA to new Format.
 * @private
*/
export const buildContextOptions = (actions) => {
  const filteredAction = _filteredActionList(actions);
  const modifiedActions = _getModifiedActions(filteredAction);
  const flattenAction = _getFlattenAction(modifiedActions);
  return _getModuleBasedAction(flattenAction);
};

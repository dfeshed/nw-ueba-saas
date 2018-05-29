import _ from 'lodash';
import { log } from 'ember-debug';
import {
  nonUrlBasedActions,
  nonSupportedActionList
} from 'rsa-context-menu/utils/non-url-actions-handler';
import windowProxy from 'rsa-context-menu/utils/window-proxy';

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
      'UAP.investigation.events.view.EventGrid'
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
const _getModifiedActions = (contextActions, i18n) => {

  return contextActions.map((action) => {
    action.menuAction = {
      label: i18n.exists(`contextmenu.actions.${action.displayName}`) ? i18n.t(`contextmenu.actions.${action.displayName}`) : action.displayName,
      action(selection, contextDetails) {
        if (action.urlFormat) {
          windowProxy.openInNewTab(action.urlFormat.replace('{0}', selection[0].metaValue).replace('{1}', selection[0].metaName));
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
 * Input: output from _getModifiedActions(contextActions, i18n)
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
    EventGrid: {
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
const _getModuleBasedAction = (flattenAction, i18n) => {
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
          moduleBasedAction[key][scope].push({ label: i18n.exists(`contextmenu.groups.${groupkey}`) ? i18n.t(`contextmenu.groups.${groupkey}`) : groupkey, subActions: actions });
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
export const buildContextOptions = (actions, i18n) => {
  const filteredAction = _filteredActionList(actions);
  const modifiedActions = _getModifiedActions(filteredAction, i18n);
  const flattenAction = _getFlattenAction(modifiedActions);
  return _getModuleBasedAction(flattenAction, i18n);
};
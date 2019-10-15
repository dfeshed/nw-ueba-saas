import {
  isValidToWrapWithParens,
  findSelectedPills,
  selectedPillIndexes
} from 'investigate-events/actions/pill-utils';

const queryWithSelected = (context, i18n) => {
  return {
    label: i18n.t('queryBuilder.querySelected'),
    disabled() {
      return context.get('hasInvalidSelectedPill');
    },
    action() {
      const pillsData = context.get('pillsData');
      const pillSet = new Set(pillsData);
      const selectedPills = findSelectedPills(context.get('pillsData'));
      const pillsToDelete = [...pillSet.difference(new Set(selectedPills))];
      // This action will delete pills and deselect + remove focus
      context.send('deleteGuidedPill', { pillData: pillsToDelete });
      // submit query with remaining selected pills
      context._submitQuery();
    }
  };
};

const queryWithSelectedNewTab = (context, i18n) => {
  return {
    label: i18n.t('queryBuilder.querySelectedNewTab'),
    disabled() {
      return context.get('hasInvalidSelectedPill');
    },
    action() {
      // Do not want to check canQueryGuided because user might
      // want to execute the same query in new tab
      context.get('executeQuery')({
        externalLink: true,
        paren: false
      });
      // deselect all the pills and remove focus. Can't trigger this first, as
      // route action picks up selected pills from state to executeQ
      context.send('removePillFocus');
      context.send('deselectAllGuidedPills');
    }
  };
};

const deleteSelection = (context, i18n) => {
  return {
    label: i18n.t('queryBuilder.delete'),
    action() {
      context.send('deleteSelectedGuidedPills');
    }
  };
};

const wrapWithParens = (context, i18n) => {
  return {
    label: i18n.t('queryBuilder.wrapParens'),
    disabled() {
      const pills = context.get('pillsData');
      const { startIndex, endIndex } = selectedPillIndexes(pills);
      return !isValidToWrapWithParens(pills, startIndex, endIndex) || context.get('hasInvalidSelectedPill');
    },
    action() {
      const pills = context.get('pillsData');
      const { startIndex, endIndex } = selectedPillIndexes(pills);
      context.send('wrapWithParens', { startIndex, endIndex });
      context.send('deselectAllGuidedPills');
    }
  };
};

// Prepare an object that contains all possible list of options
function getContextItems(context, i18n) {
  const _this = context;
  return {
    pills: [
      queryWithSelected(_this, i18n),
      queryWithSelectedNewTab(_this, i18n),
      deleteSelection(_this, i18n),
      wrapWithParens(_this, i18n)
    ],
    parens: [
      queryWithSelected(_this, i18n),
      queryWithSelectedNewTab(_this, i18n),
      deleteSelection(_this, i18n)
    ]
  };
}

export {
  getContextItems
};
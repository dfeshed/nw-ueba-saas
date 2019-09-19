import { pillsSetDifference } from 'investigate-events/actions/utils';

const rightClickQueryWithSelected = (context, i18n) => {
  return {
    label: i18n.t('queryBuilder.querySelected'),
    disabled() {
      return context.get('hasInvalidSelectedPill');
    },
    action() {
      // Delete all deselected pills first
      // submit query with remaining selected pills
      context.send('deleteGuidedPill', { pillData: context.get('deselectedPills') });
      context._submitQuery();
    }
  };
};

const rightClickQueryWithSelectedNewTab = (context, i18n) => {
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

const rightClickDeleteSelection = (context, i18n) => {
  return {
    label: i18n.t('queryBuilder.delete'),
    action() {
      context.send('deleteSelectedGuidedPills');
    }
  };
};

const rightClickParenDeleteContents = (context, i18n) => {
  return {
    label: i18n.t('queryBuilder.deleteParenContents'),
    action() {
      context.send('deleteSelectedParenContents');
      context.send('deselectAllGuidedPills');
    }
  };
};

const rightClickParensQueryContents = (context, i18n) => {
  return {
    label: i18n.t('queryBuilder.queryParenContents'),
    action() {
      const pillsData = context.get('pillsData');
      const position = context.get('rightClickTarget').getAttribute('position');
      const pillsToDelete = pillsSetDifference(position, pillsData);
      context.send('deleteGuidedPill', { pillData: pillsToDelete });
      context._submitQuery();
    }
  };
};

const rightClickParensQueryContentsNewTab = (context, i18n) => {
  return {
    label: i18n.t('queryBuilder.queryParenContentsNewTab'),
    action() {
      const position = context.get('rightClickTarget').getAttribute('position');
      context.get('executeQuery')({
        externalLink: true,
        paren: true,
        position
      });
      context.send('removePillFocus');
      context.send('deselectAllGuidedPills');
    }
  };
};

// Prepare an object that contains all possible list of options
function getContextItems(context, i18n) {
  const _this = context;
  return {
    pills: [
      rightClickQueryWithSelected(_this, i18n),
      rightClickQueryWithSelectedNewTab(_this, i18n),
      rightClickDeleteSelection(_this, i18n)
    ],
    parens: [
      rightClickParensQueryContents(_this, i18n),
      rightClickParensQueryContentsNewTab(_this, i18n),
      rightClickParenDeleteContents(_this, i18n),
      rightClickDeleteSelection(_this, i18n)
    ]
  };
}

export {
  getContextItems
};